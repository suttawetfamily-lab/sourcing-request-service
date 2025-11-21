package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.*;

import java.util.*;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class EmailActivitySearchDto {
    private List<EmailActivityDto> emailActivityDtoList;
    private int page;
    private int pageSize;
    private long total;
    private long totalPage;
}
