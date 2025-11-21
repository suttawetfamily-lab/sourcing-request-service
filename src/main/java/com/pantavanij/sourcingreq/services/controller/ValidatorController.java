package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
public class ValidatorController {

    private final ValidatorService validatorService;

    @GetMapping(value = "/validator/test", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity validatorTest() {
        return new ResponseEntity<>(new ApiResponse<>("Validator controller..."),HttpStatus.OK);
    }

    @GetMapping(value = "/validator/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAllValidator() {
        List<ValidatorDto> ValidatorList = validatorService.getAllValidator();
        if (!ValidatorList.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse<>(ValidatorList), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7010),HttpStatus.OK);
    }
}
