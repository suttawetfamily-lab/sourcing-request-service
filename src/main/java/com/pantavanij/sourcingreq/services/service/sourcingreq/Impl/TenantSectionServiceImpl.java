package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.mapper.TenantSectionDetailMapper;
import com.pantavanij.sourcingreq.services.domain.mapper.TenantSectionMapper;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.AppException;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantConfigService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantSectionService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UaaService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.services.util.CommonUtils;
import com.pantavanij.sourcingreq.services.util.Constant;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.RequestType.REQUEST_TYPE_CONDITION;
import static com.pantavanij.sourcingreq.services.enums.RequestType.REQUEST_TYPE_QUANTITY;
import static com.pantavanij.sourcingreq.services.util.Constant.REC_ID;
import static com.pantavanij.sourcingreq.services.util.Constant.TENANT;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantSectionServiceImpl implements TenantSectionService {

    private final TenantSectionRepository tenantSectionRepository;
    private final TenantSectionDetailOptionRepository tenantSectionDetailOptionRepository;
    private final TenantConfigService tenantConfigService;
    private final TenantService tenantService;
    private final UaaService uaaService;
    private final TenantSectionDetailRepository tenantSectionDetailRepository;
    private final TenantOrganizationTemplateRepository tenantOrganizationTemplateRepository;
    private final TenantTemplateRepository tenantTemplateRepository;

    // ================== เมธอดที่รองรับ organizationId แล้ว (คง logic, ทวนความถูกต้อง) ==================

    @Override
    public List<TenantSectionDto> getRequestFields(String privilegeCode, Integer organizationId) {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);

        List<TenantSection> tenantSectionList;
        List<String> types = Collections.singletonList("REQ");

        if (organizationId != null) {
            Integer templateId = tenantOrganizationTemplateRepository
                    .findTemplateIdByTenantCodeAndOrganizationId(tenantCode, organizationId);

            if (templateId == null) {
                throw new BusinessException(
                        String.format("Template not found for tenantCode=%s, organizationId=%s", tenantCode, organizationId)
                );
            }

            tenantSectionList = tenantSectionRepository
                    .getTenantSectionByTenantIdAndTemplateIdAndType(tenant.getRecId(), templateId, types);
        } else {
            tenantSectionList = tenantSectionRepository
                    .getTenantSectionByTenantIdAndType(tenant.getRecId(), types);
        }

        for (TenantSection tenantSection : tenantSectionList) {
            List<TenantSectionDetail> list = tenantSection.getFields().stream()
                    .sorted(Comparator.comparing(TenantSectionDetail::getSequence))
                    .collect(Collectors.toList());
            tenantSection.setFields(list);
        }
        return this.populateFields(privilegeCode, tenantSectionList, "REQ", 0);
    }

    @Override
    public List<TenantSectionDto> getRequestItemFields(Integer requestTypeId, Integer organizationId) {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);
        List<String> types = Collections.singletonList("REQI");

        List<TenantSection> tenantSectionList = resolveSectionsByOrg(tenant, tenantCode, organizationId, types);

        for (TenantSection tenantSection : tenantSectionList) {
            List<TenantSectionDetail> list = tenantSection.getFields().stream()
                    .filter(s -> (requestTypeId == REQUEST_TYPE_QUANTITY.id() && !s.getFieldName().equalsIgnoreCase("conditions")) ||
                            (requestTypeId == REQUEST_TYPE_CONDITION.id() && !s.getFieldName().equalsIgnoreCase("quantity")))
                    .sorted(Comparator.comparing(TenantSectionDetail::getSequence))
                    .collect(Collectors.toList());
            tenantSection.setFields(list);
        }
        return this.populateFields("", tenantSectionList, "REQI", requestTypeId);
    }

    @Override
    public List<TenantSectionDto> getRequestItemHeaderFields(Integer requestTypeId, Integer organizationId) {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);
        List<String> types = Collections.singletonList("REQI");

        List<TenantSection> tenantSectionList = resolveSectionsByOrg(tenant, tenantCode, organizationId, types);

        for (TenantSection tenantSection : tenantSectionList) {
            List<TenantSectionDetail> list = tenantSection.getFields().stream()
                    .filter(s ->
                            (
                                    (requestTypeId == REQUEST_TYPE_QUANTITY.id() && !s.getFieldName().equalsIgnoreCase("conditions")) ||
                                            (requestTypeId == REQUEST_TYPE_CONDITION.id() && !s.getFieldName().equalsIgnoreCase("quantity"))
                            )
                                    && s.isHeader()
                    )
                    .sorted(Comparator.comparing(TenantSectionDetail::getHeaderSequence))
                    .collect(Collectors.toList());
            tenantSection.setFields(list);
        }
        return this.populateFields("", tenantSectionList, "REQI_HEADER", requestTypeId);
    }

    // ================== เดิม: getByRecId / search / option (เพิ่ม filter org ใน search) ==================

    @Override
    public TenantSectionDto getByRecId(Integer tenantSectionId) {
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        List<String> types = new ArrayList<>(List.of("REQ"));

        Optional<TenantSection> tenantSectionOpt = tenantSectionRepository.findByRecId(tenantSectionId);
        tenantSectionOpt.ifPresent(tenantSection -> {
            if (!types.contains(tenantSection.getType())) {
                types.add(tenantSection.getType());
            }
        });

        List<TenantSection> tenantSectionList = tenantSectionRepository.findByTenantAndTypeIn(tenant, types);
        List<Long> tenantSectionIds = tenantSectionList.stream()
                .map(TenantSection::getId)
                .collect(Collectors.toList());

        List<TenantSectionDetail> tenantSectionDetailList =
                tenantSectionDetailRepository.findByTenantAndTenantSectionIdIn(tenant, tenantSectionIds);

        return tenantSectionOpt
                .map(tenantSection -> TenantSectionMapper.INSTANCE.toTenantSectionDto(tenantSection, tenantSectionDetailList, timeZone))
                .orElse(null);
    }

    @Override
    public TenantSectionSearchDto searchTenantSectionByCondition(TenantSectionSearchRequest request, Pageable pageable) {
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        Page<TenantSection> tenantSectionPage = tenantSectionRepository.findAll(
                Specification.where(getTenantSectionSearchSpecification(request)), pageable);
        int totalPage = tenantSectionPage.getTotalPages();
        long total = tenantSectionPage.getTotalElements();
        List<TenantSectionDto> tenantSectionList =
                TenantSectionMapper.INSTANCE.toTenantSectionDtoList(tenantSectionPage.getContent(), timeZone);
        tenantSectionList = tenantSectionList.stream()
                .peek(tenantSectionDto -> {
                    tenantSectionDto.setFields(null);
                    tenantSectionDto.setTenantSectionDetailList(null);
                })
                .collect(Collectors.toList());

        return TenantSectionSearchDto.builder()
                .tenantSectionList(tenantSectionList)
                .total(total)
                .totalPage(totalPage)
                .pageSize(pageable.getPageSize())
                .build();
    }

    @Override
    public TenantSectionDto updateTenantSection(TenantSectionRequest request, Integer organizationId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);

        try {
            TenantTemplate template = resolveTemplate(tenant.getCode(), organizationId);

            Optional<TenantSection> tenantSectionExisting = tenantSectionRepository.findByRecId(request.getRecId());
            if (tenantSectionExisting.isPresent()) {
                tenantSectionRepository.reOrderOtherTenantSectionSequence(
                        tenant.getRecId(),
                        tenantSectionExisting.get().getSequence(),
                        request.getSequence()
                );

                TenantSection tenantSectionUpdate =
                        tenantSectionRepository.save(getTenantSectionUpdate(tenantSectionExisting.get(), request, template));

                tenantSectionUpdate.setFields(null);
                tenantSectionUpdate.setTenantSectionDetailList(null);

                return TenantSectionMapper.INSTANCE.toTenantSectionDto(tenantSectionUpdate, new ArrayList<>(), timeZone);
            }
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public TenantSectionDto createNewTenantSection(TenantSectionRequest request, Integer organizationId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);

        try {
            TenantTemplate template = resolveTemplate(tenant.getCode(), organizationId);

            Optional<TenantSection> tenantSectionOptional =
                    tenantSectionRepository.findFirstByTenantRecIdOrderBySequenceDesc(tenant.getRecId());
            if (tenantSectionOptional.isPresent() && request.getSequence() == 0) {
                request.setSequence(tenantSectionOptional.get().getSequence() + 1);
            } else if (tenantSectionOptional.isEmpty() && request.getSequence() == 0) {
                request.setSequence(1);
            } else {
                tenantSectionOptional.ifPresent(tenantSection ->
                        tenantSectionRepository.reOrderOtherReportLineSequence(
                                tenant.getRecId(),
                                tenantSection.getSequence() + 1,
                                request.getSequence()));
            }

            TenantSection tenantSection = tenantSectionRepository.save(getTenantSectionCreate(request, tenant, template));
            return TenantSectionMapper.INSTANCE.toTenantSectionDto(tenantSection, new ArrayList<>(), timeZone);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Boolean deleteTenantSection(Integer tenantSectionId, Integer organizationId) {
        try {
            Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());

            if (organizationId != null) {
                Integer templateId = tenantOrganizationTemplateRepository
                        .findTemplateIdByTenantCodeAndOrganizationId(tenant.getCode(), organizationId);

                if (templateId == null) {
                    throw new BusinessException(
                            String.format("Template not found for tenantCode=%s, organizationId=%s", tenant.getCode(), organizationId)
                    );
                }

                Optional<TenantSection> sectionOpt = tenantSectionRepository.findByRecId(tenantSectionId);
                if (sectionOpt.isEmpty() || !Objects.equals(sectionOpt.get().getTemplate().getRecId(), templateId)) {
                    throw new BusinessException(
                            String.format("TenantSection %s does not belong to templateId=%s", tenantSectionId, templateId)
                    );
                }
            }

            tenantSectionRepository.deleteById(tenantSectionId.longValue());
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private TenantSection getTenantSectionCreate(TenantSectionRequest request, Tenant tenant, TenantTemplate template) {
        return TenantSection.builder()
                .tenant(tenant)
                .template(template)
                .privilegeCode("sqn")
                .type(request.getType())
                .sectionName(request.getSectionName())
                .sectionTitle(request.getSectionTitle())
                .step(request.getStep())
                .sequence(request.getSequence())
                .visible(request.isVisible())
                .fields(null)
                .tenantSectionDetailList(null)
                .createdBy(AppUtil.getUserName())
                .createdDate(DateTimeUtil.getTimestampUTC())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }

    private TenantSection getTenantSectionUpdate(TenantSection tenantSection, TenantSectionRequest request, TenantTemplate template) {
        return TenantSection.builder()
                .id(tenantSection.getId())
                .tenant(tenantSection.getTenant())
                .template(template != null ? template : tenantSection.getTemplate())
                .privilegeCode(tenantSection.getPrivilegeCode())
                .type(request.getType())
                .sectionName(request.getSectionName())
                .sectionTitle(request.getSectionTitle())
                .step(request.getStep())
                .sequence(request.getSequence())
                .visible(request.isVisible())
                .fields(tenantSection.getFields())
                .tenantSectionDetailList(tenantSection.getTenantSectionDetailList())
                .createdBy(tenantSection.getCreatedBy())
                .createdDate(tenantSection.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }

    private Specification<TenantSection> getTenantSectionSearchSpecification(TenantSectionSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
            if (tenant != null) {
                predicates.add(criteriaBuilder.equal(root.get(TENANT).get(REC_ID), tenant.getRecId()));
            }

            // ★★ เพิ่ม filter ด้วย organizationId → templateId ถ้ามี ★★
            if (request.getOrganizationId() != null) {
                Integer templateId = tenantOrganizationTemplateRepository
                        .findTemplateIdByTenantCodeAndOrganizationId(tenant.getCode(), request.getOrganizationId());
                if (templateId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("template").get("recId"), templateId));
                } else {
                    // ถ้าไม่เจอ templateId ก็ให้ return empty ทันที
                    predicates.add(criteriaBuilder.equal(root.get("template").get("recId"), -1));
                }
            }

            List<ConditionSearchRequest> conditionSearchRequests = request.getConditionSearchList();
            if (conditionSearchRequests != null && !conditionSearchRequests.isEmpty()) {
                List<Predicate> orPredicates = new ArrayList<>();
                for (ConditionSearchRequest conditionSearchRequest : conditionSearchRequests) {
                    String searchField = conditionSearchRequest.getSearchField();
                    String searchValue = conditionSearchRequest.getSearchValue();

                    Predicate predicate = null;
                    if (org.apache.commons.lang.StringUtils.isNotEmpty(searchField) &&
                            org.apache.commons.lang.StringUtils.isNotEmpty(searchValue)) {
                        if (Constant.TYPE.equalsIgnoreCase(searchField) ||
                                Constant.SECTION_NAME.equalsIgnoreCase(searchField) ||
                                Constant.SECTION_TITLE.equalsIgnoreCase(searchField) ||
                                Constant.STEP.equalsIgnoreCase(searchField) ||
                                Constant.VISIBLE.equalsIgnoreCase(searchField) ||
                                Constant.CREATED_BY.equalsIgnoreCase(searchField)) {
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

    // ================== เมธอด “ดึง Section” อื่น ๆ ที่ยังไม่ได้รองรับ → ปรับให้ครบ ==================

    @Override
    public List<TenantSectionDto> getRequestItemFieldsForTemplateExcel(Integer requestTypeId, Integer organizationId) {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);
        List<String> types = Collections.singletonList("REQI");

        List<TenantSection> tenantSectionList = resolveSectionsByOrg(tenant, tenantCode, organizationId, types);

        for (TenantSection tenantSection : tenantSectionList) {
            List<TenantSectionDetail> list = tenantSection.getFields().stream()
                    .filter(s -> (s.isExcelItemTemplate() && (
                            (requestTypeId == REQUEST_TYPE_QUANTITY.id() && !s.getFieldName().equalsIgnoreCase("conditions")) ||
                                    (requestTypeId == REQUEST_TYPE_CONDITION.id() && !s.getFieldName().equalsIgnoreCase("quantity")))))
                    .sorted(Comparator.comparing(TenantSectionDetail::getSequence))
                    .collect(Collectors.toList());
            tenantSection.setFields(list);
        }
        return this.populateFields("", tenantSectionList, "REQI", requestTypeId);
    }

    @Override
    public List<TenantSectionDto> getExistingPriceItemFields(Integer requestTypeId, Integer organizationId) {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);
        List<String> types = Collections.singletonList("REQIEX");

        List<TenantSection> tenantSectionList = resolveSectionsByOrg(tenant, tenantCode, organizationId, types);

        for (TenantSection tenantSection : tenantSectionList) {
            List<TenantSectionDetail> list = tenantSection.getFields().stream()
                    .filter(s ->
                            (requestTypeId == REQUEST_TYPE_QUANTITY.id()
                                    && !s.getFieldName().equalsIgnoreCase("conditions")
                                    && !s.getFieldName().equalsIgnoreCase("awardedAmount"))
                                    ||
                                    (requestTypeId == REQUEST_TYPE_CONDITION.id()
                                            && !s.getFieldName().equalsIgnoreCase("quantity")
                                            && !s.getFieldName().equalsIgnoreCase("awardedQuantity"))
                    )
                    .sorted(Comparator.comparing(TenantSectionDetail::getSequence))
                    .collect(Collectors.toList());
            tenantSection.setFields(list);
        }
        return this.populateFields("", tenantSectionList, "REQIEX", requestTypeId);
    }

    @Override
    public List<TenantSectionDto> getExistingPriceItemHeaderFields(Integer requestTypeId, Integer organizationId) {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);
        List<String> types = Collections.singletonList("REQIEX");

        List<TenantSection> tenantSectionList = resolveSectionsByOrg(tenant, tenantCode, organizationId, types);

        for (TenantSection tenantSection : tenantSectionList) {
            List<TenantSectionDetail> list = tenantSection.getFields().stream()
                    .filter(s ->
                            (
                                    (requestTypeId == REQUEST_TYPE_QUANTITY.id()
                                            && !s.getFieldName().equalsIgnoreCase("conditions")
                                            && !s.getFieldName().equalsIgnoreCase("awardedAmount"))
                                            ||
                                            (requestTypeId == REQUEST_TYPE_CONDITION.id()
                                                    && !s.getFieldName().equalsIgnoreCase("quantity")
                                                    && !s.getFieldName().equalsIgnoreCase("awardedQuantity"))
                            )
                                    && s.isHeader()
                    )
                    .sorted(Comparator.comparing(TenantSectionDetail::getHeaderSequence))
                    .collect(Collectors.toList());
            tenantSection.setFields(list);
        }
        return this.populateFields("", tenantSectionList, "REQIEX_HEADER", requestTypeId);
    }

    @Override
    public List<TenantSectionDto> getExceptionalSourcingItemFields(Integer requestTypeId, Integer organizationId) {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);
        List<String> types = Collections.singletonList("REQIEC");

        List<TenantSection> tenantSectionList = resolveSectionsByOrg(tenant, tenantCode, organizationId, types);

        for (TenantSection tenantSection : tenantSectionList) {
            List<TenantSectionDetail> list = tenantSection.getFields().stream()
                    .filter(s ->
                            (requestTypeId == REQUEST_TYPE_QUANTITY.id()
                                    && !s.getFieldName().equalsIgnoreCase("conditions")
                                    && !s.getFieldName().equalsIgnoreCase("awardedAmount"))
                                    ||
                                    (requestTypeId == REQUEST_TYPE_CONDITION.id()
                                            && !s.getFieldName().equalsIgnoreCase("quantity")
                                            && !s.getFieldName().equalsIgnoreCase("awardedQuantity"))
                    )
                    .sorted(Comparator.comparing(TenantSectionDetail::getSequence))
                    .collect(Collectors.toList());
            tenantSection.setFields(list);
        }
        return this.populateFields("", tenantSectionList, "REQIEC", requestTypeId);
    }

    @Override
    public List<TenantSectionDto> getExceptionalSourcingItemHeaderFields(Integer requestTypeId, Integer organizationId) {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);
        List<String> types = Collections.singletonList("REQIEC");

        List<TenantSection> tenantSectionList = resolveSectionsByOrg(tenant, tenantCode, organizationId, types);

        for (TenantSection tenantSection : tenantSectionList) {
            List<TenantSectionDetail> list = tenantSection.getFields().stream()
                    .filter(s ->
                            (
                                    (requestTypeId == REQUEST_TYPE_QUANTITY.id()
                                            && !s.getFieldName().equalsIgnoreCase("conditions")
                                            && !s.getFieldName().equalsIgnoreCase("awardedAmount"))
                                            ||
                                            (requestTypeId == REQUEST_TYPE_CONDITION.id()
                                                    && !s.getFieldName().equalsIgnoreCase("quantity")
                                                    && !s.getFieldName().equalsIgnoreCase("awardedQuantity"))
                            )
                                    && s.isHeader()
                    )
                    .sorted(Comparator.comparing(TenantSectionDetail::getHeaderSequence))
                    .collect(Collectors.toList());
            tenantSection.setFields(list);
        }
        return this.populateFields("", tenantSectionList, "REQIEC_HEADER", requestTypeId);
    }

    @Override
    public List<TenantSectionDto> getRequestReportFields(Integer organizationId) {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);
        boolean isReportFiltering = tenantConfigService.getFilterReportConfiguration(tenant.getRecId());
        List<String> types = Collections.singletonList("RPT");

        List<TenantSection> tenantSectionList = resolveSectionsByOrg(tenant, tenantCode, organizationId, types);

        for (TenantSection tenantSection : tenantSectionList) {
            List<TenantSectionDetail> list = tenantSection.getFields().stream()
                    .filter(s -> (isReportFiltering && s.isReportFilter()) || (!isReportFiltering && s.isForceVisible()))
                    .sorted(Comparator.comparing(TenantSectionDetail::getSequence))
                    .collect(Collectors.toList());
            tenantSection.setFields(list);
        }
        return this.populateFields("", tenantSectionList, "RPT", 0);
    }

    @Override
    public List<TenantSectionDetailDto> getRequestPreviewFields(String mode, String pathUrl, Integer organizationId) {
        log.info("Get Request preview fields for mode : {}", mode);
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);

        List<TenantSectionDetail> previewFieldlist = new ArrayList<>();
        List<String> types = Arrays.asList("REQ", "RPT");

        // ดึงตาม org ถ้ามี
        List<TenantSection> tenantSectionList;
        if (organizationId != null) {
            Integer templateId = tenantOrganizationTemplateRepository
                    .findTemplateIdByTenantCodeAndOrganizationId(tenantCode, organizationId);
            if (templateId == null) {
                throw new BusinessException(
                        String.format("Template not found for tenantCode=%s, organizationId=%s", tenantCode, organizationId)
                );
            }
            tenantSectionList = tenantSectionRepository.getTenantSectionByTenantIdAndTemplateIdAndType(
                    tenant.getRecId(), templateId, types);
        } else {
            tenantSectionList = tenantSectionRepository.getTenantSectionByTenantIdAndType(tenant.getRecId(), types);
        }

        for (TenantSection tenantSection : tenantSectionList) {
            List<TenantSectionDetail> list;
            if ("view".equalsIgnoreCase(mode)) {
                if ("sourcing-request".equalsIgnoreCase(pathUrl)) {
                    list = tenantSection.getFields().stream()
                            .filter(TenantSectionDetail::isModeViewSQN)
                            .sorted(Comparator.comparing(TenantSectionDetail::getModeViewSequenceSQN))
                            .collect(Collectors.toList());
                } else if ("review-request".equalsIgnoreCase(pathUrl)) {
                    list = tenantSection.getFields().stream()
                            .filter(TenantSectionDetail::isModeViewSQV)
                            .sorted(Comparator.comparing(TenantSectionDetail::getModeViewSequenceSQV))
                            .collect(Collectors.toList());
                } else if ("manage-request".equalsIgnoreCase(pathUrl)) {
                    list = tenantSection.getFields().stream()
                            .filter(TenantSectionDetail::isModeViewSQP)
                            .sorted(Comparator.comparing(TenantSectionDetail::getModeViewSequenceSQP))
                            .collect(Collectors.toList());
                } else if ("approve-request".equalsIgnoreCase(pathUrl)) {
                    list = tenantSection.getFields().stream()
                            .filter(TenantSectionDetail::isModeViewSQA)
                            .sorted(Comparator.comparing(TenantSectionDetail::getModeViewSequenceSQA))
                            .collect(Collectors.toList());
                } else {
                    list = tenantSection.getFields().stream()
                            .filter(TenantSectionDetail::isPreview)
                            .sorted(Comparator.comparing(TenantSectionDetail::getPreviewSequence))
                            .collect(Collectors.toList());
                }
            } else {
                list = tenantSection.getFields().stream()
                        .filter(TenantSectionDetail::isPreview)
                        .sorted(Comparator.comparing(TenantSectionDetail::getPreviewSequence))
                        .collect(Collectors.toList());
            }
            previewFieldlist.addAll(list);
        }

        if ("view".equalsIgnoreCase(mode)) {
            if ("sourcing-request".equalsIgnoreCase(pathUrl)) {
                previewFieldlist.sort(Comparator.comparing(TenantSectionDetail::getModeViewSequenceSQN));
            } else if ("review-request".equalsIgnoreCase(pathUrl)) {
                previewFieldlist.sort(Comparator.comparing(TenantSectionDetail::getModeViewSequenceSQV));
            } else if ("manage-request".equalsIgnoreCase(pathUrl)) {
                previewFieldlist.sort(Comparator.comparing(TenantSectionDetail::getModeViewSequenceSQP));
            } else if("approve-request".equalsIgnoreCase(pathUrl)) {
                previewFieldlist.sort(Comparator.comparing(TenantSectionDetail::getModeViewSequenceSQA));
            } else {
                previewFieldlist.sort(Comparator.comparing(TenantSectionDetail::getPreviewSequence));
            }
        }
        return this.populatePreviewFields(previewFieldlist);
    }

    @Override
    public List<TenantSectionDto> getRequestFullPreviewFields(Integer organizationId) {
        // 1) หา tenantCode จาก context
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);

        // 2) ใช้ resolveTemplate เพื่อรองรับ orgId (optional)
        TenantTemplate template = resolveTemplate(tenantCode, organizationId);

        List<String> types = Arrays.asList("REQ");
        List<TenantSection> tenantSectionList;

        if (template != null) {
            tenantSectionList = tenantSectionRepository.getTenantSectionByTenantIdAndTemplateIdAndType(
                    tenant.getRecId(), template.getRecId(), types);
        } else {
            tenantSectionList = tenantSectionRepository.getTenantSectionByTenantIdAndType(
                    tenant.getRecId(), types);
        }

        // 3) filter เฉพาะ field ที่ fullPreview = true และ sort ตาม sequence
        for (TenantSection tenantSection : tenantSectionList) {
            List<TenantSectionDetail> list = tenantSection.getFields().stream()
                    .filter(TenantSectionDetail::isFullPreview)
                    .sorted(Comparator.comparing(TenantSectionDetail::getSequence))
                    .collect(Collectors.toList());
            tenantSection.setFields(list);
        }

        // 4) map เป็น DTO
        return this.populateFullPreviewFields(tenantSectionList, "REQ");
    }


    @Override
    public List<TenantSection> getRequestItemReportFields(Integer organizationId) {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);
        List<String> types = Arrays.asList("REQ", "REQI");

        List<TenantSection> tenantSectionList = resolveSectionsByOrg(tenant, tenantCode, organizationId, types);

        for (TenantSection tenantSection : tenantSectionList) {
            List<TenantSectionDetail> list = tenantSection.getFields().stream()
                    .sorted(Comparator.comparing(TenantSectionDetail::getSequence))
                    .collect(Collectors.toList());
            tenantSection.setFields(list);
        }
        return tenantSectionList;
    }


    @Override
    public List<OptionDto> getOptionList(String optionName) {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);
        List<OptionDto> optionList = new ArrayList<>();
        List<TenantSectionDetailOption> detailOptionList =
                tenantSectionDetailOptionRepository.findByOptionNameAndTenant(optionName, tenant);
        detailOptionList.forEach(opt -> {
            OptionDto dto = new OptionDto();
            dto.setValue(opt.getOptionValue());
            dto.setLabel(opt.getOptionLabel());
            dto.setName(opt.getOptionName() + "-" + opt.getOptionValue());
            dto.setIsDefault(opt.isDefault());
            optionList.add(dto);
        });
        return optionList;
    }

    // ================== Private helpers ==================

    private List<TenantSection> resolveSectionsByOrg(Tenant tenant, String tenantCode, Integer organizationId, List<String> types) {
        if (organizationId != null) {
            Integer templateId = tenantOrganizationTemplateRepository
                    .findTemplateIdByTenantCodeAndOrganizationId(tenantCode, organizationId);
            if (templateId == null) {
                throw new BusinessException(
                        String.format("Template not found for tenantCode=%s, organizationId=%s", tenantCode, organizationId)
                );
            }
            return tenantSectionRepository.getTenantSectionByTenantIdAndTemplateIdAndType(
                    tenant.getRecId(), templateId, types);
        }
        return tenantSectionRepository.getTenantSectionByTenantIdAndType(tenant.getRecId(), types);
    }

    private TenantTemplate resolveTemplate(String tenantCode, Integer organizationId) {
        if (organizationId == null) {
            return null;
        }
        Integer templateId = tenantOrganizationTemplateRepository
                .findTemplateIdByTenantCodeAndOrganizationId(tenantCode, organizationId);

        if (templateId == null) {
            throw new BusinessException(
                    String.format("Template not found for tenantCode=%s, organizationId=%s", tenantCode, organizationId)
            );
        }
        return tenantTemplateRepository.findById(templateId)
                .orElseThrow(() -> new BusinessException("TenantTemplate not found for templateId=" + templateId));
    }

    // ================== populate methods (คงเดิม ยกเว้นไม่แตะ behavior) ==================

    private List<TenantSectionDto> populateFields(String privilegeCode, List<TenantSection> tenantSectionList, String type, Integer requestTypeId) {
        boolean isInvalidPrePostSpan = tenantSectionList.stream().anyMatch(t -> t.getFields().stream().anyMatch(f -> f.getPreSpan() < 0 || f.getPreSpan() > 24 || f.getPostSpan() < 0 || f.getPostSpan() > 24));
        if (isInvalidPrePostSpan)
            throw new AppException(ApiMessage.E7070, String.format(ApiMessage.E7070.description()));

        boolean isInvalidSpan = tenantSectionList.stream().anyMatch(t -> t.getFields().stream().anyMatch(f -> f.getSpan() < 1 || f.getSpan() > 24));
        if (isInvalidSpan)
            throw new AppException(ApiMessage.E7071, String.format(ApiMessage.E7071.description()));

        List<TenantSectionDto> tenantSectionDtoList = new ArrayList<>();
        for (TenantSection tenantSection : tenantSectionList) {
            List<TenantSectionDetail> list = new ArrayList<>();
            for (TenantSectionDetail tsd : tenantSection.getFields()) {
                if (tsd.isVisible()) {
                    list.add(tsd);
                }
            }
            tenantSection.setFields(list);
            TenantSectionDto TenantSectionDto = TenantSectionMapper.INSTANCE.toTenantSectionDto(tenantSection);

            for (TenantSectionDetailDto tenantSectionDetail : TenantSectionDto.getFields()) {
                PreSelectDto preSelect = null;
                List<DependencyObjectDto> depends = null;
                List<WatchFieldNameDto> watches = null;
                List<DisableObjectDto> disable = null;
                List<String> validations = new ArrayList<>();
                Map params = new HashMap<>();
                Map styles = new HashMap<>();
                tenantSectionDetail.getTenantSectionDetailValidatorList().sort(Comparator.comparing(TenantSectionDetailValidatorDto::getSequence));
                for (TenantSectionDetailValidatorDto ts : tenantSectionDetail.getTenantSectionDetailValidatorList()) {
                    String name = ts.getName();
                    validations.add(name);

                    if (ts.getValue() != null && !ts.getValue().isEmpty()) {
                        if (params.put(ts.getName(), ts.getValue()) != null) {
                            throw new IllegalStateException("Duplicate key");
                        }
                    }
                }
                tenantSectionDetail.setValidations(validations);
                tenantSectionDetail.setParams(params);

                if (tenantSectionDetail.getTenantSectionDetailDataSourceList() != null &&
                        tenantSectionDetail.getTenantSectionDetailDataSourceList().size() > 0) {
                    Map searchTerm = new HashMap<>();
                    List<SearchFormMappingDto> searchFormMappingList = new ArrayList<>();
                    QueryParamDto queryParamDto = new QueryParamDto();
                    ResponseMappingDto responseMappingDto = new ResponseMappingDto();

                    DataSourceDto dataSourceDto = tenantSectionDetail.getTenantSectionDetailDataSourceList().get(0).getDataSource();

                    if (dataSourceDto.getPath().contains("name_param")) {
                        String newPath = dataSourceDto.getPath().replaceAll("name_param", tenantSectionDetail.getFieldName());
                        dataSourceDto.setPath(newPath);
                    }

                    if (dataSourceDto.getQueryParameters() != null && !dataSourceDto.getQueryParameters().isEmpty()) {
                        searchTerm.put("name", dataSourceDto.getQueryParameters());
                    }
                    queryParamDto.setSearchTerm(searchTerm);

                    if ((dataSourceDto.getFormName() != null && !dataSourceDto.getFormName().isEmpty()) &&
                            (dataSourceDto.getQueryName() != null && !dataSourceDto.getQueryName().isEmpty()) &&
                            (dataSourceDto.getFormValue() != null && !dataSourceDto.getFormValue().isEmpty())) {

                        SearchFormMappingDto searchFormMappingDto = new SearchFormMappingDto();

                        searchFormMappingDto.setFormName(dataSourceDto.getFormName());
                        searchFormMappingDto.setQueryName(dataSourceDto.getQueryName());
                        searchFormMappingDto.setFormValue(dataSourceDto.getFormValue());
                        searchFormMappingList.add(searchFormMappingDto);
                    }
                    queryParamDto.setSearchFormMapping(searchFormMappingList);

                    if ((dataSourceDto.getObjectKey() != null && !dataSourceDto.getObjectKey().isEmpty()) &&
                            (dataSourceDto.getValueKey() != null && !dataSourceDto.getValueKey().isEmpty()) &&
                            (dataSourceDto.getLabelKey() != null && !dataSourceDto.getLabelKey().isEmpty()) &&
                            (dataSourceDto.getNameKey() != null && !dataSourceDto.getNameKey().isEmpty())) {

                        responseMappingDto.setObjectKey(dataSourceDto.getObjectKey());
                        responseMappingDto.setValueKey(dataSourceDto.getValueKey());
                        responseMappingDto.setLabelKey(dataSourceDto.getLabelKey().split(","));
                        responseMappingDto.setNameKey(dataSourceDto.getNameKey());
                    }

                    dataSourceDto.setQueryParams(queryParamDto);
                    dataSourceDto.setResponseMapping(responseMappingDto);
                    tenantSectionDetail.setDataSource(dataSourceDto);
                }
                if (tenantSectionDetail.getPreSelectName() != null && !tenantSectionDetail.getPreSelectName().isEmpty()) {
                    if (preSelect == null) preSelect = new PreSelectDto();
                    preSelect.setName(tenantSectionDetail.getPreSelectName());
                    preSelect.setDisabled(tenantSectionDetail.getPreSelectValues() != null ?
                            tenantSectionDetail.getPreSelectValues().equals("disable") : false);
                }
                tenantSectionDetail.setPreSelect(preSelect);

                if (tenantSectionDetail.getTenantSectionDetailDependencyList() != null &&
                        tenantSectionDetail.getTenantSectionDetailDependencyList().size() > 0) {
                    for (TenantSectionDetailDependencyDto ts : tenantSectionDetail.getTenantSectionDetailDependencyList()) {
                        if (depends == null) depends = new ArrayList<>();

                        DependencyObjectDto dependObj = new DependencyObjectDto();
                        dependObj.setName(ts.getName());

                        String[] dependObjects = ts.getValue() != null ? ts.getValue().split(",") : null;
                        dependObj.setValues(dependObjects);
                        dependObj.setGroupName(ts.getGroupName());
                        dependObj.setAction(ts.getAction());
                        depends.add(dependObj);
                    }
                    tenantSectionDetail.setDepends(depends);
                }

                if (tenantSectionDetail.getTenantSectionDetailWatchList() != null &&
                        tenantSectionDetail.getTenantSectionDetailWatchList().size() > 0) {
                    if (watches == null) watches = new ArrayList<>();

                    List<String> watchGroupNameList = tenantSectionDetail.getTenantSectionDetailWatchList().stream()
                            .map(TenantSectionDetailWatchDto::getGroupName).distinct().collect(Collectors.toList());
                    for (String watchGroupName : watchGroupNameList) {

                        List<WatchFieldDto> fields = new ArrayList<>();
                        List<WatchDataFieldDto> defaultDataFromProps = null;
                        List<WatchDataFieldDto> updateDataFromProps = null;

                        for (TenantSectionDetailWatchDto ts : tenantSectionDetail.getTenantSectionDetailWatchList().stream()
                                .filter(l -> l.getGroupName().equalsIgnoreCase(watchGroupName)).collect(Collectors.toList())) {

                            WatchFieldDto fieldDto = new WatchFieldDto();
                            fieldDto.setFieldName(ts.getFieldName());
                            fieldDto.setValues(ts.getValues() != null ? ts.getValues().split(",") : null);
                            fields.add(fieldDto);

                            if (ts.getOriginalFieldName() != null && !ts.getOriginalFieldName().isEmpty()) {
                                if (defaultDataFromProps == null) defaultDataFromProps = new ArrayList<>();
                                WatchDataFieldDto defaultDataFromProp = new WatchDataFieldDto();
                                defaultDataFromProp.setFieldName(ts.getFieldName());
                                defaultDataFromProp.setPropName(ts.getOriginalFieldName());
                                defaultDataFromProps.add(defaultDataFromProp);
                            }

                            if (ts.getUpdatedFieldName() != null && !ts.getUpdatedFieldName().isEmpty()) {
                                if (updateDataFromProps == null) updateDataFromProps = new ArrayList<>();
                                WatchDataFieldDto updateDataFromProp = new WatchDataFieldDto();
                                updateDataFromProp.setFieldName(ts.getFieldName());
                                updateDataFromProp.setPropName(ts.getUpdatedFieldName());
                                updateDataFromProps.add(updateDataFromProp);
                            }
                        }
                        WatchFieldNameDto watchFieldName = new WatchFieldNameDto();
                        watchFieldName.setFields(fields);
                        watchFieldName.setDefaultDataFromProps(defaultDataFromProps);
                        watchFieldName.setUpdateDataFromProps(updateDataFromProps);
                        watchFieldName.setResetValue(tenantSectionDetail.getWatchResetValue());
                        watches.add(watchFieldName);
                    }
                    tenantSectionDetail.setWatches(watches);
                }

                if (tenantSectionDetail.getCssStyles() != null && !tenantSectionDetail.getCssStyles().isEmpty()) {
                    for (String cssStyles : tenantSectionDetail.getCssStyles().split(",", -1)) {
                        String[] style = cssStyles.split(":");
                        if (styles.put(style[0].trim(), style[1].trim().replaceAll("\"", "")) != null) {
                            throw new IllegalStateException("Duplicate key");
                        }
                    }
                }
                tenantSectionDetail.setStyles(styles);

                if ((tenantSectionDetail.getDisableObjectName() != null && !tenantSectionDetail.getDisableObjectName().isEmpty()) &&
                        (tenantSectionDetail.getDisableObjectValues() != null && !tenantSectionDetail.getDisableObjectValues().isEmpty())) {
                    if (disable == null) disable = new ArrayList<>();

                    DisableObjectDto disableObj = new DisableObjectDto();
                    disableObj.setName(tenantSectionDetail.getDisableObjectName());

                    String[] disableObjects = tenantSectionDetail.getDisableObjectValues().split(",");
                    disableObj.setValues(disableObjects);
                    disable.add(disableObj);
                }
                tenantSectionDetail.setDisable(disable);

                if (type.equalsIgnoreCase("REQI_HEADER") ||
                        type.equalsIgnoreCase("REQIEX_HEADER") ||
                        type.equalsIgnoreCase("REQIEC_HEADER")) {
                    tenantSectionDetail.setCode(tenantSectionDetail.getFieldName());
                    tenantSectionDetail.setDisplayName(tenantSectionDetail.getLabel());

                    tenantSectionDetail.setName(null);
                    tenantSectionDetail.setFieldName(null);
                    tenantSectionDetail.setFieldGroupName(null);
                    tenantSectionDetail.setFieldGroupLabel(null);
                    tenantSectionDetail.setLabel(null);
                    tenantSectionDetail.setPreSelect(null);
                    tenantSectionDetail.setHeight(null);
                }
                if (type.equalsIgnoreCase("REQI_HEADER")) {
                    tenantSectionDetail.setDataSource(null);
                    tenantSectionDetail.setValidations(null);
                    tenantSectionDetail.setParams(null);
                }

                if (StringUtils.containsIgnoreCase(tenantSectionDetail.getFieldName(), "PDPA") ||
                        StringUtils.containsIgnoreCase(tenantSectionDetail.getFieldGroupName(), "PDPA")) {
                    if ("SQP".equalsIgnoreCase(privilegeCode)) {
                        tenantSectionDetail.setDisabled(true);
                    }
                }

                String tenantCodeLocal = AppUtil.getTenantId();
                Tenant tenantLocal = tenantService.findByCode(tenantCodeLocal);
                boolean isDisableTypeForPurchaserEdit = tenantConfigService.isDisabledTypeForPurchaserEdit(tenantLocal.getRecId());

                if (isDisableTypeForPurchaserEdit) {
                    if (StringUtils.containsIgnoreCase(tenantSectionDetail.getFieldName(), "typeObj")) {
                        if ("SQP".equalsIgnoreCase(privilegeCode)) {
                            tenantSectionDetail.setDisabled(true);
                        }
                    }
                }
            }

            if (!TenantSectionDto.getFields().isEmpty())
                tenantSectionDtoList.add(TenantSectionDto);
        }
        return tenantSectionDtoList;
    }

    private List<TenantSectionDetailDto> populatePreviewFields(List<TenantSectionDetail> tenantSectionDetailList) {
        List<TenantSectionDetail> tenantSectionDetaillist = new ArrayList<>();
        for (TenantSectionDetail tsd : tenantSectionDetailList) {
            if (tsd.isVisible() || tsd.isPreview() || (tsd.isModeViewSQN() || tsd.isModeViewSQV() || tsd.isModeViewSQP())) {
                tenantSectionDetaillist.add(tsd);
            }
        }
        List<TenantSectionDetailDto> tenantSectionDetailDtoList = TenantSectionDetailMapper.INSTANCE.toTenantSectionDetailDto(tenantSectionDetaillist);
        for (TenantSectionDetailDto tenantSectionDetail : tenantSectionDetailDtoList) {
            tenantSectionDetail.setSpan(8);
            tenantSectionDetail.setPreSpan(0);
            tenantSectionDetail.setPostSpan(0);
        }
        return tenantSectionDetailDtoList;
    }

    private List<TenantSectionDto> populateFullPreviewFields(List<TenantSection> tenantSectionList, String type) {
        List<TenantSectionDto> tenantSectionDtoList = new ArrayList<>();
        for (TenantSection tenantSection : tenantSectionList) {
            List<TenantSectionDetail> list = new ArrayList<>();
            for (TenantSectionDetail tsd : tenantSection.getFields()) {
                if (tsd.isFullPreview()) {
                    list.add(tsd);
                }
            }
            tenantSection.setFields(list);
            TenantSectionDto tenantSectionDto = TenantSectionMapper.INSTANCE.toTenantSectionDto(tenantSection);

            Integer index = 0;
            for (TenantSectionDetailDto tenantSectionDetail : tenantSectionDto.getFields()) {
                List<DependencyObjectDto> depends = null;
                List<WatchFieldNameDto> watches = null;
                if (("pdpaQ01".equalsIgnoreCase(tenantSectionDetail.getName())) ||
                        ("pdpaQ02".equalsIgnoreCase(tenantSectionDetail.getName())) ||
                        ("pdpaQ03".equalsIgnoreCase(tenantSectionDetail.getName())) ||
                        ("pdpaQ04".equalsIgnoreCase(tenantSectionDetail.getName())) ||
                        ("pdpaQ05".equalsIgnoreCase(tenantSectionDetail.getName())) ||
                        ("pdpaQ06".equalsIgnoreCase(tenantSectionDetail.getName())) ||
                        ("location".equalsIgnoreCase(tenantSectionDetail.getName())) ||
                        ("pdpaTopic01".equalsIgnoreCase(tenantSectionDetail.getName())) ||
                        ("pdpaTopic03".equalsIgnoreCase(tenantSectionDetail.getName()))
                ) {
                    tenantSectionDetail.setSpan(24);
                } else {
                    tenantSectionDetail.setSpan(12);
                    tenantSectionDetail.setPreSpan(index++ % 2 == 0 ? 0 : 12);
                    tenantSectionDetail.setPostSpan(0);
                }

                if (tenantSectionDetail.getTenantSectionDetailDependencyList() != null &&
                        tenantSectionDetail.getTenantSectionDetailDependencyList().size() > 0) {
                    for (TenantSectionDetailDependencyDto ts : tenantSectionDetail.getTenantSectionDetailDependencyList()) {
                        if (depends == null) depends = new ArrayList<>();

                        DependencyObjectDto dependObj = new DependencyObjectDto();
                        dependObj.setName(ts.getName());

                        String[] dependObjects = ts.getValue() != null ? ts.getValue().split(",") : null;
                        dependObj.setValues(dependObjects);
                        dependObj.setGroupName(ts.getGroupName());
                        dependObj.setAction(ts.getAction());
                        depends.add(dependObj);
                    }
                    tenantSectionDetail.setDepends(depends);
                }

                if (tenantSectionDetail.getTenantSectionDetailWatchList() != null &&
                        tenantSectionDetail.getTenantSectionDetailWatchList().size() > 0) {
                    if (watches == null) watches = new ArrayList<>();

                    List<String> watchGroupNameList = tenantSectionDetail.getTenantSectionDetailWatchList().stream()
                            .map(TenantSectionDetailWatchDto::getGroupName).distinct().collect(Collectors.toList());
                    for (String watchGroupName : watchGroupNameList) {

                        List<WatchFieldDto> fields = new ArrayList<>();
                        List<WatchDataFieldDto> defaultDataFromProps = null;
                        List<WatchDataFieldDto> updateDataFromProps = null;

                        for (TenantSectionDetailWatchDto ts : tenantSectionDetail.getTenantSectionDetailWatchList().stream()
                                .filter(l -> l.getGroupName().equalsIgnoreCase(watchGroupName)).collect(Collectors.toList())) {

                            WatchFieldDto fieldDto = new WatchFieldDto();
                            fieldDto.setFieldName(ts.getFieldName());
                            fieldDto.setValues(ts.getValues() != null ? ts.getValues().split(",") : null);
                            fields.add(fieldDto);

                            if (ts.getOriginalFieldName() != null && !ts.getOriginalFieldName().isEmpty()) {
                                if (defaultDataFromProps == null) defaultDataFromProps = new ArrayList<>();
                                WatchDataFieldDto defaultDataFromProp = new WatchDataFieldDto();
                                defaultDataFromProp.setFieldName(ts.getFieldName());
                                defaultDataFromProp.setPropName(ts.getOriginalFieldName());
                                defaultDataFromProps.add(defaultDataFromProp);
                            }

                            if (ts.getUpdatedFieldName() != null && !ts.getUpdatedFieldName().isEmpty()) {
                                if (updateDataFromProps == null) updateDataFromProps = new ArrayList<>();
                                WatchDataFieldDto updateDataFromProp = new WatchDataFieldDto();
                                updateDataFromProp.setFieldName(ts.getFieldName());
                                updateDataFromProp.setPropName(ts.getUpdatedFieldName());
                                updateDataFromProps.add(updateDataFromProp);
                            }
                        }
                        WatchFieldNameDto watchFieldName = new WatchFieldNameDto();
                        watchFieldName.setFields(fields);
                        watchFieldName.setDefaultDataFromProps(defaultDataFromProps);
                        watchFieldName.setUpdateDataFromProps(updateDataFromProps);
                        watchFieldName.setResetValue(tenantSectionDetail.getWatchResetValue());
                        watches.add(watchFieldName);
                    }
                    tenantSectionDetail.setWatches(watches);
                }
            }

            if (!tenantSectionDto.getFields().isEmpty())
                tenantSectionDtoList.add(tenantSectionDto);
        }
        return tenantSectionDtoList;
    }
}
