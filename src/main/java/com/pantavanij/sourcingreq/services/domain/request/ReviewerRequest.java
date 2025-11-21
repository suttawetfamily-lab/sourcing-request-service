package com.pantavanij.sourcingreq.services.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.*;

@Data
public class ReviewerRequest {
    @NotNull(message = "Reviewer id is required!")
    private Integer recId;
    @NotNull(message = "User Id is required!")
    private Integer userId;
    @NotBlank(message = "Login Id is required!")
    private String loginId;
    @NotBlank(message = "Reviewer name is required!")
    private String reviewerName;
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
