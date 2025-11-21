package com.pantavanij.sourcingreq.services.domain.request;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import javax.validation.constraints.*;

@Data
public class ApproverRequest {
    @NotNull(message = "Approver Id (RecId) is required!")
    private Integer recId;
    @NotNull(message = "User Id is required!")
    private Integer userId;
    @NotBlank(message = "Login Id is required!")
    private String loginId;
    @NotBlank(message = "Approver name is required!")
    private String approverName;
    @NotBlank(message = "Email is required!")
    @Email(message = "Email is invalid!")
    private String email;
    @Size(max = 50, message = "Phone number could not over 50 digits!")
    private String phone;
    @Positive(message = "Sequence must be a positive number!")
    @NotNull(message = "Sequence is required!")
    private Integer sequence;
    @JsonProperty("default")
    private boolean isDefault;
    private boolean active;
}
