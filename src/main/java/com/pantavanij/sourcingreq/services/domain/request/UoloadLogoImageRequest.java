package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

@Data
public class UoloadLogoImageRequest {
    private String srLogoImageFileId;
    private String srLogoImageStyles;

    private String epLogoImageFileId;
    private String epLogoImageStyles;

    private String seLogoImageFileId;
    private String seLogoImageStyles;

    private String erfxLogoImageFileId;
    private String erfxLogoImageStyles;

    private String uamAdminLogoImageFileId;
    private String uamAdminLogoImageStyles;

    private String dashboardLogoImageFileId;
    private String dashboardLogoImageStyles;

    private String system;


}
