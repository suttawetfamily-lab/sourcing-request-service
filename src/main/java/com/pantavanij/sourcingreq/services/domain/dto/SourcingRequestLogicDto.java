package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SourcingRequestLogicDto {
    private String tenantId;
    private Integer maximumUploadItem;
    private Step2LogicDto step2;
    private ExportReportDataRangeDto exportReportDataRange;
    private boolean lockDateRange;
    private boolean isSetDeliveryLocationFromStep1ToRequestItem;
    private boolean userLoginRedundancyCheck;
    private String[] checkWaringChangeFields;
    private Boolean forceSelectAllItemCopyToPR;
    private Boolean allowRepeateCopyToPR;
    private Boolean copyToPRViaERP;
}
