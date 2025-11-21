package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UoloadAttachmentRequest {
    private String fileName;
    private String fileGroup;
    private long sysUserId;
    private MultipartFile file;
}
