package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.ApiErrorResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponseStatus;
import com.pantavanij.sourcingreq.services.domain.response.ERFXBidDocResponse;
import com.pantavanij.sourcingreq.services.enums.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.ERFXService;
import com.pantavanij.sourcingreq.services.util.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.endpoint.version}")
public class ERFXController {
    private final ERFXService eRFXService;

    @PatchMapping(value = "/api/erfx/{erfxNo}/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity cancelERFX(@PathVariable Long erfxNo, @RequestBody ERFXCancelRequest eRFXCancelRequest) {
//        logger.info("cancelERFX erfxNo : {} Reason : {} ", erfxNo, eRFXCancelRequest.getReason());
        boolean erfxStatusResponse = eRFXService.cancelERFX(erfxNo, eRFXCancelRequest);
        if (erfxStatusResponse) {
            return new ResponseEntity<>(new ApiResponse(null), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('SQP')")
    @PostMapping(value = "/erfx/{authCode}/url", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createERFX(@PathVariable String authCode, @RequestBody ERFXCreateRequest eRFXCreateRequest) {
        int attachmentSize = (int) eRFXCreateRequest.getAttachments().stream()
                .filter(attachmentDto -> attachmentDto != null &&
                        (attachmentDto.getFlag() == null || !attachmentDto.getFlag().equalsIgnoreCase(AttachmentFlag.DDNO.code())))
                .count();

        if(((eRFXCreateRequest.getItems().size() + 1) * Constant.MAX_ATTACHMENTS_PER_REQ_REQITEM) < attachmentSize) {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7083, ApiMessage.E7083.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            String redirectURL = eRFXService.createERFX(eRFXCreateRequest, authCode);
            if (redirectURL == null) {
                return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7082, ApiMessage.E7082.description()), HttpStatus.METHOD_NOT_ALLOWED);
            } else if (!redirectURL.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(redirectURL), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @PostMapping(value = "/erfx/{authCode}/test-zero", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity testZero(@PathVariable String authCode, @RequestBody ERFXCreateRequest eRFXCreateRequest) {
//        logger.info("createERFX RequestNo : {} ", eRFXCreateRequest.getDocNum());
        ERFXCreateRequest response = eRFXService.testZero(eRFXCreateRequest, authCode);
        if (response != null) {
            return new ResponseEntity<>(new ApiResponse(response), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/erfx/{authCode}/test", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity test(@PathVariable String authCode, @RequestBody ERFXCreateRequest eRFXCreateRequest) {
        ERFXAdditionalDataRequest response = eRFXService.test(eRFXCreateRequest, authCode);
        if (response != null) {
            return new ResponseEntity<>(new ApiResponse(response), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/api/erfx/{erfxNo}/receive", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity receiveERFX(@PathVariable String erfxNo, @Valid @RequestBody ERFXReceiveRequest erfxReceiveRequest) throws Exception {
        boolean isReceived = eRFXService.receiveERFX(erfxNo, erfxReceiveRequest);
        if (isReceived) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.I1001, ApiMessage.I1001.description()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/api/erfx/update-info", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateERFXInfo(@Valid @RequestBody ERFXUpdateInfoRequest erfxUpdateInfoRequest) throws Exception {
        boolean isUpdated = eRFXService.updateERFXInfo(erfxUpdateInfoRequest);
        if (isUpdated) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.I1001, ApiMessage.I1001.description()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/api/erfx/update-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateERFXStatus(@Valid @RequestBody ERFXUpdateStatusRequest erfxUpdateStatusRequest) throws Exception {
        boolean isUpdated = eRFXService.updateERFXStatus(erfxUpdateStatusRequest);
        if (isUpdated) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.I1001, ApiMessage.I1001.description()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('XNX')")
    @GetMapping(value = "/erfx/{authCode}/url", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getCreateNewURL(@PathVariable String authCode) {
        String redirectURL = eRFXService.getCreateNewURL(authCode);
        if (!redirectURL.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(redirectURL), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('XCS')")
    @GetMapping(value = "/erfx/{authCode}/status-url", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getCheckStatusURL(@PathVariable String authCode) {
        String redirectURL = eRFXService.getCheckStatusURL(authCode);
        if (!redirectURL.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(redirectURL), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('ERF')")
    @GetMapping(value = "/erfx/{authCode}/shortlist-url", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getApproveShortlistURL(@PathVariable String authCode) {
        String redirectURL = eRFXService.getApproveShortlistURL(authCode);
        if (!redirectURL.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(redirectURL), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/erfx/bid-doc/{docNum}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getBidDoc(@PathVariable String docNum) {
//        logger.info("getBidDoc RequestNo : {} ", docNum);
        ERFXBidDocResponse eRFXBidDocResponse = eRFXService.getBidDoc(docNum);
        if (eRFXBidDocResponse != null) {
            return new ResponseEntity<>(new ApiResponse(eRFXBidDocResponse), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
