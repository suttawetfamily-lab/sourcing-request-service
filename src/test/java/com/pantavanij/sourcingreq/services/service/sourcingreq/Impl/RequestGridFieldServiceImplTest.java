package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.RequestGridFieldDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestGridFieldSearchableDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantRequestGridField;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestGridFieldRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantOrganizationTemplateRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantRequestGridFieldRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestGridFieldService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UaaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequestGridFieldServiceImplTest {
    private TenantRequestGridFieldRepository tenantRequestGridFieldRepository = mock(TenantRequestGridFieldRepository.class);
    private RequestGridFieldRepository requestGridFieldRepository = mock(RequestGridFieldRepository.class);
    private TenantService tenantService = mock(TenantService.class);
    private UaaService uaaService = mock(UaaService.class);
    private  TenantOrganizationTemplateRepository tenantOrganizationTemplateRepository = mock(TenantOrganizationTemplateRepository.class);

    private RequestGridFieldService requestGridFieldService = new RequestGridFieldServiceImpl(tenantRequestGridFieldRepository,tenantOrganizationTemplateRepository, requestGridFieldRepository, tenantService, uaaService);

    @Test
    public void getRequestGridField() {
        String tenantCode = "TRUE";
        String privilegeCode = "SQA";

        List<TenantRequestGridField> mockTenantRequestGridField = Arrays.asList(
                TenantRequestGridField.builder()
                        .privilegeCode("SQA")
                        .code("requestNo")
                        .displayName("Request No.")
                        .sequence(1)
                        .visible(true)
                        .sorting("ASC")
                        .width(null)
                        .build(),
                TenantRequestGridField.builder()
                        .privilegeCode("SQA")
                        .code("requestName")
                        .displayName("Sourcing Request Name")
                        .sequence(2)
                        .visible(true)
                        .sorting("ASC")
                        .width(150)
                        .build(),
                TenantRequestGridField.builder()
                        .privilegeCode("SQA")
                        .code("projectCode")
                        .displayName("Project Code")
                        .sequence(4)
                        .visible(true)
                        .sorting("ASC")
                        .width(null)
                        .build(),
                TenantRequestGridField.builder()
                        .privilegeCode("SQA")
                        .code("status")
                        .displayName("Status")
                        .sequence(12)
                        .visible(false)
                        .sorting("ASC")
                        .width(null)
                        .build()
        );

        when(tenantRequestGridFieldRepository.findByPrivilegeCodeAndTenant_CodeOrderBySequence(privilegeCode, tenantCode))
                .thenReturn(mockTenantRequestGridField);

        List<RequestGridFieldDto> actualResult = requestGridFieldService.getRequestGridField(privilegeCode, tenantCode,0);

        List<RequestGridFieldDto> expectedResult = Arrays.asList(
                RequestGridFieldDto.builder()
                        .code("requestNo")
                        .displayName("Request No.")
                        .sequence(1)
                        .sorting("ASC")
                        .width(null)
                        .build(),
                RequestGridFieldDto.builder()
                        .code("requestName")
                        .displayName("Sourcing Request Name")
                        .sequence(2)
                        .sorting("ASC")
                        .width(150)
                        .build(),
                RequestGridFieldDto.builder()
                        .code("projectCode")
                        .displayName("Project Code")
                        .sequence(4)
                        .sorting("ASC")
                        .width(null)
                        .build()
        );

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getRequestGridFieldSearchable() {
        String tenantCode = "TRUE";
        String privilegeCode = "SQA";

        List<TenantRequestGridField> mockTenantRequestGridField = Arrays.asList(
                TenantRequestGridField.builder()
                        .privilegeCode("SQA")
                        .code("requestNo")
                        .displayName("Request No.")
                        .sequence(1)
                        .visible(true)
                        .sorting("ASC")
                        .width(25)
                        .build(),
                TenantRequestGridField.builder()
                        .privilegeCode("SQA")
                        .code("requestName")
                        .displayName("Sourcing Request Name")
                        .sequence(2)
                        .visible(true)
                        .sorting("ASC")
                        .width(15)
                        .build(),
                TenantRequestGridField.builder()
                        .privilegeCode("SQA")
                        .code("projectCode")
                        .displayName("Project Code")
                        .sequence(4)
                        .visible(true)
                        .sorting("ASC")
                        .width(10)
                        .build()
        );

        when(tenantRequestGridFieldRepository.findByPrivilegeCodeAndTenant_CodeAndSearchableOrderBySequence(
                privilegeCode, tenantCode, true)).thenReturn(mockTenantRequestGridField);

        List<RequestGridFieldSearchableDto> actualResult =
                requestGridFieldService.getRequestGridFieldSearchable(privilegeCode, tenantCode, 0);

        List<RequestGridFieldSearchableDto> expectedResult = Arrays.asList(
                RequestGridFieldSearchableDto.builder()
                        .code("requestNo")
                        .displayName("Request No.")
                        .sequence(1)
                        .build(),
                RequestGridFieldSearchableDto.builder()
                        .code("requestName")
                        .displayName("Sourcing Request Name")
                        .sequence(2)
                        .build(),
                RequestGridFieldSearchableDto.builder()
                        .code("projectCode")
                        .displayName("Project Code")
                        .sequence(4)
                        .build()
        );

        assertEquals(expectedResult, actualResult);
    }

}

