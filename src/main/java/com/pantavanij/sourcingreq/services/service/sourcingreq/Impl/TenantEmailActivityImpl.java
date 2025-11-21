package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.mapper.*;
import com.pantavanij.sourcingreq.services.exception.*;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;

import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class TenantEmailActivityImpl implements TenantEmailActivityService {

    private final TenantEmailActivityRepository tenantEmailActivityRepository;
    private final TenantService tenantService;

    @Override
    public SendMailToRoleDto getByTenantIdAndEmailActivityIdAndActivityId(Long emailActivityId, Integer activityId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        try {
            Optional<TenantEmailActivity> tenantEmailActivityOpt = tenantEmailActivityRepository.findByTenant_RecIdAndEmailActivity_RecIdAndActivity_RecId(tenant.getRecId(), emailActivityId, activityId);
            if (tenantEmailActivityOpt.isPresent()) {
                TenantEmailActivity tenantEmailActivity = tenantEmailActivityOpt.get();
                return TenantEmailActivityMapper.INSTANCE.toSendMailToRoleDto(tenantEmailActivity);
            }
            return null;
        } catch (Exception e) {
            log.error("Error while get TenantEmailActivity: {} ", e.getMessage());
            System.out.println(e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }



}
