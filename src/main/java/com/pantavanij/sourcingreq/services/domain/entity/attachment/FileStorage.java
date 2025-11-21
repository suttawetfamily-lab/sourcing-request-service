package com.pantavanij.sourcingreq.services.domain.entity.attachment;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Data
@Component
public class FileStorage {
    private String excelDir;
    private String excelTemplateDir;
    private String sysUserDir;


    public Path getExcelStorage() {
        return Paths.get(this.excelDir).toAbsolutePath().normalize();
    }

    public Path getExcelTemplateStorage() {
        return Paths.get(this.excelTemplateDir).toAbsolutePath().normalize();
    }

    public Path getFileStorageLocationBySysUser() {
        return Paths.get(this.sysUserDir).toAbsolutePath().normalize();
    }

}
