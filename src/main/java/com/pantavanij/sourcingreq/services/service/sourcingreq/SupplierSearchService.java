package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.request.SupplierWebWorkSearchByTPShortNameRequest;
import com.pantavanij.sourcingreq.services.domain.request.SupplierWebWorksSearchRequest;
import com.pantavanij.sourcingreq.services.domain.response.elasticsearch.SupplierWebWorkResponse;
import com.pantavanij.sourcingreq.services.domain.response.elasticsearch.SupplierWebWorksResponse;

public interface SupplierSearchService {

    SupplierWebWorksResponse searchCompany(SupplierWebWorksSearchRequest request);

    SupplierWebWorkResponse getSupplier(SupplierWebWorkSearchByTPShortNameRequest request);
}

