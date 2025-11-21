package com.pantavanij.sourcingreq.services.schedule;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.dto.elasticsearch.CoreContactPersonDto;
import com.pantavanij.sourcingreq.services.domain.dto.elasticsearch.SupplierWebworksDto;
import com.pantavanij.sourcingreq.services.domain.dto.supplier.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.request.supplier.CreateSupplierRequest;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.domain.response.pr.CreateOraclePrResponse;
import com.pantavanij.sourcingreq.services.enums.EmailActivity;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import com.pantavanij.sourcingreq.services.util.SupplierNameUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.Activity.ACTIVITY_SHEDLOCK;
import static com.pantavanij.sourcingreq.services.enums.Role.*;
import static com.pantavanij.sourcingreq.services.enums.SupplierMailingLogStatus.SUPPLIER_MAILING_LOG_AWAITING;
import static com.pantavanij.sourcingreq.services.enums.SupplierMailingLogStatus.SUPPLIER_MAILING_LOG_SENT;
import static com.pantavanij.sourcingreq.services.enums.SupplierMailingQueueStatus.SUPPLIER_MAILING_QUEUE_AWAITING;
import static com.pantavanij.sourcingreq.services.enums.SupplierMailingQueueStatus.SUPPLIER_MAILING_QUEUE_COMPLETED;

@Log
@EnableScheduling
@Configuration
@EnableAsync
@RequiredArgsConstructor
public class ScheduleTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleTask.class);
    private final ProjectService projectService;
    private final RequestService requestService;
    private final TenantConfigService tenantConfigService;
    private final AttachmentService attachmentService;
    private final TenantService tenantService;
    private final UaaService uaaService;
    private final TenantRepository tenantRepository;
    private final EPAuthService epAuthService;
    private final TenantSubCategoryRepository tenantSubCategoryRepository;
    private final SupplierMailingQueueRepository supplierMailingQueueRepository;
    private final SupplierMailingLogRepository supplierMailingLogRepository;
    private final SupplierNotificationService supplierNotificationService;
    private final EmailService emailService;
    private final RequestRequesterService requestRequesterService;
    private final RequesterRepository requesterRepository;
    private final RequestReviewerService requestReviewerService;
    private final ReviewerRepository reviewerRepository;
    private final ReportLineRepository reportLineRepository;
    private final RequestReportLineService requestReportLineService;
    private final RequestDeptApproverService requestDeptApproverService;
    private final ApproverRepository approverRepository;
    private final SupplierRepository supplierRepository;
    private final SupplierService supplierService;
    private final PurchaserRepository purchaserRepository;
    private final RequestPurchaserService requestPurchaserService;
    private final RequestRepository requestRepository;
    private final PrService prService;

    @Scheduled(cron = "0 */15 * * * ?") // every 15 minute
    @SchedulerLock(name = "getProjectScheduleTask")
    public void executeGetProject() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LOGGER.info(dtf.format(LocalDateTime.now()) + " Sourcing Request: Execute Get Project every 15 minute");
        List<Tenant> tenantList = tenantRepository.findAll();
        for (Tenant tenant:tenantList) {
            // Retrieve data from AIT API to upsert Project data
            LOGGER.info(dtf.format(LocalDateTime.now()) + " Start retrieve data from API " +tenant.getCode());
            String uri = tenantConfigService.getProjectListURL(tenant.getRecId());

            if ( uri != null) {
                projectService.saveProjectBySchedule(uri,tenant);
            }
            LOGGER.info(dtf.format(LocalDateTime.now()) + " End retrieve data from API " +tenant.getCode());
        }

    }

    @Scheduled(cron = "0 0 */3 * * ?") // every 3 hours
    @SchedulerLock(name = "cleanUpScheduleTask")
    @Async
    public void executeCleanup() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LOGGER.info(dtf.format(LocalDateTime.now()) + " Sourcing Request: Execute Cleanup every 3 hours");

        // Delete unused attachment (RequestAttachment/RequestItemAppachment/ExistingPriceAttachment)
        LOGGER.info(dtf.format(LocalDateTime.now()) + " Start delete unused attachment");
        List<Tenant> tenantList = tenantRepository.findAll();
        for (Tenant tenant:tenantList) {
            attachmentService.deleteUnusedAttachment(tenant);
        }
        LOGGER.info(dtf.format(LocalDateTime.now()) + " End delete unused attachment");
    }

    @Scheduled(cron = "7 0 0 * * ?") // every day 00:00:01
    //@Scheduled(cron = "0 */5 * * * ?") // @Scheduled(cron = "1 0 0 * * ?") // every day 00:00:01
    @SchedulerLock(name = "cleanUpUserScheduleTask")
    @Async
    public void executeCleanupUser() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LOGGER.info(dtf.format(LocalDateTime.now()) + " Sourcing Request: Execute Cleanup every 7 days");

        LOGGER.info(dtf.format(LocalDateTime.now()) + " Start update unused users");
        List<Tenant> tenantList = tenantRepository.findAll();
        for (Tenant tenant:tenantList) {
            try {
                // Update Status to InActive instead
                requesterRepository.inActiveUnusedRequesters();
                reviewerRepository.inActiveUnusedReviewers();
                reportLineRepository.inActiveUnusedReportLines();
                approverRepository.inActiveUnusedApprovers();
                // TODO: Inactive unused purchaser
//            purchaserRepository.inActiveUnusedPurchasers()

            } catch (Exception e) {
                LOGGER.error("Error update unused users", e);
            }
        }
        LOGGER.info(dtf.format(LocalDateTime.now()) + " End update unused users");
    }

