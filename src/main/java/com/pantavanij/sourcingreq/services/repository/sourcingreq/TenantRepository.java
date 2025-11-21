package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.*;

import java.util.*;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Integer>, JpaSpecificationExecutor<Tenant> {
    Tenant findTenantByCode(String code);
    Tenant findTenantByRecId(Integer recId);
}
