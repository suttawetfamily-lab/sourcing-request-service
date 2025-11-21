package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.ApprovalStatusNameDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestStatusNameDto;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponseStatus;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.service.sourcingreq.DeptApprovalStatusService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantApprovalStatusService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantRequestStatusService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequestStatusControllerTest {

    private final TenantRequestStatusService tenantRequestStatusService = mock(TenantRequestStatusService.class);
    private final TenantApprovalStatusService tenantApprovalStatusService = mock(TenantApprovalStatusService.class);
    private final TenantService tenantService = mock(TenantService.class);
    private final DeptApprovalStatusService deptApprovalStatusService = mock(DeptApprovalStatusService.class);
    private final RequestStatusController requestStatusController = new RequestStatusController(
            tenantRequestStatusService,
            tenantApprovalStatusService,
            tenantService,
            deptApprovalStatusService);

    @Test
    public void getRequestStatusSearchByTenant_success() {
        List<RequestStatusNameDto> mockRequestStatusDtoList = Arrays.asList(
                RequestStatusNameDto.builder().recId(1).name("Draft").build(),
                RequestStatusNameDto.builder().recId(2).name("Awaiting").build(),
                RequestStatusNameDto.builder().recId(3).name("Partial Completed").build(),
                RequestStatusNameDto.builder().recId(4).name("Completed").build(),
                RequestStatusNameDto.builder().recId(5).name("Rejected").build(),
                RequestStatusNameDto.builder().recId(6).name("Cancelled").build()
        );

        when(tenantRequestStatusService.getRequestStatusList()).thenReturn(mockRequestStatusDtoList);

        ResponseEntity actualResult = requestStatusController.getRequestStatusSearchByTenant();

        ResponseEntity expectedResult = ResponseEntity.ok().body(new ApiResponse(mockRequestStatusDtoList));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getRequestStatusSearchByTenant_notFound() {
        when(tenantRequestStatusService.getRequestStatusList()).thenReturn(Collections.emptyList());

        ResponseEntity actualResult = requestStatusController.getRequestStatusSearchByTenant();

        ResponseEntity expectedResult = ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiResponse(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getApprovalStatusSearchByTenant_success() {
        List<ApprovalStatusNameDto> mockApprovalStatusNameDtoList = Arrays.asList(
                ApprovalStatusNameDto.builder().recId(1).name("Awaiting").build(),
                ApprovalStatusNameDto.builder().recId(2).name("Partial Completed").build(),
                ApprovalStatusNameDto.builder().recId(3).name("Completed").build(),
                ApprovalStatusNameDto.builder().recId(4).name("Rejected").build(),
                ApprovalStatusNameDto.builder().recId(5).name("Cancelled").build()
        );

        when(tenantApprovalStatusService.getApprovalStatusSearchList()).thenReturn(mockApprovalStatusNameDtoList);

        ResponseEntity actualResult = requestStatusController.getApprovalStatusSearchByTenant();

        ResponseEntity expectedResult = ResponseEntity.ok().body(new ApiResponse(mockApprovalStatusNameDtoList));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getApprovalStatusSearchByTenant_notFound() {
        when(tenantApprovalStatusService.getApprovalStatusSearchList()).thenReturn(Collections.emptyList());

        ResponseEntity actualResult = requestStatusController.getApprovalStatusSearchByTenant();

        ResponseEntity expectedResult = ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiResponse(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getReviewStatusSearchByTenant_success() {
        List<RequestStatusNameDto> mockRequestStatusDtoList = Arrays.asList(
                RequestStatusNameDto.builder().recId(1).name("Draft").build(),
                RequestStatusNameDto.builder().recId(2).name("Awaiting").build(),
                RequestStatusNameDto.builder().recId(3).name("Partial Completed").build(),
                RequestStatusNameDto.builder().recId(4).name("Completed").build(),
                RequestStatusNameDto.builder().recId(5).name("Rejected").build(),
                RequestStatusNameDto.builder().recId(6).name("Cancelled").build()
        );

        when(tenantRequestStatusService.getReviewStatusList()).thenReturn(mockRequestStatusDtoList);

        ResponseEntity actualResult = requestStatusController.getReviewStatusSearchByTenant();

        ResponseEntity expectedResult = ResponseEntity.ok().body(new ApiResponse(mockRequestStatusDtoList));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getReviewStatusSearchByTenant_notFound() {
        when(tenantRequestStatusService.getReviewStatusList()).thenReturn(Collections.emptyList());

        ResponseEntity actualResult = requestStatusController.getReviewStatusSearchByTenant();

        ResponseEntity expectedResult = ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiResponse(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        assertEquals(expectedResult, actualResult);
    }

}
