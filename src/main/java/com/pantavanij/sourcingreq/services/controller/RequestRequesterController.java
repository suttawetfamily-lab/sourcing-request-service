package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.DelegationDto;
import com.pantavanij.sourcingreq.services.domain.dto.EPAuthRequesterDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestRequesterDto;
import com.pantavanij.sourcingreq.services.domain.dto.UserDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.request.DelegationActiveRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequesterSearchRequest;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponseStatus;
import com.pantavanij.sourcingreq.services.domain.response.DelegationActiveResponse;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthRequesterResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.Activity.ACTIVITY_UNKNOWN;
import static com.pantavanij.sourcingreq.services.enums.Role.REQUESTER;

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
public class RequestRequesterController {

    private final RequestRequesterService requestRequesterService;
    private final RequestRepository requestRepository;
    private final UaaService uaaService;
    private final TenantRequestStatusService tenantRequestStatusService;
    private final RequestReviewerRepository requestReviewerRepository;
    private final RequestPurchaserRepository requestPurchaserRepository;
    private final DelegationService delegationService;
    private final RequestReportLineRepository requestReportLineRepository;
    private final RequestApproverRepository requestApproverRepository;

    @PreAuthorize("hasAnyAuthority('SQN','SQP','SQV','SRL','SQA')")
    @GetMapping(value = "/request-requester/{requestId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getRequestRequester(@PathVariable Long requestId) {
        UserDto user = AppUtil.getUser();
        Request request = requestRepository.findRequestsByRecId(requestId);
        List<TenantRequestStatus> tenantRequestStatuses = tenantRequestStatusService.getRequestStatuses();
        List<RequestApprover> requestApprovers = requestApproverRepository.findRequestApproversByRequest(request);
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

        if (AppUtil.isAllowedAction(user, request, requestPurchasers, requestApprovers, null, null, requestReviewers, reportLines, delegationDto, tenantRequestStatuses, ACTIVITY_UNKNOWN)) {
            String[] privilegeCodeList = new String[] {REQUESTER.privilegeCode()};
            EPAuthRequesterResponse response = getEpAuthRequester(requestId, privilegeCodeList);
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            List<RequestRequesterDto> requestRequesterDtos = requestRequesterService.findByRequest(requestId);
            List<EPAuthRequesterDto> epAuthRequesterList = response.getData().stream()
                    .filter(r -> requestRequesterDtos.stream().anyMatch(req -> req.getRequester().getLoginId().equalsIgnoreCase(r.getLoginId().toString())))
                    .map(item -> {
                        RequestRequesterDto requestRequesterDto = requestRequesterDtos.stream()
                                .filter(req -> req.getRequester().getLoginId().equalsIgnoreCase(item.getLoginId().toString()) && req.getRequest().getRecId().equals(requestId))
                                .findFirst().orElse(null);
                        if (requestRequesterDto != null) {
                            EPAuthRequesterDto epAuthRequesterDto = new EPAuthRequesterDto(); // EPAuthRequesterDto.builder()
                            epAuthRequesterDto.setRequestRequesterId(requestRequesterDto.getRecId());
                            epAuthRequesterDto.setSysUserId(item.getSysUserId());
                            epAuthRequesterDto.setLoginId(item.getLoginId());
                            epAuthRequesterDto.setFullName(item.getFullName());
                            epAuthRequesterDto.setEmail(item.getEmail());
                            epAuthRequesterDto.setMobilePhone(item.getMobilePhone());
                            epAuthRequesterDto.setPhone(item.getPhone());
                            epAuthRequesterDto.setTimezone(item.getTimezone());
                            epAuthRequesterDto.setComment(requestRequesterDto.getComment());
                            epAuthRequesterDto.setCommentDate(!"-".equalsIgnoreCase(requestRequesterDto.getComment()) ? requestRequesterDto.getUpdatedDate() : null);
                            epAuthRequesterDto.setAddedBy(requestRequesterDto.getCreatedByName());
                            return epAuthRequesterDto;
                        } else {
                            return null;
                        }
                    })
                    .distinct()
                    .collect(Collectors.toList());

            response.setData(epAuthRequesterList);
            response.setTotal(requestRequesterDtos.size());
            return ResponseEntity.ok().body(response);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7082, ApiMessage.E7082.description()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    private EPAuthRequesterResponse getEpAuthRequester (Long requestId, String[] privilegeCodes) {
        RequesterSearchRequest requesterSearchRequest = new RequesterSearchRequest();
        requesterSearchRequest.setTenantId(AppUtil.getTenantId());
        requesterSearchRequest.setRequestId(requestId);
        requesterSearchRequest.setPage(1);
        requesterSearchRequest.setPageSize(99999);// Checking passed
        requesterSearchRequest.setSortBy("username");
        requesterSearchRequest.setSortOrder("desc");
        return requestRequesterService.getRequesterListByConditions(requesterSearchRequest, privilegeCodes);
    }
}
