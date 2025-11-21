package com.pantavanij.sourcingreq.services.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
public class RequestItemAttachmentController {
//    private final RequestItemAttachmentService requestItemAttachmentService;

//    @DeleteMapping(value = "/request-item-attachment/{requestItemId}/{attachmentId}")
//    public ResponseEntity deleteRequestItemAttachment(@PathVariable Long requestItemId, @PathVariable Long attachmentId) {
//        boolean success = requestItemAttachmentService.deleteByRequestItemAndAttachment(requestItemId, attachmentId);
//        if (success) {
//            return new ResponseEntity<>(new ApiResponse(null), HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7014, ApiMessage.E7014.description()), HttpStatus.NOT_FOUND);
//        }
//    }
}
