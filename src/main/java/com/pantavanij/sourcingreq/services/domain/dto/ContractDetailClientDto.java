package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ContractDetailClientDto {
    private String userId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String mobilePhone;
    private String mobile;
    private String department;
    private Integer homeBorgID;
    private String homeBorgName;
    private List<String> borgIdList;
    private boolean isRequester;
    private boolean isPurchaser;
    private boolean isReviewer;
    private boolean isReportLine;
}
