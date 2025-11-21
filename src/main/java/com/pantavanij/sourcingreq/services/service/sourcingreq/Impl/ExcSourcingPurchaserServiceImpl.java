package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.EpAuthClient;
import com.pantavanij.sourcingreq.services.domain.dto.EPAuthPurchaserDto;
import com.pantavanij.sourcingreq.services.domain.dto.EPAuthUserDTO;
import com.pantavanij.sourcingreq.services.domain.dto.ExcSourcingPurchaserDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.mapper.ExcSourcingPurchaserMapper;
import com.pantavanij.sourcingreq.services.domain.request.EPAuthUserSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.PurchaserSearchRequest;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthPurchaserResponse;
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
public class ExcSourcingPurchaserServiceImpl implements ExcSourcingPurchaserService {
    private final ExcSourcingPurchaserRepository excSourcingPurchaserRepository;
    private final ExcSourcingRepository excSourcingRepository;
    private final UaaService uaaService;
    private final TenantConfigService tenantConfigService;
    private final TenantService tenantService;
    private final EpAuthClient epAuthClient;
    private final ApproverRepository approverRepository;
    private final DeptApprovalStatusRepository deptApprovalStatusRepository;
    private final PurchaserService purchaserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveExcSourcingPurchaser(ExcSourcing excSourcing, Tenant tenant, List<Integer> purchasers) {
        List<ExcSourcingPurchaser> excSourcingPurchasers = new ArrayList<>();
        Integer sequence = 1;

        DeptApprovalStatus deptApprovalStatusNoneObj = deptApprovalStatusRepository.findByName(DEPT_APPROVAL_NONE.code());
        for(Integer purchaserId : purchasers) {
            Optional<Approver> approver = approverRepository.findApproverByTenantAndUserId(tenant, purchaserId);
            if(approver.isPresent()) {
                ExcSourcingPurchaser excSourcingPurchaser = ExcSourcingPurchaser.builder()
                        .excSourcing(excSourcing)
                        .approver(approver.get())
                        .approvalStatus(sequence != 1 ? deptApprovalStatusNoneObj : excSourcing.getDeptApprovalStatus())
                        .sequence(sequence++)
                        .createdBy(AppUtil.getUserName())
                        .createdDate(DateTimeUtil.getTimestampUTC())
                        .build();
                excSourcingPurchasers.add(excSourcingPurchaser);
            } else {

                //List<ExcSourcingPurchaser> excSourcingPurchasers = new ArrayList<>();
                PurchaserSearchRequest excSourcingSearchRequest = getPurchaserSearchRequest(tenant);
                EPAuthPurchaserResponse epAuthPurchaserResponse = purchaserService.getPurchaserListByConditions(excSourcingSearchRequest);
                List<EPAuthPurchaserDto> epAuthPurchaserList = new ArrayList<>();

                if(epAuthPurchaserResponse.getData() != null) {
                    epAuthPurchaserList = epAuthPurchaserResponse.getData();
                }

                Optional<EPAuthPurchaserDto> epAuthPurchaserDto = null;
                if(!epAuthPurchaserList.isEmpty()) {
                    epAuthPurchaserDto = epAuthPurchaserList.stream()
                            .filter(da -> da.getSysUserId().equals(purchaserId)).findFirst();
                }

                if(epAuthPurchaserDto != null && epAuthPurchaserDto.isPresent()) {
                    Optional<Approver> newApprover = approverRepository.findApproverByTenantAndUserId(tenant, purchaserId);
                    if(newApprover.isPresent()) {
                        ExcSourcingPurchaser excSourcingPurchaser = ExcSourcingPurchaser.builder()
                                .excSourcing(excSourcing)
                                .approver(newApprover.get())
                                .approvalStatus(sequence != 1 ? deptApprovalStatusNoneObj : excSourcing.getDeptApprovalStatus())
                                .sequence(sequence++)
                                .createdBy(AppUtil.getUserName())
                                .createdDate(DateTimeUtil.getTimestampUTC())
                                .build();
                        excSourcingPurchasers.add(excSourcingPurchaser);
                    }
                }
            }
        }

        // Delete excSourcing report line.
        excSourcingPurchaserRepository.deleteExcSourcingPurchaserByExcSourcingId(excSourcing.getRecId());

        if (!excSourcingPurchasers.isEmpty()) {
            excSourcingPurchaserRepository.saveAll(excSourcingPurchasers);
        }
    }

    public PurchaserSearchRequest getPurchaserSearchRequest(Tenant tenant) {
        PurchaserSearchRequest excSourcingSearchRequest = new PurchaserSearchRequest();
        excSourcingSearchRequest.setPage(1);
        excSourcingSearchRequest.setPageSize(99999);// Checking passed
        excSourcingSearchRequest.setSortBy("username");
        excSourcingSearchRequest.setSortOrder("desc");
        excSourcingSearchRequest.setTenantId(tenant.getCode());
        return excSourcingSearchRequest;
    }

