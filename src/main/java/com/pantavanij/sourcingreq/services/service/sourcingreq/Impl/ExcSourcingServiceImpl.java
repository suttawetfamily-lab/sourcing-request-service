package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.ExcSourcingRequestItemKey;
import com.pantavanij.sourcingreq.services.domain.mapper.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;

import com.pantavanij.sourcingreq.services.enums.Role;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.Activity.*;
import static com.pantavanij.sourcingreq.services.enums.DeptApprovalStatus.*;
import static com.pantavanij.sourcingreq.services.enums.ExcSourcingStatus.*;
import static com.pantavanij.sourcingreq.services.enums.SearchRequestType.*;
import static com.pantavanij.sourcingreq.services.enums.SourcingStatus.SOURCING_NONE;
import static com.pantavanij.sourcingreq.services.enums.SourcingType.SOURCING_TYPE_DRAFT;
import static com.pantavanij.sourcingreq.services.enums.SourcingType.SOURCING_TYPE_EXCEPTIONAL_SOURCING;
import static com.pantavanij.sourcingreq.services.enums.SourcingStatus.SOURCING_REJECTED;
import static com.pantavanij.sourcingreq.services.enums.SourcingStatus.SOURCING_AWAITING_RESPONSE;
import static com.pantavanij.sourcingreq.services.enums.SourcingStatus.SOURCING_QUALIFIED_SUPPLIER;
import static com.pantavanij.sourcingreq.services.enums.TenantExcSourcingStatus.*;

import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@RequiredArgsConstructor
@Service
@Slf4j
public class ExcSourcingServiceImpl implements ExcSourcingService {

    private final ExcSourcingRepository excSourcingRepository;
    private final UaaService uaaService;
    private final EPAuthService epAuthService;
    private final TenantService tenantService;
    private final SourcingReferenceService sourcingReferenceService;
    private final ExcSourcingRequestItemRepository excSourcingRequestItemRepository;
    private final RequestItemRepository requestItemRepository;
    private final ExcSourcingStatusRepository excSourcingStatusRepository;
    private final ApprovalStatusRepository approvalStatusRepository;
    private final DeptApprovalStatusRepository deptApprovalStatusRepository;
    private final TenantApprovalStatusRepository tenantApprovalStatusRepository;
    private final TenantExcSourcingStatusRepository tenantExcSourcingStatusRepository;
    private final ExcSourcingApproverRepository excSourcingApproverRepository;
    private final ApproverRepository approverRepository;
    private final RequestDeptApproverService requestDeptApproverService;
    private final RequestService requestService;
    private final ExcSourcingDeptApproverService excSourcingDeptApproverService;
    private final SourcingStatusRepository sourcingStatusRepository;
    private final SourcingTypeRepository sourcingTypeRepository;
    private final ExcSourcingPurchaserRepository excSourcingPurchaserRepository;
    private final ExcSourcingPurchaserService excSourcingPurchaserService;
    private final PurchaserRepository purchaserRepository;
    private final ExistingPriceItemRepository existingPriceItemRepository;
    private final ExistingPriceItemSupplierRepository existingPriceItemSupplierRepository;
    private final ExistingPriceItemAttachmentRepository existingPriceItemAttachmentRepository;
    private final RequestRepository requestRepository;
    private final RequestPurchasingApproverService requestPurchasingApproverService;
    private final EmailService emailService;

    @Override
    public ExcSourcingApproverSearchDto searchExcSourcingApproverByCondition(
            ExcSourcingApproverSearchRequest searchRequest, Pageable pageable) {

        Page<ExcSourcing> excSourcings =
                excSourcingRepository.findAll(
                        Specification.where(
                                getExcSourcingApproverSpecificationByCondition(searchRequest)
                        ),
                        pageable
                );

        int totalPage = excSourcings.getTotalPages();
        long total = excSourcings.getTotalElements();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);

        // ✅ Map entity → dto (convert timezone)
        List<ExcSourcingDto> resultList = ExcSourcingMapper.INSTANCE.toDtoList(excSourcings.getContent(), timeZone);

        // ======================================================
        // 🔹 Enrich approval status (ของ user ปัจจุบัน)
        // ======================================================
        String currentUser = AppUtil.getUserName();

        for (int i = 0; i < resultList.size(); i++) {
            ExcSourcingDto dto = resultList.get(i);
            ExcSourcing entity = excSourcings.getContent().get(i);

            DeptApprovalStatusDto deptStatusDto = null;
            DeptApprovalStatusDto purStatusDto = null;
            DeptApprovalStatusDto userStatusDto = null;

            boolean isUserDept = false;
            boolean isUserPurchaser = false;

            // ✅ 1) หา status ของ user ฝั่ง Dept
            if (entity.getExcSourcingApprovers() != null) {
                for (ExcSourcingApprover approver : entity.getExcSourcingApprovers()) {
                    if (approver.getApprover() != null &&
                            currentUser.equalsIgnoreCase(approver.getApprover().getLoginId())) {
                        deptStatusDto = DeptApprovalStatusMapper.INSTANCE
                                .toDeptApprovalStatusDto(approver.getDeptApprovalStatus());
                        isUserDept = true;
                        break;
                    }
                }
            }

            // ✅ 2) หา status ของ user ฝั่ง Purchaser
            if (entity.getExcSourcingPurchasers() != null) {
                for (ExcSourcingPurchaser purchaser : entity.getExcSourcingPurchasers()) {
                    if (purchaser.getApprover() != null &&
                            currentUser.equalsIgnoreCase(purchaser.getApprover().getLoginId())) {
                        purStatusDto = DeptApprovalStatusMapper.INSTANCE
                                .toDeptApprovalStatusDto(purchaser.getApprovalStatus());
                        isUserPurchaser = true;
                        break;
                    }
                }
            }

            // ===============================
            // ✅ 3) ตัดสินใจเลือก userStatusDto
            // ===============================
            String deptName = (deptStatusDto != null && deptStatusDto.getName() != null)
                    ? deptStatusDto.getName().trim()
                    : null;
            String purName = (purStatusDto != null && purStatusDto.getName() != null)
                    ? purStatusDto.getName().trim()
                    : null;

            boolean deptApprovedByUser = isApproved(deptName);
            boolean purchaserApprovedByUser = isApproved(purName);
            boolean deptChainFullyApproved = isDeptChainFullyApproved(entity.getExcSourcingApprovers());

            // กรณี user เป็นทั้ง Dept & Purchaser
            if (isUserDept && isUserPurchaser) {

                // ถ้า user อนุมัติเป็น Purchaser ไปแล้ว → แสดงสถานะ purchaser ของตัวเอง
                if (purchaserApprovedByUser) {
                    userStatusDto = purStatusDto;
                } else {
                    // ยังไม่อนุมัติในฝั่ง Purchaser
                    if (deptApprovedByUser) {
                        // ถ้า Dept ทั้งสายอนุมัติจบแล้ว → เช็คว่า user คือ "คนถัดไป" ในสาย Purchaser หรือไม่
                        if (deptChainFullyApproved && isNextPurchaser(entity.getExcSourcingPurchasers(), currentUser)) {
                            // เป็นคนถัดไป → ต้องเห็นว่า "AWAITING" (รออนุมัติในบทบาทใหม่)
                            userStatusDto = awaitingDto();
                        } else {
                            // ไม่ใช่คนถัดไปใน Purchaser → ให้คง "เห็นว่าอนุมัติไปแล้ว" ในบทบาทที่ผ่านมา
                            userStatusDto = deptStatusDto; // APPROVED
                        }
                    } else {
                        // ยังอนุมัติ Dept ของตัวเองไม่เสร็จ → แสดงสถานะ Dept ที่ตัวเองมี
                        userStatusDto = deptStatusDto; // อาจเป็น AWAITING/PENDING/...
                    }
                }

            } else if (isUserPurchaser) {
                // เป็นเฉพาะ Purchaser
                // ถ้าจะ override จาก Dept มาที่ Purchaser ต้องเป็นสองกรณี:
                //   1) user อนุมัติ purchaser ไปแล้ว (ให้เห็นว่า APPROVED)
                //   2) Dept ทั้งสายอนุมัติครบ และ user เป็นคนถัดไปใน Purchaser (ให้เห็นว่า AWAITING)
                if (purchaserApprovedByUser) {
                    userStatusDto = purStatusDto;
                } else if (deptChainFullyApproved && isNextPurchaser(entity.getExcSourcingPurchasers(), currentUser)) {
                    userStatusDto = awaitingDto();
                } else {
                    userStatusDto = purStatusDto; // สถานะจริงของตัวเองในฝั่ง purchaser (เช่น AWAITING แต่ยังไม่ถึงคิวจริงก็แสดงตามจริง)
                }

            } else if (isUserDept) {
                // เป็นเฉพาะ Dept
                userStatusDto = deptStatusDto;
            } else {
                // ไม่ได้อยู่ในสองสาย → ไม่ตั้งค่า (หรือจะปล่อย null ให้ UI handle)
                userStatusDto = null;
            }

            // ✅ 4) ใส่กลับเข้า DTO
            dto.setDeptApprovalStatus(deptStatusDto);
            dto.setPurApprovalStatus(purStatusDto);
            dto.setApprovalStatus(userStatusDto);
        }

