package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.*;
import org.springframework.core.io.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProjectChangeLogHeaderServiceImpl implements ProjectChangeLogHeaderService {

    private final FileUtil fileUtil;
    private final TenantService tenantService;
    private final ProjectChangeLogHeaderRepository projectChangeLogHeaderRepository;

    @Override
    public ByteArrayResource downloadProjectHistory(String fileId) {
        try {
            final String FOLDER_NAME = "master-data";
            Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
            String customFolderName = String.format("%s/%s/project", FOLDER_NAME, tenant.getCode());

            Optional<ProjectChangeLogHeader> projectChangeLogHeaderOptional = projectChangeLogHeaderRepository.findByFileId(fileId);
            if (projectChangeLogHeaderOptional.isPresent()) {
                return new ByteArrayResource(fileUtil.downloadFile(fileId, customFolderName));
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ByteArrayResource downloadFileCurrent() {
        try {
            final String FOLDER_NAME = "master-data";
            Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
            String customFolderName = String.format("%s/%s/project", FOLDER_NAME, tenant.getCode());

            Optional<ProjectChangeLogHeader> projectChangeLogHeaderOptional = projectChangeLogHeaderRepository.findFirstByTenantOrderByRecIdDesc(tenant);
            return projectChangeLogHeaderOptional.map(projectChangeLogHeader ->
                    new ByteArrayResource(fileUtil.downloadFile(projectChangeLogHeader.getFileId(), customFolderName))).orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    public String deleteProjectChangeLogHeaderFile(String fileId) {
        Optional<ProjectChangeLogHeader>  projectChangeLogHeaderOptional = projectChangeLogHeaderRepository.findByFileId(fileId);
        try {
            if (projectChangeLogHeaderOptional.isPresent()) {
                projectChangeLogHeaderRepository.deleteByFileId(fileId);

                final String FOLDER_NAME = "master-data";
                Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
                String customFolderName = String.format("%s/%s/project", FOLDER_NAME, tenant.getCode());
                fileUtil.removeFile(fileId, customFolderName);

                return projectChangeLogHeaderOptional.get().getFileId();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

}
