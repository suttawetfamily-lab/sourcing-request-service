package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.mapper.TenantSourcingStatusMapper;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantSourcingStatusRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantSourcingStatusService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UaaService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.services.util.CommonUtils;
import com.pantavanij.sourcingreq.services.util.Constant;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.pantavanij.sourcingreq.services.util.Constant.*;

@RequiredArgsConstructor
@Service
public class TenantSourcingStatusServiceImpl implements TenantSourcingStatusService {
    private final TenantSourcingStatusRepository tenantSourcingStatusRepository;
    private final TenantService tenantService;
    private final UaaService uaaService;

//    @Override
//    public TenantSourcingStatusDto createTenantSourcingStatus(TenantSourcingStatusRequest request) {
//        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
//        try {
//            SourcingStatus sourcingStatus = sourcingStatusRepository.findSourcingStatusByRecId(request.getSourcingStatusId());
//            if(sourcingStatus != null) {
//                TenantSourcingStatus tenantSourcingStatus = tenantSourcingStatusRepository.save(getTenantSourcingStatusSubmit(request, sourcingStatus, tenant));
//                return TenantSourcingStatusMapper.INSTANCE.toTenantSourcingStatusDto(tenantSourcingStatus);
//            }
//           return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException(e.getMessage());
//        }
//    }

    @Override
    public TenantSourcingStatusSearchDto searchTenantSourcingStatusByCondition(TenantSourcingStatusSearchRequest request, Pageable pageable) {
        Page<TenantSourcingStatus> tenantSourcingStatusPage = tenantSourcingStatusRepository.findAll(Specification.where(searchTenantSourcingStatusByWhereCondition(request)), pageable);
        int totalPage = tenantSourcingStatusPage.getTotalPages();
        long total = tenantSourcingStatusPage.getTotalElements();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        List<TenantSourcingStatusDto> tennatCurrencyDtoList = TenantSourcingStatusMapper.INSTANCE.toTenantSourcingStatusDtoList(tenantSourcingStatusPage.getContent(), timeZone);
        TenantSourcingStatusSearchDto tenantSourcingStatusSearchDto = new TenantSourcingStatusSearchDto();
        tenantSourcingStatusSearchDto.setTenantSourcingStatusList(tennatCurrencyDtoList);
        tenantSourcingStatusSearchDto.setTotal(total);
        tenantSourcingStatusSearchDto.setTotalPage(totalPage);
        tenantSourcingStatusSearchDto.setPage(pageable.getPageNumber());
        return tenantSourcingStatusSearchDto;
    }

    @Override
    public TenantSourcingStatusDto getBySourcingStatusIdAndTenant(Integer sourcingStatusId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        Optional<TenantSourcingStatus> tenantSourcingStatusOpt = tenantSourcingStatusRepository.findById_SourcingStatusIdAndTenant(sourcingStatusId, tenant);
        if (tenantSourcingStatusOpt.isPresent()) {
            TenantSourcingStatus tenantSourcingStatus = tenantSourcingStatusOpt.get();
            return TenantSourcingStatusMapper.INSTANCE.toTenantSourcingStatusDto(tenantSourcingStatus, timeZone);
        }
        return null;
    }

