package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.interceptor.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;
import java.util.*;

import static com.pantavanij.sourcingreq.services.enums.ProjectSearchType.NAME;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.endpoint.version}")
public class TenantConfigController {
    private final TenantConfigService tenantConfigService;
    private final TenantService tenantService;
    private final UaaService uaaService;
    private final AttachmentService attachmentService;

//    @GetMapping(value = "/tenant-config/workflow-template", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<ApiResponse<String>> getWorkflowTemplate() {
//        String tenantCode = AppUtil.getTenantId();
//        Tenant tenant = tenantService.findByCode(tenantCode);
//
//        if (tenant == null)  throw new BusinessException(ApiMessage.E7016, ApiMessage.E7016.description());
//
//        String workflowTemplateId = tenantConfigService.getWorkflowTemplateId(tenant.getRecId());
//
//        if (workflowTemplateId != null) {
//            return ResponseEntity.ok().body(new ApiResponse(workflowTemplateId));
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                    new ApiResponse<>(null, new ApiResponseStatus(ApiMessage.E7027, ApiMessage.E7027.description())));
//        }
//    }

    @GetMapping(value = "/tenant-config/max-session-timeout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Long>> getMaxSessionTimeout() {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);

        if (tenant == null)  throw new BusinessException(ApiMessage.E7016, ApiMessage.E7016.description());

        Long miniutes = tenantConfigService.getMaxSessionTimeout(tenant.getRecId());

        if (miniutes != null) {
            return ResponseEntity.ok().body(new ApiResponse(miniutes));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(null, new ApiResponseStatus(ApiMessage.E7027, ApiMessage.E7027.description())));
        }
    }

    @GetMapping(value = "/tenant-config/image/logo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getLogoImage(@Valid @RequestParam("system") String system) {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);
        String logoUrl = "";

        if(system.equalsIgnoreCase("sr")){
            logoUrl = tenantConfigService.getSRLogoImageFileId(tenant.getRecId());
        } else if(system.equalsIgnoreCase("ep")){
            logoUrl = tenantConfigService.getEPLogoImageFileId(tenant.getRecId());
        } else if(system.equalsIgnoreCase("se")){
            logoUrl = tenantConfigService.getSELogoImageFileId(tenant.getRecId());
        } else if(system.equalsIgnoreCase("erfx")){
            logoUrl = tenantConfigService.getERFXLogoImageFileId(tenant.getRecId());
        } else if(system.equalsIgnoreCase("uam-admin")){
            logoUrl = tenantConfigService.getUAMAdminLogoImageFileId(tenant.getRecId());
        } else if(system.equalsIgnoreCase("dashboard")){
            logoUrl = tenantConfigService.getDashboardLogoImageFileId(tenant.getRecId());
        }

        ByteArrayResource bytes = attachmentService.downloadAttachment(tenant, logoUrl);
        HttpHeaders header = new HttpHeaders();
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + logoUrl);
        header.setContentLength(bytes.contentLength());
        header.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(bytes, header, HttpStatus.OK);
    }

    @GetMapping(value = "/tenant-config/image/logo/styles", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<String>> getLogoImageStyles(@Valid @RequestParam("system") String system) {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);

        if (tenant == null)  throw new BusinessException(ApiMessage.E7016, ApiMessage.E7016.description());
        String logoImageStyles = "";
        if(system.equalsIgnoreCase("sr")){
            logoImageStyles = tenantConfigService.getSRLogoImageStyles(tenant.getRecId());
        } else if(system.equalsIgnoreCase("ep")){
            logoImageStyles = tenantConfigService.getEPLogoImageStyles(tenant.getRecId());
        } else if(system.equalsIgnoreCase("se")){
            logoImageStyles = tenantConfigService.getSELogoImageStyles(tenant.getRecId());
        } else if(system.equalsIgnoreCase("erfx")){
            logoImageStyles = tenantConfigService.getERFXLogoImageStyles(tenant.getRecId());
        } else if(system.equalsIgnoreCase("uam-admin")){
            logoImageStyles = tenantConfigService.getUAMAdminLogoImageStyles(tenant.getRecId());
        } else if(system.equalsIgnoreCase("dashboard")){
            logoImageStyles = tenantConfigService.getDashboardLogoImageStyles(tenant.getRecId());
        }


        if (StringUtils.isNotBlank(logoImageStyles)) {
            return ResponseEntity.ok().body(new ApiResponse(logoImageStyles));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(null, new ApiResponseStatus(ApiMessage.E7027, ApiMessage.E7027.description())));
        }
    }

    @GetMapping(value = "/tenant-config/display", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<SourcingRequestDisplayDto>> getDisplayConfiguration() {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);

        if (tenant == null) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                    new ApiResponse<>(null, new ApiResponseStatus(ApiMessage.E7064, ApiMessage.E7064.description())));
        }

        SourcingRequestDisplayDto sourcingRequestDisplayDto = tenantConfigService.getDisplayConfiguration(tenant);
        return ResponseEntity.ok().body(new ApiResponse(sourcingRequestDisplayDto));
    }

    @GetMapping(value = "/tenant-config/logic", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<SourcingRequestLogicDto>> getLogicConfiguration() {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);

        if (tenant == null) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                    new ApiResponse<>(null, new ApiResponseStatus(ApiMessage.E7064, ApiMessage.E7064.description())));
        }

        SourcingRequestLogicDto sourcingRequestLogicDto = tenantConfigService.getLogicConfiguration(tenant);
        return ResponseEntity.ok().body(new ApiResponse(sourcingRequestLogicDto));
    }

