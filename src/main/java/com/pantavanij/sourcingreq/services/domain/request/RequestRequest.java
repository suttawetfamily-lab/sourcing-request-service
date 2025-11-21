package com.pantavanij.sourcingreq.services.domain.request;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.List;

@Data
public class RequestRequest {
    
    @NotNull(message = "recId must not be null or empty" )
    @Max(value = 9223372036854775807L ,message = "recId exceed limit {value} value")
    @Min(value = 0 ,message = "recId exceed limit {value} value")
    private Long recId;

    @NotNull(message = "tenantId must not be null or empty")
    @Max(value = 2147483647 ,message = "tenantId exceed limit {value} value")
    @Min(value = 0 ,message = "tenantId exceed limit {value} value")
    private Integer tenantId;

    private OptionDto organizationObj;
    private OptionDto requestTypeObj;

    @Size(max = 255 ,message = "requestName exceed limit {max} chars")
    private String requestName;

    private Timestamp expectedDate;

    @NotNull(message = "currencyObj must not be null")
    private OptionDto currencyObj;

    @Size(max = 26 ,message = "budget exceed limit {max} chars")
    private String budget;

    @Size(max = 20 ,message = "phone exceed limit {max} chars")
    private String phone;

    @Size(max = 20 ,message = "mobile exceed limit {max} chars")
    private String mobile;

    private String email;

    @Size(max = 2000 ,message = "noteToApprovers exceed limit {max} chars")
    private String noteToApprovers;

    @Size(max = 2000 ,message = "cancellationReason exceed limit {max} chars")
    private String cancellationReason;

    private List<Integer> approvers;

    private List<String> reviewers;
    private String projectCode;
    private String projectName;
    private String projectLabel;
    private String objective;
    private String budgetRefNo;
    private String vatType;

    private OptionDto objectiveObj;

    private String categoryLabel;
    private String subCategoryLabel;

    private OptionDto categoryObj;
    private OptionDto subCategoryObj;
    private OptionDto purchaserObj;
    private OptionDto projectObj;
    private OptionDto typeObj;
    private OptionDto departmentObj;
    private OptionDto budgetRefNoObj;
    private Integer vatTypeId;

    private LocationDto deliveryLocation;

    @Size(max = 500 ,message = "location exceed limit {max} chars")
    private String location;

    @Size(max = 100 ,message = "contactName exceed limit {max} chars")
    private String contactName;

    @Size(max = 50 ,message = "contactPhone exceed limit {max} chars")
    private String contactPhone;

    // BAY ADDITIONAL
    private OptionDto whtAbsorbedBy;
    private Timestamp evaluationDate;
    private String requester;
    private OptionDto performanceEvaluation;
    private OptionDto pdpaQ01;
    private OptionDto pdpaQ02;
    private OptionDto pdpaQ03;
    private OptionDto pdpaQ04;
    private OptionDto pdpaQ05;
    private OptionDto pdpaQ06;
    private String relatePdpa;
    private String thirdPartyRole;
    private String dpaType;
    private OptionDto outsourceService;
    private String background;
    private String requestStatus;
    private String department;
    private List<Long> reportLines;

    private String approveNo;
    private String costcenter;
    private OptionDto makingContract;
    private OptionDto makingRptContract;
    private OptionDto needWhtCert;
    private OptionDto vatAbsorbedBy;
    private OptionDto stampDuty;
    private String makingContractReason;
    private String makingRptContractReason;

    private String mainStatus;
    private String pathUrl;

    private List<RequestAttachmentDto> requestAttachmentList;

    private Integer organizationId;
}
