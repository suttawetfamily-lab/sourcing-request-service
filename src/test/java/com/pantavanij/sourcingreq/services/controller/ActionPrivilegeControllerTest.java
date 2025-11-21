package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.ActionPrivilegeDto;
import com.pantavanij.sourcingreq.services.domain.response.ApiErrorResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.service.sourcingreq.ActionPrivilegeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ActionPrivilegeControllerTest {

    private ActionPrivilegeService actionPrivilegeService = mock(ActionPrivilegeService.class);
    private ActionPrivilegeController actionPrivilegeController = new ActionPrivilegeController(actionPrivilegeService);

    @Test
    public void getActionPrivilege_success() {
        List<ActionPrivilegeDto> mockActionPrivilegeDtoList =
                Arrays.asList(
                        ActionPrivilegeDto.builder()
                                .recId(1)
                                .action("Create Sourcing")
                                .typeId(1)
                                .privilegeCode("SQN")
                                .build(),
                        ActionPrivilegeDto.builder()
                                .recId(2)
                                .action("Approve Shortlist")
                                .typeId(1)
                                .privilegeCode("ERF")
                                .build()
                );

        when(actionPrivilegeService.getActionPrivilege()).thenReturn(mockActionPrivilegeDtoList);

        ResponseEntity actualResult = actionPrivilegeController.getActionPrivilege();

        ResponseEntity expectedResult = ResponseEntity.ok().body(mockActionPrivilegeDtoList);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getActionPrivilege_notFound() {
        List<ActionPrivilegeDto> mockActionPrivilegeDtoList = Collections.emptyList();

        when(actionPrivilegeService.getActionPrivilege()).thenReturn(mockActionPrivilegeDtoList);

        ResponseEntity actualResult = actionPrivilegeController.getActionPrivilege();

        ResponseEntity expectedResult = ResponseEntity.ok()
                .body(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()));
        assertEquals(expectedResult, actualResult);
    }

}