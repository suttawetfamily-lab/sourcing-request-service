package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.EpAuthClient;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.mapper.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.ReviewerRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.Role.REVIEWER;
import static com.pantavanij.sourcingreq.services.enums.SearchReviewer.*;
import static com.pantavanij.sourcingreq.services.util.Constant.REC_ID;
import static com.pantavanij.sourcingreq.services.util.Constant.TENANT;


@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewerServiceImpl implements ReviewerService {

    private final ReviewerRepository reviewerRepository;
    private final UaaService uaaService;
    private final EPAuthService epAuthService;
    private final TenantService tenantService;
    private final TenantConfigService tenantConfigService;
    private final EpAuthClient epAuthClient;

    @Override
    public ReviewerResponse getRequestReviewer(ReviewerSearchRequest request, Pageable pageable) {
        Page<Reviewer> requestReviewers = reviewerRepository.findAll(Specification.where(getSpecificationByCondition(request)), pageable);
        int totalPage = requestReviewers.getTotalPages();
        long total = requestReviewers.getTotalElements();

        List<ReviewerDto> requestReviewerDtos = ReviewerMapper.INSTANCE.toRequestReviewerDtoList(requestReviewers.getContent())
                .stream()
                .filter(i -> !request.getExceptReviewers().contains(i.getRecId().intValue()))
                .distinct()
                .collect(Collectors.toList());

        return ReviewerResponse.builder()
                .total(total)
                .totalPage(totalPage)
                .data(requestReviewerDtos)
                .pageSize(requestReviewerDtos.size())
                .build();
    }

    @Override
    public Integer saveReviewer(ReviewerDto reviewerDto) {
        UserDto userDto = AppUtil.getUser();
        Tenant tenant = tenantService.findByCode(userDto.getTenantId());
        log.info("Save Report line for SysUserId : {}, username: {}", reviewerDto.getUserId(), reviewerDto.getReviewerName());
        Reviewer reviewer = reviewerRepository.findByUserId(Integer.parseInt(reviewerDto.getUserId()))
                .orElse(new Reviewer());

        reviewer.setPhone(reviewerDto.getPhone());
        reviewer.setEmail(reviewerDto.getEmail());
        reviewer.setUserId(Integer.parseInt(reviewerDto.getUserId()));
        reviewer.setReviewerName(reviewerDto.getReviewerName());
        reviewer.setTenant(tenant);
        reviewer.setUpdatedBy(userDto.getUsername());
        reviewer.setUpdatedDate(DateTimeUtil.getTimestampUTC());
        reviewer.setLoginId(reviewerDto.getLoginId());

        if (StringUtils.isEmpty(reviewer.getCreatedBy())) {
            reviewer.setCreatedBy(userDto.getUsername());
        }
        if (reviewer.getCreatedDate() == null) {
            reviewer.setCreatedDate(DateTimeUtil.getTimestampUTC());
        }

        return reviewerRepository.save(reviewer).getRecId();
    }

    @Override
    public EPAuthReviewerResponse getReviewerListByConditions(ReviewerSearchRequest request) {
        EPAuthUserSearchRequest epAuthUserSearchRequest = EPAuthUserSearchRequest.builder()
                .conditionSearchList(request.getConditionSearchList())
                .tenantId(request.getTenantId())
                .page(request.getPage())
                .pageSize(request.getPageSize())
                .sortBy(request.getSortBy())
                .sortOrder(request.getSortOrder())
                .build();

        String[] privilegeCode = new String[] {REVIEWER.privilegeCode()};
        EPAuthUserListResponse epAuthUserListResponse = new EPAuthUserListResponse();
        try {
            epAuthUserListResponse = epAuthClient.getEPAuthUserList(privilegeCode, epAuthUserSearchRequest);
        } catch (Exception ex) {
            ex.getMessage();
        }

        List<EPAuthReviewerDto> epAuthReviewerDtoList = new ArrayList();

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
            ReviewerDto saveReviewer = new ReviewerDto();
            saveReviewer.setPhone(epAuthUserDTO.getPhone());
            saveReviewer.setEmail(epAuthUserDTO.getEmail());
            saveReviewer.setUserId(epAuthUserDTO.getSysUserId().toString());
            saveReviewer.setReviewerName(epAuthUserDTO.getFullName());
            saveReviewer.setMobilePhone(epAuthUserDTO.getMobilePhone());
            saveReviewer.setLoginId(epAuthUserDTO.getLoginId());
            saveReviewer(saveReviewer);

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
        return EPAuthReviewerResponse.builder()
                .data(epAuthReviewerDtoList)
                .page(epAuthUserListResponse.getPage())
                .pageSize(epAuthUserListResponse.getPageSize())
                .total(epAuthUserListResponse.getTotal())
                .totalPage(epAuthUserListResponse.getTotalPage())
                .build();
    }

    @Override
    public List<Reviewer> findByReviewerName(String reviewerName) {
        return reviewerRepository.findByReviewerName(reviewerName);
    }

    @Override
    public ReviewerDto createReviewer(ReviewerRequest reviewerRequest) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        try {
            Optional<Reviewer> reviewerOptional = reviewerRepository.findFirstByTenantRecIdOrderBySequenceDesc(tenant.getRecId());
            if (reviewerOptional.isPresent() && reviewerRequest.getSequence() == 0) {
                reviewerRequest.setSequence(reviewerOptional.get().getSequence() + 1);
            } else if (reviewerOptional.isEmpty() && reviewerRequest.getSequence() == 0) {
                reviewerRequest.setSequence(1);
            } else {
                reviewerOptional.ifPresent(reviewer -> reviewerRepository.reOrderOtherReviewerSequence(tenant.getRecId(), reviewer.getSequence() + 1, reviewerRequest.getSequence()));
            }
            Reviewer reviewer = reviewerRepository.save(getSubmitReviewer(reviewerRequest));
            return RequestMapper.INSTANCE.toReviewerDto(reviewer, timeZone);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ReviewerDto updateReviewer(ReviewerRequest reviewerRequest) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        try {
            Optional<Reviewer> reviewer = reviewerRepository.findReviewerByRecIdAndTenant(reviewerRequest.getRecId(), tenant.getRecId());
            if (reviewer.isPresent()) {
                reviewerRepository.reOrderOtherReviewerSequence(tenant.getRecId(), reviewer.get().getSequence(), reviewerRequest.getSequence());
                Reviewer reviewerUpdate = reviewerRepository.save(getReviewerUpdate(reviewer.get(), reviewerRequest));
                return RequestMapper.INSTANCE.toReviewerDto(reviewerUpdate, timeZone);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    public boolean deleteReviewer(Integer reviewerId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        try {
            Optional<Reviewer> reviewerDto = reviewerRepository.findReviewerByRecIdAndTenant(reviewerId, tenant.getRecId());
            if (reviewerDto.isPresent()) {
                reviewerRepository.deleteReviewerByRecId(reviewerId);
                reviewerRepository.reOrderSequenceByTenantRecId(tenant.getRecId());
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ReviewerDto updateReviewerSequence(SequenceRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Optional<Reviewer> reviewerOptional = reviewerRepository.findReviewerByRecIdAndTenant(request.getRecId(), tenant.getRecId());
        if (reviewerOptional.isPresent()) {
            reviewerRepository.reOrderOtherReviewerSequence(tenant.getRecId(), reviewerOptional.get().getSequence(), request.getSequence());
            Reviewer reviewerUpdateSequence = reviewerRepository.save(getReviewerSequenceUpdate(reviewerOptional.get(), request));
            return RequestMapper.INSTANCE.toReviewerDto(reviewerUpdateSequence);
        }
        return null;
    }

    private Reviewer getReviewerSequenceUpdate(Reviewer reviewer, SequenceRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        return Reviewer.builder()
                .recId(Integer.valueOf(reviewer.getRecId()))
                .tenant(tenant)
                .userId(reviewer.getUserId())
                .loginId(reviewer.getLoginId())
                .reviewerName(reviewer.getReviewerName())
                .email(reviewer.getEmail())
                .phone(reviewer.getPhone())
                .sequence(request.getSequence())
                .isDefault(reviewer.isDefault())
                .active(reviewer.isActive())
                .createdBy(reviewer.getCreatedBy())
                .createdDate(reviewer.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }

    public Reviewer getReviewerUpdate(Reviewer reviewer, ReviewerRequest reviewerRequest) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        return Reviewer.builder()
                .recId(Integer.valueOf(reviewerRequest.getRecId()))
                .tenant(tenant)
                .userId(reviewerRequest.getUserId())
                .loginId(reviewerRequest.getLoginId())
                .reviewerName(reviewerRequest.getReviewerName())
                .email(reviewerRequest.getEmail())
                .phone(reviewerRequest.getPhone())
                .sequence(reviewerRequest.getSequence())
                .isDefault(reviewerRequest.isDefault())
                .active(reviewerRequest.isActive())
                .createdBy(reviewer.getCreatedBy())
                .createdDate(reviewer.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }

    public Reviewer getSubmitReviewer(ReviewerRequest reviewerRequest) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        return Reviewer.builder()
                .tenant(tenant)
                .userId(reviewerRequest.getUserId())
                .loginId(reviewerRequest.getLoginId())
                .reviewerName(reviewerRequest.getReviewerName())
                .email(reviewerRequest.getEmail())
                .phone(reviewerRequest.getPhone())
                .sequence(reviewerRequest.getSequence())
                .isDefault(reviewerRequest.isDefault())
                .active(reviewerRequest.isActive())
                .createdBy(AppUtil.getUserName())
                .createdDate(DateTimeUtil.getTimestampUTC())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }

    private Specification<Reviewer> getSpecificationByCondition(ReviewerSearchRequest searchRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
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

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public ReviewerSearchDto searchReviewerListByConditions(ReviewerSearchRequest request, Pageable pageable) {
        Page<Reviewer> reviewerPage = reviewerRepository.findAll(Specification.where(getReviewerSpecificationByCondition(request)), pageable);
        int totalPage = reviewerPage.getTotalPages();
        long total = reviewerPage.getTotalElements();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        List<ReviewerDto> resultList = RequestMapper.INSTANCE.toReviewerListDto(reviewerPage.getContent(), timeZone);

        ReviewerSearchDto reviewerSearchDto = new ReviewerSearchDto();
        reviewerSearchDto.setReviewers(resultList);
        reviewerSearchDto.setTotal(total);
        reviewerSearchDto.setTotalPage(totalPage);
        reviewerSearchDto.setPageSize(pageable.getPageSize());

        return reviewerSearchDto;
    }

    private Specification<Reviewer> getReviewerSpecificationByCondition(ReviewerSearchRequest searchRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
            if (tenant != null) {
                predicates.add(criteriaBuilder.equal(root.get(TENANT).get(REC_ID), tenant.getRecId()));
            }

            List<ConditionSearchRequest> conditionSearchRequestList = searchRequest.getConditionSearchList();
            if (conditionSearchRequestList != null && !conditionSearchRequestList.isEmpty()) {
                List<Predicate> orPredicates = new ArrayList<>();
                for (ConditionSearchRequest condition : conditionSearchRequestList) {
                    String searchField = condition.getSearchField();
                    String searchValue = condition.getSearchValue();

                    Predicate predicate = null;
                    if (StringUtils.isNotEmpty(searchField) && StringUtils.isNotEmpty(searchValue)) {
                        if (REVIEWER_NAME.description().equalsIgnoreCase(searchField)) {
                            predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%");
                        } else if (EMAIL.description().equalsIgnoreCase(searchField)) {
                            predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%");
                        } else if (CREATED_BY.description().equalsIgnoreCase(searchField)) {
                            predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%");
                        } else if (LOGIN_ID.description().equalsIgnoreCase(searchField)) {
                            predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%");
                        } else {
                            if (CommonUtils.isNumeric(searchValue)) {
                                predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%");
                            } else {
                                predicate= criteriaBuilder.like(root.get(searchField), "%" + searchValue.toLowerCase() + "%");
                            }
                        }
                        if (predicate != null) {
                            orPredicates.add(predicate);
                        }
                    }
                }
                if (!orPredicates.isEmpty()) {
                    predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
                }
            }

            query.distinct(true);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public ReviewerDto findReviewerByRecId(Integer reviewerId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        Optional<Reviewer> reviewer = reviewerRepository.findReviewerByRecIdAndTenant(reviewerId, tenant.getRecId());
        return reviewer.map(line -> RequestMapper.INSTANCE.toReviewerDto(line, timeZone)).orElse(null);
    }

}
