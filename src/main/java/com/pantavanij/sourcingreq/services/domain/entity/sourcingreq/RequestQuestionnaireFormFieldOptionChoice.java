package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "RequestQuestionnaireFormFieldOptionChoice")
public class RequestQuestionnaireFormFieldOptionChoice implements Cloneable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "RecId")
    private Long recId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RequestQuestionnaireFormFieldId")
    @JsonBackReference
    private RequestQuestionnaireFormField requestQuestionnaireFormField;

    @Column(name = "OptionChoiceID")
    private Long optionChoiceId;

    @Column(name = "Sequence")
    private Integer sequence;

    @Column(name = "ChoiceName")
    private String choiceName;

    @Column(name = "IsOther")
    private Boolean isOther = false;

    @Column(name = "IsSelected")
    private Boolean isSelected = false;

    @Column(name = "Point")
    private Double point;

    @Column(name = "CreatedBy")
    private String createdBy;

    @Column(name = "CreatedDate")
    private Timestamp createdDate;

    @Column(name = "UpdatedBy")
    private String updatedBy;

    @Column(name = "UpdatedDate")
    private Timestamp updatedDate;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
