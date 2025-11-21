package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.ContractDetailClientDto;
import com.pantavanij.sourcingreq.services.domain.dto.ExcSourcingDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequestDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SupplierMailingLog;
import com.pantavanij.sourcingreq.services.domain.request.SendExcSourcingEMailRequest;
import com.pantavanij.sourcingreq.services.domain.response.EmailResponse;
import com.pantavanij.sourcingreq.services.domain.request.SendEMailRequest;

import java.util.List;

public interface EmailService {
    List<EmailResponse> sendEMailNotification(SendEMailRequest sendEMailRequest ,RequestDto requestDto);

    List<EmailResponse> sendExcSourcingEMailNotification(SendExcSourcingEMailRequest sendExcSourcingEMailRequest, RequestDto requestDto, ExcSourcingDto excSourcingDto);

    List<EmailResponse> sendSupplierEMailNotification(SendEMailRequest sendEMailRequest, SupplierMailingLog supplierMailingLog);

    boolean isLastApprover(RequestDto requestDto);

//    List<EmailResponse> sendEMailNotificationDelegate(SendEMailRequest sendEMailRequest, ContractDetailClientDto delegatorDetail, ContractDetailClientDto delegateeDetail, String delegationPeriod, String delegationCancelReason, String delegationCancelDateTime);
}
