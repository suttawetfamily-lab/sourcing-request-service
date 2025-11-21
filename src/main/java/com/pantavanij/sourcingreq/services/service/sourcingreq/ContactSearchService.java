package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.response.elasticsearch.SupplierContactResponse;

public interface ContactSearchService {

    SupplierContactResponse getContact(String TPShortName);
}

