package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.eform.FormDTO;
import com.pantavanij.sourcingreq.services.domain.dto.eform.FormFieldDTO;
import com.pantavanij.sourcingreq.services.domain.dto.eform.FormOptionChoiceDTO;
import com.pantavanij.sourcingreq.services.domain.dto.eform.FormSectionDTO;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestQuestionnaire;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestQuestionnaireFormField;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestQuestionnaireFormFieldOptionChoice;
import com.pantavanij.sourcingreq.services.domain.request.EFormSaveRequest;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface QuestionnaireMapper {

    QuestionnaireMapper INSTANCE = Mappers.getMapper(QuestionnaireMapper.class);

    FormDTO toFormDTO(EFormSaveRequest request);

    @Mapping(source = "requestQuestionnaireFormFields", target = "formTabs")
    FormDTO toFormDTO(RequestQuestionnaire entity);

    FormFieldDTO toFormFieldDTO(RequestQuestionnaireFormField requestQuestionnaireFormField);

    FormOptionChoiceDTO toFormOptionChoiceDTO(RequestQuestionnaireFormFieldOptionChoice requestQuestionnaireFormFieldOptionChoice);

}
