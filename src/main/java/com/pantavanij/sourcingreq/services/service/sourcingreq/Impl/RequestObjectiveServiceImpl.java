package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Objective;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestObjective;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestObjectiveKey;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.ObjectiveRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestObjectiveRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestObjectiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RequestObjectiveServiceImpl implements RequestObjectiveService {

    private final RequestObjectiveRepository requestObjectiveRepository;

    private final ObjectiveRepository objectiveRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(Integer objectiveId, Request request) {
        Optional<RequestObjective> existingRequestObjective = requestObjectiveRepository.findTop1ByRequestId(request.getRecId());

        if (existingRequestObjective.isPresent()) {
            if (existingRequestObjective.get().getObjective().getRecId() == objectiveId) return;
            requestObjectiveRepository.delete(existingRequestObjective.get());
        }

        if (objectiveId == null) return;

        Objective objective = objectiveRepository.findById(objectiveId)
                .orElseThrow(() -> new BusinessException(ApiMessage.E7065, ApiMessage.E7065.description()));

        RequestObjective requestObjective = RequestObjective.builder()
                .id(new RequestObjectiveKey())
                .request(request)
                .objective(objective)
                .objectiveCode(objective.getCode())
                .objectiveName(objective.getName())
                .build();

        requestObjectiveRepository.save(requestObjective);
    }
}
