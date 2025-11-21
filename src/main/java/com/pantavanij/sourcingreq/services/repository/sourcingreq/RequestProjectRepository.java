package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Project;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestProjectRepository extends JpaRepository<RequestProject, Integer> {

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "INSERT INTO RequestProject(RequestId, ProjectId, ProjectCode, ProjectName) VALUES(:requestId, :projectId, :projectCode, :projectName)", nativeQuery = true)
    void saveRequestProject(@Param("requestId") Long requestId,
                               @Param("projectId") Integer projectId,
                               @Param("projectCode") String projectCode,
                               @Param("projectName") String projectName);

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE RequestProject SET ProjectCode = :projectCode, ProjectName = :projectName WHERE RequestId = :requestId AND ProjectId = :projectId ", nativeQuery = true)
    void updateRequestProject(@Param("requestId") Long requestId,
                                 @Param("projectId") Integer projectId,
                                 @Param("projectCode") String projectCode,
                                 @Param("projectName") String projectName);


    List<RequestProject> findRequestProjectsByRequest(Request request);

    RequestProject findRequestProjectByRequestAndProject(Request request, Project project);

    void deleteRequestProjectByRequest(Request request);

    void deleteRequestProjectByRequestAndProject(Request request, Project project);

    @Query(value = "SELECT TOP 1 * FROM RequestProject WHERE RequestId = :requestId", nativeQuery = true)
    Optional<RequestProject> findTop1ByRequestId(@Param("requestId") Long requestId);

    List<RequestProject> findByRequest(Request request);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    void deleteByRequest(Request request);
}
