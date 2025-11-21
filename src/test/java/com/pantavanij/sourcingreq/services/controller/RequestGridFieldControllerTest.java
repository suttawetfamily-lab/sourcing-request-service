package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.RequestGridFieldDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestGridFieldSearchableDto;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponseStatus;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.enums.PathUrl;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestGridFieldService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RequestGridFieldControllerTest {

    private RequestGridFieldService requestGridFieldService = mock(RequestGridFieldService.class);
    private RequestGridFieldController requestGridFieldController = new RequestGridFieldController(requestGridFieldService);

    @Test
    public void getRequestGridField_success() {
        String tenantCode = "TRUE";

        List<RequestGridFieldDto> mockRequestGridFieldDtoList = Arrays.asList(
                RequestGridFieldDto.builder()
                        .code("requestNo")
                        .displayName("Request No.")
                        .sequence(1)
                        .sorting("ASC")
                        .width(30)
                        .build(),
                RequestGridFieldDto.builder()
                        .code("projectCode")
                        .displayName("Project Code")
                        .sequence(4)
                        .sorting("ASC")
                        .width(100)
                        .build()
        );

        when(requestGridFieldService.getRequestGridField(PathUrl.APPROVE.privilegeCode(), tenantCode, 0)).thenReturn(mockRequestGridFieldDtoList);

        ResponseEntity actualResult;
        try (MockedStatic mockAppUtil = mockStatic(AppUtil.class)) {
            mockAppUtil.when(AppUtil::getTenantId).thenReturn(tenantCode);
            actualResult = requestGridFieldController.getRequestGridField(PathUrl.APPROVE, 0);
        }

        ResponseEntity expectedResult = ResponseEntity.ok().body(new ApiResponse(mockRequestGridFieldDtoList));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getRequestGridField_notFound() {
        String tenantCode = "TRUE";

        when(requestGridFieldService.getRequestGridField(PathUrl.APPROVE.privilegeCode(), tenantCode, 0)).thenReturn(Collections.emptyList());

        ResponseEntity actualResult;
        try (MockedStatic mockAppUtil = mockStatic(AppUtil.class)) {
            mockAppUtil.when(AppUtil::getTenantId).thenReturn(tenantCode);
            actualResult = requestGridFieldController.getRequestGridField(PathUrl.APPROVE, 0);
        }

        ResponseEntity expectedResult = ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiResponse(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getRequestGridFieldSearchable_success() {
        String tenantCode = "TRUE";

        List<RequestGridFieldSearchableDto> mockRequestGridFieldSearchableDtoList = Arrays.asList(
                RequestGridFieldSearchableDto.builder()
                        .code("requestNo")
                        .displayName("Request No.")
                        .sequence(1)
                        .build(),
                RequestGridFieldSearchableDto.builder()
                        .code("projectCode")
                        .displayName("Project Code")
                        .sequence(4)
                        .build()
        );

        when(requestGridFieldService.getRequestGridFieldSearchable(PathUrl.APPROVE.privilegeCode(), tenantCode, 0))
                .thenReturn(mockRequestGridFieldSearchableDtoList);

        ResponseEntity actualResult;
        try (MockedStatic mockAppUtil = mockStatic(AppUtil.class)) {
            mockAppUtil.when(AppUtil::getTenantId).thenReturn(tenantCode);
            actualResult = requestGridFieldController.getRequestGridFieldSearchable(PathUrl.APPROVE, 0);
        }

        ResponseEntity expectedResult = ResponseEntity.ok().body(new ApiResponse(mockRequestGridFieldSearchableDtoList));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getRequestGridFieldSearchable_notFound() {
        String tenantCode = "TRUE";

        when(requestGridFieldService.getRequestGridFieldSearchable(PathUrl.APPROVE.privilegeCode(), tenantCode, 0))
                .thenReturn(Collections.emptyList());

        ResponseEntity actualResult;
        try (MockedStatic mockAppUtil = mockStatic(AppUtil.class)) {
            mockAppUtil.when(AppUtil::getTenantId).thenReturn(tenantCode);
            actualResult = requestGridFieldController.getRequestGridFieldSearchable(PathUrl.APPROVE, 0);
        }

        ResponseEntity expectedResult = ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiResponse(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        assertEquals(expectedResult, actualResult);
    }

}