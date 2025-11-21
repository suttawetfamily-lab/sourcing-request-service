package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.EpAuthClient;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Requester;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.mapper.RequestMapper;
import com.pantavanij.sourcingreq.services.domain.mapper.RequesterMapper;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthRequesterResponse;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthUserListResponse;
import com.pantavanij.sourcingreq.services.domain.response.RequesterResponse;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequesterRepository;
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
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.Role.REVIEWER;
import static com.pantavanij.sourcingreq.services.enums.SearchRequester.*;
import static com.pantavanij.sourcingreq.services.util.Constant.REC_ID;
import static com.pantavanij.sourcingreq.services.util.Constant.TENANT;


@Slf4j
@RequiredArgsConstructor
@Service
public class RequesterServiceImpl implements RequesterService {

    private final RequesterRepository requesterRepository;
    private final UaaService uaaService;
    private final EPAuthService epAuthService;
    private final TenantService tenantService;
    private final TenantConfigService tenantConfigService;
    private final EpAuthClient epAuthClient;

    @Override
    public RequesterResponse getRequestRequester(RequesterSearchRequest request, Pageable pageable) {
        Page<Requester> requestRequesters = requesterRepository.findAll(Specification.where(getSpecificationByCondition(request)), pageable);
        int totalPage = requestRequesters.getTotalPages();
        long total = requestRequesters.getTotalElements();

        List<RequesterDto> requestRequesterDtos = RequesterMapper.INSTANCE.toRequestRequesterDtoList(requestRequesters.getContent())
                .stream()
                .filter(i -> !request.getExceptRequesters().contains(i.getRecId().intValue()))
                .distinct()
                .collect(Collectors.toList());

        return RequesterResponse.builder()
                .total(total)
                .totalPage(totalPage)
                .data(requestRequesterDtos)
                .pageSize(requestRequesterDtos.size())
                .build();
    }

    @Override
    public Integer saveRequester(RequesterDto requesterDto) {
        UserDto userDto = AppUtil.getUser();
        Tenant tenant = tenantService.findByCode(userDto.getTenantId());
        log.info("Save Report line for SysUserId : {}, username: {}", requesterDto.getUserId(), requesterDto.getRequesterName());
        Requester requester = requesterRepository.findByUserId(Integer.parseInt(requesterDto.getUserId()))
                .orElse(new Requester());

        requester.setPhone(requesterDto.getPhone());
        requester.setEmail(requesterDto.getEmail());
        requester.setUserId(Integer.parseInt(requesterDto.getUserId()));
        requester.setRequesterName(requesterDto.getRequesterName());
        requester.setTenant(tenant);
        requester.setUpdatedBy(userDto.getUsername());
        requester.setUpdatedDate(DateTimeUtil.getTimestampUTC());
        requester.setLoginId(requesterDto.getLoginId());

        if (StringUtils.isEmpty(requester.getCreatedBy())) {
            requester.setCreatedBy(userDto.getUsername());
        }
        if (requester.getCreatedDate() == null) {
            requester.setCreatedDate(DateTimeUtil.getTimestampUTC());
        }

        return requesterRepository.save(requester).getRecId();
    }

    @Override
    public EPAuthRequesterResponse getRequesterListByConditions(RequesterSearchRequest request) {
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

        List<EPAuthRequesterDto> epAuthRequesterDtoList = new ArrayList();

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
            RequesterDto saveRequester = new RequesterDto();
            saveRequester.setPhone(epAuthUserDTO.getPhone());
            saveRequester.setEmail(epAuthUserDTO.getEmail());
            saveRequester.setUserId(epAuthUserDTO.getSysUserId().toString());
            saveRequester.setRequesterName(epAuthUserDTO.getFullName());
            saveRequester.setMobilePhone(epAuthUserDTO.getMobilePhone());
            saveRequester.setLoginId(epAuthUserDTO.getLoginId());
            saveRequester(saveRequester);

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
        return EPAuthRequesterResponse.builder()
                .data(epAuthRequesterDtoList)
                .page(epAuthUserListResponse.getPage())
                .pageSize(epAuthUserListResponse.getPageSize())
                .total(epAuthUserListResponse.getTotal())
                .totalPage(epAuthUserListResponse.getTotalPage())
                .build();
    }

    @Override
    public List<Requester> findByRequesterName(String requesterName) {
        return requesterRepository.findByRequesterName(requesterName);
    }

