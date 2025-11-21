package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestQuestionnaireFormFieldOptionChoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestQuestionnaireFormFieldOptionChoiceRepository extends JpaRepository<RequestQuestionnaireFormFieldOptionChoice, Long> {

    @Query(value = "SELECT * FROM RequestQuestionnaireFormFieldOptionChoice WHERE RequestQuestionnaireFormFieldId IN (:formFieldId)", nativeQuery = true)
    List<RequestQuestionnaireFormFieldOptionChoice> findByRequestQuestionnaireFormField(@Param("formFieldId") List<Long> formFieldIds);

    @Query(value = "SELECT * FROM RequestQuestionnaireFormFieldOptionChoice WHERE RequestQuestionnaireFormFieldId = :formFieldId", nativeQuery = true)
    List<RequestQuestionnaireFormFieldOptionChoice> findByRequestQuestionnaireFormFieldId(@Param("formFieldId") Long formFieldId);
}
