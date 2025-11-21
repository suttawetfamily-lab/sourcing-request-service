package com.pantavanij.sourcingreq.services.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import javax.validation.constraints.*;

@Data
public class MenuPrivilegeRequest {
    @NotNull(message = "Menu id is required")
    @PositiveOrZero(message = "Menu id must be a positive number")
    private Integer recId;
    @NotBlank(message = "Menu name is required")
    private String pathUrl;
    @NotNull(message = "Type id is required")
    @PositiveOrZero(message = "Type id must be a positive number")
    private Integer typeId;
    @NotBlank(message = "Privilege code is required")
    private String privilegeCode;
    @NotBlank(message = "Menu name is required")
    private String menuName;
    @NotBlank(message = "Menu label is required")
    private String label;
    @NotNull(message = "Sequence is required!")
    private Integer sequence;
    @JsonProperty("default")
    @NotNull(message = "Default is required!")
    private boolean isDefault;
    @NotNull(message = "Active is required!")
    private boolean active;
}
