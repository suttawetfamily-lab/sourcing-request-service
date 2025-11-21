package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.EpAuthClient;
import com.pantavanij.sourcingreq.services.domain.dto.EPAuthDeptApproverDto;
import com.pantavanij.sourcingreq.services.domain.dto.EPAuthUserDTO;
import com.pantavanij.sourcingreq.services.domain.dto.ExcSourcingDeptApproverDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.mapper.ExcSourcingApproverMapper;
import com.pantavanij.sourcingreq.services.domain.request.ApproverSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.DeptApproverSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.EPAuthUserSearchRequest;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthDeptApproverResponse;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthUserListResponse;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import com.pantavanij.sourcingreq.services.util.UserDetailServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.DeptApprovalStatus.*;
import static com.pantavanij.sourcingreq.services.enums.Role.DEPT_APPROVER;

@RequiredArgsConstructor
@Service
@Slf4j
public class ExcSourcingDeptApproverServiceImpl implements ExcSourcingDeptApproverService {
    private final ExcSourcingApproverRepository excSourcingApproverRepository;
    private final ExcSourcingRepository excSourcingRepository;
    private final UaaService uaaService;
    private final TenantConfigService tenantConfigService;
    private final TenantService tenantService;
    private final EpAuthClient epAuthClient;
    private final ApproverRepository approverRepository;
    private final DeptApprovalStatusRepository deptApprovalStatusRepository;
    private final ApproverService approverService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveExcSourcingDeptApprover(ExcSourcing excSourcing, Tenant tenant, List<Integer> approvers) {
        List<ExcSourcingApprover> excSourcingApprovers = new ArrayList<>();
        Integer sequence = 1;

        DeptApprovalStatus deptApprovalStatusNoneObj = deptApprovalStatusRepository.findByName(DEPT_APPROVAL_NONE.code());
        for(Integer approverId : approvers) {
            Optional<Approver> approver = approverRepository.findApproverByTenantAndUserId(tenant, approverId);
            if(approver.isPresent()) {
                ExcSourcingApprover excSourcingApprover = ExcSourcingApprover.builder()
                        .excSourcing(excSourcing)
                        .approver(approver.get())
                        .deptApprovalStatus(sequence != 1 ? deptApprovalStatusNoneObj : excSourcing.getDeptApprovalStatus())
                        .sequence(sequence++)
                        .createdBy(AppUtil.getUserName())
                        .createdDate(DateTimeUtil.getTimestampUTC())
                        .build();
                excSourcingApprovers.add(excSourcingApprover);
            } else {

                //List<ExcSourcingApprover> excSourcingApprovers = new ArrayList<>();
                ApproverSearchRequest excSourcingSearchRequest = getApproverSearchRequest(tenant);
                EPAuthDeptApproverResponse epAuthDeptApproverResponse = approverService.getApproverListByConditions(excSourcingSearchRequest);
                List<EPAuthDeptApproverDto> epAuthDeptApproverList = new ArrayList<>();

                if(epAuthDeptApproverResponse.getData() != null) {
                    epAuthDeptApproverList = epAuthDeptApproverResponse.getData();
                }

                Optional<EPAuthDeptApproverDto> epAuthDeptApproverDto = null;
                if(!epAuthDeptApproverList.isEmpty()) {
                    epAuthDeptApproverDto = epAuthDeptApproverList.stream()
                            .filter(da -> da.getSysUserId().equals(approverId)).findFirst();
                }

                if(epAuthDeptApproverDto != null && epAuthDeptApproverDto.isPresent()) {
                    Optional<Approver> newApprover = approverRepository.findApproverByTenantAndUserId(tenant, approverId);
                    if(newApprover.isPresent()) {
                        ExcSourcingApprover excSourcingApprover = ExcSourcingApprover.builder()
                                .excSourcing(excSourcing)
                                .approver(newApprover.get())
                                .deptApprovalStatus(sequence != 1 ? deptApprovalStatusNoneObj : excSourcing.getDeptApprovalStatus())
                                .sequence(sequence++)
                                .createdBy(AppUtil.getUserName())
                                .createdDate(DateTimeUtil.getTimestampUTC())
                                .build();
                        excSourcingApprovers.add(excSourcingApprover);
                    }
                }
            }
        }

        // Delete excSourcing report line.
        excSourcingApproverRepository.deleteExcSourcingApproverByExcSourcingId(excSourcing.getRecId());

        if (!excSourcingApprovers.isEmpty()) {
            excSourcingApproverRepository.saveAll(excSourcingApprovers);
        }
    }

