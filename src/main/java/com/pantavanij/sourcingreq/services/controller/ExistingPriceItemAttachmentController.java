package com.pantavanij.sourcingreq.services.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.endpoint.version}")
public class ExistingPriceItemAttachmentController {
//    private final ExistingPriceItemAttachmentService existingPriceItemAttachmentService;

//    @PostMapping(value = "/existing-price-item-attachment", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity addExistingPriceItem(@Valid @RequestBody ExistingPriceItemAttachmentRequest existingPriceItemAttachmentRequest) {
//        ExistingPriceItemAttachment existingPriceItemAttachment = existingPriceItemAttachmentService.saveExistingPriceItemAttachment(existingPriceItemAttachmentRequest);
//        if (existingPriceItemAttachment != null) {
//            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.I1001, ApiMessage.I1001.description()), HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7020, ApiMessage.E7020.description()), HttpStatus.NOT_FOUND);
//        }
//    }

//    @DeleteMapping(value = "/existing-price-item-attachment/{existingPriceItemId}/{attachmentId}/")
//    public ResponseEntity deleteExistingPriceAttachment(@PathVariable Long existingPriceItemId, @PathVariable Long attachmentId) {
//        boolean success = existingPriceItemAttachmentService.deleteByExistingPriceItemAndAttachment(existingPriceItemId, attachmentId);
//        if (success)
//            return new ResponseEntity<>(new ApiResponse(null), HttpStatus.OK);
//        else
//            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7015, ApiMessage.E7015.description()), HttpStatus.NOT_FOUND);
//    }
}
