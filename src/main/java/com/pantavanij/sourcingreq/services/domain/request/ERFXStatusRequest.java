package com.pantavanij.sourcingreq.services.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ERFXStatusRequest {
    List<Long>  erfxNum;
}