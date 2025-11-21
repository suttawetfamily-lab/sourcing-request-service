package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailDto {

    private String userId;
    private String username;
    private String firstName;
    private String lastName;
    private String firstNameLocal;
    private String lastNameLocal;
    private String email;
    private String companyId;
    private String companyName;
    private String companyTaxId;
    private String companyBranch;
    private String companyCountryCode;
    private String timeZone;

    public UserDetailDto(Map userDetail){
        this.userId = (String) userDetail.get("userId");
        this.username = (String) userDetail.get("username");
        this.firstName =  userDetail.get("firstName") ==null?"":(String)userDetail.get("firstName") ;
        this.lastName = userDetail.get("lastName") ==null?"":(String) userDetail.get("lastName");
        this.firstNameLocal =  userDetail.get("firstNameLocal") ==null?"":(String) userDetail.get("firstNameLocal");
        this.lastNameLocal = userDetail.get("lastNameLocal") ==null?"":(String) userDetail.get("lastNameLocal");
        this.email = (String) userDetail.get("email");
        this.companyId = (String) userDetail.get("companyId");
        this.companyName = (String) userDetail.get("companyName");
        this.companyTaxId = (String) userDetail.get("companyTaxId");
        this.companyBranch = (String) userDetail.get("companyBranch");
        this.companyCountryCode = (String) userDetail.get("companyCountryCode");
        this.timeZone = (String) userDetail.get("timeZone");
    }


    public void setTimeZone(String timeZone) {
        if(timeZone == null || "".equals(timeZone)){
            this.timeZone = "GMT+7";
        } else {
            this.timeZone = timeZone;
        }
    }
}
