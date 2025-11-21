package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.EpAuthClient;
import com.pantavanij.sourcingreq.services.domain.dto.RequestReportDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestReportSearchDto;
import com.pantavanij.sourcingreq.services.domain.dto.TenantRequestReportDto;
import com.pantavanij.sourcingreq.services.domain.dto.UserDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.TenantRequestReportKey;
import com.pantavanij.sourcingreq.services.domain.mapper.RequestReportMapper;
import com.pantavanij.sourcingreq.services.domain.request.ConditionSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestReportRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestReportSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.SequenceRequest;
import com.pantavanij.sourcingreq.services.domain.response.RequestReportResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.DataNotFoundException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestReportRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantRequestReportRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantSectionDetailRepository;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.SearchRequestReportType.*;
import static com.pantavanij.sourcingreq.services.util.Constant.REC_ID;
import static com.pantavanij.sourcingreq.services.util.Constant.TENANT;


@Slf4j
@RequiredArgsConstructor
@Service
public class RequestReportServiceImpl implements RequestReportService {

    private final RequestReportRepository requestReportRepository;
    private final UaaService uaaService;
    private final EPAuthService epAuthService;
    private final TenantService tenantService;
    private final TenantConfigService tenantConfigService;
    private final EpAuthClient epAuthClient;
    private final TenantSectionDetailRepository tenantSectionDetailRepository;
    private final TenantRequestReportRepository tenantRequestReportRepository;


    @Override
    public RequestReportResponse getRequestReport(RequestReportSearchRequest request, Pageable pageable) {
        Page<RequestReport> requestRequestReports = requestReportRepository.findAll(Specification.where(getSpecificationByCondition(request)), pageable);
        int totalPage = requestRequestReports.getTotalPages();
        long total = requestRequestReports.getTotalElements();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);

        List<RequestReportDto> requestRequestReportDtos = RequestReportMapper.INSTANCE.toRequestReportDtoList(requestRequestReports.getContent(), timeZone)
                .stream()
                .distinct()
                .collect(Collectors.toList());

        return RequestReportResponse.builder()
                .total(total)
                .totalPage(totalPage)
                .data(requestRequestReportDtos)
                .pageSize(requestRequestReportDtos.size())
                .build();
    }
    
    @Override
    public Integer saveRequestReport(RequestReportDto requestReportDto) {
        UserDto userDto = AppUtil.getUser();
        Tenant tenant = tenantService.findByCode(userDto.getTenantId());
        log.info("Save RequestReport for Code : {}, Name: {}", requestReportDto.getCode(), requestReportDto.getName());
        RequestReport requestReport = requestReportRepository.findRequestReportByRecIdAndTenant(requestReportDto.getRecId(), tenant.getRecId())
                .orElse(new RequestReport());

//        requestReport.setPhone(requestReportDto.getPhone());
//        requestReport.setEmail(requestReportDto.getEmail());
//        requestReport.setUserId(requestReportDto.getUserId());
//        requestReport.setRequestReportName(requestReportDto.getRequestReportName());
//        requestReport.setLoginId(requestReportDto.getLoginId());
        requestReport.setTenant(tenant);
        requestReport.setUpdatedBy(userDto.getUsername());
        requestReport.setUpdatedDate(DateTimeUtil.getTimestampUTC());


        if (StringUtils.isEmpty(requestReport.getCreatedBy())) {
            requestReport.setCreatedBy(userDto.getUsername());
        }
        if (requestReport.getCreatedDate() == null) {
            requestReport.setCreatedDate(DateTimeUtil.getTimestampUTC());
        }
        return requestReportRepository.save(requestReport).getRecId();
    }