        // ✅ Build response
        ExcSourcingApproverSearchDto response = new ExcSourcingApproverSearchDto();
        response.setExcSourcingDtoList(resultList);
        response.setTotal(total);
        response.setTotalPage(totalPage);
        response.setPageSize(searchRequest.getPageSize());
        return response;
    }

    // === Helper: เช็คชื่อสถานะ (ใช้รูปแบบเดิมของโปรเจกต์คุณ) ===
    private static boolean equalsIgnoreCase(String a, String b) {
        return a != null && b != null && a.equalsIgnoreCase(b);
    }

    private static boolean isApproved(String statusName) {
        // ใช้ enum เดิมถ้ามี: DEPT_APPROVAL_APPROVED.code()
        return equalsIgnoreCase(statusName, DEPT_APPROVAL_APPROVED.code())
                || equalsIgnoreCase(statusName, "APPROVED");
    }

    private static boolean isActionable(String statusName) {
        // สถานะที่ถือว่า "ถึงคิว/รอดำเนินการ" ในสายถัดไป
        return equalsIgnoreCase(statusName, DEPT_APPROVAL_AWAITING.code())
                || equalsIgnoreCase(statusName, DEPT_APPROVAL_PENDING.code());
    }

    // === Helper: Dept ทั้งสายอนุมัติครบทุกคนหรือยัง ===
    private static boolean isDeptChainFullyApproved(List<ExcSourcingApprover> approvers) {
        if (approvers == null || approvers.isEmpty()) return true; // ไม่มีใครอนุมัติ ถือว่าพร้อมผ่าน
        for (ExcSourcingApprover a : approvers) {
            String name = (a.getDeptApprovalStatus() != null && a.getDeptApprovalStatus().getName() != null)
                    ? a.getDeptApprovalStatus().getName().trim()
                    : null;
            if (!isApproved(name)) {
                return false;
            }
        }
        return true;
    }

    // === Helper: คนถัดไปในสาย Purchaser คือใคร (เช็คแบบลำดับแรกที่ยัง actionable) ===
    private static boolean isNextPurchaser(List<ExcSourcingPurchaser> purchasers, String currentUser) {
        if (purchasers == null || purchasers.isEmpty()) return false;

        // สมมติ list ถูกจัดเรียงลำดับแล้วใน DB/Repository
        for (ExcSourcingPurchaser p : purchasers) {
            String pStatus = (p.getApprovalStatus() != null && p.getApprovalStatus().getName() != null)
                    ? p.getApprovalStatus().getName().trim()
                    : null;

            if (isActionable(pStatus)) {
                // เจอ "คนถัดไป" ในสาย Purchaser → เทียบกับ user ปัจจุบัน
                String loginId = (p.getApprover() != null) ? p.getApprover().getLoginId() : null;
                return loginId != null && loginId.equalsIgnoreCase(currentUser);
            }

            // ถ้าสถานะเป็น REJECT/CANCEL ปกติถือว่าจบสายหรือหยุด — ข้ามไป
            // ถ้าเป็น APPROVED → หาคนถัดไปต่อ
        }
        return false;
    }

    // === Helper: สร้าง AWAITING Dto สั้น ๆ (ให้ UI เห็นว่า "รออนุมัติ") ===
    private static DeptApprovalStatusDto awaitingDto() {
        DeptApprovalStatusDto dto = new DeptApprovalStatusDto();
        dto.setName(DEPT_APPROVAL_AWAITING.code());
        dto.setDescription(DEPT_APPROVAL_AWAITING.code());
        return dto;
    }




    private Specification<ExcSourcing> getExcSourcingApproverSpecificationByCondition(
            ExcSourcingApproverSearchRequest searchRequest) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // =============================================
            // 🔹 JOIN CHAIN: ExcSourcing -> Request
            // =============================================
            Join<ExcSourcing, Request> joinRequest = root.join("request", JoinType.LEFT);

            // =============================================
            // 🔹 TENANT FILTER
            // =============================================
            Integer tenantId = searchRequest.getTenant().getRecId();
            if (tenantId != null) {
                predicates.add(cb.equal(root.get("tenant").get("recId"), tenantId));
            }

            // =============================================
            // 🔹 USER CONTEXT (approver / purchaser)
            // =============================================
            String currentUser = AppUtil.getUserName();
            if (!StringUtils.isEmpty(currentUser)) {

                Join<ExcSourcing, ExcSourcingApprover> joinDept = root.join("excSourcingApprovers", JoinType.LEFT);
                Join<ExcSourcingApprover, Approver> joinApprover = joinDept.join("approver", JoinType.LEFT);

                Join<ExcSourcing, ExcSourcingPurchaser> joinPurchaser = root.join("excSourcingPurchasers", JoinType.LEFT);
                Join<ExcSourcingPurchaser, Approver> joinPurchaserUser = joinPurchaser.join("approver", JoinType.LEFT);

                if (searchRequest.getApproveStatusList() == null || searchRequest.getApproveStatusList().isEmpty()) {
                    Predicate deptAwaiting = cb.equal(joinDept.get("deptApprovalStatus").get("recId"), DEPT_APPROVAL_AWAITING.id());
                    Predicate deptApproved = cb.equal(joinDept.get("deptApprovalStatus").get("recId"), DEPT_APPROVAL_APPROVED.id());
                    Predicate deptRejected = cb.equal(joinDept.get("deptApprovalStatus").get("recId"), DEPT_APPROVAL_REJECTED.id());
                    Predicate deptCancelled = cb.equal(joinDept.get("deptApprovalStatus").get("recId"), DEPT_APPROVAL_CANCELLED.id());

                    Predicate purAwaiting = cb.equal(joinPurchaser.get("approvalStatus").get("recId"), DEPT_APPROVAL_AWAITING.id());
                    Predicate purApproved = cb.equal(joinPurchaser.get("approvalStatus").get("recId"), DEPT_APPROVAL_APPROVED.id());
                    Predicate purRejected = cb.equal(joinPurchaser.get("approvalStatus").get("recId"), DEPT_APPROVAL_REJECTED.id());
                    Predicate purCancelled = cb.equal(joinPurchaser.get("approvalStatus").get("recId"), DEPT_APPROVAL_CANCELLED.id());

                    predicates.add(cb.or(
                            cb.or(deptAwaiting, deptApproved, deptRejected, deptCancelled),
                            cb.or(purAwaiting, purApproved, purRejected, purCancelled)
                    ));
                }


                List<Integer> visibleStatusIds = Arrays.asList(
                        DEPT_APPROVAL_AWAITING.id(),
                        DEPT_APPROVAL_APPROVED.id(),
                        DEPT_APPROVAL_REJECTED.id()
                );

                // ✅ Dept Approver
                Predicate isDeptApprover = cb.and(
                        cb.equal(joinApprover.get("loginId"), currentUser),
                        joinDept.get("deptApprovalStatus").get("recId").in(visibleStatusIds)
                );

                // ✅ Purchaser Approver
                Predicate isPurchasingApprover = cb.and(
                        cb.equal(joinPurchaserUser.get("loginId"), currentUser),
                        joinPurchaser.get("approvalStatus").get("recId").in(visibleStatusIds)
                );

                predicates.add(cb.or(isDeptApprover, isPurchasingApprover));

                // =============================================
                // 🔹 FILTER BY APPROVE STATUS LIST
                // =============================================
                if (searchRequest.getApproveStatusList() != null && !searchRequest.getApproveStatusList().isEmpty()) {
                    List<Integer> approvalStatusIds = searchRequest.getApproveStatusList().stream()
                            .map(DeptApprovalStatusDto::getRecId)
                            .collect(Collectors.toList());

                    Predicate matchDeptApprover = cb.and(
                            cb.equal(joinApprover.get("loginId"), currentUser),
                            joinDept.get("deptApprovalStatus").get("recId").in(approvalStatusIds)
                    );

                    Predicate matchPurchaserApprover = cb.and(
                            cb.equal(joinPurchaserUser.get("loginId"), currentUser),
                            joinPurchaser.get("approvalStatus").get("recId").in(approvalStatusIds)
                    );

                    predicates.add(cb.or(matchDeptApprover, matchPurchaserApprover));
                }
            }

            // =============================================
            // 🔹 DATE RANGE
            // =============================================
            Date requestFromDateCondition = searchRequest.getRequestFromDate();
            Date requestToDateCondition = searchRequest.getRequestToDate();
            if (requestFromDateCondition != null && requestToDateCondition != null) {
                Timestamp fromDate = DateTimeUtil.getTimestampUTC(DateTimeUtil.subtractHour(requestFromDateCondition, 7));
                Timestamp toDate = DateTimeUtil.getTimestampUTC(
                        DateTimeUtil.subtractHour(DateTimeUtil.addDate(requestToDateCondition, 1), 7)
                );
                predicates.add(cb.between(joinRequest.get("requestDate"), fromDate, toDate));
            } else if (requestFromDateCondition != null) {
                Timestamp fromDate = DateTimeUtil.getTimestampUTC(DateTimeUtil.subtractHour(requestFromDateCondition, 7));
                predicates.add(cb.greaterThan(joinRequest.get("requestDate"), fromDate));
            } else if (requestToDateCondition != null) {
                Timestamp toDate = DateTimeUtil.getTimestampUTC(
                        DateTimeUtil.subtractHour(DateTimeUtil.addDate(requestToDateCondition, 1), 7)
                );
                predicates.add(cb.lessThanOrEqualTo(joinRequest.get("requestDate"), toDate));
            }

            // =============================================
            // 🔹 JOIN STATUS (relation-based)
            // =============================================
