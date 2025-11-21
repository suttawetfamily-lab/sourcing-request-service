package com.pantavanij.sourcingreq.services.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalHierarchyResponse {

    private int purchaserID;
    private String purLoginID;
    private int approverID;
    private String appLoginID;
    private int approveLimit;

}
