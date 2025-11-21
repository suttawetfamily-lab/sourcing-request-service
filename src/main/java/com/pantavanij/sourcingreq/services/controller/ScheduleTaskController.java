package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.dto.elasticsearch.SupplierWebworksDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.pantavanij.sourcingreq.services.enums.Role.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.endpoint.version}")
public class ScheduleTaskController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleTaskController.class);

    private final TenantRepository tenantRepository;
    private final EPAuthService epAuthService;
    private final TenantSubCategoryRepository tenantSubCategoryRepository;
    private final RequestRequesterService requestRequesterService;
    private final RequesterRepository requesterRepository;
    private final RequestReviewerService requestReviewerService;
    private final ReviewerRepository reviewerRepository;
    private final RequestReportLineService requestReportLineService;
    private final ReportLineRepository reportLineRepository;
    private final RequestDeptApproverService requestDeptApproverService;
    private final ApproverRepository approverRepository;
    private final UaaService uaaService;
    private final SupplierRepository supplierRepository;
    private final SupplierService supplierService;
    private final TenantConfigService tenantConfigService;


    @PreAuthorize("hasAuthority('SAM')")
    @PostMapping(value = "/scheduled/update-approver", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateApprover() {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LOGGER.info(dtf.format(LocalDateTime.now()) + " Start Daily Update Approver");
        List<Tenant> tenantList = tenantRepository.findAll();

        for (Tenant tenant:tenantList) {
            if("bay".equalsIgnoreCase(tenant.getCode())) { // TODO: Checking data in TenantConfig instead
                List<PurchaserGroupResponse> purchaserList = epAuthService.getAllPurchaserGroup(tenant.getCode());
                if (purchaserList != null && purchaserList.size() > 0) {
                    for (PurchaserGroupResponse purchaser : purchaserList) {
                        String type = purchaser.getIsIT().trim().replace(" ","-"); //Hardcoding incase Non-IT testing, and need to clarify between both side
                        String category = purchaser.getCategory().trim();
                        String subCategory = purchaser.getSubCategory().trim();
                        Optional<TenantSubCategory> tenantSubCategory = tenantSubCategoryRepository.getSubCategoriesByTenantIdAndTypeAndCategory(tenant.getRecId(), type, category, subCategory);
                        if (tenantSubCategory.isPresent()) {
                            TenantSubCategory updatedTenantSubCategory = tenantSubCategory.get();
                            updatedTenantSubCategory.setBuyer(purchaser.getUsername());
                            updatedTenantSubCategory.setEmail(purchaser.getEmail());
                            updatedTenantSubCategory.setTelephone(purchaser.getPhone());
                            updatedTenantSubCategory.setUpdatedBy("ShedLock (API)");
                            updatedTenantSubCategory.setUpdatedDate(DateTimeUtil.getTimestampUTC());

                            tenantSubCategoryRepository.save(updatedTenantSubCategory);
                        } else {
                            //TODO: Set isActive to be false
                        }
                    }
                }
            }
        }

        LOGGER.info(dtf.format(LocalDateTime.now()) + " End Daily Update Approver");
        return ResponseEntity.ok().body(dtf.format(LocalDateTime.now()) + " Update Approver Done.");
    }


    @PreAuthorize("hasAuthority('SAM')")
    @PostMapping(value = "/scheduled/update-requester", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateRequester() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LOGGER.info(dtf.format(LocalDateTime.now()) + " Start Daily Update Requester");
        List<Tenant> tenantList = tenantRepository.findAll();

        for (Tenant tenant:tenantList) {

            String[] privilegeCodes = new String[] {REQUESTER.privilegeCode()};

            RequesterSearchRequest requesterSearchRequest = new RequesterSearchRequest();
            requesterSearchRequest.setTenantId(tenant.getCode());
            requesterSearchRequest.setPage(1);
            requesterSearchRequest.setPageSize(99999);
            EPAuthRequesterResponse response = requestRequesterService.getRequesterListByConditions(requesterSearchRequest, privilegeCodes);

            if(response.getTotal() > 0) {
                Integer maxSequence = 0;
                Optional<Requester> requesterMaxSequence = requesterRepository.findFirstByTenantRecIdOrderBySequenceDesc(tenant.getRecId());
                if (requesterMaxSequence.isPresent()) {
                    maxSequence = requesterMaxSequence.get().getSequence();
                }
                List<Requester> requesterList = new ArrayList<>();
                for (EPAuthRequesterDto epAuthRequesterDto : response.getData()) {
                    Optional<Requester> optionalRequester = requesterRepository.findByLoginId(tenant.getRecId(), epAuthRequesterDto.getLoginId());
                    if (optionalRequester.isPresent()) {
                        Requester requester = optionalRequester.get();
                        requester.setUserId(epAuthRequesterDto.getSysUserId());
                        requester.setRequesterName(epAuthRequesterDto.getFullName());
                        requester.setEmail(epAuthRequesterDto.getEmail());
                        requester.setPhone(epAuthRequesterDto.getPhone());
                        requester.setActive(true);
                        requester.setUpdatedBy("ShedLock (API)");
                        requester.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                        requesterList.add(requester);

                    } else {

                        Requester requester = new Requester();
                        requester.setTenant(tenant);
                        requester.setUserId(epAuthRequesterDto.getSysUserId());
                        requester.setLoginId(epAuthRequesterDto.getLoginId());
                        requester.setRequesterName(epAuthRequesterDto.getFullName());
                        requester.setEmail(epAuthRequesterDto.getEmail());
                        requester.setPhone(epAuthRequesterDto.getPhone());
                        requester.setCreatedBy("ShedLock (API)");
                        requester.setCreatedDate(DateTimeUtil.getTimestampUTC());
                        requester.setSequence(maxSequence + 1);
                        requesterList.add(requester);
                        maxSequence++;
                    }
                }
                requesterRepository.saveAll(requesterList);
            }
        }

        LOGGER.info(dtf.format(LocalDateTime.now()) + " End Daily Update Requester");
        return ResponseEntity.ok().body(dtf.format(LocalDateTime.now()) + " Update Requester Done.");
    }


    @PreAuthorize("hasAuthority('SAM')")
    @PostMapping(value = "/scheduled/update-reviewer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateReviewer() {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LOGGER.info(dtf.format(LocalDateTime.now()) + " Sourcing Request: Execute Daily Update Reviewer everyday 00:00:01");

        LOGGER.info(dtf.format(LocalDateTime.now()) + " Start Daily Update Reviewer");
        List<Tenant> tenantList = tenantRepository.findAll();

        for (Tenant tenant:tenantList) {

            String[] privilegeCodes = new String[] {REVIEWER.privilegeCode()};

            ReviewerSearchRequest reviewerSearchRequest = new ReviewerSearchRequest();
            reviewerSearchRequest.setTenantId(tenant.getCode());
            reviewerSearchRequest.setPage(1);
            reviewerSearchRequest.setPageSize(99999);
            EPAuthReviewerResponse response = requestReviewerService.getReviewerListByConditions(reviewerSearchRequest, privilegeCodes);

            if(response.getTotal() > 0) {
                Integer maxSequence = 0;
                Optional<Reviewer> reviewerMaxSequence = reviewerRepository.findFirstByTenantRecIdOrderBySequenceDesc(tenant.getRecId());
                if (reviewerMaxSequence.isPresent()) {
                    maxSequence = reviewerMaxSequence.get().getSequence();
                }
                List<Reviewer> reviewerList = new ArrayList<>();
                for (EPAuthReviewerDto epAuthReviewerDto : response.getData()) {
                    Optional<Reviewer> optionalReviewer = reviewerRepository.findByLoginId(tenant.getRecId(), epAuthReviewerDto.getLoginId());
                    if (optionalReviewer.isPresent()) {
                        Reviewer reviewer = optionalReviewer.get();
                        reviewer.setUserId(epAuthReviewerDto.getSysUserId());
                        reviewer.setReviewerName(epAuthReviewerDto.getFullName());
                        reviewer.setEmail(epAuthReviewerDto.getEmail());
                        reviewer.setPhone(epAuthReviewerDto.getPhone());
                        reviewer.setActive(true);
                        reviewer.setUpdatedBy("ShedLock (API)");
                        reviewer.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                        reviewerList.add(reviewer);

                    } else {

                        Reviewer reviewer = new Reviewer();
                        reviewer.setTenant(tenant);
                        reviewer.setUserId(epAuthReviewerDto.getSysUserId());
                        reviewer.setLoginId(epAuthReviewerDto.getLoginId());
                        reviewer.setReviewerName(epAuthReviewerDto.getFullName());
                        reviewer.setEmail(epAuthReviewerDto.getEmail());
                        reviewer.setPhone(epAuthReviewerDto.getPhone());
                        reviewer.setCreatedBy("ShedLock (API)");
                        reviewer.setCreatedDate(DateTimeUtil.getTimestampUTC());
                        reviewer.setSequence(maxSequence + 1);
                        reviewerList.add(reviewer);
                        maxSequence++;
                    }
                }
                reviewerRepository.saveAll(reviewerList);
            }
        }

        LOGGER.info(dtf.format(LocalDateTime.now()) + " End Daily Update Reviewer");
        return ResponseEntity.ok().body(dtf.format(LocalDateTime.now()) + " Update Reviewer Done.");
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PostMapping(value = "/scheduled/update-report-line", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateReportLine() {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LOGGER.info(dtf.format(LocalDateTime.now()) + " Start Daily Update ReportLine");
        List<Tenant> tenantList = tenantRepository.findAll();

        for (Tenant tenant:tenantList) {

            String[] privilegeCodes = new String[] {REPORT_LINE.privilegeCode()};

            ReportLineSearchRequest reportLineSearchRequest = new ReportLineSearchRequest();
            reportLineSearchRequest.setTenantId(tenant.getCode());
            reportLineSearchRequest.setPage(1);
            reportLineSearchRequest.setPageSize(99999);
            EPAuthReportLineResponse response = requestReportLineService.getReportLineListByConditions(reportLineSearchRequest, privilegeCodes);

            if(response.getTotal() > 0) {
                Integer maxSequence = 0;
                Optional<ReportLine> reportLineMaxSequence = reportLineRepository.findFirstByTenantRecIdOrderBySequenceDesc(tenant.getRecId());
                if (reportLineMaxSequence.isPresent()) {
                    maxSequence = reportLineMaxSequence.get().getSequence();
                }
                List<ReportLine> reportLineList = new ArrayList<>();
                for (EPAuthReportLineDto epAuthReportLineDto : response.getData()) {
                    Optional<ReportLine> optionalReportLine = reportLineRepository.findByLoginId(tenant.getRecId(), epAuthReportLineDto.getLoginId());
                    if (optionalReportLine.isPresent()) {
                        ReportLine reportLine = optionalReportLine.get();
                        reportLine.setUserId(epAuthReportLineDto.getSysUserId());
                        reportLine.setReportLineName(epAuthReportLineDto.getFullName());
                        reportLine.setEmail(epAuthReportLineDto.getEmail());
                        reportLine.setPhone(epAuthReportLineDto.getPhone());
                        reportLine.setActive(true);
                        reportLine.setUpdatedBy("ShedLock (API)");
                        reportLine.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                        reportLineList.add(reportLine);

                    } else {

                        ReportLine reportLine = new ReportLine();
                        reportLine.setTenant(tenant);
                        reportLine.setUserId(epAuthReportLineDto.getSysUserId());
                        reportLine.setLoginId(epAuthReportLineDto.getLoginId());
                        reportLine.setReportLineName(epAuthReportLineDto.getFullName());
                        reportLine.setEmail(epAuthReportLineDto.getEmail());
                        reportLine.setPhone(epAuthReportLineDto.getPhone());
                        reportLine.setCreatedBy("ShedLock (API)");
                        reportLine.setCreatedDate(DateTimeUtil.getTimestampUTC());
                        reportLine.setSequence(maxSequence + 1);
                        reportLineList.add(reportLine);
                        maxSequence++;
                    }
                }
                reportLineRepository.saveAll(reportLineList);
            }
        }

        LOGGER.info(dtf.format(LocalDateTime.now()) + " End Daily Update ReportLine");
        return ResponseEntity.ok().body(dtf.format(LocalDateTime.now()) + " Update ReportLine Done.");
    }


    @PreAuthorize("hasAuthority('SAM')")
    @PostMapping(value = "/scheduled/update-dept-approver", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateDeptApprover() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LOGGER.info(dtf.format(LocalDateTime.now()) + " Start Daily Update DeptApprover");
        List<Tenant> tenantList = tenantRepository.findAll();

        for (Tenant tenant:tenantList) {

            String[] privilegeCodes = new String[] {DEPT_APPROVER.privilegeCode()};

            DeptApproverSearchRequest deptApproverSearchRequest = new DeptApproverSearchRequest();
            deptApproverSearchRequest.setTenantId(tenant.getCode());
            deptApproverSearchRequest.setPage(1);
            deptApproverSearchRequest.setPageSize(99999);
            EPAuthDeptApproverResponse response = requestDeptApproverService.getDeptApproverListByConditions(deptApproverSearchRequest, privilegeCodes);

            if(response.getTotal() > 0) {
                Integer maxSequence = 0;
                Optional<Approver> approverMaxSequence = approverRepository.findFirstByTenantRecIdOrderBySequenceDesc(tenant.getRecId());
                if (approverMaxSequence.isPresent()) {
                    maxSequence = approverMaxSequence.get().getSequence();
                }
                List<Approver> approverList = new ArrayList<>();
                for (EPAuthDeptApproverDto epAuthDeptApproverDto : response.getData()) {
                    Optional<Approver> optionalApprover = approverRepository.findByLoginId(tenant.getRecId(), epAuthDeptApproverDto.getLoginId());
                    if (optionalApprover.isPresent()) {
                        Approver approver = optionalApprover.get();
                        approver.setUserId(epAuthDeptApproverDto.getSysUserId());
                        approver.setApproverName(epAuthDeptApproverDto.getFullName());
                        approver.setEmail(epAuthDeptApproverDto.getEmail());
                        approver.setPhone(epAuthDeptApproverDto.getPhone());
                        approver.setActive(true);
                        approver.setUpdatedBy("ShedLock (API)");
                        approver.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                        approverList.add(approver);

                    } else {

                        Approver approver = new Approver();
                        approver.setTenant(tenant);
                        approver.setUserId(epAuthDeptApproverDto.getSysUserId());
                        approver.setLoginId(epAuthDeptApproverDto.getLoginId());
                        approver.setApproverName(epAuthDeptApproverDto.getFullName());
                        approver.setEmail(epAuthDeptApproverDto.getEmail());
                        approver.setPhone(epAuthDeptApproverDto.getPhone());
                        approver.setCreatedBy("ShedLock (API)");
                        approver.setCreatedDate(DateTimeUtil.getTimestampUTC());
                        approver.setSequence(maxSequence + 1);
                        approverList.add(approver);
                        maxSequence++;
                    }
                }
                approverRepository.saveAll(approverList);
            }
        }

        LOGGER.info(dtf.format(LocalDateTime.now()) + " End Daily Update DeptApprover");
        return ResponseEntity.ok().body(dtf.format(LocalDateTime.now()) + " Update DeptApprover Done.");
    }


    @PreAuthorize("hasAuthority('SAM')")
    @PostMapping(value = "/scheduled/update-supplier", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateSupplier() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LOGGER.info(dtf.format(LocalDateTime.now()) + " Start Daily Update Supplier");

        List<Tenant> tenantList = tenantRepository.findAll();

        for (Tenant tenant : tenantList) {

            List<TenantInfoResponse> response = uaaService.getTenantInfo(tenant.getCode());
            boolean isSearchByInvitationCode = tenantConfigService.getIsSearchByInvitationCode(tenant.getRecId());

            List<Supplier> supplierList = supplierRepository.findAllByTenant(tenant);
            for (Supplier supplier : supplierList) {
                SupplierWebWorkSearchByTPShortNameRequest supplierWebWorkSearchByTPShortNameRequest = new SupplierWebWorkSearchByTPShortNameRequest();
                supplierWebWorkSearchByTPShortNameRequest.setTPShortName(supplier.getShortName());
                supplierWebWorkSearchByTPShortNameRequest.setInvitationCode(isSearchByInvitationCode ? (response != null && !response.isEmpty() ? response.get(0).getInvitation_code() : AppUtil.getTenantId()) : "");
                SupplierWebworksDto supplierWebworksDto = supplierService.getSupplierWebWorkByTPShortName(supplierWebWorkSearchByTPShortNameRequest);

                if (supplierWebworksDto != null) {

                    Supplier existingSupplier = supplierRepository.getSupplierByShortName(supplier.getShortName(), tenant.getRecId());

                    existingSupplier.setShortName(supplierWebworksDto.getTPShortName());
                    existingSupplier.setFullCompanyNameEN(supplierWebworksDto.getFullCompanyNameEN());
                    existingSupplier.setFullCompanyNameLocal(supplierWebworksDto.getFullCompanyNameLocal());
                    existingSupplier.setCompanyNameEN(supplierWebworksDto.getCompanyNameEN());
                    existingSupplier.setCompanyNameLocal(supplierWebworksDto.getCompanyNameLocal());
                    existingSupplier.setBranchNameEN(supplierWebworksDto.getBranchNameEN());
                    existingSupplier.setBranchNameLocal(supplierWebworksDto.getBranchNameLocal());
                    existingSupplier.setTaxId(supplierWebworksDto.getTaxId());
                    existingSupplier.setUpdatedBy("ShedLock");
                    existingSupplier.setUpdatedDate(DateTimeUtil.getTimestampUTC());

                    supplierRepository.save(existingSupplier);
                }
            }
        }

        LOGGER.info(dtf.format(LocalDateTime.now()) + " End Daily Update Supplier");
        return ResponseEntity.ok().body(dtf.format(LocalDateTime.now()) + " Update Supplier Done.");
    }
}