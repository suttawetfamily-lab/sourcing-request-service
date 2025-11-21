package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
public class TenantEmailActivityController {

    private final TenantEmailActivityService tenantEmailActivityService;

    @GetMapping(value = "/tenant-email-activity")
    public ResponseEntity getTenantEmailActivity(
            @RequestParam ("emailActivityId") Long emailActivityId,
            @RequestParam ("activityId") Integer activityId
    ) {
        SendMailToRoleDto sendMailToRoleDto = tenantEmailActivityService.getByTenantIdAndEmailActivityIdAndActivityId(emailActivityId, activityId);
        if (sendMailToRoleDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(sendMailToRoleDto), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
