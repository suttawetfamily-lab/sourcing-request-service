package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "TenantSectionDetail")
public class TenantSectionDetail {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "RecId")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenantSectionId")
    @JsonBackReference
    private TenantSection tenantSection;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TenantId")
    @JsonBackReference
    private Tenant tenant;
    
    @Column(name = "Name")
    private String name;

    @Column(name = "FieldName")
    private String fieldName;

    @Column(name = "FieldGroupName")
    private String fieldGroupName;

    @Column(name = "FieldGroupLabel")
    private String fieldGroupLabel;

    @Column(name = "Type")
    private String type;
    
    @Column(name = "Tooltip")
    private String tooltip;
    
    @Column(name = "Label")
    private String label;

    @Column(name = "ReportLabel")
    private String reportLabel;
    
    @Column(name = "PreSelectName")
    private String preSelectName;
    
    @Column(name = "PreSelectValues")
    private String preSelectValues;
    
    @Column(name = "Span")
    private Integer span;
    
    @Column(name = "PreSpan")
    private Integer preSpan;
    
    @Column(name = "PostSpan")
    private Integer postSpan;
    
    @Column(name = "PlaceHolder")
    private String placeholder;

    @Column(name = "DependencyObjectName")
    private String dependencyObjectName;

    @Column(name = "DependencyObjectValues")
    private String dependencyObjectValues;

    @Column(name = "WatchResetValue")
    private String watchResetValue;

    @Column(name = "WatchOriginalFieldName")
    private String watchOriginalFieldName;

    @Column(name = "WatchUpdatedFieldName")
    private String watchUpdatedFieldName;

    @Column(name = "DisableObjectName")
    private String disableObjectName;

    @Column(name = "DisableObjectValues")
    private String disableObjectValues;

    @Column(name = "DisabledStyles")
    private String disabledStyles;

    @Column(name = "CssStyles")
    private String cssStyles;

    @Column(name = "MaxLength")
    private Integer maxLength;

    @Column(name = "Height")
    private Integer height;

    @Column(name = "Width")
    private Integer width;

    @Column(name = "Align")
    private String align;

    @Column(name = "Fixed")
    private String fixed;

    @Column(name = "Sequence")
    private Integer sequence;
    
    @Column(name = "ReportFilter")
    private boolean reportFilter;
    
    @Column(name = "Visible")
    private boolean visible;

    @Column(name = "ForceVisible")
    private boolean forceVisible;

    @Column(name = "FullPreview")
    private boolean fullPreview;

    @Column(name = "Preview")
    private boolean preview;

    @Column(name = "PreviewSequence")
    private Integer previewSequence;

//    @Column(name = "ExportItemRpt")
//    private boolean exportItemRpt;
//
//    @Column(name = "ExportItemRptSequence")
//    private Integer exportItemRptSequence;
//
//    @Column(name = "ExportRequestRpt")
//    private boolean exportRequestRpt;
//
//    @Column(name = "ExportRequestRptSequence")
//    private Integer exportRequestRptSequence;

    @Column(name = "Header")
    private boolean header;

    @Column(name = "HeaderSequence")
    private Integer headerSequence;

    @Column(name = "HeaderLabel")
    private String headerLabel;

    @Column(name = "CreatedBy")
    private String createdBy;
    
    @Column(name = "CreatedDate")
    private Timestamp createdDate;
    
    @Column(name = "UpdatedBy")
    private String updatedBy;
    
    @Column(name = "UpdatedDate")
    private Timestamp updatedDate;

    @Column(name = "ForceDisable")
    private boolean disabled;

    @Column(name = "ExcelItemTemplate")
    private boolean excelItemTemplate;

    @Column(name = "ModeView")
    private boolean modeView;

    @Column(name = "ModeViewSequence")
    private Integer modeViewSequence;

    @Column(name = "ModeView_SQN")
    private boolean modeViewSQN;

    @Column(name = "ModeViewSequence_SQN")
    private Integer modeViewSequenceSQN;

    @Column(name = "ModeView_SQV")
    private boolean modeViewSQV;

    @Column(name = "ModeViewSequence_SQV")
    private Integer modeViewSequenceSQV;

    @Column(name = "ModeView_SQP")
    private boolean modeViewSQP;

    @Column(name = "ModeViewSequence_SQP")
    private Integer modeViewSequenceSQP;

    @Column(name = "ModeView_SQA")
    private boolean modeViewSQA;

    @Column(name = "ModeViewSequence_SQA")
    private Integer modeViewSequenceSQA;

    @OneToMany(mappedBy = "tenantSectionDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonBackReference
    private List<TenantSectionDetailValidator> tenantSectionDetailValidatorList;

    @OneToMany(mappedBy = "tenantSectionDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonBackReference
    private List<TenantSectionDetailDataSource> tenantSectionDetailDataSourceList;

    @OneToMany(mappedBy = "tenantSectionDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonBackReference
    private List<TenantSectionDetailDependency> tenantSectionDetailDependencyList;

    @OneToMany(mappedBy = "tenantSectionDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonBackReference
    private List<TenantSectionDetailWatch> tenantSectionDetailWatchList;

    @OneToOne(mappedBy = "tenantSectionDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonBackReference
    private TenantSectionDetailDescription tenantSectionDetailDescription;

    @Column(name = "Mode")
    private String mode;

    @Column(name = "ReportExampleData")
    private String reportExampleData;

    @Column(name = "Remark")
    private String remark;
}