    public ApproverSearchRequest getApproverSearchRequest(Tenant tenant) {
        ApproverSearchRequest excSourcingSearchRequest = new ApproverSearchRequest();
        excSourcingSearchRequest.setPage(1);
        excSourcingSearchRequest.setPageSize(99999);// Checking passed
        excSourcingSearchRequest.setSortBy("username");
        excSourcingSearchRequest.setSortOrder("desc");
        excSourcingSearchRequest.setTenantId(tenant.getCode());
        return excSourcingSearchRequest;
    }

    @Override
    public List<ExcSourcingDeptApproverDto> findByExcSourcing(Long excSourcingId) {
        List<ExcSourcingDeptApproverDto> excSourcingDeptApproverDtos = new ArrayList<>();
        ExcSourcing excSourcing = excSourcingRepository.findExcSourcingsByRecId(excSourcingId);
        List<ExcSourcingApprover> excSourcingApprovers = excSourcingApproverRepository.findExcSourcingApproversByExcSourcing(excSourcing);
        if(excSourcingApprovers != null && !excSourcingApprovers.isEmpty()) {
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            excSourcingDeptApproverDtos = ExcSourcingApproverMapper.INSTANCE.toExcSourcingDeptApproverDtoList(excSourcingApprovers, timeZone);
            excSourcingDeptApproverDtos.stream()
                    .peek(excSourcingDeptApproverDto -> {
                        excSourcingDeptApproverDto.setCreatedByName(UserDetailServiceUtil.getFullName(excSourcingDeptApproverDto.getCreatedBy()));
                    }).collect(Collectors.toList());
        }
        return excSourcingDeptApproverDtos;
    }

    @Override
    public ExcSourcingDeptApproverDto findCurrentAwaitingDeptApprover(Long excSourcingId) {
        ExcSourcingDeptApproverDto excSourcingDeptApproverDto = null;
        Optional<ExcSourcingApprover> excSourcingApprover = excSourcingApproverRepository.findCurrentAwaitingDeptApprover(excSourcingId);
        if(excSourcingApprover.isPresent() ) {
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            excSourcingDeptApproverDto = ExcSourcingApproverMapper.INSTANCE.toExcSourcingDeptApproverDto(excSourcingApprover.get(), timeZone);
            excSourcingDeptApproverDto.setCreatedByName(UserDetailServiceUtil.getFullName(excSourcingDeptApproverDto.getCreatedBy()));
        }
        return excSourcingDeptApproverDto;
    }

    @Override
    public ExcSourcingDeptApproverDto findLatestApprovedDeptApprover(Long excSourcingId) {
        ExcSourcingDeptApproverDto excSourcingDeptApproverDto = null;
        Optional<ExcSourcingApprover> excSourcingApprover = excSourcingApproverRepository.findLatestApprovedDeptApprover(excSourcingId);
        if(excSourcingApprover.isPresent() ) {
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            excSourcingDeptApproverDto = ExcSourcingApproverMapper.INSTANCE.toExcSourcingDeptApproverDto(excSourcingApprover.get(), timeZone);
            excSourcingDeptApproverDto.setCreatedByName(UserDetailServiceUtil.getFullName(excSourcingDeptApproverDto.getCreatedBy()));
        }
        return excSourcingDeptApproverDto;
    }

    @Override
    public ExcSourcingDeptApproverDto find1StCancelledDeptApprover(Long excSourcingId) {
        ExcSourcingDeptApproverDto excSourcingDeptApproverDto = null;
        Optional<ExcSourcingApprover> excSourcingApprover = excSourcingApproverRepository.find1StCancelledDeptApprover(excSourcingId);
        if(excSourcingApprover.isPresent() ) {
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            excSourcingDeptApproverDto = ExcSourcingApproverMapper.INSTANCE.toExcSourcingDeptApproverDto(excSourcingApprover.get(), timeZone);
            excSourcingDeptApproverDto.setCreatedByName(UserDetailServiceUtil.getFullName(excSourcingDeptApproverDto.getCreatedBy()));
        }
        return excSourcingDeptApproverDto;
    }

