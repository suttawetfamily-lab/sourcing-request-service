package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.request.DeleteExistingPriceItemRequest;
import com.pantavanij.sourcingreq.services.domain.request.ExistingPriceItemRequest;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExistingPriceItemControllerTest {

    @InjectMocks
    private ExistingPriceItemController existingPriceItemController;

    @Mock
    private ExistingPriceItemService existingPriceItemService;

    @Mock
    private TenantService tenantService;

    @Mock
    private RequestService requestService;

    @Mock
    private SourcingStatusService sourcingStatusService;

    private ExistingPriceItemRequest validRequest;
    private Request mockRequest;
    private ExistingPriceItemSearchDto mockSearchDto;

    @Before
    public void setup() {
        validRequest = new ExistingPriceItemRequest();
        validRequest.setUnitPrice(new BigDecimal("100.00"));
        validRequest.setRequestId(1L);
        validRequest.setItemName("Test Item");

        mockRequest = new Request();
        mockRequest.setRecId(1L);

        mockSearchDto = new ExistingPriceItemSearchDto();
        mockSearchDto.setExistingPriceItemDtoList(new ArrayList<>());
        mockSearchDto.setTotal(0L);
        mockSearchDto.setTotalPage(0);
    }

    @Test
    public void submitExistingPriceItem_WithValidData_ReturnsSuccess() {
        when(existingPriceItemService.saveExistingPriceItem(any(ExistingPriceItemRequest.class), eq(true)))
                .thenReturn(null);

        ResponseEntity response = existingPriceItemController.submitExistingPriceItem(validRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void submitExistingPriceItem_WithZeroPrice_ReturnsError() {
        validRequest.setUnitPrice(BigDecimal.ZERO);
        ResponseEntity response = existingPriceItemController.submitExistingPriceItem(validRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getExistingPriceItemList_ReturnsValidResponse() {
        when(requestService.searchRequestByRecId(1L)).thenReturn(mockRequest);
        when(existingPriceItemService.getItemByRequest(eq(mockRequest), any(Pageable.class), eq("AUTH"), eq("path")))
                .thenReturn(mockSearchDto);

        ResponseEntity response = existingPriceItemController.getExistingPriceItemList(1L, "AUTH", 1, 10, "path");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void deleteRequestItem_WithValidRequest_ReturnsSuccess() {
        DeleteExistingPriceItemRequest deleteRequest = new DeleteExistingPriceItemRequest();

        Tenant mockTenant = new Tenant();
        mockTenant.setRecId(1);

        when(tenantService.findByCode(any())).thenReturn(mockTenant);
        when(existingPriceItemService.rejectExistingPriceItem(any(), any())).thenReturn(true);

        ResponseEntity response = existingPriceItemController.deleteRequestItem(deleteRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getExistingPriceItemListV2_ReturnsValidResponse() {
        Sort sort = Sort.by("ItemSequence").ascending();
        Pageable pageable = PageRequest.of(0, 10, sort);

        ExistingPriceItemSearchDtoV2 mockSearchDtoV2 = new ExistingPriceItemSearchDtoV2();
        mockSearchDtoV2.setExistingPriceItemDtoList(new ArrayList<>());
        mockSearchDtoV2.setTotal(0L);
        mockSearchDtoV2.setTotalPage(0);

        when(requestService.searchRequestByRecId(1L)).thenReturn(mockRequest);
        when(existingPriceItemService.getItemByRequestV2(eq(mockRequest), any(Pageable.class), eq("AUTH"), eq("path"), 2, ""))
                .thenReturn(mockSearchDtoV2);

        ResponseEntity response = existingPriceItemController.getExistingPriceItemListV2(1L, "AUTH", 1, 10,  2, "" ,"path");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getExistingPriceItemIDList_WithValidRequest_ReturnsResponse() {
        when(requestService.searchRequestByRecId(1L)).thenReturn(mockRequest);
        when(existingPriceItemService.getAllItemByRequest(mockRequest)).thenReturn(new ArrayList<>());

        ResponseEntity response = existingPriceItemController.getExistingPriceItemIDList(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
