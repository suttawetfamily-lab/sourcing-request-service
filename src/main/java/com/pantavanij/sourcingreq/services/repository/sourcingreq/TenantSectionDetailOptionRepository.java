package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantSectionDetailOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantSectionDetailOptionRepository extends JpaRepository<TenantSectionDetailOption, Long> {

    List<TenantSectionDetailOption> findByOptionNameAndTenant(String optionName, Tenant tenant);

}
