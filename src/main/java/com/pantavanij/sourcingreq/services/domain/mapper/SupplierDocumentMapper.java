package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.elasticsearch.SupplierWebworksDto;
import com.pantavanij.sourcingreq.services.domain.response.elasticsearch.SupplierDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SupplierDocumentMapper {
    SupplierDocumentMapper INSTANCE = Mappers.getMapper(SupplierDocumentMapper.class);

    @Mapping(target = "fullCompanyNameLocal", source = "supplierDocument.invNameLocal")
    @Mapping(target = "fullCompanyNameEN", source = "supplierDocument.invNameInter")
    @Mapping(target = "companyNameEN", source = "supplierDocument.companyNameInter")
    @Mapping(target = "branchNumber", source = "supplierDocument.branch")
    @Mapping(target = "TPShortName", source = "supplierDocument.orgId")
    SupplierWebworksDto toSupplierWebworksDto(SupplierDocument supplierDocument);
}
