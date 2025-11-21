package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.AttachmentDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.UoloadAttachmentRequest;
import com.pantavanij.sourcingreq.services.domain.request.UoloadLogoImageRequest;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponseStatus;
import com.pantavanij.sourcingreq.services.domain.response.UoloadAttachmentResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.service.sourcingreq.AttachmentService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantConfigService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.endpoint.version}")
public class AttachmentController {
    private static final Logger logger = LoggerFactory.getLogger(AttachmentController.class);
    private final AttachmentService attachmentService;
    private final TenantService tenantService;
    private final TenantConfigService tenantConfigService;

    @PreAuthorize("hasAnyAuthority('SQN','SQP','SAM')")
    @PostMapping(value = "/attachment/upload")
    public ResponseEntity uploadAttachment(UoloadAttachmentRequest request) {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);
        AttachmentDto attachmentDto = attachmentService.UploadAttachment(tenant, request);
        return new ResponseEntity<>(new UoloadAttachmentResponse(attachmentDto), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('SAM')")
    @PostMapping(value = "/image/logo/upload")
    public ResponseEntity uploadLogoImage(@RequestBody UoloadLogoImageRequest request) {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);
        String system = request.getSystem();
        if(system.equalsIgnoreCase("sr")){
            tenantConfigService.setSRLogoImageFileId(tenant.getRecId(), request.getSrLogoImageFileId());
            tenantConfigService.setSRLogoImageStyles(tenant.getRecId(), request.getSrLogoImageStyles());
        } else if(system.equalsIgnoreCase("ep")){
            tenantConfigService.setEPLogoImageFileId(tenant.getRecId(), request.getEpLogoImageFileId());
            tenantConfigService.setEPLogoImageStyles(tenant.getRecId(), request.getEpLogoImageStyles());
        } else if(system.equalsIgnoreCase("se")){
            tenantConfigService.setSELogoImageFileId(tenant.getRecId(), request.getSeLogoImageFileId());
            tenantConfigService.setSELogoImageStyles(tenant.getRecId(), request.getSeLogoImageStyles());
        } else if(system.equalsIgnoreCase("erfx")){
            tenantConfigService.setERFXLogoImageFileId(tenant.getRecId(), request.getErfxLogoImageFileId());
            tenantConfigService.setERFXLogoImageStyles(tenant.getRecId(), request.getErfxLogoImageStyles());
        } else if(system.equalsIgnoreCase("uam-admin")){
            tenantConfigService.setUAMAdminLogoImageFileId(tenant.getRecId(), request.getUamAdminLogoImageFileId());
            tenantConfigService.setUAMAdminLogoImageStyles(tenant.getRecId(), request.getUamAdminLogoImageStyles());
        } else if(system.equalsIgnoreCase("dashboard")){
            tenantConfigService.setDashboardLogoImageFileId(tenant.getRecId(), request.getDashboardLogoImageFileId());
            tenantConfigService.setDashboardLogoImageStyles(tenant.getRecId(), request.getDashboardLogoImageStyles());
        }

        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.I1001, ApiMessage.I1001.description()), HttpStatus.OK);
    }

//    @PreAuthorize("hasAnyAuthority('SQN','SQP')")
//    @DeleteMapping(value = "/attachment/{attachmentId}")
//    public ResponseEntity deleteRequestAttachment(@PathVariable Long attachmentId) {
//        String tenantCode = AppUtil.getTenantId();
//        Tenant tenant = tenantService.findByCode(tenantCode);
//        boolean success = attachmentService.deleteAttachment(tenant, attachmentId);
//        if (success)
//            return new ResponseEntity<>(new ApiResponse(null), HttpStatus.OK);
//        else
//            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7012, ApiMessage.E7012.description()), HttpStatus.NOT_FOUND);
//    }

    @PreAuthorize("hasAnyAuthority('SQN','SQP','SQV','SRL','SQA')")
    @GetMapping(value = "/attachment/download")
    public ResponseEntity downloadAttachment(@RequestParam() String uniqueFileName) {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);
        ByteArrayResource bytes = attachmentService.downloadAttachment(tenant, uniqueFileName);
        HttpHeaders header = new HttpHeaders();
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + uniqueFileName);
        header.setContentLength(bytes.contentLength());
        return new ResponseEntity<>(bytes, header, HttpStatus.OK);
    }
}
