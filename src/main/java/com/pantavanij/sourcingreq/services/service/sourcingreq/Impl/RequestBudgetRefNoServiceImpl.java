package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.BudgetRefNo;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestBudgetRefNo;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestBudgetRefNoKey;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.BudgetRefNoRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestBudgetRefNoRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestBudgetRefNoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RequestBudgetRefNoServiceImpl implements RequestBudgetRefNoService {

    private final RequestBudgetRefNoRepository requestBudgetRefNoRepository;

    private final BudgetRefNoRepository budgetRefNoIdRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(Integer budgetRefNoId, Request request) {
        Optional<RequestBudgetRefNo> existingRequestBudgetRefNo =
                requestBudgetRefNoRepository.findTop1ByRequestId(request.getRecId());

        if (existingRequestBudgetRefNo.isPresent()) {
            if (existingRequestBudgetRefNo.get().getBudgetRefNo().getRecId() == budgetRefNoId) return;
            requestBudgetRefNoRepository.delete(existingRequestBudgetRefNo.get());
        }

        if (budgetRefNoId == null) return;

        BudgetRefNo budgetRefNo = budgetRefNoIdRepository.findById(budgetRefNoId)
                .orElseThrow(() -> new BusinessException(ApiMessage.E7065, ApiMessage.E7065.description()));

        RequestBudgetRefNo requestBudgetRefNoId = RequestBudgetRefNo.builder()
                .id(new RequestBudgetRefNoKey())
                .request(request)
                .budgetRefNo(budgetRefNo)
                .budgetRefNoCode(budgetRefNo.getCode())
                .budgetRefNoName(budgetRefNo.getName())
                .build();

        requestBudgetRefNoRepository.save(requestBudgetRefNoId);
    }
}
