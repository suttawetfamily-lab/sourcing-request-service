package com.pantavanij.sourcingreq.services.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pantavanij.sourcingreq.services.domain.dto.UserDto;
import com.pantavanij.sourcingreq.services.domain.response.ApiErrorResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.util.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger LOG = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenUtil jwtTokenUtil;
    private final TenantService tenantService;


    @Value("${basic.auth.username}")
    private String username;

    @Value("${basic.auth.password}")
    private String password;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final HttpServletRequest req = (HttpServletRequest) request;
        final HttpServletResponse res = (HttpServletResponse) response;

        if (checkExceptURL(req)) {
            chain.doFilter(req, res);
            return;
        }

        String requestTokenHeader = request.getHeader("Authorization");
        UserDto userDto = null;
        ApiErrorResponse errorResponse = null;
        ApiErrorResponse errorResponseWhiteListIPAddress = null;
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            String jwtToken = requestTokenHeader.substring(7);
            request.setAttribute("jwtToken", jwtToken);
            try {
                userDto = jwtTokenUtil.parseToken(jwtToken);
                MDC.put("tenantId", userDto.getTenantId());
            } catch (ExpiredJwtException e) {
                errorResponse = new ApiErrorResponse(ApiMessage.E1004,
                        String.format(ApiMessage.E1004.description(), " : JWT Token is expired"));
                LOG.error(errorResponse.getStatus().toString());
                LOG.error(e.getMessage(), e);
            } catch (SignatureException | IllegalArgumentException e) {
                String msg = String.format(ApiMessage.E1004.description(),
                        " : JWT Token is invalid") + "(" + e.getMessage() + ")";
                errorResponse = new ApiErrorResponse(ApiMessage.E1004, msg);
                LOG.error(errorResponse.getStatus().toString());
                LOG.error(e.getMessage(), e);
            }
        } else if (requestTokenHeader != null && requestTokenHeader.startsWith("Basic ")) {
            String base64Credentials = requestTokenHeader.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            final String[] values = credentials.split(":", 2);
            if (username.equals(values[0]) && password.equals(values[1])) {
                userDto = new UserDto();
                userDto.setUsername(values[0]);
                String tenantId = request.getHeader("tenantId");
                if (!StringUtils.isBlank(tenantId)) {
                    userDto.setTenantId(tenantId);
                    MDC.put("tenantId", tenantId);
                }
            } else {
                errorResponse = new ApiErrorResponse(ApiMessage.E1004, "Unauthorized : Invalid credential");
                LOG.error("Unauthorized : Invalid credential {}", credentials);
            }
        }

        if (userDto != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(userDto, null, userDto.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        if (errorResponseWhiteListIPAddress != null) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(errorResponseWhiteListIPAddress));
        } else if (errorResponse != null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(errorResponse));
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean checkExceptURL(HttpServletRequest request) {
        String[] excepts = new String[]{
                "/api/sourcingreq/v1/send-email-submit-due-date",
                "/api/sourcingreq/v1/send-email-awaiting-approval",
                "/api/sourcingreq/v1/send-email-completed",
                "/api/sourcingreq/v1/send-email-rejected",
        };

        return Arrays.stream(excepts).anyMatch(request.getRequestURI()::contains);
    }
}