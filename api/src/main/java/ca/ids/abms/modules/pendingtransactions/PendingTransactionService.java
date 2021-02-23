package ca.ids.abms.modules.pendingtransactions;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ca.ids.abms.modules.common.dto.MediaDocument;
import ca.ids.abms.modules.pendingtransactionapprovals.PendingTransactionApprovals;
import ca.ids.abms.modules.pendingtransactionapprovals.PendingTransactionApprovalsService;
import ca.ids.abms.modules.pendingtransactionapprovals.enumerate.PendingTransactionAction;
import ca.ids.abms.modules.pendingtransactions.enumerate.PendingTransactionDocumentType;
import ca.ids.abms.modules.transactionapprovals.TransactionApprovalsService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.db.JoinFilter;
import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ErrorDTO;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.billings.BillingLedgerRepository;
import ca.ids.abms.modules.chargesadjustment.ChargesAdjustment;
import ca.ids.abms.modules.roles.Role;
import ca.ids.abms.modules.transactions.Transaction;
import ca.ids.abms.modules.transactions.TransactionService;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.users.UserService;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import ca.ids.abms.modules.workflows.ApprovalWorkflow;
import ca.ids.abms.modules.workflows.ApprovalWorkflowRepository;
import ca.ids.abms.modules.workflows.ApprovalWorkflowService;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional
public class PendingTransactionService {

    private static final Logger LOG = LoggerFactory.getLogger(PendingTransactionService.class);

    private final PendingTransactionRepository pendingTransactionRepository;
    private final ApprovalWorkflowService approvalWorkflowService;
    private final CurrencyUtils currencyUtils;
    private final BillingLedgerRepository billingLedgerRepository;
    private final UserService userService;
    private final PendingTransactionMapper pendingTransactionMapper;
    private final TransactionService transactionService;
    private final ApprovalWorkflowRepository approvalWorkflowRepository;
    private final PendingTransactionApprovalsService pendingTransactionApprovalsService;
    private final PendingChargeAdjustmentRepository pendingChargeAdjustmentRepository;
    private final TransactionApprovalsService transactionApprovalsService;

    public PendingTransactionService(
        final PendingTransactionRepository pendingTransactionRepository,
        final PendingTransactionMapper pendingTransactionMapper,
        final ApprovalWorkflowService approvalWorkflowService,
        final UserService userService,
        final CurrencyUtils currencyUtils,
        final BillingLedgerRepository billingLedgerRepository,
        final TransactionService transactionService,
        final ApprovalWorkflowRepository approvalWorkflowRepository,
        final PendingTransactionApprovalsService pendingTransactionApprovalsService,
        final PendingChargeAdjustmentRepository pendingChargeAdjustmentRepository,
        final TransactionApprovalsService transactionApprovalsService) {

        this.pendingTransactionRepository = pendingTransactionRepository;
        this.approvalWorkflowService = approvalWorkflowService;
        this.pendingTransactionMapper = pendingTransactionMapper;
        this.userService = userService;
        this.currencyUtils = currencyUtils;
        this.billingLedgerRepository = billingLedgerRepository;
        this.transactionService = transactionService;
        this.approvalWorkflowRepository = approvalWorkflowRepository;
        this.pendingTransactionApprovalsService = pendingTransactionApprovalsService;
        this.pendingChargeAdjustmentRepository = pendingChargeAdjustmentRepository;
        this.transactionApprovalsService = transactionApprovalsService;
    }

    @Transactional(readOnly = true)
    public Page<PendingTransaction> findAll(final Pageable pageable, final String textSearch,
            final String approvalName, final String currentRoles) {

        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder();

        if (StringUtils.isNotBlank(textSearch)) {
            filterBuilder.lookFor(textSearch);
        }
        if (StringUtils.isNotBlank(approvalName)) {
            filterBuilder.restrictOn(JoinFilter.equal("currentApprovalLevel", "approvalName", approvalName));
        }
        if (Boolean.valueOf(currentRoles)) {
            final User user = userService.getAuthenticatedUser();
            Collection<Role> roles = user.getRoles();
            if (CollectionUtils.isEmpty(roles)) {
                roles = CollectionUtils.EMPTY_COLLECTION;
            }
            return pendingTransactionRepository.findAllByCurrentApprovalLevelApprovalGroupIsIn(roles, pageable);
        } else {
            return pendingTransactionRepository.findAll(filterBuilder.build(), pageable);
        }
    }

