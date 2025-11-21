package com.pantavanij.sourcingreq.services.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pantavanij.sourcingreq.services.domain.response.ApiErrorResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    private static final long serialVersionUID = -7858869558953243875L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException ex) throws IOException {
        ApiErrorResponse errorResponse = new ApiErrorResponse(ApiMessage.E1004,
                String.format(ApiMessage.E1004.description(), " : " + ex.getMessage()));
        String requestTokenHeader = request.getHeader("Authorization");
        if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
            errorResponse = new ApiErrorResponse(ApiMessage.E1004,
                    String.format(ApiMessage.E1004.description(), " : No JWT Token"));
        }
        LOG.error(errorResponse.getStatus().toString());
        LOG.error(ex.getMessage(), ex);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }
}