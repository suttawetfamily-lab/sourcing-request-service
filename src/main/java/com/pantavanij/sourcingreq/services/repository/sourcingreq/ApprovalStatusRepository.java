package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ApprovalStatus;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestStatus;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalStatusRepository extends JpaRepository<ApprovalStatus, Integer> {

    List<ApprovalStatus> findByRecIdNotIn(List<Integer> id);

    List<ApprovalStatus> findByNameNotIn(List<String> excludedStatus);

    @Query(value = "SELECT aps.* FROM ApprovalStatus aps " +
            "JOIN TenantApprovalStatus tas ON aps.RecId = tas.ApprovalStatusId " +
            "WHERE tas.TenantId = :tenantId AND aps.Name = :name", nativeQuery = true)
    ApprovalStatus findByNameAndTenantId(@Param("name") String name, @Param("tenantId") Integer tenantId);
}