    @Override
    public RequesterDto createRequester(RequesterRequest requesterRequest) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        try {
            Optional<Requester> requesterOptional = requesterRepository.findFirstByTenantRecIdOrderBySequenceDesc(tenant.getRecId());
            if (requesterOptional.isPresent() && requesterRequest.getSequence() == 0) {
                requesterRequest.setSequence(requesterOptional.get().getSequence() + 1);
            } else if (requesterOptional.isEmpty() && requesterRequest.getSequence() == 0) {
                requesterRequest.setSequence(1);
            } else {
                requesterOptional.ifPresent(requester -> requesterRepository.reOrderOtherRequesterSequence(tenant.getRecId(), requester.getSequence() + 1, requesterRequest.getSequence()));
            }
            Requester requester = requesterRepository.save(getSubmitRequester(requesterRequest));
            return RequestMapper.INSTANCE.toRequesterDto(requester, timeZone);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public RequesterDto updateRequester(RequesterRequest requesterRequest) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        try {
            Optional<Requester> requester = requesterRepository.findRequesterByRecIdAndTenant(requesterRequest.getRecId(), tenant.getRecId());
            if (requester.isPresent()) {
                requesterRepository.reOrderOtherRequesterSequence(tenant.getRecId(), requester.get().getSequence(), requesterRequest.getSequence());
                Requester requesterUpdate = requesterRepository.save(getRequesterUpdate(requester.get(), requesterRequest));
                return RequestMapper.INSTANCE.toRequesterDto(requesterUpdate, timeZone);
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
    public boolean deleteRequester(Integer requesterId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        try {
            Optional<Requester> requesterDto = requesterRepository.findRequesterByRecIdAndTenant(requesterId, tenant.getRecId());
            if (requesterDto.isPresent()) {
                requesterRepository.deleteRequesterByRecId(requesterId);
                requesterRepository.reOrderSequenceByTenantRecId(tenant.getRecId());
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public RequesterDto updateRequesterSequence(SequenceRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Optional<Requester> requesterOptional = requesterRepository.findRequesterByRecIdAndTenant(request.getRecId(), tenant.getRecId());
        if (requesterOptional.isPresent()) {
            requesterRepository.reOrderOtherRequesterSequence(tenant.getRecId(), requesterOptional.get().getSequence(), request.getSequence());
            Requester requesterUpdateSequence = requesterRepository.save(getRequesterSequenceUpdate(requesterOptional.get(), request));
            return RequestMapper.INSTANCE.toRequesterDto(requesterUpdateSequence);
        }
        return null;
    }

    private Requester getRequesterSequenceUpdate(Requester requester, SequenceRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        return Requester.builder()
                .recId(Integer.valueOf(requester.getRecId()))
                .tenant(tenant)
                .userId(requester.getUserId())
                .loginId(requester.getLoginId())
                .requesterName(requester.getRequesterName())
                .email(requester.getEmail())
                .phone(requester.getPhone())
                .sequence(request.getSequence())
                .isDefault(requester.isDefault())
                .active(requester.isActive())
                .createdBy(requester.getCreatedBy())
                .createdDate(requester.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }

    public Requester getRequesterUpdate(Requester requester, RequesterRequest requesterRequest) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        return Requester.builder()
                .recId(Integer.valueOf(requesterRequest.getRecId()))
                .tenant(tenant)
                .userId(requesterRequest.getUserId())
                .loginId(requesterRequest.getLoginId())
                .requesterName(requesterRequest.getRequesterName())
                .email(requesterRequest.getEmail())
                .phone(requesterRequest.getPhone())
                .sequence(requesterRequest.getSequence())
                .isDefault(requesterRequest.isDefault())
                .active(requesterRequest.isActive())
                .createdBy(requester.getCreatedBy())
                .createdDate(requester.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }

    public Requester getSubmitRequester(RequesterRequest requesterRequest) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        return Requester.builder()
                .tenant(tenant)
                .userId(requesterRequest.getUserId())
                .loginId(requesterRequest.getLoginId())
                .requesterName(requesterRequest.getRequesterName())
                .email(requesterRequest.getEmail())
                .phone(requesterRequest.getPhone())
                .sequence(requesterRequest.getSequence())
                .isDefault(requesterRequest.isDefault())
                .active(requesterRequest.isActive())
                .createdBy(AppUtil.getUserName())
                .createdDate(DateTimeUtil.getTimestampUTC())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }

    private Specification<Requester> getSpecificationByCondition(RequesterSearchRequest searchRequest) {
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
    public RequesterSearchDto searchRequesterListByConditions(RequesterSearchRequest request, Pageable pageable) {
        Page<Requester> requesterPage = requesterRepository.findAll(Specification.where(getRequesterSpecificationByCondition(request)), pageable);
        int totalPage = requesterPage.getTotalPages();
        long total = requesterPage.getTotalElements();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        List<RequesterDto> resultList = RequestMapper.INSTANCE.toRequesterListDto(requesterPage.getContent(), timeZone);

        RequesterSearchDto requesterSearchDto = new RequesterSearchDto();
        requesterSearchDto.setRequesters(resultList);
        requesterSearchDto.setTotal(total);
        requesterSearchDto.setTotalPage(totalPage);
        requesterSearchDto.setPageSize(pageable.getPageSize());

        return requesterSearchDto;
    }

    private Specification<Requester> getRequesterSpecificationByCondition(RequesterSearchRequest searchRequest) {
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
                        if (REQUESTER_NAME.description().equalsIgnoreCase(searchField)) {
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
    public RequesterDto findRequesterByRecId(Integer requesterId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        Optional<Requester> requester = requesterRepository.findRequesterByRecIdAndTenant(requesterId, tenant.getRecId());
        return requester.map(line -> RequestMapper.INSTANCE.toRequesterDto(line, timeZone)).orElse(null);
    }

}
