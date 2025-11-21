package com.pantavanij.sourcingreq.services.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private String tenantId;
    private String username;
    private String fullName;
    private String email;
    private Set<String> userRoles;
    private List<String> privilegeCode;
    private Map<String, String> privilegeScope;
    private List<GrantedAuthority> authorities;
    private String idp;

    public UserDto() {

    }

    public UserDto(String username, String fullName, String email,Set<String> userRoles) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.userRoles = userRoles;
    }

    public UserDto(
            String tenantId,
            String username,
            Set<String> userRoles,
            List<String> privilegeCode,
            Map<String, String> privilegeScope,
            List<GrantedAuthority> authorities,
            String idp) {
        this.tenantId = tenantId;
        this.username = username;
        this.userRoles = userRoles;
        this.privilegeCode = privilegeCode;
        this.privilegeScope = privilegeScope;
        this.authorities = authorities;
        this.idp = idp;
    }
}
