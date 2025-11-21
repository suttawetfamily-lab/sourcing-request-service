package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.RequestGridFieldDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestGridFieldSearchDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestGridFieldSearchableDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.mapper.TenantRequestGridFieldMapper;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestGridFieldRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantOrganizationTemplateRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantRequestGridFieldRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestGridFieldService;
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
public class RequestGridFieldServiceImpl implements RequestGridFieldService {

    private final TenantRequestGridFieldRepository tenantRequestGridFieldRepository;
    private final TenantOrganizationTemplateRepository tenantOrganizationTemplateRepository;
    private final RequestGridFieldRepository requestGridFieldRepository;
    private final TenantService tenantService;
    private final UaaService uaaService;

    @Override
    public List<RequestGridFieldDto> getRequestGridField(String privilegeCode, String tenantCode, Integer organizationId) {

        List<TenantRequestGridField> tenantRequestGridFieldList;

        if (organizationId != null) {
            // 1) หา templateId จาก tenantCode + organizationId
            Integer templateId = tenantOrganizationTemplateRepository
                    .findTemplateIdByTenantCodeAndOrganizationId(tenantCode, organizationId);

            if (templateId == null) {
                throw new BusinessException(
                        String.format("Template not found for tenantCode=%s, organizationId=%s", tenantCode, organizationId)
                );
            }

            // 2) หา fields โดยใช้ privilegeCode + templateId
            tenantRequestGridFieldList =
                    tenantRequestGridFieldRepository.findByPrivilegeCodeAndTenant_CodeAndTemplateRecIdOrderBySequence(
                            privilegeCode, tenantCode, templateId);

        } else {
            // ใช้ privilegeCode + tenantCode (ไม่สน org)
            tenantRequestGridFieldList =
                    tenantRequestGridFieldRepository.findByPrivilegeCodeAndTenant_CodeOrderBySequence(privilegeCode, tenantCode);
        }

        // 3) filter visible = true
        List<TenantRequestGridField> visibleList = tenantRequestGridFieldList.stream()
                .filter(TenantRequestGridField::isVisible)
                .collect(Collectors.toList());

        // 4) map เป็น DTO
        return TenantRequestGridFieldMapper.INSTANCE.toRequestGridFieldDto(visibleList);
    }



    @Override
    public List<RequestGridFieldSearchableDto> getRequestGridFieldSearchable(String privilegeCode,
                                                                             String tenantCode,
                                                                             Integer organizationId) {
        List<TenantRequestGridField> tenantRequestGridFieldList;

        if (organizationId != null) {
            tenantRequestGridFieldList =
                    tenantRequestGridFieldRepository.findSearchableWithOrganization(privilegeCode, tenantCode, organizationId);
        } else {
            tenantRequestGridFieldList =
                    tenantRequestGridFieldRepository.findByPrivilegeCodeAndTenant_CodeAndSearchableOrderBySequence(privilegeCode, tenantCode, true);
        }

        return TenantRequestGridFieldMapper.INSTANCE.toRequestGridFieldSearchableDto(tenantRequestGridFieldList);
    }


