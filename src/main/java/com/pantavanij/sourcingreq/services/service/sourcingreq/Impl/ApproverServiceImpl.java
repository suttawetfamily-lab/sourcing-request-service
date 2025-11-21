package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.EpAuthClient;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Approver;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.mapper.ApproverMapper;
import com.pantavanij.sourcingreq.services.domain.mapper.RequestMapper;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.ApproverResponse;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthDeptApproverResponse;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthUserListResponse;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.ApproverRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.services.util.CommonUtils;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.Role.DEPT_APPROVER;
import static com.pantavanij.sourcingreq.services.enums.SearchApproverType.*;
import static com.pantavanij.sourcingreq.services.util.Constant.REC_ID;
import static com.pantavanij.sourcingreq.services.util.Constant.TENANT;


@Slf4j
@RequiredArgsConstructor
@Service
public class ApproverServiceImpl implements ApproverService {

    private final ApproverRepository approverRepository;
    private final UaaService uaaService;
    private final EPAuthService epAuthService;
    private final TenantService tenantService;
    private final TenantConfigService tenantConfigService;
    private final EpAuthClient epAuthClient;

    @Override
    public ApproverResponse getRequestApprover(ApproverSearchRequest request, Pageable pageable) {
        Page<Approver> requestApprovers = approverRepository.findAll(Specification.where(getSpecificationByCondition(request)), pageable);
        int totalPage = requestApprovers.getTotalPages();
        long total = requestApprovers.getTotalElements();

        List<ApproverDto> requestApproverDtos = ApproverMapper.INSTANCE.toRequestApproverDtoList(requestApprovers.getContent())
                .stream()
                .filter(i -> !request.getExceptApprovers().contains(i.getRecId().intValue()))
                .distinct()
                .collect(Collectors.toList());

        return ApproverResponse.builder()
                .total(total)
                .totalPage(totalPage)
                .data(requestApproverDtos)
                .pageSize(requestApproverDtos.size())
                .build();
    }
    
    @Override
    public Integer saveApprover(ApproverDto approverDto) {
        UserDto userDto = AppUtil.getUser();
        Tenant tenant = tenantService.findByCode(userDto.getTenantId());
        log.info("Save Report line for SysUserId : {}, username: {}", approverDto.getUserId(), approverDto.getApproverName());
        Approver approver = approverRepository.findApproverByTenantAndUserId(tenant, approverDto.getUserId())
                .orElse(new Approver());

        approver.setPhone(approverDto.getPhone());
        approver.setEmail(approverDto.getEmail());
        approver.setUserId(approverDto.getUserId());
        approver.setApproverName(approverDto.getApproverName());
        approver.setTenant(tenant);
        approver.setUpdatedBy(userDto.getUsername());
        approver.setUpdatedDate(DateTimeUtil.getTimestampUTC());
        approver.setLoginId(approverDto.getLoginId());

        if (StringUtils.isEmpty(approver.getCreatedBy())) {
            approver.setCreatedBy(userDto.getUsername());
        }
        if (approver.getCreatedDate() == null) {
            approver.setCreatedDate(DateTimeUtil.getTimestampUTC());
        }
        return approverRepository.save(approver).getRecId();
    }

