package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface ProjectChangeLogHeaderRepository extends JpaRepository<ProjectChangeLogHeader, Long>, JpaSpecificationExecutor<ProjectChangeLogHeader> {
    Optional<ProjectChangeLogHeader> findByFileId(String fileId);
    Optional<ProjectChangeLogHeader> findFirstByTenantOrderByRecIdDesc(Tenant tenant);
    void deleteByFileId(String fileId);
}
