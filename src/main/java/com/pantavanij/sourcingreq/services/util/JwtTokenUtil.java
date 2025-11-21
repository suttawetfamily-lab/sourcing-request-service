package com.pantavanij.sourcingreq.services.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pantavanij.sourcingreq.services.domain.dto.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class JwtTokenUtil implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    public UserDto parseToken(String token) {
        Claims body = this.decodeTokenClaims(token);
        List<GrantedAuthority> authorities = new ArrayList<>();
        Object tenantId = body.get("tenantId");
        Object username = body.get("username");
        Object objPrivilegeScopes = body.get("privilegeScope");
        Object idp = body.get("idp");
        Map<String, String> privilegeScope = this.parsePrivilegeScope(objPrivilegeScopes);
        privilegeScope.forEach((key, value) -> authorities.add(new SimpleGrantedAuthority(key.toUpperCase())));
        if (tenantId == null || "".equals(((String) tenantId).trim())) {
            throw new IllegalArgumentException("JWT Token field tenantId must not be empty");
        }
        if (username == null || "".equals(((String) username).trim())) {
            throw new IllegalArgumentException("JWT Token field username must not be empty");
        }
        if (body.getExpiration() == null) {
            throw new IllegalArgumentException("JWT Token expiration must not be null");
        }
        return new UserDto(
                (String) tenantId,
                (String) username,
                new HashSet<>(),
                null,
                privilegeScope,
                authorities,
                (String) idp
        );
    }

    public Claims decodeTokenClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(jwtSecret.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public List<String> parsePrivilegeCode(Object objPrivilegeCodes) {
        List<String> privilegeCodes = new ArrayList<>();
        if (objPrivilegeCodes != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonPrivilegeCodes = objectMapper.writeValueAsString(objPrivilegeCodes);
                privilegeCodes = objectMapper.readValue(jsonPrivilegeCodes, new TypeReference<List<String>>() {
                });
            } catch (JsonProcessingException e) {
                LOG.warn(e.getMessage(), e);
            }
        }
        privilegeCodes.replaceAll(String::toUpperCase);
        return privilegeCodes;
    }

    public Map<String, String> parsePrivilegeScope(Object objPrivilegeScopes) {
        List<PrivilegeScope> privilegeScopes = new ArrayList<>();
        if (objPrivilegeScopes != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonPrivilegeCodes = objectMapper.writeValueAsString(objPrivilegeScopes);
                privilegeScopes = objectMapper.readValue(jsonPrivilegeCodes, new TypeReference<List<PrivilegeScope>>() {
                });
            } catch (JsonProcessingException e) {
                LOG.warn(e.getMessage(), e);
            }
        }
        Map<String, String> map = new TreeMap<>();
        if (!privilegeScopes.isEmpty()) {
            for (PrivilegeScope scope : privilegeScopes) {
                if(scope.getPrivilegeCode() != null)
                    map.put(scope.getPrivilegeCode().toUpperCase(), scope.getScope());
            }
        }
        return map;
    }
}

@Data
class PrivilegeScope {

    String privilegeCode;
    String scope;

    PrivilegeScope() {
    }

    PrivilegeScope(String privilegeCode, String scope) {
        this.privilegeCode = privilegeCode;
        this.scope = scope;
    }
}