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
public class EmailExcSourcingContractDetailDto {

    private ContractDetailClientDto purchaser;
    private List<ContractDetailClientDto> excSourcingDeptApproverList;
    private List<ContractDetailClientDto> excSourcingPurchasingApproverList;
}
