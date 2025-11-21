package com.pantavanij.sourcingreq.services.util;

import com.pantavanij.sourcingreq.services.domain.dto.ContractDetailClientDto;
import com.pantavanij.sourcingreq.services.domain.response.UserDetailResponse;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UaaService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class UserDetailServiceUtil {

    private static final Logger LOG = LoggerFactory.getLogger(UserDetailServiceUtil.class);
    private static UaaService uaaService;
    private static Map<String, UserDetailResponse> userDetailMap = new HashMap<>();
    @Autowired
    public UserDetailServiceUtil(UaaService uaaService){
        this.uaaService = uaaService;
    }

    public static String getFullName(String userName) {
        String fullName = "";
        ContractDetailClientDto userDetail = uaaService.getContractDetail(AppUtil.getTenantId(), AppUtil.getIdp(), userName, userDetailMap);

        if(userDetail != null) {
            String firstName = userDetail.getFirstName();
            String lastName = StringUtils.isBlank(userDetail.getLastName()) ? "" : " " + userDetail.getLastName();
            fullName = firstName + lastName;
        }
        return fullName;
    }

}

