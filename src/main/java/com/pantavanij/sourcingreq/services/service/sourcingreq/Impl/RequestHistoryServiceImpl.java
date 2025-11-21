package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.DelegationDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestHistoryDto;
import com.pantavanij.sourcingreq.services.domain.dto.UserDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.mapper.RequestHistoryMapper;
import com.pantavanij.sourcingreq.services.domain.request.DelegationActiveRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestHistoryRequest;
import com.pantavanij.sourcingreq.services.domain.response.DelegationActiveResponse;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import com.pantavanij.sourcingreq.services.util.UserDetailServiceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.Activity.ACTIVITY_UNKNOWN;

@RequiredArgsConstructor
@Service
public class RequestHistoryServiceImpl implements RequestHistoryService {

    private final RequestHistoryRepository requestHistoryRepository;
    private final RequestForwarderRepository requestForwarderRepository;
    private final RequestRepository requestRepository;
    private final TenantRepository tenantRepository;
    private final TenantApprovalStatusService tenantApprovalStatusService;
    private final TenantRequestStatusService tenantRequestStatusService;
    private final ActivityRepository activityRepository;
    private final UaaService uaaService;
    private final RequestReviewerRepository requestReviewerRepository;
    private final RequestPurchaserRepository requestPurchaserRepository;
    private final DelegationService delegationService;
    private final RequestReportLineRepository requestReportLineRepository;
    private final RequestApproverRepository requestApproverRepository;
    private final ExcSourcingApproverRepository excSourcingApproverRepository;
    private final ExcSourcingPurchaserRepository excSourcingPurchaserRepository;

    @Override
    public List<RequestHistoryDto> getRequestHistoryByRequestID(Long requestID) {
        List<TenantRequestStatus> tenantRequestStatuses = tenantRequestStatusService.getRequestStatuses();
        List<RequestHistory> requestHistoryList = requestHistoryRepository.getRequestHistoryListByCondition(requestID);
        Request request = requestHistoryList.stream().findFirst().isPresent() ? requestHistoryList.stream().findFirst().get().getRequest() : null;
        List<RequestApprover> requestApprovers = requestApproverRepository.findRequestApproversByRequest(request);
        List<ExcSourcingApprover> excSourcingApprovers = excSourcingApproverRepository.findExcSourcingApproversByRequestId(request.getRecId());
        List<ExcSourcingPurchaser> excSourcingPurchasers = excSourcingPurchaserRepository.findExcSourcingPurchasersByRequestId(request.getRecId());
        List<RequestReviewer> requestReviewers = requestReviewerRepository.getByRequest(request.getRecId());
        List<RequestPurchaser> requestPurchasers = requestPurchaserRepository.findByRequest(request.getRecId());
        List<RequestReportLine> reportLines = requestReportLineRepository.getByRequest(request.getRecId());
        DelegationDto delegationDto = null;

        DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
        delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
        delegationActiveRequest.setDelegatorBy(request.getAssignedBy());

        DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
        if (delegationActiveResponse.getData() != null) {
            delegationDto = delegationActiveResponse.getData();
        }

        UserDto user = AppUtil.getUser();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);

