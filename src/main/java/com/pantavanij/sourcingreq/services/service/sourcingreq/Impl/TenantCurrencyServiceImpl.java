package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Currency;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.*;
import com.pantavanij.sourcingreq.services.domain.mapper.*;
import com.pantavanij.sourcingreq.services.domain.projection.TenantCurrencyOptionProjection;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.*;
import org.apache.commons.lang3.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.*;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.util.Constant.REC_ID;
import static com.pantavanij.sourcingreq.services.util.Constant.TENANT;

@Service
@RequiredArgsConstructor
public class TenantCurrencyServiceImpl implements TenantCurrencyService {
    private final TenantCurrencyRepository tenantCurrencyRepository;
    private final CurrencyRepository currencyRepository;
    private final TenantService tenantService;
    private final UaaService uaaService;
    private final TenantCurrencyOrganizationRepository tenantCurrencyOrganizationRepository;

    public List<OptionDto> getTenantCurrencyByTenantIdAndSearchTerm(Integer tenantId, String searchTerm) {
        List<TenantCurrencyOptionProjection> tenantCurrencyList =
                tenantCurrencyRepository.findByTenantIdAndCodeOrDescription(tenantId, searchTerm.trim());

        return TenantCurrencyMapper.INSTANCE.toOptionDtoList(tenantCurrencyList);
    }

