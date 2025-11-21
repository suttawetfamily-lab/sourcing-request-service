package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.EpAuthClient;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.mapper.RequestRequesterMapper;
import com.pantavanij.sourcingreq.services.domain.request.EPAuthUserSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestRequesterRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequesterSearchRequest;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthRequesterResponse;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthUserListResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestReportLineRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestRequesterRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequesterRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import com.pantavanij.sourcingreq.services.util.UserDetailServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.Activity.ACTIVITY_REVIEW_COMMENT;
import static com.pantavanij.sourcingreq.services.enums.Activity.ACTIVITY_REVIEW_EDIT;

@RequiredArgsConstructor
@Service
@Slf4j
public class RequestRequesterServiceImpl implements RequestRequesterService {
    private final RequestReportLineRepository requestReportLineRepository;
    private final RequestRequesterRepository requestRequesterRepository;
    private final RequestRepository requestRepository;
    private final RequestHistoryService requestHistoryService;
    private final UaaService uaaService;
    private final TenantConfigService tenantConfigService;
    private final TenantService tenantService;
    private final EpAuthClient epAuthClient;
    private final RequesterRepository requesterRepository;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<RequestRequesterDto> saveRequestRequester(RequestRequesterRequest requestRequesterRequest, EPAuthRequesterResponse response) {
        // TODO to recheck if need to return deleted data?
        List<RequestRequesterDto> requestRequesterDtos = new ArrayList<>();
        List<RequestRequester> requestRequesterList = setRequestRequester(requestRequesterRequest, response);

        if(requestRequesterList != null && requestRequesterList.size() > 0) {
            // 1. Delete all exists data in RequestRequester
            this.deleteRequesterByRequestId(requestRequesterRequest.getRequestId());
            // 2. Save all new data to RequestRequester
            List<RequestRequester> requestRequesters = requestRequesterRepository.saveAll(requestRequesterList);

            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            requestRequesterDtos = RequestRequesterMapper.INSTANCE.toRequestRequesterDtoList(requestRequesters, timeZone);
            requestRequesterDtos.stream()
                    .peek(requestRequesterDto -> {
                        requestRequesterDto.setCreatedByName(UserDetailServiceUtil.getFullName(requestRequesterDto.getCreatedBy()));
                    }).collect(Collectors.toList());

        } else {
            requestRequesterRepository.deleteRequestRequesterByRequestId(requestRequesterRequest.getRequestId());
        }
        return requestRequesterDtos;
    }

    @Override
    public List<RequestRequesterDto> findByRequest(Long requestId) {
        List<RequestRequesterDto> requestRequesterDtos = new ArrayList<>();
        Request request = requestRepository.findRequestsByRecId(requestId);
        List<RequestRequester> requestRequesters = requestRequesterRepository.getByRequest(request.getRecId());
        if(requestRequesters != null && !requestRequesters.isEmpty()) {
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            requestRequesterDtos = RequestRequesterMapper.INSTANCE.toRequestRequesterDtoList(requestRequesters, timeZone);
            requestRequesterDtos.stream()
                    .peek(requestRequesterDto -> {
                        requestRequesterDto.setCreatedByName(UserDetailServiceUtil.getFullName(requestRequesterDto.getCreatedBy()));
                    }).collect(Collectors.toList());
        }
        return requestRequesterDtos;
    }

    @Override
    public EPAuthRequesterResponse getRequesterListByConditions(RequesterSearchRequest request, String[] privilegeCodes) {
        EPAuthUserSearchRequest epAuthUserSearchRequest = EPAuthUserSearchRequest.builder()
                .conditionSearchList(request.getConditionSearchList())
                .tenantId(request.getTenantId())
                .page(request.getPage())
                .pageSize(request.getPageSize())
                .sortBy(request.getSortBy())
                .sortOrder(request.getSortOrder())
                .build();

//        String tenantCode = AppUtil.getTenantId();
//        Tenant tenant = tenantService.findByCode(tenantCode);
//        String[] privilegeCodeList = new String[] {REQUESTER.privilegeCode()};
//        String privilegeCode = tenantConfigService.getEPAuthPrivilegeCode(tenant.getRecId(), REQUESTER.roleName());
        EPAuthUserListResponse epAuthUserListResponse = new EPAuthUserListResponse();
        try {
            epAuthUserListResponse = epAuthClient.getEPAuthUserList(privilegeCodes, epAuthUserSearchRequest);
        } catch(Exception ex) {
            ex.getMessage();
        }
        List<EPAuthRequesterDto> epAuthRequesterDtoList = new ArrayList<>();

        if (epAuthUserListResponse.getData() == null) {
            return EPAuthRequesterResponse.builder()
                    .data(null)
                    .page(epAuthUserListResponse.getPage())
                    .pageSize(epAuthUserListResponse.getPageSize())
                    .total(epAuthUserListResponse.getTotal())
                    .totalPage(epAuthUserListResponse.getTotalPage())
                    .build();
        }

        for (EPAuthUserDTO epAuthUserDTO: epAuthUserListResponse.getData()) {
            epAuthRequesterDtoList.add(
                    EPAuthRequesterDto.builder()
                            .sysUserId(epAuthUserDTO.getSysUserId())
                            .loginId(epAuthUserDTO.getLoginId())
                            .fullName(epAuthUserDTO.getFullName())
                            .email(epAuthUserDTO.getEmail())
                            .mobilePhone(epAuthUserDTO.getMobilePhone())
                            .phone(epAuthUserDTO.getPhone())
                            .timezone(epAuthUserDTO.getTimezone())
                            .build());
        }

        // Sort data by fullname
        epAuthRequesterDtoList.sort(Comparator.comparing(EPAuthRequesterDto::getFullName));

        return EPAuthRequesterResponse.builder()
                .data(epAuthRequesterDtoList)
                .page(epAuthUserListResponse.getPage())
                .pageSize(epAuthUserListResponse.getPageSize())
                .total(epAuthUserListResponse.getTotal())
                .totalPage(epAuthUserListResponse.getTotalPage())
                .build();
    }
    private List<RequestRequester> setRequestRequester(RequestRequesterRequest requestRequesterRequest, EPAuthRequesterResponse response) {
        Request request = requestRepository.findRequestByRecId(requestRequesterRequest.getRequestId());
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());

