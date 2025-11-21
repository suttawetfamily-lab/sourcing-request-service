package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.MasterGridFieldDto;
import com.pantavanij.sourcingreq.services.domain.dto.MasterGridFieldSearchableDto;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponseStatus;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.enums.MasterGroup;
import com.pantavanij.sourcingreq.services.service.sourcingreq.MasterGridFieldService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.endpoint.version}")
public class MasterGridFieldController {
    private final MasterGridFieldService masterGridFieldService;

    @GetMapping(value = "/master-grid-field")
    public ResponseEntity<ApiResponse<List<MasterGridFieldDto>>> getMasterGridField(@RequestParam MasterGroup masterGroup) {
        String tenantCode = AppUtil.getTenantId();
        List<MasterGridFieldDto> masterGridFieldDtoList =
                masterGridFieldService.getMasterGridField(masterGroup.groupCode(), tenantCode);

        if (masterGridFieldDtoList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }

        return ResponseEntity.ok().body(new ApiResponse(masterGridFieldDtoList));
    }


    @GetMapping(value = "/master/grid-field/searchable")
    public ResponseEntity<ApiResponse<List<MasterGridFieldSearchableDto>>> getMasterGridFieldSearchable(@RequestParam MasterGroup masterGroup) {
        String tenantCode = AppUtil.getTenantId();
        List<MasterGridFieldSearchableDto> masterGridFieldDtoList =
                masterGridFieldService.getMasterGridFieldSearchable(masterGroup.groupCode(), tenantCode);

        if (masterGridFieldDtoList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }

        return ResponseEntity.ok().body(new ApiResponse(masterGridFieldDtoList));
    }
}
