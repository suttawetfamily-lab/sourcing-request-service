package com.pantavanij.sourcingreq.services.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pantavanij.sourcingreq.services.domain.response.ApiErrorResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    public static final Logger LOG = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exc) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            LOG.warn("User: {} attempted to access the protected URL: {}", auth.getName(), request.getRequestURI());
        }
        ApiErrorResponse errorResponse = new ApiErrorResponse(ApiMessage.E1005,
                String.format(ApiMessage.E1005.description(), " : No permission"));
        response.setStatus(HttpStatus.SC_FORBIDDEN);
        response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }
}
