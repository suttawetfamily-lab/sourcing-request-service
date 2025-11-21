package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.TenantRequestReportDto;
import com.pantavanij.sourcingreq.services.domain.dto.TenantRequestReportSearchDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestReport;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantRequestReport;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.TenantRequestReportKey;
import com.pantavanij.sourcingreq.services.domain.mapper.TenantRequestReportMapper;
import com.pantavanij.sourcingreq.services.domain.request.ConditionSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestReportRequest;
import com.pantavanij.sourcingreq.services.domain.request.SequenceRequest;
import com.pantavanij.sourcingreq.services.domain.request.TenantRequestReportSearchRequest;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestReportRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantRequestReportRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantRequestReportService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UaaService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.services.util.CommonUtils;
import com.pantavanij.sourcingreq.services.util.Constant;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
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

import static com.pantavanij.sourcingreq.services.util.Constant.REC_ID;
import static com.pantavanij.sourcingreq.services.util.Constant.TENANT;

@Service
@RequiredArgsConstructor
public class TenantRequestReportServiceImpl implements TenantRequestReportService {
    private final TenantRequestReportRepository tenantRequestReportRepository;
    private final RequestReportRepository requestReportRepository;
    private final TenantService tenantService;
    private final UaaService uaaService;

    @Override
    public TenantRequestReportDto getByRequestReportId(Integer requestReportId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        try {
            Optional<TenantRequestReport> tenantRequestReportOpt = tenantRequestReportRepository.findByTenantRecIdAndRequestReportRecId(tenant.getRecId(), requestReportId);
            return tenantRequestReportOpt.map(tenantRequestReport -> TenantRequestReportMapper.INSTANCE.toTenantRequestReportDto(tenantRequestReport, timeZone)).orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Integer createTenantRequestReport(RequestReportRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        try {
            Optional<RequestReport> requestReport = requestReportRepository.findRequestReportByRecId(request.getId());
            if (!requestReport.isPresent()) {
                return 0;
            }

            Optional<TenantRequestReport> tenantRequestReportOpt = tenantRequestReportRepository.findByTenantRecIdAndRequestReportRecId(tenant.getRecId(), request.getId());
            if (tenantRequestReportOpt.isPresent()) {
                return -1;
            }

            Optional<TenantRequestReport> tenantRequestReportExitOpt = tenantRequestReportRepository.findFirstByTenantRecIdOrderBySequenceDesc(tenant.getRecId());
            if (tenantRequestReportExitOpt.isPresent() && request.getSequence() == 0) {
                request.setSequence(tenantRequestReportExitOpt.get().getSequence() + 1);
            } else if (tenantRequestReportExitOpt.isEmpty() && request.getSequence() == 0) {
                request.setSequence(1);
            } else {
                tenantRequestReportExitOpt.ifPresent(tenantRequestReport -> tenantRequestReportRepository.reOrderOtherTenantRequestReportSequence(tenant.getRecId(), tenantRequestReport.getSequence() + 1, request.getSequence()));
            }

            TenantRequestReportKey key = TenantRequestReportKey.builder()
                    .tenantId(tenant.getRecId())
                    .requestReportId(request.getId())
                    .build();

            TenantRequestReport tenantRequestReport = TenantRequestReport.builder()
                    .id(key)
                    .tenant(tenant)
                    .requestReport(requestReport.get())
                    .sequence(request.getSequence())
                    .isDefault(request.isDefault())
                    .active(request.isActive())
                    .createdBy(AppUtil.getUserName())
                    .createdDate(DateTimeUtil.getTimestampUTC())
                    .updatedBy(AppUtil.getUserName())
                    .updatedDate(DateTimeUtil.getTimestampUTC())
                    .build();

            TenantRequestReport saved = tenantRequestReportRepository.save(tenantRequestReport);
            return saved.getId().getRequestReportId();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Integer updateTenantRequestReport(RequestReportRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        try {
            Optional<RequestReport> requestReport = requestReportRepository.findRequestReportByRecId(request.getId());
            if (!requestReport.isPresent()) {
                return 0;
            }

            Optional<TenantRequestReport> tenantRequestReportOpt = tenantRequestReportRepository.findByTenantRecIdAndRequestReportRecId(tenant.getRecId(), request.getId());
            if (tenantRequestReportOpt.isPresent()) {
                tenantRequestReportOpt.ifPresent(tenantRequestReport -> tenantRequestReportRepository.reOrderOtherTenantRequestReportSequence(tenant.getRecId(), tenantRequestReport.getSequence(), request.getSequence()));

                TenantRequestReportKey key = TenantRequestReportKey.builder()
                        .tenantId(tenant.getRecId())
                        .requestReportId(request.getId())
                        .build();

                TenantRequestReport tenantRequestReportExist = tenantRequestReportOpt.get();
                TenantRequestReport tenantRequestReport = TenantRequestReport.builder()
                        .id(key)
                        .tenant(tenant)
                        .requestReport(requestReport.get())
                        .sequence(request.getSequence())
                        .isDefault(request.isDefault())
                        .active(request.isActive())
                        .createdBy(tenantRequestReportExist.getCreatedBy())
                        .createdDate(tenantRequestReportExist.getCreatedDate())
                        .updatedBy(AppUtil.getUserName())
                        .updatedDate(DateTimeUtil.getTimestampUTC())
                        .build();

                TenantRequestReport updated = tenantRequestReportRepository.save(tenantRequestReport);
                return updated.getId().getRequestReportId();
            }
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public TenantRequestReportSearchDto searchTenantRequestReportByCondition(TenantRequestReportSearchRequest request, Pageable pageable) {
        Page<TenantRequestReport> tenantRequestReportPage = tenantRequestReportRepository.findAll(Specification.where(searchTenantRequestReportByWhereCondition(request)), pageable);
        int totalPage = tenantRequestReportPage.getTotalPages();
        long total = tenantRequestReportPage.getTotalElements();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);

        List<TenantRequestReportDto> tennatRequestReportDtoList = TenantRequestReportMapper.INSTANCE.toTenantRequestReportDtoList(tenantRequestReportPage.getContent(), timeZone);
        TenantRequestReportSearchDto tenantRequestReportSearchDto = new TenantRequestReportSearchDto();
        tenantRequestReportSearchDto.setTenantRequestReportList(tennatRequestReportDtoList);
        tenantRequestReportSearchDto.setTotal(total);
        tenantRequestReportSearchDto.setTotalPage(totalPage);
        tenantRequestReportSearchDto.setPage(pageable.getPageNumber());
        return tenantRequestReportSearchDto;
    }

    @Override
    public TenantRequestReportDto updateTenantRequestReportSequence(SequenceRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        Optional<TenantRequestReport> tenantRequestReportOpt = tenantRequestReportRepository.findByTenantRecIdAndRequestReportRecId(tenant.getRecId(), request.getRecId());
        if (tenantRequestReportOpt.isPresent()) {
            TenantRequestReport tenantRequestReportExist = tenantRequestReportOpt.get();
            tenantRequestReportRepository.reOrderOtherTenantRequestReportSequence(tenant.getRecId(), tenantRequestReportExist.getSequence(), request.getSequence());

            tenantRequestReportExist.setSequence(request.getSequence());

            TenantRequestReport updateSequence = tenantRequestReportRepository.save(tenantRequestReportExist);
            return TenantRequestReportMapper.INSTANCE.toTenantRequestReportDto(updateSequence, timeZone);
        }
        return null;
    }

    private Specification<TenantRequestReport> searchTenantRequestReportByWhereCondition(TenantRequestReportSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            List<ConditionSearchRequest> conditionSearchRequestList = request.getConditionSearchList();

            Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
            if (tenant != null) {
                predicates.add(criteriaBuilder.equal(root.get(TENANT).get(REC_ID), tenant.getRecId()));
            }

            if (conditionSearchRequestList != null && !conditionSearchRequestList.isEmpty()) {
                for (ConditionSearchRequest condition : conditionSearchRequestList) {
                    String searchField = condition.getSearchField();
                    String searchValue = condition.getSearchValue();

                    if (StringUtils.isNotEmpty(searchField) && StringUtils.isNotEmpty(searchValue)) {
                        if (Constant.CODE.equalsIgnoreCase(searchField)) {
                            predicates.add(criteriaBuilder.like(root.get(Constant.REQUEST_REPORT).get(searchField), "%" + searchValue + "%"));
                        } else if (Constant.NAME.equalsIgnoreCase(searchField)) {
                            predicates.add(criteriaBuilder.like(root.get(Constant.REQUEST_REPORT).get(searchField), "%" + searchValue + "%"));
                        } else if (Constant.ACTIVE.equalsIgnoreCase(searchField)) {
                            boolean active = "1".equals(searchValue) || 1 == Integer.parseInt(searchValue);
                            predicates.add(criteriaBuilder.equal(root.get(searchField), active));
                        } else if (Constant.CREATED_BY.equalsIgnoreCase(searchField)) {
                            predicates.add(criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%"));
                        } else {
                            if (CommonUtils.isNumeric(searchValue)) {
                                predicates.add(criteriaBuilder.equal(root.get(searchField), searchValue));
                            } else {
                                predicates.add(criteriaBuilder.like(root.get(searchField), "%" + searchValue.toLowerCase() + "%"));
                            }
                        }
                    }
                }
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
