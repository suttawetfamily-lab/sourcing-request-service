package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponse;
import com.pantavanij.sourcingreq.services.enums.VatType;
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
public class VatTypeController {

    @GetMapping(value = "/vat-type", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<OptionDto>>> getVatTypeByTenantId(){
        List<OptionDto> vatTypeOption = Arrays.asList(
                OptionDto.builder()
                        .value(VatType.INCLUDED_VAT.value().toString())
                        .name(VatType.INCLUDED_VAT.name())
                        .label(VatType.INCLUDED_VAT.description()).build(),
                OptionDto.builder()
                        .value(VatType.EXCLUDED_VAT.value().toString())
                        .name(VatType.EXCLUDED_VAT.name())
                        .label(VatType.EXCLUDED_VAT.description()).build()
        );

        return ResponseEntity.ok().body(new ApiResponse(vatTypeOption));
    }
}
