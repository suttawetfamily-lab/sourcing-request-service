package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.constraint.SearchTermConstraint;
import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestStatusNameDto;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponseStatus;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.enums.RequestStatusOption;
import com.pantavanij.sourcingreq.services.service.sourcingreq.SourcingStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
public class SourcingStatusController {

    private final SourcingStatusService sourcingStatusService;

    @GetMapping(value = "/sourcing-status/option", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<OptionDto>>> getRequestStatusOption() {
        List<OptionDto> sourcingStatusOptionList = new ArrayList<>();
        for (RequestStatusOption option : RequestStatusOption.values()) {
            sourcingStatusOptionList.add(new OptionDto(option.getValue().toString(), option.getCode(), option.getDescription(), option.getIsDefault()));
        }

        if (!sourcingStatusOptionList.isEmpty()) {
            return ResponseEntity.ok().body(new ApiResponse(sourcingStatusOptionList));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }
    }

    @GetMapping(value = "/sourcing-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<RequestStatusNameDto>>> getRequestStatusSearchBySearchTerm(@RequestParam @SearchTermConstraint String searchTerm) {
        List<OptionDto> sourcingStatusNameDtoList = sourcingStatusService.getRequestStatusOptionDto(searchTerm.trim());
        if (!sourcingStatusNameDtoList.isEmpty()) {
            return ResponseEntity.ok().body(new ApiResponse(sourcingStatusNameDtoList));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }
    }

}
