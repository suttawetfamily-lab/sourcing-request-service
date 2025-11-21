package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.ProjectDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Project;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface ProjectMapper {

    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    ProjectDto toProjectDto(Project project);

    @Mapping(target = "value", expression = "java(project.getCode().trim())")
    @Mapping(target = "name", expression = "java(project.getName().trim())")
    @Mapping(target = "isDefault", expression = "java(project.isDefault())")
//    @Mapping( target = "label", expression = "java(project.getCode().trim())")
    @Mapping(target = "label", source = ".", qualifiedByName = "mapLabel")
    OptionDto toProjectOptionDto(Project project);

    List<OptionDto> toProjectOptionDto(List<Project> projects);

    default ProjectDto toProjectDto(Project project, String timeZone) {
        ProjectDto projectDto = toProjectDto(project);
        if (projectDto != null) {
            projectDto.setCreatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(projectDto.getCreatedDate(), timeZone));
            projectDto.setUpdatedDate(
                    DateTimeUtil.convertTimestampByUserTimeZone(projectDto.getUpdatedDate(), timeZone));
        }
        return projectDto;
    }

    default List<ProjectDto> toProjectDtoList(List<Project> projectList, String timeZone) {
        List<ProjectDto> projectDtoList = new ArrayList();
        if (projectList != null) {
            projectList.forEach(i -> projectDtoList.add(toProjectDto(i, timeZone)));
        }
        return projectDtoList;
    }

    @Named(value = "mapLabel")
    default String mapLabel(Project project) {
        return project.getCode() + "-" + project.getName();
    }
}
