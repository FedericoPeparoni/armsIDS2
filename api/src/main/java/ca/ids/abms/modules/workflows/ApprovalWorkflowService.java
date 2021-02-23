package ca.ids.abms.modules.workflows;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ErrorDTO;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.currencies.CurrencyRepository;
import ca.ids.abms.modules.roles.RoleRepository;
import ca.ids.abms.modules.util.models.ModelUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApprovalWorkflowService {

    private final ApprovalWorkflowRepository approvalWorkflowRepository;

    private final RoleRepository roleRepository;

    private final CurrencyRepository currencyRepository;

    public ApprovalWorkflowService (final ApprovalWorkflowRepository approvalWorkflowRepository,
                                    final RoleRepository roleRepository,
                                    final CurrencyRepository currencyRepository) {
        this.approvalWorkflowRepository = approvalWorkflowRepository;
        this.roleRepository = roleRepository;
        this.currencyRepository = currencyRepository;
    }

    @Transactional(readOnly = true)
    public ApprovalWorkflow getOne (final Integer level) {
        return approvalWorkflowRepository.findOneByLevel(level);
    }

    @Transactional(readOnly = true)
    public Page<ApprovalWorkflow> findAll (final Pageable pageable) {
        return approvalWorkflowRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public ApprovalWorkflow getInitialStatus() {
        final List<ApprovalWorkflow> result = approvalWorkflowRepository.findAllByStatusType(StatusType.INITIAL);
        return CollectionUtils.isNotEmpty(result) ? result.get(0) : null;
    }

    @Transactional(readOnly = true)
    public ApprovalWorkflow getApprovedStatus() {
        final List<ApprovalWorkflow> result = approvalWorkflowRepository.findAllByStatusType(StatusType.FINAL);
        return CollectionUtils.isNotEmpty(result) ? result.get(0) : null;
    }

    @Transactional(readOnly = true)
    public ApprovalWorkflow getDeletedStatus() {
        final List<ApprovalWorkflow> result = approvalWorkflowRepository.findAllByDelete(true);
        return CollectionUtils.isNotEmpty(result) ? result.get(0) : null;
    }

    public ApprovalWorkflow create(final Integer level, final ApprovalWorkflow item) {
        assert (item != null);

        final ApprovalWorkflow existingLevel = approvalWorkflowRepository.findOneByLevel(level);
        if (existingLevel != null) {
            throw new ErrorDTO.Builder()
                .appendDetails("Already exists the level ")
                .appendDetails(level)
                .addInvalidField(ApprovalWorkflow.class, "level", "Already exists").buildInvalidDataException();
        } else {
            item.setId(null);
            item.setLevel(level);
        }
        validateStatusType(item);
        validateSelfReferences(item);
        setLevelObjects(item, null);

        if (item.getApprovalGroup() != null && item.getApprovalGroup().getId() != null) {
            item.setApprovalGroup(roleRepository.getOne(item.getApprovalGroup().getId()));
        }
        if (item.getThresholdCurrency() != null && item.getThresholdCurrency().getId() != null) {
            item.setThresholdCurrency(currencyRepository.getOne(item.getThresholdCurrency().getId()));
        }
        return approvalWorkflowRepository.save(item);
    }

    public void create (final List<ApprovalWorkflow> items) {
        if (CollectionUtils.isNotEmpty(items) && items.size() > 1) {
            try {
                approvalWorkflowRepository.deleteAll();
                approvalWorkflowRepository.flush();
            } catch (DataIntegrityViolationException dive) {
                throw new ErrorDTO.Builder(ErrorConstants.ERR_WFL_IN_USE).appendDetails("Please accept/reject the current pending transactions before").buildInvalidDataException();
            }

            boolean hasRejectedStatus = false;
            for (final ApprovalWorkflow item : items) {
                validateSelfReferences(item);
                if (Boolean.TRUE.equals(item.getDelete())) {
                    hasRejectedStatus = true;
                }
                if (item.getApprovalGroup() != null && item.getApprovalGroup().getId() != null) {
                    item.setApprovalGroup(roleRepository.getOne(item.getApprovalGroup().getId()));
                }
                if (item.getThresholdCurrency() != null && item.getThresholdCurrency().getId() != null) {
                    item.setThresholdCurrency(currencyRepository.getOne(item.getThresholdCurrency().getId()));
                }
                approvalWorkflowRepository.save(item);
            }
            if (!hasRejectedStatus) {
                throw new ErrorDTO.Builder(ErrorConstants.ERR_VALIDATION.toValue())
                    .appendDetails("The deletion level is missing")
                    .addInvalidField(ApprovalWorkflow.class, "delete", "Please set as 'delete' at least one level")
                    .buildInvalidDataException();
            }
            updateReferences(items);
        }
    }

    public ApprovalWorkflow update(final Integer level, final ApprovalWorkflow item) {
        assert (item != null && level != null);
        ApprovalWorkflow result = null;
        try {
            validateSelfReferences(item);

            final ApprovalWorkflow existingItem = approvalWorkflowRepository.findOneByLevel(level);
            if (existingItem != null) {
                setLevelObjects(item, null);

                if (item.getApprovalGroup() != null && item.getApprovalGroup().getId() != null) {
                    item.setApprovalGroup(roleRepository.getOne(item.getApprovalGroup().getId()));
                }
                if (item.getThresholdCurrency() != null && item.getThresholdCurrency().getId() != null) {
                    item.setThresholdCurrency(currencyRepository.getOne(item.getThresholdCurrency().getId()));
                }

                ModelUtils.checkVersionIfComparables(item, existingItem);
                ModelUtils.merge(item, existingItem, "level");

                result = approvalWorkflowRepository.save(existingItem);
            }
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
        if (result == null) {
            throw new CustomParametrizedException(ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
        return result;
    }

    public void delete(final Integer level) {
        try {
            final ApprovalWorkflow itemToDelete = approvalWorkflowRepository.findOneByLevel(level);
            if (itemToDelete == null) {
                throw new EntityNotFoundException("Not found the item with level " + level);
            }
            approvalWorkflowRepository.delete(itemToDelete.getId());
        } catch (DataIntegrityViolationException dive) {
            throw ExceptionFactory.getDepencencyViolationException(ApprovalWorkflow.class, ApprovalWorkflow.class);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }
    }

    private void validateStatusType (final ApprovalWorkflow item) {
        ApprovalWorkflow existing;
        switch (item.getStatusType()) {
            case INITIAL:
                existing = getInitialStatus();
                break;
            case FINAL:
                existing = getApprovedStatus();
                break;
            case DELETED:
                existing = getDeletedStatus();
                break;
            default:
                existing = null;
        }
        if (existing != null && !existing.getId().equals(item.getId())) {
            throw new ErrorDTO.Builder()
                .appendDetails("Already exists a level with status ")
                .appendDetails(item.getStatusType().name())
                .addInvalidField(ApprovalWorkflow.class, "statusType", "Already exists").buildInvalidDataException();
        }
    }

    private void validateSelfReferences (final ApprovalWorkflow item) {
        if (item.getLevel() != null) {
            if (item.getLevel().equals(item.getApprovalOverLevel())) {
                throw new ErrorDTO.Builder()
                    .appendDetails(String.format(ErrorConstants.ERR_WFL_SAME_LEVEL_LINK.toValue(), item.getLevel()))
                    .addInvalidField(ApprovalWorkflow.class, "approvalOver", ErrorConstants.ERR_WFL_SAME_LEVEL.toValue())
                    .buildInvalidDataException();
            }
            if (item.getLevel().equals(item.getApprovalUnderLevel())) {
                throw new ErrorDTO.Builder()
                    .appendDetails(String.format(ErrorConstants.ERR_WFL_SAME_LEVEL_LINK.toValue(), item.getLevel()))
                    .addInvalidField(ApprovalWorkflow.class, "approvalUnder", ErrorConstants.ERR_WFL_SAME_LEVEL.toValue())
                    .buildInvalidDataException();
            }
            if (item.getLevel().equals(item.getRejectedLevel())) {
                throw new ErrorDTO.Builder()
                    .appendDetails(String.format(ErrorConstants.ERR_WFL_SAME_LEVEL_LINK.toValue(), item.getLevel()))
                    .addInvalidField(ApprovalWorkflow.class, "rejected", ErrorConstants.ERR_WFL_SAME_LEVEL.toValue())
                    .buildInvalidDataException();
            }
        }
    }

    private void setLevelObjects (final ApprovalWorkflow item, final Map<Integer, ApprovalWorkflow> cache) {
        ApprovalWorkflow approvalUnder = null;
        ApprovalWorkflow approvalOver = null;

        if (item.getApprovalUnderLevel() != null) {
            approvalUnder = getLevel(item.getApprovalUnderLevel(), cache);
        }
        if (item.getApprovalOverLevel() != null) {
            approvalOver = getLevel(item.getApprovalOverLevel(), cache);
        }
        if (approvalOver != null && approvalUnder == null) {
            approvalUnder = approvalOver;
        } else if (approvalOver == null && approvalUnder != null) {
            approvalOver = approvalUnder;
        }
        item.setApprovalUnder(approvalUnder);
        item.setApprovalOver(approvalOver);
        if (item.getRejectedLevel() != null) {
            item.setRejected(getLevel(item.getRejectedLevel(), cache));
        } else if (item.getLevel() > 0 && !item.getStatusType().equals(StatusType.FINAL) && Boolean.FALSE.equals(item.getDelete())) {
            item.setRejected(getLevel(item.getLevel()-1, cache));
        }
    }

    private void updateReferences (final List<ApprovalWorkflow> sourceItems) {
        final List<ApprovalWorkflow> savedItems = approvalWorkflowRepository.findAll();
        if (CollectionUtils.isNotEmpty(savedItems) && sourceItems != null) {
            final Map<Integer, ApprovalWorkflow> cache = savedItems.stream().collect(Collectors.toMap(ApprovalWorkflow::getLevel, Function.identity()));

            sourceItems.forEach(item -> holdReferences(item, cache.get(item.getLevel())));
            savedItems.forEach(item -> {
                setLevelObjects(item, cache);
                approvalWorkflowRepository.save(item);
            });
        }
    }

    private void holdReferences (final ApprovalWorkflow source, final ApprovalWorkflow target) {
        if (source != null) {
            target.setApprovalOverLevel(source.getApprovalOverLevel());
            target.setApprovalUnderLevel(source.getApprovalUnderLevel());
            target.setRejectedLevel(source.getRejectedLevel());
        }
    }

    private ApprovalWorkflow getLevel (final Integer level, final Map<Integer, ApprovalWorkflow> temporaryCache) {
        if (temporaryCache != null) {
            return temporaryCache.get(level);
        } else {
            return approvalWorkflowRepository.findOneByLevel(level);
        }
    }
}
