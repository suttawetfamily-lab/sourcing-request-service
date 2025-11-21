package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponse;
import com.pantavanij.sourcingreq.services.enums.VatType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class VatTypeControllerTest {
    private VatTypeController vatTypeController = new VatTypeController();

    @Test
    public void getVatTypeByTenantId_success() {
        List<OptionDto> vatTypeOptions = Arrays.asList(
                OptionDto.builder()
                        .value(VatType.INCLUDED_VAT.value().toString())
                        .name(VatType.INCLUDED_VAT.name())
                        .label(VatType.INCLUDED_VAT.description()).build(),
                OptionDto.builder()
                        .value(VatType.EXCLUDED_VAT.value().toString())
                        .name(VatType.EXCLUDED_VAT.name())
                        .label(VatType.EXCLUDED_VAT.description()).build()
        );

        ResponseEntity actualResult = vatTypeController.getVatTypeByTenantId();

        ResponseEntity expectedResult = ResponseEntity.ok().body(new ApiResponse(vatTypeOptions));
        assertEquals(expectedResult, actualResult);
    }
}