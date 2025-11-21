package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestItemPrRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestItemPrService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RequestItemPrServiceImpl implements RequestItemPrService {

    private final RequestItemPrRepository requestItemPrRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRequestItemPR(Long requestItemId, Integer prId) {
        requestItemPrRepository.saveRequestItemPr(requestItemId, prId);
    }

}