    @Override
    public RequestGridFieldSearchDto searchRequestGridFieldByCondition(RequestGridFieldSearchRequest searchRequest, Pageable pageable) {
        Page<TenantRequestGridField> tenantRequestGridFieldList = tenantRequestGridFieldRepository.findAll(Specification.where(getSpecificationByCondition(searchRequest)), pageable);
        int totalPage = tenantRequestGridFieldList.getTotalPages();
        long total = tenantRequestGridFieldList.getTotalElements();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        List<RequestGridFieldDto> resultList = TenantRequestGridFieldMapper.INSTANCE.toRequestGridFieldDtoList(tenantRequestGridFieldList.getContent(), timeZone);

        resultList = resultList.stream()
                .peek(requestGridFieldDto -> {
                    requestGridFieldDto.setCreatedByName(UserDetailServiceUtil.getFullName(requestGridFieldDto.getCreatedBy()));
                    requestGridFieldDto.setUpdatedByName(requestGridFieldDto.getUpdatedBy() != null ? UserDetailServiceUtil.getFullName(requestGridFieldDto.getUpdatedBy()) : null);
                }).collect(Collectors.toList());

        RequestGridFieldSearchDto requestGridFieldSearchDto = new RequestGridFieldSearchDto();
        requestGridFieldSearchDto.setRequestGridFields(resultList);
        requestGridFieldSearchDto.setTotal(total);
        requestGridFieldSearchDto.setTotalPage(totalPage);
        requestGridFieldSearchDto.setPageSize(resultList.size());
        return requestGridFieldSearchDto;
    }

//    @Override
//    @CachePut(value = "requestGridFieldDtoList", key="#tenantCode")
//    public List<RequestGridFieldSearchableDto> refreshCache(String tenantCode) {
//        List<TenantRequestGridField> tenantRequestGridFieldList =
//                tenantRequestGridFieldRepository.findByTenant_CodeAndSearchableOrderBySequence(tenantCode,true);
//        return TenantRequestGridFieldMapper.INSTANCE.toRequestGridFieldSearchableDto(tenantRequestGridFieldList);
//    }
//    @Override
//    @Cacheable(value = "requestGridFieldDtoList", key="#tenantCode")
//    public List<RequestGridFieldSearchableDto> getCache(String tenantCode) {
//        List<TenantRequestGridField> tenantRequestGridFieldList =
//                tenantRequestGridFieldRepository.findByTenant_CodeAndSearchableOrderBySequence(tenantCode, true);
//        return TenantRequestGridFieldMapper.INSTANCE.toRequestGridFieldSearchableDto(tenantRequestGridFieldList);
//    }


    private Specification<TenantRequestGridField> getSpecificationByCondition(RequestGridFieldSearchRequest searchRequest) {
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
    public RequestGridFieldDto findRequestGridFieldByRecIdAndTenantIdAndPrivilegeCode(Integer requestGridFieldId, String privilegeCode) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        Optional<TenantRequestGridField> requestGridFields = tenantRequestGridFieldRepository.findTenantRequestGridFieldByRecIdAndPrivilegeCodeAndTenantId(requestGridFieldId, privilegeCode, tenant.getRecId());
        return requestGridFields.map(requestGridField -> TenantRequestGridFieldMapper.INSTANCE.toRequestGridFieldDto(requestGridField, timeZone)).orElse(null);
    }

