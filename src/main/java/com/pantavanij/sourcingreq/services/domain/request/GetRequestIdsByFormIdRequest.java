package com.pantavanij.sourcingreq.services.domain.request;
import lombok.Data;

import java.util.List;

@Data
public class GetRequestIdsByFormIdRequest {
    private List<Integer> notAllowedStatus;
}
