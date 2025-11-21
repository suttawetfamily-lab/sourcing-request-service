package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Currency;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItem;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItemCurrency;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestItemCurrencyKey;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.CurrencyRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestItemCurrencyRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestItemCurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RequestItemCurrencyServiceImpl implements RequestItemCurrencyService {

    private final RequestItemCurrencyRepository requestItemCurrencyRepository;

    private final CurrencyRepository currencyRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(Integer currencyId, RequestItem requestItem) {
        Optional<RequestItemCurrency> existingRequestItemCurrency =
                requestItemCurrencyRepository.findTop1ByRequestItemId(requestItem.getRecId());

        if (existingRequestItemCurrency.isPresent()) {
            if (existingRequestItemCurrency.get().getCurrency().getRecId() == currencyId) return;
            requestItemCurrencyRepository.delete(existingRequestItemCurrency.get());
        }

        if (currencyId == null) return;

        Currency currency = currencyRepository.findById(currencyId)
                .orElseThrow(() -> new BusinessException(ApiMessage.E7068, ApiMessage.E7068.description()));

        RequestItemCurrency requestCurrency = RequestItemCurrency.builder()
                .id(new RequestItemCurrencyKey())
                .requestItem(requestItem)
                .currency(currency)
                .currencyCode(currency.getCode())
                .currencyName(currency.getName())
                .build();

        requestItemCurrencyRepository.save(requestCurrency);
    }
}