//    @Override
//    public EPAuthDeptRequestReportResponse getRequestReportListByConditions(RequestReportSearchRequest request) {
//        EPAuthUserSearchRequest epAuthUserSearchRequest = EPAuthUserSearchRequest.builder()
//                .conditionSearchList(request.getConditionSearchList())
//                .tenantId(request.getTenantId())
//                .page(request.getPage())
//                .pageSize(request.getPageSize())
//                .sortBy(request.getSortBy())
//                .sortOrder(request.getSortOrder())
//                .build();
//
////        String tenantCode = AppUtil.getTenantId();
////        Tenant tenant = tenantService.findByCode(tenantCode);
//        String[] privilegeCode = new String[] {DEPT_APPROVER.privilegeCode()};
////        String privilegeCode = tenantConfigService.getEPAuthPrivilegeCode(tenant.getRecId(), DEPT_APPROVER.roleName());
//        EPAuthUserListResponse epAuthUserListResponse = new EPAuthUserListResponse();
//        try {
//            epAuthUserListResponse = epAuthClient.getEPAuthUserList(privilegeCode, epAuthUserSearchRequest);
//        } catch(Exception ex) {
//            ex.getMessage();
//        }
//
//        List<EPAuthDeptRequestReportDto> epAuthDeptRequestReportDtoList = new ArrayList();
//
//        if (epAuthUserListResponse.getData() == null) {
//            return EPAuthDeptRequestReportResponse.builder()
//                    .data(null)
//                    .page(epAuthUserListResponse.getPage())
//                    .pageSize(epAuthUserListResponse.getPageSize())
//                    .total(epAuthUserListResponse.getTotal())
//                    .totalPage(epAuthUserListResponse.getTotalPage())
//                    .build();
//        }
//
//        for (EPAuthUserDTO epAuthUserDTO: epAuthUserListResponse.getData()) {
//            RequestReportDto saveRequestReport = new RequestReportDto();
//            saveRequestReport.setPhone(epAuthUserDTO.getPhone());
//            saveRequestReport.setEmail(epAuthUserDTO.getEmail());
//            saveRequestReport.setUserId(epAuthUserDTO.getSysUserId());
//            saveRequestReport.setRequestReportName(epAuthUserDTO.getFullName());
//            saveRequestReport.setLoginId(epAuthUserDTO.getLoginId());
//            this.saveRequestReport(saveRequestReport);
//
//            epAuthDeptRequestReportDtoList.add(
//                    EPAuthDeptRequestReportDto.builder()
//                            .sysUserId(epAuthUserDTO.getSysUserId())
//                            .loginId(epAuthUserDTO.getLoginId())
//                            .fullName(epAuthUserDTO.getFullName())
//                            .email(epAuthUserDTO.getEmail())
//                            .mobilePhone(epAuthUserDTO.getMobilePhone())
//                            .phone(epAuthUserDTO.getPhone())
//                            .timezone(epAuthUserDTO.getTimezone())
//                            .build());
//        }
//        return EPAuthDeptRequestReportResponse.builder()
//                .data(epAuthDeptRequestReportDtoList)
//                .page(epAuthUserListResponse.getPage())
//                .pageSize(epAuthUserListResponse.getPageSize())
//                .total(epAuthUserListResponse.getTotal())
//                .totalPage(epAuthUserListResponse.getTotalPage())
//                .build();
//    }


    private Specification<RequestReport> getSpecificationByCondition(RequestReportSearchRequest searchRequest) {
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
    public RequestReportSearchDto searchRequestReportListByCondition(RequestReportSearchRequest requestReportSearchRequest, Pageable pageable) {
        Page<RequestReport> requestReportPage = requestReportRepository.findAll(Specification.where(getSearchSpecificationByCondition(requestReportSearchRequest)), pageable);
        int totalPage = requestReportPage.getTotalPages();
        long total = requestReportPage.getTotalElements();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);

        List<RequestReportDto> requestReportDtoList = RequestReportMapper.INSTANCE.toRequestReportDtoList(requestReportPage.getContent(), timeZone);
        RequestReportSearchDto requestReportSearchDto = new RequestReportSearchDto();
        requestReportSearchDto.setRequestReportDtoList(requestReportDtoList);
        requestReportSearchDto.setTotal(total);
        requestReportSearchDto.setTotalPage(totalPage);
        requestReportSearchDto.setPage(pageable.getPageNumber());
        return requestReportSearchDto;
    }

    private Specification<RequestReport> getSearchSpecificationByCondition(RequestReportSearchRequest requestReportSearchRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
            if (tenant != null) {
                predicates.add(criteriaBuilder.equal(root.get(TENANT).get(REC_ID), tenant.getRecId()));
            }

            List<ConditionSearchRequest> conditionSearchRequestList = requestReportSearchRequest.getConditionSearchList();
            if (conditionSearchRequestList != null && !conditionSearchRequestList.isEmpty()) {
                for (ConditionSearchRequest condition : conditionSearchRequestList) {
                    String searchField = condition.getSearchField();
                    String searchValue = condition.getSearchValue();

                    if (StringUtils.isNotEmpty(searchField) && StringUtils.isNotEmpty(searchValue)) {
                        if (CODE.description().equalsIgnoreCase(searchField)) {
                            predicates.add(criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%"));
                        } else if (NAME.description().equalsIgnoreCase(searchField)) {
                            predicates.add(criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%"));
                        } else if (CREATED_BY.description().equalsIgnoreCase(searchField)) {
                            predicates.add(criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%"));
                        } else {
                            if (CommonUtils.isNumeric(searchValue)) {
                                predicates.add(criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%"));
                            } else {
                                predicates.add(criteriaBuilder.like(root.get(searchField), "%" + searchValue.toLowerCase() + "%"));
                            }
                        }
                    }
                }
            }

            query.distinct(true);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public RequestReportDto findRequestReportByRecId(Integer requestReportId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        Optional<RequestReport> requestReports = requestReportRepository.findRequestReportByRecIdAndTenant(requestReportId, tenant.getRecId());
        return requestReports.map(requestReport -> RequestReportMapper.INSTANCE.toRequestReportDto(requestReport, timeZone)).orElse(null);
    }

    @Override
    public RequestReportDto createRequestReport(RequestReportRequest requestReportRequest) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());

        Optional<RequestReport> maxSequenceRequestReport = requestReportRepository.findFirstByTenantRecIdOrderBySequenceDesc(tenant.getRecId());
        if (maxSequenceRequestReport.isPresent()) {
            requestReportRequest.setSequence(maxSequenceRequestReport.get().getSequence() + 1);
        } else {
            requestReportRequest.setSequence(1);
        }

        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        RequestReport requestReportSubmit = requestReportRepository.save(getSubmitRequestReport(requestReportRequest));

        List<TenantRequestReport> tenantRequestReports = getSubmitTenantRequestReportList(requestReportRequest, requestReportSubmit);
        requestReportSubmit.setTenantRequestReportList(tenantRequestReports);
        RequestReport requestReportAndTenantRequestReportSubmit = requestReportRepository.save(requestReportSubmit);

        return RequestReportMapper.INSTANCE.toRequestReportDto(requestReportAndTenantRequestReportSubmit, timeZone);
    }



    @Transactional
    @Override
    public RequestReportDto updateRequestReport(RequestReportRequest requestReportRequest) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());

        // ดึงข้อมูล RequestReport เดิม
        RequestReport existingRequestReport = requestReportRepository.findById(requestReportRequest.getId())
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("RequestReport id %d not found", requestReportRequest.getId())
                ));


        // ดึง TenantRequestReport เดิมทั้งหมดที่เกี่ยวข้อง
        List<TenantRequestReport> existingTenantRequestReports = existingRequestReport.getTenantRequestReportList();

        // สร้าง TenantRequestReport ใหม่จาก Request
        List<TenantRequestReport> newTenantRequestReports = getSubmitTenantRequestReportList(requestReportRequest, existingRequestReport);

        // 1. ลบ TenantRequestReport ที่ไม่มีใน Request ใหม่
        List<TenantRequestReport> toDelete = existingTenantRequestReports.stream()
                .filter(oldItem -> newTenantRequestReports.stream()
                        .noneMatch(newItem -> newItem.getTenantSectionDetail().getId().equals(oldItem.getTenantSectionDetail().getId()))
                ).collect(Collectors.toList());

        // ลบออกจากฐานข้อมูล (ตรวจสอบให้แน่ใจว่าเราใช้ delete หรือ remove)
        for (TenantRequestReport item : toDelete) {
            tenantRequestReportRepository.deleteTenantRequestReportByIds(tenant.getRecId(), item.getRequestReport().getRecId(), item.getTenantSectionDetail().getId().intValue());
        }

        // โหลดข้อมูล RequestReport ใหม่หลังจากทำการลบ
        existingRequestReport = requestReportRepository.findById(requestReportRequest.getId())
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("RequestReport id %d not found", requestReportRequest.getId())
                ));

        // 2. อัปเดตหรือเพิ่ม TenantRequestReport ใหม่
        for (TenantRequestReport newItem : newTenantRequestReports) {
            Optional<TenantRequestReport> existingItem = existingTenantRequestReports.stream()
                    .filter(oldItem -> oldItem.getTenantSectionDetail().getId().equals(newItem.getTenantSectionDetail().getId()))
                    .findFirst();

            if (existingItem.isPresent()) {
                // อัปเดตค่าใน TenantRequestItemReport ที่มีอยู่
                TenantRequestReport oldItem = existingItem.get();
                oldItem.setSequence(newItem.getSequence());
                oldItem.setDefault(newItem.isDefault());
                oldItem.setActive(newItem.isActive());
                oldItem.setUpdatedBy(AppUtil.getUserName());
                oldItem.setUpdatedDate(DateTimeUtil.getTimestampUTC());
            } else {
                // เพิ่ม TenantRequestItemReport ใหม่
                existingTenantRequestReports.add(newItem);
            }
        }

        // อัปเดตข้อมูลใน RequestReport
        existingRequestReport.setCode(requestReportRequest.getCode());
        existingRequestReport.setName(requestReportRequest.getName());
        existingRequestReport.setPrivilegeCode(requestReportRequest.getPrivilegeCode());
        existingRequestReport.setSequence(requestReportRequest.getSequence());
        existingRequestReport.setDefault(requestReportRequest.isDefault());
        existingRequestReport.setActive(requestReportRequest.isActive());
        existingRequestReport.setUpdatedBy(AppUtil.getUserName());
        existingRequestReport.setUpdatedDate(DateTimeUtil.getTimestampUTC());

        // บันทึกข้อมูล
        RequestReport updatedRequestReport = requestReportRepository.save(existingRequestReport);

        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        return RequestReportMapper.INSTANCE.toRequestReportDto(updatedRequestReport, timeZone);
    }



    @Transactional
    @Override
    public boolean deleteRequestReport(Integer requestReportId) {
        try {
            // ดึงข้อมูล RequestReport ตาม ID
            Optional<RequestReport> optionalRequestReport = requestReportRepository.findById(requestReportId);
            if (optionalRequestReport.isEmpty()) {
                return false;
            }

            RequestReport requestReport = optionalRequestReport.get();

            // ลบ TenantRequestReport ทั้งหมดที่เกี่ยวข้อง
            List<TenantRequestReport> tenantRequestReports = requestReport.getTenantRequestReportList();
            if (!tenantRequestReports.isEmpty()) {
                tenantRequestReportRepository.deleteAll(tenantRequestReports);
            }

            // ลบ RequestReport
            requestReportRepository.delete(requestReport);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public RequestReportDto updateRequestReportSequence(SequenceRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Optional<RequestReport> requestReportOptional = requestReportRepository.findRequestReportByRecIdAndTenant(request.getRecId(), tenant.getRecId());
        if (requestReportOptional.isPresent()) {
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            requestReportRepository.reOrderOtherRequestReportSequence(tenant.getRecId(), requestReportOptional.get().getSequence(), request.getSequence());
            RequestReport requestReportUpdateSequence = requestReportRepository.save(getRequestReportUpdateSequence(requestReportOptional.get(), request, tenant));
            return RequestReportMapper.INSTANCE.toRequestReportDto(requestReportUpdateSequence, timeZone);
        }
        return null;
    }

    public RequestReport getRequestReportUpdateSequence(RequestReport requestReport, SequenceRequest request, Tenant tenant) {
        return RequestReport.builder()
                .recId(requestReport.getRecId())
                .tenant(tenant)
//                .userId(requestReport.getUserId())
//                .loginId(requestReport.getLoginId())
//                .requestReportName(requestReport.getRequestReportName())
//                .email(requestReport.getEmail())
//                .phone(requestReport.getPhone())
                .sequence(request.getSequence())
                .isDefault(requestReport.isDefault())
                .active(requestReport.isActive())
                .createdBy(requestReport.getCreatedBy())
                .createdDate(requestReport.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }

    public RequestReport getRequestReportUpdate(RequestReportRequest requestReportRequest, RequestReport requestReport) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        return RequestReport.builder()
                .recId(requestReportRequest.getId())
                .tenant(tenant)
                .code(requestReportRequest.getCode())
                .name(requestReportRequest.getName())
                .privilegeCode(requestReportRequest.getPrivilegeCode())
                .sequence(requestReportRequest.getSequence() != null ? requestReportRequest.getSequence() : requestReport.getSequence())
                .isDefault(requestReportRequest.isDefault())
                .active(requestReportRequest.isActive())
                .createdBy(AppUtil.getUserName())
                .createdDate(DateTimeUtil.getTimestampUTC())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .tenantRequestReportList(Collections.emptyList())
                .build();
    }


    public RequestReport getSubmitRequestReport(RequestReportRequest requestReportRequest) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        RequestReport requestReport = RequestReport.builder()
                .recId(requestReportRequest.getId())
                .tenant(tenant)
                .code(requestReportRequest.getCode())
                .name(requestReportRequest.getName())
                .privilegeCode(requestReportRequest.getPrivilegeCode())
                .sequence(requestReportRequest.getSequence())
                .isDefault(requestReportRequest.isDefault())
                .active(requestReportRequest.isActive())
                .createdBy(AppUtil.getUserName())
                .createdDate(DateTimeUtil.getTimestampUTC())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .tenantRequestReportList(Collections.emptyList())
                .build();
        return requestReport;
    }

    public List<TenantRequestReport> getSubmitTenantRequestReportList(RequestReportRequest requestReportRequest, RequestReport requestReport) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        List<TenantRequestReportDto> requestTenantRequestReportList = requestReportRequest.getTenantRequestReportList();
        List<TenantRequestReport> tenantRequestReports = new ArrayList<>();

        for (TenantRequestReportDto requestTenantRequestReport : requestTenantRequestReportList) {
            Optional<TenantSectionDetail> tenantSectionDetail = tenantSectionDetailRepository.findFirstByTenantAndId(
                    tenant, requestTenantRequestReport.getTenantSectionDetail().getValue()
            );

            if (tenantSectionDetail.isPresent()) {
                TenantRequestReportKey id = TenantRequestReportKey.builder()
                        .tenantId(tenant.getRecId())
                        .requestReportId(requestReport.getRecId())
                        .tenantSectionDetailId(tenantSectionDetail.get().getId())
                        .build();

                TenantRequestReport tenantRequestReport = TenantRequestReport.builder()
                        .id(id)
                        .tenant(tenant)
                        .requestReport(requestReport)
                        .tenantSectionDetail(tenantSectionDetail.get())
                        .sequence(requestTenantRequestReport.getSequence())
                        .isDefault(requestTenantRequestReport.getIsDefault())
                        .active(requestTenantRequestReport.getActive())
                        .createdBy(AppUtil.getUserName())
                        .createdDate(DateTimeUtil.getTimestampUTC())
                        .updatedBy(AppUtil.getUserName())
                        .updatedDate(DateTimeUtil.getTimestampUTC())
                        .build();

                tenantRequestReports.add(tenantRequestReport);
            }
        }

        return tenantRequestReports;
    }







}
