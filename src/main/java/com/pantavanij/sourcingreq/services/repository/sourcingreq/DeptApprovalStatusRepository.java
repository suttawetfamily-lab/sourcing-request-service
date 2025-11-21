package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.DeptApprovalStatus;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeptApprovalStatusRepository extends JpaRepository<DeptApprovalStatus, Integer> {

    List<DeptApprovalStatus> findDeptApprovalStatusByNameNotIn(List<String> excludedDeptApprovalStatus);

    DeptApprovalStatus findByName(String name);
}
