package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.EpAuthClient;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.TenantRequestItemReportKey;
import com.pantavanij.sourcingreq.services.domain.mapper.RequestItemReportMapper;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.RequestItemReportResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.DataNotFoundException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
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
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.SearchRequestItemReportType.*;
import static com.pantavanij.sourcingreq.services.util.Constant.REC_ID;
import static com.pantavanij.sourcingreq.services.util.Constant.TENANT;

@Slf4j
@RequiredArgsConstructor
@Service
public class RequestItemReportServiceImpl implements RequestItemReportService {

    private final RequestItemReportRepository requestItemReportRepository;
    private final MenuPrivilegeRepository menuPrivilegeRepository;
    private final TenantService tenantService;
    private final UaaService uaaService;
    private final EPAuthService epAuthService;
    private final EpAuthClient epAuthClient;
    private final TenantConfigService tenantConfigService;
    private final TenantSectionDetailRepository tenantSectionDetailRepository;
    private final TenantRequestItemReportRepository tenantRequestItemReportRepository;
    private final TenantOrganizationTemplateRepository tenantOrganizationTemplateRepository;
    private final TenantTemplateRepository tenantTemplateRepository;

    // =========================================================
    // 🔹 Utility: หา templateId จาก tenant + organization
    // =========================================================
    private Integer resolveTemplateId(Integer tenantId, Integer organizationId) {
        if (organizationId == null) throw new RuntimeException("organizationId is required in request header");
        Integer templateId = tenantOrganizationTemplateRepository.findTemplateIdByTenantIdAndOrganizationId(tenantId, organizationId);
        if (templateId == null) throw new RuntimeException("Template not found for tenantId=" + tenantId + ", organizationId=" + organizationId);
        return templateId;
    }

    // =========================================================
    // 🔹 Search (คืน logic เดิม)
    // =========================================================
    @Override
    public RequestItemReportResponse getRequestRequestItemReport(RequestItemReportSearchRequest request, Pageable pageable) {
        Page<RequestItemReport> page = requestItemReportRepository.findAll(Specification.where(getSpecificationByCondition(request)), pageable);
        int totalPage = page.getTotalPages();
        long total = page.getTotalElements();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        List<RequestItemReportDto> list = RequestItemReportMapper.INSTANCE.toRequestItemReportDtoList(page.getContent(), timeZone)
                .stream().distinct().collect(Collectors.toList());
        return RequestItemReportResponse.builder()
                .total(total)
                .totalPage(totalPage)
                .data(list)
                .pageSize(list.size())
                .build();
    }

