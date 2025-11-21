package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.*;
import com.pantavanij.sourcingreq.services.domain.mapper.*;
import com.pantavanij.sourcingreq.services.domain.projection.TenantUnitOptionProjection;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.*;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.*;

import static com.pantavanij.sourcingreq.services.util.Constant.REC_ID;
import static com.pantavanij.sourcingreq.services.util.Constant.TENANT;

@RequiredArgsConstructor
@Service
public class TenantUnitServiceImpl implements TenantUnitService {
    private final TenantUnitRepository tenantUnitRepository;
    private final TenantUnitOrganizationRepository tenantUnitOrganizationRepository;
    private final TenantService tenantService;
    private final UaaService uaaService;
    private final UnitRepository unitRepository;

    @Override
    public List<OptionDto> getTenantUnitByTenantIdAndSearchTerm(Integer tenantId, Integer organizationId, String searchTerm) {
        List<TenantUnitOptionProjection> tenantUnitList;

        if (organizationId != null) {
            tenantUnitList = tenantUnitOrganizationRepository
                    .findByTenantIdAndOrganizationIdAndSearchTerm(tenantId, organizationId, searchTerm.trim());
        } else {
            tenantUnitList = tenantUnitRepository
                    .findByTenantIdAndCodeOrDescription(tenantId, searchTerm.trim());
        }


        return TenantUnitMapper.INSTANCE.toOptionDtoList(tenantUnitList);
    }


    @Override
    public void createTenantUnit(TenantUnitDto tenantUnitDto) {
        tenantUnitRepository.saveTenantUnit(tenantUnitDto.getTenantId(),
                tenantUnitDto.getUnitId(),
                AppUtil.getUserName(),
                DateTimeUtil.getTimestampUTC());
    }