    @Override
    public List<ExcSourcingDeptApproverDto> findAllApprovedDeptApprover(Long excSourcingId) {
        List<ExcSourcingDeptApproverDto> excSourcingDeptApproverDtos = new ArrayList<>();
        ExcSourcing excSourcing = excSourcingRepository.findExcSourcingsByRecId(excSourcingId);
        DeptApprovalStatus deptApprovalStatusNoneObj = deptApprovalStatusRepository.findByName(DEPT_APPROVAL_APPROVED.code());
        List<ExcSourcingApprover> excSourcingApprovers = excSourcingApproverRepository.findExcSourcingApproversByExcSourcingAndDeptApprovalStatus(excSourcing, deptApprovalStatusNoneObj);
        if(excSourcingApprovers != null && !excSourcingApprovers.isEmpty()) {
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            excSourcingDeptApproverDtos = ExcSourcingApproverMapper.INSTANCE.toExcSourcingDeptApproverDtoList(excSourcingApprovers, timeZone);
            excSourcingDeptApproverDtos.stream()
                    .peek(excSourcingDeptApproverDto -> {
                        excSourcingDeptApproverDto.setCreatedByName(UserDetailServiceUtil.getFullName(excSourcingDeptApproverDto.getCreatedBy()));
                    }).collect(Collectors.toList());
        }
        return excSourcingDeptApproverDtos;
    }

    @Override
    public List<Long> findAllRelatedRequestIds() {
        List<Long> excSourcingIds = new ArrayList<>();


        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Optional<Approver> approver = approverRepository.findApproverByTenantAndLoginId(tenant, AppUtil.getUserName());
        if(approver.isPresent()) {
            List<ExcSourcingApprover> excSourcingApproverList = excSourcingApproverRepository.findExcSourcingApproverByApprover(approver.get());

            if (excSourcingApproverList != null && !excSourcingApproverList.isEmpty()) {
                for (ExcSourcingApprover excSourcingApprover : excSourcingApproverList) {
                    excSourcingIds.add(excSourcingApprover.getExcSourcing().getRecId());
                }
            }
        }

        return excSourcingIds;
    }

    @Override
    public EPAuthDeptApproverResponse getDeptApproverListByConditions(DeptApproverSearchRequest excSourcing) {
        EPAuthUserSearchRequest epAuthUserSearchRequest = EPAuthUserSearchRequest.builder()
                .conditionSearchList(excSourcing.getConditionSearchList())
                .tenantId(excSourcing.getTenantId())
                .page(excSourcing.getPage())
                .pageSize(excSourcing.getPageSize())
                .sortBy(excSourcing.getSortBy())
                .sortOrder(excSourcing.getSortOrder())
                .build();

//        String tenantCode = AppUtil.getTenantId();
//        Tenant tenant = tenantService.findByCode(tenantCode);
        String[] privilegeCode = new String[] {DEPT_APPROVER.privilegeCode()};
//        String privilegeCode = tenantConfigService.getEPAuthPrivilegeCode(tenant.getRecId(), Role.DEPT_APPROVER.roleName());
        EPAuthUserListResponse epAuthUserListResponse = new EPAuthUserListResponse();
        try {
            epAuthUserListResponse = epAuthClient.getEPAuthUserList(privilegeCode, epAuthUserSearchRequest);
        } catch(Exception ex) {
            ex.getMessage();
        }
        List<EPAuthDeptApproverDto> epAuthDeptApproverDtoList = new ArrayList<>();

        if (epAuthUserListResponse.getData() == null) {
            return EPAuthDeptApproverResponse.builder()
                    .data(null)
                    .page(epAuthUserListResponse.getPage())
                    .pageSize(epAuthUserListResponse.getPageSize())
                    .total(epAuthUserListResponse.getTotal())
                    .totalPage(epAuthUserListResponse.getTotalPage())
                    .build();
        }

        List<EPAuthUserDTO> epAuthUserList = epAuthUserListResponse.getData();


        // Sort data by fullname
        epAuthUserList.sort(Comparator.comparing(EPAuthUserDTO::getFullName));

        for (EPAuthUserDTO epAuthUserDTO: epAuthUserList) {
            epAuthDeptApproverDtoList.add(
                    EPAuthDeptApproverDto.builder()
                            .sysUserId(epAuthUserDTO.getSysUserId())
                            .loginId(epAuthUserDTO.getLoginId())
                            .fullName(epAuthUserDTO.getFullName())
                            .email(epAuthUserDTO.getEmail())
                            .mobilePhone(epAuthUserDTO.getMobilePhone())
                            .phone(epAuthUserDTO.getPhone())
                            .timezone(epAuthUserDTO.getTimezone())
                            .build());
        }
        return EPAuthDeptApproverResponse.builder()
                .data(epAuthDeptApproverDtoList)
                .page(epAuthUserListResponse.getPage())
                .pageSize(epAuthUserListResponse.getPageSize())
                .total(epAuthUserListResponse.getTotal())
                .totalPage(epAuthUserListResponse.getTotalPage())
                .build();
    }