        if (AppUtil.isAllowedAction(user, request, requestPurchasers, requestApprovers, excSourcingApprovers, excSourcingPurchasers, requestReviewers, reportLines, delegationDto, tenantRequestStatuses, ACTIVITY_UNKNOWN)) {
            List<RequestHistoryDto> requestHistoryDtoList =
                    RequestHistoryMapper.INSTANCE.toRequestHistoryDtoList(requestHistoryList, timeZone);
            requestHistoryDtoList = requestHistoryDtoList.stream()
                    .map(requestHistoryDto -> {
                        requestHistoryDto.setCreatedByName(UserDetailServiceUtil.getFullName(requestHistoryDto.getCreatedBy()));

                        return requestHistoryDto;
                    }).collect(Collectors.toList());
            return requestHistoryDtoList;
        }
        return null;
    }

    @Override
    public List<RequestHistoryDto> getAllRequestHistoryByRequestID(Long requestID) {
        List<TenantRequestStatus> tenantRequestStatuses = tenantRequestStatusService.getRequestStatuses();
        List<RequestHistory> requestHistoryList = requestHistoryRepository.findByRequestRecIdOrderByCreatedDateDesc(requestID);
        Request request = requestRepository.findRequestsByRecId(requestID);
        UserDto user = AppUtil.getUser();
        List<RequestApprover> requestApprovers = requestApproverRepository.findRequestApproversByRequest(request);
        List<ExcSourcingApprover> excSourcingApprovers = excSourcingApproverRepository.findExcSourcingApproversByRequestId(request.getRecId());
        List<ExcSourcingPurchaser> excSourcingPurchasers = excSourcingPurchaserRepository.findExcSourcingPurchasersByRequestId(request.getRecId());
        List<RequestReviewer> requestReviewers = requestReviewerRepository.getByRequest(request.getRecId());
        List<RequestPurchaser> requestPurchasers = requestPurchaserRepository.findByRequest(request.getRecId());
        List<RequestReportLine> reportLines = requestReportLineRepository.getByRequest(request.getRecId());
        DelegationDto delegationDto = null;

        DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
        delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
        delegationActiveRequest.setDelegatorBy(request.getAssignedBy());

        DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
        if (delegationActiveResponse.getData() != null) {
            delegationDto = delegationActiveResponse.getData();
        }

        if (AppUtil.isAllowedAction(user, request, requestPurchasers, requestApprovers, excSourcingApprovers, excSourcingPurchasers, requestReviewers, reportLines, delegationDto, tenantRequestStatuses, ACTIVITY_UNKNOWN)) {
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            List<RequestHistoryDto> requestHistoryDtoList =
                    RequestHistoryMapper.INSTANCE.toRequestHistoryDtoList(requestHistoryList, timeZone);
            requestHistoryDtoList = requestHistoryDtoList.stream()
                    .map(requestHistoryDto -> {
                        requestHistoryDto.setCreatedByName(UserDetailServiceUtil.getFullName(requestHistoryDto.getCreatedBy()));
                        requestHistoryDto.getActivity().setDescription(
                                String.format(requestHistoryDto.getActivity().getDescription(), requestHistoryDto.getRemark()));
                        return requestHistoryDto;
                    }).collect(Collectors.toList());
            return requestHistoryDtoList;
        }
        return null;
    }

    @Override
    public RequestHistory saveRequestHistory(RequestHistoryRequest requestHistoryRequest) {
        return requestHistoryRepository.save(buildHistoryRequest(requestHistoryRequest));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RequestHistory saveRequestHistoryByAction(Request request, Integer activityId) {
        RequestHistoryRequest requestHistoryRequest = new RequestHistoryRequest();
        requestHistoryRequest.setRequestId(request.getRecId());
        requestHistoryRequest.setTenantId(request.getTenant().getRecId());
        requestHistoryRequest.setActivityId(activityId);
        return this.saveRequestHistory(requestHistoryRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RequestHistory saveRequestHistoryByAction(Request request, Integer activityId, String remark) {
        RequestHistoryRequest requestHistoryRequest = new RequestHistoryRequest();
        requestHistoryRequest.setRequestId(request.getRecId());
        requestHistoryRequest.setTenantId(request.getTenant().getRecId());
        requestHistoryRequest.setActivityId(activityId);
        requestHistoryRequest.setRemark(remark);
        return this.saveRequestHistory(requestHistoryRequest);
    }

    private RequestHistory buildHistoryRequest(RequestHistoryRequest requestHistoryRequest) {
        RequestHistory requestHistory = new RequestHistory();
        String username = (AppUtil.getUserName() == null ? "System" : AppUtil.getUserName());
        Request request = requestRepository.getById(requestHistoryRequest.getRequestId());
        Tenant tenant = tenantRepository.getById(requestHistoryRequest.getTenantId());
        Activity activity = activityRepository.getById(requestHistoryRequest.getActivityId());
        requestHistory.setRequest(request);
        requestHistory.setTenant(tenant);
        requestHistory.setActivity(activity);
        requestHistory.setCreatedBy(username);
        requestHistory.setRemark(requestHistoryRequest.getRemark().substring(1, Math.min(requestHistoryRequest.getRemark().length(), 4000)));
        requestHistory.setCreatedDate(DateTimeUtil.getTimestampUTC());
        return requestHistory;
    }

}
