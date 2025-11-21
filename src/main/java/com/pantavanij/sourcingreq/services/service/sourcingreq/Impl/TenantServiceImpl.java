package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.mapper.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.*;

import static com.pantavanij.sourcingreq.services.enums.SearchApproverType.*;
import static com.pantavanij.sourcingreq.services.enums.SearchApproverType.LOGIN_ID;
import static com.pantavanij.sourcingreq.services.util.Constant.REC_ID;
import static com.pantavanij.sourcingreq.services.util.Constant.TENANT;

@RequiredArgsConstructor
@Service
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;

    @Override
    public Tenant findByCode(String code) {
        return tenantRepository.findTenantByCode(code);
    }

    @Override
    public Tenant getTenantUnitByRecId(Integer recId) {
        return tenantRepository.getById(recId);
    }

    @Override
    public TenantDto getTenantDtoByRecId(Integer recId, String timeZone) {
        Tenant tenant = tenantRepository.findTenantByRecId(recId);
        if (tenant != null && tenant.getRecId() != null) {
            return TenantMapper.INSTANCE.toTenantDto(tenant, timeZone);
        }
        return null;
    }

    @Override
    public TenantSearchDto searchTenantListByCondition(TenantSearchRequest request, Pageable pageable, String timeZone) {
        Page<Tenant> tenantPage = tenantRepository.findAll(Specification.where(searchTenantSpecificationByCondition(request, timeZone)), pageable);
        int totalPage = tenantPage.getTotalPages();
        long total = tenantPage.getTotalElements();

        List<TenantDto> tenantDtoList = TenantMapper.INSTANCE.toTenantDtoList(tenantPage.getContent(), timeZone);
        TenantSearchDto tenantSearchDto = new TenantSearchDto();
        tenantSearchDto.setTenantList(tenantDtoList);
        tenantSearchDto.setTotal(total);
        tenantSearchDto.setTotalPage(totalPage);
        tenantSearchDto.setPage(pageable.getPageNumber());
        return tenantSearchDto;
    }

    @Override
    public Integer createTenant(TenantRequest request) {
        try {
            Tenant tennat = tenantRepository.findTenantByCode(request.getCode());
            if (tennat == null) {
                Tenant tenant = Tenant.builder()
                        .code(request.getCode())
                        .name(request.getName())
                        .description(request.getDescription())
                        .createdBy(AppUtil.getUserName())
                        .createdDate(DateTimeUtil.getTimestampUTC())
                        .build();

                return tenantRepository.save(tenant).getRecId();
            }
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer updateTenant(TenantRequest request) {
        try {
            Tenant tenant = tenantRepository.findTenantByRecId(request.getRecId());
            if (tenant != null && tenant.getRecId() != null) {
                tenant.setRecId(tenant.getRecId());
                tenant.setCode(request.getCode());
                tenant.setName(request.getName());
                tenant.setDescription(request.getDescription());
                tenantRepository.save(tenant);
                return tenant.getRecId();
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private Specification<Tenant> searchTenantSpecificationByCondition(TenantSearchRequest request, String timeZone) {
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
