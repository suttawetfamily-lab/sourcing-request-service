package com.pantavanij.sourcingreq.services.domain.request;

import com.pantavanij.sourcingreq.services.domain.dto.EmailExcSourcingContractDetailDto;
import com.pantavanij.sourcingreq.services.enums.Activity;
import com.pantavanij.sourcingreq.services.enums.EmailActivity;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SendExcSourcingEMailRequest {

    @NotNull(message = "requestId must not be null or empty")
    private Long requestId;
    @NotNull(message = "excSourcingId must not be null or empty")
    private Long excSourcingId;
    @NotNull(message = "emailActivity must not be null or empty")
    private EmailActivity emailActivity;
    @NotNull(message = "activity must not be null or empty")
    private Activity activity;
    private EmailExcSourcingContractDetailDto emailExcSourcingContractDetailDto;

}