    @Override
    public EPAuthDeptApproverResponse getApproverListByConditions(ApproverSearchRequest request) {
        EPAuthUserSearchRequest epAuthUserSearchRequest = EPAuthUserSearchRequest.builder()
                .conditionSearchList(request.getConditionSearchList())
                .tenantId(request.getTenantId())
                .page(request.getPage())
                .pageSize(request.getPageSize())
                .sortBy(request.getSortBy())
                .sortOrder(request.getSortOrder())
                .build();

        String[] privilegeCode = new String[] {DEPT_APPROVER.privilegeCode()};
        EPAuthUserListResponse epAuthUserListResponse = new EPAuthUserListResponse();
        try {
            epAuthUserListResponse = epAuthClient.getEPAuthUserList(privilegeCode, epAuthUserSearchRequest);
        } catch(Exception ex) {
            ex.getMessage();
        }

        List<EPAuthDeptApproverDto> epAuthDeptApproverDtoList = new ArrayList();

        if (epAuthUserListResponse.getData() == null) {
            return EPAuthDeptApproverResponse.builder()
                    .data(null)
                    .page(epAuthUserListResponse.getPage())
                    .pageSize(epAuthUserListResponse.getPageSize())
                    .total(epAuthUserListResponse.getTotal())
                    .totalPage(epAuthUserListResponse.getTotalPage())
                    .build();
        }

        for (EPAuthUserDTO epAuthUserDTO: epAuthUserListResponse.getData()) {
            ApproverDto saveApprover = new ApproverDto();
            saveApprover.setPhone(epAuthUserDTO.getPhone());
            saveApprover.setEmail(epAuthUserDTO.getEmail());
            saveApprover.setUserId(epAuthUserDTO.getSysUserId());
            saveApprover.setApproverName(epAuthUserDTO.getFullName());
            saveApprover.setLoginId(epAuthUserDTO.getLoginId());
            this.saveApprover(saveApprover);

            epAuthDeptApproverDtoList.add(
                    EPAuthDeptApproverDto.builder()
                            .sysUserId(epAuthUserDTO.getSysUserId())
                            .loginId(epAuthUserDTO.getLoginId())
                            .fullName(epAuthUserDTO.getFullName())
                            .email(epAuthUserDTO.getEmail())
                            .mobilePhone(epAuthUserDTO.getMobilePhone())
                            .phone(epAuthUserDTO.getPhone())
                            .timezone(epAuthUserDTO.getTimezone())
                            .build());
        }
        return EPAuthDeptApproverResponse.builder()
                .data(epAuthDeptApproverDtoList)
                .page(epAuthUserListResponse.getPage())
                .pageSize(epAuthUserListResponse.getPageSize())
                .total(epAuthUserListResponse.getTotal())
                .totalPage(epAuthUserListResponse.getTotalPage())
                .build();
    }


    private Specification<Approver> getSpecificationByCondition(ApproverSearchRequest searchRequest) {
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
    public ApproverSearchDto searchApproverListByCondition(ApproverSearchRequest approverSearchRequest, Pageable pageable) {
        Page<Approver> approverPage = approverRepository.findAll(Specification.where(getSearchSpecificationByCondition(approverSearchRequest)), pageable);
        int totalPage = approverPage.getTotalPages();
        long total = approverPage.getTotalElements();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);

        List<ApproverDto> approverDtoList = RequestMapper.INSTANCE.toApproverListDto(approverPage.getContent(), timeZone);
        ApproverSearchDto approverSearchDto = new ApproverSearchDto();
        approverSearchDto.setApproverDtoList(approverDtoList);
        approverSearchDto.setTotal(total);
        approverSearchDto.setTotalPage(totalPage);
        approverSearchDto.setPage(pageable.getPageNumber());
        return approverSearchDto;
    }

