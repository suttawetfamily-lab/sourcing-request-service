package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Currency;
import com.pantavanij.sourcingreq.services.domain.mapper.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

import javax.persistence.criteria.*;
import java.util.*;

import static com.pantavanij.sourcingreq.services.enums.SearchApproverType.CREATED_BY;
import static com.pantavanij.sourcingreq.services.util.Constant.REC_ID;
import static com.pantavanij.sourcingreq.services.util.Constant.TENANT;

@RequiredArgsConstructor
@Service
public class CurrencyServiceImpl implements CurrencyService {
    private final CurrencyRepository currencyRepository;
    private final TenantCurrencyRepository tenantCurrencyRepository;
    private final TenantService tenantService;
    private final UaaService uaaService;

    public List<OptionDto> getCurrencyByTenantIdAndSearchTerm(Integer tenantId, String searchTerm) {
        List<Currency> currencyList = currencyRepository.findByTenantIdAndCodeOrDescription(tenantId, searchTerm.trim());
        return CurrencyMapper.INSTANCE.toCurrencyOptionDto(currencyList);
    }

    @Override
    public List<OptionDto> getCurrencyByTenantId(Integer tenantId) {
        List<Currency> currencyList = currencyRepository.findByTenantId(tenantId);
        return CurrencyMapper.INSTANCE.toCurrencyOptionDto(currencyList);
    }

    @Override
    public CurrencyDto getCurrencyById(Integer currencyId) {
        try {
            Optional<Currency> currencyOpt = currencyRepository.findByRecId(currencyId);
            return currencyOpt.map(CurrencyMapper.INSTANCE::currencyToCurrencyDto).orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public CurrencyMasterDto getCurrencyMasterDataById(Integer currencyId) {
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        try {
            Optional<Currency> currencyOpt = currencyRepository.findByRecId(currencyId);
            return currencyOpt.map(currency -> CurrencyMapper.INSTANCE.toCurrencyMasterDto(currency, timeZone)).orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<CurrencyDto> getAllCurrency() {
        List<CurrencyDto> currencyDtoList = new ArrayList<>();
        try {
            List<Currency> currencyList = currencyRepository.findAll();
            if (!currencyList.isEmpty()) {
                currencyList.forEach(currency -> {
                    CurrencyDto currencyDto = CurrencyMapper.INSTANCE.currencyToCurrencyDto(currency);
                    currencyDtoList.add(currencyDto);
                });
            }
            return currencyDtoList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public CurrencySearchDto searchCurrencyListByCondition(CurrencySearchRequest request, Pageable pageable) {
        Page<Currency> currencyPage = currencyRepository.findAll(Specification.where(searchCurrencySpecificationByCondition(request)), pageable);
        int totalPage = currencyPage.getTotalPages();
        long total = currencyPage.getTotalElements();

        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        List<CurrencyMasterDto> currencyMasterDataList = CurrencyMapper.INSTANCE.toCurrencyMasterDto(currencyPage.getContent(), timeZone);
        CurrencySearchDto currencySearchDto = new CurrencySearchDto();
        currencySearchDto.setCurrencyList(currencyMasterDataList);
        currencySearchDto.setTotal(total);
        currencySearchDto.setTotalPage(totalPage);
        currencySearchDto.setPage(pageable.getPageNumber());
        return currencySearchDto;
    }

    @Override
    public Integer createCurrency(CurrencyMasterDataRequest request) {
        try {
            Optional<Currency> currencyOpt = currencyRepository.findByCode(request.getCode());
            if (currencyOpt.isEmpty()) {
                Currency currency = Currency.builder()
                        .code(request.getCode())
                        .name(request.getName())
                        .createdBy(AppUtil.getUserName())
                        .createdDate(DateTimeUtil.getTimestampUTC())
                        .updatedBy(AppUtil.getUserName())
                        .updatedDate(DateTimeUtil.getTimestampUTC())
                        .build();

                return currencyRepository.save(currency).getRecId();
            }
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Integer updateCurrency(CurrencyMasterDataRequest request) {
        try {
            Optional<Currency> currencyOpt = currencyRepository.findByRecId(request.getRecId());
            if (currencyOpt.isPresent()) {
                Currency currency = currencyOpt.get();
                currency.setCode(request.getCode());
                currency.setName(request.getName());
                currency.setUpdatedBy(AppUtil.getUserName());
                currency.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                currencyRepository.save(currency);
                return currency.getRecId();
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    public Integer deleteCurrencyById(Integer currencyId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        try {
            Optional<Currency> currencyOpt = currencyRepository.findByRecId(currencyId);
            if (currencyOpt.isPresent()) {
                Currency currency = currencyOpt.get();
                Optional<TenantCurrency> tenantCurrency = tenantCurrencyRepository.findByTenantRecIdAndCurrencyRecId(tenant.getRecId(), currency.getRecId());
                if(tenantCurrency.isEmpty()){
                    currencyRepository.deleteByRecId(currencyOpt.get().getRecId());
                    return currencyId;
                }
                return -1;
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private Specification<Currency> searchCurrencySpecificationByCondition(CurrencySearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            List<ConditionSearchRequest> conditionSearchRequestList = request.getConditionSearchList();
            if (conditionSearchRequestList != null && !conditionSearchRequestList.isEmpty()) {
                List<Predicate> orPredicates = new ArrayList<>();
                for (ConditionSearchRequest condition : conditionSearchRequestList) {
                    String searchField = condition.getSearchField();
                    String searchValue = condition.getSearchValue();

                    Predicate predicate = null;
                    if (StringUtils.isNotEmpty(searchField) && StringUtils.isNotEmpty(searchValue)) {
                        if (Constant.CODE.equalsIgnoreCase(searchField)) {
                            predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%");
                        } else if (Constant.NAME.equalsIgnoreCase(searchField)) {
                            predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%");
                        } else if (CREATED_BY.description().equalsIgnoreCase(searchField)) {
                            predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%");
                        } else {
                            if (CommonUtils.isNumeric(searchValue)) {
                                predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%");
                            } else {
                                predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue.toLowerCase() + "%");
                            }
                        }

                        if (predicate != null) {
                            orPredicates.add(predicate);
                        }
                    }
                }

                if (!orPredicates.isEmpty()) {
                    predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
