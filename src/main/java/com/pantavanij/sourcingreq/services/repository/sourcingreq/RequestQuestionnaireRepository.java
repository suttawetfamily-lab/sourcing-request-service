package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestQuestionnaire;
import com.pantavanij.sourcingreq.services.domain.projection.RequestIdStatusProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestQuestionnaireRepository extends JpaRepository<RequestQuestionnaire, Long> {
    @Query(value = "SELECT TOP 1 * FROM RequestQuestionnaire WHERE RequestId = :requestId ORDER BY RecId DESC", nativeQuery = true)
    Optional<RequestQuestionnaire> findByRequestId(@Param("requestId") Long requestId);

    @Query(value = "SELECT TOP 1 * FROM RequestQuestionnaire WHERE RequestId = :requestId AND FormId = :formId ORDER BY RecId DESC", nativeQuery = true)
    Optional<RequestQuestionnaire> findByRequestIdAndFormId(@Param("requestId") Long requestId, @Param("formId") Long formId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    void deleteByRequest(Request request);

    @Query(
            value = "SELECT r.RecId AS recId, r.StatusId AS statusId " +
                    "FROM RequestQuestionnaire rq " +
                    "INNER JOIN Request r ON rq.RequestId = r.RecId " +
                    "WHERE rq.FormId = :formId " +
                    "AND r.StatusId IN (:statusList)",
            nativeQuery = true
    )
    List<RequestIdStatusProjection> findRequestIdAndStatusByFormIdAndStatusId(
            @Param("formId") Long formId,
            @Param("statusList") List<Integer> statusList
    );
    boolean existsByFormId(Long formId);
}
