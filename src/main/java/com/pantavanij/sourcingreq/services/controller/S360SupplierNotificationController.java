package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.request.NotifySupplierRequest;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponseStatus;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.service.sourcingreq.SupplierNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/sourcingreq/v2")
@RequiredArgsConstructor
public class S360SupplierNotificationController {

    private final SupplierNotificationService supplierNotificationService;

    @PostMapping(value = "/notify-supplier", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity notifySupplier(@Valid @RequestBody NotifySupplierRequest request) {
        boolean result = supplierNotificationService.notifySupplier(request, true);
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.I1001, ApiMessage.I1001.description()), HttpStatus.ACCEPTED);
    }

}