package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.SupplierOptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.elasticsearch.SupplierWebworksDto;
import com.pantavanij.sourcingreq.services.domain.dto.elasticsearch.SupplierContactsDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Supplier;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.SupplierWebWorkSearchByTPShortNameRequest;
import com.pantavanij.sourcingreq.services.domain.request.supplier.CreateSupplierRequest;
import com.pantavanij.sourcingreq.services.domain.response.CreateSupplierResponse;

import java.util.List;

public interface SupplierService {
    List<SupplierOptionDto> getSupplierByTenantAndSearchTerm(Tenant tenant, String searchTerm);
    Boolean isExistingERPSupplier(String taxRegistrationNumber, Integer tenantId);
    CreateSupplierResponse createERPSupplier(CreateSupplierRequest createSupplierRequest, Integer tenantId);
    List<SupplierOptionDto> getSupplierByRequestId(Tenant tenant, Long requestId, String searchTerm);
    Supplier getSupplierByShortName(String shortName, Integer tenantId);
    Supplier createSupplier(Supplier supplier);
    SupplierWebworksDto getSupplierWebWorkByTPShortName(SupplierWebWorkSearchByTPShortNameRequest supplierWebWorkSearchByTPShortNameRequest);
    List<SupplierContactsDto> getSupplierContactByTPShortName(String tpShortName);
}
