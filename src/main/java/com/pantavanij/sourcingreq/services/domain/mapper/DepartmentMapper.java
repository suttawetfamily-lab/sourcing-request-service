package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.DepartmentDto;
import com.pantavanij.sourcingreq.services.domain.dto.DepartmentOptionDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Department;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Project;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface DepartmentMapper {

    DepartmentMapper INSTANCE = Mappers.getMapper(DepartmentMapper.class);

    DepartmentDto toDepartmentDto(Department department);

//    @Mapping(target = "value", expression = "java(department.getCode().trim())")
//    @Mapping(target = "name", expression = "java(department.getName().trim())")
//    @Mapping( target = "label", source = ".", qualifiedByName = "mapLabel")
    @Mapping(target = "value", source = "department.recId")
    @Mapping(target = "name", source = "department.name")
    @Mapping(target = "code", source = "department.code")
    @Mapping(target = "label", source = ".", qualifiedByName = "mapLabel")
    DepartmentOptionDto toDepartmentOptionDto(Department department);

    List<DepartmentOptionDto> toDepartmentOptionDto(List<Department> departments);

    default DepartmentDto toDepartmentDto(Department department, String timeZone) {
        DepartmentDto departmentDto = toDepartmentDto(department);
        if (departmentDto != null) {
            convertTimeStampByTimeZone(departmentDto, timeZone);
        }
        return departmentDto;
    }

    default List<DepartmentDto> toDepartmentDtoList(List<Department> departmentList, String timeZone) {
        List<DepartmentDto> departmentDtoList = new ArrayList();
        if (departmentList != null) {
            departmentList.forEach(i -> departmentDtoList.add(toDepartmentDto(i, timeZone)));
        }
        return departmentDtoList;
    }

    default DepartmentDto convertTimeStampByTimeZone(DepartmentDto departmentDto, String timeZone) {
        if (departmentDto != null) {
            departmentDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(departmentDto.getCreatedDate(), timeZone));
            departmentDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(departmentDto.getUpdatedDate(), timeZone));
        }
        return departmentDto;
    }

    @Named(value = "mapLabel")
    default String mapLabel(Department department) {
        return department.getCode().length() > 1 ? department.getCode() + "-" + department.getName() : department.getName();
    }

}
