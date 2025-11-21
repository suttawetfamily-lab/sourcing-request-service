package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.ErfxClient;
import com.pantavanij.sourcingreq.services.config.ERFXConfig;
import com.pantavanij.sourcingreq.services.domain.dto.AttachmentDto;
import com.pantavanij.sourcingreq.services.domain.dto.DelegationDto;
import com.pantavanij.sourcingreq.services.domain.dto.ERFXAttachmentDto;
import com.pantavanij.sourcingreq.services.domain.dto.UserDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.mapper.AttachmentMapper;
import com.pantavanij.sourcingreq.services.domain.request.DelegationActiveRequest;
import com.pantavanij.sourcingreq.services.domain.request.UoloadAttachmentRequest;
import com.pantavanij.sourcingreq.services.domain.response.DelegationActiveResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.AttachmentService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.DelegationService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantRequestStatusService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UaaService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import com.pantavanij.sourcingreq.services.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.unbescape.html.HtmlEscape;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.pantavanij.sourcingreq.services.enums.Activity.ACTIVITY_EDIT;

@RequiredArgsConstructor
@Service
public class AttachmentServiceImpl implements AttachmentService {

    private final ERFXConfig erfxConfig;
    private final ErfxClient erfxClient;
    private final AttachmentRepository attachmentRepository;
    private final RequestAttachmentRepository requestAttachmentRepository;
    private final TenantRequestStatusService tenantRequestStatusService;
    private final RequestPurchaserRepository requestPurchaserRepository;
    private final RequestReviewerRepository requestReviewerRepository;
    private final DelegationService delegationService;
    private final UaaService uaaService;
    private final FileUtil fileUtil;
    private final RequestReportLineRepository requestReportLineRepository;
    private final RequestApproverRepository requestApproverRepository;
    private final String FOLDER_NAME = "attachments";

    @Override
    public AttachmentDto UploadAttachment(Tenant tenant, UoloadAttachmentRequest request) {

        this.validateFile(request.getFile());
        final String fileName = request.getFile().getOriginalFilename();
        AttachmentDto attachmentDto = null;

        try {
            String fileExtension = getFileExtension(fileName);
            String uuid = UUID.randomUUID().toString();
            String fileUniqueName = uuid + "." + fileExtension;

            String customFolderName = String.format("%s/%s",FOLDER_NAME, tenant.getCode());
            fileUtil.uploadFileByte(request.getFile().getBytes(), fileUniqueName, customFolderName);

            Attachment attachment = Attachment.builder()
                    .tenantId(tenant.getRecId())
                    .fileId(fileUniqueName)
                    .fileName(fileName)
                    .fileGroup(request.getFileGroup())
                    .fileSize(Math.toIntExact(request.getFile().getSize()))
                    .statusId(1)
                    .createdBy(AppUtil.getUserName())
                    .createdDate(DateTimeUtil.getTimestampUTC())
                    .build();

            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            attachmentDto = AttachmentMapper.INSTANCE.toAttachmentDto(this.saveAttachment(attachment), timeZone);
        }
        catch (Exception ex) {

        }

        return attachmentDto;
    }

    @Override
    public Attachment saveAttachment(Attachment attachment) {
        return attachmentRepository.save(attachment);
    }

    @Override
    public ByteArrayResource downloadAttachment(Tenant tenant, String fileUniqueName) {
        String customFolderName = String.format("%s/%s",FOLDER_NAME, tenant.getCode());
        ByteArrayResource byteArrayResource = null;
        Attachment attachment = attachmentRepository.findAttachmentByFileId(fileUniqueName);
        if(attachment != null) {
            if(attachment.getFileGroup().equalsIgnoreCase("REQIERFX")) {
                 byteArrayResource = new ByteArrayResource(this.getAttachmentFromURL(attachment.getFileURL()));
            } else {
                byteArrayResource = new ByteArrayResource(fileUtil.downloadFile(fileUniqueName, customFolderName));
            }
        }
        return byteArrayResource;
    }

