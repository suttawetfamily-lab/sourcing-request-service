package com.pantavanij.sourcingreq.services.service.sourcingreq;

import javax.servlet.http.HttpServletRequest;

public interface LoggingService {

    void displayReq(HttpServletRequest request, Object body);

//    void displayResp(HttpServletRequest request, HttpServletResponse response, Object body);
}
