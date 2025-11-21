package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import org.springframework.data.domain.*;

import javax.validation.*;
import java.util.*;

public interface CurrencyService {
    List<OptionDto> getCurrencyByTenantIdAndSearchTerm(Integer tenantId, String searchTerm);
    List<OptionDto> getCurrencyByTenantId(Integer tenantId);
    CurrencyDto getCurrencyById(Integer currencyId);
    CurrencyMasterDto getCurrencyMasterDataById(Integer currencyId);
    List<CurrencyDto> getAllCurrency();
    CurrencySearchDto searchCurrencyListByCondition(CurrencySearchRequest request, Pageable pageable);
    Integer createCurrency(CurrencyMasterDataRequest request);
    Integer updateCurrency(@Valid CurrencyMasterDataRequest request);
    Integer deleteCurrencyById(Integer currencyId);
}
