package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Pr;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.PurchaseRequisitionRequest;
import com.pantavanij.sourcingreq.services.domain.response.ApiErrorResponse;
import com.pantavanij.sourcingreq.services.domain.response.PurchaseRequisitionResponse;
import com.pantavanij.sourcingreq.services.domain.response.pr.CreateOraclePrResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.service.sourcingreq.PrService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantConfigService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.endpoint.version}")
public class PrController {

    private final TenantService tenantService;
    private final TenantConfigService tenantConfigService;
    private final PrService prService;


    //@PreAuthorize("hasAuthority('SQR')")
    @PostMapping(value = "/pr/request", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createNewPRByRequest(@RequestBody PurchaseRequisitionRequest purchaseRequisitionRequest) {
        String requestNo = purchaseRequisitionRequest.getRequestNo();
        List<Long> requestItemsId = purchaseRequisitionRequest.getRequestItemsId();
        String prNumber;

        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);

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

            //
            CreateOraclePrResponse response = prService.createERPPurchaseRequisition(tenant, requestNo, requestItemsId);
            prNumber = response.getRequisition();
            if (response.getRequisition() == null) {
                return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7055, String.format(ApiMessage.E7055.description(), response.getMessage())), HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } else {
            PurchaseRequisitionResponse response = prService.createPurchaseRequisition(requestNo, requestItemsId);
            Integer statusCode = response.getHeader().getCode();
            prNumber = response.getHeader().getPrNumber();
            if (200 != statusCode) {
                return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7055, String.format(ApiMessage.E7055.description(), response.getHeader().getMessage())), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        Pr pr = prService.savePrNumber(prNumber);
        prService.saveRequestItemPR(pr.getRecId(), requestItemsId);
        return ResponseEntity.ok().body("Created new pr: " + prNumber);
    }
}