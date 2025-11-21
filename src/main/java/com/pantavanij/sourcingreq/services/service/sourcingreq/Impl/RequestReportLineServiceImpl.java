package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.EpAuthClient;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.dto.reviewer.RequestReportLineDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.mapper.ReportLineMapper;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.AppException;
import com.pantavanij.sourcingreq.services.exception.DataNotFoundException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import com.pantavanij.sourcingreq.services.util.UserDetailServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.*;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.Activity.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestReportLineServiceImpl implements RequestReportLineService {

    private final RequestReportLineRepository requestReportLineRepository;
    private final ReportLineRepository reportLineRepository;
    private final RequestRepository requestRepository;
    private final UaaService uaaService;

    private final EPAuthService epAuthService;
    private final RequestHistoryService requestHistoryService;
    private final RequestHistoryRepository requestHistoryRepository;
    private final TenantService tenantService;
    private final TenantRequestStatusService tenantRequestStatusService;
    private final RequestReviewerRepository requestReviewerRepository;
    private final RequestPurchaserRepository requestPurchaserRepository;
    private final DelegationService delegationService;
    private final ReportLineService reportLineService;
    private final RequestApproverRepository requestApproverRepository;
    private final EpAuthClient epAuthClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRequestReportLine(Request request, Tenant tenant, List<Long> requestReportLines, EPAuthReviewerResponse response) {
        List<RequestReportLine> reportLines = new ArrayList<>();
        ReportLineSearchRequest requestSearchRequest = getReportLineSearchRequest(tenant);
        EPAuthReviewerResponse epAuthReviewerResponse = reportLineService.getReportLineListByConditions(requestSearchRequest, requestReportLines);
        List<EPAuthReviewerDto> epAuthReviewerList = new ArrayList<>();
        if(epAuthReviewerResponse.getData() != null) {
            epAuthReviewerList = epAuthReviewerResponse.getData();
        }

        for (Long id : requestReportLines) {
            if(id != null) {
                boolean isValidReportLineUser = epAuthReviewerList.stream().anyMatch(r -> r.getSysUserId().longValue() == id);
                if (!isValidReportLineUser) {
                    throw new AppException(ApiMessage.E7084, ApiMessage.E7084.description());
                }

                Optional<ReportLine> reportLineOptional = reportLineRepository.findByUserId(id.intValue());
                ReportLine reportLine = reportLineOptional.orElse(null);

                Long reportLineId = reportLine.getRecId();
                if (reportLine == null) {
                    EPAuthReviewerDto reportline = epAuthReviewerList.stream().filter(r -> r.getSysUserId().longValue() == id).findFirst().orElse(null);
                    reportLineId = insertUniqueReportLine(reportline);
                }

                Optional<RequestReportLine> resRequestReportLine = requestReportLineRepository.findRequestReportLineByRequestIdAndReportLineId(request.getRecId(), reportLineId);
                String comment = resRequestReportLine.isPresent() ? resRequestReportLine.get().getComment() : null;
                RequestReportLine requestReportLine = RequestReportLine.builder()
                        .request(request)
                        .tenant(tenant)
                        .reportLine(reportLine)
                        .comment(comment)
                        .createdDate(DateTimeUtil.getTimestampUTC())
                        .createdBy(AppUtil.getUserName())
                        .build();

                reportLines.add(requestReportLine);
            }
        }
        // Delete request report line.
        requestReportLineRepository.deleteRequestReportLineByRequestId(request.getRecId());

        if (!reportLines.isEmpty()) {
            requestReportLineRepository.saveAll(reportLines);
        }
    }

    public ReportLineSearchRequest getReportLineSearchRequest(Tenant tenant) {
        ReportLineSearchRequest requestSearchRequest = new ReportLineSearchRequest();
        requestSearchRequest.setPage(1);
        requestSearchRequest.setPageSize(99999);// Checking passed
        requestSearchRequest.setSortBy("username");
        requestSearchRequest.setSortOrder("desc");
        requestSearchRequest.setTenantId(tenant.getCode());
        return requestSearchRequest;
    }


    public Long insertUniqueReportLine(EPAuthReviewerDto reportline) {
        ReportLineDto saveReportLine = new ReportLineDto();
        saveReportLine.setPhone(reportline.getPhone());
        saveReportLine.setEmail(reportline.getEmail());
        saveReportLine.setUserId(reportline.getSysUserId().toString());
        saveReportLine.setReportLineName(reportline.getFullName());
        saveReportLine.setMobilePhone(reportline.getMobilePhone());
        saveReportLine.setLoginId(reportline.getLoginId());
        return this.saveReportLine(saveReportLine);
    }

    public Long saveReportLine(ReportLineDto reportLineDto) {
        UserDto userDto = AppUtil.getUser();
        Tenant tenant = tenantService.findByCode(userDto.getTenantId());
        log.info("Save Report line for SysUserId : {}, username: {}", reportLineDto.getUserId(), reportLineDto.getReportLineName());
        ReportLine reportLine = reportLineRepository.findByUserId(Integer.parseInt(reportLineDto.getUserId()))
                .orElse(new ReportLine());

        reportLine.setPhone(reportLineDto.getPhone());
        reportLine.setEmail(reportLineDto.getEmail());
        reportLine.setUserId(Integer.parseInt(reportLineDto.getUserId()));
        reportLine.setReportLineName(reportLineDto.getReportLineName());
        reportLine.setTenant(tenant);
        reportLine.setUpdatedBy(userDto.getUsername());
        reportLine.setUpdatedDate(DateTimeUtil.getTimestampUTC());
        reportLine.setLoginId(reportLineDto.getLoginId());

        if (StringUtils.isEmpty(reportLine.getCreatedBy())) {
            reportLine.setCreatedBy(userDto.getUsername());
        }
        if (reportLine.getCreatedDate() == null) {
            reportLine.setCreatedDate(DateTimeUtil.getTimestampUTC());
        }

        return reportLineRepository.save(reportLine).getRecId();
    }

    @Override
    public List<EPAuthReviewerDto> getByRequest(Long requestId) {
        /**
         * Check default report line
         * 1. Get Request entity from Request ID
         * 2. Call UAA to get borgId and userId from Requester
         * 3. Call EPAuth to get defaultReportLine
         */
        List<RequestReportLine> reportLines = requestReportLineRepository.getByRequest(requestId);
        List<TenantRequestStatus> tenantRequestStatuses = tenantRequestStatusService.getRequestStatuses();
        UserDto user = AppUtil.getUser();
        Request request = requestRepository.findRequestsByRecId(requestId);
        List<RequestApprover> requestApprovers = requestApproverRepository.findRequestApproversByRequest(request);
        List<RequestReviewer> requestReviewers = requestReviewerRepository.getByRequest(request.getRecId());
        List<RequestPurchaser> requestPurchasers = requestPurchaserRepository.findByRequest(request.getRecId());
        DelegationDto delegationDto = null;

        DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
        delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
        delegationActiveRequest.setDelegatorBy(request.getAssignedBy());

        DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
        if (delegationActiveResponse.getData() != null) {
            delegationDto = delegationActiveResponse.getData();
        }

        // Sort data by fullname
        reportLines.sort(Comparator.comparing(l -> l.getReportLine().getReportLineName()));
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);

        if (AppUtil.isAllowedAction(user, request, requestPurchasers, requestApprovers, null, null, requestReviewers, reportLines, delegationDto, tenantRequestStatuses, ACTIVITY_UNKNOWN)) {
            try {
                ContractDetailClientDto userDetail = uaaService.getContractDetail(request.getTenant().getCode(), "EP", request.getCreatedBy(), null);
                EPAuthDefaultUserDTO epAuthUserDTO = epAuthService.getDefaultUser(request.getTenant().getCode(), userDetail.getBorgIdList().get(0), userDetail.getUserId());

                return reportLines.stream().map(i -> {
                    EPAuthReviewerDto epAuthReviewerDto = new EPAuthReviewerDto();
                    epAuthReviewerDto.setRequestReviewerId(i.getRecId());
                    epAuthReviewerDto.setSysUserId(i.getReportLine().getUserId());
                    epAuthReviewerDto.setComment(null != i.getComment() ? i.getComment() : "-");
                    epAuthReviewerDto.setCommentDate(null != i.getComment() ? DateTimeUtil.convertTimestampByUserTimeZone(i.getUpdatedDate(), timeZone) : null);
                    epAuthReviewerDto.setEmail(i.getReportLine().getEmail());
                    epAuthReviewerDto.setFullName(i.getReportLine().getReportLineName());
                    epAuthReviewerDto.setPhone(i.getReportLine().getPhone());
                    epAuthReviewerDto.setAddedBy(UserDetailServiceUtil.getFullName(request.getCreatedBy()));
                    epAuthReviewerDto.setLoginId(i.getReportLine().getLoginId());
                    epAuthReviewerDto.setIsDefault(epAuthUserDTO == null ? false : Integer.valueOf(epAuthUserDTO.getUserId()).equals(i.getReportLine().getUserId()));
                    return epAuthReviewerDto;
                }).collect(Collectors.toList());
            } catch (Exception e) {
                return reportLines.stream().map(i -> {
                    EPAuthReviewerDto epAuthReviewerDto = new EPAuthReviewerDto();
                    epAuthReviewerDto.setRequestReviewerId(i.getRecId());
                    epAuthReviewerDto.setSysUserId(i.getReportLine().getUserId());
                    epAuthReviewerDto.setComment(null != i.getComment() ? i.getComment() : "-");
                    epAuthReviewerDto.setCommentDate(null != i.getComment() ? DateTimeUtil.convertTimestampByUserTimeZone(i.getUpdatedDate(), timeZone) : null);
                    epAuthReviewerDto.setEmail(i.getReportLine().getEmail());
                    epAuthReviewerDto.setFullName(i.getReportLine().getReportLineName());
                    epAuthReviewerDto.setPhone(i.getReportLine().getPhone());
                    epAuthReviewerDto.setAddedBy(UserDetailServiceUtil.getFullName(request.getCreatedBy()));
                    epAuthReviewerDto.setLoginId(i.getReportLine().getLoginId());
                    epAuthReviewerDto.setIsDefault(false);
                    return epAuthReviewerDto;
                }).collect(Collectors.toList());
            }
        }
        return null;
    }

    @Override
    public ReportLineDto getDefaultReportLine() {
        UserDto userDto = AppUtil.getUser();
        log.info("Get User Detail for username:{}, tenant:{} , IDP:{}", userDto.getUsername(), userDto.getTenantId(), userDto.getIdp());
        ContractDetailClientDto userDetail = uaaService.getContractDetail(userDto.getTenantId(), userDto.getIdp(), userDto.getUsername(), null);
        // Request Param will be BorgId and userId from UAA Service
        String borgId = userDetail.getBorgIdList().get(0);
        log.info("Get default report line for borgId:{} and sysUserId:{}",borgId , userDetail.getUserId());
        ReportLineDto defaultReportLine = new ReportLineDto();
        try {
            EPAuthUserDTO epAuthUserDTO = epAuthService.getDefaultReportLine(userDto.getTenantId(), borgId, userDetail.getUserId());
            defaultReportLine.setReportLineName(epAuthUserDTO.getFullName());
            defaultReportLine.setUserId(epAuthUserDTO.getSysUserId().toString());
            defaultReportLine.setRecId(epAuthUserDTO.getSysUserId().longValue());
            defaultReportLine.setEmail(epAuthUserDTO.getEmail());
            defaultReportLine.setPhone(epAuthUserDTO.getPhone());
            defaultReportLine.setMobilePhone(epAuthUserDTO.getMobilePhone());
            return defaultReportLine;
        } catch (DataNotFoundException exception) {
            defaultReportLine.setRecId(0L);
            defaultReportLine.setUserId("0");
            defaultReportLine.setReportLineName("Mock Default Report Line");
            return defaultReportLine;
        }
    }

    @Override
    public List<RequestReportLineDto> saveComment(ReportLineCommentRequest request) {
        List<RequestReportLineDto> response = Collections.emptyList();

        Optional<ReportLine> reportLineOptional = reportLineRepository.findByReportLineApproveName(UserDetailServiceUtil.getFullName(AppUtil.getUserName()));
        if (reportLineOptional.isPresent()) {
            Optional<RequestReportLine> requestReportLineOptional = requestReportLineRepository.findByRequestIdAndReportLineId(request.getRequestId(), reportLineOptional.get().getRecId());
            if (requestReportLineOptional.isPresent()) {
                RequestReportLine requestReportLine = requestReportLineOptional.get();
                requestReportLine.setComment(request.getComment());
                requestReportLine.setUpdatedBy(AppUtil.getUserName());
                requestReportLine.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                requestReportLineRepository.save(requestReportLine);

                // save history log comment
                Integer activityId = ACTIVITY_REVIEW_COMMENT.id();

                List<RequestHistoryDto> requestHistories = requestHistoryService.getAllRequestHistoryByRequestID(requestReportLine.getRequest().getRecId());
                if (!requestHistories.isEmpty() && this.duplicateChecker(requestHistories)) {
                    activityId = ACTIVITY_REVIEW_EDIT.id();
                }
                requestHistoryService.saveRequestHistoryByAction(requestReportLine.getRequest(), activityId);

                List<RequestReportLine> reportLines = requestReportLineRepository.getByRequest(request.getRequestId());

                response = reportLines.stream().map(i -> {
                    RequestReportLineDto requestReportLineDto = new RequestReportLineDto();
                    BeanUtils.copyProperties(i, requestReportLineDto);
                    requestReportLineDto.setRequestId(request.getRequestId());
                    requestReportLineDto.setReportLine(ReportLineMapper.INSTANCE.toRequestReportLineDto(i.getReportLine()));
                    return requestReportLineDto;
                }).collect(Collectors.toList());

            }
        }
        return response;
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Request request) {
        requestReportLineRepository.deleteRequestReportLineByRequestId(request.getRecId());
    }

    @Override
    public List<Long> findRequestByReportLineId(List<Long> reportLineId) {
        List<Long> requestId = new ArrayList<>();
        List<RequestReportLine> requestReportLineList = requestReportLineRepository.findRequestReportLineByReportLineId(reportLineId);

        if (requestReportLineList != null && !requestReportLineList.isEmpty()) {
            for (RequestReportLine reportLine : requestReportLineList) {
                requestId.add(reportLine.getRequest().getRecId());
            }
        }
        return requestId;
    }

    @Override
    public List<RequestReportLineDto> getRequestReportLineByRequest(Long requestId) {
        List<RequestReportLine> requestReportLineList = requestReportLineRepository.getByRequest(requestId);
        List<RequestReportLineDto> requestReportLineDtos = new ArrayList<>();
        for (RequestReportLine reportLine : requestReportLineList) {
            RequestReportLineDto requestReportLineDto = new RequestReportLineDto();
            ReportLineDto reportLineDto = new ReportLineDto();
            reportLineDto.setRecId(reportLine.getReportLine().getRecId());
            reportLineDto.setPhone(reportLine.getReportLine().getPhone());
            reportLineDto.setMobilePhone(reportLine.getReportLine().getPhone());
            reportLineDto.setEmail(reportLine.getReportLine().getEmail());
            reportLineDto.setUserId(String.valueOf(reportLine.getReportLine().getUserId()));
            reportLineDto.setReportLineName(reportLine.getReportLine().getReportLineName());
            reportLineDto.setLoginId(reportLine.getReportLine().getLoginId());
            requestReportLineDto.setReportLine(reportLineDto);
            requestReportLineDto.setComment(reportLine.getComment());
            requestReportLineDto.setRecId(reportLine.getRecId());
            requestReportLineDto.setCreatedBy(reportLine.getCreatedBy());
            requestReportLineDto.setCreatedDate(reportLine.getCreatedDate());
            requestReportLineDto.setUpdatedBy(reportLine.getUpdatedBy());
            requestReportLineDto.setUpdatedDate(reportLine.getUpdatedDate());
            requestReportLineDtos.add(requestReportLineDto);
        }
        return requestReportLineDtos;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByRequestId(Long requestId) {
        log.info("Delete all Report Line by Request ID : {}", requestId);
        requestReportLineRepository.deleteRequestReportLineByRequestId(requestId);
    }

    @Override
    public EPAuthReportLineResponse getReportLineListByConditions(ReportLineSearchRequest request, String[] privilegeCodes) {
        EPAuthUserSearchRequest epAuthUserSearchRequest = EPAuthUserSearchRequest.builder()
                .conditionSearchList(request.getConditionSearchList())
                .tenantId(request.getTenantId())
                .page(request.getPage())
                .pageSize(request.getPageSize())
                .sortBy(request.getSortBy())
                .sortOrder(request.getSortOrder())
                .build();

        EPAuthUserListResponse epAuthUserListResponse = new EPAuthUserListResponse();
        try {
            epAuthUserListResponse = epAuthClient.getEPAuthUserList(privilegeCodes, epAuthUserSearchRequest);
        } catch(Exception ex) {
            ex.getMessage();
        }
        List<EPAuthReportLineDto> epAuthReportLineDtoList = new ArrayList<>();

        if (epAuthUserListResponse.getData() == null) {
            return EPAuthReportLineResponse.builder()
                    .data(null)
                    .page(epAuthUserListResponse.getPage())
                    .pageSize(epAuthUserListResponse.getPageSize())
                    .total(epAuthUserListResponse.getTotal())
                    .totalPage(epAuthUserListResponse.getTotalPage())
                    .build();
        }

        for (EPAuthUserDTO epAuthUserDTO: epAuthUserListResponse.getData()) {
            epAuthReportLineDtoList.add(
                    EPAuthReportLineDto.builder()
                            .sysUserId(epAuthUserDTO.getSysUserId())
                            .loginId(epAuthUserDTO.getLoginId())
                            .fullName(epAuthUserDTO.getFullName())
                            .email(epAuthUserDTO.getEmail())
                            .mobilePhone(epAuthUserDTO.getMobilePhone())
                            .phone(epAuthUserDTO.getPhone())
                            .timezone(epAuthUserDTO.getTimezone())
                            .build());
        }

        epAuthReportLineDtoList.sort(Comparator.comparing(EPAuthReportLineDto::getFullName));
        return EPAuthReportLineResponse.builder()
                .data(epAuthReportLineDtoList)
                .page(epAuthUserListResponse.getPage())
                .pageSize(epAuthUserListResponse.getPageSize())
                .total(epAuthUserListResponse.getTotal())
                .totalPage(epAuthUserListResponse.getTotalPage())
                .build();
    }
}
