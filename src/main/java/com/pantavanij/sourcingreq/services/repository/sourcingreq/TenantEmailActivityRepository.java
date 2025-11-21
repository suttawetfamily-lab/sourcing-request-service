package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface TenantEmailActivityRepository extends JpaRepository<TenantEmailActivity, Long>, JpaSpecificationExecutor<TenantEmailActivity> {
    Optional<TenantEmailActivity> findByTenant_RecIdAndEmailActivity_RecIdAndActivity_RecId(Integer tenantRecId, Long emailActivityRecId, Integer activityRecId);
}
