
package com.pantavanij.sourcingreq.services.domain.request;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import lombok.*;

import javax.validation.constraints.*;
import java.sql.*;
import java.util.*;

@Data
public class TenantSectionDetailRequest {
    @NotNull(message = "RecId is required!")
    @PositiveOrZero(message = "RecId must be positive or zero!")
    private Integer recId;
    @NotNull(message = "Tenant section id is required!")
    @PositiveOrZero(message = "Tenant section id must be positive or zero!")
    private Integer tenantSectionId;
    @NotBlank(message = "Name is required!")
    private String name;
    @NotBlank(message = "Field name is required!")
    private String fieldName;
    private String fieldGroupName;
    private String fieldGroupLabel;
    @NotBlank(message = "Type is required!")
    private String type;
    private String tooltip;
    @NotBlank(message = "Label is required!")
    private String label;
    @NotBlank(message = "Report label is required!")
    private String reportLabel;
    private String preSelectName;
    private String preSelectValues;
    @NotNull(message = "Span is required!")
    @PositiveOrZero(message = "Span must be positive or zero!")
    @Size(min = 0, max = 24, message = "Span must be between 0 and 24!")
    private Integer span;
    @NotNull(message = "Pre-span is required!")
    @PositiveOrZero(message = "Pre-span must be positive or zero!")
    @Size(min = 0, max = 24, message = "Pre-span must be between 0 and 24!")
    private Integer preSpan;
    @NotNull(message = "Post-span is required!")
    @PositiveOrZero(message = "Post-span must be positive or zero!")
    @Size(min = 0, max = 24, message = "Post-span must be between 0 and 24!")
    private Integer postSpan;
    private String placeholder;
    private String dependencyObjectName;
    private List<String> dependencyObjectValues;
    private String watchResetValue;
    private String watchOriginalFieldName;
    private String watchUpdatedFieldName;
    private String disableObjectName;
    private List<String> disableObjectValues;
    private String disabledStyles;
    private String cssStyles;
    private Integer maxLength;
    private Integer height;
    private Integer width;
    private String align;
    private String fixed;
    @NotNull(message = "Sequence is required!")
    @PositiveOrZero(message = "Sequence must be positive or zero!")
    private Integer sequence;
    @NotNull(message = "Report-filter is required!")
    private boolean reportFilter;
    @NotNull(message = "Visible is required!")
    private boolean visible;
    @NotNull(message = "Force visible is required!")
    private boolean forceVisible;
    @NotNull(message = "Full preview is required!")
    private boolean fullPreview;
    @NotNull(message = "Preview is required!")
    private boolean preview;
    @NotNull(message = "Preview sequence is required!")
    @PositiveOrZero(message = "Preview sequence must be positive or zero!")
    private Integer previewSequence;
//    @NotNull(message = "Export itemRpt is required!")
//    private boolean exportItemRpt;
//    @NotNull(message = "Export itemRpt sequence is required!")
//    @PositiveOrZero(message = "Export itemRpt sequence must be positive or zero!")
//    private Integer exportItemRptSequence;
//    @NotNull(message = "Export requestRpt is required!")
//    private boolean exportRequestRpt;
//    @NotNull(message = "Export requestRpt sequence is required!")
//    @PositiveOrZero(message = "Export requestRpt sequence must be positive or zero!")
//    private Integer ExportRequestRptSequence;
    @NotNull(message = "Header is required!")
    private boolean header;
    @NotNull(message = "Header sequence is required!")
    @PositiveOrZero(message = "Header sequence must be positive or zero!")
    private Integer headerSequence;
    private String headerLabel;
    private boolean disabled;
    private boolean excelItemTemplate;
    private boolean modeView;
    private Integer modeViewSequence;
    private boolean modeViewSQN;
    private Integer modeViewSequenceSQN;
    private boolean modeViewSQV;
    private Integer modeViewSequenceSQV;
    private boolean modeViewSQP;
    private Integer modeViewSequenceSQP;
    private boolean modeViewSQA;
    private Integer modeViewSequenceSQA;
    private String mode;
    private String reportExampleData;
    private String remark;
    private TenantSectionDetailDataSourceRequest tenantSectionDetailDataSourceRequest;
    private List<TenantSectionDetailDependencyRequest> tenantSectionDetailDependencyRequestList;
    private List<TenantSectionDetailValidatorRequest> tenantSectionDetailValidatorRequestList;
    private List<TenantSectionDetailWatchRequest> tenantSectionDetailWatchRequestList;
    private TenantSectionDetailDescriptionRequest tenantSectionDetailDescriptionRequest;
}