//            Join<ExcSourcing, TenantExcSourcingStatus> joinExcStatus = root.join("tenantExcSourcingStatus", JoinType.LEFT);
//            Join<ExcSourcing, DeptApprovalStatus> joinApproval = root.join("approvalStatus", JoinType.LEFT);
//            Join<ExcSourcing, DeptApprovalStatus> joinDeptApproval = root.join("deptApprovalStatus", JoinType.LEFT);

            // =============================================
            // 🔹 DYNAMIC CONDITION (multi-field search)
            // =============================================
            List<ConditionSearchRequest> conditionSearchRequestList = searchRequest.getConditionSearchList();
            if (conditionSearchRequestList != null && !conditionSearchRequestList.isEmpty()) {
                for (ConditionSearchRequest condition : conditionSearchRequestList) {
                    String searchField = condition.getSearchField();
                    String searchValue = condition.getSearchValue();
                    if (!StringUtils.isEmpty(searchField) && !StringUtils.isEmpty(searchValue)) {

                        if ("requester".equalsIgnoreCase(searchField)) {
                            List<String> loginIds = getLoginIdByUsername(searchValue, Role.REQUESTER.privilegeCode());
                            predicates.add(cb.in(joinRequest.get(REQUESTER.description())).value(loginIds));
                        } else if ("projectCode".equalsIgnoreCase(searchField)) {
                            Join<Request, RequestProject> joinProject = joinRequest.join("requestProjectList", JoinType.LEFT);
                            Predicate predicateProjectCode = cb.like(cb.lower(joinProject.get("projectCode")), "%" + searchValue.toLowerCase() + "%");
                            Predicate predicateRequestCode = cb.like(cb.lower(joinRequest.get("projectCode")), "%" + searchValue.toLowerCase() + "%");
                            predicates.add(cb.or(predicateProjectCode, predicateRequestCode));

                        } else if ("projectName".equalsIgnoreCase(searchField)) {
                            Join<Request, RequestProject> joinProject = joinRequest.join("requestProjectList", JoinType.LEFT);
                            Predicate predicateProject = cb.like(cb.lower(joinProject.get("projectName")), "%" + searchValue.toLowerCase() + "%");

                            Join<Request, RequestDepartment> joinDepartment = joinRequest.join("requestDepartmentList", JoinType.LEFT);
                            Predicate predicateDepartment = cb.like(cb.lower(joinDepartment.get("departmentName")), "%" + searchValue.toLowerCase() + "%");
                            Predicate predicateProjectName = cb.like(cb.lower(joinRequest.get("projectName")), "%" + searchValue.toLowerCase() + "%");
                            predicates.add(cb.or(predicateProject, predicateDepartment, predicateProjectName));

                        } else if ("projectLabel".equalsIgnoreCase(searchField)) {
                            Join<Request, RequestProject> joinProject = joinRequest.join("requestProjectList", JoinType.LEFT);
                            Predicate predicateProjectCode = cb.like(cb.lower(joinProject.get("projectCode")), "%" + searchValue.toLowerCase() + "%");
                            Predicate predicateProjectName = cb.like(cb.lower(joinProject.get("projectName")), "%" + searchValue.toLowerCase() + "%");

                            Join<Request, RequestDepartment> joinDepartment = joinRequest.join("requestDepartmentList", JoinType.LEFT);
                            Predicate predicateDepartment = cb.like(cb.lower(joinDepartment.get("departmentName")), "%" + searchValue.toLowerCase() + "%");

                            predicates.add(cb.or(predicateProjectCode, predicateProjectName, predicateDepartment));

                        } else if ("requestNo".equalsIgnoreCase(searchField)) {
                            predicates.add(cb.like(cb.lower(joinRequest.get("requestNo")), "%" + searchValue.toLowerCase() + "%"));

                        } else if ("requestName".equalsIgnoreCase(searchField)) {
                            predicates.add(cb.like(cb.lower(joinRequest.get("requestName")), "%" + searchValue.toLowerCase() + "%"));

                        } else {
                            switch (searchField.toLowerCase()) {
                                case "excsourcingdocno":
                                    predicates.add(cb.like(cb.lower(root.get("excSourcingDocNo")), "%" + searchValue.toLowerCase() + "%"));
                                    break;
                                case "createdby":
                                    predicates.add(cb.like(cb.lower(root.get("createdBy")), "%" + searchValue.toLowerCase() + "%"));
                                    break;
                                default:
                                    log.warn("Unsupported searchField: {}", searchField);
                                    break;
                            }
                        }
                    }
                }
            }

            // =============================================
            // 🔹 FILTER BY EXC SOURCING STATUS
            // =============================================
            if (searchRequest.getExcSourcingStatusList() != null && !searchRequest.getExcSourcingStatusList().isEmpty()) {
                List<Integer> statusIds = searchRequest.getExcSourcingStatusList().stream()
                        .map(ExcSourcingStatusDto::getRecId)
                        .collect(Collectors.toList());
                predicates.add(root.get("excSourcingStatus").get("recId").in(statusIds));
            }

            // =============================================
            // 🔹 ALWAYS EXCLUDE DRAFT STATUS
            // =============================================
            Predicate notDraft = cb.notEqual(
                    root.get("excSourcingStatus").get("recId"),
                    TENANT_EXC_SOURCING_DRAFT.id()
            );
            predicates.add(notDraft);

//            // =============================================
//            // 🔹 DEFAULT APPROVAL STATUS (ถ้าไม่ได้ส่งมา)
//            // =============================================
//            if (searchRequest.getApproveStatusList() == null || searchRequest.getApproveStatusList().isEmpty()) {
//                Predicate predicateAwaiting = cb.equal(joinDeptApproval.get("recId"), DEPT_APPROVAL_AWAITING.id());
//                Predicate predicateApproved = cb.equal(joinDeptApproval.get("recId"), DEPT_APPROVAL_APPROVED.id());
//                Predicate predicateRejected = cb.equal(joinDeptApproval.get("recId"), DEPT_APPROVAL_REJECTED.id());
//                Predicate predicateCancelled = cb.equal(joinDeptApproval.get("recId"), DEPT_APPROVAL_CANCELLED.id());
//                predicates.add(cb.or(predicateAwaiting, predicateApproved, predicateRejected, predicateCancelled));
//            }

            // =============================================
            // 🔹 DISTINCT เพื่อไม่ให้ซ้ำเมื่อ JOIN หลายตาราง (Approver + Purchaser)
            // =============================================
            query.distinct(true);

            // =============================================
            // 🔹 RETURN FINAL PREDICATES
            // =============================================
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }


    private List<String> getLoginIdByUsername(String searchValue, String privilegeCode) {
        List<ConditionSearchRequest> conditionSearchList = new ArrayList<>();
        conditionSearchList.add(ConditionSearchRequest.builder().searchField("username").searchValue(searchValue).build());

        EPAuthUserSearchRequest ePAuthUserSearchRequest = new EPAuthUserSearchRequest();
        ePAuthUserSearchRequest.setTenantId(AppUtil.getTenantId());
        ePAuthUserSearchRequest.setPage(1);
        ePAuthUserSearchRequest.setPageSize(99999);
        ePAuthUserSearchRequest.setSortBy("username");
        ePAuthUserSearchRequest.setSortOrder("desc");
        ePAuthUserSearchRequest.setConditionSearchList(conditionSearchList);

        EPAuthUserListResponse response = epAuthService.getListByConditions(ePAuthUserSearchRequest, privilegeCode);

        if (response != null) {
            return response.getData().stream().map(EPAuthUserDTO::getLoginId).collect(Collectors.toList());
        }
        return null;
    }

    @Transactional
    @Override
    public ExcSourcingReponseDto saveExcSourcing(ExcSourcingRequestDto excSourcingRequestDto) {

        Boolean isSubmit = Boolean.TRUE.equals(excSourcingRequestDto.getIsSubmit());

        // 1. ดึง tenant
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());

        // 2. หา requestItem list จาก requestItemIdList
        List<RequestItem> requestItems = requestItemRepository.getRequestItemByRequestItemIdList(excSourcingRequestDto.getRequestItemIds());
        if (requestItems == null || requestItems.isEmpty()) {
            throw new BusinessException("No RequestItem found for given IDs: " + excSourcingRequestDto.getRequestItemIds());
        }

        // 3. ตรวจสอบว่าเป็นการ update หรือ create ใหม่
        ExcSourcing excSourcing;
        boolean isNew = false;

        if (StringUtils.isNotBlank(excSourcingRequestDto.getExcSourcingDocNo())) {
            // มี docNo → อัปเดต
            excSourcing = excSourcingRepository.findByExcSourcingDocNo(excSourcingRequestDto.getExcSourcingDocNo())
                    .orElseThrow(() -> new BusinessException("ExcSourcing not found for docNo: " + excSourcingRequestDto.getExcSourcingDocNo()));
            excSourcing.setUpdatedBy(AppUtil.getUserName());
            excSourcing.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        } else {
            // ไม่มี docNo → create ใหม่
            isNew = true;
            excSourcing = new ExcSourcing();
            String excSourcingDocNo = sourcingReferenceService.generateSourcingNumber(
                    "EXCEPTIONAL_SOURCING", tenant.getRecId(), excSourcingRequestDto.getOrganizationId()
            );
            excSourcing.setExcSourcingDocNo(excSourcingDocNo);
            excSourcing.setTenant(tenant);
            excSourcing.setCreatedBy(AppUtil.getUserName());
            excSourcing.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        }

        // 4. ใช้ requestItem ตัวแรกเป็น sample
        RequestItem sampleRequestItem = requestItems.get(0);

        // 5. ตั้งค่า ExcSourcingStatus
        ExcSourcingStatus excStatus = excSourcingStatusRepository
                .findById(isSubmit ? EXC_SOURCING_AWAITING_RESPONSE.id() : EXC_SOURCING_DRAFT.id())
                .orElseThrow(() -> new BusinessException("ExcSourcingStatus not found"));
        excSourcing.setExcSourcingStatus(excStatus);

        // 6. ตั้งค่า TenantExcSourcingStatus
        TenantExcSourcingStatus tenantExcSourcingStatus = tenantExcSourcingStatusRepository
                .findByNameAndTenant(isSubmit ? TENANT_EXC_SOURCING_AWAITING_RESPONSE.name() : TENANT_EXC_SOURCING_DRAFT.name(), tenant);
        excSourcing.setTenantExcSourcingStatus(tenantExcSourcingStatus);

        // 7. ตั้งค่า Approval / DeptApproval Status
        DeptApprovalStatus approvalPending = deptApprovalStatusRepository.findById(DEPT_APPROVAL_PENDING.id())
                .orElseThrow(() -> new BusinessException("ApprovalStatus not found"));
        excSourcing.setApprovalStatus(approvalPending);




        DeptApprovalStatus deptApprovalStatusAwaiting = deptApprovalStatusRepository.findById(DEPT_APPROVAL_AWAITING.id()).orElseThrow(() -> new BusinessException("DeptApprovalStatus not found"));
        DeptApprovalStatus deptApprovalStatusPending = deptApprovalStatusRepository.findById(DEPT_APPROVAL_PENDING.id()).orElseThrow(() -> new BusinessException("DeptApprovalStatus not found"));
        DeptApprovalStatus deptApprovalStatusNone = deptApprovalStatusRepository.findById(DEPT_APPROVAL_NONE.id()).orElseThrow(() -> new BusinessException("DeptApprovalStatus not found"));

        excSourcing.setDeptApprovalStatus(Boolean.TRUE.equals(isSubmit) ? deptApprovalStatusAwaiting : deptApprovalStatusNone);

        // 8. set request
        excSourcing.setRequest(sampleRequestItem.getRequest());

        // 9. save ExcSourcing (JPA จะ update หรือ insert ตาม recId)
        ExcSourcing savedExcSourcing = excSourcingRepository.save(excSourcing);

        // 10. mapping RequestItem → ExcSourcingRequestItem (เฉพาะตอน create ใหม่เท่านั้น)
        if (isNew) {
            List<ExcSourcingRequestItem> mappingList = new ArrayList<>();
            for (RequestItem requestItem : requestItems) {
                ExcSourcingRequestItem mapping = new ExcSourcingRequestItem();
                ExcSourcingRequestItemKey key = new ExcSourcingRequestItemKey();
                key.setExcSourcingId(savedExcSourcing.getRecId());
                key.setRequestItemId(requestItem.getRecId());
                mapping.setId(key);
                mapping.setExcSourcing(savedExcSourcing);
                mapping.setRequestItem(requestItem);
                mappingList.add(mapping);

                // update sourcingDocNo ของ requestItem
                // requestItem.setSourcingDocNo(savedExcSourcing.getExcSourcingDocNo());
            }

            excSourcingRequestItemRepository.saveAll(mappingList);
            requestItemRepository.saveAll(requestItems);
        }

