package com.pantavanij.sourcingreq.services.domain.dto.eform;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class FormFieldDTO {
    private Long fieldId;
    private Long fieldTypeId;
    private String fieldName;
    private String fieldDesc;
    private Integer sequence;
    private Double weight;
    private String defaultValue;
    private Integer maximumFile;
    private Boolean isRequire;
    private Boolean isQuiz;
    private Boolean isScore;
    private Boolean isRankByResponse;
    private Boolean isDisplayComment;
    private List<FormOptionChoiceDTO> optionChoices;
    private Double fullScore;
    private String fieldTypeName;
    private Long requestQuestionnaireFormFieldId;
}
