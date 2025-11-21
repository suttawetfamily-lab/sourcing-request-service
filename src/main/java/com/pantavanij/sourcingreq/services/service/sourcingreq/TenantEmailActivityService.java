package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.*;

public interface TenantEmailActivityService {
    SendMailToRoleDto getByTenantIdAndEmailActivityIdAndActivityId(Long emailActivityId, Integer activityId);
}
