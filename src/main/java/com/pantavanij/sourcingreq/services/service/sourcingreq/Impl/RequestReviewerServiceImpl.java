package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.EpAuthClient;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.mapper.RequestReviewerMapper;
import com.pantavanij.sourcingreq.services.domain.request.EPAuthUserSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestReviewerRequest;
import com.pantavanij.sourcingreq.services.domain.request.ReviewerCommentRequest;
import com.pantavanij.sourcingreq.services.domain.request.ReviewerSearchRequest;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthReviewerResponse;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthUserListResponse;
import com.pantavanij.sourcingreq.services.domain.response.UploadMessageResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.enums.Role;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestReportLineRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestReviewerRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.ReviewerRepository;
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
import static com.pantavanij.sourcingreq.services.enums.Role.*;
import static com.pantavanij.sourcingreq.services.enums.TenantRequestStatus.TENANT_REQUEST_CANCELLED;
import static com.pantavanij.sourcingreq.services.enums.TenantRequestStatus.TENANT_REQUEST_DRAFT;

@RequiredArgsConstructor
@Service
@Slf4j
public class RequestReviewerServiceImpl implements RequestReviewerService {
    private final RequestReportLineRepository requestReportLineRepository;
    private final RequestReviewerRepository requestReviewerRepository;
    private final RequestRepository requestRepository;
    private final RequestHistoryService requestHistoryService;
    private final UaaService uaaService;
    private final TenantConfigService tenantConfigService;
    private final TenantService tenantService;
    private final EpAuthClient epAuthClient;
    private final ReviewerRepository reviewerRepository;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<RequestReviewerDto> saveRequestReviewer(RequestReviewerRequest requestReviewerRequest, EPAuthReviewerResponse response) {
        // TODO to recheck if need to return deleted data?
        List<RequestReviewerDto> requestReviewerDtos = new ArrayList<>();
        List<RequestReviewer> requestReviewerList = setRequestReviewer(requestReviewerRequest, response);

        if(requestReviewerList != null && requestReviewerList.size() > 0) {
            // 1. Delete all exists data in RequestReviewer
            this.deleteReviewerByRequestId(requestReviewerRequest.getRequestId());
            // 2. Save all new data to RequestReviewer
            List<RequestReviewer> requestReviewers = requestReviewerRepository.saveAll(requestReviewerList);

            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            requestReviewerDtos = RequestReviewerMapper.INSTANCE.toRequestReviewerDtoList(requestReviewers, timeZone);
            requestReviewerDtos.stream()
                    .peek(requestReviewerDto -> {
                        requestReviewerDto.setCreatedByName(UserDetailServiceUtil.getFullName(requestReviewerDto.getCreatedBy()));
                    }).collect(Collectors.toList());

        } else {
            requestReviewerRepository.deleteRequestReviewerByRequestId(requestReviewerRequest.getRequestId());
        }
        return requestReviewerDtos;
    }

    @Override
    public List<RequestReviewerDto> findByRequest(Long requestId) {
        List<RequestReviewerDto> requestReviewerDtos = new ArrayList<>();
        Request request = requestRepository.findRequestsByRecId(requestId);
        List<RequestReviewer> requestReviewers = requestReviewerRepository.getByRequest(request.getRecId());
        if(requestReviewers != null && !requestReviewers.isEmpty()) {
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            requestReviewerDtos = RequestReviewerMapper.INSTANCE.toRequestReviewerDtoList(requestReviewers, timeZone);
            requestReviewerDtos.stream()
                    .peek(requestReviewerDto -> {
                        requestReviewerDto.setCreatedByName(UserDetailServiceUtil.getFullName(requestReviewerDto.getCreatedBy()));
                    }).collect(Collectors.toList());
        }
        return requestReviewerDtos;
    }

