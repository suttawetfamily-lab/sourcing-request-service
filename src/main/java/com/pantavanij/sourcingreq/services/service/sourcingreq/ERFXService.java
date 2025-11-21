package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.ERFXBidDocResponse;

public interface ERFXService {

    boolean cancelERFX(Long erfxNo, ERFXCancelRequest eRFXCancelRequest);

    String createERFX(ERFXCreateRequest request, String authCode);

    boolean receiveERFX(String erfxNo, ERFXReceiveRequest request) throws Exception;

    boolean updateERFXInfo(ERFXUpdateInfoRequest erfxUpdateInfoRequest)  throws Exception;

    boolean updateERFXStatus(ERFXUpdateStatusRequest erfxUpdateStatusRequest)  throws Exception;

    String getCreateNewURL(String authCode);

    String getCheckStatusURL(String authCode);

    String getApproveShortlistURL(String authCode);
    String getDraftURL(String authCode, String erfxNum);

    ERFXBidDocResponse getBidDoc(String docNum);

    ERFXCreateRequest testZero(ERFXCreateRequest request, String authCode);

    ERFXAdditionalDataRequest test(ERFXCreateRequest request, String authCode);
}
