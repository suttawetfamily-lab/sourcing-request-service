package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import org.springframework.data.domain.*;

import javax.validation.*;
import java.util.List;

public interface TenantCurrencyService {
    TenantCurrencyDto getByCurrencyId(Integer currencyId);
    Integer createTenantCurrency(CurrencyRequest request);
    Integer updateTenantCurrency(CurrencyRequest request);
    TenantCurrencySearchDto searchTenantCurrencyByCondition(@Valid TenantCurrencySearchRequest request, Pageable pageable);
    TenantCurrencyDto updateTenantCurrencySequence(SequenceRequest request);

    List<OptionDto> getTenantCurrencyByTenantIdAndSearchTerm(Integer tenantId, String searchTerm);
    Integer deleteTenantCurrencyById(Integer currencyId);
    List<OptionDto> getTenantCurrencyByTenantIdAndOrganizationIdAndSearchTerm(
            Integer tenantId, Integer organizationId, String searchTerm);
}
