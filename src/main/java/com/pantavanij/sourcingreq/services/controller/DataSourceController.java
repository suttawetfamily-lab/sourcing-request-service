package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.endpoint.version}")
public class DataSourceController {

    private final DataSourceService dataSourceService;

    @GetMapping(value = "/data-source/test", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity testDatasource() {
        return new ResponseEntity<>(new ApiResponse<>("Data source controller..."),HttpStatus.OK);
    }

    @GetMapping(value = "/data-source/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAllDataSource() {
        List<DataSourceDto> dataSourceList = dataSourceService.getAllDataSource();
        if (!dataSourceList.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse<>(dataSourceList), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7010),HttpStatus.OK);
    }
}
