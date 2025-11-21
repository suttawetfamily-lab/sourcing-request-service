package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItemGridField;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantRequestItemGridField;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantTemplate;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.TenantRequestItemGridFieldKey;
import com.pantavanij.sourcingreq.services.domain.mapper.TenantRequestItemGridFieldMapper;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestItemGridFieldRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantRequestItemGridFieldRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantOrganizationTemplateRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestItemGridFieldService;
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
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.RequestType.REQUEST_TYPE_CONDITION;
import static com.pantavanij.sourcingreq.services.enums.RequestType.REQUEST_TYPE_QUANTITY;
import static com.pantavanij.sourcingreq.services.util.Constant.REC_ID;
import static com.pantavanij.sourcingreq.services.util.Constant.TENANT;

@Service
@RequiredArgsConstructor
public class RequestItemGridFieldServiceImpl implements RequestItemGridFieldService {

    private final TenantRequestItemGridFieldRepository tenantRequestItemGridFieldRepository;
    private final RequestItemGridFieldRepository requestItemGridFieldRepository;
    private final TenantService tenantService;
    private final UaaService uaaService;
    private final TenantOrganizationTemplateRepository tenantOrganizationTemplateRepository;

    private Integer resolveTemplateId(Integer tenantId, Integer organizationId) {
        if (organizationId == null) {
            throw new RuntimeException("organizationId is required");
        }
        Integer templateId = tenantOrganizationTemplateRepository.findTemplateIdByTenantIdAndOrganizationId(tenantId, organizationId);
        if (templateId == null) {
            throw new RuntimeException("Template not found for tenantId=" + tenantId + ", organizationId=" + organizationId);
        }
        return templateId;
    }

    @Override
    public List<RequestItemGridFieldDto> getRequestItemGridField(String privilegeCode, String tenantCode, Integer typeId, Integer organizationId) {
        Tenant tenant = tenantService.findByCode(tenantCode);
        Integer templateId = resolveTemplateId(tenant.getRecId(), organizationId);

        List<TenantRequestItemGridField> tenantRequestItemGridFieldList =
                tenantRequestItemGridFieldRepository.findByPrivilegeCodeAndTenant_CodeAndTemplate_RecIdAndVisibleOrderBySequence(
                        privilegeCode, tenantCode, templateId, true);

        List<TenantRequestItemGridField> visibleList = tenantRequestItemGridFieldList.stream()
                .filter(i -> i.isVisible()
                        && (REQUEST_TYPE_QUANTITY.isQuantity(typeId, i.getCode()) ||
                        REQUEST_TYPE_CONDITION.isCondition(typeId, i.getCode())))
                .collect(Collectors.toList());

        List<RequestItemGridFieldDto> dtoList = TenantRequestItemGridFieldMapper.INSTANCE.toRequestItemGridFieldDto(visibleList);

        for (RequestItemGridFieldDto dto : dtoList) {
            List<RequestItemChildFieldDto> childFields = null;
            if (dto.getChildField() != null && !dto.getChildField().isEmpty()) {
                int seq = 1;
                childFields = new ArrayList<>();
                for (String childField : dto.getChildField().split(",")) {
                    String[] values = childField.split("\\|");
                    if (values.length == 2) {
                        RequestItemChildFieldDto child = new RequestItemChildFieldDto();
                        child.setName(values[0]);
                        child.setLabel(values[1]);
                        child.setSequence(seq++);
                        childFields.add(child);
                    }
                }
            }
            dto.setChildFields(childFields);
        }
        return dtoList;
    }

    @Override
    public RequestItemGridFieldSearchDto searchRequestItemGridFieldByCondition(RequestItemGridFieldSearchRequest searchRequest,
                                                                               Pageable pageable) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Integer templateId = resolveTemplateId(tenant.getRecId(), searchRequest.getOrganizationId());

        Page<TenantRequestItemGridField> tenantRequestItemGridFieldList =
                tenantRequestItemGridFieldRepository.findAll(
                        Specification.where(getSpecificationByCondition(searchRequest, tenant.getRecId(), templateId)), pageable);

        int totalPage = tenantRequestItemGridFieldList.getTotalPages();
        long total = tenantRequestItemGridFieldList.getTotalElements();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        List<RequestItemGridFieldDto> resultList = TenantRequestItemGridFieldMapper.INSTANCE
                .toRequestItemGridFieldDtoList(tenantRequestItemGridFieldList.getContent(), timeZone);