    private HttpHeaders createTokenHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String authHeader = "Bearer " + AppUtil.getJwtToken();
        headers.set("Authorization", authHeader);
        return headers;
    }

    @Override
    @Transactional
    public boolean deleteAttachment(Tenant tenant, Long attachmentId) {
        UserDto user = AppUtil.getUser();
        RequestAttachment requestAttachment = requestAttachmentRepository.findRequestAttachmentByAttachmentId(attachmentId);
        Request request = new Request();

        if (requestAttachment != null && requestAttachment.getRequest() != null) {
            request = requestAttachment.getRequest();
        } else {
            return false;
        }

        List<TenantRequestStatus> tenantRequestStatuses = tenantRequestStatusService.getRequestStatuses();
        List<RequestApprover> requestApprovers = requestApproverRepository.findRequestApproversByRequest(request);
        List<RequestReviewer> requestReviewers = requestReviewerRepository.getByRequest(request.getRecId());
        List<RequestPurchaser> requestPurchasers = requestPurchaserRepository.findByRequest(request.getRecId());
        List<RequestReportLine> reportLines = requestReportLineRepository.getByRequest(request.getRecId());

        DelegationDto delegationDto = null;
        DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
        delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
        delegationActiveRequest.setDelegatorBy(request.getAssignedBy());

        DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
        if (delegationActiveResponse.getData() != null) {
            delegationDto = delegationActiveResponse.getData();
        }

        if (AppUtil.isAllowedAction(user, request, requestPurchasers, requestApprovers, null, null, requestReviewers, reportLines, delegationDto, tenantRequestStatuses, ACTIVITY_EDIT)) {
            Optional<Attachment> attachment = attachmentRepository.findById(attachmentId);
            if(attachment.isPresent()) {
                try {
                    String customFolderName = String.format("%s/%s",FOLDER_NAME, tenant.getCode());
                    AttachmentDto attachmentDto = AttachmentMapper.INSTANCE.toAttachmentDto(attachment.get());
                    fileUtil.removeFile(attachmentDto.getFileId(), customFolderName);
                    attachmentRepository.deleteAttachmentByRecId(attachmentDto.getRecId());
                    return true;
                } catch (Exception ex) {
                    ex.getMessage();
                }
            }
        }
        return false;
    }

    @Override
    @Transactional
    public void deleteUnusedAttachment(Tenant tenant) {
        List<Attachment> attachments = attachmentRepository.getUnusedAttachment(Math.toIntExact(tenant.getRecId()));
        attachments
            .forEach(attachment -> {
                    try {
                        String customFolderName = String.format("%s/%s", FOLDER_NAME, tenant.getCode());
                        fileUtil.removeFile(attachment.getFileId(), customFolderName);
                        attachmentRepository.delete(attachment);
                    } catch (Exception ex) {
                    }
                }
            );
    }

    @Override
    public List<Attachment> saveAttachmentERFX(List<ERFXAttachmentDto> attachmentDtoList, Integer tenantId) {
        List<Attachment> attachmentList = new ArrayList<>();
        try {
            if(attachmentDtoList != null && !attachmentDtoList.isEmpty()){
                for (ERFXAttachmentDto dto : attachmentDtoList){

                    String fileExtension = getFileExtension(dto.getUrl());
                    String uuid = UUID.randomUUID().toString();
                    String fileUniqueName = uuid + "." + fileExtension;

                    Attachment attachment = new Attachment();
                    attachment.setFileId(fileUniqueName);
                    attachment.setFileName(HtmlEscape.unescapeHtml(dto.getName()));
                    attachment.setTenantId(tenantId);
                    attachment.setFileGroup("REQIERFX");
                    attachment.setStatusId(1);
                    String fullyFileURL = String.format("%s/%s", erfxConfig.getHostName(),  dto.getUrl());
                    try {
                        attachment.setFileSize(FileUtil.getFileSize(new URL(fullyFileURL)));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    attachment.setFileURL(dto.getUrl());
                    attachment.setCreatedBy(AppUtil.getUserName());
                    attachment.setCreatedDate(DateTimeUtil.getTimestampUTC());
                    attachment = saveAttachment(attachment);
                    attachmentList.add(attachment);
                }
            }
            return attachmentList;
        } catch (Exception ex){
            throw new BusinessException(ApiMessage.E7043, ApiMessage.E7043.description());
        }
    }

    private void validateFile(MultipartFile files) {
        if (!FileUtil.isAllowedFileType(files.getOriginalFilename())) {
            throw new BusinessException(ApiMessage.E7002);
        }
    }

    private String getFileExtension(String file) {
        return file.substring(file.lastIndexOf('.') + 1);
    }

    public byte[] getAttachmentFromURL(String fileUrl) {
        try {
            String bearerToken = "Bearer " + AppUtil.getJwtToken();
            return erfxClient.getAttachmentFromURL(bearerToken, fileUrl);
        } catch (Exception e) {
            e.printStackTrace(); // Handle or log the exception appropriately
            return null;
        }
    }
}
