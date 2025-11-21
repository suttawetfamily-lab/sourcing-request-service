package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantOrganizationTemplate;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantOrganizationTemplatePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantOrganizationTemplateRepository extends JpaRepository<TenantOrganizationTemplate, TenantOrganizationTemplatePK> {

    @Query("SELECT t.id.templateId " +
            "FROM TenantOrganizationTemplate t " +
            "JOIN Tenant tn ON tn.recId = t.id.tenantId " +
            "WHERE tn.code = :tenantCode " +
            "AND t.id.organizationId = :organizationId")
    Integer findTemplateIdByTenantCodeAndOrganizationId(@Param("tenantCode") String tenantCode,
                                                        @Param("organizationId") Integer organizationId);

    @Query("SELECT t.id.templateId " +
            "FROM TenantOrganizationTemplate t " +
            "WHERE t.id.tenantId = :tenantId " +
            "AND t.id.organizationId = :organizationId")
    Integer findTemplateIdByTenantIdAndOrganizationId(@Param("tenantId") Integer tenantId,
                                                      @Param("organizationId") Integer organizationId);

    Optional<TenantOrganizationTemplate> findTop1ByIdTenantIdAndIdOrganizationId(Integer tenantId, Integer organizationId);

}
