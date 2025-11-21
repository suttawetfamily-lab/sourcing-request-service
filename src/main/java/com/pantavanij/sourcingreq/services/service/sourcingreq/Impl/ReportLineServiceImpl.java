package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.EpAuthClient;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.mapper.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthReviewerResponse;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthUserListResponse;
import com.pantavanij.sourcingreq.services.domain.response.ReportLineResponse;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.ReportLineRepository;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.Role.*;
import static com.pantavanij.sourcingreq.services.enums.SearchReportLine.*;
import static com.pantavanij.sourcingreq.services.util.Constant.REC_ID;
import static com.pantavanij.sourcingreq.services.util.Constant.TENANT;


@Slf4j
@RequiredArgsConstructor
@Service
public class ReportLineServiceImpl implements ReportLineService {
    private final ReportLineRepository reportLineRepository;
    private final UaaService uaaService;
    private final EPAuthService epAuthService;
    private final TenantService tenantService;
    private final TenantConfigService tenantConfigService;
    private final EpAuthClient epAuthClient;

    @Override
    public ReportLineResponse getRequestReportLine(ReportLineSearchRequest request, Pageable pageable) {
        Page<ReportLine> requestReportLines = reportLineRepository.findAll(Specification.where(getSpecificationByCondition(request)), pageable);
        int totalPage = requestReportLines.getTotalPages();
        long total = requestReportLines.getTotalElements();

        List<ReportLineDto> requestReportLineDtos = ReportLineMapper.INSTANCE.toRequestReportLineDtoList(requestReportLines.getContent())
                .stream()
                .filter(i -> !request.getExceptReportLines().contains(i.getRecId().intValue()))
                .distinct()
                .collect(Collectors.toList());

        return ReportLineResponse.builder()
                .total(total)
                .totalPage(totalPage)
                .data(requestReportLineDtos)
                .pageSize(requestReportLineDtos.size())
                .build();
    }

    @Override
    public EPAuthReviewerDto getDefaultReportLine() {
        UserDto userDto = AppUtil.getUser();
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        log.info("Get User Detail for username:{}, tenant:{} , IDP:{}", userDto.getUsername(), userDto.getTenantId(), userDto.getIdp());
        ContractDetailClientDto userDetail = uaaService.getContractDetail(userDto.getTenantId(), userDto.getIdp(), userDto.getUsername(), null);
        // Request Param will be BorgId and userId from UAA Service
        String borgId = userDetail.getBorgIdList().get(0);
        log.info("Get default report line for borgId:{} and sysUserId:{}",borgId , userDetail.getUserId());
        EPAuthReviewerDto defaultReportLine = new EPAuthReviewerDto();
        try {
//            EPAuthUserDTO epAuthUserDTO = epAuthService.getDefaultReportLine(userDto.getTenantId(), borgId, userDetail.getUserId());
            EPAuthDefaultUserDTO epAuthUserDTO = epAuthService.getDefaultUser(userDto.getTenantId(), borgId, userDetail.getUserId());
            log.info("Default report line : {}", epAuthUserDTO);
            defaultReportLine.setFullName(epAuthUserDTO.getFirstName());
            defaultReportLine.setSysUserId(Integer.valueOf(epAuthUserDTO.getUserId()));
            defaultReportLine.setLoginId(epAuthUserDTO.getUserId());
            defaultReportLine.setEmail(epAuthUserDTO.getEmail());
            defaultReportLine.setPhone(epAuthUserDTO.getPhone());
            defaultReportLine.setMobilePhone(epAuthUserDTO.getMobilePhone());
            return defaultReportLine;
        } catch (Exception exception) {
            log.error(exception.getMessage());
            defaultReportLine = null;
        }
        if ( null != defaultReportLine) {
            ReportLineDto saveReportLine = new ReportLineDto();
            saveReportLine.setPhone(defaultReportLine.getPhone());
            saveReportLine.setEmail(defaultReportLine.getEmail());
            saveReportLine.setUserId(defaultReportLine.getSysUserId().toString());
            saveReportLine.setReportLineName(defaultReportLine.getFullName());
            saveReportLine.setMobilePhone(defaultReportLine.getMobilePhone());
            saveReportLine.setLoginId(defaultReportLine.getLoginId());
            Long reportLineId = this.saveReportLine(saveReportLine, tenant);
        }

        return defaultReportLine;
    }

