package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponse;
import com.pantavanij.sourcingreq.services.enums.RequestType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
@Validated
public class RequestTypeController {

    @GetMapping(value = "/request-type", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<OptionDto>>> getRequestTypeByTenantId(){
        List<OptionDto> requestTypeOption = Arrays.asList(
                OptionDto.builder()
                        .value(RequestType.REQUEST_TYPE_QUANTITY.id().toString())
                        .name(RequestType.REQUEST_TYPE_QUANTITY.name())
                        .label(RequestType.REQUEST_TYPE_QUANTITY.description()).build(),
                OptionDto.builder()
                        .value(RequestType.REQUEST_TYPE_CONDITION.id().toString())
                        .name(RequestType.REQUEST_TYPE_CONDITION.name())
                        .label(RequestType.REQUEST_TYPE_CONDITION.description()).build()
        );

        return ResponseEntity.ok().body(new ApiResponse(requestTypeOption));
    }
}