    @Override
    public TenantSourcingStatusDto updateTenantSourcingStatus(TenantSourcingStatusRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        try {
            Optional<TenantSourcingStatus> tenantSourcingStatusOptional = tenantSourcingStatusRepository.findById_SourcingStatusIdAndTenant(request.getSourcingStatusId(), tenant);
            if (tenantSourcingStatusOptional.isPresent()) {
                tenantSourcingStatusRepository.reOrderOtherTenantSourcingStatusSequence(tenant.getRecId(), tenantSourcingStatusOptional.get().getSequence(), request.getSequence());
                TenantSourcingStatus tenantSourcingStatus = tenantSourcingStatusRepository.save(getTenantSourcingStatusUpdate(tenantSourcingStatusOptional.get(), request, tenant));
                return TenantSourcingStatusMapper.INSTANCE.toTenantSourcingStatusDto(tenantSourcingStatus,  timeZone);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public TenantSourcingStatusDto updateTenantSourcingStatusSequence(SequenceRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        Optional<TenantSourcingStatus> tenantSourcingStatusOpt = tenantSourcingStatusRepository.findById_SourcingStatusIdAndTenant(request.getRecId(), tenant);
        if (tenantSourcingStatusOpt.isPresent()) {
            TenantSourcingStatus tenantSourcingStatusExist = tenantSourcingStatusOpt.get();
            tenantSourcingStatusRepository.reOrderOtherTenantSourcingStatusSequence(tenant.getRecId(), tenantSourcingStatusExist.getSequence(), request.getSequence());

            tenantSourcingStatusExist.setSequence(request.getSequence());

            TenantSourcingStatus updateSequence = tenantSourcingStatusRepository.save(tenantSourcingStatusExist);
            return TenantSourcingStatusMapper.INSTANCE.toTenantSourcingStatusDto(updateSequence, timeZone);
        }
        return null;
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    @Modifying
//    public boolean deleteTenantSourcingStatus(Integer tenantSourcingStatusId) {
//        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
//        try {
//            Optional<TenantSourcingStatus> tenantSourcingStatusOptional = tenantSourcingStatusRepository.findByRecIdAndTenant(tenantSourcingStatusId, tenant);
//            if (tenantSourcingStatusOptional.isPresent()) {
//                tenantSourcingStatusRepository.deleteByRecIdAndTenant(tenantSourcingStatusId, tenant);
//                tenantSourcingStatusRepository.reOrderSequenceByTenantRecId(tenant.getRecId());
//                return true;
//            }
//            return false;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException(e.getMessage());
//        }
//    }

//    public TenantSourcingStatus getTenantSourcingStatusSubmit(TenantSourcingStatusRequest request, SourcingStatus sourcingStatus, Tenant tenant) {
//        return TenantSourcingStatus.builder()
//                .sourcingStatus(sourcingStatus)
//                .tenant(tenant)
//                .createdDate(DateTimeUtil.getTimestampUTC())
//                .updatedBy(AppUtil.getUserName())
//                .updatedDate(DateTimeUtil.getTimestampUTC())
//                .build();
//    }

    public TenantSourcingStatus getTenantSourcingStatusUpdate(TenantSourcingStatus tenantSourcingStatus, TenantSourcingStatusRequest request, Tenant tenant) {
        return TenantSourcingStatus.builder()
                .id(tenantSourcingStatus.getId())
                .sourcingStatus(tenantSourcingStatus.getSourcingStatus())
                .tenant(tenant)
                .approver(request.getApprover())
                .remark(request.getRemark())
                .purchaserOwner(request.getPurchaserOwner())
                .purchaserNotOwner(request.getPurchaserNotOwner())
                .reviewer(request.getReviewer())
                .requester(request.getRequester())
                .sequence(request.getSequence() != null ? request.getSequence() : tenantSourcingStatus.getSequence())
                .isDefault(request.isDefault())
                .active(request.isActive())
                .createdBy(tenantSourcingStatus.getCreatedBy())
                .createdDate(tenantSourcingStatus.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }

    private Specification<TenantSourcingStatus> searchTenantSourcingStatusByWhereCondition(TenantSourcingStatusSearchRequest request) {
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
                    if (org.apache.commons.lang3.StringUtils.isNotEmpty(searchField) && StringUtils.isNotEmpty(searchValue)) {
                        if (Constant.SOURCING_STATUS_CODE.equalsIgnoreCase(searchField)) {
                            predicate = criteriaBuilder.like(root.get(Constant.SOURCING_STATUS).get(Constant.CODE), "%" + searchValue + "%");
                        } else if (Constant.SOURCING_STATUS_DESCRIPTION.equalsIgnoreCase(searchField)) {
                            predicate = criteriaBuilder.like(root.get(Constant.SOURCING_STATUS).get(Constant.DESCRIPTION), "%" + searchValue + "%");
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

}
