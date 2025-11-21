package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.SourcingMenuDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.response.ApiErrorResponse;
import com.pantavanij.sourcingreq.services.domain.response.SourcingMenuListResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.service.sourcingreq.SourcingMenuService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
public class SourcingMenuController {

    private final SourcingMenuService sourcingMenuService;
    private final TenantService tenantService;

    @GetMapping(value = "/sourcing-menu", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSourcingMenuByTenantId(){
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);
        List<SourcingMenuDto> sourcingMenuList = sourcingMenuService.getSourcingMenuByTenantId(tenant.getRecId());

        if (sourcingMenuList  != null && !sourcingMenuList.isEmpty()) {
            return new ResponseEntity<>(new SourcingMenuListResponse(sourcingMenuList), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7062, ApiMessage.E7062.description()) , HttpStatus.NOT_FOUND);
        }
    }
}
