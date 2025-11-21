package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.RequestDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestSearchDto;
import com.pantavanij.sourcingreq.services.domain.dto.reviewer.ReviewerRequestDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.mapper.RequestMapper;
import com.pantavanij.sourcingreq.services.domain.mapper.RequestStatusMapper;
import com.pantavanij.sourcingreq.services.domain.mapper.SourcingStatusMapper;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.sql.Timestamp;
import java.util.*;

import static com.pantavanij.sourcingreq.services.enums.ApprovalStatus.*;
import static com.pantavanij.sourcingreq.services.enums.RequestStatus.REQUEST_CANCELLED;
import static com.pantavanij.sourcingreq.services.enums.RequestStatus.REQUEST_DRAFT;
import static com.pantavanij.sourcingreq.services.util.Constant.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequestServiceImplTest {
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private RequestAttachmentRepository requestAttachmentRepository;
    @Mock
    private RequestStatusRepository requestStatusRepository;
    @Mock
    private SourcingStatusRepository sourcingStatusRepository;
    @Mock
    private RequestStatusMapper requestStatusMapper;
    @Mock
    private SourcingStatusMapper sourcingStatusMapper;
    @Mock
    private RequestItemService requestItemService;
    @Mock
    private TenantRepository tenantRepository;
//    @Mock
//    private BudgetTypeRepository budgetTypeRepository;
    @Mock
    private CurrencyRepository currencyRepository;
    @Mock
    private TenantConfigServiceImpl tenantConfigService;
    @Mock
    private RequestHistoryService requestHistoryService;
    @Mock
    private WorkflowInstanceApprovalService workflowInstanceApprovalService;
    @Mock
    private TenantService tenantService;
    @Mock
    private UaaService uaaService;
    @Mock
    private MenuPrivilegeRepository menuPrivilegeRepository;
    @InjectMocks
    private RequestServiceImpl requestService;
    @Mock
    private RequestForwarderServiceImpl requestForwarderService;

    @Before
    public void setup() {
    }

    @Test
    public void findByRecId_success() {
        Request request = null;
        RequestDto requestDto = null;
        Boolean isReview = false;
        Long requestId = 0L;
        if (request != null) {
            requestDto = RequestMapper.INSTANCE.toRequestDto(request);
        }
        assertEquals(requestDto, requestService.findRequestSourcingByRecId(requestId));
    }

//    @Test
//    public void findByTenantId_success() {
//        List<Request> requestList = new ArrayList<>();
//
//        when(requestRepository.findRequestsByTenant(Mockito.anyInt())).thenReturn(requestList);
//        assertEquals(requestList, requestService.findByTenantId(Mockito.anyInt()));
//    }

    @Test
    public void findByRecIdForDuplicate_success() {
        RequestDto requestDto = null;
        Request request = null;
        Long requestId = 0L;
        if (request != null) {
            requestDto = RequestMapper.INSTANCE.toRequestDto(request);
        }
        assertEquals(requestDto, requestService.findByRecIdForDuplicate(requestId));
    }

    @Test
    public void cancelRequest_success() {
        RequestDto requestDto = null;
        Request request = null;
        Long requestId = 0L;
        RequestCancellationRequest cancellationRequest = new RequestCancellationRequest();
        cancellationRequest.setRecId(requestId);
        if (request != null) {
            requestDto = RequestMapper.INSTANCE.toRequestDto(request);
        }
        assertEquals(requestDto, requestService.cancelRequest(cancellationRequest));
        when(uaaService.getUserTimeZone(any(), null)).thenReturn("Asia/Bangkok");
    }

    @Test
    public void approveRequest_success() {
        RequestDto requestDto = null;
        Request request = null;
        Long requestId = 0L;
        RequestApproveRequest requestApproveRequest = new RequestApproveRequest();
        requestApproveRequest.setRequestId(requestId);
        if (request != null) {
            requestDto = RequestMapper.INSTANCE.toRequestDto(request);
        }
        assertEquals(requestDto, requestService.approveRequest(requestApproveRequest));
    }

    @Test
    public void rejectRequest_success() {
        RequestDto requestDto = null;
        Request request = null;
        Long requestId = 0L;
        RequestRejectRequest rejectRequest = new RequestRejectRequest();
        rejectRequest.setRequestId(requestId);
        if (request != null) {
            requestDto = RequestMapper.INSTANCE.toRequestDto(request);
        }
        assertEquals(requestDto, requestService.rejectRequest(rejectRequest));
    }

    @Test
    public void searchRequestByRecId_success() {
        Request request = null;
        Long requestId = 0L;
        when(requestRepository.findRequestByRecId(requestId)).thenReturn(request);
        assertEquals(request, requestService.searchRequestByRecId(requestId));
    }

    @Test
    public void searchRequestByCondition_success() {
        RequestSearchDto requestSearchDto = null;
        RequestSearchRequest searchRequest = new RequestSearchRequest();
        Pageable pageable = null;
        Page<Request> requests = requestRepository.findAll(Specification.where(getSpecificationByCondition(searchRequest)), pageable);
        if (requests != null) {
            List<ReviewerRequestDto> resultList = RequestMapper.INSTANCE.toReviewerRequestDtoList(requests.getContent(), "Asia/Bangkok");
            requestSearchDto.setRequestDtoList(resultList);
        }
        assertEquals(requestSearchDto, requestService.searchRequestByCondition(searchRequest, pageable));
    }

    @Test
    public void searchAllApprovalListByCondition_success() {
        RequestSearchDto requestSearchDto = null;
        ApprovalSearchRequest searchRequest = new ApprovalSearchRequest();
        List<Long> workflowInstanceIds = null;
        Pageable pageable = null;
        Page<Request> requests = requestRepository.findAll(Specification.where(getApprovalSpecificationByCondition(searchRequest, workflowInstanceIds, true)), pageable);
        if (requests != null) {
            List<ReviewerRequestDto> resultList = RequestMapper.INSTANCE.toReviewerRequestDtoList(requests.getContent(), "Asia/Bangkok");
            requestSearchDto.setRequestDtoList(resultList);
        }
        assertEquals(requestSearchDto, requestService.searchAllApprovalListByCondition(searchRequest, pageable));
    }

    @Test
    public void searchMyApprovalListByCondition_success() {
        RequestSearchDto requestSearchDto = null;
        ApprovalSearchRequest searchRequest = new ApprovalSearchRequest();
        Pageable pageable = null;
        Page<Request> requests = requestRepository.findAll(Specification.where(getApprovalSpecificationByCondition(searchRequest, null, false)), pageable);
        if (requests != null) {
            List<ReviewerRequestDto> resultList = RequestMapper.INSTANCE.toReviewerRequestDtoList(requests.getContent(), "Asia/Bangkok");
            requestSearchDto.setRequestDtoList(resultList);
        }
        assertEquals(requestSearchDto, requestService.searchMyApprovalListByCondition(searchRequest, pageable));
    }

//    @Test
//    public void saveRequest_success() {
//        RequestRequest request = new RequestRequest();
//        EPAuthReviewerResponse response = getEpAuthReviewer(1l);
//        request.setRecId(1L);
//        request.setTenantId(1);
////        request.setOrganizationId(2);
////        request.setRequestTypeId(0);
//        request.setPhone("023456789#09");
//
//        when(tenantService.findByCode(any())).thenReturn(Tenant.builder().recId(1).build());
//
//        requestService.saveRequest(request, false);
//    }

//    private EPAuthReviewerResponse getEpAuthReviewer (Long requestId) {
//        ReviewerSearchRequest reviewerSearchRequest = new ReviewerSearchRequest();
//        reviewerSearchRequest.setTenantId(AppUtil.getTenantId());
//        reviewerSearchRequest.setRequestId(requestId);
//        reviewerSearchRequest.setPage(1);
//        reviewerSearchRequest.setPageSize(99999);
//        reviewerSearchRequest.setSortBy("username");
//        reviewerSearchRequest.setSortOrder("desc");
//        return requestReviewerService.getReviewerListByConditions(reviewerSearchRequest);
//    }

    @Test
    public void deleteRequesByRecId_success() {
        requestService.deleteRequestByRecId(anyLong());
    }

    @Test
    public void assignRequestByRecId_success() {
        requestService.deleteRequestByRecId(anyLong());
    }

    @Test
    public void removeRequestByRecId_success() {
        requestService.deleteRequestByRecId(anyLong());
    }

    @Test
    public void searchRequestReviewerByCondition_success() {
        RequestSearchDto requestSearchDto = null;
        RequestSearchRequest searchRequest = new RequestSearchRequest();
        List<Long> requestId = new ArrayList<>(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L));
        Pageable pageable = null;
        Page<Request> requests = requestRepository.findAll(Specification.where(getRequestReviewerSpecificationByCondition(searchRequest, requestId)), pageable);
        if (requests != null) {
            List<ReviewerRequestDto> resultList = RequestMapper.INSTANCE.toReviewerRequestDtoList(requests.getContent(), "Asia/Bangkok");
            requestSearchDto.setRequestDtoList(resultList);
        }
        assertEquals(requestSearchDto, requestService.searchRequestReviewerByCondition(searchRequest, pageable));
    }

    @Test
    public void getRequestByRequestNo_success() {
        String requestNo = "";
        Integer tenant = 1;
        Request request = requestRepository.getRequestByRequestNo(requestNo, tenant);
        RequestDto requestDto = RequestMapper.INSTANCE.toRequestDto(request);
        assertEquals(requestDto, requestService.getRequestByRequestNo(requestNo, tenant));
    }

    private Specification<Request> getSpecificationByCondition(RequestSearchRequest searchRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Integer tenantId = searchRequest.getTenant().getRecId();
            if (tenantId != null) {
                predicates.add(criteriaBuilder.equal(root.get(TENANT).get(REC_ID), tenantId));
            }

            String createdBy = AppUtil.getUserName();
            if (!StringUtils.isEmpty(createdBy)) {
                predicates.add(criteriaBuilder.equal(root.get(CREATED_BY), createdBy));
            }

            Date fromDateCondition = searchRequest.getFromDate();
            Date toDateCondition = searchRequest.getToDate();
            if (fromDateCondition != null && toDateCondition != null) {
                Timestamp fromDate = DateTimeUtil.getTimestampUTC(fromDateCondition);
                Timestamp toDate = DateTimeUtil.getTimestampUTC(DateTimeUtil.addDate(toDateCondition, 1));
                predicates.add(criteriaBuilder.between(root.get(REQUEST_DATE), fromDate, toDate));
            } else if (fromDateCondition != null) {
                Timestamp fromDate = DateTimeUtil.getTimestampUTC(fromDateCondition);
                predicates.add(criteriaBuilder.greaterThan(root.get(REQUEST_DATE), fromDate));
            } else if (toDateCondition != null) {
                Timestamp toDate = DateTimeUtil.getTimestampUTC(DateTimeUtil.addDate(toDateCondition, 1));
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(REQUEST_DATE), toDate));
            }

            List<ConditionSearchRequest> conditionSearchRequestList = searchRequest.getConditionSearchList();
            if (conditionSearchRequestList != null && !conditionSearchRequestList.isEmpty()) {
                for (ConditionSearchRequest condition : conditionSearchRequestList) {
                    String searchField = condition.getSearchField();
                    String searchValue = condition.getSearchValue();
                    if (!StringUtils.isEmpty(searchField) && !StringUtils.isEmpty(searchValue)) {
                        predicates.add(criteriaBuilder.like(root.get(searchField), "%" + searchValue.toLowerCase() + "%"));
                    }

                }
            }

            Integer requestStatusId = searchRequest.getRequestStatusList().get(0).getRecId();
            if (requestStatusId != null) {
                predicates.add(criteriaBuilder.equal(root.get(REQUEST_STATUS_ID), requestStatusId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    private Specification<Request> getApprovalSpecificationByCondition(ApprovalSearchRequest searchRequest, List<Long> workflowInstanceIds, boolean isAllApproval) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Integer tenantId = searchRequest.getTenant().getRecId();
            if (tenantId != null) {
                predicates.add(criteriaBuilder.equal(root.get(TENANT).get(REC_ID), tenantId));
            }

            if (!isAllApproval) {
                String assignedBy = AppUtil.getUserName();
                if (!StringUtils.isEmpty(assignedBy)) {
                    predicates.add(criteriaBuilder.equal(root.get(ASSIGNED_BY), assignedBy));
                }
            } else {
                if (workflowInstanceIds != null) {
                    predicates.add(criteriaBuilder.in(root.get(WORKFLOW_INSTANCE_ID)).value(workflowInstanceIds));
                }
            }

            Date fromDateCondition = searchRequest.getFromDate();
            Date toDateCondition = searchRequest.getToDate();
            if (fromDateCondition != null && toDateCondition != null) {
                Timestamp fromDate = DateTimeUtil.getTimestampUTC(fromDateCondition);
                Timestamp toDate = DateTimeUtil.getTimestampUTC(DateTimeUtil.addDate(toDateCondition, 1));
                predicates.add(criteriaBuilder.between(root.get(REQUEST_DATE), fromDate, toDate));
            } else if (fromDateCondition != null) {
                Timestamp fromDate = DateTimeUtil.getTimestampUTC(fromDateCondition);
                predicates.add(criteriaBuilder.greaterThan(root.get(REQUEST_DATE), fromDate));
            } else if (toDateCondition != null) {
                Timestamp toDate = DateTimeUtil.getTimestampUTC(DateTimeUtil.addDate(toDateCondition, 1));
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(REQUEST_DATE), toDate));
            }

            List<ConditionSearchRequest> conditionSearchRequestList = searchRequest.getConditionSearchList();
            if (conditionSearchRequestList != null && !conditionSearchRequestList.isEmpty()) {
                for (ConditionSearchRequest condition : conditionSearchRequestList) {
                    String searchField = condition.getSearchField();
                    String searchValue = condition.getSearchValue();
                    if (!StringUtils.isEmpty(searchField) && !StringUtils.isEmpty(searchValue)) {
                        predicates.add(criteriaBuilder.like(root.get(searchField), "%" + searchValue.toLowerCase() + "%"));
                    }

                }
            }
            if (isAllApproval) {
                Integer approvalStatusId = searchRequest.getApprovalStatusList().get(0).getRecId();
                if (approvalStatusId != null) {
                    predicates.add(criteriaBuilder.equal(root.get(APPROVAL_STATUS_ID), approvalStatusId));
                } else {
                    predicates.add(criteriaBuilder.notEqual(root.get(APPROVAL_STATUS_ID), APPROVAL_DRAFT.id()));
                    predicates.add(criteriaBuilder.notEqual(root.get(APPROVAL_STATUS_ID), APPROVAL_CANCELLED.id()));
                }
            } else {
//                predicates.add(criteriaBuilder.equal(root.get(APPROVAL_STATUS).get(REC_ID), APPROVAL_AWAITING.id()));
//                predicates.add(criteriaBuilder.equal(root.get(APPROVAL_STATUS).get(REC_ID), APPROVAL_PARTIAL_COMPLETED.id()));

                List<Integer> approvalStatusIds = new ArrayList<>();
                approvalStatusIds.add(APPROVAL_AWAITING.id());
                approvalStatusIds.add(APPROVAL_PARTIAL_COMPLETED.id());

                predicates.add(criteriaBuilder.in(root.get(APPROVAL_STATUS_ID)).value(approvalStatusIds));
            }

            predicates.add(criteriaBuilder.notEqual(root.get(WORKFLOW_INSTANCE_ID), 0));

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    private Specification<Request> getRequestReviewerSpecificationByCondition(RequestSearchRequest searchRequest, List<Long> requestId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Integer tenantId = searchRequest.getTenant().getRecId();
            if (tenantId != null) {
                predicates.add(criteriaBuilder.equal(root.get(TENANT).get(REC_ID), tenantId));
            }

            predicates.add(criteriaBuilder.in(root.get(REC_ID)).value(requestId));

            Date fromDateCondition = searchRequest.getFromDate();
            Date toDateCondition = searchRequest.getToDate();
            if (fromDateCondition != null && toDateCondition != null) {
                Timestamp fromDate = DateTimeUtil.getTimestampUTC(fromDateCondition);
                Timestamp toDate = DateTimeUtil.getTimestampUTC(DateTimeUtil.addDate(toDateCondition, 1));
                predicates.add(criteriaBuilder.between(root.get(REQUEST_DATE), fromDate, toDate));
            } else if (fromDateCondition != null) {
                Timestamp fromDate = DateTimeUtil.getTimestampUTC(fromDateCondition);
                predicates.add(criteriaBuilder.greaterThan(root.get(REQUEST_DATE), fromDate));
            } else if (toDateCondition != null) {
                Timestamp toDate = DateTimeUtil.getTimestampUTC(DateTimeUtil.addDate(toDateCondition, 1));
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(REQUEST_DATE), toDate));
            }

            List<ConditionSearchRequest> conditionSearchRequestList = searchRequest.getConditionSearchList();
            if (conditionSearchRequestList != null && !conditionSearchRequestList.isEmpty()) {
                for (ConditionSearchRequest condition : conditionSearchRequestList) {
                    String searchField = condition.getSearchField();
                    String searchValue = condition.getSearchValue();
                    if (!StringUtils.isEmpty(searchField) && !StringUtils.isEmpty(searchValue)) {
                        predicates.add(criteriaBuilder.like(root.get(searchField), "%" + searchValue.toLowerCase() + "%"));
                    }

                }
            }

            Integer requestStatusId = searchRequest.getRequestStatusList().get(0).getRecId();
            if (requestStatusId != null) {
                predicates.add(criteriaBuilder.equal(root.get(REQUEST_STATUS_ID), requestStatusId));
            } else {
                predicates.add(criteriaBuilder.notEqual(root.get(REQUEST_STATUS_ID), REQUEST_DRAFT.id()));
                predicates.add(criteriaBuilder.notEqual(root.get(REQUEST_STATUS_ID), REQUEST_CANCELLED.id()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
