package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "RequestQuestionnaireFormField")
public class RequestQuestionnaireFormField implements Cloneable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "RecId")
    private Long recId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RequestQuestionnaireId")
    @JsonBackReference
    private RequestQuestionnaire requestQuestionnaire;

    @Column(name = "FieldID")
    private Long fieldId;

    @Column(name = "FieldTypeID")
    private Long fieldTypeId;

    @Column(name = "Sequence")
    private Integer sequence;

    @Column(name = "FieldName")
    private String fieldName;

    @Column(name = "FieldDesc")
    private String fieldDesc;

    @Column(name = "DefaultValue")
    private String defaultValue;

    @Column(name = "IsQuiz")
    private Boolean isQuiz = false;

    @Column(name = "IsRankByResponse")
    private Boolean isRankByResponse = false;

    @Column(name = "IsRequire")
    private Boolean isRequire = false;

    @Column(name = "IsScore")
    private Boolean isScore = false;

    @Column(name = "MaximumFile")
    private Integer maximumFile;

    @Column(name = "Weight")
    private Double weight;

    @Column(name = "IsDisplayComment")
    private Boolean isDisplayComment;

    @Column(name = "FullScore")
    private Double fullScore;

    @Column(name = "FieldTypeName")
    private String fieldTypeName;

    @Column(name = "CreatedBy")
    private String createdBy;

    @Column(name = "CreatedDate")
    private Timestamp createdDate;

    @Column(name = "UpdatedBy")
    private String updatedBy;

    @Column(name = "UpdatedDate")
    private Timestamp updatedDate;

    @OneToMany(mappedBy = "requestQuestionnaireFormField", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonBackReference
    private List<RequestQuestionnaireFormFieldOptionChoice> requestQuestionnaireFormFieldOptionChoices;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
