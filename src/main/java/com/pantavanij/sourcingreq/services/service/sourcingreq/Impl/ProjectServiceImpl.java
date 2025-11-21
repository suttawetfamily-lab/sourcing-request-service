package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.AdminClient;
import com.pantavanij.sourcingreq.services.client.BifrostClient;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.mapper.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.domain.response.admin.AdminResponse;
import com.pantavanij.sourcingreq.services.enums.*;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.*;

import javax.persistence.criteria.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.ProjectSearchType.*;
import static com.pantavanij.sourcingreq.services.util.Constant.REC_ID;
import static com.pantavanij.sourcingreq.services.util.Constant.TENANT;

@RequiredArgsConstructor
@Service
public class ProjectServiceImpl implements ProjectService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final BifrostClient bifrostClient;
    private final FileUtil fileUtil;
    private final ProjectRepository projectRepository;
    private final ProjectChangeLogHeaderRepository projectChangeLogHeaderRepository;
    private final ProjectChangeLogDetailRepository projectChangeLogDetailRepository;
    private final TenantService tenantService;
    private final UaaService uaaService;
    private final TenantConfigRepository tenantConfigRepository;

    @Value("${bifrost.service.username}")
    private String userName;
    @Value("${bifrost.service.password}")
    private String password;

    @Override
    public List<ProjectDto> getProjectBySearchTermV1(Integer tenantId, String searchTerm) {
        String topic = "Configuration";
        String section = "SearchProject";
        String name = "Limit";
        String limit = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        List<Project> projectList = projectRepository.getProjectByTenantIdAndSearchTerm(tenantId, searchTerm.replace("-", "").trim(), Integer.valueOf(limit));
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        return ProjectMapper.INSTANCE.toProjectDtoList(projectList, timeZone);
    }

    @Override
    public ProjectDto getProjectByProjectCode(String projectCode, Integer tenantId) {
        Project project = projectRepository.getProjectByProjectCode(projectCode, tenantId);
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);

        return ProjectMapper.INSTANCE.toProjectDto(project, timeZone);
    }

    @Override
    public OptionDto getProjectOptionByProjectCode(String projectCode, Integer tenantId) {
        Project project = projectRepository.getProjectByProjectCode(projectCode, tenantId);
        return ProjectMapper.INSTANCE.toProjectOptionDto(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveProjectBySchedule(String uri, Tenant tenant) {
        List<Project> projectList = projectRepository.findAll();
        ProjectActiveDto projectActiveAllList = bifrostClient.getProjectActiveAllList(uri);
        int sequence = 1;
        for (ProjectActiveDataDto data : projectActiveAllList.getData()) {
            boolean  isProjectCodeMatch = projectList.stream().anyMatch(p -> p.getCode().equalsIgnoreCase(data.getCode()));
            if (!isProjectCodeMatch) {
                Project project = new Project();
                project.setTenant(tenant);
                project.setCode(data.getCode());
                project.setName(data.getName());
                project.setSequence(sequence);
                project.setDefault(false);
                project.setActive(true);
                project.setCreatedBy("Schedule");
                project.setCreatedDate(DateTimeUtil.getTimestampUTC());
                projectList.add(project);
            } else {
                boolean  isProjectNameMatch = projectList.stream().anyMatch(p -> p.getCode().equals(data.getCode()));
                if(!isProjectNameMatch) {
                    Project project = new Project();
                    project.setName(data.getName());
                    project.setSequence(sequence);
                    project.setDefault(false);
                    project.setActive(true);
                    project.setUpdatedBy("Schedule");
                    project.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                    projectList.add(project);
                }
            }
            sequence++;
        }
        projectRepository.saveAll(projectList);
    }

    @Override
    public List<OptionDto> getProjectBySearchTerm(Integer tenantId, String searchTerm) {
//        if (tenantId == 2) {
//            List<OptionDto> optionDtos = new ArrayList<>();
//            AdminResponse response = adminClient.getProjectList();
//            if (null != response) {
//                optionDtos = response.getData().stream()
//                        .filter(p -> p.getProjectCode().toLowerCase().contains(searchTerm.toLowerCase()) || p.getProjectName().toLowerCase().contains(searchTerm.toLowerCase()))
//                        .map(i -> {
//                    return OptionDto.builder()
//                            .value(i.getProjectCode())
//                            .name(i.getProjectName())
//                            .label(i.getProjectCode() + " - " + i.getProjectName())
//                            .build();
//                }).collect(Collectors.toList());
//            }
//            return optionDtos;
//        } else {
            String topic = "Configuration";
            String section = "SearchProject";
            String name = "Limit";
            String limit = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
            List<Project> projectList = projectRepository.getProjectByTenantIdAndSearchTerm(tenantId, searchTerm.replace("-", "").trim(), Integer.valueOf(limit));
            return ProjectMapper.INSTANCE.toProjectOptionDto(projectList);
//        }
    }

    @Override
    public ProjectSearchDto searchProjectByCondition(ProjectSearchRequest request, Pageable pageable) {
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);

        Page<Project> projectPage = projectRepository.findAll(Specification.where(getSearchSpecification(request)), pageable);
        int totalPage = projectPage.getTotalPages();
        long total = projectPage.getTotalElements();
        List<ProjectDto> projectDtoList = RequestMapper.INSTANCE.toProjectDtoList(projectPage.getContent(), timeZone);

        ProjectSearchDto projectSearchDto = new ProjectSearchDto();
        projectSearchDto.setProjectList(projectDtoList);
        projectSearchDto.setTotal(total);
        projectSearchDto.setTotalPage(totalPage);
        projectSearchDto.setPageSize(pageable.getPageSize());
        return projectSearchDto;
    }

    @Override
    public UploadProjectFileResponse validateProjectFileUpload(MultipartFile file) throws IOException {
        List<UploadMessageResponse> messageResponsesList = new ArrayList<>();
        List<Project> projectList = new ArrayList<>();
        UploadProjectFileResponse uploadProjectFileResponses = new UploadProjectFileResponse();

        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheet = workbook.getSheetAt(0);

        boolean isExcelFile = "application/vnd.ms-excel".equals(file.getContentType()) ||
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(file.getContentType());
        if (!isExcelFile) {
            return throwMessageResponse(ApiMessage.E7048);
        }

        if (sheet == null) {
            return throwMessageResponse(ApiMessage.E7054);
        }

        int rowCount = 0;
        boolean isFirstRow = true;
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

        for (Row row : sheet) {
            // Skip header
            if (isFirstRow) {
                isFirstRow = false;
                continue;
            }

            boolean hasData = false;
            for (Cell cell : row) {
                CellValue cellValue = evaluator.evaluate(cell);
                if (cellValue != null) {
                    switch (cellValue.getCellTypeEnum()) {
                        case NUMERIC:
                        case BOOLEAN:
                            hasData = true;
                            break;
                        case STRING:
                            if (!cellValue.getStringValue().trim().isEmpty()) {
                                hasData = true;
                            }
                            break;
                    }
                    if (hasData) break;
                }
            }

            if (hasData) {
                rowCount++;
            }
        }

        if (rowCount == 0) {
            return throwMessageResponse(ApiMessage.E7054);
        }

        int lastCellIndex = sheet.getRow(0).getLastCellNum();
        for (int columnIndex = 0; columnIndex < lastCellIndex; columnIndex++) {
            Row rowHeader = sheet.getRow(0);
            Cell cellHeader = rowHeader.getCell(columnIndex);
            String columnHeader =  getExcelFieldData(cellHeader);

            for (int rowIndex = 0; rowIndex <= rowCount; rowIndex++) {
                // Skip first header.
                Row row = sheet.getRow(rowIndex);
                if (row.getRowNum() == 0) {
                    continue;
                }

                UploadMessageResponse messageResponse = new UploadMessageResponse();
                Cell cell = row.getCell(columnIndex);
                switch (columnHeader.trim()) {
                    case "Project Code":
                    case "Project Name":
                        if (checkIsEmptyExcelStringAndNumber(cell)) {
                            messageResponse = createMessageResponse(messageResponse, rowIndex, columnIndex, ApiMessage.E7052, null, columnHeader.trim());
                        }
                        break;
                    default:
                        break;
                }

                if (messageResponse.getCode() != null) {
                    messageResponsesList.add(messageResponse);
                }
            }
        }

        if (!messageResponsesList.isEmpty()) {
            messageResponsesList.sort(Comparator.comparingInt(UploadMessageResponse::getRow)
                    .thenComparing(UploadMessageResponse::getColumn, Comparator.nullsFirst(Comparator.naturalOrder())));

            uploadProjectFileResponses.setValid(false);
            uploadProjectFileResponses.setMessageResponses(messageResponsesList);

        } else {
            for (Row row : sheet) {
                // Skip header
                if (row.getRowNum() > 0) {
                    Cell projectCodeCell = row.getCell(0);
                    Cell projectNameCell = row.getCell(1);
                    Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
                    Project project = Project.builder()
                            .tenant(tenant)
                            .code(getExcelFieldData(projectCodeCell))
                            .name(getExcelFieldData(projectNameCell))
                            .sequence(null)
                            .isDefault(false)
                            .active(true)
                            .createdBy(AppUtil.getUserName())
                            .createdDate(DateTimeUtil.getTimestampUTC())
                            .updatedBy(AppUtil.getUserName())
                            .updatedDate(DateTimeUtil.getTimestampUTC())
                            .build();
                    projectList.add(project);
                }
            }
            uploadProjectFileResponses.setValid(true);
            uploadProjectFileResponses.setProjectList(projectList);
        }
        return uploadProjectFileResponses;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    public ProjectChangeLogHeaderDto saveProjectData(List<Project> newProjectList, MultipartFile file) {
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        List<Project> projectListSave = new ArrayList<>();


        try {
            List<Project> projectList = projectRepository.findByTenant(tenant);
            newProjectList.sort(Comparator.comparing(Project::getCode)
                    .thenComparing(Project::getName));

            projectList.sort(Comparator.comparing(Project::getCode)
                    .thenComparing(Project::getName));

            List<String> checkedCodes = new ArrayList<>();

            // Prevent duplicate project.
            // Use Linear or Binary search based on data size
            for (Project newProject : newProjectList) {
                if (checkedCodes.contains(newProject.getCode())) {
                    continue;
                }
                checkedCodes.add(newProject.getCode());

                if (projectList.size() < 10000) {
                    // Linear search for small datasets
                    Optional<Project> exists = projectList.stream()
                            .filter(p -> p.getCode().equals(newProject.getCode())).findFirst();
                    if (!exists.isPresent()) {
                        projectListSave.add(newProject);
                    } else {
                        Project updatedProject = exists.get();
                        updatedProject.setName(newProject.getName());
                        projectListSave.add(updatedProject);
                    }
                } else {
                    // Binary search for large datasets
                    int index = Collections.binarySearch(projectList, newProject, Comparator.comparing(Project::getCode));
                    if (index < 0) {
                        projectListSave.add(newProject);
                    } else {
                        Project updatedProject = projectList.get(index);
                        updatedProject.setName(newProject.getName());
                        projectListSave.add(updatedProject);
                    }
                }
            }

            projectChangeLogDetailRepository.deleteByTenantId(tenant.getRecId());
            ProjectChangeLogHeader projectChangeLogHeader = projectChangeLogHeaderRepository.save(getProjectChangeLogHeader(file));
            List<ProjectChangeLogDetail> projectChangeLogDetailList = new ArrayList<>();
            checkedCodes = new ArrayList<>();
            for (Project project : newProjectList) {
                if (checkedCodes.contains(project.getCode())) {
                    continue;
                }
                checkedCodes.add(project.getCode());
                ProjectChangeLogDetail projectChangeLogDetail = getProjectChangeLogDetail(project, projectChangeLogHeader);
                projectChangeLogDetailList.add(projectChangeLogDetail);
            }
            List<ProjectChangeLogDetail> saveChangeLogDetailList = projectChangeLogDetailRepository.saveAll(projectChangeLogDetailList);

            if (!projectListSave.isEmpty()) {
                //Optional<Project> projectOptional = projectRepository.findFirstByTenantRecIdOrderBySequenceDesc(tenant.getRecId());
                //int sequence = projectOptional.map(Project::getSequence).orElse(0);0
                int sequence = 0;

                        // In-active all previous-projects if not in ChangeLodDetail.
                List<Project> existingProjects = projectRepository.findByTenant(tenant);
                for (Project existingProject: existingProjects) {
                    boolean exists = saveChangeLogDetailList.stream()
                            .anyMatch(changeLogDetail -> changeLogDetail.getCode().equals(existingProject.getCode()));
                    existingProject.setActive(exists);
                }
                projectRepository.saveAll(existingProjects);

                // Active new-project and increase sequence.
                for (Project project : projectListSave) {
                    project.setActive(true);
                    project.setSequence(++sequence);
                }
                projectRepository.saveAll(projectListSave);
            }

            final String FOLDER_NAME = "master-data";
            String customFolderName = String.format("%s/%s/project", FOLDER_NAME, tenant.getCode());
            fileUtil.uploadFileByte(file.getBytes(), projectChangeLogHeader.getFileId(), customFolderName);

            return RequestMapper.INSTANCE.toProjectChangeLogHeaderDto(projectChangeLogHeader, timeZone);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ProjectChangeLogHeaderSearchDto searchProjectChangeLogHeaderByCondition(ProjectChangeLogHeaderSearchRequest request, Pageable pageable) {
        Page<ProjectChangeLogHeader> projectChangeLogHeaderPage = projectChangeLogHeaderRepository.findAll(Specification.where(getProjectChangeLogHeaderSearchSpecification(request)), pageable);
        int totalPage = projectChangeLogHeaderPage.getTotalPages();
        long total = projectChangeLogHeaderPage.getTotalElements();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        List<ProjectChangeLogHeaderDto> projectChangeLogHeaderDtoList = RequestMapper.INSTANCE.toProjectChangeLogHeaderDtoList(projectChangeLogHeaderPage.getContent(), timeZone);

        ProjectChangeLogHeaderSearchDto projectChangeLogHeaderSearchDto = new ProjectChangeLogHeaderSearchDto();
        projectChangeLogHeaderSearchDto.setProjectChangeLogHeaderDtoList(projectChangeLogHeaderDtoList);
        projectChangeLogHeaderSearchDto.setTotal(total);
        projectChangeLogHeaderSearchDto.setTotalPage(totalPage);
        projectChangeLogHeaderSearchDto.setPageSize(pageable.getPageSize());
        return projectChangeLogHeaderSearchDto;
    }

    @Override
    public ByteArrayResource downloadProjectTemplate() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Project");

        XSSFRow row = sheet.createRow(0);
        XSSFCell cellCode = row.createCell(0);
        cellCode.setCellValue("Project Code");
        cellCode.setCellStyle(headerStyle(workbook));
        cellCode.setCellType(CellType.STRING);
        sheet.autoSizeColumn(0);

        XSSFCell cellName = row.createCell(1);
        cellName.setCellValue("Project Name");
        cellName.setCellStyle(headerStyle(workbook));
        cellCode.setCellType(CellType.STRING);
        sheet.autoSizeColumn(1);

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            return new ByteArrayResource(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Integer deleteProjectByRecId(Integer recId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Project project = projectRepository.findProjectByRecId(recId);
        if (project != null && project.getRecId() != null) {
            projectRepository.deleteByProjectRecIdAndTenantId(recId, tenant.getRecId());
            return recId;
        }
        return null;
    }

    @Override
    public ProjectDto updateProject(ProjectRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Project existingProject = projectRepository.findProjectByRecId(request.getRecId());
        if (existingProject != null && existingProject.getRecId() != null) {
            projectRepository.reOrderOtherProjectSequences(tenant.getRecId(), existingProject.getSequence(), request.getSequence());
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            Project projectUpdate = getUpdateProject(existingProject, request);
            projectRepository.save(projectUpdate);
            return RequestMapper.INSTANCE.toProjectDto(projectUpdate, timeZone);
        }
        return null;
    }

    public Project getUpdateProject(Project project, ProjectRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
       return Project.builder()
               .recId(project.getRecId())
               .tenant(tenant)
               .code(request.getCode())
               .name(request.getName())
               .sequence(request.getSequence())
               .isDefault(request.isDefault())
               .active(request.isActive())
               .createdBy(project.getCreatedBy())
               .createdDate(project.getCreatedDate())
               .updatedBy(AppUtil.getUserName())
               .updatedDate(DateTimeUtil.getTimestampUTC())
               .build();
    }

    @Override
    public ProjectDto updateProjectSequence(SequenceRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        Project existingProject = projectRepository.findProjectByRecId(request.getRecId());
        if (existingProject != null && existingProject.getRecId() != null) {
            projectRepository.reOrderOtherProjectSequences(tenant.getRecId(), existingProject.getSequence(), request.getSequence());
            Project projectUpdate = projectRepository.save(getUpdateProjectSequence(existingProject, request));
            return RequestMapper.INSTANCE.toProjectDto(projectUpdate, timeZone);
        }
        return null;
    }

    public Project getUpdateProjectSequence(Project project, SequenceRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        return Project.builder()
                .recId(project.getRecId())
                .tenant(tenant)
                .code(project.getCode())
                .name(project.getName())
                .sequence(request.getSequence())
                .isDefault(project.isDefault())
                .active(project.isActive())
                .createdBy(project.getCreatedBy())
                .createdDate(project.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }

    private Specification<ProjectChangeLogHeader> getProjectChangeLogHeaderSearchSpecification(ProjectChangeLogHeaderSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
            if (tenant != null) {
                predicates.add(criteriaBuilder.equal(root.get(TENANT).get(REC_ID), tenant.getRecId()));
            }

            List<ConditionSearchRequest> conditionSearchRequests = request.getConditionSearchList();
            if (conditionSearchRequests != null && !conditionSearchRequests.isEmpty()) {
                for (ConditionSearchRequest conditionSearchRequest : conditionSearchRequests) {
                    String searchField = conditionSearchRequest.getSearchField();
                    String searchValue = conditionSearchRequest.getSearchValue();

                    if (StringUtils.isNotEmpty(searchField) && StringUtils.isNotEmpty(searchValue)) {
                        if (Constant.FILE_NAME.equalsIgnoreCase(searchField)) {
                            predicates.add(criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%"));
                        } else if (Constant.CREATED_BY.equalsIgnoreCase(searchField)) {
                            predicates.add(criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%"));
                        } else {
                            if (CommonUtils.isNumeric(searchValue)) {
                                predicates.add(criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%"));
                            } else {
                                predicates.add(criteriaBuilder.like(root.get(searchField), "%" + searchValue.toLowerCase() + "%"));
                            }
                        }
                    }
                }
            }

            query.distinct(true);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }


    public ProjectChangeLogHeader getProjectChangeLogHeader(MultipartFile file) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        return ProjectChangeLogHeader.builder()
                .tenant(tenant)
                .fileId(CommonUtils.generateUUID()+"."+FileUtil.getFileExtension(file.getOriginalFilename()))
                .fileName(FileUtil.getFileName(file))
                .fileSize((int)FileUtil.getFileSizeInBytes(file))
                .createdBy(AppUtil.getUserName())
                .createdDate(DateTimeUtil.getTimestampUTC())
                .build();
    }

    public ProjectChangeLogDetail getProjectChangeLogDetail(Project project, ProjectChangeLogHeader projectChangeLogHeader) {
        return ProjectChangeLogDetail.builder()
                .projectChangeLogHeader(projectChangeLogHeader)
                .code(project.getCode())
                .name(project.getName())
                .createdBy(AppUtil.getUserName())
                .createdDate(DateTimeUtil.getTimestampUTC())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }


    public String getExcelFieldData (Cell cell) {
        switch (cell.getCellTypeEnum()) {
            case STRING:
                return cell.getStringCellValue().trim();

            case NUMERIC:
                return String.valueOf((int)cell.getNumericCellValue());
        }
        return null;
    }

    public boolean checkIsEmptyExcelStringAndNumber(Cell cell) {
        if (cell == null) {
            return true;
        }

        switch (cell.getCellTypeEnum()) {
            case STRING:
                String valueString = cell.getStringCellValue().trim();
                if (StringUtils.isEmpty(valueString)) {
                    return true;
                }
                break;

            case NUMERIC:
                Double valueNumber = cell.getNumericCellValue();
                if (valueNumber == null) {
                    return true;
                }
                break;
        }
        return false;
    }

    public UploadProjectFileResponse throwMessageResponse(ApiMessage apiMessage) {
        UploadProjectFileResponse uploadProjectFileResponse = new UploadProjectFileResponse();
        List<UploadMessageResponse> messageResponses = new ArrayList<>();
        UploadMessageResponse messageResponse = new UploadMessageResponse();
        createMessageResponse(messageResponse, null, null, apiMessage, null, null);
        messageResponses.add(messageResponse);
        uploadProjectFileResponse.setMessageResponses(messageResponses);
        uploadProjectFileResponse.setValid(false);
        return uploadProjectFileResponse;
    }

    public UploadMessageResponse createMessageResponse(UploadMessageResponse messageResponse, Integer rowNum, Integer columnNum, ApiMessage apiMessage, String fieldName, String displayColumn) {
        messageResponse.setCode(apiMessage);
        messageResponse.setRow(rowNum);
        messageResponse.setColumn(null != displayColumn ? StringUtils.capitalize(displayColumn) : null);
        if (fieldName != null) {
            messageResponse.setDescription(String.format(ApiMessage.E7031.description(), fieldName));
        } else {
            messageResponse.setDescription(apiMessage.description());
        }
        return messageResponse;
    }

    private Specification<Project> getSearchSpecification(ProjectSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
            if (tenant != null) {
                predicates.add(criteriaBuilder.equal(root.get(TENANT).get(REC_ID), tenant.getRecId()));
            }

            Root<ProjectChangeLogDetail> projectChangeLogDetailRoot = query.from(ProjectChangeLogDetail.class);
            Predicate codePredicate = criteriaBuilder.equal(projectChangeLogDetailRoot.get(Constant.CODE), root.get(Constant.CODE));
            predicates.add(codePredicate);

            List<ConditionSearchRequest> conditionSearchRequests = request.getConditionSearchList();
            if (conditionSearchRequests != null && !conditionSearchRequests.isEmpty()) {
                List<Predicate> orPredicates = new ArrayList<>();
                for (ConditionSearchRequest conditionSearchRequest : conditionSearchRequests) {
                    String searchField = conditionSearchRequest.getSearchField();
                    String searchValue = conditionSearchRequest.getSearchValue();

                    Predicate predicate = null;
                    if (StringUtils.isNotEmpty(searchField) && StringUtils.isNotEmpty(searchValue)) {
                        if (NAME.description().equalsIgnoreCase(searchField)) {
                            predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%");
                        } else if (CODE.description().equalsIgnoreCase(searchField)) {
                            predicate= criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%");
                        } else if (CREATED_BY.description().equalsIgnoreCase(searchField)) {
                            predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%");
                        } else {
                            if (CommonUtils.isNumeric(searchValue)) {
                                predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%");
                            } else {
                                predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue.toLowerCase() + "%");
                            }
                        }

                        if (predicate != null) {
                            orPredicates.add(predicate);
                        }
                    }
                }
                if (!orPredicates.isEmpty()) {
                    predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
                }
            }

            query.distinct(true);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public ProjectDto getProjectByRecId(Integer projectId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        Project project = projectRepository.findProjectByRecId(projectId);
        if (project != null && project.getRecId() != null) {
            return RequestMapper.INSTANCE.toProjectSingleDto(project, timeZone);
        }
        return null;
    }

    private CellStyle headerStyle(XSSFWorkbook workbook) {
        XSSFFont font = workbook.createFont();
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setFont(font);
        return headerStyle;
    }
}