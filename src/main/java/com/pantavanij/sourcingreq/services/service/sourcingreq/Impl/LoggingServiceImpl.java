package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.google.gson.Gson;
import com.pantavanij.sourcingreq.services.service.sourcingreq.LoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class LoggingServiceImpl implements LoggingService {

    Logger logger = LoggerFactory.getLogger(LoggingServiceImpl.class);

    @Override
    public void displayReq(HttpServletRequest request, Object body) {
        StringBuilder reqMessage = new StringBuilder();
        Map<String,String> parameters = getParameters(request);

        reqMessage.append("REQUEST ");
        reqMessage.append("method = [").append(request.getMethod()).append("]");
        reqMessage.append(" path = [").append(request.getRequestURI()).append("] ");

        if(!parameters.isEmpty()) {
            reqMessage.append(" parameters = [").append(parameters).append("] ");
        }

        if(!Objects.isNull(body)) {
            String jsonBody = new Gson().toJson(body);
            reqMessage.append(" body = [").append(jsonBody).append("]");
        }

        logger.info("log Request: {}", reqMessage);
    }

    private Map<String,String> getParameters(HttpServletRequest request) {
        Map<String,String> parameters = new HashMap<>();
        Enumeration<String> params = request.getParameterNames();
        while(params.hasMoreElements()) {
            String paramName = params.nextElement();
            String paramValue = request.getParameter(paramName);
            parameters.put(paramName,paramValue);
        }
        return parameters;
    }
}
