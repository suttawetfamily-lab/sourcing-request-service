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
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.function.*;

import static com.pantavanij.sourcingreq.services.enums.SearchApproverType.CREATED_BY;

@RequiredArgsConstructor
@Service
public class UnitServiceImpl implements UnitService {

    private final TenantService tenantService;
    private final TenantUnitRepository tenantUnitRepository;
    private final UnitRepository unitRepository;
    private final UaaService uaaService;

    @Override
    public List<UnitDto> getUnitByTenantIdV1(Integer tenantId, Integer organizationId) {
        List<UnitDto> unitDtoList = null;

        List<Unit> unitList = unitRepository.getUnitByTenantId(tenantId, organizationId);
        if(unitList.size() > 0) {
            unitDtoList = UnitMapper.INSTANCE.toUnitDto(unitList);
        }
        return unitDtoList;
    }

    @Override
    public List<UnitDto> getUnitSearchTermV1(Integer tenantId, String searchTerm, Integer organizationId) {
        List<UnitDto> unitDtoList = null;

        List<Unit> unitList = unitRepository.findByTenantIdAndCode(tenantId, searchTerm.trim(), organizationId);
        if(!unitList.isEmpty()) {
            unitDtoList = UnitMapper.INSTANCE.toUnitDto(unitList);
        }
        return unitDtoList;
    }

    @Override
    public List<OptionDto> getUnitSearchTerm(Integer tenantId, String searchTerm, Integer organizationId) {
        List<Unit> unitList = unitRepository.findByTenantIdAndCode(tenantId, searchTerm.trim(), organizationId);
        return UnitMapper.INSTANCE.toUnitOptionDto(unitList);
    }

    @Override
    public Optional<Unit> getUnitByUnitCode(String unitCode) {
        return unitRepository.getUnitByUnitCode(unitCode);
    }

    @Override
    public Unit createUnit(UnitDto unitDto) {
        Unit unit = new Unit();
        unit.setCode(unitDto.getUnitCode());
        unit.setName(unitDto.getDescription());
        unit.setCreatedBy(AppUtil.getUserName());
        unit.setCreatedDate(DateTimeUtil.getTimestampUTC());
        return unitRepository.save(unit);
    }

    @Override
    public List<OptionDto> getAllUnit() {
        try {
            List<Unit> unitList = unitRepository.findAll();
            if (!unitList.isEmpty()) {
                return UnitMapper.INSTANCE.toUnitOptionDto(unitList);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Integer createUnit(UnitMasterDataRequest request) {
        try {
            Optional<Unit> unitOpt = unitRepository.findUnitByRecId(request.getRecId());
            if (unitOpt.isEmpty()) {
                Unit unit = Unit.builder()
                        .code(request.getCode())
                        .name(request.getName())
                        .createdBy(AppUtil.getUserName())
                        .createdDate(DateTimeUtil.getTimestampUTC())
                        .updatedBy(AppUtil.getUserName())
                        .updatedDate(DateTimeUtil.getTimestampUTC())
                        .build();
                Unit saved = unitRepository.save(unit);
                return saved.getRecId();
            }
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public UnitMasterDataDto getUnitByUnitId(Integer unitId) {
        try {
            Optional<Unit> unitOpt = unitRepository.findUnitByRecId(unitId);
            if (unitOpt.isPresent()) {
                Unit unit = unitOpt.get();
                return UnitMapper.INSTANCE.toUnitMasterDataDto(unit);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public UnitSearchDto searchUnitsByCondition(UnitSearchRequest request, Pageable pageable) {
        Page<Unit> uintPage = unitRepository.findAll(Specification.where(searchUnitByCondition(request)), pageable);
        int totalPage = uintPage.getTotalPages();
        long total = uintPage.getTotalElements();

        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        List<UnitMasterDataDto> unitMasterDataDtoList = UnitMapper.INSTANCE.toUnitMasterDataDtoList(uintPage.getContent(), timeZone);
        UnitSearchDto unitSearchDto = new UnitSearchDto();
        unitSearchDto.setUnitList(unitMasterDataDtoList);
        unitSearchDto.setTotal(total);
        unitSearchDto.setTotalPage(totalPage);
        unitSearchDto.setPage(pageable.getPageNumber());
        return unitSearchDto;
    }

    @Override
    public Integer updateUnit(UnitMasterDataRequest request) {
        try {
            Optional<Unit> unitOpt = unitRepository.findUnitByRecId(request.getRecId());
            if (unitOpt.isPresent()) {
                Unit unit = unitOpt.get();
                unit.setCode(request.getCode());
                unit.setName(request.getName());
                unit.setUpdatedBy(AppUtil.getUserName());
                unit.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                return unitRepository.save(unit).getRecId();
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Integer deleteUnitById(Integer unitId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        try {
            Optional<Unit> unitOpt = unitRepository.findUnitByRecId(unitId);
            if (unitOpt.isPresent()) {
                Unit unit = unitOpt.get();
                Optional<TenantUnit> tenantUnit  = tenantUnitRepository.findByTenantRecIdAndUnitRecId(tenant.getRecId(), unit.getRecId());
                if(tenantUnit.isEmpty()){
                    unitRepository.deleteByRecId(unit.getRecId());
                    return unitId;
                }
                return -1;
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private Specification<Unit> searchUnitByCondition(UnitSearchRequest request) {
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
            query.distinct(true);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
