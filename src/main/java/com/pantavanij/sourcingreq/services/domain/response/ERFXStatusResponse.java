package com.pantavanij.sourcingreq.services.domain.response;

import com.pantavanij.sourcingreq.services.domain.dto.ERFXStatusDto;
import lombok.Data;

import java.util.List;

@Data
public class ERFXStatusResponse {
    private List<ERFXStatusDto> data;
}