    @Override
    public List<ExcSourcingPurchaserDto> findByExcSourcing(Long excSourcingId) {
        List<ExcSourcingPurchaserDto> excSourcingPurchaserDtos = new ArrayList<>();
        ExcSourcing excSourcing = excSourcingRepository.findExcSourcingsByRecId(excSourcingId);
        List<ExcSourcingPurchaser> excSourcingPurchasers = excSourcingPurchaserRepository.findExcSourcingPurchasersByExcSourcing(excSourcing);
        if(excSourcingPurchasers != null && !excSourcingPurchasers.isEmpty()) {
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            excSourcingPurchaserDtos = ExcSourcingPurchaserMapper.INSTANCE.toExcSourcingPurchaserDtoList(excSourcingPurchasers, timeZone);
            excSourcingPurchaserDtos.stream()
                    .peek(excSourcingPurchaserDto -> {
                        excSourcingPurchaserDto.setCreatedByName(UserDetailServiceUtil.getFullName(excSourcingPurchaserDto.getCreatedBy()));
                    }).collect(Collectors.toList());
        }
        return excSourcingPurchaserDtos;
    }

    @Override
    public ExcSourcingPurchaserDto findCurrentAwaitingPurchaser(Long excSourcingId) {
        ExcSourcingPurchaserDto excSourcingPurchaserDto = null;
        Optional<ExcSourcingPurchaser> excSourcingPurchaser = excSourcingPurchaserRepository.findCurrentAwaitingPurchaser(excSourcingId);
        if(excSourcingPurchaser.isPresent() ) {
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            excSourcingPurchaserDto = ExcSourcingPurchaserMapper.INSTANCE.toExcSourcingPurchaserDto(excSourcingPurchaser.get(), timeZone);
            excSourcingPurchaserDto.setCreatedByName(UserDetailServiceUtil.getFullName(excSourcingPurchaserDto.getCreatedBy()));
        }
        return excSourcingPurchaserDto;
    }

    @Override
    public ExcSourcingPurchaserDto findLatestApprovedPurchaser(Long excSourcingId) {
        ExcSourcingPurchaserDto excSourcingPurchaserDto = null;
        Optional<ExcSourcingPurchaser> excSourcingPurchaser = excSourcingPurchaserRepository.findLatestApprovedPurchaser(excSourcingId);
        if(excSourcingPurchaser.isPresent() ) {
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            excSourcingPurchaserDto = ExcSourcingPurchaserMapper.INSTANCE.toExcSourcingPurchaserDto(excSourcingPurchaser.get(), timeZone);
            excSourcingPurchaserDto.setCreatedByName(UserDetailServiceUtil.getFullName(excSourcingPurchaserDto.getCreatedBy()));
        }
        return excSourcingPurchaserDto;
    }

    @Override
    public ExcSourcingPurchaserDto find1StCancelledPurchaser(Long excSourcingId) {
        ExcSourcingPurchaserDto excSourcingPurchaserDto = null;
        Optional<ExcSourcingPurchaser> excSourcingPurchaser = excSourcingPurchaserRepository.find1StCancelledPurchaser(excSourcingId);
        if(excSourcingPurchaser.isPresent() ) {
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            excSourcingPurchaserDto = ExcSourcingPurchaserMapper.INSTANCE.toExcSourcingPurchaserDto(excSourcingPurchaser.get(), timeZone);
            excSourcingPurchaserDto.setCreatedByName(UserDetailServiceUtil.getFullName(excSourcingPurchaserDto.getCreatedBy()));
        }
        return excSourcingPurchaserDto;
    }

    @Override
    public List<ExcSourcingPurchaserDto> findAllApprovedPurchaser(Long excSourcingId) {
        List<ExcSourcingPurchaserDto> excSourcingPurchaserDtos = new ArrayList<>();
        ExcSourcing excSourcing = excSourcingRepository.findExcSourcingsByRecId(excSourcingId);
        DeptApprovalStatus deptApprovalStatusNoneObj = deptApprovalStatusRepository.findByName(DEPT_APPROVAL_APPROVED.code());
        List<ExcSourcingPurchaser> excSourcingPurchasers = excSourcingPurchaserRepository.findExcSourcingPurchasersByExcSourcingAndApprovalStatus(excSourcing, deptApprovalStatusNoneObj);
        if(excSourcingPurchasers != null && !excSourcingPurchasers.isEmpty()) {
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            excSourcingPurchaserDtos = ExcSourcingPurchaserMapper.INSTANCE.toExcSourcingPurchaserDtoList(excSourcingPurchasers, timeZone);
            excSourcingPurchaserDtos.stream()
                    .peek(excSourcingPurchaserDto -> {
                        excSourcingPurchaserDto.setCreatedByName(UserDetailServiceUtil.getFullName(excSourcingPurchaserDto.getCreatedBy()));
                    }).collect(Collectors.toList());
        }
        return excSourcingPurchaserDtos;
    }

