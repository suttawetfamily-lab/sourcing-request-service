package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.MenuPrivilegeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuPrivilegeControllerTest {

    @Mock
    private MenuPrivilegeService menuPrivilegeService;

    @InjectMocks
    private MenuPrivilegeController menuPrivilegeController;

    private MenuPrivilegeDto menuPrivilegeDto;
    private List<MenuPrivilegeDto> menuPrivilegeDtoList;

    @BeforeEach
    public void setUp() {
        menuPrivilegeDto = new MenuPrivilegeDto();
        menuPrivilegeDto.setPathUrl("/test/path");
        menuPrivilegeDtoList = List.of(menuPrivilegeDto);
    }

    @Test
    public void getMenuPrivilege_Success() {
        when(menuPrivilegeService.getMenuPrivilege()).thenReturn(menuPrivilegeDtoList);

        ResponseEntity response = menuPrivilegeController.getMenuPrivilege();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(menuPrivilegeDtoList, response.getBody());
    }

    @Test
    public void getMenuPrivilege_ReturnsError_WhenNoData() {
        when(menuPrivilegeService.getMenuPrivilege()).thenReturn(null);

        ResponseEntity response = menuPrivilegeController.getMenuPrivilege();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ApiErrorResponse);
    }

    @Test
    public void checkPermission_Success() {
        when(menuPrivilegeService.checkPermission(anyString(), eq(1))).thenReturn(menuPrivilegeDto);

        ResponseEntity response = menuPrivilegeController.checkPermission("/test/path");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof MenuPrivilegeResponse);
    }

    @Test
    public void checkPermission_Unauthorized() {
        MenuPrivilegeDto emptyDto = new MenuPrivilegeDto();
        when(menuPrivilegeService.checkPermission(anyString(), eq(1))).thenReturn(emptyDto);

        ResponseEntity response = menuPrivilegeController.checkPermission("/test/path");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void createMenuPrivilege_Success() {
        MenuPrivilegeRequest request = new MenuPrivilegeRequest();
        when(menuPrivilegeService.createMenuPrivilege(any())).thenReturn(1);

        ResponseEntity response = menuPrivilegeController.createMenuPrivilege(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void createMenuPrivilege_Conflict() {
        MenuPrivilegeRequest request = new MenuPrivilegeRequest();
        when(menuPrivilegeService.createMenuPrivilege(any())).thenReturn(-1);

        ResponseEntity response = menuPrivilegeController.createMenuPrivilege(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void searchMenuPrivilege_Success() {
        MenuPrivilegeSearchRequest request = new MenuPrivilegeSearchRequest();
        request.setPage(1);
        request.setPageSize(10);

        MenuPrivilegeSearchDto searchDto = new MenuPrivilegeSearchDto();
        searchDto.setMenuPrivilegeList(menuPrivilegeDtoList);
        searchDto.setTotal(1L);
        searchDto.setTotalPage(1);

        when(menuPrivilegeService.searchMenuPrivilegeByCondition(any(), any())).thenReturn(searchDto);

        ResponseEntity response = menuPrivilegeController.searchMenuPrivilege(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof MenuPrivilegeSearchResponse);
    }

    @Test
    public void deleteMenuPrivilege_Success() {
        when(menuPrivilegeService.deleteMenuPrivilegeById(anyInt())).thenReturn(1);

        ResponseEntity response = menuPrivilegeController.deleteMenuPrivilege(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void deleteMenuPrivilege_NotFound() {
        when(menuPrivilegeService.deleteMenuPrivilegeById(anyInt())).thenReturn(0);

        ResponseEntity response = menuPrivilegeController.deleteMenuPrivilege(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
