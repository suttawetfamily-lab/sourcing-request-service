package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.EPAuthDefaultUserDTO;
import com.pantavanij.sourcingreq.services.domain.dto.EPAuthUserDTO;
import com.pantavanij.sourcingreq.services.domain.request.EPAuthUserSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.MailingConfigRequest;
import com.pantavanij.sourcingreq.services.domain.request.UpdateSessionRequest;
import com.pantavanij.sourcingreq.services.domain.response.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

public interface EPAuthService {
    EPAuthUserListResponse getListByConditions(EPAuthUserSearchRequest request, String privilegeCode);

    EPAuthUserDTO getDefaultReportLine(String tenantName, String borgId, String sysUserId);

    List<PurchaserGroupResponse> getAllPurchaserGroup(String tenantCode);

    List<ApprovalHierarchyResponse> getAllApprovalHierarchy(String tenantCode);

    EPAuthDefaultUserDTO getDefaultUser(String tenantName, String borgId, String sysUserId);

    MailingConfigResponse getSRMailingConfig(MailingConfigRequest request);

    VerifySessionResponse updateSession(UpdateSessionRequest request);

    DeleteSessionResponse deleteUserSession(String username, String tenantId);
}