    @Override
    public EPAuthDeptApproverResponse getDeptApproverListByConditions(DeptApproverSearchRequest excSourcing, String[] privilegeCodes) {
        EPAuthUserSearchRequest epAuthUserSearchRequest = EPAuthUserSearchRequest.builder()
                .conditionSearchList(excSourcing.getConditionSearchList())
                .tenantId(excSourcing.getTenantId())
                .page(excSourcing.getPage())
                .pageSize(excSourcing.getPageSize())
                .sortBy(excSourcing.getSortBy())
                .sortOrder(excSourcing.getSortOrder())
                .build();

        EPAuthUserListResponse epAuthUserListResponse = new EPAuthUserListResponse();
        try {
            epAuthUserListResponse = epAuthClient.getEPAuthUserList(privilegeCodes, epAuthUserSearchRequest);
        } catch(Exception ex) {
            ex.getMessage();
        }
        List<EPAuthDeptApproverDto> epAuthDeptApproverDtoList = new ArrayList<>();

        if (epAuthUserListResponse.getData() == null) {
            return EPAuthDeptApproverResponse.builder()
                    .data(null)
                    .page(epAuthUserListResponse.getPage())
                    .pageSize(epAuthUserListResponse.getPageSize())
                    .total(epAuthUserListResponse.getTotal())
                    .totalPage(epAuthUserListResponse.getTotalPage())
                    .build();
        }

        for (EPAuthUserDTO epAuthUserDTO: epAuthUserListResponse.getData()) {
            epAuthDeptApproverDtoList.add(
                    EPAuthDeptApproverDto.builder()
                            .sysUserId(epAuthUserDTO.getSysUserId())
                            .loginId(epAuthUserDTO.getLoginId())
                            .fullName(epAuthUserDTO.getFullName())
                            .email(epAuthUserDTO.getEmail())
                            .mobilePhone(epAuthUserDTO.getMobilePhone())
                            .phone(epAuthUserDTO.getPhone())
                            .timezone(epAuthUserDTO.getTimezone())
                            .build());
        }

        epAuthDeptApproverDtoList.sort(Comparator.comparing(EPAuthDeptApproverDto::getFullName));
        return EPAuthDeptApproverResponse.builder()
                .data(epAuthDeptApproverDtoList)
                .page(epAuthUserListResponse.getPage())
                .pageSize(epAuthUserListResponse.getPageSize())
                .total(epAuthUserListResponse.getTotal())
                .totalPage(epAuthUserListResponse.getTotalPage())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByRequestId(Long excSourcingId) {
        log.info("Delete all Dept Approver by Request ID : {}", excSourcingId);
        excSourcingApproverRepository.deleteExcSourcingApproverByExcSourcingId(excSourcingId);
    }

    @Override
    public boolean hasApprovalPermission(ExcSourcingApprover excSourcingApprover) {
        boolean result = false;
        DeptApprovalStatus deptApproverStatus = excSourcingApprover.getDeptApprovalStatus();
        if(deptApproverStatus.getName().equalsIgnoreCase(DEPT_APPROVAL_AWAITING.code())) {
            result = true;
        }
        return result;
    }

    @Override
    public boolean isLastDeptApprover(Long excSourcingId) {
        boolean result = false;
        ExcSourcing excSourcing = excSourcingRepository.findExcSourcingsByRecId(excSourcingId);

        DeptApprovalStatus deptApprovalStatusAwaitingObj = deptApprovalStatusRepository.findByName(DEPT_APPROVAL_AWAITING.code());
        List<ExcSourcingApprover> excSourcingApprovers = excSourcingApproverRepository.findExcSourcingApproversByExcSourcingAndDeptApprovalStatus(excSourcing, deptApprovalStatusAwaitingObj);

        if(excSourcingApprovers == null || excSourcingApprovers.isEmpty()) {
            result = true;
        }
        return result;
    }

}
