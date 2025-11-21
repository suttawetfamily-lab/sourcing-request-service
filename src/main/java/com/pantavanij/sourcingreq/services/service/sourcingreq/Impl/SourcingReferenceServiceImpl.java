package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SourcingReference;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.SourcingReferenceRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantOrganizationTemplateRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.SourcingReferenceService;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SourcingReferenceServiceImpl implements SourcingReferenceService {

    private final SourcingReferenceRepository sourcingReferenceRepository;
    private final TenantOrganizationTemplateRepository tenantOrganizationTemplateRepository;
    private final RequestRepository requestRepository;

    @Override
    public String generateSourcingNumber(String type, Integer tenantId, Integer organizationId) {
        Integer templateId = resolveTemplateId(tenantId, organizationId);
        SourcingReference reference = sourcingReferenceRepository
                .findByTypeAndTenantIdAndTemplateId(type, tenantId, templateId);
        if (reference == null) {
            return null;
        }
        return buildSourcingNumber(reference, type, tenantId);
    }

    @Override
    public String generateRequestNo(String type, Integer tenantId, Integer organizationId) {
        Integer templateId = resolveTemplateId(tenantId, organizationId);
        SourcingReference reference = sourcingReferenceRepository
                .findByTypeAndTenantIdAndTemplateId(type, tenantId, templateId);
        if (reference != null) {
            return buildRequestNo(reference, type, tenantId);
        } else {
            // fallback ไปใช้วิธี gen แบบเดิม (ไม่ผูก template)
            return this.generateRequestNo(tenantId);
        }
    }

    // ---------- Shared builders ----------

    private String buildSourcingNumber(SourcingReference reference, String type, Integer tenantId) {
        Integer nextSequence = reference.getSequence() == null ? 1 : reference.getSequence() + 1;

        String baseNo = String.format("%06d", nextSequence);

        StringBuilder sourcingNumber = new StringBuilder();
        if (reference.getPrefix() != null && !reference.getPrefix().isEmpty()) {
            sourcingNumber.append(reference.getPrefix());
        }
        sourcingNumber.append(baseNo);
        if (reference.getPostfix() != null && !reference.getPostfix().isEmpty()) {
            sourcingNumber.append(reference.getPostfix());
        }

        // ตรวจ flag SyncAllTemplates
        if (Boolean.TRUE.equals(reference.getSyncAllTemplates())) {
            syncSequenceAcrossTenant(type, tenantId, nextSequence);
        } else {
            reference.setSequence(nextSequence);
            reference.setUpdatedDate(DateTimeUtil.getTimestampUTC());
            sourcingReferenceRepository.save(reference);
        }

        return sourcingNumber.toString();
    }

    private String buildRequestNo(SourcingReference reference, String type, Integer tenantId) {
        Date date = new Date(DateTimeUtil.getTimestampUTC().getTime());
        String strYYYYMM = new SimpleDateFormat("yyyyMM").format(date);

        Integer nextSequence = reference.getSequence() == null ? 1 : reference.getSequence() + 1;
        String baseNo = strYYYYMM + String.format("%06d", nextSequence);

        StringBuilder requestNo = new StringBuilder();
        if (reference.getPrefix() != null && !reference.getPrefix().isEmpty()) {
            requestNo.append(reference.getPrefix());
        }
        requestNo.append(baseNo);
        if (reference.getPostfix() != null && !reference.getPostfix().isEmpty()) {
            requestNo.append(reference.getPostfix());
        }

        // ตรวจ flag SyncAllTemplates
        if (Boolean.TRUE.equals(reference.getSyncAllTemplates())) {
            syncSequenceAcrossTenant(type, tenantId, nextSequence);
        } else {
            reference.setSequence(nextSequence);
            reference.setUpdatedDate(DateTimeUtil.getTimestampUTC());
            sourcingReferenceRepository.save(reference);
        }

        return requestNo.toString();
    }

    /**
     * กรณี fallback เดิม (ไม่ใช้ template/organization)
     */
    private String generateRequestNo(Integer tenantId) {
        String requestNo;
        Date date = new Date(DateTimeUtil.getTimestampUTC().getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
        String strYYYYMM = formatter.format(date);
        int index = 1;
        int runningNo = 0;

        Optional<String> latestRunningRequestNo = requestRepository.findLatestRequestNoByTenantId(tenantId);
        if (latestRunningRequestNo.isPresent()) {
            runningNo = Integer.parseInt(latestRunningRequestNo.get());
        }

        // Format : yyyymm000001
        do {
            requestNo = strYYYYMM + String.format("%06d", runningNo + index++);
        } while (requestRepository.getRequestByRequestNo(requestNo, tenantId) != null);

        return requestNo;
    }

    // ---------- Helpers ----------

    private Integer resolveTemplateId(Integer tenantId, Integer organizationId) {
        if (organizationId == null) {
            throw new RuntimeException("organizationId is required");
        }
        Integer templateId = tenantOrganizationTemplateRepository
                .findTemplateIdByTenantIdAndOrganizationId(tenantId, organizationId);
        if (templateId == null) {
            throw new RuntimeException("Template not found for tenantId=" + tenantId
                    + ", organizationId=" + organizationId);
        }
        return templateId;
    }

    /**
     * Sync sequence ให้ทุก template ภายใต้ tenant เดียวกัน
     */
    private void syncSequenceAcrossTenant(String type, Integer tenantId, Integer nextSequence) {
        List<SourcingReference> references =
                sourcingReferenceRepository.findAllByTypeAndTenantId(type, tenantId);
        for (SourcingReference ref : references) {
            ref.setSequence(nextSequence);
            ref.setUpdatedDate(DateTimeUtil.getTimestampUTC());
        }
        sourcingReferenceRepository.saveAll(references);
    }
}
