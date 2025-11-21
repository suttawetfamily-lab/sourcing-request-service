package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestType;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestTypeKey;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Type;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestTypeRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TypeRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RequestTypeServiceImpl implements RequestTypeService {

    private final RequestTypeRepository requestTypeRepository;

    private final TypeRepository typeRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(Integer typeId, Request request) {
        Optional<RequestType> existingRequestType = requestTypeRepository.findTop1ByRequestId(request.getRecId());

        if (existingRequestType.isPresent()) {
            if (existingRequestType.get().getType().getRecId() == typeId) return;
            requestTypeRepository.delete(existingRequestType.get());
        }

        if (typeId == null) return;

        Type type = typeRepository.findById(typeId)
                .orElseThrow(() -> new BusinessException(ApiMessage.E7025, ApiMessage.E7025.description()));

        RequestType requestType = RequestType.builder()
                .id(new RequestTypeKey())
                .request(request)
                .type(type)
                .typeCode(type.getCode())
                .typeName(type.getName())
                .build();

        requestTypeRepository.save(requestType);
    }
}
