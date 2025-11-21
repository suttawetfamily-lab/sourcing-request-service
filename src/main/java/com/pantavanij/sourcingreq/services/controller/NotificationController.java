package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/notification/test")
    public ResponseEntity test() {
        return ResponseEntity.ok(new ApiResponse<>("Hello, I'm from notification."));
    }

    @PreAuthorize("hasAnyAuthority('SQA', 'SQP')")
    @GetMapping(value = "/notification/dept-approver/task/count")
    public ResponseEntity countDepApproveTask() {
        Integer count = notificationService.getCountApproverTask();
        if (count != -1) {
            return ResponseEntity.ok(new ApiResponse<>(count));
        }
        return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasAnyAuthority('SQV', 'SRL')")
    @GetMapping(value = "/notification/reviewer/task/count")
    public ResponseEntity countReviewerTask() {
        Integer count = notificationService.getCountReviewerTask();
        if (count != -1) {
            return ResponseEntity.ok(new ApiResponse<>(count));
        }
        return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
