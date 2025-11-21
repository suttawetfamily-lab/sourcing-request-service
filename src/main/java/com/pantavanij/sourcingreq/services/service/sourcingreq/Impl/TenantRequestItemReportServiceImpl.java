package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.TenantRequestItemReportDto;
import com.pantavanij.sourcingreq.services.domain.dto.TenantRequestItemReportSearchDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.TenantRequestItemReportKey;
import com.pantavanij.sourcingreq.services.domain.mapper.TenantRequestItemReportMapper;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.DataNotFoundException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantRequestItemReportService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UaaService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.services.util.CommonUtils;
import com.pantavanij.sourcingreq.services.util.Constant;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.util.Constant.REC_ID;
import static com.pantavanij.sourcingreq.services.util.Constant.TENANT;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantRequestItemReportServiceImpl implements TenantRequestItemReportService {

    private final TenantRequestItemReportRepository tenantRequestItemReportRepository;
    private final RequestItemReportRepository requestItemReportRepository;
    private final TenantSectionDetailRepository tenantSectionDetailRepository;
    private final TenantOrganizationTemplateRepository tenantOrganizationTemplateRepository;
    private final TenantTemplateRepository tenantTemplateRepository;
    private final TenantService tenantService;
    private final UaaService uaaService;

    // ------------------------------------------------------------------
    // Utility: Map organization -> templateId
    // ------------------------------------------------------------------
    private Integer resolveTemplateId(Integer tenantId, Integer organizationId) {
        if (organizationId == null) {
            throw new RuntimeException("organizationId is required in request header");
        }
        Integer templateId = tenantOrganizationTemplateRepository.findTemplateIdByTenantIdAndOrganizationId(tenantId, organizationId);
        if (templateId == null) {
            throw new RuntimeException("Template not found for tenantId=" + tenantId + ", organizationId=" + organizationId);
        }
        return templateId;
    }

    // ==================================================================
    // GET BY REPORT ID
    // ==================================================================
    @Override
    public List<TenantRequestItemReportDto> getByRequestItemReportId(Integer requestItemReportId) {
        return getByRequestItemReportId(requestItemReportId, null);
    }

    @Override
    public List<TenantRequestItemReportDto> getByRequestItemReportId(Integer requestItemReportId, Integer organizationId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);

        try {
            List<TenantRequestItemReport> list = tenantRequestItemReportRepository
                    .findByTenantRecIdAndRequestItemReportRecId(tenant.getRecId(), requestItemReportId);
            if (list.isEmpty()) {
                return new ArrayList<>();
            }

            if (organizationId != null) {
                Integer templateId = resolveTemplateId(tenant.getRecId(), organizationId);
                list = list.stream()
                        .filter(t -> t.getTenantTemplate() == null ||
                                (t.getTenantTemplate() != null && t.getTenantTemplate().getRecId().equals(templateId)))
                        .collect(Collectors.toList());
            }

            return TenantRequestItemReportMapper.INSTANCE.toTenantRequestItemReportDtoList(list, timeZone);
        } catch (Exception e) {
            log.error("Error in getByRequestItemReportId", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    // ==================================================================
    // CREATE
    // ==================================================================
    @Override
    @Transactional
    public TenantRequestItemReportDto createTenantRequestItemReport(TenantRequestItemReportRequest request) {
        return createTenantRequestItemReport(request, null);
    }

    @Override
    @Transactional
    public TenantRequestItemReportDto createTenantRequestItemReport(TenantRequestItemReportRequest request, Integer organizationId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);

        try {
            Optional<RequestItemReport> reportOpt = requestItemReportRepository.findById(request.getRequestItemReportId());
            if (reportOpt.isEmpty()) {
                throw new DataNotFoundException(String.format(ApiMessage.E7096.description(),
                        "Request Item Report id: " + request.getRequestItemReportId()));
            }

            Optional<TenantSectionDetail> sectionOpt =
                    tenantSectionDetailRepository.findById(request.getTenantSectionDetailId().longValue());
            if (sectionOpt.isEmpty()) {
                throw new DataNotFoundException(String.format(ApiMessage.E7096.description(),
                        "Tenant Section Detail id: " + request.getTenantSectionDetailId()));
            }

            // ปรับ logic ให้รองรับทั้งกรณีมี / ไม่มี organizationId
            TenantTemplate tenantTemplate;
            if (organizationId != null) {
                Integer templateId = resolveTemplateId(tenant.getRecId(), organizationId);
                tenantTemplate = tenantTemplateRepository.findById(templateId)
                        .orElseThrow(() -> new DataNotFoundException("Template not found: " + templateId));
            } else {
                tenantTemplate = reportOpt.get().getTenantTemplate(); // fallback
                if (tenantTemplate == null) {
                    throw new RuntimeException("TenantTemplate cannot be null when organizationId is not provided");
                }
            }

            TenantRequestItemReport entity = getSubmitTenantRequestItemReport(
                    request,
                    reportOpt.get(),
                    sectionOpt.get(),
                    tenantTemplate,
                    tenant
            );

            TenantRequestItemReport saved = tenantRequestItemReportRepository.save(entity);
            return TenantRequestItemReportMapper.INSTANCE.toTenantRequestItemReportDto(saved, timeZone);

        } catch (Exception e) {
            log.error("Error in createTenantRequestItemReport", e);
            throw new RuntimeException(e.getMessage());
        }
    }


    private TenantRequestItemReport getSubmitTenantRequestItemReport(
            TenantRequestItemReportRequest req,
            RequestItemReport report,
            TenantSectionDetail section,
            TenantTemplate template,
            Tenant tenant
    ) {
        TenantRequestItemReportKey key = new TenantRequestItemReportKey();
        key.setTenantId(tenant.getRecId());
        key.setRequestItemReportId(report.getRecId());
        key.setTenantSectionDetailId(section.getId());

        return TenantRequestItemReport.builder()
                .id(key)
                .tenant(tenant)
                .requestItemReport(report)
                .tenantSectionDetail(section)
                .tenantTemplate(template)
                .sequence(req.getSequence())
                .isDefault(req.isDefault())
                .active(req.isActive())
                .createdBy(AppUtil.getUserName())
                .createdDate(DateTimeUtil.getTimestampUTC())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }

    // ==================================================================
    // UPDATE (ยึด old logic แต่เพิ่มรองรับ organizationId/template)
    // ==================================================================
    @Override
    public Integer updateTenantRequestItemReport(TenantRequestItemReportRequest request) {
        return updateTenantRequestItemReport(request, null);
    }

    @Override
    @Transactional
    public Integer updateTenantRequestItemReport(TenantRequestItemReportRequest request, Integer organizationId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String userName = AppUtil.getUserName();

        try {
            Optional<RequestItemReport> requestItemReportOpt = requestItemReportRepository.findById(request.getId());
            if (requestItemReportOpt.isEmpty()) {
                return 0;
            }

            List<TenantRequestItemReport> list =
                    tenantRequestItemReportRepository.findByTenantRecIdAndRequestItemReportRecId(tenant.getRecId(), request.getId());

            if (list.isEmpty()) {
                return -1;
            }

            TenantRequestItemReport existing = list.get(0);
            Integer currentSeq = existing.getSequence();
            Integer newSeq = request.getSequence();

            // re-order sequence เหมือน old
            tenantRequestItemReportRepository.reOrderOtherTenantRequestItemReportSequence(
                    tenant.getRecId(), currentSeq, newSeq
            );

            // update entity
            existing.setSequence(newSeq);
            existing.setDefault(request.isDefault());
            existing.setActive(request.isActive());
            existing.setUpdatedBy(userName);
            existing.setUpdatedDate(DateTimeUtil.getTimestampUTC());

            TenantRequestItemReport updated = tenantRequestItemReportRepository.save(existing);
            return updated.getId().getRequestItemReportId();
        } catch (Exception e) {
            log.error("Error in updateTenantRequestItemReport", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    // ==================================================================
    // SEARCH (เหมือน old)
    // ==================================================================
    @Override
    public TenantRequestItemReportSearchDto searchTenantRequestItemReportByCondition(
            TenantRequestItemReportSearchRequest request, Pageable pageable) {
        return searchTenantRequestItemReportByCondition(request, pageable, null);
    }

    @Override
    public TenantRequestItemReportSearchDto searchTenantRequestItemReportByCondition(
            TenantRequestItemReportSearchRequest request, Pageable pageable, Integer organizationId) {

        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Integer tenantId = tenant.getRecId();
        Integer templateId = null;
        if (organizationId != null) {
            templateId = resolveTemplateId(tenantId, organizationId);
        }

        Page<TenantRequestItemReport> page = tenantRequestItemReportRepository.findAll(
                Specification.where(searchTenantRequestItemReportByWhereCondition(request, tenantId, templateId)),
                pageable);

        int totalPage = page.getTotalPages();
        long total = page.getTotalElements();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);

        List<TenantRequestItemReportDto> dtoList =
                TenantRequestItemReportMapper.INSTANCE.toTenantRequestItemReportDtoList(page.getContent(), timeZone);

        TenantRequestItemReportSearchDto dto = new TenantRequestItemReportSearchDto();
        dto.setTenantRequestItemReportList(dtoList);
        dto.setTotal(total);
        dto.setTotalPage(totalPage);
        dto.setPage(pageable.getPageNumber());
        return dto;
    }

    private Specification<TenantRequestItemReport> searchTenantRequestItemReportByWhereCondition(
            TenantRequestItemReportSearchRequest request,
            Integer tenantId,
            Integer templateId
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get(TENANT).get(REC_ID), tenantId));
            if (templateId != null) {
                predicates.add(cb.equal(root.get("tenantTemplate").get("recId"), templateId));
            }

            List<ConditionSearchRequest> conditionList = request.getConditionSearchList();
            if (conditionList != null && !conditionList.isEmpty()) {
                for (ConditionSearchRequest condition : conditionList) {
                    String field = condition.getSearchField();
                    String value = condition.getSearchValue();

                    if (StringUtils.isNotEmpty(field) && StringUtils.isNotEmpty(value)) {
                        if (Constant.CODE.equalsIgnoreCase(field)) {
                            predicates.add(cb.like(root.get(Constant.REQUEST_ITEM_REPORT).get(field), "%" + value + "%"));
                        } else if (Constant.NAME.equalsIgnoreCase(field)) {
                            predicates.add(cb.like(root.get(Constant.REQUEST_ITEM_REPORT).get(field), "%" + value + "%"));
                        } else if (Constant.ACTIVE.equalsIgnoreCase(field)) {
                            boolean active = "1".equals(value) || "true".equalsIgnoreCase(value);
                            predicates.add(cb.equal(root.get(field), active));
                        } else if (Constant.CREATED_BY.equalsIgnoreCase(field)) {
                            predicates.add(cb.like(root.get(field), "%" + value + "%"));
                        } else {
                            if (CommonUtils.isNumeric(value)) {
                                predicates.add(cb.equal(root.get(field), value));
                            } else {
                                predicates.add(cb.like(root.get(field), "%" + value.toLowerCase() + "%"));
                            }
                        }
                    }
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // ==================================================================
    // UPDATE SEQUENCE (คืน logic เดิมทั้งหมด)
    // ==================================================================
    @Override
    public TenantRequestItemReportDto updateTenantRequestItemReportSequence(SequenceRequest request) {
        return updateTenantRequestItemReportSequence(request, null);
    }

    @Override
    @Transactional
    public TenantRequestItemReportDto updateTenantRequestItemReportSequence(SequenceRequest request, Integer organizationId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);

        Integer tenantId = tenant.getRecId();
        Integer templateId = null;
        if (organizationId != null) {
            templateId = resolveTemplateId(tenantId, organizationId);
        }

        Optional<TenantRequestItemReport> opt = tenantRequestItemReportRepository
                .findByTenantRecIdAndRequestItemReportRecId(tenantId, request.getRecId())
                .stream().findFirst();

        if (opt.isEmpty()) {
            log.warn("No TenantRequestItemReport found for tenantId={}, requestId={}", tenantId, request.getRecId());
            return null;
        }

        TenantRequestItemReport existing = opt.get();

        if (templateId != null) {
            tenantRequestItemReportRepository.reOrderOtherTenantRequestItemReportSequenceWithTemplate(
                    tenantId, templateId, existing.getSequence(), request.getSequence());
        } else {
            tenantRequestItemReportRepository.reOrderOtherTenantRequestItemReportSequence(
                    tenantId, existing.getSequence(), request.getSequence());
        }

        existing.setSequence(request.getSequence());
        existing.setUpdatedBy(AppUtil.getUserName());
        existing.setUpdatedDate(DateTimeUtil.getTimestampUTC());

        TenantRequestItemReport updated = tenantRequestItemReportRepository.save(existing);
        return TenantRequestItemReportMapper.INSTANCE.toTenantRequestItemReportDto(updated, timeZone);
    }
}
