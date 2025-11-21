package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.request.DelegationActiveRequest;
import com.pantavanij.sourcingreq.services.domain.request.DelegatorByDelegateeRequest;
import com.pantavanij.sourcingreq.services.domain.response.DelegationActiveResponse;
import com.pantavanij.sourcingreq.services.domain.response.DelegatorByDelegateeResponse;

public interface DelegationService {

    DelegationActiveResponse getActiveDelegationByDeletatorAndDelegatee(DelegationActiveRequest request);
    DelegatorByDelegateeResponse getActiveDelegationByDeletatorAndDelegatee(DelegatorByDelegateeRequest request);

}