    @Override
    public List<RequestReviewerDto> saveCommentReviewer(ReviewerCommentRequest reviewerCommentRequest) {
        List<RequestReviewerDto> requestReviewerDtos = new ArrayList<>();
        List<RequestReviewer> requestReviewers = requestReviewerRepository.saveAll(setCommentRequestReviewer(reviewerCommentRequest));
        if(!requestReviewers.isEmpty()) {
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            requestReviewerDtos = RequestReviewerMapper.INSTANCE.toRequestReviewerDtoList(requestReviewers, timeZone);
            requestReviewerDtos.stream()
                    .peek(requestReviewerDto -> {
                        requestReviewerDto.setCreatedByName(UserDetailServiceUtil.getFullName(requestReviewerDto.getCreatedBy()));
                    }).collect(Collectors.toList());
        }
        return requestReviewerDtos;
    }

//    @Override
//    public List<Long> findRequestReviewerByReviewerName(String reviewerName) {
//        List<Long> requestId = new ArrayList<>();
//        List<RequestReviewer> requestReviewerList = requestReviewerRepository.findRequestReviewerByReviewerName(reviewerName);
//
//        if (requestReviewerList != null && !requestReviewerList.isEmpty()) {
//            for (RequestReviewer reviewer : requestReviewerList) {
//                requestId.add(reviewer.getRequest().getRecId());
//            }
//        }
//        return requestId;
//    }

