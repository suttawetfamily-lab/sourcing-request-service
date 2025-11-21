package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.constraint.SearchTermConstraint;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.*;
import com.pantavanij.sourcingreq.services.interceptor.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.*;
import org.springframework.core.io.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;

import javax.validation.*;
import java.io.*;
import java.util.*;

import static com.pantavanij.sourcingreq.services.enums.ProjectSearchType.*;
import static com.pantavanij.sourcingreq.services.enums.SearchRequestType.SEQUENCE;


@RequiredArgsConstructor
@RestController
@RequestMapping("${api.endpoint.version}")
@Validated
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectChangeLogHeaderService projectChangeLogHeaderService;
    private final TenantService tenantService;

    @GetMapping(value = "/project", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<OptionDto>>> getProjectBySearchTerm(@RequestParam @SearchTermConstraint String searchTerm,@RequestParam(value = "currentProjectCode", required = false) String currentProjectCode) {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);

        List<OptionDto> projectOptions = projectService.getProjectBySearchTerm(tenant.getRecId(), searchTerm);
        if(currentProjectCode != null && !currentProjectCode.isBlank()) {
            OptionDto currentProjectOption = projectService.getProjectOptionByProjectCode(currentProjectCode, tenant.getRecId());
            if (currentProjectOption != null) {
                projectOptions.removeIf(opt ->
                        currentProjectCode.equalsIgnoreCase(opt.getValue())
                );
                projectOptions.add(0, currentProjectOption);
            }
        }

        return ResponseEntity.ok().body(new ApiResponse(projectOptions));
    }

    @PreAuthorize("hasAuthority('SMJ')")
    @GetMapping(value = "/project/view", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity viewProject(@RequestParam ("projectId") Integer projectId) {
        ProjectDto projectDto = projectService.getProjectByRecId(projectId);
        if (projectDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(projectDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SMJ')")
    @GetMapping(value = "/project/download-file")
    public ResponseEntity downloadProject(
            @RequestParam(value = "fileId") String fileId,
            @RequestParam(value = "fileName") String fileName
    ) {
        if (StringUtils.isEmpty(fileId) || StringUtils.isEmpty(fileName)) {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E1002, ApiMessage.E1002.description()), HttpStatus.BAD_REQUEST);
        }

        ByteArrayResource resource = projectChangeLogHeaderService.downloadProjectHistory(fileId);
        if (resource != null) {
            HttpHeaders header = new HttpHeaders();
            header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            header.setContentLength(resource.contentLength());
            return new ResponseEntity<>(resource, header, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasAuthority('SMJ')")
    @GetMapping(value = "/project/download-file-current")
    public ResponseEntity downloadFileCurrent() {
        ByteArrayResource resource = projectChangeLogHeaderService.downloadFileCurrent();
        if (resource != null) {
            HttpHeaders header = new HttpHeaders();
            header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=MasterDataProjectCurrent.xlsx");
            header.setContentLength(resource.contentLength());
            return new ResponseEntity<>(resource, header, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasAuthority('SMJ')")
    @GetMapping(value = "/project-download/template")
    public ResponseEntity downloadProjectTemplate() {
        ByteArrayResource resource = projectService.downloadProjectTemplate();
        if (resource != null) {
            HttpHeaders header = new HttpHeaders();
            header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Project-Template.xlsx");
            header.setContentLength(resource.contentLength());
            return new ResponseEntity<>(resource, header, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

//    @PostMapping(value = "/project", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> getProjectBySearchTermV1(@RequestParam @SearchTermConstraint String searchTerm) {
//
//        String tenantCode = AppUtil.getTenantId();
//        Tenant tenant = tenantService.findByCode(tenantCode);
//        List<ProjectDto> projectList = projectService.getProjectBySearchTermV1(tenant.getRecId(), searchTerm);
//        if (!projectList.isEmpty()) {
//            return new ResponseEntity<>(new ProjectResponse(projectList), HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7023, ApiMessage.E7023.description()), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @PreAuthorize("hasAuthority('SMJ')")
    @PostMapping(value = "/project/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchProject(@RequestBody @Valid ProjectSearchRequest request) {
        Sort sort;
        int page = request.getPage();
        int size = request.getPageSize();
        String sortBy = request.getSortBy();
        String sortOrder = request.getSortOrder();

        if (("".equals(sortBy) || sortBy == null) && ("".equals(sortOrder) || sortOrder == null)) {
            sort = Sort.by(NAME.description()).descending();
        } else {
            List<Sort.Order> orders = new ArrayList<>();
            assert sortBy != null;
            orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC: Sort.Direction.ASC, sortBy));
            if (!sortBy.equalsIgnoreCase("sequence")) {
                orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, SEQUENCE.description()));
            }
            sort = Sort.by(orders);
        }

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        ProjectSearchDto projectSearchDto = projectService.searchProjectByCondition(request, pageable);
        ProjectSearchResponse response = ProjectSearchResponse.builder()
                .status(new ApiResponseStatus())
                .pageSize(projectSearchDto.getPageSize())
                .page(page)
                .totalPage(projectSearchDto.getTotalPage())
                .total(projectSearchDto.getTotal())
                .data(projectSearchDto.getProjectList())
                .build();

        if (projectSearchDto.getProjectList() != null && !projectSearchDto.getProjectList().isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @ControllerExecuteTime
    @PreAuthorize("hasAuthority('SMJ')")
    @PostMapping(value = "/project/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity uploadProject(@RequestPart("file") MultipartFile file) throws IOException {
        UploadProjectFileResponse uploadProjectFileResponse = projectService.validateProjectFileUpload(file);
        if (!uploadProjectFileResponse.isValid()) {
            uploadProjectFileResponse.setStatus(new ApiResponseStatus(ApiMessage.E7053, ApiMessage.E7053.description()));
            return new ResponseEntity<>(uploadProjectFileResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        List<Project> projectList = uploadProjectFileResponse.getProjectList();
        ProjectChangeLogHeaderDto projectChangeLogHeaderDto = projectService.saveProjectData(projectList, file);
        if (projectChangeLogHeaderDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(projectChangeLogHeaderDto), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasAuthority('SMJ')")
    @PostMapping(value = "/project-upload/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getProjectUploadHistory(@RequestBody @Valid ProjectChangeLogHeaderSearchRequest request) {
        Sort sort;
        int page = request.getPage();
        int size = request.getPageSize();
        String sortBy = request.getSortBy();
        String sortOrder = request.getSortOrder();

        if (("".equals(sortBy) || sortBy == null) && ("".equals(sortOrder) || sortOrder == null)) {
            sort = Sort.by(NAME.description()).descending();
        } else {
            List<Sort.Order> orders = new ArrayList<>();
            assert sortBy != null;
            orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC: Sort.Direction.ASC, sortBy));
            sort = Sort.by(orders);
        }

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        ProjectChangeLogHeaderSearchDto projectChangeLogHeaderSearchDto = projectService.searchProjectChangeLogHeaderByCondition(request, pageable);
        ProjectChangeLogHeaderSearchResponse response = ProjectChangeLogHeaderSearchResponse.builder()
                .status(new ApiResponseStatus())
                .pageSize(projectChangeLogHeaderSearchDto.getPageSize())
                .page(page)
                .totalPage(projectChangeLogHeaderSearchDto.getTotalPage())
                .total(projectChangeLogHeaderSearchDto.getTotal())
                .data(projectChangeLogHeaderSearchDto.getProjectChangeLogHeaderDtoList())
                .build();

        if (projectChangeLogHeaderSearchDto.getProjectChangeLogHeaderDtoList() != null && !projectChangeLogHeaderSearchDto.getProjectChangeLogHeaderDtoList().isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SMJ')")
    @PutMapping(value = "/project/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateProject(@RequestBody @Valid ProjectRequest request) {
        ProjectDto projectDto = projectService.updateProject(request);
        if (projectDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(projectDto), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasAuthority('SMJ')")
    @PutMapping(value = "/project/update/sequence", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateProjectSequence(@RequestBody @Valid SequenceRequest request) {
        ProjectDto projectDto = projectService.updateProjectSequence(request);
        if (projectDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(projectDto), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasAuthority('SMJ')")
    @DeleteMapping(value = "/project-delete-file/{fileId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteProjectFile(@PathVariable("fileId") String fileId) {
        String projectChangeLogHeaderRecId = projectChangeLogHeaderService.deleteProjectChangeLogHeaderFile(fileId);
        if (projectChangeLogHeaderRecId != null) {
            return new ResponseEntity<>(new ApiResponse<>(projectChangeLogHeaderRecId), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasAuthority('SMJ')")
    @DeleteMapping(value = "/project/{recId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteProject(@PathVariable("recId") Integer recId) {
        Integer id = projectService.deleteProjectByRecId(recId);
        if (id != null) {
            return new ResponseEntity<>(new ApiResponse<>(id), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
