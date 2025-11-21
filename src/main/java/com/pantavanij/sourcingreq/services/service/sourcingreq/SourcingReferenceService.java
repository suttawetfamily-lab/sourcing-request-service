package com.pantavanij.sourcingreq.services.service.sourcingreq;

public interface SourcingReferenceService {

    /**
     * Generate Sourcing Number (prefix + running number + postfix)
     * โดย sequence จะถูกแยกตาม tenantId + organizationId (resolve เป็น templateId ภายใน service)
     */
    String generateSourcingNumber(String type, Integer tenantId, Integer organizationId);

    /**
     * Generate Request Number (yyyymm + running number + prefix/postfix)
     * โดย sequence จะถูกแยกตาม tenantId + organizationId (resolve เป็น templateId ภายใน service)
     */
    String generateRequestNo(String type, Integer tenantId, Integer organizationId);

}