//    @GetMapping(value = "/tenant-config/report/filter", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<ApiResponse<Boolean>> getConfigurationReportFiltering() {
//        String tenantCode = AppUtil.getTenantId();
//        Tenant tenant = tenantService.findByCode(tenantCode);
//
//        if (tenant == null) {
//            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
//                    new ApiResponse<>(null, new ApiResponseStatus(ApiMessage.E7064, ApiMessage.E7064.description())));
//        }
//
//        boolean data = tenantConfigService.getFilterReportConfiguration(tenant.getRecId());
//        return ResponseEntity.ok().body(new ApiResponse(data));
//    }

//    @GetMapping(value = "/tenant-config/existing-price-item/menu", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<ApiResponse<Boolean>> getConfigurationExistingPriceItemMenu() {
//        String tenantCode = AppUtil.getTenantId();
//        Tenant tenant = tenantService.findByCode(tenantCode);
//
//        if (tenant == null) {
//            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
//                    new ApiResponse<>(null, new ApiResponseStatus(ApiMessage.E7064, ApiMessage.E7064.description())));
//        }
//
//        boolean data = tenantConfigService.getConfigurationExistingPriceItemMenu(tenant.getRecId());
//        return ResponseEntity.ok().body(new ApiResponse(data));
//    }

    @GetMapping(value = "/tenant-config/our-service", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<TenantConfigDto>> getOurService() {
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);

        if (tenant == null) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                    new ApiResponse<>(null, new ApiResponseStatus(ApiMessage.E7064, ApiMessage.E7064.description())));
        }

        List<TenantConfigDto> data = tenantConfigService.getOurService(tenant.getRecId(), timeZone);
        return ResponseEntity.ok().body(new ApiResponse(data));
    }

    @PreAuthorize("hasAuthority('SAM')")
    @GetMapping(value = "/tenant-config/view/{tenantConfigId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getTenantConfig(@PathVariable("tenantConfigId") Integer tenantConfigId) {
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        TenantConfigDto tenantConfigDto = tenantConfigService.getByTenantConfigIdAndTenant(tenantConfigId, timeZone);
        if (tenantConfigDto != null) {
            return ResponseEntity.ok().body(new ApiResponse<>(tenantConfigDto));
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7096, String.format(ApiMessage.E7096.description(), "Data")), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PostMapping(value = "/tenant-config/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchTenantConfig(@RequestBody @Valid TenantConfigSearchRequest request) {
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);

        Sort sort;
        int page = request.getPage();
        int size = request.getPageSize();
        String sortBy = request.getSortBy();
        String sortOrder = request.getSortOrder();

        if (("".equals(sortBy) || sortBy == null) && ("".equals(sortOrder) || sortOrder == null)) {
            sort = Sort.by(NAME.description()).descending();
        } else {
            List<Sort.Order> orders = new ArrayList<>();
            assert sortBy != null;
            orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC: Sort.Direction.ASC, sortBy));
            sort = Sort.by(orders);
        }

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        TenantConfigSearchDto tenantConfigSearchDto = tenantConfigService.searchTenantConfigByCondition(request, pageable, timeZone);
        TenantConfigSearchResponse response = TenantConfigSearchResponse.builder()
                .status(new ApiResponseStatus())
                .pageSize(tenantConfigSearchDto.getPageSize())
                .page(page)
                .totalPage(tenantConfigSearchDto.getTotalPage())
                .total(tenantConfigSearchDto.getTotal())
                .data(tenantConfigSearchDto.getTenantConfigList())
                .build();

        if (tenantConfigSearchDto.getTenantConfigList() != null && !tenantConfigSearchDto.getTenantConfigList().isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }


    @ControllerExecuteTime
    @PreAuthorize("hasAuthority('SAM')")
    @PostMapping(value = "/tenant-config/submit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity submitTenantConfig(@RequestBody TenantConfigRequest request) {
        TenantConfigDto tenantConfigDto = tenantConfigService.createTenantConfig(request);
        if (tenantConfigDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(tenantConfigDto, new ApiResponseStatus(ApiMessage.I1002, ApiMessage.I1002.description())), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ControllerExecuteTime
    @PreAuthorize("hasAuthority('SAM')")
    @PutMapping(value = "/tenant-config/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateTenantConfig(@RequestBody TenantConfigRequest request) {
        TenantConfigDto tenantConfigDto = tenantConfigService.updateTenantConfig(request);
        if (tenantConfigDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(tenantConfigDto, new ApiResponseStatus(ApiMessage.I1002, ApiMessage.I1002.description())), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PutMapping(value = "/tenant-config/update/sequence", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateProjectSequence(@RequestBody @Valid SequenceRequest request) {
        TenantConfigDto tenantConfigDto = tenantConfigService.updateTenantConfigSequence(request);
        if (tenantConfigDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(tenantConfigDto), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasAuthority('SAM')")
    @DeleteMapping(value = "/tenant-config/delete/{tenantConfigId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteTenantConfig(@PathVariable("tenantConfigId") Integer tenantConfigId) {
        boolean isDeleted = tenantConfigService.deleteTenantConfig(tenantConfigId);
        if (isDeleted) {
            return new ResponseEntity<>(new ApiResponseStatus(), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
