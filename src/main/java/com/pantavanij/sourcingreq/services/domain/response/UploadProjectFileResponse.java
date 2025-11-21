package com.pantavanij.sourcingreq.services.domain.response;

import com.fasterxml.jackson.annotation.*;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import lombok.*;

import java.util.*;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadProjectFileResponse {
    private ApiResponseStatus status;
    private boolean valid;
    private List<UploadMessageResponse> messageResponses;
    private List<Project> projectList;
}
