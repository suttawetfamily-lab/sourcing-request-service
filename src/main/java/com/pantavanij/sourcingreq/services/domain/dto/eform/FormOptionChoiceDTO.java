package com.pantavanij.sourcingreq.services.domain.dto.eform;

import lombok.Data;

@Data
public class FormOptionChoiceDTO {
    private Long optionChoiceId;
    private String choiceName;
    private Double point;
    private Boolean isOther;
    private Integer sequence;
    private Boolean isSelected;
}
