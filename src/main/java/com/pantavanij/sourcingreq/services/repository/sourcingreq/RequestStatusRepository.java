package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestStatusRepository extends JpaRepository<RequestStatus, Integer> {

    RequestStatus findRequestStatusByRecId(Integer recId);

    @Query(value = "SELECT rs.* FROM RequestStatus rs " +
            "JOIN TenantRequestStatus trs ON rs.RecId = trs.RequestStatusId " +
            "WHERE trs.TenantId = :tenantId AND rs.Name = :name", nativeQuery = true)
    RequestStatus findByNameAndTenantId(@Param("name") String name, @Param("tenantId") Integer tenantId);
}
