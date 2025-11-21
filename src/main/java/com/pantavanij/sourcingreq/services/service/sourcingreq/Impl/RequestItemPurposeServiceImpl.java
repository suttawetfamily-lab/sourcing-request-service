package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Purpose;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItem;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItemPurpose;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestItemPurposeKey;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.PurposeRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestItemPurposeRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestItemPurposeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RequestItemPurposeServiceImpl implements RequestItemPurposeService {

    private final RequestItemPurposeRepository requestItemPurposeRepository;

    private final PurposeRepository purposeRepository;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(Integer purposeId, RequestItem requestItem) {
        Optional<RequestItemPurpose> existingRequestItemPurpose =
                requestItemPurposeRepository.findTop1ByRequestItemId(requestItem.getRecId());

        if (existingRequestItemPurpose.isPresent()) {
            if (existingRequestItemPurpose.get().getPurpose().getRecId() == purposeId) return;
            requestItemPurposeRepository.delete(existingRequestItemPurpose.get());
        }

        if (purposeId == null) return;

        Purpose purpose = purposeRepository.findById(purposeId)
                .orElseThrow(() -> new BusinessException(ApiMessage.E7069, ApiMessage.E7069.description()));

        RequestItemPurpose requestPurpose = RequestItemPurpose.builder()
                .id(new RequestItemPurposeKey())
                .requestItem(requestItem)
                .purpose(purpose)
                .purposeCode(purpose.getCode())
                .purposeName(purpose.getName())
                .build();

        requestItemPurposeRepository.save(requestPurpose);
    }
}
