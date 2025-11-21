package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Approver;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExcSourcing;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponseStatus;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.DeptApprovalStatusService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.ExcSourcingService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.DeptApprovalStatus.DEPT_APPROVAL_AWAITING;
import static com.pantavanij.sourcingreq.services.enums.DeptApprovalStatus.DEPT_APPROVAL_PENDING;

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
public class ExcSourcingController {

    private final ExcSourcingService excSourcingService;
    private final TenantService tenantService;
    private final ApproverRepository approverRepository;
    private final PurchaserRepository purchaserRepository;
    private final ExcSourcingRepository excSourcingRepository;
    private final ExcSourcingApproverRepository excSourcingApproverRepository;
    private final ExcSourcingPurchaserRepository excSourcingPurchaserRepository;

    @PostMapping(value = "/exceptional-sourcing/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveExcSourcing(@RequestBody @Valid ExcSourcingRequestDto excSourcingRequestDto) {
        try {
            // เรียก service แล้วได้ response DTO กลับมา
            ExcSourcingReponseDto responseDto = excSourcingService.saveExcSourcing(excSourcingRequestDto);

            // สร้าง response object ที่รวมทั้ง status และ data
            ApiResponseStatus status = new ApiResponseStatus(ApiMessage.I1002, ApiMessage.I1002.description());

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("status", status);
            responseBody.put("data", responseDto);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);

        } catch (BusinessException e) {
            ApiResponseStatus errorStatus = new ApiResponseStatus(ApiMessage.E7085, e.getMessage());
            return new ResponseEntity<>(errorStatus, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            ApiResponseStatus errorStatus = new ApiResponseStatus(ApiMessage.E7085, ApiMessage.E7085.description());
            return new ResponseEntity<>(errorStatus, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping(value = "/exceptional-sourcing/{excSourcingDocNo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getExcSourcingByDocNo(@PathVariable String excSourcingDocNo) {
        try {
            ExcSourcingDto excSourcing = excSourcingService.findByDocNo(excSourcingDocNo);
            if (excSourcing != null) {
                return ResponseEntity.ok(excSourcing);
            } else {
                return new ResponseEntity<>(
                        new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description()),
                        HttpStatus.NOT_FOUND
                );
            }
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponseStatus(ApiMessage.E7085, ApiMessage.E7085.description()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }


    @PostMapping(value = "/exceptional-sourcing/approve", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> approveExcSourcing(@RequestBody ExcSourcingApprovalRequest request) {
        try {
            String currentUser = AppUtil.getUserName();
            Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());

            // 🔹 หา approver object ปัจจุบัน (แทนการใช้ purchaserRepository)
            Optional<Approver> approverOpt = approverRepository.findApproverByTenantAndLoginId(tenant, currentUser);
            if (approverOpt.isEmpty()) {
                return new ResponseEntity<>(
                        new ApiResponseStatus(ApiMessage.E7082, "User is not an approver"),
                        HttpStatus.FORBIDDEN
                );
            }
            Approver approver = approverOpt.get();

            ExcSourcing excSourcing = excSourcingRepository.findByRecId(request.getExcSourcingId());
            if (excSourcing == null) {
                return new ResponseEntity<>(
                        new ApiResponseStatus(ApiMessage.E7001, "Not found excSourcing"),
                        HttpStatus.NOT_FOUND
                );
            }

            // 🔹 ตรวจว่า approver นี้อยู่ในสาย Dept หรือ Purchasing
            boolean isDeptApprover = excSourcingApproverRepository
                    .findExcSourcingApproversByExcSourcingAndApprover(excSourcing, approver)
                    .isPresent();

            boolean isPurchasingApprover = excSourcingPurchaserRepository
                    .findExcSourcingPurchasersByExcSourcingAndApprover(excSourcing, approver)
                    .isPresent();

            // 🔹 ตรวจว่าอยู่ขั้นตอนของ role ไหน (คนที่ต้อง approve ตอนนี้คือคนที่มีสถานะ Awaiting)
            boolean hasAwaitingDept = excSourcingApproverRepository
                    .existsByExcSourcingAndDeptApprovalStatusId(excSourcing, DEPT_APPROVAL_AWAITING.id());

            boolean hasAwaitingPurchaser = !hasAwaitingDept &&
                    excSourcingPurchaserRepository
                            .existsByExcSourcingAndApprovalStatusId(excSourcing, DEPT_APPROVAL_AWAITING.id());

            boolean isApproved = false;

            // 🔹 ถ้าอยู่ในสาย dept และตอนนี้เป็นขั้น Dept awaiting → ใช้ service approveExcSourcingApprover
            if (hasAwaitingDept && isDeptApprover) {
                isApproved = excSourcingService.approveExcSourcingApprover(request);
            }
            // 🔹 ถ้าอยู่ในสาย purchaser และตอนนี้เป็นขั้น Purchaser awaiting → ใช้ service approveExcSourcingPurchaser
            else if (hasAwaitingPurchaser && isPurchasingApprover) {
                isApproved = excSourcingService.approveExcSourcingPurchaser(request);
            }

            if (isApproved) {
                return new ResponseEntity<>(
                        new ApiResponseStatus(ApiMessage.I1003, ApiMessage.I1003.description()),
                        HttpStatus.OK
                );
            }

            return new ResponseEntity<>(
                    new ApiResponseStatus(ApiMessage.E7082, ApiMessage.E7082.description()),
                    HttpStatus.METHOD_NOT_ALLOWED
            );

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(
                    new ApiResponseStatus(ApiMessage.E7082, e.getMessage()),
                    HttpStatus.METHOD_NOT_ALLOWED
            );
        }
    }




    @PostMapping(value = "/exceptional-sourcing/reject", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> rejectExcSourcing(@RequestBody ExcSourcingApprovalRequest request) {
        try {
            String currentUser = AppUtil.getUserName();
            Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());

            // 🔹 หา approver จากตาราง Approver (แทนการใช้ purchaserRepository)
            Optional<Approver> approverOpt = approverRepository.findApproverByTenantAndLoginId(tenant, currentUser);
            if (approverOpt.isEmpty()) {
                return new ResponseEntity<>(
                        new ApiResponseStatus(ApiMessage.E7082, "User is not an approver"),
                        HttpStatus.FORBIDDEN
                );
            }
            Approver approver = approverOpt.get();

            // 🔹 ดึงเอกสาร Exceptional Sourcing
            ExcSourcing excSourcing = excSourcingRepository.findByRecId(request.getExcSourcingId());
            if (excSourcing == null) {
                return new ResponseEntity<>(
                        new ApiResponseStatus(ApiMessage.E7001, "Not found excSourcing"),
                        HttpStatus.NOT_FOUND
                );
            }

            // 🔹 ตรวจว่ายูสเซอร์นี้อยู่สายไหน
            boolean isDeptApprover = excSourcingApproverRepository
                    .findExcSourcingApproversByExcSourcingAndApprover(excSourcing, approver)
                    .isPresent();

            boolean isPurchasingApprover = excSourcingPurchaserRepository
                    .findExcSourcingPurchasersByExcSourcingAndApprover(excSourcing, approver)
                    .isPresent();

            // 🔹 ตรวจว่าอยู่ขั้นตอนของ role ไหน (คนที่ต้อง reject ตอนนี้คือคนที่มีสถานะ Awaiting)
            boolean hasAwaitingDept = excSourcingApproverRepository
                    .existsByExcSourcingAndDeptApprovalStatusId(excSourcing, DEPT_APPROVAL_AWAITING.id());

            boolean hasAwaitingPurchaser = !hasAwaitingDept &&
                    excSourcingPurchaserRepository
                            .existsByExcSourcingAndApprovalStatusId(excSourcing, DEPT_APPROVAL_AWAITING.id());

            boolean isRejected = false;

            // 🔹 ถ้าอยู่ในสาย dept และตอนนี้เป็นขั้น Dept awaiting → ใช้ service rejectExcSourcingApprover
            if (hasAwaitingDept && isDeptApprover) {
                isRejected = excSourcingService.rejectExcSourcingApprover(request);
            }
            // 🔹 ถ้าอยู่ในสาย purchaser และตอนนี้เป็นขั้น Purchaser awaiting → ใช้ service rejectExcSourcingPurchaser
            else if (hasAwaitingPurchaser && isPurchasingApprover) {
                isRejected = excSourcingService.rejectExcSourcingPurchaser(request);
            }

            if (isRejected) {
                return new ResponseEntity<>(
                        new ApiResponseStatus(ApiMessage.I1003, ApiMessage.I1003.description()),
                        HttpStatus.OK
                );
            }

            return new ResponseEntity<>(
                    new ApiResponseStatus(ApiMessage.E7082, ApiMessage.E7082.description()),
                    HttpStatus.METHOD_NOT_ALLOWED
            );

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(
                    new ApiResponseStatus(ApiMessage.E7082, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }




    @DeleteMapping(value = "/exceptional-sourcing/{excSourcingDocNo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteExcSourcing(@PathVariable String excSourcingDocNo) {
        try {
            boolean isDeleted = excSourcingService.deleteExcSourcingByDocNo(excSourcingDocNo);
            if (isDeleted) {
                return new ResponseEntity<>(
                        new ApiResponseStatus(ApiMessage.I1004, "ExcSourcing deleted successfully."),
                        HttpStatus.OK
                );
            } else {
                return new ResponseEntity<>(
                        new ApiResponseStatus(ApiMessage.E7010, "ExcSourcing not found or not in DRAFT status."),
                        HttpStatus.BAD_REQUEST
                );
            }
        } catch (BusinessException e) {
            return new ResponseEntity<>(
                    new ApiResponseStatus(ApiMessage.E7085, e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(
                    new ApiResponseStatus(ApiMessage.E7085, ApiMessage.E7085.description()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping(value = "/exceptional-sourcing/approval-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<ApprovalStatusNameDto>>> getExcSourcingApprovalStatusSearchByTenant() {
        List<DeptApprovalStatusDto> deptApprovalStatusDtoList = excSourcingService.getExcSourcingApprovalStatusSearchList();

        if (!deptApprovalStatusDtoList.isEmpty()) {
            return ResponseEntity.ok().body(new ApiResponse(deptApprovalStatusDtoList));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }
    }

    @GetMapping(value = "/exceptional-sourcing/sourcing-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getExcSourcingSourcingStatusSearchByTenant() {
        List<TenantExcSourcingStatusDto> statusList = excSourcingService.getExcSourcingStatusSearchList();

        if (!statusList.isEmpty()) {

            List<Map<String, Object>> simplifiedList = statusList.stream()
                    .map(item -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("recId", item.getRecId());
                        map.put("name", item.getDescription());
                        return map;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok().body(new ApiResponse<>(simplifiedList));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(null,
                            new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }
    }


}