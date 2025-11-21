package com.pantavanij.sourcingreq.services.client;

import com.pantavanij.sourcingreq.services.config.FeignClientConfig;
import com.pantavanij.sourcingreq.services.domain.request.DuplicateQuestionnaireRequest;
import com.pantavanij.sourcingreq.services.domain.request.EFormSearchRequest;
import com.pantavanij.sourcingreq.services.domain.response.eform.DuplicateQuestionnaireResponse;
import com.pantavanij.sourcingreq.services.domain.response.eform.EFormSearchResponse;
import com.pantavanij.sourcingreq.services.domain.response.eform.EFormViewResponse;
import com.pantavanij.sourcingreq.services.interceptor.MethodExecuteTime;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "eform-client", url = "${base.url.api.eform}", configuration = FeignClientConfig.class)
public interface EFormClient {

    String AUTH_TOKEN = "Authorization";
    @MethodExecuteTime
    @PostMapping(value = "/api/questionnaire-template/search",
            produces = "application/json")
    EFormSearchResponse searchQuestionnaire(@RequestHeader(AUTH_TOKEN) String bearerToken,
                                         @RequestBody EFormSearchRequest request);

    @MethodExecuteTime
    @GetMapping(value = "/api/form/{formId}",
            produces = "application/json")
    EFormViewResponse viewQuestionnaire(@RequestHeader(AUTH_TOKEN) String bearerToken,
                                        @PathVariable("formId") Long formId);

    @MethodExecuteTime
    @DeleteMapping(
            value = "/api/form/{formId}/delete",
            produces = "application/json"
    )
    EFormViewResponse deleteQuestionnaire(
            @RequestHeader(AUTH_TOKEN) String bearerToken,
            @PathVariable("formId") Long formId,
            @RequestParam(value = "isNotCheckStatusDraft", required = false) Boolean isNotCheckStatusDraft
    );

    @MethodExecuteTime
    @PostMapping(value = "/api/sourcing-request/eform/{formId}/duplicate",
            produces = "application/json")
    DuplicateQuestionnaireResponse duplicateSourcingRequestForm(@RequestHeader(AUTH_TOKEN) String bearerToken,
                                                                @PathVariable("formId") Long formId,
                                                                @RequestBody DuplicateQuestionnaireRequest request);
}
