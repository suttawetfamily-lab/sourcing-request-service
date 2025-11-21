package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.*;
import java.util.stream.*;

@Mapper
public interface TenantSectionDetailMapper {

    TenantSectionDetailMapper INSTANCE = Mappers.getMapper(TenantSectionDetailMapper.class);

    TenantSectionDetailDto toTenantSectionDetailDto(TenantSectionDetail tenantSectionDetail);

    List<TenantSectionDetailDto> toTenantSectionDetailDto(List<TenantSectionDetail> tenantSectionDetailList);

    List<TenantSectionDetailDto> toTenantSectionDetailDtoList(List<TenantSectionDetailDto> tenantSectionDetailDtoList);

    default List<TenantSectionDetailDto> toTenantSectionDetailDtoList(List<TenantSectionDetailDto> tenantSectionDetailDtoList, List<TenantSectionDetail> tenantSectionDetailList, String timeZone) {
        List<TenantSectionDetailDto> tenantSectionDetailDtoList_ = toTenantSectionDetailDtoList(tenantSectionDetailDtoList);
        if (!tenantSectionDetailDtoList_.isEmpty()) {
            tenantSectionDetailDtoList_ = tenantSectionDetailDtoList_.stream()
                .peek(tenantSectionDetailDto -> {
                    if (!tenantSectionDetailDto.getTenantSectionDetailDataSourceList().isEmpty()) {
                        tenantSectionDetailDto.setDataSource(tenantSectionDetailDto.getTenantSectionDetailDataSourceList().get(0).getDataSource());
                    }
                    if (!tenantSectionDetailDto.getTenantSectionDetailDependencyList().isEmpty()) {
                        List<DependencyObjectDto> dependencyObjectDtoList = tenantSectionDetailDto.getTenantSectionDetailDependencyList().stream()
                            .map(dependencyDto -> {
                                String[] values = dependencyDto.getValue().split(",");
                                String copyName = dependencyDto.getName();
                                String label = copyName.substring(0, 1).toUpperCase() + copyName.substring(1);

                                String nameToFind = dependencyDto.getName();
                                Optional<TenantSectionDetail> matchingResultOpt = tenantSectionDetailList.stream()
                                        .filter(detail -> detail.getFieldName().equals(nameToFind))
                                        .findFirst();

                                return DependencyObjectDto.builder()
                                    .name(dependencyDto.getName())
                                    .nameObj(NameObjectDto.builder()
                                            .label(label)
                                            .name(dependencyDto.getName())
                                            .value(matchingResultOpt.isPresent() ? matchingResultOpt.get().getId(): 0)
                                        .build())
                                    .values(values)
                                    .groupName(dependencyDto.getGroupName())
                                    .action(dependencyDto.getAction())
                                    .build();
                            })
                            .collect(Collectors.toList());
                        tenantSectionDetailDto.setDependencyList(dependencyObjectDtoList);
                    }
                    if (!tenantSectionDetailDto.getTenantSectionDetailValidatorList().isEmpty()) {
                        List<ValidatorDto> validatorDtoList = tenantSectionDetailDto.getTenantSectionDetailValidatorList().stream()
                            .map(validatorDto -> ValidatorDto.builder()
                                .recId(validatorDto.getValidator().getRecId())
                                .name(validatorDto.getName())
                                .value(validatorDto.getValue())
                                .build())
                            .collect(Collectors.toList());
                        tenantSectionDetailDto.setValidatorList(validatorDtoList);
                    }
                    if (!tenantSectionDetailDto.getTenantSectionDetailWatchList().isEmpty()) {
                        List<WatchDto> watchDtoList = tenantSectionDetailDto.getTenantSectionDetailWatchList().stream()
                            .map(watchDto -> {
                                String copyFieldName = watchDto.getFieldName();
                                String label = copyFieldName.substring(0, 1).toUpperCase() + copyFieldName.substring(1);

                                String fieldNameToFind = watchDto.getFieldName();
                                Optional<TenantSectionDetail> matchingResultOpt = tenantSectionDetailList.stream()
                                        .filter(detail -> detail.getFieldName().equals(fieldNameToFind))
                                        .findFirst();

                                return WatchDto.builder()
                                    .fieldName(watchDto.getFieldName())
                                    .nameObj(NameObjectDto.builder()
                                        .label(label)
                                        .name(watchDto.getFieldName())
                                        .value(matchingResultOpt.isPresent() ? matchingResultOpt.get().getId(): 0)
                                        .build())
                                    .originalFieldName(watchDto.getOriginalFieldName())
                                    .updatedFieldName(watchDto.getUpdatedFieldName())
                                    .groupName(watchDto.getGroupName())
                                    .values(watchDto.getValues())
                                    .build();
                            })
                            .collect(Collectors.toList());
                        tenantSectionDetailDto.setWatchList(watchDtoList);
                    }
                    if (tenantSectionDetailDto.getTenantSectionDetailDataSourceList() != null
                            && !tenantSectionDetailDto.getTenantSectionDetailDataSourceList().isEmpty()) {
                        List<TenantSectionDetailDataSourceDto> tenantSectionDetailDataSourceList = tenantSectionDetailDto.getTenantSectionDetailDataSourceList().stream()
                            .peek(detail -> {
                                if (detail.getDataSource() != null) {
                                    DataSourceDto updatedDataSource = detail.getDataSource();
                                    String copyPath = updatedDataSource.getPath();
                                    copyPath = copyPath.substring(copyPath.lastIndexOf("/") + 1);
                                    String name = copyPath;
                                    copyPath = copyPath.substring(0, 1).toUpperCase() + copyPath.substring(1);
                                    String label = updatedDataSource.getMethod()+ "-" + copyPath;

                                    updatedDataSource.setLabel(label);
                                    updatedDataSource.setName(name);
                                    updatedDataSource.setValue(detail.getDataSource().getRecId());
                                    detail.setDataSource(updatedDataSource);
                                }
                            })
                            .collect(Collectors.toList());
                        tenantSectionDetailDto.setTenantSectionDetailDataSourceList(tenantSectionDetailDataSourceList);
                    }
                })
                .collect(Collectors.toList());
        }
        return tenantSectionDetailDtoList_;
    }

    default TenantSectionDetailFieldNameDto mapTenantSectionDetailToDto(TenantSectionDetail tenantSectionDetail) {
        if (tenantSectionDetail == null) {
            return null;
        }
        TenantSectionDetailFieldNameDto dto = new TenantSectionDetailFieldNameDto();
        dto.setId(tenantSectionDetail.getId());
        dto.setName(tenantSectionDetail.getFieldName());
        dto.setFieldName(tenantSectionDetail.getFieldName());
        dto.setValue(tenantSectionDetail.getId());
        dto.setLabel(tenantSectionDetail.getLabel());
        return dto;
    }

    default TenantSectionDetailFieldNameDto toTenantSectionDetailFieldNameDto(TenantSectionDetail tenantSectionDetail) {
        return mapTenantSectionDetailToDto(tenantSectionDetail);
    }
}