    @Override
    public Long saveReportLine(ReportLineDto reportLineDto, Tenant tenant) {
        UserDto userDto = AppUtil.getUser();
        log.info("Save Report line for SysUserId : {}, username: {}", reportLineDto.getUserId(), reportLineDto.getReportLineName());
        ReportLine reportLine = reportLineRepository.findByUserId(Integer.parseInt(reportLineDto.getUserId()))
                .orElse(new ReportLine());

        reportLine.setPhone(reportLineDto.getPhone());
        reportLine.setEmail(reportLineDto.getEmail());
        reportLine.setUserId(Integer.parseInt(reportLineDto.getUserId()));
        reportLine.setReportLineName(reportLineDto.getReportLineName());
        reportLine.setTenant(tenant);
        reportLine.setUpdatedBy(AppUtil.getUserName());
        reportLine.setUpdatedDate(DateTimeUtil.getTimestampUTC());
        reportLine.setLoginId(reportLineDto.getLoginId());

        if (StringUtils.isEmpty(reportLine.getCreatedBy())) {
            reportLine.setCreatedBy(AppUtil.getUserName());
        }
        if (reportLine.getCreatedDate() == null) {
            reportLine.setCreatedDate(DateTimeUtil.getTimestampUTC());
        }

        return reportLineRepository.save(reportLine).getRecId();
    }

