package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.DelegateClient;
import com.pantavanij.sourcingreq.services.domain.request.DelegationActiveRequest;
import com.pantavanij.sourcingreq.services.domain.request.DelegatorByDelegateeRequest;
import com.pantavanij.sourcingreq.services.domain.response.DelegationActiveResponse;
import com.pantavanij.sourcingreq.services.domain.response.DelegatorByDelegateeResponse;
import com.pantavanij.sourcingreq.services.service.sourcingreq.DelegationService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DelegationServiceImpl implements DelegationService {

    private final DelegateClient delegateClient;

    @Override
    public DelegationActiveResponse getActiveDelegationByDeletatorAndDelegatee(DelegationActiveRequest request) {
        String authHeader = "Bearer " + AppUtil.getJwtToken();
        return delegateClient.getActiveDelegationByDeletatorAndDelegatee(authHeader, request);
    }

    @Override
    public DelegatorByDelegateeResponse getActiveDelegationByDeletatorAndDelegatee(DelegatorByDelegateeRequest request) {
        String authHeader = "Bearer " + AppUtil.getJwtToken();
        return delegateClient.getDelegatorByDelegatee(authHeader, request);
    }
}
