package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

import java.util.Date;

@Data
public class RequestExportRequest {
    private Date fromDate;
    private Date toDate;
}