//    @Scheduled(cron = "0 */5 * * * ?") // every 1 minute
//    @SchedulerLock(name = "updateSourcingStatusTask")
//    public void executeUpdateSourcingStatus() {
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LOGGER.info(dtf.format(LocalDateTime.now()) + " Sourcing Request: Execute Update SourcingStatus every 1 minute");
//        LOGGER.info(dtf.format(LocalDateTime.now()) + " Start update Sourcing Status (eRFX)");
//        List<Tenant> tenantList = tenantRepository.findAll();
//
//        for (Tenant tenant:tenantList) {
//            try {
//                String authCode = tenantConfigService.getERFXAuthCode(tenant.getRecId());
//                AuthenticationRequest request = new AuthenticationRequest();
//                request.setGrantType("auth_code");
//                request.setAuthCode(authCode);
//                request.setPathUrl("home");
//                Map<String, Object> responseMap = uaaService.getToken(request);
//
//                RefreshAuthenticationRequest refreshRequest = new RefreshAuthenticationRequest();
//                refreshRequest.setGrantType("refresh_token");
//                refreshRequest.setRefreshToken(responseMap.get("refresh_token").toString());
//                refreshRequest.setPathUrl(request.getPathUrl());
//                Map<String, Object> refreshResponseMap = uaaService.getRefreshToken(refreshRequest);
//
//                String refreshToken = refreshResponseMap.get("refresh_token").toString();
//                String maxItems = tenantConfigService.getERFXNoMaxItems(tenant.getRecId());
//
//                if(refreshToken != null && maxItems != null) {
//                    LOGGER.info(dtf.format(LocalDateTime.now()) + " Get TopN Update Sourcing Status (eRFX): Tenant -> " + tenant.getCode() + ", Max Items -> " + maxItems );
//                    requestService.getTopNUpdatedERFXRequest(tenant, maxItems, refreshToken);
//                }
//            } catch (Exception ex) {
//                LOGGER.info(dtf.format(LocalDateTime.now()) + " eRFX Auth_Code is not available for tenant configuration : " + tenant.getCode() + " | " + ex.getMessage());
//            }
//        }
//
//        LOGGER.info(dtf.format(LocalDateTime.now()) + " End update Sourcing Status (eRFX)");
//    }

    @Scheduled(cron = "1 0 0 * * ?") // every day 00:00:01
    //@Scheduled(cron = "0 */10 * * * ?") // @Scheduled(cron = "1 0 0 * * ?") // every day 00:00:01
    @SchedulerLock(name = "dailyUpdateApprover")
    @Async
    @Transactional
    public void executeDailyUpdateApprover() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LOGGER.info(dtf.format(LocalDateTime.now()) + " Sourcing Request: Execute Daily Update Approver everyday 00:00:01");

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
                        LOGGER.info(dtf.format(LocalDateTime.now()) + String.format(" Data Checking : Type=\"%s\", Category=\"%s\", SubCategory=\"%s\"", type, category, subCategory));
                        Optional<TenantSubCategory> tenantSubCategory = tenantSubCategoryRepository.getSubCategoriesByTenantIdAndTypeAndCategory(tenant.getRecId(), type, category, subCategory);
                        if (tenantSubCategory.isPresent()) {
                            TenantSubCategory updatedTenantSubCategory = tenantSubCategory.get();
                            updatedTenantSubCategory.setBuyer(purchaser.getUsername());
                            updatedTenantSubCategory.setEmail(purchaser.getEmail());
                            updatedTenantSubCategory.setTelephone(purchaser.getPhone());
                            updatedTenantSubCategory.setUpdatedBy("ShedLock");
                            updatedTenantSubCategory.setUpdatedDate(DateTimeUtil.getTimestampUTC());

                            tenantSubCategoryRepository.save(updatedTenantSubCategory);
                        } else {
                            //TODO: Set isActive to be false
                        }
                    }
                }
            } else {
                String[] privilegeCodes = new String[] {PURCHASER.privilegeCode()};

                PurchaserSearchRequest purchaserSearchRequest = new PurchaserSearchRequest();
                purchaserSearchRequest.setTenantId(tenant.getCode());
                purchaserSearchRequest.setPage(1);
                purchaserSearchRequest.setPageSize(99999);
                EPAuthPurchaserResponse response = requestPurchaserService.getPurchaserListByConditions(purchaserSearchRequest, privilegeCodes);

                if(response.getTotal() > 0) {
                    Integer maxSequence = 0;
                    Optional<Purchaser> purchaserMaxSequence = purchaserRepository.findFirstByTenantRecIdOrderBySequenceDesc(tenant.getRecId());
                    if (purchaserMaxSequence.isPresent()) {
                        maxSequence = purchaserMaxSequence.get().getSequence();
                    }
                    List<Purchaser> purchaserList = new ArrayList<>();
                    for (EPAuthPurchaserDto epAuthPurchaserDto : response.getData()) {
                        Optional<Purchaser> optionalPurchaser = purchaserRepository.findByLoginId(tenant.getRecId(), epAuthPurchaserDto.getLoginId());
                        if (optionalPurchaser.isPresent()) {
                            Purchaser purchaser = optionalPurchaser.get();
                            purchaser.setUserId(epAuthPurchaserDto.getSysUserId());
                            purchaser.setPurchaserName(epAuthPurchaserDto.getFullName());
                            purchaser.setEmail(epAuthPurchaserDto.getEmail());
                            purchaser.setPhone(epAuthPurchaserDto.getPhone());
                            purchaser.setActive(true);
                            purchaser.setUpdatedBy("ShedLock");
                            purchaser.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                            purchaserList.add(purchaser);

                        } else {

                            Purchaser purchaser = new Purchaser();
                            purchaser.setTenant(tenant);
                            purchaser.setUserId(epAuthPurchaserDto.getSysUserId());
                            purchaser.setLoginId(epAuthPurchaserDto.getLoginId());
                            purchaser.setPurchaserName(epAuthPurchaserDto.getFullName());
                            purchaser.setEmail(epAuthPurchaserDto.getEmail());
                            purchaser.setPhone(epAuthPurchaserDto.getPhone());
                            purchaser.setCreatedBy("ShedLock");
                            purchaser.setCreatedDate(DateTimeUtil.getTimestampUTC());
                            purchaser.setSequence(maxSequence + 1);
                            purchaserList.add(purchaser);
                            maxSequence++;
                        }
                    }
                    purchaserRepository.saveAll(purchaserList);
                }
            }
        }

        LOGGER.info(dtf.format(LocalDateTime.now()) + " End Daily Update Approver");
    }

    @Scheduled(cron = "1 0 0 * * ?") // every day 00:00:01
    //@Scheduled(cron = "0 */10 * * * ?") // @Scheduled(cron = "1 0 0 * * ?") // every day 00:00:01
    @SchedulerLock(name = "dailyUpdateRequester")
    @Async
    @Transactional
    public void executeDailyUpdateRequester() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LOGGER.info(dtf.format(LocalDateTime.now()) + " Sourcing Request: Execute Daily Update Requester everyday 00:00:01");

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
                        requester.setUpdatedBy("ShedLock");
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
                        requester.setCreatedBy("ShedLock");
                        requester.setCreatedDate(DateTimeUtil.getTimestampUTC());
                        requester.setSequence(maxSequence + 1);
                        requesterList.add(requester);
                        maxSequence++;
                        //LOGGER.info(tenant.getCode() + " : " + requester.getLoginId() + " " + requester.getRequesterName());
                    }
                    //LOGGER.info(tenant.getCode() + " : " + epAuthRequesterDto.getLoginId() + " " + epAuthRequesterDto.getFullName());
                }
                requesterRepository.saveAll(requesterList);
            }
        }

        LOGGER.info(dtf.format(LocalDateTime.now()) + " End Daily Update Requester");
    }

    @Scheduled(cron = "1 0 0 * * ?") // every day 00:00:01
    //@Scheduled(cron = "0 */10 * * * ?") // @Scheduled(cron = "1 0 0 * * ?") // every day 00:00:01
    @SchedulerLock(name = "dailyUpdateReviewer")
    @Async
    @Transactional
    public void executeDailyUpdateReviewer() {
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
                        reviewer.setUpdatedBy("ShedLock");
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
                        reviewer.setCreatedBy("ShedLock");
                        reviewer.setCreatedDate(DateTimeUtil.getTimestampUTC());
                        reviewer.setSequence(maxSequence + 1);
                        reviewerList.add(reviewer);
                        maxSequence++;
                        //LOGGER.info(tenant.getCode() + " : " + reviewer.getLoginId() + " " + reviewer.getReviewerName());
                    }
                    //LOGGER.info(tenant.getCode() + " : " + epAuthReviewerDto.getLoginId() + " " + epAuthReviewerDto.getFullName());
                }
                reviewerRepository.saveAll(reviewerList);
            }
        }

        LOGGER.info(dtf.format(LocalDateTime.now()) + " End Daily Update Reviewer");
    }

    @Scheduled(cron = "1 0 0 * * ?") // every day 00:00:01
    //@Scheduled(cron = "0 */10 * * * ?") // @Scheduled(cron = "1 0 0 * * ?") // every day 00:00:01
    @SchedulerLock(name = "dailyUpdateReportLine")
    @Async
    @Transactional
    public void executeDailyUpdateReportLine() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LOGGER.info(dtf.format(LocalDateTime.now()) + " Sourcing Request: Execute Daily Update ReportLine everyday 00:00:01");

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
                        reportLine.setUpdatedBy("ShedLock");
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
                        reportLine.setCreatedBy("ShedLock");
                        reportLine.setCreatedDate(DateTimeUtil.getTimestampUTC());
                        reportLine.setSequence(maxSequence + 1);
                        reportLineList.add(reportLine);
                        maxSequence++;
                        //LOGGER.info(tenant.getCode() + " : " + reportLine.getLoginId() + " " + reportLine.getReportLineName());
                    }
                    //LOGGER.info(tenant.getCode() + " : " + epAuthReportLineDto.getLoginId() + " " + epAuthReportLineDto.getFullName());
                }
                reportLineRepository.saveAll(reportLineList);
            }
        }

        LOGGER.info(dtf.format(LocalDateTime.now()) + " End Daily Update ReportLine");
    }


    @Scheduled(cron = "1 0 0 * * ?") // every day 00:00:01
    //@Scheduled(cron = "0 */10 * * * ?") // @Scheduled(cron = "1 0 0 * * ?") // every day 00:00:01
    @SchedulerLock(name = "dailyUpdateSupplier")
    @Async
    @Transactional
    public void executeDailyUpdateSupplier() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LOGGER.info(dtf.format(LocalDateTime.now()) + " Sourcing Request: Execute Daily Update Supplier everyday 00:00:01");

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
    }


    @Scheduled(cron = "1 0 0 * * ?") // every day 00:00:01
    //@Scheduled(cron = "0 */10 * * * ?") // @Scheduled(cron = "1 0 0 * * ?") // every day 00:00:01
    @SchedulerLock(name = "dailyUpdateDeptApprover")
    @Async
    @Transactional
    public void executeDailyUpdateDeptApprover() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LOGGER.info(dtf.format(LocalDateTime.now()) + " Sourcing Request: Execute Daily Update DeptApprover everyday 00:00:01");

        LOGGER.info(dtf.format(LocalDateTime.now()) + " Start Daily Update DeptApprover");
        List<Tenant> tenantList = tenantRepository.findAll();

        for (Tenant tenant:tenantList) {

            String[] privilegeCodes = new String[] {DEPT_APPROVER.privilegeCode(), EXC_DEPT_APPROVER.privilegeCode(), EXC_PURCHASING_APPROVER.privilegeCode()};

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
                        approver.setUpdatedBy("ShedLock");
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
                        approver.setCreatedBy("ShedLock");
                        approver.setCreatedDate(DateTimeUtil.getTimestampUTC());
                        approver.setSequence(maxSequence + 1);
                        approverList.add(approver);
                        maxSequence++;
                        //LOGGER.info(tenant.getCode() + " : " + approver.getLoginId() + " " + approver.getDeptApproverName());
                    }
                    //LOGGER.info(tenant.getCode() + " : " + epAuthDeptApproverDto.getLoginId() + " " + epAuthDeptApproverDto.getFullName());
                }
                approverRepository.saveAll(approverList);
            }
        }

        LOGGER.info(dtf.format(LocalDateTime.now()) + " End Daily Update DeptApprover");
    }



    @Scheduled(cron = "0 */5 * * * ?") // every 5 minutes
    @SchedulerLock(name = "supplierMailingQueueAndNotify")
    @Async
    @Transactional
    public void executeSupplierMailingQueueAndNotify() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LOGGER.info(dtf.format(LocalDateTime.now()) + " Sourcing Request: Execute Supplier Mailing Queue and Notification every 5 minutes");
        LOGGER.info(dtf.format(LocalDateTime.now()) + " Start Supplier Notify");

        List<SupplierMailingLog> supplierMailingLogList = supplierMailingLogRepository.findByStatus(SUPPLIER_MAILING_LOG_AWAITING.code());
        if(!supplierMailingLogList.isEmpty()) {
            LOGGER.info(dtf.format(LocalDateTime.now()) + "  - Send email notification to Supplier Contact & Update SupplierMailingLog Status");

            for(SupplierMailingLog supplierMailingLog : supplierMailingLogList) {

                // Send email notification to Supplier Contact
                SendEMailRequest supplierNotifyEmail = new SendEMailRequest();
                supplierNotifyEmail.setRequestId(0L);
                supplierNotifyEmail.setEmailActivity(EmailActivity.SUPPLIER_NOTIFY);
                supplierNotifyEmail.setActivity(ACTIVITY_SHEDLOCK);

                EmailContractDetailDto EmailContractDetailDto = new EmailContractDetailDto();
                ContractDetailClientDto ContractDetailClientDto = new ContractDetailClientDto();
                ContractDetailClientDto.setEmail(supplierMailingLog.getEmail());
                EmailContractDetailDto.setRequester(ContractDetailClientDto);
                supplierNotifyEmail.setEmailContractDetailDto(EmailContractDetailDto);

                emailService.sendSupplierEMailNotification(supplierNotifyEmail, supplierMailingLog);

                // Update SupplierMailingLog Status
                supplierMailingLog.setStatus(SUPPLIER_MAILING_LOG_SENT.code());
                supplierMailingLog.setUpdatedBy("shedlock");
                supplierMailingLog.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                supplierMailingLogRepository.save(supplierMailingLog);

            }
        }

        List<SupplierMailingQueue> supplierMailingQueueList = supplierMailingQueueRepository.findByStatus(SUPPLIER_MAILING_QUEUE_AWAITING.code());
        if(!supplierMailingQueueList.isEmpty()) {
            LOGGER.info(dtf.format(LocalDateTime.now()) + "  - Call SupplierDirectory to retrieve supplier contact data & Upsert contact data into SupplierMailingLog & SupplierMailingQueue Status");

            for(SupplierMailingQueue supplierMailingQueue : supplierMailingQueueList) {

                // Call SupplierDirectory to retrieve supplier contact data
                SupplierContactRequest supplierContactRequest = new SupplierContactRequest();
                supplierContactRequest.setPageNo(1);
                supplierContactRequest.setPageSize(9999);
                supplierContactRequest.setCatLevel1Id(supplierMailingQueue.getCatLevel1Id());
                supplierContactRequest.setCatLevel2Id(supplierMailingQueue.getCatLevel2Id());
                supplierContactRequest.setCatLevel3Id(supplierMailingQueue.getCatLevel3Id());
                supplierContactRequest.setRuleName("Pantavanij Standard");

                SupplierContactResponse response = supplierNotificationService.getSupplierContact(supplierContactRequest);

                // Insert contact data into SupplierMailingLog & Update SupplierMailingQueue Status
                if(response.getContent() != null) {
                    SupplierContactDto content = response.getContent();
                    SupplierCategoryDto supplierCategoryDto = content.getSupplierCategory();

                    String supplierCategoryValueLocal = String.format("%s / %s / %s", supplierCategoryDto.getCategoryValueLocalLev1()
                            ,supplierCategoryDto.getCategoryValueLocalLev2()
                            , supplierCategoryDto.getCategoryValueLocalLev3());

                    String supplierCategoryValueInter = String.format("%s / %s / %s", supplierCategoryDto.getCategoryValueInterLev1()
                    ,supplierCategoryDto.getCategoryValueInterLev2()
                    , supplierCategoryDto.getCategoryValueInterLev3());

                    Integer contactCount = 0;
                    for(SupplierInfoDto supplierInfo : content.getSuppliers()) {
                        for(ContactDto contact : supplierInfo.getSupplierContacts()) {
                            SupplierMailingLog supplierMailingLog = new SupplierMailingLog();
                            supplierMailingLog.setSupplierMailingQueue(supplierMailingQueue);
                            supplierMailingLog.setSupplierCategoryValueLocal(supplierCategoryValueLocal);
                            supplierMailingLog.setSupplierCategoryValueInter(supplierCategoryValueInter);
                            supplierMailingLog.setSupplierNameLocal(supplierInfo.getSupplierName().getInvNameLocal());
                            supplierMailingLog.setSupplierNameInter(supplierInfo.getSupplierName().getInvNameInter());
                            supplierMailingLog.setContactNameLocal(contact.getContactNameLocal());
                            supplierMailingLog.setContactNameInter(contact.getContactNameInter());
                            supplierMailingLog.setEmail(contact.getEMail().replace("epcbox@ptvnservice.com", "qatest_sr@hotmail.com"));
                            supplierMailingLog.setStatus(SUPPLIER_MAILING_LOG_AWAITING.code());
                            supplierMailingLog.setCreatedBy(supplierMailingQueue.getCreatedBy());
                            supplierMailingLog.setCreatedDate(supplierMailingQueue.getCreatedDate());

                            supplierMailingLogRepository.save(supplierMailingLog);
                            contactCount++;
                        }
                    }

                    supplierMailingQueue.setNumberOfContact(contactCount);
                    supplierMailingQueue.setUpdatedBy("shedlock");
                    supplierMailingQueue.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                    supplierMailingQueue.setStatus(SUPPLIER_MAILING_QUEUE_COMPLETED.code());

                    supplierMailingQueueRepository.save(supplierMailingQueue);
                }
            }
        }
        LOGGER.info(dtf.format(LocalDateTime.now()) + " End Supplier Notify");
    }



    @Scheduled(cron = "0 */5 * * * ?") // every 5 minutes
    //@Scheduled(cron = "0 */1 * * * ?")
    @SchedulerLock(name = "dailyUpdateOracleSupplier")
    @Async
    @Transactional
    public void executeDailyUpdateOracleSupplier() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LOGGER.info(dtf.format(LocalDateTime.now()) + " Sourcing Request: Execute Daily Update Oracle Supplier everyday 00:00:01");
        LOGGER.info(dtf.format(LocalDateTime.now()) + " Start Daily Update Oracle Supplier");

        List<Tenant> tenantList = tenantRepository.findAll();

        for (Tenant tenant : tenantList) {

            List<TenantInfoResponse> response = uaaService.getTenantInfo(tenant.getCode());
            boolean isSearchByInvitationCode = tenantConfigService.getIsSearchByInvitationCode(tenant.getRecId());
            boolean isCopyToPRViaERP = tenantConfigService.getCopyToPRViaERP(tenant.getRecId());

            if(isCopyToPRViaERP) {

                String copyToPrOrganizationsConfig = tenantConfigService.getCopyToPrOrganizations(tenant.getRecId());
                List<Integer> orgIds;
                int apply = 1;
                if (copyToPrOrganizationsConfig == null || copyToPrOrganizationsConfig.isBlank()) {
                    apply = 0;
                    orgIds = java.util.Collections.singletonList(-1);
                } else {
                    orgIds = java.util.Arrays.stream(copyToPrOrganizationsConfig.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());
                    if (orgIds.isEmpty()) {
                        apply = 0;
                        orgIds = java.util.Collections.singletonList(-1);
                    }
                }

                List<Supplier> supplierList = supplierRepository.getERPSupplierByTenantAndSourcingStatus(tenant.getRecId(), apply, orgIds);

                for (Supplier supplier : supplierList) {

                    Boolean isExistingERPSupplier = isExistingERPSupplier = supplierService.isExistingERPSupplier(supplier.getTaxId(), tenant.getRecId());;
//                    try {
//                        isExistingERPSupplier = supplierService.isExistingERPSupplier(supplier.getTaxId(), tenant.getRecId());
//                    } catch (Exception e) {
//                        //Log & Continue
//                        continue;
//                    }

                    LOGGER.info(dtf.format(LocalDateTime.now()) + " TaxRegistrationNumber : " + supplier.getTaxId() + " is " + isExistingERPSupplier);

                    if(!isExistingERPSupplier) {
                        //Create ERP Supplier

                        SupplierWebWorkSearchByTPShortNameRequest supplierWebWorkSearchByTPShortNameRequest = new SupplierWebWorkSearchByTPShortNameRequest();
                        supplierWebWorkSearchByTPShortNameRequest.setTPShortName(supplier.getShortName());
                        supplierWebWorkSearchByTPShortNameRequest.setInvitationCode(isSearchByInvitationCode ? (response != null && !response.isEmpty() ? response.get(0).getInvitation_code() : AppUtil.getTenantId()) : "");
                        SupplierWebworksDto supplierWebworksDto = supplierService.getSupplierWebWorkByTPShortName(supplierWebWorkSearchByTPShortNameRequest);

                        if (supplierWebworksDto != null) {
                            //

                            Supplier existingSupplier = supplierRepository.getSupplierByShortName(supplierWebworksDto.getTPShortName(), tenant.getRecId());
                            boolean isShowSupplierLocalLanguage = tenantConfigService.isShowSupplierLocalLanguage(tenant.getRecId());
                            String supplierFieldName = tenantConfigService.getSupplierFieldName(tenant.getRecId());

                            String supplierName = SupplierNameUtil.resolveSupplierName(existingSupplier, isShowSupplierLocalLanguage, supplierFieldName);

                            CreateSupplierRequest createSupplierRequest = new CreateSupplierRequest();
                            createSupplierRequest.setSupplier("บจ.ดีลอยท์ คอนซัลติ้ง"); //(existingSupplier.getFullCompanyNameLocal());//(supplierName);
                            createSupplierRequest.setTaxOrganizationType("Corporation");
                            createSupplierRequest.setSupplierType("Non-Trade Supplier");
                            createSupplierRequest.setBusinessRelationship("Spend Authorized");
                            createSupplierRequest.setTaxRegistrationCountryCode(supplierWebworksDto.getCoreCountry().getCountryCode());
                            createSupplierRequest.setOneTimeSupplierFlag(false);
                            createSupplierRequest.setTaxRegistrationNumber(existingSupplier.getTaxId());
                            createSupplierRequest.setUseWithholdingTaxFlag(false);

                            // Set Core Address
                            List<OracleAddressDto> oracleAddressDtoList = new ArrayList<>();
                            OracleAddressDto oracleAddressDto = new OracleAddressDto();
                            oracleAddressDto.setAddressName(!supplierWebworksDto.getBranch().isEmpty() ? supplierWebworksDto.getBranch() : "No branch");
                            oracleAddressDto.setCountryCode(supplierWebworksDto.getCoreCountry().getCountryCode());
                            oracleAddressDto.setCounty(supplierWebworksDto.getCoreCountry().getCountryName());

                            if(supplierWebworksDto.getCoreOrgAddressCompany() != null) {
                                String address = isShowSupplierLocalLanguage ? supplierWebworksDto.getCoreOrgAddressCompany().getAddress().getHouseNoLocal() :
                                        supplierWebworksDto.getCoreOrgAddressCompany().getAddress().getHouseNoInter();
                                oracleAddressDto.setAddressLine1(address);

//                                oracleAddressDto.setAddressLine2("");

                                String subDistrict = isShowSupplierLocalLanguage ? supplierWebworksDto.getCoreOrgAddressCompany().getAddress().getSubDistrictLocal() :
                                        supplierWebworksDto.getCoreOrgAddressCompany().getAddress().getSubDistrictInter();
                                oracleAddressDto.setAddressLine3(subDistrict);

                                String district = isShowSupplierLocalLanguage ? supplierWebworksDto.getCoreOrgAddressCompany().getAddress().getCityLocal() :
                                        supplierWebworksDto.getCoreOrgAddressCompany().getAddress().getCityInter();
                                oracleAddressDto.setAddressLine4(district);

                                String province = isShowSupplierLocalLanguage ? supplierWebworksDto.getCoreOrgAddressCompany().getAddress().getStateLocal() :
                                        supplierWebworksDto.getCoreOrgAddressCompany().getAddress().getStateInter();
                                oracleAddressDto.setProvince(province);
                            }

                            oracleAddressDto.setPostalCode((supplierWebworksDto.getCoreOrgAddressCompany() != null) ?
                                    (supplierWebworksDto.getCoreOrgAddressCompany().getAddress().getPostalCode()) : "No postal code");

                            oracleAddressDto.setAddressPurposeOrderingFlag(true);
                            oracleAddressDto.setAddressPurposeRemitToFlag(true);
                            oracleAddressDto.setAddressPurposeRFQOrBiddingFlag(true);
                            oracleAddressDtoList.add(oracleAddressDto);
                            createSupplierRequest.setAddresses(oracleAddressDtoList);


                            // Set Core Sites
                            List<OracleSiteDto> oracleSiteDtoList = new ArrayList<>();
                            OracleSiteDto oracleSiteDto = new OracleSiteDto();
                            oracleSiteDto.setSupplierSite("NT 30");
                            oracleSiteDto.setProcurementBU("CP Axtra BU");
                            oracleSiteDto.setSupplierAddressName(!supplierWebworksDto.getBranch().isEmpty() ? supplierWebworksDto.getBranch() : "No branch");
                            oracleSiteDto.setSitePurposePayFlag(true);
                            oracleSiteDto.setSitePurposePayFlag(true);
                            oracleSiteDto.setPayOnReceiptFlag(false);
                            oracleSiteDto.setMatchApprovalLevelCode("THREE");
                            oracleSiteDto.setPaymentPriority("99");
                            oracleSiteDtoList.add(oracleSiteDto);
                            createSupplierRequest.setSites(oracleSiteDtoList);

                            List<OracleContactDto> oracleContactDtoList = new ArrayList<>();

                            if(supplierWebworksDto.getCoreContactPersons() != null) {
                                for (CoreContactPersonDto coreContactPersonDto : supplierWebworksDto.getCoreContactPersons()) {
                                    OracleContactDto oracleContactDto = new OracleContactDto();

                                    String contactFullName = isShowSupplierLocalLanguage ? coreContactPersonDto.getFullNameLocal() :
                                            coreContactPersonDto.getFullNameInter();
                                    oracleContactDto.setFirstName(contactFullName);
                                    oracleContactDto.setEmail(coreContactPersonDto.getEmail());
                                    oracleContactDto.setAdministrativeContactFlag(true);

                                    oracleContactDtoList.add(oracleContactDto);
                                }
                            }

                            createSupplierRequest.setContacts(oracleContactDtoList);

                            LOGGER.info(dtf.format(LocalDateTime.now()) + " createSupplierRequest object : " + createSupplierRequest);

                            CreateSupplierResponse createSupplierResponse = null;
                            try {
                                createSupplierResponse = supplierService.createERPSupplier(createSupplierRequest, tenant.getRecId());
                            } catch (Exception e) {
                                String message = e.getMessage();
                                if(message.contains("tax registration number already exists")) {
                                    LOGGER.info(dtf.format(LocalDateTime.now()) + " tax registration number already exists : " + createSupplierRequest.getTaxRegistrationNumber());
                                    supplier.setActiveOnERP(true);
                                    supplierRepository.save(supplier);
                                }
                            }

                            if(createSupplierResponse != null && createSupplierResponse.getSupplierId() > 0) {
                                LOGGER.info(dtf.format(LocalDateTime.now()) + " createSupplierResponse object : " + createSupplierResponse);

                                supplier.setActiveOnERP(true);
                                supplierRepository.save(supplier);
                            }


                        }
                    }
                }
            }
        }

        LOGGER.info(dtf.format(LocalDateTime.now()) + " End Daily Update Oracle Supplier");
    }


    @Scheduled(cron = "0 0 7,11,14,16 * * *", zone = "Asia/Bangkok") // every day 00:00:01
    //@Scheduled(cron = "0 */30 * * * ?") // @Scheduled(cron = "1 0 0 * * ?") // every day 00:00:01
    @SchedulerLock(name = "dailyUpdateOracleCopyToPR") //, lockAtLeastFor = "PT1M", lockAtMostFor = "PT10M")
    @Async
    @Transactional
    public void executeDailyUpdateOracleCopyToPR() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LOGGER.info(dtf.format(LocalDateTime.now()) + " Sourcing Request: Execute Daily Update Oracle CopyToPR everyday 00:00:01");

        LOGGER.info(dtf.format(LocalDateTime.now()) + " Start Daily Update Oracle CopyToPR");

        List<Tenant> tenantList = tenantRepository.findAll();

        for (Tenant tenant : tenantList) {

            //List<TenantInfoResponse> response = uaaService.getTenantInfo(tenant.getCode());
            boolean isCopyToPRViaERP = tenantConfigService.getCopyToPRViaERP(tenant.getRecId());

            if(isCopyToPRViaERP) {

                String copyToPrOrganizationsConfig = tenantConfigService.getCopyToPrOrganizations(tenant.getRecId());
                List<Integer> orgIds;
                int apply = 1;
                if (copyToPrOrganizationsConfig == null || copyToPrOrganizationsConfig.isBlank()) {
                    apply = 0;
                    orgIds = java.util.Collections.singletonList(-1);
                } else {
                    orgIds = java.util.Arrays.stream(copyToPrOrganizationsConfig.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());
                    if (orgIds.isEmpty()) {
                        apply = 0;
                        orgIds = java.util.Collections.singletonList(-1);
                    }
                }

                List<Request> requestList = requestRepository.getERPRequestByTenantAndSourcingStatus(tenant.getRecId(), apply, orgIds);


                for (Request request : requestList) {

                    List<RequestItem> requestItemList = request.getRequestItemList();
                    List<Long> requestItemIds = requestItemList.stream()
                            .filter(item -> item.getSourcingStatus().getRecId() == 9)
                            .map(RequestItem::getRecId)
                            .collect(Collectors.toList());

                    try {
                        CreateOraclePrResponse createOraclePrResponse = prService.createERPPurchaseRequisition(tenant, request.getRequestNo(), requestItemIds);
//
//                        Integer statusCode = purchaseRequisitionResponse.getHeader().getCode();
//                        String prNumber = purchaseRequisitionResponse.getHeader().getPrNumber();
//                        if (200 != statusCode) {
//                            //return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7055, String.format(ApiMessage.E7055.description(), purchaseRequisitionResponse.getHeader().getMessage())), HttpStatus.INTERNAL_SERVER_ERROR);
//                        }
//
//                        Pr pr = prService.savePrNumber(prNumber);
//                        prService.saveRequestItemPR(pr.getRecId(), requestItemIds);
//
//                        // Set IsCopyToPRFail flag
//                        request.setIsCopyToPRFail(false);
//                        requestRepository.save(request);

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        LOGGER.info(dtf.format(LocalDateTime.now()) + " End Daily Update Oracle CopyToPR");
    }

}
