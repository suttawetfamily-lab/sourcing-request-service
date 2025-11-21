package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.mapper.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TypeRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.*;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.*;

import static com.pantavanij.sourcingreq.services.util.Constant.REC_ID;
import static com.pantavanij.sourcingreq.services.util.Constant.TENANT;

@Service
@RequiredArgsConstructor
public class TypeServiceImpl implements TypeService {
    private final TypeRepository typeRepository;
    private final TenantService tenantService;
    private final UaaService uaaService;

    public List<OptionDto> getTypeByTenantId(Integer tenantId) {
        List<Type> typeList = typeRepository.getTypeByTenantId(tenantId);
        return TypeMapper.INSTANCE.toTypeOptionDto(typeList);
    }

    @Override
    public TypeDto getByTypeId(Integer typeId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        try {
            Optional<Type> typeOpt = typeRepository.findByTenantRecIdAndRecId(tenant.getRecId(), typeId);
            return typeOpt.map(type -> TypeMapper.INSTANCE.toTypeDto(type, timeZone)).orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public TypeSearchDto searchTypeListByCondition(TypeSearchRequest request, Pageable pageable) {
        Page<Type> typePage = typeRepository.findAll(Specification.where(searchTypeByCondition(request)), pageable);
        int totalPage = typePage.getTotalPages();
        long total = typePage.getTotalElements();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);

        List<TypeDto> typeDtoList = TypeMapper.INSTANCE.toTypeDtoList(typePage.getContent(), timeZone);
        TypeSearchDto typeSearchDto = new TypeSearchDto();
        typeSearchDto.setTypeList(typeDtoList);
        typeSearchDto.setTotal(total);
        typeSearchDto.setTotalPage(totalPage);
        typeSearchDto.setPage(pageable.getPageNumber());
        return typeSearchDto;
    }

    @Override
    public Integer createType(TypeRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        try {
            Optional<Type> typeOpt = typeRepository.findFirstByTenantRecIdOrderBySequenceDesc(tenant.getRecId());
            if (typeOpt.isPresent() && request.getSequence() == 0) {
                request.setSequence(typeOpt.get().getSequence() + 1);
            } else if (typeOpt.isEmpty() && request.getSequence() == 0) {
                request.setSequence(1);
            } else {
                typeOpt.ifPresent(type -> typeRepository.reOrderOtherTypeSequence(tenant.getRecId(), type.getSequence() + 1, request.getSequence()));
            }

            Type type = Type.builder()
                    .tenant(tenant)
                    .code(request.getCode())
                    .name(request.getName())
                    .sequence(request.getSequence())
                    .isDefault(request.isDefault())
                    .active(request.isActive())
                    .createdBy(AppUtil.getUserName())
                    .createdDate(DateTimeUtil.getTimestampUTC())
                    .updatedBy(AppUtil.getUserName())
                    .updatedDate(DateTimeUtil.getTimestampUTC())
                    .build();

            Type typeCreated = typeRepository.save(type);
            return typeCreated.getRecId();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public Integer updateType(TypeRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        try {
            Optional<Type> typeOpt = typeRepository.findByTenantRecIdAndRecId(tenant.getRecId(), request.getId());
            if (typeOpt.isPresent()) {
                typeOpt.ifPresent(type -> typeRepository.reOrderOtherTypeSequence(tenant.getRecId(), type.getSequence(), request.getSequence()));

                Type type = Type.builder()
                        .recId(request.getId())
                        .tenant(tenant)
                        .code(request.getCode())
                        .name(request.getName())
                        .sequence(request.getSequence())
                        .isDefault(request.isDefault())
                        .active(request.isActive())
                        .createdBy(typeOpt.get().getCreatedBy())
                        .createdDate(typeOpt.get().getCreatedDate())
                        .updatedBy(AppUtil.getUserName())
                        .updatedDate(DateTimeUtil.getTimestampUTC())
                        .build();

                Type typeCreated = typeRepository.save(type);
                return typeCreated.getRecId();
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public TypeDto updateTypeSequence(SequenceRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        Optional<Type> typeOpt = typeRepository.findByTenantRecIdAndRecId(tenant.getRecId(), request.getRecId());
        if (typeOpt.isPresent()) {
            Type typeExist = typeOpt.get();
            typeRepository.reOrderOtherTypeSequence(tenant.getRecId(), typeExist.getSequence(), request.getSequence());

            typeExist.setSequence(request.getSequence());

            Type updateSequence = typeRepository.save(typeExist);
            return TypeMapper.INSTANCE.toTypeDto(updateSequence, timeZone);
        }
        return null;
    }

    private Specification<Type> searchTypeByCondition(TypeSearchRequest request) {
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
                            predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%");
                        } else if (Constant.NAME.equalsIgnoreCase(searchField)) {
                            predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%");
                        } else if (Constant.ACTIVE.equalsIgnoreCase(searchField)) {
                            boolean active = "1".equalsIgnoreCase(searchValue) || 1 == Integer.parseInt(searchValue);
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
            return criteriaBuilder.and(predicates.toArray(new javax.persistence.criteria.Predicate[0]));
        };
    }
}


