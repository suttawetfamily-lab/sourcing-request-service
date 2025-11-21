package com.pantavanij.sourcingreq.services.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.util.List;
import java.util.Map;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TenantSectionDetailDto {
    private Long id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String fieldName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String fieldGroupName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String fieldGroupLabel;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String code;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String type;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String tooltip;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String label;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String reportLabel;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String displayName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PreSelectDto preSelect;

    @JsonIgnore
    private String preSelectName;

    @JsonIgnore
    private String preSelectValues;

    private Integer span;
    private Integer preSpan;
    private Integer postSpan;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String placeholder;

    private String watchResetValue;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<DependencyObjectDto> depends;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<WatchFieldNameDto> watches;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<DisableObjectDto> disable;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean disabled;

    @JsonIgnore
    private String disableObjectName;

    @JsonIgnore
    private String disableObjectValues;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String disabledStyles;

    @JsonIgnore
    private String cssStyles;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, String> styles;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer maxLength;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer height;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer width;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String align;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String fixed;

    private Integer sequence;
    private boolean reportFilter;
    private boolean forceVisible;
    private boolean fullPreview;
    private boolean preview;
    private Integer previewSequence;
    private boolean visible;
    private boolean header;
    private Integer headerSequence;
    private String headerLabel;
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

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> validations;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, String> params;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private DataSourceDto dataSource;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<DependencyObjectDto> dependencyList;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ValidatorDto> validatorList;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<WatchDto> watchList;

    @JsonIgnore
    private List<TenantSectionDetailValidatorDto> tenantSectionDetailValidatorList;

    @JsonIgnore
    private List<TenantSectionDetailDataSourceDto> tenantSectionDetailDataSourceList;

    @JsonIgnore
    private List<TenantSectionDetailDependencyDto> tenantSectionDetailDependencyList;

    @JsonIgnore
    private List<TenantSectionDetailWatchDto> tenantSectionDetailWatchList;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TenantSectionDetailDescriptionDto description;

    private String reportExampleData;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String remark;

}
