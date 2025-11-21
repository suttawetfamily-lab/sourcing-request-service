package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class EMailConfigDto {

    @NotNull
    @Email(message = "Email should be valid")
    private String mailFrom;

    @NotNull
    private List<@Email(message = "Email should be valid") String> mailTo;

    private List<@Email(message = "Email should be valid") String> mailCC;

    private List<@Email(message = "Email should be valid") String> mailBCC;

    @NotNull(message = "mailSubject must not be null or empty ")
    private String mailSubject;

    private byte[] attachment;

    @NotNull(message = "templateId must not be null or empty ")
    private Long templateId;

    @NotNull(message = "url must not be null or empty ")
    private String url;

    private String mappingData;

    @NotNull(message = "confirmName must not be null or empty ")
    private String confirmName;
}
