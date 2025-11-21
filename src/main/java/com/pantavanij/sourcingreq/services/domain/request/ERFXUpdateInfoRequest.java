package com.pantavanij.sourcingreq.services.domain.request;

import com.pantavanij.sourcingreq.services.domain.dto.ERFXAttachmentDto;
import com.pantavanij.sourcingreq.services.domain.dto.ShortlistDto;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class ERFXUpdateInfoRequest {
    private Long erfxNum;
    private String status;
    private String companyCode;
    private String companyName;
}
