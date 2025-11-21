package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.ActionPrivilegeDto;
import com.pantavanij.sourcingreq.services.domain.response.ApiErrorResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.service.sourcingreq.ActionPrivilegeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
public class ActionPrivilegeController {

    private final ActionPrivilegeService actionPrivilegeService;

    @GetMapping(value = "/privilege/action", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getActionPrivilege() {
        List<ActionPrivilegeDto> actionPrivilegeDtoList = actionPrivilegeService.getActionPrivilege();
        if (!actionPrivilegeDtoList.isEmpty()) {
            return ResponseEntity.ok().body(actionPrivilegeDtoList);
        } else {
            return ResponseEntity.ok().body(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()));
        }
    }
}