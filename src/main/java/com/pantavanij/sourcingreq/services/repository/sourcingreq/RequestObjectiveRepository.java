package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Objective;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestObjective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestObjectiveRepository extends JpaRepository<RequestObjective, Integer> {

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "INSERT INTO RequestObjective(RequestId, ObjectiveId, ObjectiveCode, ObjectiveName) VALUES(:requestId, :objectiveId, :objectiveCode, :objectiveName)", nativeQuery = true)
    void saveRequestObjective(@Param("requestId") Long requestId,
                               @Param("objectiveId") Integer objectiveId,
                               @Param("objectiveCode") String objectiveCode,
                               @Param("objectiveName") String objectiveName);

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE RequestObjective SET ObjectiveCode = :objectiveCode, ObjectiveName = :objectiveName, ObjectiveId = :objectiveId WHERE RequestId = :requestId", nativeQuery = true)
    void updateRequestObjective(@Param("requestId") Long requestId,
                                 @Param("objectiveId") Integer objectiveId,
                                 @Param("objectiveCode") String objectiveCode,
                                 @Param("objectiveName") String objectiveName);


    List<RequestObjective> findRequestObjectivesByRequest(Request request);

    RequestObjective findRequestObjectiveByRequestAndObjective(Request request, Objective objective);

    void deleteRequestObjectiveByRequest(Request request);

    void deleteRequestObjectiveByRequestAndObjective(Request request, Objective objective);

    @Query(value = "SELECT TOP 1 * FROM RequestObjective WHERE RequestId = :requestId", nativeQuery = true)
    Optional<RequestObjective> findTop1ByRequestId(@Param("requestId") Long requestId);

    List<RequestObjective> findByRequest(Request request);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    void deleteByRequest(Request request);
}
