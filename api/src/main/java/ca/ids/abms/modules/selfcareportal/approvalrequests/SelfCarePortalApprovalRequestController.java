package ca.ids.abms.modules.selfcareportal.approvalrequests;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.common.mappers.DateTimeMapperUtils;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.selfcareportal.approvalrequests.enumerate.RequestDataset;
import ca.ids.abms.modules.selfcareportal.approvalrequests.enumerate.RequestType;
import ca.ids.abms.modules.selfcareportal.querysubmission.QuerySubmissionService;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.util.models.PageImplCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.BiFunction;

@RestController
@RequestMapping("/api/self-care-approval-request")
@SuppressWarnings({"unused", "squid:S1452"})
public class SelfCarePortalApprovalRequestController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(SelfCarePortalApprovalRequestController.class);

    private final SelfCarePortalApprovalRequestService selfCarePortalApprovalRequestService;
    private final SelfCarePortalApprovalRequestMapper selfCarePortalApprovalRequestMapper;
    private final QuerySubmissionService querySubmissionService;
    private final ReportDocumentCreator reportDocumentCreator;

    public SelfCarePortalApprovalRequestController(final SelfCarePortalApprovalRequestService selfCarePortalApprovalRequestService,
                                                   final SelfCarePortalApprovalRequestMapper selfCarePortalApprovalRequestMapper,
                                                   final QuerySubmissionService querySubmissionService,
                                                   final ReportDocumentCreator reportDocumentCreator) {
        this.selfCarePortalApprovalRequestService = selfCarePortalApprovalRequestService;
        this.selfCarePortalApprovalRequestMapper = selfCarePortalApprovalRequestMapper;
        this.querySubmissionService = querySubmissionService;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @PreAuthorize("hasAuthority('self_care_admin')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<SelfCarePortalApprovalRequestViewModel> getApprovalRequest(@PathVariable Integer id) {
        LOG.debug("REST request to get self-care portal approval request : {}", id);

        SelfCarePortalApprovalRequestViewModel request = selfCarePortalApprovalRequestMapper.toViewModel(selfCarePortalApprovalRequestService.getOne(id));

        return Optional.ofNullable(request).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('self_care_admin')")
    @GetMapping
    @SuppressWarnings("squid:S00107") //Methods should not have too many parameters
    public ResponseEntity<?> getAllApprovalRequests(@RequestParam(name = "search", required = false) final String textSearch,
                                                    @RequestParam(name = "status", required = false) final String status,
                                                    @RequestParam(name = "objectType", required = false) final String objectType,
                                                    @RequestParam(name = "account", required = false) final Integer accountId,
                                                    @RequestParam(name = "startDate", required = false) final String startDate,
                                                    @RequestParam(name = "endDate", required = false) final String endDate,
                                                    @SortDefault(sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable,
                                                    @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get all self-care portal approval request");

        LocalDateTime startDateFilter = null;
        LocalDateTime endDateFilter = null;

        if (startDate != null) {
            startDateFilter = DateTimeMapperUtils.parseISODate(startDate).atStartOfDay();
        }

        if (endDate != null) {
            endDateFilter = DateTimeMapperUtils.parseISODate(endDate).atTime(LocalTime.MAX);
        }

        final Page<SelfCarePortalApprovalRequest> page = selfCarePortalApprovalRequestService.findAll(textSearch, status, objectType, accountId, startDateFilter, endDateFilter, pageable);

        if (csvExport != null && csvExport) {
            final List<SelfCarePortalApprovalRequestCsvExportModel> csvExportModel = selfCarePortalApprovalRequestMapper.toCsvModel(page.getContent());
            ReportDocument report = reportDocumentCreator.createCsvDocument("Self_Care_Portal_Approvals",
                csvExportModel, SelfCarePortalApprovalRequestCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<SelfCarePortalApprovalRequestViewModel> resultPage = new PageImplCustom<>(selfCarePortalApprovalRequestMapper.toViewModel(page),
                pageable, page.getTotalElements(), selfCarePortalApprovalRequestService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @PreAuthorize("hasAuthority('self_care_admin')")
    @PutMapping(value = "/approve/{id}")
    public ResponseEntity<SelfCarePortalApprovalRequestViewModel> approveRequest(@RequestBody SelfCarePortalApprovalRequestViewModel requestDto,
                                                                                 @PathVariable Integer id) {
        LOG.debug("REST request to approve self-care portal approval request : {}", requestDto);

        SelfCarePortalApprovalRequestViewModel result = approveApprovalRequest(requestDto, id);

        if (result == null) {
            LOG.debug("Request {} a(n) {} wasn't approved", requestDto.getRequestType(), requestDto.getRequestDataset());
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception(String.format("Request %s a(n) %s wasn't approved", requestDto.getRequestType(), requestDto.getRequestDataset())));
        }

        String requestIdentifier = "";
        try {
            requestIdentifier = selfCarePortalApprovalRequestService.getRequestIdentifier(id);
        } catch (IOException e) {
            LOG.debug("Could not find request identifier for approve reqeust: {}", e.getMessage(), e);
        }

        ArrayList<ArrayList> requests = new ArrayList<>();
        ArrayList<String> ar = new ArrayList<>();
        ar.add(requestDto.getRequestType());
        ar.add(requestDto.getRequestDataset());
        ar.add(requestIdentifier);

        requests.add(ar);

        sendEmail("Approved", requestDto.getUser().getEmail(), requestDto.getResponseText(), requestDto.getUser().getName(), requests);

        return ResponseEntity.ok().body(result);
    }

    @PreAuthorize("hasAuthority('self_care_admin')")
    @PutMapping(value = "/reject/{id}")
    public ResponseEntity<SelfCarePortalApprovalRequestViewModel> rejectRequest(@RequestBody SelfCarePortalApprovalRequestViewModel requestDto,
                                                                                @PathVariable Integer id) {
        LOG.debug("REST request to reject self-care portal approval request : {}", requestDto);

        SelfCarePortalApprovalRequestViewModel result = rejectApprovalRequest(requestDto, id);

        if (result == null) {
            LOG.debug("Request wasn't rejected");
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception("Request wasn't rejected. Please check the data"));

        }
        String requestIdentifier = "";
        try {
            requestIdentifier = selfCarePortalApprovalRequestService.getRequestIdentifier(id);
        } catch (IOException e) {
            LOG.debug("Could not find request identifier for reject request: {}", e.getMessage(), e);
        }
        ArrayList<ArrayList> requests = new ArrayList<>();
        ArrayList<String> ar = new ArrayList<>();
        ar.add(requestDto.getRequestType());
        ar.add(requestDto.getRequestDataset());
        ar.add(requestIdentifier);

        requests.add(ar);

        sendEmail("Rejected", requestDto.getUser().getEmail(), requestDto.getResponseText(), requestDto.getUser().getName(), requests);
        return ResponseEntity.ok().body(result);
    }

    @Transactional
    @PreAuthorize("hasAuthority('self_care_admin')")
    @PutMapping(value = "/bulk-approve/{text}")
    public void bulkApproveRequest(@RequestBody List<Integer> requestIds, @PathVariable String text) {
        LOG.debug("REST request to approve self-care portal approval requests : {}", requestIds);

        Map<User, ArrayList<SelfCarePortalApprovalRequestViewModel>> requests = bulkRequest(requestIds, text, this::approveApprovalRequest);

        sendBulkEmails(requests, "Approved", text);
    }

    @Transactional
    @PreAuthorize("hasAuthority('self_care_admin')")
    @PutMapping(value = "/bulk-reject/{text}")
    public void bulkRejectRequest(@RequestBody List<Integer> requestIds, @PathVariable String text) {
        LOG.debug("REST request to reject self-care portal approval requests : {}", requestIds);

        Map<User, ArrayList<SelfCarePortalApprovalRequestViewModel>> requests = bulkRequest(requestIds, text, this::rejectApprovalRequest);

        sendBulkEmails(requests, "Rejected", text);
    }

    private SelfCarePortalApprovalRequestViewModel approveApprovalRequest(SelfCarePortalApprovalRequestViewModel requestDto, Integer id) {
        if (requestDto.getRequestDataset() == null || requestDto.getRequestType() == null) {
            LOG.debug("Bad request: Dataset or type of the request is not provided");
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception("Dataset or type of the request is not provided"));
        }

        if (requestDto.getAccount() == null && !requestDto.getRequestDataset().equals(RequestDataset.ACCOUNT.toValue())) {
            LOG.debug("Bad request: Account of the request is not provided");
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception("Account of the request is not provided"));
        }

        if (requestDto.getObjectId() == null && !requestDto.getRequestType().equals(RequestType.CREATE.toValue())) {
            LOG.debug("Bad request: Object id of the request is not provided");
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception("Object id of the request is not provided"));
        }

        SelfCarePortalApprovalRequestViewModel result = null;

        final SelfCarePortalApprovalRequest request = selfCarePortalApprovalRequestMapper.toModel(requestDto);

        if (request.getRequestDataset().equals(RequestDataset.ACCOUNT.toValue())) {
            result = selfCarePortalApprovalRequestMapper.toViewModel(selfCarePortalApprovalRequestService.approveAccount(id, request));
        }
        if (request.getRequestDataset().equals(RequestDataset.AIRCRAFT_REGISTRATION.toValue())) {
            result = selfCarePortalApprovalRequestMapper.toViewModel(selfCarePortalApprovalRequestService.approveAircraftRegistration(id, request));
        }
        if (request.getRequestDataset().equals(RequestDataset.FLIGHT_SCHEDULE.toValue())) {
            result = selfCarePortalApprovalRequestMapper.toViewModel(selfCarePortalApprovalRequestService.approveFlightSchedule(id, request));
        }

        return result;
    }

    private SelfCarePortalApprovalRequestViewModel rejectApprovalRequest(SelfCarePortalApprovalRequestViewModel requestDto, Integer id) {
        final SelfCarePortalApprovalRequest request = selfCarePortalApprovalRequestMapper.toModel(requestDto);
        return selfCarePortalApprovalRequestMapper.toViewModel(selfCarePortalApprovalRequestService.rejectRequest(id, request));
    }

    private Map<User, ArrayList<SelfCarePortalApprovalRequestViewModel>> bulkRequest(List<Integer> requestIds,
                                                                                     String text,
                                                                                     BiFunction<SelfCarePortalApprovalRequestViewModel,
                                                                                         Integer,
                                                                                         SelfCarePortalApprovalRequestViewModel> callback) {

        Map<User, ArrayList<SelfCarePortalApprovalRequestViewModel>> requests = new HashMap<>();

        for (Integer id: requestIds) {
            SelfCarePortalApprovalRequest request = selfCarePortalApprovalRequestService.getOne(id);
            request.setResponseText(text);
            SelfCarePortalApprovalRequestViewModel resultView = callback.apply(selfCarePortalApprovalRequestMapper.toViewModel(request), id);

            if (resultView != null) {
                requests.computeIfAbsent(resultView.getUser(), k -> new ArrayList<>()).add(resultView);
            }
        }
        return requests;
    }

    private void sendBulkEmails(Map<User, ArrayList<SelfCarePortalApprovalRequestViewModel>> requests, String emailSubject, String text) {
        requests.forEach((user, list) -> {
            ArrayList<ArrayList> info = new ArrayList<>();
            ArrayList<String> data = new ArrayList<>();
            for (SelfCarePortalApprovalRequestViewModel item : list) {
                String requestIdentifier = "";
                try {
                    requestIdentifier = selfCarePortalApprovalRequestService.getRequestIdentifier(item.getId());
                } catch (IOException e) {
                    LOG.debug("Exception: {}", e.getMessage(), e);
                }
                data.add("\n" + item.getRequestType());
                data.add(item.getRequestDataset());
                data.add(requestIdentifier);
            }
            info.add(data);

            try {
                sendEmail(emailSubject, user.getEmail(), text, user.getName(), info);
            } catch (Exception e) {
                LOG.debug("One or more emails couldn't be sent: {}", e.getMessage(), e);
                throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION, new Exception("One or more emails couldn't be sent: " + e.getMessage()));
            }
        });
    }

    private Boolean sendEmail(String subject, String email, String message, String userName, ArrayList<ArrayList> requests) {
        LOG.debug("REST request to send an email about {} to {}", subject, email);

        if (email == null) {
            return false;
        }

        String text =
            String.format("Hello %s \nYour request(s) has been %s \n\nRequest: \n%s\n\nRequest response: %s",
                userName, subject, requests.toString().replaceAll("[\\[\\]]", "").replaceAll("\\,", " "), message);

        return querySubmissionService.send(subject, text, new ArrayList<>(Collections.singletonList(email)), true);
    }
}
