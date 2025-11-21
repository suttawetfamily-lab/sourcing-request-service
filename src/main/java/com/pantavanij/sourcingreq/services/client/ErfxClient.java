package com.pantavanij.sourcingreq.services.client;

import com.pantavanij.sourcingreq.services.config.FeignClientConfig;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.interceptor.MethodExecuteTime;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "erfx-client", url = "${erfx.service.host}", configuration = FeignClientConfig.class)
public interface ErfxClient {

    String AUTH_TOKEN = "Authorization";

    @MethodExecuteTime
    @PostMapping(value = "/rest/erfx2/create/rfq", produces = "application/json")
    ERFXCreateResponse createErfx(@RequestHeader(AUTH_TOKEN) String bearerToken,
                                  @RequestBody ERFXCreateRequest request);

    @MethodExecuteTime
    @PostMapping(value = "/rest/erfx2/additional/bay", produces = "application/json")
    ERFXAdditionalDataResponse additionalDataErfx(@RequestHeader(AUTH_TOKEN) String bearerToken,
                                                  @RequestBody ERFXAdditionalDataRequest request);

//    @MethodExecuteTime
//    @PostMapping(value = "/rest/erfx2/bid-doc/bay", produces = "application/json")
//    ERFXBidDocResponse bidDocErfx(@RequestHeader(AUTH_TOKEN) String bearerToken,
//                                  @RequestBody ERFXBidDocRequest request);
//
//    @MethodExecuteTime
//    @PostMapping(value = "/rest/erfx2/requester-information/bay", produces = "application/json")
//    ERFXRequesterInformationResponse requesterInformationErfx(@RequestHeader(AUTH_TOKEN) String bearerToken,
//                                                              @RequestBody ERFXRequesterInformationRequest request);
//
//    @MethodExecuteTime
//    @PostMapping(value = "/rest/erfx2/questionnaire-section/bay", produces = "application/json")
//    ERFXQuestionnaireSectionResponse questionnaireSectionErfx(@RequestHeader(AUTH_TOKEN) String bearerToken,
//                                                  @RequestBody ERFXQuestionnaireSectionRequest request);

//    @MethodExecuteTime
//    @PostMapping(value = "/rest/erfx2/status", produces = "application/json")
//    ERFXStatusResponse getErfxStatus(@RequestHeader(AUTH_TOKEN) String bearerToken,
//                                     @RequestBody ERFXStatusRequest request);

    @MethodExecuteTime
    @GetMapping(value = "/rest/erfx2/shortlist/{file-url}", produces = "application/octet-stream")
    byte[] getAttachmentFromURL(@RequestHeader(AUTH_TOKEN) String bearerToken,
                                @PathVariable("file-url") String fileUrl);

}