        resultList = resultList.stream()
                .peek(dto -> {
                    dto.setCreatedByName(UserDetailServiceUtil.getFullName(dto.getCreatedBy()));
                    dto.setUpdatedByName(dto.getUpdatedBy() != null ? UserDetailServiceUtil.getFullName(dto.getUpdatedBy()) : null);
                }).collect(Collectors.toList());

        RequestItemGridFieldSearchDto response = new RequestItemGridFieldSearchDto();
        response.setRequestItemGridFields(resultList);
        response.setTotal(total);
        response.setTotalPage(totalPage);
        response.setPageSize(resultList.size());
        return response;
    }

    private Specification<TenantRequestItemGridField> getSpecificationByCondition(RequestItemGridFieldSearchRequest searchRequest,
                                                                                  Integer tenantId,
                                                                                  Integer templateId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get(TENANT).get(REC_ID), tenantId));
            predicates.add(cb.equal(root.get("id").get("templateId"), templateId));

            if (searchRequest.getConditionSearchList() != null) {
                for (ConditionSearchRequest condition : searchRequest.getConditionSearchList()) {
                    if (StringUtils.isNotEmpty(condition.getSearchField()) &&
                            StringUtils.isNotEmpty(condition.getSearchValue())) {
                        predicates.add(cb.like(root.get(condition.getSearchField()), "%" + condition.getSearchValue().toLowerCase() + "%"));
                    }
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public RequestItemGridFieldDto findRequestItemGridFieldByRecIdAndTenantIdAndPrivilegeCode(Integer requestItemGridFieldId,
                                                                                              String privilegeCode,
                                                                                              Integer organizationId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Integer templateId = resolveTemplateId(tenant.getRecId(), organizationId);
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);

        Optional<TenantRequestItemGridField> opt =
                tenantRequestItemGridFieldRepository.findByRecIdAndPrivilegeCodeAndTenantIdAndTemplateId(
                        requestItemGridFieldId, privilegeCode, tenant.getRecId(), templateId);

        return opt.map(entity -> TenantRequestItemGridFieldMapper.INSTANCE.toRequestItemGridFieldDto(entity, timeZone)).orElse(null);
    }

    @Override
    @Transactional
    public RequestItemGridFieldDto createRequestItemGridField(RequestItemGridFieldRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Integer templateId = resolveTemplateId(tenant.getRecId(), request.getOrganizationId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);

        Optional<TenantRequestItemGridField> maxSeq =
                tenantRequestItemGridFieldRepository.findFirstByTenantRecIdAndId_TemplateIdOrderBySequenceDesc(tenant.getRecId(), templateId);

        if (maxSeq.isPresent()) {
            if (request.getSequence() > maxSeq.get().getSequence()) {
                request.setSequence(maxSeq.get().getSequence() + 1);
            } else {
                tenantRequestItemGridFieldRepository.adjustOtherTenantRequestItemGridFieldSequences(
                        request.getPrivilegeCode(), tenant.getRecId(), templateId,
                        maxSeq.get().getSequence() + 1, request.getSequence());
            }
        } else {
            request.setSequence(1);
        }

        TenantRequestItemGridField entity = getSubmitRequestItemGridField(request, tenant, templateId);
        TenantRequestItemGridField saved = tenantRequestItemGridFieldRepository.save(entity);
        return TenantRequestItemGridFieldMapper.INSTANCE.toRequestItemGridFieldDto(saved, timeZone);
    }

    @Override
    public RequestItemGridFieldDto updateRequestItemGridField(RequestItemGridFieldRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Integer templateId = resolveTemplateId(tenant.getRecId(), request.getOrganizationId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);

        Optional<TenantRequestItemGridField> opt =
                tenantRequestItemGridFieldRepository.findByRecIdAndPrivilegeCodeAndTenantIdAndTemplateId(
                        request.getRecId(), request.getPrivilegeCode(), tenant.getRecId(), templateId);

        if (opt.isPresent()) {
            tenantRequestItemGridFieldRepository.adjustOtherTenantRequestItemGridFieldSequences(
                    request.getPrivilegeCode(), tenant.getRecId(), templateId,
                    opt.get().getSequence(), request.getSequence());

            TenantRequestItemGridField updated = tenantRequestItemGridFieldRepository
                    .save(getUpdateRequestItemGridField(opt.get(), request));
            return TenantRequestItemGridFieldMapper.INSTANCE.toRequestItemGridFieldDto(updated, timeZone);
        }
        return null;
    }

    @Override
    public RequestItemGridFieldDto updateRequestItemGridFieldSequence(RequestItemGridFieldSequenceRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Integer templateId = resolveTemplateId(tenant.getRecId(), request.getOrganizationId());

        Optional<TenantRequestItemGridField> opt =
                tenantRequestItemGridFieldRepository.findByRecIdAndPrivilegeCodeAndTenantIdAndTemplateId(
                        request.getRecId(), request.getPrivilegeCode(), tenant.getRecId(), templateId);

        if (opt.isPresent()) {
            tenantRequestItemGridFieldRepository.adjustOtherTenantRequestItemGridFieldSequences(
                    request.getPrivilegeCode(), tenant.getRecId(), templateId,
                    opt.get().getSequence(), request.getSequence());

            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            TenantRequestItemGridField updated = getUpdateRequestItemGridFieldSequence(opt.get(), request);
            tenantRequestItemGridFieldRepository.save(updated);
            return TenantRequestItemGridFieldMapper.INSTANCE.toRequestItemGridFieldDto(updated, timeZone);
        }
        return null;
    }

    private TenantRequestItemGridField getSubmitRequestItemGridField(RequestItemGridFieldRequest request, Tenant tenant, Integer templateId) {
        Optional<RequestItemGridField> fieldOpt = requestItemGridFieldRepository.findByCode(request.getCode());
        if (fieldOpt.isPresent()) {
            TenantRequestItemGridFieldKey id = new TenantRequestItemGridFieldKey();
            id.setTenantId(tenant.getRecId());
            id.setPrivilegeCode(request.getPrivilegeCode());
            id.setRequestItemGridFieldId(fieldOpt.get().getRecId());
            id.setTemplateId(templateId);

            TenantTemplate template = new TenantTemplate();
            template.setRecId(templateId);

            return TenantRequestItemGridField.builder()
                    .id(id)
                    .tenant(tenant)
                    .requestItemGridField(fieldOpt.get())
                    .template(template)
                    .privilegeCode(request.getPrivilegeCode())
                    .code(request.getCode())
                    .displayName(request.getDisplayName())
                    .sequence(request.getSequence())
                    .width(request.getWidth())
                    .type(request.getType())
                    .visible(request.isVisible())
                    .tooltip(request.getTooltip())
                    .childField(request.getChildField())
                    .align(request.getAlign())
                    .createdBy(AppUtil.getUserName())
                    .createdDate(DateTimeUtil.getTimestampUTC())
                    .updatedBy(AppUtil.getUserName())
                    .updatedDate(DateTimeUtil.getTimestampUTC())
                    .build();
        }
        return null;
    }

    private TenantRequestItemGridField getUpdateRequestItemGridField(TenantRequestItemGridField entity, RequestItemGridFieldRequest request) {
        return TenantRequestItemGridField.builder()
                .id(entity.getId())
                .tenant(entity.getTenant())
                .requestItemGridField(entity.getRequestItemGridField())
                .template(entity.getTemplate())
                .privilegeCode(Optional.ofNullable(request.getPrivilegeCode()).orElse(entity.getPrivilegeCode()))
                .code(Optional.ofNullable(request.getCode()).orElse(entity.getCode()))
                .displayName(Optional.ofNullable(request.getDisplayName()).orElse(entity.getDisplayName()))
                .sequence(Optional.ofNullable(request.getSequence()).orElse(entity.getSequence()))
                .width(Optional.ofNullable(request.getWidth()).orElse(entity.getWidth()))
                .type(Optional.ofNullable(request.getType()).orElse(entity.getType()))
                .visible(request.isVisible())
                .tooltip(Optional.ofNullable(request.getTooltip()).orElse(entity.getTooltip()))
                .childField(Optional.ofNullable(request.getChildField()).orElse(entity.getChildField()))
                .align(Optional.ofNullable(request.getAlign()).orElse(entity.getAlign()))
                .createdBy(entity.getCreatedBy())
                .createdDate(entity.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }

    private TenantRequestItemGridField getUpdateRequestItemGridFieldSequence(TenantRequestItemGridField entity, RequestItemGridFieldSequenceRequest request) {
        return TenantRequestItemGridField.builder()
                .id(entity.getId())
                .tenant(entity.getTenant())
                .requestItemGridField(entity.getRequestItemGridField())
                .template(entity.getTemplate())
                .privilegeCode(entity.getPrivilegeCode())
                .code(entity.getCode())
                .displayName(entity.getDisplayName())
                .sequence(Optional.ofNullable(request.getSequence()).orElse(entity.getSequence()))
                .width(entity.getWidth())
                .type(entity.getType())
                .visible(entity.isVisible())
                .tooltip(entity.getTooltip())
                .childField(entity.getChildField())
                .align(entity.getAlign())
                .createdBy(entity.getCreatedBy())
                .createdDate(entity.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }
}
