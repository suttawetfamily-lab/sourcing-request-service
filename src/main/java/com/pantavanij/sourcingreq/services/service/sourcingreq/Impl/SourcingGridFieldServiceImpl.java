package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.SourcingGridFieldDto;
import com.pantavanij.sourcingreq.services.domain.dto.SourcingGridFieldSearchDto;
import com.pantavanij.sourcingreq.services.domain.dto.SourcingGridFieldSearchableDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SourcingGridField;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantSourcingGridField;
import com.pantavanij.sourcingreq.services.domain.mapper.TenantSourcingGridFieldMapper;
import com.pantavanij.sourcingreq.services.domain.request.ConditionSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.SourcingGridFieldRequest;
import com.pantavanij.sourcingreq.services.domain.request.SourcingGridFieldSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.SourcingGridFieldSequenceRequest;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.SourcingGridFieldRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantSourcingGridFieldRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.SourcingGridFieldService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UaaService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import com.pantavanij.sourcingreq.services.util.UserDetailServiceUtil;
import lombok.RequiredArgsConstructor;
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

import static com.pantavanij.sourcingreq.services.util.Constant.REC_ID;
import static com.pantavanij.sourcingreq.services.util.Constant.TENANT;

@Service
@RequiredArgsConstructor
public class SourcingGridFieldServiceImpl implements SourcingGridFieldService {

    private final TenantSourcingGridFieldRepository tenantSourcingGridFieldRepository;
    private final SourcingGridFieldRepository sourcingGridFieldRepository;
    private final TenantService tenantService;
    private final UaaService uaaService;

    @Override
    public List<SourcingGridFieldDto> getSourcingGridField(String privilegeCode, String tenantCode) {

        List<TenantSourcingGridField> tenantSourcingGridFieldList =
                tenantSourcingGridFieldRepository.findByPrivilegeCodeAndTenant_CodeOrderBySequence(privilegeCode, tenantCode);

        List<TenantSourcingGridField> visibleList = tenantSourcingGridFieldList
                .stream()
                .filter(TenantSourcingGridField::isVisible)
                .collect(Collectors.toList());

        return TenantSourcingGridFieldMapper.INSTANCE.toSourcingGridFieldDto(visibleList);
    }

    @Override
    public List<SourcingGridFieldSearchableDto> getSourcingGridFieldSearchable(String privilegeCode, String tenantCode) {

        List<TenantSourcingGridField> tenantSourcingGridFieldList =
                tenantSourcingGridFieldRepository.findByPrivilegeCodeAndTenant_CodeAndSearchableOrderBySequence(
                        privilegeCode, tenantCode, true);

        return TenantSourcingGridFieldMapper.INSTANCE.toSourcingGridFieldSearchableDto(tenantSourcingGridFieldList);
    }

    @Override
    public SourcingGridFieldSearchDto searchSourcingGridFieldByCondition(SourcingGridFieldSearchRequest searchRequest, Pageable pageable) {
        Page<TenantSourcingGridField> tenantSourcingGridFieldList = tenantSourcingGridFieldRepository.findAll(Specification.where(getSpecificationByCondition(searchRequest)), pageable);
        int totalPage = tenantSourcingGridFieldList.getTotalPages();
        long total = tenantSourcingGridFieldList.getTotalElements();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        List<SourcingGridFieldDto> resultList = TenantSourcingGridFieldMapper.INSTANCE.toSourcingGridFieldDtoList(tenantSourcingGridFieldList.getContent(), timeZone);

        resultList = resultList.stream()
                .peek(sourcingGridFieldDto -> {
                    sourcingGridFieldDto.setCreatedByName(UserDetailServiceUtil.getFullName(sourcingGridFieldDto.getCreatedBy()));
                    sourcingGridFieldDto.setUpdatedByName(sourcingGridFieldDto.getUpdatedBy() != null ? UserDetailServiceUtil.getFullName(sourcingGridFieldDto.getUpdatedBy()) : null);
                }).collect(Collectors.toList());

        SourcingGridFieldSearchDto sourcingGridFieldSearchDto = new SourcingGridFieldSearchDto();
        sourcingGridFieldSearchDto.setSourcingGridFields(resultList);
        sourcingGridFieldSearchDto.setTotal(total);
        sourcingGridFieldSearchDto.setTotalPage(totalPage);
        sourcingGridFieldSearchDto.setPageSize(resultList.size());
        return sourcingGridFieldSearchDto;
    }

//    @Override
//    @CachePut(value = "sourcingGridFieldDtoList", key="#tenantCode")
//    public List<SourcingGridFieldSearchableDto> refreshCache(String tenantCode) {
//        List<TenantSourcingGridField> tenantSourcingGridFieldList =
//                tenantSourcingGridFieldRepository.findByTenant_CodeAndSearchableOrderBySequence(tenantCode,true);
//        return TenantSourcingGridFieldMapper.INSTANCE.toSourcingGridFieldSearchableDto(tenantSourcingGridFieldList);
//    }
//    @Override
//    @Cacheable(value = "sourcingGridFieldDtoList", key="#tenantCode")
//    public List<SourcingGridFieldSearchableDto> getCache(String tenantCode) {
//        List<TenantSourcingGridField> tenantSourcingGridFieldList =
//                tenantSourcingGridFieldRepository.findByTenant_CodeAndSearchableOrderBySequence(tenantCode, true);
//        return TenantSourcingGridFieldMapper.INSTANCE.toSourcingGridFieldSearchableDto(tenantSourcingGridFieldList);
//    }


