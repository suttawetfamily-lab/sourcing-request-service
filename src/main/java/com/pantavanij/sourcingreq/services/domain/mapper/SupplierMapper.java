package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.SupplierDto;
import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.SupplierOptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.approver.ApproverRequestDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Department;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Supplier;
import com.pantavanij.sourcingreq.services.util.SupplierNameUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface SupplierMapper {
    SupplierMapper INSTANCE = Mappers.getMapper(SupplierMapper.class);

    SupplierDto toSupplierDto(Supplier supplier);

    @Mapping(target = "recId", source = "supplier.recId")
    @Mapping(target = "value", source = "supplier.shortName")
    @Mapping(target = "name", source = "supplier.shortName")
    //@Mapping(target = "label", source = "supplier.fullName")
    @Mapping(target = "label", source = ".", qualifiedByName = "mapLabel")
    SupplierOptionDto toSupplierOptionDto(Supplier supplier);

    List<SupplierOptionDto> toSupplierOptionDto(List<Supplier> suppliers);

    default SupplierOptionDto toSupplierOptionDto(Supplier supplier, boolean isShowSupplierLocalLanguage, String supplierFieldName) {
        SupplierOptionDto supplierOptionDto = toSupplierOptionDto(supplier);
        if (supplierOptionDto != null) {
            supplierOptionDto.setLabel(SupplierNameUtil.resolveSupplierName(supplier, isShowSupplierLocalLanguage, supplierFieldName));
        }
        return supplierOptionDto;
    }

    default List<SupplierOptionDto> toSupplierOptionDto(List<Supplier> supplierList, boolean isShowSupplierLocalLanguage, String supplierFieldName) {
        List<SupplierOptionDto> supplierOptionDtoList = new ArrayList<>();
        if (supplierList != null) {
            for (Supplier supplier : supplierList) {
                supplierOptionDtoList.add(toSupplierOptionDto(supplier, isShowSupplierLocalLanguage, supplierFieldName));
            }
        }
        return supplierOptionDtoList;
    }


    @Named(value = "mapLabel")
    default String mapLabel(Supplier supplier) {
        return supplier.getFullCompanyNameLocal();
    }
}
