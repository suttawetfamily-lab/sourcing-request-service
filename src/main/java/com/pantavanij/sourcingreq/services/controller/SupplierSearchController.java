package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.service.sourcingreq.Impl.SupplierSearchServiceImpl;
import com.pantavanij.sourcingreq.services.domain.request.SupplierWebWorkSearchByTPShortNameRequest;
import com.pantavanij.sourcingreq.services.domain.request.SupplierWebWorksSearchRequest;
import com.pantavanij.sourcingreq.services.domain.response.elasticsearch.SupplierWebWorkResponse;
import com.pantavanij.sourcingreq.services.domain.response.elasticsearch.SupplierWebWorksResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
public class SupplierSearchController {

    private final SupplierSearchServiceImpl supplierSearchService;

    @PostMapping(value = "/company/search")
    public SupplierWebWorksResponse searchCompany(@RequestBody @Valid SupplierWebWorksSearchRequest request) {
        return supplierSearchService.searchCompany(request);
    }

    @PostMapping(value = "/branch/orgID")
    public SupplierWebWorkResponse getSupplier(@RequestBody @Valid SupplierWebWorkSearchByTPShortNameRequest request) {
        return supplierSearchService.getSupplier(request);
    }
}
