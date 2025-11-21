package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestQuestionnaireFormField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestQuestionnaireFormFieldRepository extends JpaRepository<RequestQuestionnaireFormField, Long> {

    @Query(value = "SELECT * FROM RequestQuestionnaireFormField WHERE RequestQuestionnaireId = :requestQuestionnaireId ", nativeQuery = true)
    List<RequestQuestionnaireFormField> findByRequestQuestionnaireId(@Param("requestQuestionnaireId") Long requestQuestionnaireId);
}