    @Transactional(readOnly = true)
    public PendingTransaction findOne(final Integer id) {
        return pendingTransactionRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public PendingTransaction getOne(final Integer id) {
        final PendingTransaction pendingTransaction = pendingTransactionRepository.getOne(id);
        if (pendingTransaction != null) {
            final User user = userService.getAuthenticatedUser();
            final ApprovalWorkflow currentLevel = pendingTransaction.getCurrentApprovalLevel();

            boolean theUserHasRights;
            theUserHasRights = hasApproverRole(user, currentLevel.getApprovalGroup());
            pendingTransaction.setCanApprove(theUserHasRights && (getNextApprovedLevel(pendingTransaction) != null));
            pendingTransaction.setCanReject(theUserHasRights
                    && (currentLevel.getRejected() != null || Boolean.TRUE.equals(currentLevel.getDelete())));

            fillTheRelatedInvoices(pendingTransaction);

            LOG.debug(
                    "The current pending transaction {} can be approved: {}; can be rejected: {}; by the current user: {}, from the level {}",
                    pendingTransaction.getDescription(), pendingTransaction.getCanApprove(),
                    pendingTransaction.getCanReject(), user.getName(),
                    pendingTransaction.getCurrentApprovalLevel().getApprovalName());
        }
        return pendingTransaction;
    }

    public boolean reject(final Integer pendingTransactionId) {
        boolean updated = false;
        final PendingTransaction pendingTransaction = pendingTransactionRepository.getOne(pendingTransactionId);
        if (pendingTransaction != null) {
            final User user = userService.getAuthenticatedUser();
            final ApprovalWorkflow currentLevel = pendingTransaction.getCurrentApprovalLevel();
            final ApprovalWorkflow nextRejectedLevel = currentLevel.getRejected();

            /* Check if the user has the rights to reject */
            if (!hasApproverRole(user, currentLevel.getApprovalGroup())) {
                LOG.debug("The user {} doesn't belong to the approval group {} and cannot reject the transaction {}/{}",
                        user.getName(), pendingTransaction.getCurrentApprovalLevel().getApprovalGroup(),
                        pendingTransaction.getId(), pendingTransaction.getDescription());
                throw new ErrorDTO.Builder().setErrorMessage("The user ").setErrorMessage(user.getName())
                        .setErrorMessage(" doesn't have the rights to reject the transaction ")
                        .setErrorMessage(pendingTransaction.getDescription()).buildInvalidDataException();
            }

            if (Boolean.TRUE.equals(currentLevel.getDelete())) {
                pendingTransactionRepository.delete(pendingTransactionId);
                updated = true;
                LOG.debug("The transaction {}/{} has been rejected and deleted definitely by the user {}",
                        pendingTransaction.getId(), pendingTransaction.getDescription(), user.getName());
            } else if (nextRejectedLevel != null) {
                pendingTransaction.setPreviousApprovalLevel(pendingTransaction.getCurrentApprovalLevel());
                pendingTransaction.setCurrentApprovalLevel(nextRejectedLevel);
                updated = (pendingTransactionRepository.save(pendingTransaction) != null);
                LOG.debug("The transaction {}/{} has been rejected to the previously level \"{}\" by the user {}",
                        pendingTransaction.getId(), pendingTransaction.getDescription(),
                        nextRejectedLevel.getApprovalName(), user.getName());
            } else {
                /* The workflow is NOT configured properly */
                LOG.debug("The transaction {}/{} cannot be approved because the rejection level for {} is missing",
                        pendingTransaction.getId(), pendingTransaction.getDescription(),
                        pendingTransaction.getCurrentApprovalLevel().getApprovalName());
                throw new ErrorDTO.Builder()
                        .setErrorMessage("The workflow is not configured properly. Expected the rejection level from ")
                        .setErrorMessage(pendingTransaction.getCurrentApprovalLevel().getApprovalName())
                        .buildInvalidDataException();
            }
        }
        return updated;
    }

    boolean approve(final Integer pendingTransactionId, final MultipartFile file, final String userNotes) throws IOException {

        // find and validate pending transaction
        final PendingTransaction pendingTransaction = pendingTransactionRepository.getOne(pendingTransactionId);
        if (pendingTransaction == null)
            throw new CustomParametrizedException(ErrorConstants.ERR_NOT_FOUND, "Pending transaction could not be found");

        // resolve approval document if applicable
        // approval level is not necessarily required, and file is only required if the level is set in the approval workflow table
        resolveApprovalDocument(pendingTransaction, file);

        final User user = userService.getAuthenticatedUser();
        final ApprovalWorkflow nextLevel = getNextApprovedLevel(pendingTransaction);
        final ApprovalWorkflow currentLevel = pendingTransaction.getCurrentApprovalLevel();

        // check if the user has the rights to approve
        if (!hasApproverRole(user, currentLevel.getApprovalGroup())) {
            LOG.debug("The user {} doesn't belong to the approval group {} and cannot approve the transaction {}/{}",
                user.getName(), pendingTransaction.getCurrentApprovalLevel().getApprovalGroup(),
                pendingTransaction.getId(), pendingTransaction.getDescription());
            throw new ErrorDTO.Builder().setErrorMessage("The user ").setErrorMessage(user.getName())
                .setErrorMessage(" doesn't have the rights to approve the transaction ")
                .setErrorMessage(pendingTransaction.getDescription()).buildInvalidDataException();
        }

        // check if the workflow is configured properly
        if (nextLevel == null) {
            LOG.debug("The transaction {}/{} cannot be approved because the next level for {} is missing",
                pendingTransaction.getId(), pendingTransaction.getDescription(),
                pendingTransaction.getCurrentApprovalLevel().getApprovalName());
            throw new ErrorDTO.Builder()
                .setErrorMessage("The workflow is not configured properly. Expected the next level after ")
                .setErrorMessage(pendingTransaction.getCurrentApprovalLevel().getApprovalName())
                .buildInvalidDataException();
        }

        // update pending transaction levels
        pendingTransaction.setPreviousApprovalLevel(pendingTransaction.getCurrentApprovalLevel());
        pendingTransaction.setCurrentApprovalLevel(nextLevel);

        // persist by creating a new transaction if approval complete, pending transaction is removed
        // OR updating existing pending transaction
        if (nextLevel.equals(approvalWorkflowService.getApprovedStatus())) {
            LOG.debug("The transaction {}/{} has been approved definitely by the user {}",
                pendingTransaction.getId(), pendingTransaction.getDescription(), user.getName());
            Transaction transaction = createTransaction(pendingTransaction);
            addTransactionApprovalsHistory(pendingTransactionId, transaction, user.getName(), userNotes, nextLevel.getLevel(), PendingTransactionAction.APPROVAL);
            pendingTransactionRepository.delete(pendingTransaction.getId());
            return true;
        } else {
            LOG.debug("The transaction {}/{} has been updated with the next approval level \"{}\" by the user {}",
                pendingTransaction.getId(), pendingTransaction.getDescription(), nextLevel.getApprovalName(),
                user.getName());
            return pendingTransactionRepository.save(pendingTransaction) != null;
        }
    }

    private void addTransactionApprovalsHistory(final Integer pendingTransactionId, final Transaction transaction,
                                                final String userName, final String userNotes, final Integer level,
                                                final PendingTransactionAction action) {
        List<PendingTransactionApprovals> pendingTransactionApprovals = pendingTransactionApprovalsService.getByPendingTransactionId(pendingTransactionId);
        if (pendingTransactionApprovals != null && !pendingTransactionApprovals.isEmpty()) {
            LOG.debug("Add Transaction Approvals History for the transaction id: {}", transaction.getId());
            pendingTransactionApprovals.forEach(pta -> transactionApprovalsService.addTransactionApprovalsFromPendingTransactionApprovals(pta, transaction));
        }
        transactionApprovalsService.addTransactionApprovals(transaction, userName, userNotes, level, action);
    }

    /**
     * Set pending transaction media document based on pending transaction document type.
     */
    void setMediaDocument(final Integer id, final PendingTransactionDocumentType documentType, final MediaDocument document) {

        PendingTransaction pendingTransaction = pendingTransactionRepository.getOne(id);

        if (pendingTransaction == null)
            throw new EntityNotFoundException();

        switch (documentType) {
            case APPROVAL:
                pendingTransaction.setApprovalDocument(document.data());
                pendingTransaction.setApprovalDocumentName(document.fileName());
                pendingTransaction.setApprovalDocumentType(document.contentType());
                break;
            case SUPPORTING:
                pendingTransaction.setSupportingDocument(document.data());
                pendingTransaction.setSupportingDocumentName(document.fileName());
                pendingTransaction.setSupportingDocumentType(document.contentType());
                break;
            default:
                // ignored
        }

        // save change to pending transaction
        pendingTransactionRepository.save(pendingTransaction);
    }

    /**
     * Get pending transaction media document based on pending transaction document type.
     */
    MediaDocument getMediaDocument(final Integer id, final PendingTransactionDocumentType documentType) {
        PendingTransaction pendingTransaction = pendingTransactionRepository.getOne(id);

        if (pendingTransaction == null)
            return null;

        MediaDocument document;
        switch (documentType) {
            case APPROVAL:
                document = new MediaDocument(pendingTransaction.getApprovalDocumentName(),
                    pendingTransaction.getApprovalDocumentType(), pendingTransaction.getApprovalDocument());
                break;
            case SUPPORTING:
                document = new MediaDocument(pendingTransaction.getSupportingDocumentName(),
                    pendingTransaction.getSupportingDocumentType(), pendingTransaction.getSupportingDocument());
                break;
            default:
                document = null;
        }

        return document;
    }

    private Transaction createTransaction(final PendingTransaction pendingTransaction) {
        assert (pendingTransaction != null);

        LOG.debug(
                "The pending transaction {}/{} has been approved through the approval workflow, so a transaction will be created",
                pendingTransaction.getId(), pendingTransaction.getDescription());

        final Transaction transaction = pendingTransactionMapper.pendingTransactionToTransaction(pendingTransaction);

        final String invoiceIds = pendingTransaction.getRelatedInvoices();
        if (StringUtils.isNotBlank(invoiceIds)) {
            final List<Integer> billingLedgerIds = Stream.of(StringUtils.split(invoiceIds, ',')).map(Integer::valueOf)
                .collect(Collectors.toList());
            transaction.setBillingLedgerIds(billingLedgerIds);
        }
        final List<PendingChargeAdjustment> pendingChargeAdjustments = pendingTransaction.getPendingChargeAdjustments();
        if (pendingChargeAdjustments != null) {
            final Set<ChargesAdjustment> chargesAdjustments = pendingChargeAdjustments.stream()
                    .map(pendingTransactionMapper::pendingChargesToCharges).collect(Collectors.toSet());
            transaction.setChargesAdjustment(chargesAdjustments);
        }
        LOG.debug("A transaction has been approved through the workflow: {} ", transaction);
        return transactionService.createTransactionByPendingTransaction(transaction);
    }

    private void fillTheRelatedInvoices(final PendingTransaction pendingTransaction) {
        List<BillingLedger> result = null;
        final String invoiceIds = pendingTransaction.getRelatedInvoices();
        if (StringUtils.isNotBlank(invoiceIds)) {
            final List<Integer> billingLedgerIds = Stream.of(StringUtils.split(invoiceIds, ',')).map(Integer::valueOf)
                    .collect(Collectors.toList());
            result = this.billingLedgerRepository.findAll(billingLedgerIds);
        }
        pendingTransaction.setDetailedInvoices(result);
    }

    private ApprovalWorkflow getNextApprovedLevel(final PendingTransaction pendingTransaction) {
        final ApprovalWorkflow currentLevel = pendingTransaction.getCurrentApprovalLevel();
        ApprovalWorkflow nextLevel;
        if (isUnderTheThreshold(currentLevel, pendingTransaction)) {
            nextLevel = currentLevel.getApprovalUnder();
        } else {
            nextLevel = currentLevel.getApprovalOver();
        }
        return nextLevel;
    }

    private boolean hasApproverRole(final User user, final Role requiredRole) {
        assert user != null && user.getRoles() != null;
        final Collection<Role> userRoles = user.getRoles();
        if (requiredRole != null) {
            for (final Role userRole : userRoles) {
                if (userRole.getId().equals(requiredRole.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isUnderTheThreshold(final ApprovalWorkflow currentLevel,
            final PendingTransaction pendingTransaction) {
        double amount = 0.0d;
        double threshold = 0.0d;
        if (currentLevel.getThresholdCurrency() != null && currentLevel.getThresholdAmount() != null) {
            threshold = currentLevel.getThresholdAmount();
            if (currentLevel.getThresholdCurrency().equals(pendingTransaction.getLocalCurrency())) {
                amount = pendingTransaction.getLocalAmount();
            } else if (currentLevel.getThresholdCurrency().equals(pendingTransaction.getPaymentCurrency())) {
                amount = pendingTransaction.getPaymentAmount();
            } else {
                amount = currencyUtils.convertCurrency(pendingTransaction.getPaymentAmount(),
                        pendingTransaction.getPaymentCurrency(), currentLevel.getThresholdCurrency(),
                        pendingTransaction.getTransactionDateTime());
            }
        }
        LOG.debug("The pending transaction {}/{} with the amount {} is compared with the amount threshold {}",
                pendingTransaction.getId(), pendingTransaction.getDescription(), amount, threshold);
        return (Math.abs(amount) <= Math.abs(threshold));
    }

    private void resolveApprovalDocument(final PendingTransaction pendingTransaction, final MultipartFile file) throws IOException {

        // validate approval level and return if not valid for approval document
        if (pendingTransaction == null || !validApprovalDocumentLevel(pendingTransaction.getCurrentApprovalLevel()))
            return;

        if (file != null) {
            pendingTransaction.setApprovalDocument(file.getBytes());
            pendingTransaction.setApprovalDocumentName(file.getOriginalFilename());
            pendingTransaction.setApprovalDocumentType(file.getContentType());
        } else {
            throw new CustomParametrizedException(ErrorConstants.ERR_NOT_FOUND, "Invalid file, or no file, provided");
        }
    }

    private Boolean validApprovalDocumentLevel(final ApprovalWorkflow approvalWorkflow) {

        if (approvalWorkflow == null || approvalWorkflow.getLevel() == null)
            return false;

        // find and validate approval level equals approval document level
        final ApprovalWorkflow documentApprovalWorkflow = approvalWorkflowRepository.findByApprovalDocumentRequiredTrue();

        return documentApprovalWorkflow != null && documentApprovalWorkflow.getLevel() != null
            && documentApprovalWorkflow.getLevel().equals(approvalWorkflow.getLevel());
    }

    List<BillingLedger> getInvoicesByPendingTransactionId(Integer pendingTransactionId) {
        LOG.debug("get Invoices that have a pending transaction id: {}", pendingTransactionId);
        PendingTransaction pendingTransaction = getOne(pendingTransactionId);
        return pendingTransaction != null ? pendingTransaction.getDetailedInvoices() : null;
    }

    List<PendingChargeAdjustment> getChargesAdjustmentsByPendingTransactionId(Integer pendingTransactionId) {
        LOG.debug("get Charges Adjustments that have a pending transaction id: {}", pendingTransactionId);
        return pendingChargeAdjustmentRepository.findAllByPendingTransactionId(pendingTransactionId);
    }

    public long countAll() {
        return pendingTransactionRepository.count();
    }
}