    private Specification<RequestItemReport> getSpecificationByCondition(RequestItemReportSearchRequest searchRequest) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (searchRequest.getConditionSearchList() != null) {
                for (ConditionSearchRequest c : searchRequest.getConditionSearchList()) {
                    if (StringUtils.isNotEmpty(c.getSearchField()) && StringUtils.isNotEmpty(c.getSearchValue())) {
                        predicates.add(cb.like(root.get(c.getSearchField()), "%" + c.getSearchValue().toLowerCase() + "%"));
                    }
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // =========================================================
    // 🔹 CREATE (รวม old + new)
    // =========================================================
    @Override
    @Transactional
    public RequestItemReportDto createRequestItemReport(RequestItemReportRequest req, Integer organizationId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Integer tenantId = tenant.getRecId();
        Integer templateId = resolveTemplateId(tenantId, organizationId);
        TenantTemplate tenantTemplate = tenantTemplateRepository.findById(templateId)
                .orElseThrow(() -> new DataNotFoundException("Template not found: " + templateId));

        Optional<MenuPrivilege> menuPrivilegeOpt = menuPrivilegeRepository.findById(req.getMenuPrivilegeId());
        if (menuPrivilegeOpt.isEmpty()) {
            throw new DataNotFoundException(String.format(ApiMessage.E7096.description(),
                    "MenuPrivilege id: " + req.getMenuPrivilegeId()));
        }

        Optional<RequestItemReport> maxSeq = requestItemReportRepository.findFirstByTenantRecIdOrderBySequenceDesc(tenantId);
        req.setSequence(maxSeq.map(r -> r.getSequence() + 1).orElse(1));

        List<RequestItemReport> activeList = new ArrayList<>();
        if (req.isActive()) {
            activeList = requestItemReportRepository.findActiveRequestItemReportByTenantAndMenuPrivilegeAndPrivilegeCode(
                    tenantId, req.getMenuPrivilegeId(), req.getPrivilegeCode());
        }

        RequestItemReport report = getSubmitRequestItemReport(req, menuPrivilegeOpt.get());
        report.setTenantTemplate(tenantTemplate);
        RequestItemReport saved = requestItemReportRepository.save(report);

        List<TenantRequestItemReport> tenantRequestItemReports = getSubmitTenantRequestItemReportList(req, saved);
        saved.setTenantRequestItemReportList(tenantRequestItemReports);
        requestItemReportRepository.save(saved);

        if (!activeList.isEmpty()) {
            activeList.forEach(r -> r.setActive(false));
            requestItemReportRepository.saveAll(activeList);
        }

        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        return RequestItemReportMapper.INSTANCE.toRequestItemReportDto(saved, timeZone);
    }

    // =========================================================
    // 🔹 UPDATE (รวม logic old + new)
    // =========================================================
    @Transactional
    @Override
    public RequestItemReportDto updateRequestItemReport(RequestItemReportRequest req, Integer organizationId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Integer tenantId = tenant.getRecId();
        Integer templateId = resolveTemplateId(tenantId, organizationId);
        TenantTemplate tenantTemplate = tenantTemplateRepository.findById(templateId)
                .orElseThrow(() -> new DataNotFoundException("Template not found: " + templateId));

        RequestItemReport existing = requestItemReportRepository.findById(req.getId())
                .orElseThrow(() -> new DataNotFoundException("RequestItemReport id " + req.getId() + " not found"));

        Optional<MenuPrivilege> menuPrivilegeOpt = menuPrivilegeRepository.findById(req.getMenuPrivilegeId());
        if (menuPrivilegeOpt.isEmpty()) {
            throw new DataNotFoundException(String.format(ApiMessage.E7096.description(),
                    "MenuPrivilege id: " + req.getMenuPrivilegeId()));
        }

        // ต้องเซต tenantTemplate ก่อน
        existing.setTenantTemplate(tenantTemplate);

        // แล้วค่อยสร้าง new list
        List<TenantRequestItemReport> oldList = existing.getTenantRequestItemReportList();
        List<TenantRequestItemReport> newList = getSubmitTenantRequestItemReportList(req, existing);

        List<TenantRequestItemReport> toDelete = oldList.stream()
                .filter(o -> newList.stream().noneMatch(n -> n.getTenantSectionDetail().getId().equals(o.getTenantSectionDetail().getId())))
                .collect(Collectors.toList());
        for (TenantRequestItemReport del : toDelete) {
            tenantRequestItemReportRepository.deleteTenantRequestItemReportByIds(
                    tenantId, del.getRequestItemReport().getRecId(), del.getTenantSectionDetail().getId().intValue());
        }

        existing.setCode(req.getCode());
        existing.setName(req.getName());
        existing.setMenuPrivilege(menuPrivilegeOpt.get());
        existing.setPrivilegeCode(req.getPrivilegeCode());
        existing.setExportFileName(req.getExportFileName());
        existing.setSequence(req.getSequence());
        existing.setDefault(req.isDefault());
        existing.setActive(req.isActive());
        existing.setUpdatedBy(AppUtil.getUserName());
        existing.setUpdatedDate(DateTimeUtil.getTimestampUTC());
        existing.setTenantRequestItemReportList(newList);

        RequestItemReport updated = requestItemReportRepository.save(existing);

        if (req.isActive()) {
            List<RequestItemReport> activeList = requestItemReportRepository.findActiveRequestItemReportByTenantAndMenuPrivilegeAndPrivilegeCode(
                    tenantId, req.getMenuPrivilegeId(), req.getPrivilegeCode());
            for (RequestItemReport r : activeList) {
                if (!r.getRecId().equals(updated.getRecId())) r.setActive(false);
            }
            requestItemReportRepository.saveAll(activeList);
        }

        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        return RequestItemReportMapper.INSTANCE.toRequestItemReportDto(updated, timeZone);
    }


    // =========================================================
    // 🔹 DELETE (คืนจาก old)
    // =========================================================
    @Transactional
    @Override
    public boolean deleteRequestItemReport(Integer id) {
        try {
            Optional<RequestItemReport> opt = requestItemReportRepository.findById(id);
            if (opt.isEmpty()) return false;
            RequestItemReport report = opt.get();
            if (!report.getTenantRequestItemReportList().isEmpty()) {
                tenantRequestItemReportRepository.deleteAll(report.getTenantRequestItemReportList());
            }
            requestItemReportRepository.delete(report);
            return true;
        } catch (Exception e) {
            log.error("Error deleteRequestItemReport", e);
            return false;
        }
    }

    // =========================================================
    // 🔹 UPDATE SEQUENCE (เหมือนเดิม)
    // =========================================================
    @Override
    public RequestItemReportDto updateRequestItemReportSequence(SequenceRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Optional<RequestItemReport> opt = requestItemReportRepository.findRequestItemReportByRecIdAndTenant(request.getRecId(), tenant.getRecId());
        if (opt.isPresent()) {
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            requestItemReportRepository.reOrderOtherRequestItemReportSequence(
                    tenant.getRecId(),
                    opt.get().getSequence(),
                    request.getSequence());
            RequestItemReport updated = requestItemReportRepository.save(getRequestItemReportUpdateSequence(opt.get(), request, tenant));
            return RequestItemReportMapper.INSTANCE.toRequestItemReportDto(updated, timeZone);
        }
        return null;
    }

    // =========================================================
    // 🔹 Helpers (เหมือน old)
    // =========================================================
    public RequestItemReport getRequestItemReportUpdateSequence(RequestItemReport item, SequenceRequest request, Tenant tenant) {
        return RequestItemReport.builder()
                .recId(item.getRecId())
                .tenant(tenant)
                .sequence(request.getSequence())
                .isDefault(item.isDefault())
                .active(item.isActive())
                .createdBy(item.getCreatedBy())
                .createdDate(item.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }

    public RequestItemReport getSubmitRequestItemReport(RequestItemReportRequest req, MenuPrivilege menuPrivilege) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        return RequestItemReport.builder()
                .recId(req.getId())
                .tenant(tenant)
                .code(req.getCode())
                .name(req.getName())
                .menuPrivilege(menuPrivilege)
                .privilegeCode(req.getPrivilegeCode())
                .exportFileName(req.getExportFileName())
                .sequence(req.getSequence())
                .isDefault(req.isDefault())
                .active(req.isActive())
                .createdBy(AppUtil.getUserName())
                .createdDate(DateTimeUtil.getTimestampUTC())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .tenantRequestItemReportList(Collections.emptyList())
                .build();
    }

    public List<TenantRequestItemReport> getSubmitTenantRequestItemReportList(RequestItemReportRequest req, RequestItemReport report) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        List<TenantRequestItemReportDto> list = req.getTenantRequestItemReportList();
        List<TenantRequestItemReport> result = new ArrayList<>();
        if (list == null) return result;

        TenantTemplate tenantTemplate = report.getTenantTemplate();

        for (TenantRequestItemReportDto dto : list) {
            Optional<TenantSectionDetail> sectionOpt = tenantSectionDetailRepository.findFirstByTenantAndId(tenant, dto.getTenantSectionDetail().getValue());
            sectionOpt.ifPresent(section -> {
                TenantRequestItemReportKey id = TenantRequestItemReportKey.builder()
                        .tenantId(tenant.getRecId())
                        .requestItemReportId(report.getRecId())
                        .tenantSectionDetailId(section.getId())
                        .build();

                result.add(TenantRequestItemReport.builder()
                        .id(id)
                        .tenant(tenant)
                        .requestItemReport(report)
                        .tenantSectionDetail(section)
                        .tenantTemplate(tenantTemplate)
                        .sequence(dto.getSequence())
                        .isDefault(dto.getIsDefault())
                        .active(dto.getActive())
                        .createdBy(AppUtil.getUserName())
                        .createdDate(DateTimeUtil.getTimestampUTC())
                        .updatedBy(AppUtil.getUserName())
                        .updatedDate(DateTimeUtil.getTimestampUTC())
                        .build());
            });
        }
        return result;
    }

    @Override
    public Integer saveRequestItemReport(RequestItemReportDto requestItemReportDto) {
        UserDto user = AppUtil.getUser();
        Tenant tenant = tenantService.findByCode(user.getTenantId());
        log.info("Save RequestItemReport for Code : {}, Name: {}",
                requestItemReportDto.getCode(), requestItemReportDto.getName());

        RequestItemReport entity = requestItemReportRepository
                .findRequestItemReportByRecIdAndTenant(requestItemReportDto.getRecId(), tenant.getRecId())
                .orElse(new RequestItemReport());

        entity.setTenant(tenant);
        entity.setUpdatedBy(user.getUsername());
        entity.setUpdatedDate(DateTimeUtil.getTimestampUTC());

        if (StringUtils.isEmpty(entity.getCreatedBy())) {
            entity.setCreatedBy(user.getUsername());
        }
        if (entity.getCreatedDate() == null) {
            entity.setCreatedDate(DateTimeUtil.getTimestampUTC());
        }

        return requestItemReportRepository.save(entity).getRecId();
    }

    @Override
    public RequestItemReportDto findRequestItemReportByRecId(Integer requestItemReportId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        Optional<RequestItemReport> opt =
                requestItemReportRepository.findRequestItemReportByRecIdAndTenant(requestItemReportId, tenant.getRecId());
        return opt.map(r -> RequestItemReportMapper.INSTANCE.toRequestItemReportDto(r, timeZone)).orElse(null);
    }

    @Override
    public RequestItemReportSearchDto searchRequestItemReportListByCondition(
            RequestItemReportSearchRequest request, Pageable pageable, Integer organizationId) {

        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Integer tenantId = tenant.getRecId();
        Integer templateId = organizationId != null ? resolveTemplateId(tenantId, organizationId) : null;

        Page<RequestItemReport> page = requestItemReportRepository.findAll(
                Specification.where(getSearchSpecificationByCondition(request, tenantId, templateId)), pageable);

        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        List<RequestItemReportDto> dtoList =
                RequestItemReportMapper.INSTANCE.toRequestItemReportDtoList(page.getContent(), timeZone);

        RequestItemReportSearchDto dto = new RequestItemReportSearchDto();
        dto.setRequestItemReportDtoList(dtoList);
        dto.setTotal(page.getTotalElements());
        dto.setTotalPage(page.getTotalPages());
        dto.setPage(pageable.getPageNumber());
        return dto;
    }

    private Specification<RequestItemReport> getSearchSpecificationByCondition(
            RequestItemReportSearchRequest request, Integer tenantId, Integer templateId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get(TENANT).get(REC_ID), tenantId));
            if (templateId != null) {
                predicates.add(cb.equal(root.get("tenantTemplate").get("recId"), templateId));
            }
            if (request.getConditionSearchList() != null) {
                for (ConditionSearchRequest cond : request.getConditionSearchList()) {
                    if (StringUtils.isNotEmpty(cond.getSearchField()) && StringUtils.isNotEmpty(cond.getSearchValue())) {
                        predicates.add(cb.like(root.get(cond.getSearchField()), "%" + cond.getSearchValue() + "%"));
                    }
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public RequestItemReport getRequestItemReportUpdate(RequestItemReportRequest req,
                                                        RequestItemReport existing,
                                                        MenuPrivilege menuPrivilege) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        return RequestItemReport.builder()
                .recId(req.getId())
                .tenant(tenant)
                .code(req.getCode())
                .name(req.getName())
                .menuPrivilege(menuPrivilege)
                .privilegeCode(req.getPrivilegeCode())
                .exportFileName(req.getExportFileName())
                .sequence(req.getSequence() != null ? req.getSequence() : existing.getSequence())
                .isDefault(req.isDefault())
                .active(req.isActive())
                .createdBy(AppUtil.getUserName())
                .createdDate(DateTimeUtil.getTimestampUTC())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .tenantRequestItemReportList(Collections.emptyList())
                .build();
    }


}