    @Override
    public TenantCurrencyDto getByCurrencyId(Integer currencyId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        try {
            Optional<TenantCurrency> tenantCurrencyOpt = tenantCurrencyRepository.findByTenantRecIdAndCurrencyRecId(tenant.getRecId(), currencyId);
            return tenantCurrencyOpt.map(tenantCurrency -> TenantCurrencyMapper.INSTANCE.toTenantCurrencyDto(tenantCurrency, timeZone)).orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Integer createTenantCurrency(CurrencyRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        try {
            Currency currency = currencyRepository.findCurrenciesByRecId(request.getId());
            if (currency == null) {
                return 0;
            }

            Optional<TenantCurrency> tenantCurrencyOpt = tenantCurrencyRepository.findByTenantRecIdAndCurrencyRecId(tenant.getRecId(), request.getId());
            if (tenantCurrencyOpt.isPresent()) {
                return -1;
            }

            Optional<TenantCurrency> tenantCurrencyLastSequenceOpt = tenantCurrencyRepository.findFirstByTenantRecIdOrderBySequenceDesc(tenant.getRecId());
            if (tenantCurrencyLastSequenceOpt.isPresent()) {
                if(request.getSequence() > tenantCurrencyLastSequenceOpt.get().getSequence()){
                    request.setSequence(tenantCurrencyLastSequenceOpt.get().getSequence() + 1);
                } else {
                    tenantCurrencyRepository.reOrderOtherTenantCurrencySequence(tenant.getRecId(), tenantCurrencyLastSequenceOpt.get().getSequence() + 1, request.getSequence());
                }
            } else {
                request.setSequence(1);
            }

            TenantCurrencyKey key = TenantCurrencyKey.builder()
                    .tenantId(tenant.getRecId())
                    .currencyId(request.getId())
                    .build();

            TenantCurrency tenantCurrency = TenantCurrency.builder()
                    .id(key)
                    .tenant(tenant)
                    .currency(currency)
                    .sequence(request.getSequence())
                    .isDefault(request.isDefault())
                    .active(request.isActive())
                    .createdBy(AppUtil.getUserName())
                    .createdDate(DateTimeUtil.getTimestampUTC())
                    .updatedBy(AppUtil.getUserName())
                    .updatedDate(DateTimeUtil.getTimestampUTC())
                    .build();

            TenantCurrency saved = tenantCurrencyRepository.save(tenantCurrency);
            return saved.getId().getCurrencyId();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Integer updateTenantCurrency(CurrencyRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        try {
            Currency currency = currencyRepository.findCurrenciesByRecId(request.getId());
            if (currency == null) {
                return 0;
            }

            Optional<TenantCurrency> tenantCurrencyOpt = tenantCurrencyRepository.findByTenantRecIdAndCurrencyRecId(tenant.getRecId(), request.getId());
            if (tenantCurrencyOpt.isPresent()) {
                tenantCurrencyOpt.ifPresent(tenantCurrency -> tenantCurrencyRepository.reOrderOtherTenantCurrencySequence(tenant.getRecId(), tenantCurrency.getSequence(), request.getSequence()));

                TenantCurrencyKey key = TenantCurrencyKey.builder()
                        .tenantId(tenant.getRecId())
                        .currencyId(request.getId())
                        .build();

                TenantCurrency tenantCurrencyExist = tenantCurrencyOpt.get();
                TenantCurrency tenantCurrency = TenantCurrency.builder()
                        .id(key)
                        .tenant(tenant)
                        .currency(currency)
                        .sequence(request.getSequence())
                        .isDefault(request.isDefault())
                        .active(request.isActive())
                        .createdBy(tenantCurrencyExist.getCreatedBy())
                        .createdDate(tenantCurrencyExist.getCreatedDate())
                        .updatedBy(AppUtil.getUserName())
                        .updatedDate(DateTimeUtil.getTimestampUTC())
                        .build();

                TenantCurrency updated = tenantCurrencyRepository.save(tenantCurrency);
                return updated.getId().getCurrencyId();
            }
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public TenantCurrencySearchDto searchTenantCurrencyByCondition(TenantCurrencySearchRequest request, Pageable pageable) {
        Page<TenantCurrency> tenantCurrencyPage = tenantCurrencyRepository.findAll(Specification.where(searchTenantCurrencyByWhereCondition(request)), pageable);
        int totalPage = tenantCurrencyPage.getTotalPages();
        long total = tenantCurrencyPage.getTotalElements();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);

        List<TenantCurrencyDto> tennatCurrencyDtoList = TenantCurrencyMapper.INSTANCE.toTenantCurrencyDtoList(tenantCurrencyPage.getContent(), timeZone);
        TenantCurrencySearchDto tenantCurrencySearchDto = new TenantCurrencySearchDto();
        tenantCurrencySearchDto.setTenantCurrencyList(tennatCurrencyDtoList);
        tenantCurrencySearchDto.setTotal(total);
        tenantCurrencySearchDto.setTotalPage(totalPage);
        tenantCurrencySearchDto.setPage(pageable.getPageNumber());
        return tenantCurrencySearchDto;
    }

    @Override
    public TenantCurrencyDto updateTenantCurrencySequence(SequenceRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        Optional<TenantCurrency> tenantCurrencyOpt = tenantCurrencyRepository.findByTenantRecIdAndCurrencyRecId(tenant.getRecId(), request.getRecId());
        if (tenantCurrencyOpt.isPresent()) {
            TenantCurrency tenantCurrencyExist = tenantCurrencyOpt.get();
            tenantCurrencyRepository.reOrderOtherTenantCurrencySequence(tenant.getRecId(), tenantCurrencyExist.getSequence(), request.getSequence());

            tenantCurrencyExist.setSequence(request.getSequence());

            TenantCurrency updateSequence = tenantCurrencyRepository.save(tenantCurrencyExist);
            return TenantCurrencyMapper.INSTANCE.toTenantCurrencyDto(updateSequence, timeZone);
        }
        return null;
    }

    private Specification<TenantCurrency> searchTenantCurrencyByWhereCondition(TenantCurrencySearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            List<ConditionSearchRequest> conditionSearchRequestList = request.getConditionSearchList();

            Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
            if (tenant != null) {
                predicates.add(criteriaBuilder.equal(root.get(TENANT).get(REC_ID), tenant.getRecId()));
            }

            if (conditionSearchRequestList != null && !conditionSearchRequestList.isEmpty()) {
                List<Predicate> orPredicates = new ArrayList<>();

                for (ConditionSearchRequest condition : conditionSearchRequestList) {
                    String searchField = condition.getSearchField();
                    String searchValue = condition.getSearchValue();

                    Predicate predicate = null;
                    if (StringUtils.isNotEmpty(searchField) && StringUtils.isNotEmpty(searchValue)) {
                        if (Constant.CODE.equalsIgnoreCase(searchField)) {
                            predicate = criteriaBuilder.like(root.get(Constant.CURRENCY).get(searchField), "%" + searchValue + "%");
                        } else if (Constant.NAME.equalsIgnoreCase(searchField)) {
                            predicate = criteriaBuilder.like(root.get(Constant.CURRENCY).get(searchField), "%" + searchValue + "%");
                        } else if (Constant.ACTIVE.equalsIgnoreCase(searchField)) {
                            boolean active = "1".equals(searchValue) || 1 == Integer.parseInt(searchValue);
                            predicate = criteriaBuilder.equal(root.get(searchField), active);
                        } else if (Constant.CREATED_BY.equalsIgnoreCase(searchField)) {
                            predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%");
                        } else {
                            if (CommonUtils.isNumeric(searchValue)) {
                                predicate = criteriaBuilder.equal(root.get(searchField), searchValue);
                            } else {
                                predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue.toLowerCase() + "%");
                            }
                        }
                        if (predicate!=null) {
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    public Integer deleteTenantCurrencyById(Integer currencyId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        try {
            Optional<TenantCurrency> tenantCurrency = tenantCurrencyRepository.findByTenantRecIdAndCurrencyRecId(tenant.getRecId(), currencyId);
            if(tenantCurrency.isPresent()){
                Optional<TenantCurrency> tenantCurrencyLastSequenceOpt = tenantCurrencyRepository.findFirstByTenantRecIdOrderBySequenceDesc(tenant.getRecId());
                tenantCurrencyLastSequenceOpt.ifPresent(tenantCurrencyLastSequence -> tenantCurrencyRepository.reOrderOtherTenantCurrencySequence(tenant.getRecId(), tenantCurrency.get().getSequence(), tenantCurrencyLastSequence.getSequence() + 1));
                tenantCurrencyRepository.delete(tenantCurrency.get());
                return currencyId;
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    // TenantCurrencyServiceImpl.java (เติม/แก้เฉพาะเมธอดนี้)
    @Override
    @Transactional(readOnly = true)
    public List<OptionDto> getTenantCurrencyByTenantIdAndOrganizationIdAndSearchTerm(
            Integer tenantId, Integer organizationId, String searchTerm) {

        List<TenantCurrencyOptionProjection> tenantCurrencyList =
                tenantCurrencyRepository.findByTenantIdAndOrganizationIdAndCodeOrDescription(
                        tenantId, organizationId, searchTerm);

        // ใช้ MapStruct เดิม
        return TenantCurrencyMapper.INSTANCE.toOptionDtoList(tenantCurrencyList);
    }

}