    @Override
    public EPAuthReviewerResponse getReportLineListByConditions(ReportLineSearchRequest request, List<Long> requestReportLines) {
        EPAuthUserSearchRequest epAuthUserSearchRequest = EPAuthUserSearchRequest.builder()
                .conditionSearchList(request.getConditionSearchList())
                .tenantId(request.getTenantId())
                .page(request.getPage())
                .pageSize(request.getPageSize())
                .sortBy(request.getSortBy())
                .sortOrder(request.getSortOrder())
                .build();

//        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String[] privilegeCode = new String[] {REPORT_LINE.privilegeCode()};
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
            ReportLineDto saveReportLine = new ReportLineDto();
            saveReportLine.setPhone(epAuthUserDTO.getPhone());
            saveReportLine.setEmail(epAuthUserDTO.getEmail());
            saveReportLine.setUserId(epAuthUserDTO.getSysUserId().toString());
            saveReportLine.setReportLineName(epAuthUserDTO.getFullName());
            saveReportLine.setMobilePhone(epAuthUserDTO.getMobilePhone());
            saveReportLine.setLoginId(epAuthUserDTO.getLoginId());
            if(requestReportLines != null && requestReportLines.contains(Long.parseLong(saveReportLine.getUserId()))) {
                this.saveReportLine(saveReportLine, tenant);
            }

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
    public List<ReportLine> findByReportLineName(String reportLineName) {
        return reportLineRepository.findByApproverName(reportLineName);
    }

    @Override
    public ReportLineDto createReportLine(ReportLineRequest reportLineRequest) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        try {
            Optional<ReportLine> reportLineOptional = reportLineRepository.findFirstByTenantRecIdOrderBySequenceDesc(tenant.getRecId());
            if (reportLineOptional.isPresent() && reportLineRequest.getSequence() == 0) {
                reportLineRequest.setSequence(reportLineOptional.get().getSequence() + 1);
            } else if (reportLineOptional.isEmpty() && reportLineRequest.getSequence() == 0) {
                reportLineRequest.setSequence(1);
            } else {
                reportLineOptional.ifPresent(reportLine -> reportLineRepository.reOrderOtherReportLineSequence(tenant.getRecId(), reportLine.getSequence() + 1, reportLineRequest.getSequence()));
            }

            ReportLine reportLine = reportLineRepository.save(getSubmitReportLine(reportLineRequest));
            return RequestMapper.INSTANCE.toReportLineDto(reportLine, timeZone);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ReportLineDto updateReportLine(ReportLineRequest reportLineRequest) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        try {
            Optional<ReportLine> reportLine = reportLineRepository.findReportLineByRecIdAndTenant(reportLineRequest.getRecId(), tenant.getRecId());
            if (reportLine.isPresent()) {
                reportLineRepository.reOrderOtherReportLineSequence(tenant.getRecId(), reportLine.get().getSequence(), reportLineRequest.getSequence());
                ReportLine reportLineUpdate = reportLineRepository.save(getReportLineUpdate(reportLine.get(), reportLineRequest));
                return RequestMapper.INSTANCE.toReportLineDto(reportLineUpdate, timeZone);
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
    public boolean deleteReportLine(Integer reportLineId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Optional<ReportLine> reportLineDto = reportLineRepository.findReportLineByRecIdAndTenant(reportLineId, tenant.getRecId());
        if (reportLineDto.isPresent()) {
            reportLineRepository.deleteByRecIdAndTenant(Long.valueOf(reportLineId), tenant);
            reportLineRepository.reOrderSequenceByTenantRecId(tenant.getRecId());
            return true;
        }
        return false;
    }

    @Override
    public ReportLineDto updateReportLineSequence(SequenceRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Optional<ReportLine> reportLineOptional = reportLineRepository.findReportLineByRecIdAndTenant(request.getRecId(), tenant.getRecId());
        if (reportLineOptional.isPresent()) {
            reportLineRepository.reOrderOtherReportLineSequence(tenant.getRecId(), reportLineOptional.get().getSequence(), request.getSequence());
            ReportLine reportLineUpdateSequence = reportLineRepository.save(getReportLineSequenceUpdate(reportLineOptional.get(), request));
            return RequestMapper.INSTANCE.toReportLineDto(reportLineUpdateSequence);
        }
        return null;
    }

    private ReportLine getReportLineSequenceUpdate(ReportLine reportLine, SequenceRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        return ReportLine.builder()
                .recId(Long.valueOf(reportLine.getRecId()))
                .tenant(tenant)
                .userId(reportLine.getUserId())
                .loginId(reportLine.getLoginId())
                .reportLineName(reportLine.getReportLineName())
                .email(reportLine.getEmail())
                .phone(reportLine.getPhone())
                .sequence(request.getSequence())
                .isDefault(reportLine.isDefault())
                .active(reportLine.isActive())
                .createdBy(reportLine.getCreatedBy())
                .createdDate(reportLine.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }


    public ReportLine getReportLineUpdate(ReportLine reportLine, ReportLineRequest reportLineRequest) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        return ReportLine.builder()
                .recId(Long.valueOf(reportLineRequest.getRecId()))
                .tenant(tenant)
                .userId(reportLineRequest.getUserId())
                .loginId(reportLineRequest.getLoginId())
                .reportLineName(reportLineRequest.getReportLineName())
                .email(reportLineRequest.getEmail())
                .phone(reportLineRequest.getPhone())
                .sequence(reportLineRequest.getSequence())
                .isDefault(reportLineRequest.isDefault())
                .active(reportLineRequest.isActive())
                .createdBy(reportLine.getCreatedBy())
                .createdDate(reportLine.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }

    public ReportLine getSubmitReportLine(ReportLineRequest reportLineRequest) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        return ReportLine.builder()
                .tenant(tenant)
                .userId(reportLineRequest.getUserId())
                .loginId(reportLineRequest.getLoginId())
                .reportLineName(reportLineRequest.getReportLineName())
                .email(reportLineRequest.getEmail())
                .phone(reportLineRequest.getPhone())
                .sequence(reportLineRequest.getSequence())
                .isDefault(reportLineRequest.isDefault())
                .active(reportLineRequest.isActive())
                .createdBy(AppUtil.getUserName())
                .createdDate(DateTimeUtil.getTimestampUTC())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }

    private Specification<ReportLine> getSpecificationByCondition(ReportLineSearchRequest searchRequest) {
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
    public ReportLineSearchDto searchReportLineListByConditions(ReportLineSearchRequest request, Pageable pageable) {
        Page<ReportLine> reportLinePage = reportLineRepository.findAll(Specification.where(getReportLineSpecificationByCondition(request)), pageable);
        int totalPage = reportLinePage.getTotalPages();
        long total = reportLinePage.getTotalElements();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        List<ReportLineDto> resultList = RequestMapper.INSTANCE.toReportLineListDto(reportLinePage.getContent(), timeZone);

        ReportLineSearchDto reportLineSearchDto = new ReportLineSearchDto();
        reportLineSearchDto.setReportLines(resultList);
        reportLineSearchDto.setTotal(total);
        reportLineSearchDto.setTotalPage(totalPage);
        reportLineSearchDto.setPageSize(pageable.getPageSize());

        return reportLineSearchDto;
    }

    private Specification<ReportLine> getReportLineSpecificationByCondition(ReportLineSearchRequest searchRequest) {
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
                        if ("reportLineName".equalsIgnoreCase(searchField)) {
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
    public ReportLineDto findReportLineByRecId(Integer reportLineId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        Optional<ReportLine> reportLine = reportLineRepository.findReportLineByRecIdAndTenant(reportLineId, tenant.getRecId());
        return reportLine.map(line -> RequestMapper.INSTANCE.toReportLineDto(line, timeZone)).orElse(null);
    }

}
