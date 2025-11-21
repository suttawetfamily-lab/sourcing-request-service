package com.pantavanij.sourcingreq.services.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.endpoint.version}")
public class RequestAttachmentController {

//    private final RequestAttachmentService requestAttachmentService;

//    @PreAuthorize("hasAnyAuthority('SQN','SQP')")
//    @DeleteMapping(value = "/request-attachment/{requestId}/{attachmentId}")
//    public ResponseEntity deleteRequestAttachment(@PathVariable Long requestId,@PathVariable Long attachmentId) {
//        boolean success = requestAttachmentService.deleteByRequestAndAttachment(requestId, attachmentId);
//        if(success) {
//            return new ResponseEntity<>(new ApiResponse(null), HttpStatus.OK);
//        }else {
//            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7013, ApiMessage.E7013.description()), HttpStatus.NOT_FOUND);
//        }
//    }

}