    @Override
    public List<Long> findAllRelatedRequestIds() {
        List<Long> excSourcingIds = new ArrayList<>();


        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Optional<Approver> approver = approverRepository.findApproverByTenantAndLoginId(tenant, AppUtil.getUserName());
        if(approver.isPresent()) {
            List<ExcSourcingPurchaser> excSourcingPurchaserList = excSourcingPurchaserRepository.findExcSourcingPurchaserByApprover(approver.get());

            if (excSourcingPurchaserList != null && !excSourcingPurchaserList.isEmpty()) {
                for (ExcSourcingPurchaser excSourcingPurchaser : excSourcingPurchaserList) {
                    excSourcingIds.add(excSourcingPurchaser.getExcSourcing().getRecId());
                }
            }
        }

        return excSourcingIds;
    }

    @Override
    public EPAuthPurchaserResponse getPurchaserListByConditions(PurchaserSearchRequest excSourcing) {
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
        List<EPAuthPurchaserDto> epAuthPurchaserDtoList = new ArrayList<>();

        if (epAuthUserListResponse.getData() == null) {
            return EPAuthPurchaserResponse.builder()
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
            epAuthPurchaserDtoList.add(
                    EPAuthPurchaserDto.builder()
                            .sysUserId(epAuthUserDTO.getSysUserId())
                            .loginId(epAuthUserDTO.getLoginId())
                            .fullName(epAuthUserDTO.getFullName())
                            .email(epAuthUserDTO.getEmail())
                            .mobilePhone(epAuthUserDTO.getMobilePhone())
                            .phone(epAuthUserDTO.getPhone())
                            .timezone(epAuthUserDTO.getTimezone())
                            .build());
        }
        return EPAuthPurchaserResponse.builder()
                .data(epAuthPurchaserDtoList)
                .page(epAuthUserListResponse.getPage())
                .pageSize(epAuthUserListResponse.getPageSize())
                .total(epAuthUserListResponse.getTotal())
                .totalPage(epAuthUserListResponse.getTotalPage())
                .build();
    }

    @Override
    public EPAuthPurchaserResponse getPurchaserListByConditions(PurchaserSearchRequest excSourcing, String[] privilegeCodes) {
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
        List<EPAuthPurchaserDto> epAuthPurchaserDtoList = new ArrayList<>();

        if (epAuthUserListResponse.getData() == null) {
            return EPAuthPurchaserResponse.builder()
                    .data(null)
                    .page(epAuthUserListResponse.getPage())
                    .pageSize(epAuthUserListResponse.getPageSize())
                    .total(epAuthUserListResponse.getTotal())
                    .totalPage(epAuthUserListResponse.getTotalPage())
                    .build();
        }

        for (EPAuthUserDTO epAuthUserDTO: epAuthUserListResponse.getData()) {
            epAuthPurchaserDtoList.add(
                    EPAuthPurchaserDto.builder()
                            .sysUserId(epAuthUserDTO.getSysUserId())
                            .loginId(epAuthUserDTO.getLoginId())
                            .fullName(epAuthUserDTO.getFullName())
                            .email(epAuthUserDTO.getEmail())
                            .mobilePhone(epAuthUserDTO.getMobilePhone())
                            .phone(epAuthUserDTO.getPhone())
                            .timezone(epAuthUserDTO.getTimezone())
                            .build());
        }

        epAuthPurchaserDtoList.sort(Comparator.comparing(EPAuthPurchaserDto::getFullName));
        return EPAuthPurchaserResponse.builder()
                .data(epAuthPurchaserDtoList)
                .page(epAuthUserListResponse.getPage())
                .pageSize(epAuthUserListResponse.getPageSize())
                .total(epAuthUserListResponse.getTotal())
                .totalPage(epAuthUserListResponse.getTotalPage())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByRequestId(Long excSourcingId) {
        log.info("Delete all Dept Purchaser by Request ID : {}", excSourcingId);
        excSourcingPurchaserRepository.deleteExcSourcingPurchaserByExcSourcingId(excSourcingId);
    }

    @Override
    public boolean hasApprovalPermission(ExcSourcingPurchaser excSourcingPurchaser) {
        boolean result = false;
        DeptApprovalStatus purchaserStatus = excSourcingPurchaser.getApprovalStatus();
        if(purchaserStatus.getName().equalsIgnoreCase(DEPT_APPROVAL_AWAITING.code())) {
            result = true;
        }
        return result;
    }

    @Override
    public boolean isLastPurchaser(Long excSourcingId) {
        boolean result = false;
        ExcSourcing excSourcing = excSourcingRepository.findExcSourcingsByRecId(excSourcingId);

        DeptApprovalStatus deptApprovalStatusAwaitingObj = deptApprovalStatusRepository.findByName(DEPT_APPROVAL_AWAITING.code());
        List<ExcSourcingPurchaser> excSourcingPurchasers = excSourcingPurchaserRepository.findExcSourcingPurchasersByExcSourcingAndApprovalStatus(excSourcing, deptApprovalStatusAwaitingObj);

        if(excSourcingPurchasers == null || excSourcingPurchasers.isEmpty()) {
            result = true;
        }
        return result;
    }

}