    private Specification<TenantSourcingGridField> getSpecificationByCondition(SourcingGridFieldSearchRequest searchRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
            if (tenant != null && tenant.getRecId() != null) {
                predicates.add(criteriaBuilder.equal(root.get(TENANT).get(REC_ID), tenant.getRecId()));
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
//            query.distinct(true);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public SourcingGridFieldDto findSourcingGridFieldByRecIdAndTenantIdAndPrivilegeCode(Integer sourcingGridFieldId, String privilegeCode) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        Optional<TenantSourcingGridField> sourcingGridFields = tenantSourcingGridFieldRepository.findTenantSourcingGridFieldByRecIdAndPrivilegeCodeAndTenantId(sourcingGridFieldId, privilegeCode, tenant.getRecId());
        return sourcingGridFields.map(sourcingGridField -> TenantSourcingGridFieldMapper.INSTANCE.toSourcingGridFieldDto(sourcingGridField, timeZone)).orElse(null);
    }

    @Override
    public SourcingGridFieldDto createSourcingGridField(SourcingGridFieldRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        try {
            Optional<SourcingGridField> sourcingGridField = sourcingGridFieldRepository.findByCode(request.getCode());
            if(sourcingGridField.isPresent()) {
                Optional<TenantSourcingGridField> sourcingGridFieldMaxSequence = tenantSourcingGridFieldRepository.findFirstByTenantRecIdOrderBySequenceDesc(tenant.getRecId());
                if (sourcingGridFieldMaxSequence.isPresent()) {
                    if(request.getSequence() > sourcingGridFieldMaxSequence.get().getSequence()){
                        request.setSequence(sourcingGridFieldMaxSequence.get().getSequence() + 1);
                    } else {
                        tenantSourcingGridFieldRepository.adjustOtherTenantSourcingGridFieldSequences(request.getPrivilegeCode(), tenant.getRecId(),  sourcingGridFieldMaxSequence.get().getSequence() + 1, request.getSequence());
                    }
                } else {
                    request.setSequence(1);
                }
                TenantSourcingGridField sourcingGridFieldCreated = tenantSourcingGridFieldRepository.save(getSubmitSourcingGridField(tenant,sourcingGridField.get(), request));
                return TenantSourcingGridFieldMapper.INSTANCE.toSourcingGridFieldDto(sourcingGridFieldCreated, timeZone);
            }
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public SourcingGridFieldDto updateSourcingGridField(SourcingGridFieldRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        try {
            Optional<TenantSourcingGridField> sourcingGridField = tenantSourcingGridFieldRepository.findTenantSourcingGridFieldByRecIdAndPrivilegeCodeAndTenantId(request.getRecId(), request.getPrivilegeCode(), tenant.getRecId());
            if (sourcingGridField.isPresent()) {
                tenantSourcingGridFieldRepository.adjustOtherTenantSourcingGridFieldSequences(request.getPrivilegeCode(), tenant.getRecId(),  sourcingGridField.get().getSequence(), request.getSequence());
                TenantSourcingGridField sourcingGridFieldUpdated = tenantSourcingGridFieldRepository.save(getUpdateSourcingGridField(sourcingGridField.get(), request));
                return TenantSourcingGridFieldMapper.INSTANCE.toSourcingGridFieldDto(sourcingGridFieldUpdated, timeZone);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public SourcingGridFieldDto updateSourcingGridFieldSequence(SourcingGridFieldSequenceRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Optional<TenantSourcingGridField> sourcingGridField = tenantSourcingGridFieldRepository.findTenantSourcingGridFieldByRecIdAndPrivilegeCodeAndTenantId(request.getRecId(), request.getPrivilegeCode(), tenant.getRecId());
        if (sourcingGridField.isPresent()) {
            tenantSourcingGridFieldRepository.adjustOtherTenantSourcingGridFieldSequences(request.getPrivilegeCode(), tenant.getRecId(), sourcingGridField.get().getSequence(), request.getSequence());
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            TenantSourcingGridField sourcingGridFieldUpdate = getUpdateSourcingGridFieldSequence(sourcingGridField.get(), request);
            tenantSourcingGridFieldRepository.save(sourcingGridFieldUpdate);
            return TenantSourcingGridFieldMapper.INSTANCE.toSourcingGridFieldDto(sourcingGridFieldUpdate, timeZone);
        }
        return null;
    }

    public TenantSourcingGridField getSubmitSourcingGridField(Tenant tenant, SourcingGridField sourcingGridField,SourcingGridFieldRequest request) {
        return TenantSourcingGridField.builder()
                .tenant(tenant)
                .sourcingGridField(sourcingGridField)
                .privilegeCode(request.getPrivilegeCode())
                .code(request.getCode())
                .displayName(request.getDisplayName())
                .sequence(request.getSequence())
                .sorting(request.getSorting())
                .width(request.getWidth())
                .type(request.getType())
                .visible(request.isVisible())
                .createdBy(sourcingGridField.getCreatedBy())
                .createdDate(sourcingGridField.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }

    public TenantSourcingGridField getUpdateSourcingGridField(TenantSourcingGridField sourcingGridField, SourcingGridFieldRequest request) {
        return TenantSourcingGridField.builder()
                .id(sourcingGridField.getId())
                .tenant(sourcingGridField.getTenant())
                .sourcingGridField(sourcingGridField.getSourcingGridField())
                .privilegeCode(request.getPrivilegeCode() != null ? request.getPrivilegeCode() : sourcingGridField.getPrivilegeCode())
                .code(request.getCode() != null ? request.getCode() : sourcingGridField.getCode())
                .displayName(request.getDisplayName() != null ? request.getDisplayName() : sourcingGridField.getDisplayName())
                .sequence(request.getSequence() != null ? request.getSequence() : sourcingGridField.getSequence())
                .sorting(request.getSorting() != null ? request.getSorting() : sourcingGridField.getSorting())
                .width(request.getWidth() != null ? request.getWidth() : sourcingGridField.getWidth())
                .type(request.getType() != null ? request.getType() : sourcingGridField.getType())
                .visible(request.isVisible())
                .createdBy(sourcingGridField.getCreatedBy())
                .createdDate(sourcingGridField.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }

    public TenantSourcingGridField getUpdateSourcingGridFieldSequence(TenantSourcingGridField sourcingGridField, SourcingGridFieldSequenceRequest request) {
        return TenantSourcingGridField.builder()
                .id(sourcingGridField.getId())
                .tenant(sourcingGridField.getTenant())
                .sourcingGridField(sourcingGridField.getSourcingGridField())
                .privilegeCode(sourcingGridField.getPrivilegeCode())
                .code(sourcingGridField.getCode())
                .displayName(sourcingGridField.getDisplayName())
                .sequence(request.getSequence() != null ? request.getSequence() : sourcingGridField.getSequence())
                .sorting(sourcingGridField.getSorting())
                .width(sourcingGridField.getWidth())
                .type(sourcingGridField.getType())
                .visible(sourcingGridField.isVisible())
                .createdBy(sourcingGridField.getCreatedBy())
                .createdDate(sourcingGridField.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }


}
