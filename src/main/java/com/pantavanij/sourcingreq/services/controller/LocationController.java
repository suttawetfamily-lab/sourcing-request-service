package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.LocationDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.response.ApiErrorResponse;
import com.pantavanij.sourcingreq.services.domain.response.LocationListResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.service.sourcingreq.LocationService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;
    private final TenantService tenantService;

    @GetMapping(value = "/location", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getLocationByTenantId(@RequestHeader(value = "organization", required = false) Integer organizationId){
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);

        if (tenant == null)  throw new BusinessException(ApiMessage.E7016, ApiMessage.E7016.description());

        List<LocationDto> locationList = locationService.getLocationByTenantId(tenant.getRecId(), organizationId);

        if(!locationList.isEmpty()){
            return new ResponseEntity<>(new LocationListResponse(locationList), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7006, ApiMessage.E7006.description()) , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