    @Override
    public EPAuthReviewerResponse getReviewerListByConditions(ReviewerSearchRequest request, String[] privilegeCodes) {
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
//        String[] privilegeCodeList = new String[] {REVIEWER.privilegeCode()};
//        String privilegeCode = tenantConfigService.getEPAuthPrivilegeCode(tenant.getRecId(), REVIEWER.roleName());
        EPAuthUserListResponse epAuthUserListResponse = new EPAuthUserListResponse();
        try {
            epAuthUserListResponse = epAuthClient.getEPAuthUserList(privilegeCodes, epAuthUserSearchRequest);
        } catch(Exception ex) {
            ex.getMessage();
        }
        List<EPAuthReviewerDto> epAuthReviewerDtoList = new ArrayList<>();

        if (epAuthUserListResponse.getData() == null) {
            return EPAuthReviewerResponse.builder()
                    .data(null)
                    .page(epAuthUserListResponse.getPage())
                    .pageSize(epAuthUserListResponse.getPageSize())
                    .total(epAuthUserListResponse.getTotal())
                    .totalPage(epAuthUserListResponse.getTotalPage())
                    .build();
        }

        for (EPAuthUserDTO epAuthUserDTO: epAuthUserListResponse.getData()) {
            epAuthReviewerDtoList.add(
                    EPAuthReviewerDto.builder()
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
        epAuthReviewerDtoList.sort(Comparator.comparing(EPAuthReviewerDto::getFullName));

        return EPAuthReviewerResponse.builder()
                .data(epAuthReviewerDtoList)
                .page(epAuthUserListResponse.getPage())
                .pageSize(epAuthUserListResponse.getPageSize())
                .total(epAuthUserListResponse.getTotal())
                .totalPage(epAuthUserListResponse.getTotalPage())
                .build();
    }
    private List<RequestReviewer> setRequestReviewer(RequestReviewerRequest requestReviewerRequest, EPAuthReviewerResponse response) {
        Request request = requestRepository.findRequestByRecId(requestReviewerRequest.getRequestId());
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());

        List<RequestReviewer> requestReviewers = new ArrayList<>();
        for (String sysUserId : requestReviewerRequest.getReviewers()) {
            List<RequestReportLine> requestReportLines = requestReportLineRepository.getByRequest(requestReviewerRequest.getRequestId());
            if (requestReportLines != null && !requestReportLines.isEmpty()) {
                Optional<RequestReportLine> reportLine = requestReportLines.stream()
                        .filter(r -> r.getReportLine().getLoginId().equalsIgnoreCase(sysUserId)).findFirst();

                if (reportLine.isPresent()) {
                    throw new BusinessException(ApiMessage.E7074, ApiMessage.E7074.description());
                } else {
                    compareEpAuthReviewer(response, request, tenant, requestReviewers, sysUserId);
                }
            } else {
                compareEpAuthReviewer(response, request, tenant, requestReviewers, sysUserId);
            }
        }
        return requestReviewers;
    }

    private void compareEpAuthReviewer(EPAuthReviewerResponse response, Request request, Tenant tenant, List<RequestReviewer> requestReviewers, String sysUserId) {
        Optional<EPAuthReviewerDto> epAuthReviewerDto = response.getData()
                .stream()
                .filter(r -> r.getSysUserId() == Integer.parseInt(sysUserId))
                .findFirst();

        if (epAuthReviewerDto.isPresent()) {
            Optional<Reviewer> optionalReviewer = reviewerRepository.findByLoginId(tenant.getRecId(), epAuthReviewerDto.get().getLoginId());
            Reviewer reviewer = optionalReviewer.orElseGet(() -> Reviewer.builder()
                    .tenant(tenant)
                    .userId(epAuthReviewerDto.get().getSysUserId())
                    .loginId(epAuthReviewerDto.get().getLoginId())
                    .reviewerName(epAuthReviewerDto.get().getFullName())
                    .email(epAuthReviewerDto.get().getEmail())
                    .phone(epAuthReviewerDto.get().getPhone())
                    .createdBy(AppUtil.getUserName())
                    .createdDate(DateTimeUtil.getTimestampUTC())
                    .build());

            Optional<RequestReviewer> requestReviewer = requestReviewerRepository.findRequestReviewerByRequestIdAndReviewerId(request.getRecId(), reviewer.getRecId());

            String comment = requestReviewer.isPresent() ? requestReviewer.get().getComment() : "-";
            RequestReviewer resRequestReviewer = getRequestReviewer(reviewer, request, tenant, comment);
            requestReviewers.add(resRequestReviewer);
        }
    }

    private RequestReviewer getRequestReviewer(Reviewer reviewer, Request request, Tenant tenant, String comment) {
        RequestReviewer requestReviewer = new RequestReviewer();
        requestReviewer.setRequest(request);
        requestReviewer.setTenant(tenant);
        requestReviewer.setReviewer(reviewer);
        requestReviewer.setComment(comment);
        requestReviewer.setCreatedBy(AppUtil.getUserName());
        requestReviewer.setCreatedDate(DateTimeUtil.getTimestampUTC());
        requestReviewer.setUpdatedBy(AppUtil.getUserName());
        requestReviewer.setUpdatedDate(DateTimeUtil.getTimestampUTC());
        return requestReviewer;
    }

    private List<RequestReviewer> setCommentRequestReviewer(ReviewerCommentRequest reviewerCommentRequest) {
        List<RequestReviewer> requestReviewers = new ArrayList<>();
        Request request = requestRepository.findRequestsByRecId(reviewerCommentRequest.getRequestId());
        RequestReviewer existingRequestReviewer = requestReviewerRepository.findRequestReviewerByRecId(reviewerCommentRequest.getRecId());
        if (existingRequestReviewer != null) {
            if (existingRequestReviewer.getComment().equalsIgnoreCase("-")) {
                existingRequestReviewer.setComment(StringUtils.isNotBlank(reviewerCommentRequest.getComment()) ? reviewerCommentRequest.getComment() : "-");

                List<RequestHistoryDto> requestHistories = requestHistoryService.getAllRequestHistoryByRequestID(reviewerCommentRequest.getRequestId());
                if (!requestHistories.isEmpty() && duplicateChecker(requestHistories)) {
                    // Save Request History : 12 REVIEW_EDIT	Edit Comment request successfully
                    requestHistoryService.saveRequestHistoryByAction(request, ACTIVITY_REVIEW_EDIT.id());
                } else {
                    // Save Request History : 11 REVIEW_COMMENT	Comment request successfully
                    requestHistoryService.saveRequestHistoryByAction(request, ACTIVITY_REVIEW_COMMENT.id());
                }

            } else {
                existingRequestReviewer.setComment(StringUtils.isNotBlank(reviewerCommentRequest.getComment()) ? reviewerCommentRequest.getComment() : "-");
                // Save Request History : 12 REVIEW_EDIT	Edit Comment request successfully
                requestHistoryService.saveRequestHistoryByAction(request, ACTIVITY_REVIEW_EDIT.id());
            }
            existingRequestReviewer.setComment(StringUtils.isNotBlank(reviewerCommentRequest.getComment()) ? reviewerCommentRequest.getComment() : "-");
            existingRequestReviewer.setUpdatedBy(AppUtil.getUserName());
            existingRequestReviewer.setUpdatedDate(DateTimeUtil.getTimestampUTC());

            requestReviewerRepository.save(existingRequestReviewer);
            requestReviewers = requestReviewerRepository.getByRequest(request.getRecId());
        }

        return requestReviewers;
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
    public void deleteReviewerByRequestId(Long requestId) {
        log.info("Delete all reviewer of request id : {}", requestId);
        requestReviewerRepository.deleteRequestReviewerByRequestId(requestId);
    }
}
