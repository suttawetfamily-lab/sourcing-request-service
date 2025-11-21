package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExcSourcing;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExcSourcingRepository extends JpaRepository<ExcSourcing, Long>,
        JpaSpecificationExecutor<ExcSourcing> {
    ExcSourcing findExcSourcingsByRecId(Long recId);

    Optional<ExcSourcing> findByExcSourcingDocNo(String excSourcingDocNo);

    ExcSourcing findByRecId(Long recId);

    @Query(value =
            "SELECT COUNT(DISTINCT es.RecId) " +
                    "FROM ExcSourcing es " +
                    "INNER JOIN ExcSourcingApprover esa ON es.RecId = esa.ExcSourcingId " +
                    "INNER JOIN Approver a ON esa.ApproverId = a.RecId " +
                    "WHERE es.TenantId = :tenantId " +
                    "AND a.LoginId = :username " +
                    "AND es.DeptApprovalStatusId = :statusId",
            nativeQuery = true)
    Integer countDeptApproverExcSourcingTask(@Param("tenantId") Integer tenantId,
                                             @Param("username") String username,
                                             @Param("statusId") Integer statusId);

    @Query(value =
            "SELECT COUNT(DISTINCT es.RecId) " +
                    "FROM ExcSourcing es " +
                    "INNER JOIN ExcSourcingPurchaser esp ON es.RecId = esp.ExcSourcingId " +
                    "INNER JOIN Approver a ON esp.ApproverId = a.RecId " +
                    "WHERE es.TenantId = :tenantId " +
                    "AND a.LoginId = :username " +
                    "AND es.ApprovalStatusId = :approvalStatusId",
            nativeQuery = true)
    Integer countPurchaserExcSourcingTask(@Param("tenantId") Integer tenantId,
                                          @Param("username") String username,
                                          @Param("approvalStatusId") Integer approvalStatusId);

}