    @Override
    public RequestGridFieldDto createRequestGridField(RequestGridFieldRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        try {
            Optional<RequestGridField> requestGridField = requestGridFieldRepository.findByCode(request.getCode());
            if(requestGridField.isPresent()) {
                Optional<TenantRequestGridField> requestGridFieldMaxSequence = tenantRequestGridFieldRepository.findFirstByTenantRecIdOrderBySequenceDesc(tenant.getRecId());
                if (requestGridFieldMaxSequence.isPresent()) {
                    if(request.getSequence() > requestGridFieldMaxSequence.get().getSequence()){
                        request.setSequence(requestGridFieldMaxSequence.get().getSequence() + 1);
                    } else {
                        tenantRequestGridFieldRepository.adjustOtherTenantRequestGridFieldSequences(request.getPrivilegeCode(), tenant.getRecId(),  requestGridFieldMaxSequence.get().getSequence() + 1, request.getSequence());
                    }
                } else {
                    request.setSequence(1);
                }
                TenantRequestGridField requestGridFieldCreated = tenantRequestGridFieldRepository.save(getSubmitRequestGridField(tenant,requestGridField.get(), request));
                return TenantRequestGridFieldMapper.INSTANCE.toRequestGridFieldDto(requestGridFieldCreated, timeZone);
            }
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public RequestGridFieldDto updateRequestGridField(RequestGridFieldRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        try {
            Optional<TenantRequestGridField> requestGridField = tenantRequestGridFieldRepository.findTenantRequestGridFieldByRecIdAndPrivilegeCodeAndTenantId(request.getRecId(), request.getPrivilegeCode(), tenant.getRecId());
            if (requestGridField.isPresent()) {
                tenantRequestGridFieldRepository.adjustOtherTenantRequestGridFieldSequences(request.getPrivilegeCode(), tenant.getRecId(),  requestGridField.get().getSequence(), request.getSequence());
                TenantRequestGridField requestGridFieldUpdated = tenantRequestGridFieldRepository.save(getUpdateRequestGridField(requestGridField.get(), request));
                return TenantRequestGridFieldMapper.INSTANCE.toRequestGridFieldDto(requestGridFieldUpdated, timeZone);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public RequestGridFieldDto updateRequestGridFieldSequence(RequestGridFieldSequenceRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Optional<TenantRequestGridField> requestGridField = tenantRequestGridFieldRepository.findTenantRequestGridFieldByRecIdAndPrivilegeCodeAndTenantId(request.getRecId(), request.getPrivilegeCode(), tenant.getRecId());
        if (requestGridField.isPresent()) {
            tenantRequestGridFieldRepository.adjustOtherTenantRequestGridFieldSequences(request.getPrivilegeCode(), tenant.getRecId(), requestGridField.get().getSequence(), request.getSequence());
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            TenantRequestGridField requestGridFieldUpdate = getUpdateRequestGridFieldSequence(requestGridField.get(), request);
            tenantRequestGridFieldRepository.save(requestGridFieldUpdate);
            return TenantRequestGridFieldMapper.INSTANCE.toRequestGridFieldDto(requestGridFieldUpdate, timeZone);
        }
        return null;
    }

    public TenantRequestGridField getSubmitRequestGridField(Tenant tenant, RequestGridField requestGridField,RequestGridFieldRequest request) {
        return TenantRequestGridField.builder()
                .tenant(tenant)
                .requestGridField(requestGridField)
                .privilegeCode(request.getPrivilegeCode())
                .code(request.getCode())
                .displayName(request.getDisplayName())
                .sequence(request.getSequence())
                .sorting(request.getSorting())
                .width(request.getWidth())
                .type(request.getType())
                .visible(request.isVisible())
                .createdBy(requestGridField.getCreatedBy())
                .createdDate(requestGridField.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }

    public TenantRequestGridField getUpdateRequestGridField(TenantRequestGridField requestGridField, RequestGridFieldRequest request) {
        return TenantRequestGridField.builder()
                .id(requestGridField.getId())
                .tenant(requestGridField.getTenant())
                .requestGridField(requestGridField.getRequestGridField())
                .privilegeCode(request.getPrivilegeCode() != null ? request.getPrivilegeCode() : requestGridField.getPrivilegeCode())
                .code(request.getCode() != null ? request.getCode() : requestGridField.getCode())
                .displayName(request.getDisplayName() != null ? request.getDisplayName() : requestGridField.getDisplayName())
                .sequence(request.getSequence() != null ? request.getSequence() : requestGridField.getSequence())
                .sorting(request.getSorting() != null ? request.getSorting() : requestGridField.getSorting())
                .width(request.getWidth() != null ? request.getWidth() : requestGridField.getWidth())
                .type(request.getType() != null ? request.getType() : requestGridField.getType())
                .visible(request.isVisible())
                .createdBy(requestGridField.getCreatedBy())
                .createdDate(requestGridField.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }

    public TenantRequestGridField getUpdateRequestGridFieldSequence(TenantRequestGridField requestGridField, RequestGridFieldSequenceRequest request) {
        return TenantRequestGridField.builder()
                .id(requestGridField.getId())
                .tenant(requestGridField.getTenant())
                .requestGridField(requestGridField.getRequestGridField())
                .privilegeCode(requestGridField.getPrivilegeCode())
                .code(requestGridField.getCode())
                .displayName(requestGridField.getDisplayName())
                .sequence(request.getSequence() != null ? request.getSequence() : requestGridField.getSequence())
                .sorting(requestGridField.getSorting())
                .width(requestGridField.getWidth())
                .type(requestGridField.getType())
                .visible(requestGridField.isVisible())
                .createdBy(requestGridField.getCreatedBy())
                .createdDate(requestGridField.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }


}
