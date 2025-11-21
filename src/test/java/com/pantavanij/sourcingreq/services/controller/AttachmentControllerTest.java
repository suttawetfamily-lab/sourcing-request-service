package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.AttachmentDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.UoloadAttachmentRequest;
import com.pantavanij.sourcingreq.services.service.sourcingreq.AttachmentService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AttachmentControllerTest {

    @Mock
    private AttachmentService attachmentService;

    @Mock
    private TenantService tenantService;

    @InjectMocks
    private AttachmentController attachmentController;

    private static final String TENANT_CODE = "TEST_TENANT";
    private Tenant mockTenant;
    private UoloadAttachmentRequest mockRequest;

    @Before
    public void setup() {
        mockTenant = new Tenant();
        mockTenant.setCode(TENANT_CODE);

        mockRequest = new UoloadAttachmentRequest();
        // Set necessary request properties
    }

    @Test
    public void uploadAttachment_Success() {
        try (MockedStatic<AppUtil> appUtilMock = mockStatic(AppUtil.class)) {
            // Given
            appUtilMock.when(AppUtil::getTenantId).thenReturn(TENANT_CODE);
            when(tenantService.findByCode(TENANT_CODE)).thenReturn(mockTenant);

            AttachmentDto mockAttachmentDto = new AttachmentDto();
            when(attachmentService.UploadAttachment(any(Tenant.class), any(UoloadAttachmentRequest.class)))
                .thenReturn(mockAttachmentDto);

            // When
            ResponseEntity response = attachmentController.uploadAttachment(mockRequest);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
        }
    }

    @Test
    public void downloadAttachment_Success() {
        try (MockedStatic<AppUtil> appUtilMock = mockStatic(AppUtil.class)) {
            // Given
            String uniqueFileName = "test-file.pdf";
            byte[] fileContent = "test content".getBytes();
            ByteArrayResource mockResource = new ByteArrayResource(fileContent);

            appUtilMock.when(AppUtil::getTenantId).thenReturn(TENANT_CODE);
            when(tenantService.findByCode(TENANT_CODE)).thenReturn(mockTenant);
            when(attachmentService.downloadAttachment(any(Tenant.class), any(String.class)))
                .thenReturn(mockResource);

            // When
            ResponseEntity response = attachmentController.downloadAttachment(uniqueFileName);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(mockResource, response.getBody());
        }
    }

    @Test
    public void downloadAttachment_WithHeaders() {
        try (MockedStatic<AppUtil> appUtilMock = mockStatic(AppUtil.class)) {
            // Given
            String uniqueFileName = "test-file.pdf";
            byte[] fileContent = "test content".getBytes();
            ByteArrayResource mockResource = new ByteArrayResource(fileContent);

            appUtilMock.when(AppUtil::getTenantId).thenReturn(TENANT_CODE);
            when(tenantService.findByCode(TENANT_CODE)).thenReturn(mockTenant);
            when(attachmentService.downloadAttachment(any(Tenant.class), any(String.class)))
                .thenReturn(mockResource);

            // When
            ResponseEntity response = attachmentController.downloadAttachment(uniqueFileName);

            // Then
            assertNotNull(response.getHeaders().getContentDisposition());
            assertEquals("attachment; filename=\"" + uniqueFileName + "\"",
                response.getHeaders().getContentDisposition().toString());
            assertEquals(fileContent.length, response.getHeaders().getContentLength());
        }
    }
}
