package com.pantavanij.sourcingreq.services.domain.request;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import javax.validation.constraints.*;

@Data
public class ReportLineRequest {
    @NotNull(message = "Report line id is required!")
    private Integer recId;
    @NotNull(message = "User Id is required!")
    private Integer userId;
    @NotBlank(message = "Login Id is required!")
    private String loginId;
    @NotBlank(message = "Report line name is required!")
    private String reportLineName;
    @NotBlank(message = "Email is required!")
    @Email(message = "Email is invalid!")
    private String email;
    @Size(max = 50, message = "Phone number could not over 50 digits!")
    private String phone;
    @PositiveOrZero(message = "Sequence must be a positive number or zero!")
    @NotNull(message = "Sequence is required!")
    private Integer sequence;
    @JsonProperty("default")
    private boolean isDefault;
    private boolean active;
}
