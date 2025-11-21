package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.TenantSubCategoryDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponseStatus;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantSubCategoryService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.endpoint.version}")
public class TenantSubCategoryController {

    private final TenantSubCategoryService tenantSubCategoryService;
    private final TenantService tenantService;

    @GetMapping(value = "/tenant-subcategory", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<TenantSubCategoryDto>> getSubcategory(@RequestParam Integer typeId, @RequestParam String purchaser) {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);
        if (tenant == null)  throw new BusinessException(ApiMessage.E7016, ApiMessage.E7016.description());

        List<TenantSubCategoryDto> tenantSubCategoryDtoList = tenantSubCategoryService.getSubCategoryByTenantIdAndTypeAndBuyer(tenant.getRecId(), typeId, purchaser);
        if (tenantSubCategoryDtoList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }
        return ResponseEntity.ok().body(new ApiResponse(tenantSubCategoryDtoList));
    }
}
