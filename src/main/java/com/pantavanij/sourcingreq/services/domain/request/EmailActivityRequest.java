package com.pantavanij.sourcingreq.services.domain.request;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import org.javatuples.valueintf.*;

import javax.validation.constraints.*;

@Data
public class EmailActivityRequest {
    @NotNull(message = "Email activity id is required.")
    @PositiveOrZero(message = "Email activity id must be a positive number or zero.")
    private Long recId;
    @NotBlank(message = "Activity name is required.")
    private String activityName;
    @NotNull(message = "Sequence is required.")
    @PositiveOrZero(message = "Sequence must be a positive number or zero.")
    private Integer sequence;
    @JsonProperty("default")
    private boolean isDefault;
    private boolean active;
}