    @Override
    public Integer createTenantUnit(UnitRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        try {
            Optional<Unit> unitOpt = unitRepository.findUnitByRecId(request.getId());
            if (unitOpt.isEmpty()) {
                return 0;
            }

            Optional<TenantUnit> tenantUnitOpt = tenantUnitRepository.findByTenantRecIdAndUnitRecId(tenant.getRecId(), request.getId());
            if (tenantUnitOpt.isPresent()) {
                return -1;
            }

            Optional<TenantUnit> tenantUnitLastSequenceOpt = tenantUnitRepository.findFirstByTenantRecIdOrderBySequenceDesc(tenant.getRecId());
            if (tenantUnitLastSequenceOpt.isPresent()) {
                if(request.getSequence() > tenantUnitLastSequenceOpt.get().getSequence()){
                    request.setSequence(tenantUnitLastSequenceOpt.get().getSequence() + 1);
                } else {
                    tenantUnitRepository.reOrderOtherTenantUnitSequence(tenant.getRecId(), tenantUnitLastSequenceOpt.get().getSequence() + 1, request.getSequence());
                }
            } else {
                request.setSequence(1);
            }

            TenantUnitKey key = TenantUnitKey.builder()
                    .tenantId(tenant.getRecId())
                    .unitId(request.getId())
                    .build();

            TenantUnit tenantUnit = TenantUnit.builder()
                    .id(key)
                    .tenant(tenant)
                    .unit(unitOpt.get())
                    .sequence(request.getSequence())
                    .isDefault(request.isDefault())
                    .active(request.isActive())
                    .createdBy(AppUtil.getUserName())
                    .createdDate(DateTimeUtil.getTimestampUTC())
                    .updatedBy(AppUtil.getUserName())
                    .updatedDate(DateTimeUtil.getTimestampUTC())
                    .build();

            TenantUnit saved = tenantUnitRepository.save(tenantUnit);
            return saved.getId().getUnitId();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public TenantUnit findTenantUnitByUnitIdAndTenantId(Integer unitId, Integer tenantId) {
        return tenantUnitRepository.findTenantUnitByUnitIdAndTenantId(unitId, tenantId);
    }

    @Override
    public TenantUnitDto getByUnitId(Integer unitId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        try {
            Optional<TenantUnit> tenantUnitOpt = tenantUnitRepository.findByTenantRecIdAndUnitRecId(tenant.getRecId(), unitId);
            if (tenantUnitOpt.isPresent()) {
                System.out.println(tenantUnitOpt.get().isDefault());
                return TenantUnitMapper.INSTANCE.toTenantUnitDto(tenantUnitOpt.get(), timeZone);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public TenantUnitSearchDto searchTenantUnitByCondition(UnitSearchRequest request, Pageable pageable) {
        Page<TenantUnit> tenantUnitPage = tenantUnitRepository.findAll(Specification.where(searchTenantUnitByWhereCondition(request)), pageable);
        int totalPage = tenantUnitPage.getTotalPages();
        long total = tenantUnitPage.getTotalElements();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);

        List<TenantUnitDto> tenantUnitDtoList = TenantUnitMapper.INSTANCE.toTenantUnitList(tenantUnitPage.getContent(), timeZone);
        TenantUnitSearchDto tenantUnitSearchDto = new TenantUnitSearchDto();
        tenantUnitSearchDto.setTenantUnitList(tenantUnitDtoList);
        tenantUnitSearchDto.setTotal(total);
        tenantUnitSearchDto.setTotalPage(totalPage);
        tenantUnitSearchDto.setPage(pageable.getPageNumber());
        return tenantUnitSearchDto;
    }

    @Override
    public Integer updateTenantUnit(UnitRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        try {
            Optional<Unit> unitOpt = unitRepository.findUnitByRecId(request.getId());
            if (unitOpt.isEmpty()) {
                return 0;
            }

            Optional<TenantUnit> tenantUnitOpt = tenantUnitRepository.findByTenantRecIdAndUnitRecId(tenant.getRecId(), request.getId());
            if (tenantUnitOpt.isPresent()) {
                tenantUnitOpt.ifPresent(tenantUnit -> tenantUnitRepository.reOrderOtherTenantUnitSequence(tenant.getRecId(), tenantUnit.getSequence(), request.getSequence()));

                TenantUnitKey key = TenantUnitKey.builder()
                        .tenantId(tenant.getRecId())
                        .unitId(request.getId())
                        .build();

                TenantUnit tenantUnit = TenantUnit.builder()
                        .id(key)
                        .tenant(tenant)
                        .unit(unitOpt.get())
                        .sequence(request.getSequence())
                        .isDefault(request.isDefault())
                        .active(request.isActive())
                        .createdBy(tenantUnitOpt.get().getCreatedBy())
                        .createdDate(tenantUnitOpt.get().getCreatedDate())
                        .updatedBy(AppUtil.getUserName())
                        .updatedDate(DateTimeUtil.getTimestampUTC())
                        .build();

                TenantUnit updated = tenantUnitRepository.save(tenantUnit);
                return updated.getId().getUnitId();
            }
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public TenantUnitDto updateTenantUnitSequence(SequenceRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        Optional<TenantUnit> tenantUnitOpt = tenantUnitRepository.findByTenantRecIdAndUnitRecId(tenant.getRecId(), request.getRecId());
        if (tenantUnitOpt.isPresent()) {
            TenantUnit tenantUnitExist = tenantUnitOpt.get();
            tenantUnitRepository.reOrderOtherTenantUnitSequence(tenant.getRecId(), tenantUnitExist.getSequence(), request.getSequence());

            tenantUnitExist.setSequence(request.getSequence());

            TenantUnit updateSequence = tenantUnitRepository.save(tenantUnitExist);
            return TenantUnitMapper.INSTANCE.toTenantUnitDto(updateSequence, timeZone);
        }
        return null;
    }

    private Specification<TenantUnit> searchTenantUnitByWhereCondition(UnitSearchRequest request) {
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
                            predicate = criteriaBuilder.like(root.get(Constant.UNIT).get(searchField), "%" + searchValue + "%");
                        } else if (Constant.NAME.equalsIgnoreCase(searchField)) {
                            predicate = criteriaBuilder.like(root.get(Constant.UNIT).get(searchField), "%" + searchValue + "%");
                        } else if (Constant.ACTIVE.equalsIgnoreCase(searchField)) {
                            boolean active = "1".equals(searchValue) || 1 == Integer.parseInt(searchValue);
                            predicate = criteriaBuilder.equal(root.get(searchField), active);
                        } else if (Constant.CREATED_BY.equalsIgnoreCase(searchField)) {
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    public Integer deleteTenantUnitById(Integer unitId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        try {
            Optional<TenantUnit> tenantUnit = tenantUnitRepository.findByTenantRecIdAndUnitRecId(tenant.getRecId(), unitId);
            if(tenantUnit.isPresent()){
                Optional<TenantUnit> tenantUnitLastSequenceOpt = tenantUnitRepository.findFirstByTenantRecIdOrderBySequenceDesc(tenant.getRecId());
                tenantUnitLastSequenceOpt.ifPresent(tenantUnitLastSequence -> tenantUnitRepository.reOrderOtherTenantUnitSequence(tenant.getRecId(), tenantUnit.get().getSequence(), tenantUnitLastSequence.getSequence() + 1));
                tenantUnitRepository.delete(tenantUnit.get());
                return unitId;
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
