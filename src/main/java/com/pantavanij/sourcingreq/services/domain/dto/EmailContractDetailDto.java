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
public class EmailContractDetailDto {
    private ContractDetailClientDto requester;
    private List<ContractDetailClientDto> purchaserList;
    private List<ContractDetailClientDto> reviewerList;
    private List<ContractDetailClientDto> reportLineList;
    private List<ContractDetailClientDto> deptApproverList;
}
