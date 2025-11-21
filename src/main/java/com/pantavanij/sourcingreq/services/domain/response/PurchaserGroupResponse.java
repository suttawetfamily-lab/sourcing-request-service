package com.pantavanij.sourcingreq.services.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaserGroupResponse {
    private String isIT;
    private String category;
    private String subCategory;
    private int approveLimit;
    private int approverID;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String mobilePhone;
    private String mobile;
}
