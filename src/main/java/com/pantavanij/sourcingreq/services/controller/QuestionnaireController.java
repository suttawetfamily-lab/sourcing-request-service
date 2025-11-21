package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.projection.RequestIdStatusProjection;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponseStatus;
import com.pantavanij.sourcingreq.services.domain.response.eform.DuplicateQuestionnaireResponse;
import com.pantavanij.sourcingreq.services.domain.response.eform.EFormSearchResponse;
import com.pantavanij.sourcingreq.services.domain.response.eform.EFormViewResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.AppException;
import com.pantavanij.sourcingreq.services.service.sourcingreq.EFormService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestQuestionnaireService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("${api.endpoint.version}")
public class QuestionnaireController {
    private final EFormService eFormService;
    private final RequestQuestionnaireService requestQuestionnaireService;

    @PostMapping(value = "/questionnaire/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EFormSearchResponse> searchQuestionnaire(@Valid @RequestBody EFormSearchRequest request) {
        EFormSearchResponse response = eFormService.searchQuestionnaireTemplate(request);
        return ResponseEntity.ok(response);
    }

//    @PreAuthorize("hasAnyAuthority('SEF')")
    @GetMapping(value = "/questionnaire/view/{formId}")
    public ResponseEntity<EFormViewResponse> viewQuestionnaire(@RequestParam(required = false) Long requestId, @PathVariable Long formId, @RequestParam(defaultValue = "0") Integer isDeSelect) {
        EFormViewResponse response = requestQuestionnaireService.getEFormView(formId, requestId, isDeSelect);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('SEF')")
    @PostMapping(value = "/questionnaire/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<?>> saveRequestQuestionnaire(@Valid @RequestBody RequestQuestionnaireRequest req) throws CloneNotSupportedException{
        Long result = requestQuestionnaireService.save(req);
        if (result != null) {
            if (result != -1) {
                return ResponseEntity.ok(new ApiResponse<>(result));
            }
            return new ResponseEntity(new ApiResponse<>(null), HttpStatus.OK);
        } else {
            return new ResponseEntity(new ApiResponseStatus(ApiMessage.E7082, ApiMessage.E7082.description()), HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    @PreAuthorize("hasAnyAuthority('SEF')")
    @PostMapping(value = "/request/{requestId}/questionnaire/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> updateThisQuestionnaire(@PathVariable Long requestId, @RequestBody EFormSaveRequest eFormSaveRequest) {
        requestQuestionnaireService.updateThisQuestionnaire(eFormSaveRequest, requestId);
        return ResponseEntity.ok(new ApiResponse<>(null, new ApiResponseStatus(ApiMessage.I1002)));
    }

    @PostMapping(value = "/request-questionnaire/request-id/{requestId}/eform-id", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> updateRequestQuestionnaireFormId(@PathVariable Long requestId, @RequestBody UpdateFormIdRequest request) {
        requestQuestionnaireService.updateRequestQuestionnaireFormId(requestId, request);
        return ResponseEntity.ok(new ApiResponse<>(null, new ApiResponseStatus(ApiMessage.I1003)));
    }

    @GetMapping(value = "/request-questionnaire/request-id/{requestId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Long>> getFormIdByRequestId(@PathVariable Long requestId) {
        try {
            Long formId = requestQuestionnaireService.getFormIdByRequestId(requestId);
            return ResponseEntity.ok(new ApiResponse<>(formId, new ApiResponseStatus(ApiMessage.I1001)));
        } catch (AppException ex) {
            ApiResponseStatus status = new ApiResponseStatus(ex.getApiMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(null, status));
        }
    }

    @PostMapping(value = "/eform/{formId}/duplicate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Long>> duplicateSourcingRequestForm(
            @PathVariable Long formId,
            @RequestBody DuplicateQuestionnaireRequest request) {

        try {
            DuplicateQuestionnaireResponse response =
                    eFormService.duplicateSourcingRequestForm(formId, request.getRequestId());

            Long newFormId = response != null ? response.getData() : null;


            if (newFormId == null) {
                ApiResponseStatus errorStatus = new ApiResponseStatus(ApiMessage.E7082);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(null, errorStatus));
            }

            return ResponseEntity.ok(new ApiResponse<>(newFormId, new ApiResponseStatus(ApiMessage.I1002)));

        } catch (Exception ex) {
            log.error("Failed to duplicate sourcing request form: {}", ex.getMessage(), ex);

            ApiResponseStatus errorStatus = new ApiResponseStatus(ApiMessage.E7082, ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(null, errorStatus));
        }
    }


    @PostMapping("/requests-by-form/{formId}/ids")
    public ResponseEntity<ApiResponse<List<RequestIdStatusProjection>>> getRequestIdsByFormIdNative(@PathVariable Long formId, @RequestBody GetRequestIdsByFormIdRequest request) {
        try {
            List<RequestIdStatusProjection> ids = requestQuestionnaireService.getRequestIdsByFormIdWithStatus(formId, request.getNotAllowedStatus());

            if (ids == null || ids.isEmpty()) {
                ApiResponseStatus status = new ApiResponseStatus(ApiMessage.E7010, "No request found for this formId.");
                return ResponseEntity.ok(new ApiResponse<>(ids, status));
            }

            return ResponseEntity.ok(new ApiResponse<>(ids, new ApiResponseStatus(ApiMessage.I1002)));

        } catch (Exception ex) {
            ApiResponseStatus errorStatus = new ApiResponseStatus(ApiMessage.E1001, ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(null, errorStatus));
        }
    }

    @GetMapping("/form/{formId}/in-use")
    public ResponseEntity<ApiResponse<Boolean>> isFormInUse(@PathVariable Long formId) {
        try {
            boolean isInUse = requestQuestionnaireService.isFormInUse(formId);
            return ResponseEntity.ok(new ApiResponse<>(isInUse, new ApiResponseStatus(ApiMessage.I1001)));
        } catch (Exception ex) {
            log.error("Error checking form usage: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, new ApiResponseStatus(ApiMessage.E1001, ex.getMessage())));
        }
    }


}