    private Specification<Approver> getSearchSpecificationByCondition(ApproverSearchRequest approverSearchRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
            if (tenant != null) {
                predicates.add(criteriaBuilder.equal(root.get(TENANT).get(REC_ID), tenant.getRecId()));
            }

            List<ConditionSearchRequest> conditionSearchRequestList = approverSearchRequest.getConditionSearchList();
            if (conditionSearchRequestList != null && !conditionSearchRequestList.isEmpty()) {
                List<Predicate> orPredicates = new ArrayList<>();
                for (ConditionSearchRequest condition : conditionSearchRequestList) {
                    String searchField = condition.getSearchField();
                    String searchValue = condition.getSearchValue();

                    Predicate predicate = null;
                    if (StringUtils.isNotEmpty(searchField) && StringUtils.isNotEmpty(searchValue)) {
                        if (APPROVER_NAME.description().equalsIgnoreCase(searchField)) {
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
                                predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue.toLowerCase() + "%");
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
    public ApproverDto findApproverByRecId(Integer approverId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        Optional<Approver> approvers = approverRepository.findApproverByRecIdAndTenant(approverId, tenant.getRecId());
        return approvers.map(approver -> RequestMapper.INSTANCE.toApproverDto(approver, timeZone)).orElse(null);
    }

    @Override
    public ApproverDto createApprover(ApproverRequest approverRequest) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());

        Optional<Approver> approverOptional = approverRepository.findFirstByTenantRecIdOrderBySequenceDesc(tenant.getRecId());
        if (approverOptional.isPresent()) {
            approverRequest.setSequence(approverOptional.get().getSequence() + 1);
        } else {
            approverRequest.setSequence(1);
        }

        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        Approver approverSubmit = approverRepository.save(getSubmitApprover(approverRequest));
        return RequestMapper.INSTANCE.toApproverDto(approverSubmit, timeZone);
    }

    @Override
    public ApproverDto updateApprover(ApproverRequest approverRequest) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Optional<Approver> approver = approverRepository.findApproverByRecIdAndTenant(approverRequest.getRecId(), tenant.getRecId());
        if (approver.isPresent()) {
            approverRepository.reOrderOtherApproverSequence(tenant.getRecId(), approver.get().getSequence(), approverRequest.getSequence());
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            Approver approverUpdate = getApproverUpdate(approver.get(), approverRequest, tenant);
            approverRepository.save(approverUpdate);
            return RequestMapper.INSTANCE.toApproverDto(approverUpdate, timeZone);
        }
        return null;
    }

    @Override
    public boolean deleteApprover(Integer approverId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Optional<Approver> reportLineDto = approverRepository.findApproverByRecIdAndTenant(approverId, tenant.getRecId());
        if (reportLineDto.isPresent()) {
            approverRepository.deleteApproverByRecId(approverId);
            return true;
        }
        return false;
    }

    @Override
    public ApproverDto updateApproverSequence(SequenceRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Optional<Approver> approverOptional = approverRepository.findApproverByRecIdAndTenant(request.getRecId(), tenant.getRecId());
        if (approverOptional.isPresent()) {
            approverRepository.reOrderOtherApproverSequence(tenant.getRecId(), approverOptional.get().getSequence(), request.getSequence());
            Approver approverUpdateSequence = approverRepository.save(getApproverUpdateSequence(approverOptional.get(), request, tenant));
            return RequestMapper.INSTANCE.toApproverDto(approverUpdateSequence);
        }
        return null;
    }

    public Approver getApproverUpdateSequence(Approver approver, SequenceRequest request, Tenant tenant) {
        return Approver.builder()
                .recId(approver.getRecId())
                .tenant(tenant)
                .userId(approver.getUserId())
                .loginId(approver.getLoginId())
                .approverName(approver.getApproverName())
                .email(approver.getEmail())
                .phone(approver.getPhone())
                .sequence(request.getSequence())
                .isDefault(approver.isDefault())
                .active(approver.isActive())
                .createdBy(approver.getCreatedBy())
                .createdDate(approver.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }

    public Approver getApproverUpdate(Approver approver, ApproverRequest approverRequest, Tenant tenant) {
        return Approver.builder()
                .recId(approverRequest.getRecId())
                .tenant(tenant)
                .userId(approverRequest.getUserId())
                .loginId(approverRequest.getLoginId())
                .approverName(approverRequest.getApproverName())
                .email(approverRequest.getEmail())
                .phone(approverRequest.getPhone())
                .sequence(approverRequest.getSequence())
                .isDefault(approverRequest.isDefault())
                .active(approverRequest.isActive())
                .createdBy(approver.getCreatedBy())
                .createdDate(approver.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }

    public Approver getSubmitApprover(ApproverRequest approverRequest) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        return Approver.builder()
                .tenant(tenant)
                .userId(approverRequest.getUserId())
                .loginId(approverRequest.getLoginId())
                .approverName(approverRequest.getApproverName())
                .email(approverRequest.getEmail())
                .phone(approverRequest.getPhone())
                .sequence(approverRequest.getSequence())
                .isDefault(approverRequest.isDefault())
                .active(approverRequest.isActive())
                .createdBy(AppUtil.getUserName())
                .createdDate(DateTimeUtil.getTimestampUTC())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }

}
