package com.pantavanij.sourcingreq.services.domain.response.user;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pantavanij.sourcingreq.services.domain.PrivilegeScope;
import com.pantavanij.sourcingreq.services.exception.AppException;
import com.pantavanij.sourcingreq.services.util.HTTPUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsersAuthenResponse extends ApiResponse {

    private static final Logger logger = LoggerFactory.getLogger(UsersAuthenResponse.class);

    private String username;
    private String password;
    private String roleName;
    private String tenantId;
    private String idp;
    private List<PrivilegeScope> privilegeScope;
    private String homeURL;
    private String logoutURL;

    public UsersAuthenResponse(String username
            , String password
            , String tenantId
            , String idp
            , List <PrivilegeScope> privilegeScope
            , String homeURL
            , String logoutURL){

        this.username = username;
        this.password = password;
        this.tenantId = tenantId;
        this.idp = idp;
        this.privilegeScope = privilegeScope !=null && privilegeScope.size()>0
                ?privilegeScope:null;
        this.homeURL = homeURL;
        this.logoutURL =logoutURL;
    }

    public UsersAuthenResponse(Map claimMap, String... idp){

        try{

            if(claimMap.get("username") == null){
                if(claimMap.get("preferred_username") == null){
                    this.username  = claimMap.get("userName").toString();
                } else {
                    this.username  = claimMap.get("preferred_username").toString();
                }
            } else {
                this.username  = claimMap.get("username").toString();
            }
            this.tenantId  = claimMap.get("tenantId") == null? "": claimMap.get("tenantId").toString();

            //Check if Idp is null
            if(idp == null) {
                logger.error("Idp is null");
                throw new AppException("Idp is null");
            }

            this.idp  = idp[0];
            this.roleName = claimMap.get("roleName") == null? "": claimMap.get("roleName").toString();
            this.privilegeScope = claimMap.get("privilegeScope") == null? new ArrayList<>(): HTTPUtils.cast(claimMap.get("privilegeScope"));
            this.homeURL = claimMap.get("homeURL") == null? "": claimMap.get("homeURL").toString();
            this.logoutURL = claimMap.get("logoutURL") == null? "": claimMap.get("logoutURL").toString();
        } catch( NumberFormatException ex){
            logger.error("UsersAuthenResponse constructor : "+ ex.toString());
            throw new AppException(ex.toString());
        }
    }

    public void setPrivilegeScope(Object obj) {
        List<HashMap> map  = HTTPUtils.cast(obj);

        if(map ==null){
            this.privilegeScope = new ArrayList<>();
            return;
        }
        this.privilegeScope = map.stream().map(
                element ->{
                    if (element.get("scope") instanceof Integer) {
                        return new PrivilegeScope(
                                (String)element.get("privilegeCode"),
                                String.valueOf(element.get("scope"))
                        );
                    } else {
                        return new PrivilegeScope(
                                (String)element.get("privilegeCode"),
                                (String)element.get("scope"));
                    }
                }
        ).collect(Collectors.toList());

    }
}
