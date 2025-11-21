package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.enums.VatType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OptionDtoMapper {
    OptionDtoMapper INSTANCE = Mappers.getMapper(OptionDtoMapper.class);

    @Mapping(target = "value", source = "requestObjective.objective.recId")
    @Mapping(target = "name", source = "requestObjective.objectiveCode")
    @Mapping(target = "label", source = "requestObjective.objectiveName")
    OptionDto toObjectiveOptionDto(RequestObjective requestObjective);

    @Mapping(target = "value", source = "requestCategory.id.categoryId")
    @Mapping(target = "name", source = "requestCategory.categoryCode")
    @Mapping(target = "label", source = "requestCategory.categoryName")
    OptionDto toCategoryOptionDto(RequestCategory requestCategory);

    @Mapping(target = "value", source = "tenantCategory.id")
    @Mapping(target = "name", source = "tenantCategory.categoryName")
    @Mapping(target = "label", source = "tenantCategory.categoryName")
    OptionDto toTenantCategoryOptionDto(TenantCategory tenantCategory);

    @Mapping(target = "value", source = "requestSubCategory.id.subCategoryId")
    @Mapping(target = "name", source = "requestSubCategory.subCategoryCode")
    @Mapping(target = "label", source = "requestSubCategory.subCategoryName")
    OptionDto toSubCategoryOptionDto(RequestSubCategory requestSubCategory);

    @Mapping(target = "value", source = "requestPurchaser.id.purchaserId")
    @Mapping(target = "name", source = "requestPurchaser.purchaser.loginId")
    @Mapping(target = "label", source = "requestPurchaser.purchaser.purchaserName")
    OptionDto toPurchaserOptionDto(RequestPurchaser requestPurchaser);

    @Mapping(target = "value", expression = "java(requestProject.getProjectCode().trim())")
    @Mapping(target = "name", expression = "java(requestProject.getProjectName().trim())")
    @Mapping(target = "label", source = "requestProject.project", qualifiedByName = "mapProjectLabel")
    OptionDto toProjectOptionDto(RequestProject requestProject);

    @Mapping(target = "value", source = "requestType.id.typeId")
    @Mapping(target = "name", source = "requestType.typeCode")
    @Mapping(target = "label", source = "requestType.typeName")
    OptionDto toTypeOptionDto(RequestType requestType);

//    @Mapping(target = "value", expression = "java(requestDepartment.getId().getDepartmentId().toString())")
//    @Mapping(target = "name", expression = "java(requestDepartment.getDepartmentName().trim())")
//    @Mapping(target = "label", source = "requestDepartment.department", qualifiedByName = "mapDepartmentLabel")
    @Mapping(target = "value", source = "requestDepartment.id.departmentId")
    @Mapping(target = "name", source = "requestDepartment.departmentCode")
    @Mapping(target = "label", source = "requestDepartment.department", qualifiedByName = "mapDepartmentLabel")
    OptionDto toDepartmentOptionDto(RequestDepartment requestDepartment);

    @Mapping(target = "value", source = "requestBudgetRefNo.id.budgetRefNoId")
    @Mapping(target = "name", source = "requestBudgetRefNo.budgetRefNoCode")
    @Mapping(target = "label", source = "requestBudgetRefNo.budgetRefNoName")
    OptionDto toBudgetRefNoOptionDto(RequestBudgetRefNo requestBudgetRefNo);

    @Mapping(target = "value", source = "requestLocation.id.locationId")
    @Mapping(target = "name", source = "requestLocation.deliveryLocation")
    @Mapping(target = "label", source = "requestLocation.contactName")
    OptionDto toLocationOptionDto(RequestLocation requestLocation);

    @Mapping(target = "value", source = "purpose.recId")
    @Mapping(target = "name", source = "purposeCode")
    @Mapping(target = "label", source = "purposeName")
    OptionDto toPurposeOptionDto(RequestItemPurpose requestItemPurpose);

    @Mapping(target = "value", source = "category.id")
    @Mapping(target = "name", source = "categoryCode")
    @Mapping(target = "label", source = "categoryName")
    OptionDto toCategoryOptionDto(RequestItemCategory requestItemCategory);

    @Mapping(target = "value", source = "subCategory.id")
    @Mapping(target = "name", source = "subCategoryCode")
    @Mapping(target = "label", source = "subCategoryName")
    OptionDto toSubCategoryOptionDto(RequestItemSubCategory requestItemSubCategory);

    @Mapping(target = "value", source = "currency.recId")
    @Mapping(target = "name", source = "currencyCode")
    @Mapping(target = "label", source = "currencyName")
    OptionDto toCurrencyOptionDto(RequestItemCurrency requestItemCurrency);

    @Mapping(target = "value", source = "recId")
    @Mapping(target = "name", source = "code")
    @Mapping(target = "label", source = "name")
    OptionDto toCurrencyOptionDto(Currency currency);

    @Mapping(target = "value", source = "recId")
    @Mapping(target = "name", source = "code")
    @Mapping(target = "label", source = "name")
    OptionDto toUnitOptionDto(Unit unit);

    @Mapping(target = "value", source = "supplier.recId")
    @Mapping(target = "name", source = "supplierShortName")
    @Mapping(target = "label", source = "supplierFullName")
    OptionDto toSupplierOptionDto(ExistingPriceItemSupplier existingPriceItemSupplier);

    default OptionDto toVatTypeOptionDto(Integer vatTypeId) {
        try {
            OptionDto optionDto = new OptionDto();
            optionDto.setValue(vatTypeId.toString());
            optionDto.setName(VatType.getDescriptionByValue(vatTypeId));
            optionDto.setLabel(VatType.getDescriptionByValue(vatTypeId));
            return optionDto;
        } catch (Exception ex) {
            return null;
        }
    }

    @Named(value = "mapProjectLabel")
    default String mapProjectLabel(Project project) {
        return project.getCode() + "-" + project.getName();
    }

    @Named(value = "mapDepartmentLabel")
    default String mapDepartmentLabel(Department department) {
        return department.getCode().length() > 1 ? department.getCode() + "-" + department.getName() : department.getName();
    }

}