//        // ✅ 10.1 อัปเดต ExistingPriceItem ของ RequestItem ให้ผูกกับ ExcSourcingDocNo (ทั้ง create และ update)
//        for (RequestItem requestItem : requestItems) {
//            existingPriceItemRepository.findByRequestItem(requestItem).ifPresent(existingPriceItem -> {
//                existingPriceItem.setSourcingTypeId(SOURCING_TYPE_EXCEPTIONAL_SOURCING.id());
//                existingPriceItem.setSourcingDocNo(savedExcSourcing.getExcSourcingDocNo());
//                existingPriceItem.setUpdatedBy(AppUtil.getUserName());
//                existingPriceItem.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
//                existingPriceItemRepository.save(existingPriceItem);
//            });
//        }

        // 11. Approvers (Dept + Purchasing)
        List<ExcSourcingApproverItemDto> approvals = excSourcingRequestDto.getApprovals();
        if (approvals != null && !approvals.isEmpty()) {

            // ลบ approver/purchaser เดิมก่อน (กรณี update)
            if (!isNew) {
                excSourcingApproverRepository.deleteByExcSourcing(savedExcSourcing);
                excSourcingPurchaserRepository.deleteByExcSourcing(savedExcSourcing);
            }

            for (ExcSourcingApproverItemDto approval : approvals) {

                // ================================
                // 🔹 Dept Approver
                // ================================
                if ("Approver".equalsIgnoreCase(approval.getSectionName())) {

                    List<Integer> approvers = approval.getApprovers();
                    List<ExcSourcingApprover> approverEntities = new ArrayList<>();

                    int sequence = 1;
                    for (Integer sysUserId : approvers) {

                        ExcSourcingApprover excSourcingApprover = new ExcSourcingApprover();
                        excSourcingApprover.setExcSourcing(savedExcSourcing);

                        Approver approver = approverRepository.findApproverByTenantAndUserId(tenant, sysUserId)
                                .orElseThrow(() -> new BusinessException("Approver not found: " + sysUserId));

                        excSourcingApprover.setApprover(approver);
                        excSourcingApprover.setSequence(sequence);

                        if (isSubmit) {
                            excSourcingApprover.setDeptApprovalStatus(sequence == 1 ? deptApprovalStatusAwaiting : deptApprovalStatusPending);
                        } else {
                            excSourcingApprover.setDeptApprovalStatus(deptApprovalStatusNone);
                        }

                        excSourcingApprover.setCreatedBy(AppUtil.getUserName());
                        excSourcingApprover.setCreatedDate(new Timestamp(System.currentTimeMillis()));

                        approverEntities.add(excSourcingApprover);
                        sequence++;
                    }

                    excSourcingApproverRepository.saveAll(approverEntities);
                }

                // ================================
                // 🔹 Purchasing Approver
                // ================================
                else if ("PurchasingApprover".equalsIgnoreCase(approval.getSectionName())) {

                    List<Integer> purchasers = approval.getApprovers();
                    List<ExcSourcingPurchaser> purchaserEntities = new ArrayList<>();

                    int sequence = 1;
                    for (Integer sysUserId : purchasers) {

                        ExcSourcingPurchaser excSourcingPurchaser = new ExcSourcingPurchaser();
                        excSourcingPurchaser.setExcSourcing(savedExcSourcing);

                        Approver approver = approverRepository.findApproverByTenantAndUserId(tenant, sysUserId)
                                .orElseThrow(() -> new BusinessException("Purchasing approver not found: " + sysUserId));

                        excSourcingPurchaser.setApprover(approver);
                        excSourcingPurchaser.setSequence(sequence);

                        if (isSubmit) {
                            excSourcingPurchaser.setApprovalStatus(deptApprovalStatusPending);
                        } else {
                            excSourcingPurchaser.setApprovalStatus(deptApprovalStatusNone);
                        }

                        excSourcingPurchaser.setCreatedBy(AppUtil.getUserName());
                        excSourcingPurchaser.setCreatedDate(new Timestamp(System.currentTimeMillis()));

                        purchaserEntities.add(excSourcingPurchaser);
                        sequence++;
                    }

                    excSourcingPurchaserRepository.saveAll(purchaserEntities);
                }
            }
        }

        // Send Email
        if(isSubmit) {
            // Send email to 1st Dept Approver

            SendExcSourcingEMailRequest sendExcSourcingEMailRequest = new SendExcSourcingEMailRequest();
            sendExcSourcingEMailRequest.setRequestId(sampleRequestItem.getRequest().getRecId());
            sendExcSourcingEMailRequest.setExcSourcingId(savedExcSourcing.getRecId());
            sendExcSourcingEMailRequest.setEmailActivity(com.pantavanij.sourcingreq.services.enums.EmailActivity.EXC_SR_IS_WAITING_FOR_PURCHASING_APPROVER_TO_APPROVE);
            sendExcSourcingEMailRequest.setActivity(ACTIVITY_EXC_SOURCING_SUBMIT);
            RequestDto requestDtoForEmail = RequestMapper.INSTANCE.toRequestDto(sampleRequestItem.getRequest());
            ExcSourcingDto excSourcingDto = ExcSourcingMapper.INSTANCE.toExcSourcingDto(savedExcSourcing);
            emailService.sendExcSourcingEMailNotification(sendExcSourcingEMailRequest, requestDtoForEmail, excSourcingDto);
        }

        log.info("{} ExcSourcing {} with {} requestItems",
                isNew ? "Created" : "Updated",
                savedExcSourcing.getExcSourcingDocNo(),
                requestItems.size());
        ExcSourcingReponseDto excSourcingReponseDto = new ExcSourcingReponseDto();
        excSourcingReponseDto.setExcSourcingDocNo(savedExcSourcing.getExcSourcingDocNo());
        return excSourcingReponseDto;
    }


    @Override
    public ExcSourcingDto findByDocNo(String excSourcingDocNo) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());

        return excSourcingRepository.findByExcSourcingDocNo(excSourcingDocNo)
                .map(excSourcing -> {

                    // 1. ดึง approvers จาก DB
                    List<ExcSourcingApprover> excSourcingApprovers =
                            excSourcingApproverRepository.findExcSourcingApproversByExcSourcing(excSourcing);

                    // ============================================
                    // 2. ดึง Dept Approver (EPAuth + filter by ExcSourcingApprover)
                    // ============================================
                    List<EPAuthDeptApproverDto> deptApproverList = new ArrayList<>();

                    DeptApproverSearchRequest deptSearchRequest = new DeptApproverSearchRequest();
                    deptSearchRequest.setTenantId(tenant.getCode());
                    deptSearchRequest.setIsExceptionalSourcing(true);
                    deptSearchRequest.setPage(1);
                    deptSearchRequest.setPageSize(99);

                    EPAuthDeptApproverResponse deptResponse =
                            requestDeptApproverService.getDeptApproverListByConditions(deptSearchRequest);

                    if (deptResponse != null && deptResponse.getData() != null && !deptResponse.getData().isEmpty()) {

                        // ✅ เตรียม set ของ userId จาก ExcSourcingApprover
                        Set<Integer> validDeptUserIds = Optional.ofNullable(excSourcing.getExcSourcingApprovers())
                                .orElse(Collections.emptyList())
                                .stream()
                                .filter(a -> a.getApprover() != null && a.getApprover().getUserId() != null)
                                .map(a -> a.getApprover().getUserId())
                                .collect(Collectors.toSet());

                        // ✅ กรองเฉพาะ approver ที่อยู่ใน ExcSourcingApprover
                        deptApproverList = deptResponse.getData().stream()
                                .filter(dto -> validDeptUserIds.contains(dto.getSysUserId().intValue()))
                                .filter(dto -> deptSearchRequest.getExceptApprovers() == null
                                        || !deptSearchRequest.getExceptApprovers().contains(dto.getSysUserId().toString()))
                                .distinct()
                                .collect(Collectors.toList());
                    } else {
                        deptApproverList = Collections.emptyList();
                    }

                    // ============================================
                    // 3. ดึง Purchaser Approver (EPAuth + filter by ExcSourcingPurchaser)
                    // ============================================
                    List<EPAuthDeptApproverDto> purchaserApproverList = new ArrayList<>();

                    DeptApproverSearchRequest purchaserSearchRequest = new DeptApproverSearchRequest();
                    purchaserSearchRequest.setTenantId(tenant.getCode());
                    purchaserSearchRequest.setPage(1);
                    purchaserSearchRequest.setPageSize(99);

                    EPAuthDeptApproverResponse purchaserResponse =
                            requestPurchasingApproverService.getPurchasingApproverListByConditions(purchaserSearchRequest);

                    if (purchaserResponse != null && purchaserResponse.getData() != null && !purchaserResponse.getData().isEmpty()) {

                        // ✅ เตรียม set ของ userId จาก ExcSourcingPurchaser
                        Set<Integer> validPurchaserUserIds = Optional.ofNullable(excSourcing.getExcSourcingPurchasers())
                                .orElse(Collections.emptyList())
                                .stream()
                                .filter(p -> p.getApprover() != null && p.getApprover().getUserId() != null)
                                .map(p -> p.getApprover().getUserId())
                                .collect(Collectors.toSet());

                        // ✅ กรองเฉพาะ approver ที่อยู่ใน ExcSourcingPurchaser
                        purchaserApproverList = purchaserResponse.getData().stream()
                                .filter(dto -> validPurchaserUserIds.contains(dto.getSysUserId().intValue()))
                                .filter(dto -> purchaserSearchRequest.getExceptApprovers() == null
                                        || !purchaserSearchRequest.getExceptApprovers().contains(dto.getSysUserId().toString()))
                                .distinct()
                                .collect(Collectors.toList());
                    } else {
                        purchaserApproverList = Collections.emptyList();
                    }

                    // ============================================
                    // 4. ดึง Purchaser Header สำหรับ UI
                    // ============================================
                    String idp = AppUtil.getIdp();
                    InstanceApproverHeaderDto purchaserHeader =
                            requestService.getPurchaserHeaders(tenant.getCode(), idp, excSourcing.getRequest());

                    List<InstanceApproverDto> allPurchaserHeaderApprovers = purchaserHeader.getApproverSections()
                            .get(0)
                            .getApprovers();

                    List<InstanceApproverDto> purchaserHeaderApprovers = allPurchaserHeaderApprovers.stream()
                            .filter(approver -> approver.getLoginId().equalsIgnoreCase(excSourcing.getCreatedBy()))
                            .collect(Collectors.toList());

                    purchaserHeader.getApproverSections()
                            .get(0)
                            .setApprovers(purchaserHeaderApprovers);

                    if (purchaserHeader != null
                            && purchaserHeader.getApproverSections() != null
                            && !purchaserHeader.getApproverSections().isEmpty()
                            && purchaserHeader.getApproverSections().get(0).getApprovers() != null
                            && !purchaserHeader.getApproverSections().get(0).getApprovers().isEmpty()) {

                        purchaserHeader.getApproverSections()
                                .get(0)
                                .getApprovers()
                                .get(0)
                                .setStatus(DEPT_APPROVAL_NONE.code());

                        purchaserHeader.getApproverSections()
                                .get(0)
                                .setHiddenStatus(true);

                        purchaserHeader.getApproverSections()
                                .get(0)
                                .setHiddenComment(true);

                    }

                    // ============================================
                    // 5. สร้าง DTO ผ่าน mapper
                    // ============================================
                    log.info("tenantExcSourcingStatus: {}", excSourcing.getTenantExcSourcingStatus());
                    ExcSourcingDto dto = ExcSourcingMapper.INSTANCE.toExcSourcingDto(
                            excSourcing,
                            deptApproverList,
                            purchaserHeader,
                            purchaserApproverList,
                            excSourcingApprovers
                    );


                    // =====================================================================================
                    // ✅ 6. ตรวจสอบสถานะของสาย Dept / Purchaser และสิทธิกด Approve/Reject ของ user ปัจจุบัน (รองรับ multi-role)
                    // =====================================================================================
                    String currentUser = AppUtil.getUserName();
                    boolean canApprove = false;
                    boolean canReject = false;

                    DeptApprovalStatusDto deptStatusDto = null;
                    DeptApprovalStatusDto purStatusDto = null;
                    DeptApprovalStatusDto userStatusDto = null;

                    // ------------------------------------------------------
                    // 🔹 7. ตรวจจากสาย Dept Approver
                    // ------------------------------------------------------
                    if (excSourcing.getExcSourcingApprovers() != null) {
                        for (ExcSourcingApprover approver : excSourcing.getExcSourcingApprovers()) {

                            // เก็บสถานะล่าสุดของสาย Dept (ภาพรวม)
                            if (approver.getDeptApprovalStatus() != null) {
                                deptStatusDto = DeptApprovalStatusMapper.INSTANCE
                                        .toDeptApprovalStatusDto(approver.getDeptApprovalStatus());
                            }

                            // ตรวจสิทธิ์เฉพาะ user ปัจจุบัน
                            if (approver.getApprover() != null &&
                                    currentUser.equals(approver.getApprover().getLoginId())) {

                                DeptApprovalStatusDto thisUserDeptStatus =
                                        DeptApprovalStatusMapper.INSTANCE.toDeptApprovalStatusDto(approver.getDeptApprovalStatus());

                                // ถ้าเป็น Awaiting → สามารถกดได้
                                if (approver.getDeptApprovalStatus() != null &&
                                        DEPT_APPROVAL_AWAITING.code().equals(approver.getDeptApprovalStatus().getName())) {
                                    canApprove = true;
                                    canReject = true;
                                }

                                // เก็บสถานะฝั่ง Dept ของ user
                                if (thisUserDeptStatus != null) {
                                    userStatusDto = thisUserDeptStatus; // อาจเปลี่ยนทีหลังถ้าพบ Purchaser ที่ยัง Awaiting
                                }
                            }
                        }
                    }

                    // ------------------------------------------------------
                    // 🔹 8.ตรวจจากสาย Purchasing Approver (ไม่ใช้ else — รองรับ multi-role)
                    // ------------------------------------------------------
                    if (excSourcing.getExcSourcingPurchasers() != null) {
                        for (ExcSourcingPurchaser purchaser : excSourcing.getExcSourcingPurchasers()) {

                            // เก็บสถานะล่าสุดของสาย Purchasing (ภาพรวม)
                            if (purchaser.getApprovalStatus() != null) {
                                purStatusDto = DeptApprovalStatusMapper.INSTANCE
                                        .toDeptApprovalStatusDto(purchaser.getApprovalStatus());
                            }

                            // ตรวจสิทธิ์เฉพาะ user ปัจจุบัน
                            if (purchaser.getApprover() != null &&
                                    currentUser.equals(purchaser.getApprover().getLoginId())) {

                                DeptApprovalStatusDto thisUserPurStatus =
                                        DeptApprovalStatusMapper.INSTANCE.toDeptApprovalStatusDto(purchaser.getApprovalStatus());

                                // ถ้าเป็น Awaiting → สามารถกดได้
                                if (purchaser.getApprovalStatus() != null &&
                                        DEPT_APPROVAL_AWAITING.code().equals(purchaser.getApprovalStatus().getName())) {
                                    canApprove = true;
                                    canReject = true;
                                }

                                // ถ้าเจอฝั่ง Purchaser ที่ยัง Awaiting → ให้ถือว่าเป็นสถานะหลักของ user ตอนนี้
                                if (thisUserPurStatus != null) {
                                    if (userStatusDto == null) {
                                        userStatusDto = thisUserPurStatus;
                                    } else {
                                        // ถ้า user มีทั้งสอง role → เอาฝั่งที่ยัง Awaiting เป็นค่าหลัก
                                        if (DEPT_APPROVAL_AWAITING.code().equals(thisUserPurStatus.getName())) {
                                            userStatusDto = thisUserPurStatus;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ------------------------------------------------------
                    // 🔹 9. เซตค่ากลับเข้า DTO
                    // ------------------------------------------------------
                    dto.setDeptApprovalStatus(deptStatusDto);
                    dto.setPurApprovalStatus(purStatusDto);
                    dto.setApprovalStatus(userStatusDto);
                    dto.setCanApprove(canApprove);
                    dto.setCanReject(canReject);

                    // =====================================================================================

                    return dto;
                })
                .orElse(null);
    }


    @Override
    @Transactional
    public boolean approveExcSourcingApprover(ExcSourcingApprovalRequest deptApprovalRequest) {

        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        ExcSourcing excSourcing = excSourcingRepository.findByRecId(deptApprovalRequest.getExcSourcingId());
        Optional<Approver> approverOpt = approverRepository.findApproverByTenantAndLoginId(tenant, AppUtil.getUserName());

        if (approverOpt.isPresent()) {
            Approver approver = approverOpt.get();

            Optional<ExcSourcingApprover> sourcingApproverOpt =
                    excSourcingApproverRepository.findExcSourcingApproversByExcSourcingAndApprover(excSourcing, approver);

            if (sourcingApproverOpt.isPresent()) {
                ExcSourcingApprover sourcingApprover = sourcingApproverOpt.get();

                if (excSourcingDeptApproverService.hasApprovalPermission(sourcingApprover)) {

                    DeptApprovalStatus deptApprovalStatusApproved =
                            deptApprovalStatusRepository.findByName(DEPT_APPROVAL_APPROVED.code());
                    DeptApprovalStatus deptApprovalStatusAwaiting =
                            sourcingApprover.getDeptApprovalStatus();

                    // ✅ อัปเดต approver ปัจจุบันเป็น "Approved"
                    sourcingApprover.setComment(deptApprovalRequest.getReason());
                    sourcingApprover.setDeptApprovalStatus(deptApprovalStatusApproved);
                    sourcingApprover.setUpdatedBy(AppUtil.getUserName());
                    sourcingApprover.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                    excSourcingApproverRepository.save(sourcingApprover);

                    // ✅ ตรวจสอบว่ามี approver คนถัดไปไหม (status = NONE)
                    DeptApprovalStatus deptApprovalStatusPending =
                            deptApprovalStatusRepository.findByName(DEPT_APPROVAL_PENDING.code());

                    List<ExcSourcingApprover> nextApprovers =
                            excSourcingApproverRepository.findExcSourcingApproversByExcSourcingAndDeptApprovalStatus(
                                    excSourcing, deptApprovalStatusPending);

                    if (nextApprovers != null && !nextApprovers.isEmpty()) {
                        // 🔹 มี approver ถัดไป → ปลดล็อกคนถัดไปเป็น AWAITING
                        nextApprovers.sort(Comparator.comparing(ExcSourcingApprover::getSequence));
                        ExcSourcingApprover nextApprover = nextApprovers.get(0);
                        nextApprover.setDeptApprovalStatus(deptApprovalStatusAwaiting);
                        nextApprover.setUpdatedBy(AppUtil.getUserName());
                        nextApprover.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                        excSourcingApproverRepository.save(nextApprover);

                    } else {
                        // 🔹 ไม่มี approver เหลือ → ถือว่าอนุมัติครบแล้ว
                        String sourcingStatusCode = TENANT_EXC_SOURCING_AWAITING_RESPONSE.code();
                        TenantExcSourcingStatus sourcingStatus =
                                tenantExcSourcingStatusRepository.findByNameAndTenant(
                                        sourcingStatusCode, excSourcing.getTenant());

                        excSourcing.setTenantExcSourcingStatus(sourcingStatus);
                        excSourcing.setDeptApprovalStatus(deptApprovalStatusApproved);

                        // ✅ เปิดสาย Purchaser (คนแรก = AWAITING)
                        DeptApprovalStatus statusPending = deptApprovalStatusRepository.findByName(DEPT_APPROVAL_PENDING.code());
                        DeptApprovalStatus statusAwaiting = deptApprovalStatusRepository.findByName(DEPT_APPROVAL_AWAITING.code());

                        List<ExcSourcingPurchaser> purchaserChainNone =
                                excSourcingPurchaserRepository.findExcSourcingPurchasersByExcSourcingAndApprovalStatus(excSourcing, statusPending);

                        if (purchaserChainNone != null && !purchaserChainNone.isEmpty()) {
                            purchaserChainNone.sort(Comparator.comparing(ExcSourcingPurchaser::getSequence));

                            ExcSourcingPurchaser firstPurchaser = purchaserChainNone.get(0);
                            firstPurchaser.setApprovalStatus(statusAwaiting);
                            firstPurchaser.setUpdatedBy(AppUtil.getUserName());
                            firstPurchaser.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                            excSourcingPurchaserRepository.save(firstPurchaser);

                            excSourcing.setApprovalStatus(statusAwaiting);
                        }

                        excSourcing.setUpdatedBy(AppUtil.getUserName());
                        excSourcing.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                        excSourcingRepository.save(excSourcing);

                        // ✅ อัปเดต RequestItem เฉพาะที่ไม่ใช่ REJECTED → เป็น AWAITING_RESPONSE
                        SourcingStatus sourcingAwaiting = sourcingStatusRepository.findSourcingStatusByRecId(SOURCING_AWAITING_RESPONSE.id());
                        List<RequestItem> toUpdate = collectUpdatableItems(excSourcing);
                        for (RequestItem item : toUpdate) {
                            item.setSourcingStatus(sourcingAwaiting);
                            item.setUpdatedBy(AppUtil.getUserName());
                            item.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                        }
                        if (!toUpdate.isEmpty()) {
                            requestItemRepository.saveAll(toUpdate);
                        }
                    }

                    // ✅ บันทึก History
//                     excSourcingHistoryService.saveExcSourcingHistoryByAction(excSourcing, ACTIVITY_APPROVED.id());


                    // ✅ ส่งอีเมล “ExcSourcing ถูกอนุมัติแล้ว”
                    SendExcSourcingEMailRequest sendExcSourcingEMailRequest = new SendExcSourcingEMailRequest();
                    sendExcSourcingEMailRequest.setRequestId(excSourcing.getRequest().getRecId());
                    sendExcSourcingEMailRequest.setExcSourcingId(excSourcing.getRecId());
                    sendExcSourcingEMailRequest.setEmailActivity(com.pantavanij.sourcingreq.services.enums.EmailActivity.EXC_SR_HAS_BEEN_APPROVED_BY_DEPT_APPROVER);
                    sendExcSourcingEMailRequest.setActivity(ACTIVITY_EXC_SOURCING_APPROVED);
                    RequestDto requestDtoForEmail = RequestMapper.INSTANCE.toRequestDto(excSourcing.getRequest());
                    ExcSourcingDto excSourcingDto = ExcSourcingMapper.INSTANCE.toExcSourcingDto(excSourcing);
                    emailService.sendExcSourcingEMailNotification(sendExcSourcingEMailRequest, requestDtoForEmail, excSourcingDto);

                    // ✅ ส่งอีเมล “กรุณาตรวจสอบและอนุมัติ ExcSourcing”
//                    SendEMailRequest reviewMail = new SendEMailRequest();
//                    reviewMail.setExcSourcingId(excSourcing.getRecId());
//                    reviewMail.setEmailActivity(
//                            com.pantavanij.sourcingreq.services.enums.EmailActivity.PLEASE_REVIEW_AND_APPROVE_EXC_SOURCING);
//                    reviewMail.setActivity(ACTIVITY_APPROVED);
//                    emailService.sendEMailNotification(reviewMail, excSourcingDtoForEmail);

                    return true;
                }
            }
        }
        return false;
    }

    @Override
    @Transactional
    public boolean rejectExcSourcingApprover(ExcSourcingApprovalRequest deptApprovalRequest) {

        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        ExcSourcing excSourcing = excSourcingRepository.findByRecId(deptApprovalRequest.getExcSourcingId());
        Optional<Approver> approverOpt = approverRepository.findApproverByTenantAndLoginId(tenant, AppUtil.getUserName());

        if (approverOpt.isPresent()) {
            Approver approver = approverOpt.get();

            Optional<ExcSourcingApprover> sourcingApproverOpt =
                    excSourcingApproverRepository.findExcSourcingApproversByExcSourcingAndApprover(excSourcing, approver);

            if (sourcingApproverOpt.isPresent()) {
                ExcSourcingApprover sourcingApprover = sourcingApproverOpt.get();

                if (excSourcingDeptApproverService.hasApprovalPermission(sourcingApprover)) {

                    DeptApprovalStatus deptApprovalStatusRejected =
                            deptApprovalStatusRepository.findByName(DEPT_APPROVAL_REJECTED.code());

                    sourcingApprover.setComment(deptApprovalRequest.getReason());
                    sourcingApprover.setDeptApprovalStatus(deptApprovalStatusRejected);
                    sourcingApprover.setUpdatedBy(AppUtil.getUserName());
                    sourcingApprover.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                    excSourcingApproverRepository.save(sourcingApprover);

                    DeptApprovalStatus deptApprovalStatusPending =
                            deptApprovalStatusRepository.findByName(DEPT_APPROVAL_PENDING.code());
                    DeptApprovalStatus deptApprovalStatusCancelled =
                            deptApprovalStatusRepository.findByName(DEPT_APPROVAL_CANCELLED.code());

                    List<ExcSourcingApprover> nextApprovers =
                            excSourcingApproverRepository.findExcSourcingApproversByExcSourcingAndDeptApprovalStatus(
                                    excSourcing, deptApprovalStatusPending);

                    if (nextApprovers != null && !nextApprovers.isEmpty()) {
                        for (ExcSourcingApprover next : nextApprovers) {
                            next.setDeptApprovalStatus(deptApprovalStatusCancelled);
                            next.setUpdatedBy(AppUtil.getUserName());
                            next.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                        }
                        excSourcingApproverRepository.saveAll(nextApprovers);
                    }

                    // ✅ บันทึก History
//                    excSourcingHistoryService.saveExcSourcingHistoryByAction(excSourcing, ACTIVITY_REJECTED.id());

                    // ✅ ยกเลิก purchaser ที่ยังไม่เริ่ม (PENDING → CANCELLED)
                    DeptApprovalStatus statusPending = deptApprovalStatusRepository.findByName(DEPT_APPROVAL_PENDING.code());
                    DeptApprovalStatus statusCancelled = deptApprovalStatusRepository.findByName(DEPT_APPROVAL_CANCELLED.code());

                    List<ExcSourcingPurchaser> purchaserChainNone =
                            excSourcingPurchaserRepository.findExcSourcingPurchasersByExcSourcingAndApprovalStatus(excSourcing, statusPending);

                    if (purchaserChainNone != null && !purchaserChainNone.isEmpty()) {
                        for (ExcSourcingPurchaser p : purchaserChainNone) {
                            p.setApprovalStatus(statusCancelled);
                            p.setUpdatedBy(AppUtil.getUserName());
                            p.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                        }
                        excSourcingPurchaserRepository.saveAll(purchaserChainNone);
                    }

                    // ✅ อัปเดตสถานะ ExcSourcing → REJECTED
                    String tenantExcSourcingStatusRejectedCode = TENANT_EXC_SOURCING_NO_QUALIFIED_SUPPLIER.code();
                    TenantExcSourcingStatus tenantExcSourcingStatusRejected =
                            tenantExcSourcingStatusRepository.findByNameAndTenant(
                                    tenantExcSourcingStatusRejectedCode, excSourcing.getTenant());
                    excSourcing.setTenantExcSourcingStatus(tenantExcSourcingStatusRejected);

                    ExcSourcingStatus excSourcingStatusRejected = excSourcingStatusRepository
                            .findById(EXC_SOURCING_NO_QUALIFIED_SUPPLIER.id())
                            .orElseThrow(() -> new BusinessException("ExcSourcingStatus not found"));
                    excSourcing.setExcSourcingStatus(excSourcingStatusRejected);

                    excSourcing.setDeptApprovalStatus(deptApprovalStatusRejected);
                    excSourcing.setUpdatedBy(AppUtil.getUserName());
                    excSourcing.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                    excSourcingRepository.save(excSourcing);

                    // ✅ รีเซ็ต RequestItem ทั้งหมดที่เชื่อมกับ ExcSourcing นี้ (ผ่าน mapping)
                    if (excSourcing.getExcSourcingRequestItems() != null && !excSourcing.getExcSourcingRequestItems().isEmpty()) {
                        SourcingStatus sourcingStatusNone = sourcingStatusRepository.findSourcingStatusByRecId(SOURCING_NONE.id());
                        SourcingType sourcingTypeDraft = sourcingTypeRepository.getById(SOURCING_TYPE_DRAFT.id());

                        List<RequestItem> toUpdate = collectUpdatableItems(excSourcing);
                        for (RequestItem item : toUpdate) {
                            item.setSourcingDocNo(null);
                            item.setSourcingStatus(sourcingStatusNone);
                            item.setSourcingDocId(null);
                            item.setSourcingType(sourcingTypeDraft);
                            item.setUpdatedBy(AppUtil.getUserName());
                            item.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                        }
                        if (!toUpdate.isEmpty()) {
                            requestItemRepository.saveAll(toUpdate);
                        }

//                        // ✅ ลบเฉพาะ ExistingPriceItem ของ Exceptional Sourcing เอกสารนี้เท่านั้น
//                        final Integer typeExc = SOURCING_TYPE_EXCEPTIONAL_SOURCING.id();
//                        final String docNo = excSourcing.getExcSourcingDocNo();
//
//                        // ดึงเฉพาะ IDs ที่ match เงื่อนไข เพื่อไปลบลูกก่อน (ถ้าไม่มี FK cascade)
//                        List<Long> exsExistingItemIds = existingPriceItemRepository
//                                .findIdsBySourcingTypeIdAndSourcingDocNo(typeExc, docNo);
//
//                        if (exsExistingItemIds != null && !exsExistingItemIds.isEmpty()) {
//                            // ลบลูกก่อน (ถ้าไม่ได้ตั้ง ON DELETE CASCADE)
//                            existingPriceItemAttachmentRepository.deleteByExistingPriceItemRecIdIn(exsExistingItemIds);
//                            // existingPriceItemSupplierRepository.deleteByExistingPriceItemRecIdIn(exsExistingItemIds); // ไม่ต้องลบซ้ำเพราะ on delete cascade อยู่แล้ว
//
//                            // ลบพ่อ
//                            existingPriceItemRepository.deleteAllById(exsExistingItemIds);
//                        }
//
//                        log.info("Reject EXS {} → deleted {} ExistingPriceItem (type={}, docNo={})",
//                                docNo, (exsExistingItemIds == null ? 0 : exsExistingItemIds.size()), typeExc, docNo);

                        // ✅ ไม่ต้องลบ ExistingPriceItem — เก็บไว้เป็นประวัติราคาเก่า
                        log.info("Reject EXS {} → unlinked {} RequestItem(s), kept ExistingPriceItem(s) intact",
                                excSourcing.getExcSourcingDocNo(),
                                toUpdate.size());


                    }

                    // ✅ ส่งอีเมลแจ้งว่า ExcSourcing ถูก Reject
                    SendExcSourcingEMailRequest sendExcSourcingEMailRequest = new SendExcSourcingEMailRequest();
                    sendExcSourcingEMailRequest.setRequestId(excSourcing.getRequest().getRecId());
                    sendExcSourcingEMailRequest.setExcSourcingId(excSourcing.getRecId());
                    sendExcSourcingEMailRequest.setEmailActivity(com.pantavanij.sourcingreq.services.enums.EmailActivity.EXC_SR_HAS_BEEN_REJECTED_BY_DEPT_APPROVER);
                    sendExcSourcingEMailRequest.setActivity(ACTIVITY_EXC_SOURCING_REJECTED);
                    RequestDto requestDtoForEmail = RequestMapper.INSTANCE.toRequestDto(excSourcing.getRequest());
                    ExcSourcingDto excSourcingDto = ExcSourcingMapper.INSTANCE.toExcSourcingDto(excSourcing);
                    emailService.sendExcSourcingEMailNotification(sendExcSourcingEMailRequest, requestDtoForEmail, excSourcingDto);

                    return true;
                }
            }
        }
        return false;
    }

    @Override
    @Transactional
    public boolean approveExcSourcingPurchaser(ExcSourcingApprovalRequest purchaserApprovalRequest) {

        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        ExcSourcing excSourcing = excSourcingRepository.findByRecId(purchaserApprovalRequest.getExcSourcingId());
        Optional<Approver> approverOpt = approverRepository.findApproverByTenantAndLoginId(tenant, AppUtil.getUserName());

        if (approverOpt.isEmpty()) return false;
        Approver approver = approverOpt.get();

        Optional<ExcSourcingPurchaser> sourcingPurchaserOpt =
                excSourcingPurchaserRepository.findExcSourcingPurchasersByExcSourcingAndApprover(excSourcing, approver);

        if (sourcingPurchaserOpt.isEmpty()) return false;
        ExcSourcingPurchaser sourcingPurchaser = sourcingPurchaserOpt.get();

        // ✅ ตรวจสิทธิ์
        if (!excSourcingPurchaserService.hasApprovalPermission(sourcingPurchaser)) {
            return false;
        }

        DeptApprovalStatus approvalApproved =
                deptApprovalStatusRepository.findByName(DEPT_APPROVAL_APPROVED.code());
        DeptApprovalStatus approvalAwaiting =
                deptApprovalStatusRepository.findByName(DEPT_APPROVAL_AWAITING.code());
        DeptApprovalStatus approvalPending =
                deptApprovalStatusRepository.findByName(DEPT_APPROVAL_PENDING.code());

        // ✅ อัปเดต purchaser ปัจจุบันเป็น "Approved"
        sourcingPurchaser.setComment(purchaserApprovalRequest.getReason());
        sourcingPurchaser.setApprovalStatus(approvalApproved);
        sourcingPurchaser.setUpdatedBy(AppUtil.getUserName());
        sourcingPurchaser.setUpdatedDate(DateTimeUtil.getTimestampUTC());
        excSourcingPurchaserRepository.save(sourcingPurchaser);

        // ✅ ตรวจสอบว่ามี purchaser คนถัดไปไหม (status = PENDING)
        List<ExcSourcingPurchaser> nextPurchasers =
                excSourcingPurchaserRepository.findExcSourcingPurchasersByExcSourcingAndApprovalStatus(excSourcing, approvalPending);

        if (nextPurchasers != null && !nextPurchasers.isEmpty()) {
            // 🔹 มี purchaser ถัดไป → ปลดล็อกคนถัดไปเป็น AWAITING
            nextPurchasers.sort(Comparator.comparing(ExcSourcingPurchaser::getSequence));
            ExcSourcingPurchaser nextPurchaser = nextPurchasers.get(0);
            nextPurchaser.setApprovalStatus(approvalAwaiting);
            nextPurchaser.setUpdatedBy(AppUtil.getUserName());
            nextPurchaser.setUpdatedDate(DateTimeUtil.getTimestampUTC());
            excSourcingPurchaserRepository.save(nextPurchaser);

            // ✅ อัปเดตสถานะเอกสารให้ยังอยู่ในระหว่างการอนุมัติ
            excSourcing.setApprovalStatus(approvalAwaiting);
            excSourcing.setUpdatedBy(AppUtil.getUserName());
            excSourcing.setUpdatedDate(DateTimeUtil.getTimestampUTC());
            excSourcingRepository.save(excSourcing);

        } else {
            // 🔹 ไม่มี purchaser เหลือ → ถือว่าอนุมัติครบแล้ว
            String sourcingStatusCode = TENANT_EXC_SOURCING_QUALIFIED_SUPPLIER.code();
            TenantExcSourcingStatus tenantExcSourcingCompletedStatus =
                    tenantExcSourcingStatusRepository.findByNameAndTenant(
                            sourcingStatusCode, excSourcing.getTenant());
            ExcSourcingStatus excSourcingCompletedStatus = excSourcingStatusRepository
                    .findById(EXC_SOURCING_QUALIFIED_SUPPLIER.id())
                    .orElseThrow(() -> new BusinessException("ExcSourcingStatus not found"));

            excSourcing.setTenantExcSourcingStatus(tenantExcSourcingCompletedStatus);
            excSourcing.setExcSourcingStatus(excSourcingCompletedStatus);
            excSourcing.setApprovalStatus(approvalApproved);
            excSourcing.setUpdatedBy(AppUtil.getUserName());
            excSourcing.setUpdatedDate(DateTimeUtil.getTimestampUTC());
            excSourcingRepository.save(excSourcing);

            // ✅ อัปเดต RequestItem เฉพาะที่ไม่ใช่ REJECTED → เป็น QUALIFIED_SUPPLIER
            SourcingStatus sourcingQualified = sourcingStatusRepository
                    .findSourcingStatusByRecId(SOURCING_QUALIFIED_SUPPLIER.id());

            List<RequestItem> toUpdate = collectUpdatableItems(excSourcing); // กรอง REJECTED ออกแล้ว
            for (RequestItem item : toUpdate) {
                item.setSourcingStatus(sourcingQualified);
                item.setUpdatedBy(AppUtil.getUserName());
                item.setUpdatedDate(DateTimeUtil.getTimestampUTC());
            }
            if (!toUpdate.isEmpty()) {
                requestItemRepository.saveAll(toUpdate);
            }
//            Request request = excSourcing.getRequest();
//            requestService.updateRequestStatusAfterSourcing(request);
//            requestRepository.save(request);
        }

        // ✅ TODO: บันทึก History และส่ง Email
        SendExcSourcingEMailRequest sendExcSourcingEMailRequest = new SendExcSourcingEMailRequest();
        sendExcSourcingEMailRequest.setRequestId(excSourcing.getRequest().getRecId());
        sendExcSourcingEMailRequest.setExcSourcingId(excSourcing.getRecId());
        sendExcSourcingEMailRequest.setEmailActivity(com.pantavanij.sourcingreq.services.enums.EmailActivity.EXC_SR_HAS_BEEN_APPROVED_BY_PURCHASING_APPROVER);
        sendExcSourcingEMailRequest.setActivity(ACTIVITY_EXC_SOURCING_APPROVED);
        RequestDto requestDtoForEmail = RequestMapper.INSTANCE.toRequestDto(excSourcing.getRequest());
        ExcSourcingDto excSourcingDto = ExcSourcingMapper.INSTANCE.toExcSourcingDto(excSourcing);
        emailService.sendExcSourcingEMailNotification(sendExcSourcingEMailRequest, requestDtoForEmail, excSourcingDto);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean rejectExcSourcingPurchaser(ExcSourcingApprovalRequest purchaserApprovalRequest) {

        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        ExcSourcing excSourcing = excSourcingRepository.findByRecId(purchaserApprovalRequest.getExcSourcingId());
        Optional<Approver> approverOpt = approverRepository.findApproverByTenantAndLoginId(tenant, AppUtil.getUserName());

        if (approverOpt.isEmpty()) return false;
        Approver approver = approverOpt.get();

        Optional<ExcSourcingPurchaser> sourcingPurchaserOpt =
                excSourcingPurchaserRepository.findExcSourcingPurchasersByExcSourcingAndApprover(excSourcing, approver);

        if (sourcingPurchaserOpt.isEmpty()) return false;
        ExcSourcingPurchaser sourcingPurchaser = sourcingPurchaserOpt.get();

        // ✅ ตรวจสิทธิ์
        if (!excSourcingPurchaserService.hasApprovalPermission(sourcingPurchaser)) {
            return false;
        }

        DeptApprovalStatus approvalRejected =
                deptApprovalStatusRepository.findByName(DEPT_APPROVAL_REJECTED.code());
        DeptApprovalStatus approvalPending =
                deptApprovalStatusRepository.findByName(DEPT_APPROVAL_PENDING.code());
        DeptApprovalStatus approvalCancelled =
                deptApprovalStatusRepository.findByName(DEPT_APPROVAL_CANCELLED.code());

        // ✅ อัปเดต purchaser ปัจจุบันเป็น "Rejected"
        sourcingPurchaser.setComment(purchaserApprovalRequest.getReason());
        sourcingPurchaser.setApprovalStatus(approvalRejected);
        sourcingPurchaser.setUpdatedBy(AppUtil.getUserName());
        sourcingPurchaser.setUpdatedDate(DateTimeUtil.getTimestampUTC());
        excSourcingPurchaserRepository.save(sourcingPurchaser);

        // ✅ ยกเลิก purchaser ที่เหลือ (PENDING → CANCELLED)
        List<ExcSourcingPurchaser> nextPurchasers =
                excSourcingPurchaserRepository.findExcSourcingPurchasersByExcSourcingAndApprovalStatus(excSourcing, approvalPending);
        if (nextPurchasers != null && !nextPurchasers.isEmpty()) {
            for (ExcSourcingPurchaser next : nextPurchasers) {
                next.setApprovalStatus(approvalCancelled);
                next.setUpdatedBy(AppUtil.getUserName());
                next.setUpdatedDate(DateTimeUtil.getTimestampUTC());
            }
            excSourcingPurchaserRepository.saveAll(nextPurchasers);
        }

        // ✅ อัปเดตสถานะ ExcSourcing → REJECTED
        String tenantExcSourcingStatusRejectedCode = TENANT_EXC_SOURCING_NO_QUALIFIED_SUPPLIER.code();
        TenantExcSourcingStatus tenantExcSourcingStatusRejected =
                tenantExcSourcingStatusRepository.findByNameAndTenant(
                        tenantExcSourcingStatusRejectedCode, excSourcing.getTenant());
        excSourcing.setTenantExcSourcingStatus(tenantExcSourcingStatusRejected);

        ExcSourcingStatus excSourcingStatusRejected = excSourcingStatusRepository
                .findById(EXC_SOURCING_NO_QUALIFIED_SUPPLIER.id())
                .orElseThrow(() -> new BusinessException("ExcSourcingStatus not found"));
        excSourcing.setExcSourcingStatus(excSourcingStatusRejected);

        excSourcing.setApprovalStatus(approvalRejected);
        excSourcing.setUpdatedBy(AppUtil.getUserName());
        excSourcing.setUpdatedDate(DateTimeUtil.getTimestampUTC());
        excSourcingRepository.save(excSourcing);

        // ✅ รีเซ็ต RequestItem และเคลียร์ Existing Price Item
        if (excSourcing.getExcSourcingRequestItems() != null && !excSourcing.getExcSourcingRequestItems().isEmpty()) {
            SourcingStatus sourcingStatusNone = sourcingStatusRepository.findSourcingStatusByRecId(SOURCING_NONE.id());
            SourcingType sourcingTypeDraft = sourcingTypeRepository.getById(SOURCING_TYPE_DRAFT.id());

            List<RequestItem> toUpdate = collectUpdatableItems(excSourcing);
            for (RequestItem item : toUpdate) {
                item.setSourcingDocNo(null);
                item.setSourcingStatus(sourcingStatusNone);
                item.setSourcingDocId(null);
                item.setSourcingType(sourcingTypeDraft);
                item.setUpdatedBy(AppUtil.getUserName());
                item.setUpdatedDate(DateTimeUtil.getTimestampUTC());
            }
            if (!toUpdate.isEmpty()) {
                requestItemRepository.saveAll(toUpdate);
            }

            // ✅ ลบเฉพาะ ExistingPriceItem ของ Exceptional Sourcing เอกสารนี้เท่านั้น
//            final Integer typeExc = SOURCING_TYPE_EXCEPTIONAL_SOURCING.id();
//            final String docNo = excSourcing.getExcSourcingDocNo();
//
//            // ดึงเฉพาะ IDs ที่ match เงื่อนไข เพื่อไปลบลูกก่อน (ถ้าไม่มี FK cascade)
//            List<Long> exsExistingItemIds = existingPriceItemRepository
//                    .findIdsBySourcingTypeIdAndSourcingDocNo(typeExc, docNo);
//
//            if (exsExistingItemIds != null && !exsExistingItemIds.isEmpty()) {
//                // ลบลูกก่อน (ถ้าไม่ได้ตั้ง ON DELETE CASCADE)
//                existingPriceItemAttachmentRepository.deleteByExistingPriceItemRecIdIn(exsExistingItemIds);
//                // existingPriceItemSupplierRepository.deleteByExistingPriceItemRecIdIn(exsExistingItemIds); // ไม่ต้องลบซ้ำเพราะ on delete cascade อยู่แล้ว
//
//                // ลบพ่อ
//                existingPriceItemRepository.deleteAllById(exsExistingItemIds);
//            }
//
//            log.info("Purchaser Reject EXS {} → deleted {} ExistingPriceItem (type={}, docNo={})",
//                    docNo, (exsExistingItemIds == null ? 0 : exsExistingItemIds.size()), typeExc, docNo);

            // ❌ อย่าลบ ExistingPriceItem — เก็บไว้เป็นประวัติราคาเก่า
            log.info("Purchaser Reject EXS {} → unlinked {} RequestItem(s), kept ExistingPriceItem(s) intact",
                    excSourcing.getExcSourcingDocNo(), toUpdate.size());

        }

        // ✅ TODO: บันทึก History และส่ง Email
        SendExcSourcingEMailRequest sendExcSourcingEMailRequest = new SendExcSourcingEMailRequest();
        sendExcSourcingEMailRequest.setRequestId(excSourcing.getRequest().getRecId());
        sendExcSourcingEMailRequest.setExcSourcingId(excSourcing.getRecId());
        sendExcSourcingEMailRequest.setEmailActivity(com.pantavanij.sourcingreq.services.enums.EmailActivity.EXC_SR_HAS_BEEN_REJECTED_BY_PURCHASING_APPROVER);
        sendExcSourcingEMailRequest.setActivity(ACTIVITY_EXC_SOURCING_REJECTED);
        RequestDto requestDtoForEmail = RequestMapper.INSTANCE.toRequestDto(excSourcing.getRequest());
        ExcSourcingDto excSourcingDto = ExcSourcingMapper.INSTANCE.toExcSourcingDto(excSourcing);
        emailService.sendExcSourcingEMailNotification(sendExcSourcingEMailRequest, requestDtoForEmail, excSourcingDto);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteExcSourcingByDocNo(String excSourcingDocNo) {

        // ✅ ดึงข้อมูล ExcSourcing จาก DocNo
        Optional<ExcSourcing> excSourcingOpt = excSourcingRepository.findByExcSourcingDocNo(excSourcingDocNo);
        if (excSourcingOpt.isEmpty()) {
            return false;
        }

        ExcSourcing excSourcing = excSourcingOpt.get();

        // ✅ อนุญาตให้ลบเฉพาะ ExcSourcing ที่อยู่ในสถานะ DRAFT เท่านั้น
        String draftCode = TENANT_EXC_SOURCING_DRAFT.code();
        if (excSourcing.getTenantExcSourcingStatus() == null
                || !draftCode.equals(excSourcing.getTenantExcSourcingStatus().getName())) {
            throw new BusinessException("Cannot delete ExcSourcing that is not in DRAFT status.");
        }

        // ✅ ลบ Approver ทั้งหมดที่ผูกกับ ExcSourcing
        List<ExcSourcingApprover> approvers =
                excSourcingApproverRepository.findExcSourcingApproversByExcSourcing(excSourcing);
        if (approvers != null && !approvers.isEmpty()) {
            excSourcingApproverRepository.deleteAll(approvers);
        }

        // ✅ ลบ Purchaser ทั้งหมดที่ผูกกับ ExcSourcing
        List<ExcSourcingPurchaser> purchasers =
                excSourcingPurchaserRepository.findExcSourcingPurchasersByExcSourcing(excSourcing);
        if (purchasers != null && !purchasers.isEmpty()) {
            excSourcingPurchaserRepository.deleteAll(purchasers);
        }

        // ✅ ลบ RequestItem Mapping (ExcSourcingRequestItem)
        List<ExcSourcingRequestItem> requestItemLinks = excSourcing.getExcSourcingRequestItems();
        if (requestItemLinks != null && !requestItemLinks.isEmpty()) {

            // รีเซ็ต RequestItem ให้กลับไปไม่มี sourcing
            SourcingStatus sourcingStatusNone = sourcingStatusRepository.findSourcingStatusByRecId(SOURCING_NONE.id());
            SourcingType sourcingTypeDraft = sourcingTypeRepository.getById(SOURCING_TYPE_DRAFT.id());
            List<RequestItem> toUpdate = new ArrayList<>();

            for (ExcSourcingRequestItem link : requestItemLinks) {
                RequestItem item = link.getRequestItem();
                item.setSourcingDocNo(null);
                item.setSourcingStatus(sourcingStatusNone);
                item.setSourcingDocId(null);
                item.setSourcingType(sourcingTypeDraft);
                item.setUpdatedBy(AppUtil.getUserName());
                item.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                toUpdate.add(item);
            }

            requestItemRepository.saveAll(toUpdate);

            // ✅ ลบเฉพาะ ExistingPriceItem ของ Exceptional Sourcing เอกสารนี้เท่านั้น
            final Integer typeExc = SOURCING_TYPE_EXCEPTIONAL_SOURCING.id();
            final String docNo = excSourcing.getExcSourcingDocNo();

            // ดึงเฉพาะ IDs ที่ match เงื่อนไข เพื่อไปลบลูกก่อน (ถ้าไม่มี FK cascade)
            List<Long> exsExistingItemIds = existingPriceItemRepository
                    .findIdsBySourcingTypeIdAndSourcingDocNo(typeExc, docNo);

            if (exsExistingItemIds != null && !exsExistingItemIds.isEmpty()) {
                // ลบลูกก่อน (ถ้าไม่ได้ตั้ง ON DELETE CASCADE)
                existingPriceItemAttachmentRepository.deleteByExistingPriceItemRecIdIn(exsExistingItemIds);
                // existingPriceItemSupplierRepository.deleteByExistingPriceItemRecIdIn(exsExistingItemIds); // ไม่ต้องลบซ้ำเพราะ on delete cascade อยู่แล้ว

                // ลบพ่อ
                existingPriceItemRepository.deleteAllById(exsExistingItemIds);
            }

            log.info("Deleted draft ExcSourcing {} → deleted {} ExistingPriceItem (type={}, docNo={})",
                    docNo, (exsExistingItemIds == null ? 0 : exsExistingItemIds.size()), typeExc, docNo);


            // ✅ ลบ mapping ExcSourcingRequestItem เอง
            excSourcingRequestItemRepository.deleteAll(requestItemLinks);
        }

        // ✅ ลบ ExcSourcing หลัก
        excSourcingRepository.delete(excSourcing);

        return true;
    }

    @Override
    public List<DeptApprovalStatusDto> getExcSourcingApprovalStatusSearchList() {
        List<String> excludedDeptApprovalStatus = Arrays.asList(DEPT_APPROVAL_NONE.code(), DEPT_APPROVAL_CANCELLED.code(), DEPT_APPROVAL_PENDING.code());

        List<DeptApprovalStatus> deptApprovalStatusList =
                deptApprovalStatusRepository.findDeptApprovalStatusByNameNotIn(excludedDeptApprovalStatus);
        return DeptApprovalStatusMapper.INSTANCE.toDeptApprovalStatusDto(deptApprovalStatusList);
    }

    @Override
    public List<TenantExcSourcingStatusDto> getExcSourcingStatusSearchList() {
        List<Integer> includedIds = Arrays.asList(TENANT_EXC_SOURCING_AWAITING_RESPONSE.id(), TENANT_EXC_SOURCING_QUALIFIED_SUPPLIER.id(), TENANT_EXC_SOURCING_NO_QUALIFIED_SUPPLIER.id());

        List<TenantExcSourcingStatus> statusList =
                tenantExcSourcingStatusRepository.findById_ExcSourcingStatusIdIn(includedIds);

        return TenantExcSourcingStatusMapper.INSTANCE.toTenantExcSourcingStatusDto(statusList);
    }

    // ===== Helper: ดึง RequestItem ที่สามารถอัปเดตได้ (ไม่ใช่ REJECTED) =====
    private List<RequestItem> collectUpdatableItems(ExcSourcing excSourcing) {
        if (excSourcing == null || excSourcing.getExcSourcingRequestItems() == null) return Collections.emptyList();

        return excSourcing.getExcSourcingRequestItems().stream()
                .filter(Objects::nonNull)
                .map(ExcSourcingRequestItem::getRequestItem)
                .filter(Objects::nonNull)
                .filter(item -> item.getSourcingStatus() == null
                        || item.getSourcingStatus().getRecId() == null
                        || !item.getSourcingStatus().getRecId().equals(SOURCING_REJECTED.id()))
                .collect(Collectors.toList());
    }

}
