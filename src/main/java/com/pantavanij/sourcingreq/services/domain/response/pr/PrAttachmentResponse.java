package com.pantavanij.sourcingreq.services.domain.response.pr;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PrAttachmentResponse {

    @JsonProperty("AttachedDocumentId")
    private Long attachedDocumentId;

    @JsonProperty("LastUpdateDate")
    private String lastUpdateDate;

    @JsonProperty("LastUpdatedBy")
    private String lastUpdatedBy;

    @JsonProperty("DatatypeCode")
    private String datatypeCode;

    @JsonProperty("FileName")
    private String fileName;

    @JsonProperty("DmFolderPath")
    private String dmFolderPath;

    @JsonProperty("DmDocumentId")
    private String dmDocumentId;

    @JsonProperty("DmVersionNumber")
    private String dmVersionNumber;

    @JsonProperty("Url")
    private String url;

    @JsonProperty("CategoryName")
    private String categoryName;

    @JsonProperty("UserName")
    private String userName;

    @JsonProperty("Uri")
    private String uri;

    @JsonProperty("FileUrl")
    private String fileUrl;

    @JsonProperty("UploadedText")
    private String uploadedText;

    @JsonProperty("UploadedFileContentType")
    private String uploadedFileContentType;

    @JsonProperty("UploadedFileLength")
    private Long uploadedFileLength;

    @JsonProperty("UploadedFileName")
    private String uploadedFileName;

    @JsonProperty("ContentRepositoryFileShared")
    private String contentRepositoryFileShared;

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("ErrorStatusCode")
    private String errorStatusCode;

    @JsonProperty("ErrorStatusMessage")
    private String errorStatusMessage;

    @JsonProperty("CreatedBy")
    private String createdBy;

    @JsonProperty("CreationDate")
    private String creationDate;

    @JsonProperty("ExpirationDate")
    private String expirationDate;

    @JsonProperty("LastUpdatedByUserName")
    private String lastUpdatedByUserName;

    @JsonProperty("CreatedByUserName")
    private String createdByUserName;

    @JsonProperty("AsyncTrackerId")
    private String asyncTrackerId;

    @JsonProperty("DownloadInfo")
    private String downloadInfo;

    @JsonProperty("PostProcessingAction")
    private String postProcessingAction;

    @JsonProperty("links")
    private List<PrAttachmentLinkResponse> links;
}