        List<RequestRequester> requestRequesters = new ArrayList<>();
        for (String sysUserId : requestRequesterRequest.getRequesters()) {
            List<RequestReportLine> requestReportLines = requestReportLineRepository.getByRequest(requestRequesterRequest.getRequestId());
            if (requestReportLines != null && !requestReportLines.isEmpty()) {
                Optional<RequestReportLine> reportLine = requestReportLines.stream()
                        .filter(r -> r.getReportLine().getLoginId().equalsIgnoreCase(sysUserId)).findFirst();

                if (reportLine.isPresent()) {
                    throw new BusinessException(ApiMessage.E7074, ApiMessage.E7074.description());
                } else {
                    compareEpAuthRequester(response, request, tenant, requestRequesters, sysUserId);
                }
            } else {
                compareEpAuthRequester(response, request, tenant, requestRequesters, sysUserId);
            }
        }
        return requestRequesters;
    }

    private void compareEpAuthRequester(EPAuthRequesterResponse response, Request request, Tenant tenant, List<RequestRequester> requestRequesters, String sysUserId) {
        Optional<EPAuthRequesterDto> epAuthRequesterDto = response.getData()
                .stream()
                .filter(r -> r.getSysUserId() == Integer.parseInt(sysUserId))
                .findFirst();

        if (epAuthRequesterDto.isPresent()) {
            Optional<Requester> optionalRequester = requesterRepository.findByLoginId(tenant.getRecId(), epAuthRequesterDto.get().getLoginId());
            Requester requester = optionalRequester.orElseGet(() -> Requester.builder()
                    .tenant(tenant)
                    .userId(epAuthRequesterDto.get().getSysUserId())
                    .loginId(epAuthRequesterDto.get().getLoginId())
                    .requesterName(epAuthRequesterDto.get().getFullName())
                    .email(epAuthRequesterDto.get().getEmail())
                    .phone(epAuthRequesterDto.get().getPhone())
                    .createdBy(AppUtil.getUserName())
                    .createdDate(DateTimeUtil.getTimestampUTC())
                    .build());

            Optional<RequestRequester> requestRequester = requestRequesterRepository.findRequestRequesterByRequestIdAndRequesterId(request.getRecId(), requester.getRecId());

            String comment = requestRequester.isPresent() ? requestRequester.get().getComment() : "-";
            RequestRequester resRequestRequester = getRequestRequester(requester, request, tenant, comment);
            requestRequesters.add(resRequestRequester);
        }
    }

    private RequestRequester getRequestRequester(Requester requester, Request request, Tenant tenant, String comment) {
        RequestRequester requestRequester = new RequestRequester();
        requestRequester.setRequest(request);
        requestRequester.setTenant(tenant);
        requestRequester.setRequester(requester);
        requestRequester.setComment(comment);
        requestRequester.setCreatedBy(AppUtil.getUserName());
        requestRequester.setCreatedDate(DateTimeUtil.getTimestampUTC());
        requestRequester.setUpdatedBy(AppUtil.getUserName());
        requestRequester.setUpdatedDate(DateTimeUtil.getTimestampUTC());
        return requestRequester;
    }

    public boolean duplicateChecker(List<RequestHistoryDto> requestHistoryDtos) {
        UserDto user = AppUtil.getUser();
        boolean userExists = requestHistoryDtos.stream()
                .anyMatch(dto -> Objects.requireNonNull(user).getUsername().equalsIgnoreCase(dto.getCreatedBy())
                        && Objects.equals(dto.getActivity().getRecId(), ACTIVITY_REVIEW_COMMENT.id()));

        // If this user was commented in this request.
        // Mean this user is in history of this request already.
        if (userExists) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRequesterByRequestId(Long requestId) {
        log.info("Delete all requester of request id : {}", requestId);
        requestRequesterRepository.deleteRequestRequesterByRequestId(requestId);
    }
}
