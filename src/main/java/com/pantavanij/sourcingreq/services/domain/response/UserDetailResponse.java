package com.pantavanij.sourcingreq.services.domain.response;

import lombok.Data;

import java.util.List;

@Data
public class UserDetailResponse {

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
    private List<String> borgIdList;
    private String mobilePhone;
    private String phone;
    private String department;
    private Integer homeBorgID;
    private String homeBorgName;
}