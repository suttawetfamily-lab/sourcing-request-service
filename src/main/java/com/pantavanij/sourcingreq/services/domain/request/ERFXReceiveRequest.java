package com.pantavanij.sourcingreq.services.domain.request;

import com.pantavanij.sourcingreq.services.domain.dto.ERFXAttachmentDto;
import com.pantavanij.sourcingreq.services.domain.dto.ShortlistDto;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

@Data
public class ERFXReceiveRequest {
    @Valid
    private List<ShortlistDto> erfxItems;
    private List<ERFXAttachmentDto> erfxAttachments;
    private String awardedType;
    private String companyCode;
    private String companyName;
    private String priceCondition;
    private String withdraw;
    private String withdrawReason;
}
