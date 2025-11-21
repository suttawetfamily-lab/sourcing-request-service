package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.*;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.mapper.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.PurchaserRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.*;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.*;

import static com.pantavanij.sourcingreq.services.enums.Role.DEPT_APPROVER;
import static com.pantavanij.sourcingreq.services.enums.Role.PURCHASER;
import static com.pantavanij.sourcingreq.services.enums.SearchPurchaserType.*;
import static com.pantavanij.sourcingreq.services.util.Constant.REC_ID;
import static com.pantavanij.sourcingreq.services.util.Constant.TENANT;

@Slf4j
@RequiredArgsConstructor
@Service
public class PurchaserServiceImpl implements PurchaserService {

    private final EpAuthClient epAuthClient;
    private final PurchaserRepository purchaserRepository;
    private final TenantService tenantService;
    private final UaaService uaaService;

    @Override
    public List<OptionDetailDto> getPurchaserByTenantIdAndSearchTermAndCategoryId(Integer tenantId, String searchTerm, Integer categoryId) {
        List<Purchaser> perchasers =
                purchaserRepository.findByTenantIdAndCategoryIdAndCodeOrName(tenantId, categoryId, searchTerm.trim());
        return PurchaserMapper.INSTANCE.toPurchaserOptionDetailDto(perchasers);
    }

    @Override
    public List<Purchaser> getByTenantId(Integer tenantId) {
        return purchaserRepository.findByTenantId(tenantId);
    }

    @Override
    public PurchaserDto createPurchaser(PurchaserRequest purchaserRequest) {
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());

            Optional<Purchaser> purchaserDtoOptional = purchaserRepository.findFirstByTenantRecIdOrderBySequenceDesc(tenant.getRecId());
            if (purchaserDtoOptional.isPresent()) {
                purchaserRequest.setSequence(purchaserDtoOptional.get().getSequence() + 1);
            } else {
                purchaserRequest.setSequence(1);
            }

            Purchaser purchaser = purchaserRepository.save(getSubmitPurchaser(purchaserRequest));
            return RequestMapper.INSTANCE.toPurchaserDto(purchaser, timeZone);
        }

        public Purchaser getSubmitPurchaser(PurchaserRequest purchaserRequest) {
            Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
            Integer roleId = purchaserRequest.getRoleId();
            return Purchaser.builder()
                    .recId(purchaserRequest.getRecId())
                    .tenant(tenant)
                    .userId(purchaserRequest.getUserId())
                    .loginId(purchaserRequest.getLoginId())
                    .purchaserName(purchaserRequest.getPurchaserName())
                    .email(purchaserRequest.getEmail())
                    .phone(purchaserRequest.getPhone())
                    .roleId(roleId != null ? roleId : 0)
                    .sequence(purchaserRequest.getSequence())
                    .isDefault(purchaserRequest.isDefault())
                    .active(purchaserRequest.isActive())
                    .createdBy(AppUtil.getUserName())
                    .createdDate(DateTimeUtil.getTimestampUTC())
                    .updatedBy(AppUtil.getUserName())
                    .updatedDate(DateTimeUtil.getTimestampUTC())
                    .build();
    }


    @Override
    public PurchaserSearchDto searchPurchaserByCondition(PurchaserSearchRequest purchaserSearchRequest, Pageable pageable) {
        Page<Purchaser> purchaserPage = purchaserRepository.findAll(Specification.where(searchSpecificationByCondition(purchaserSearchRequest)), pageable);
        int totalPage = purchaserPage.getTotalPages();
        long total = purchaserPage.getTotalElements();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);

        List<PurchaserDto> purchaserDtoList = RequestMapper.INSTANCE.toPurchaserDtoList(purchaserPage.getContent(), timeZone);
        PurchaserSearchDto purchaserSearchDto = new PurchaserSearchDto();
        purchaserSearchDto.setPurchaserList(purchaserDtoList);
        purchaserSearchDto.setTotal(total);
        purchaserSearchDto.setTotalPage(totalPage);
        purchaserSearchDto.setPageSize(pageable.getPageSize());
        return purchaserSearchDto;
    }

    private Specification<Purchaser> searchSpecificationByCondition(PurchaserSearchRequest purchaserSearchRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
            if (tenant != null) {
                predicates.add(criteriaBuilder.equal(root.get(TENANT).get(REC_ID), tenant.getRecId()));
            }

            List<ConditionSearchRequest> conditionSearchRequests = purchaserSearchRequest.getConditionSearchList();
            if (conditionSearchRequests != null && !conditionSearchRequests.isEmpty()) {
                List<Predicate> orPredicates = new ArrayList<>();
                for (ConditionSearchRequest conditionSearchRequest : conditionSearchRequests) {
                    String searchField = conditionSearchRequest.getSearchField();
                    String searchValue = conditionSearchRequest.getSearchValue();

                    Predicate predicate = null;
                    if (StringUtils.isNotEmpty(searchField) && StringUtils.isNotEmpty(searchValue)) {
                        if (PURCHASER_NAME.description().equalsIgnoreCase(searchField)) {
                            predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%");
                        } else if (EMAIL.description().equalsIgnoreCase(searchField)) {
                            predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%");
                        } else if (CREATED_BY.description().equalsIgnoreCase(searchField)) {
                            predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%");
                        } else if (LOGIN_ID.description().equalsIgnoreCase(searchField)) {
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

    @Override
    public PurchaserDto findPurchaserByRecId(Integer purchaserId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        Optional<Purchaser> purchaserOptional = purchaserRepository.findPurchaserByRecIdAndTenant(purchaserId, tenant.getRecId());
        return purchaserOptional.map(purchaser -> PurchaserMapper.INSTANCE.toPurchaserDto(purchaser, timeZone)).orElse(null);
    }

    @Override
    public PurchaserDto updatePurchaser(PurchaserRequest purchaserRequest) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Optional<Purchaser> existingPurchaser = purchaserRepository.findPurchaserByRecIdAndTenant(purchaserRequest.getRecId(), tenant.getRecId());
        if (existingPurchaser.isPresent()) {
            purchaserRepository.reOrderOtherPurchaserSequence(tenant.getRecId(), existingPurchaser.get().getSequence(), purchaserRequest.getSequence());
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            Purchaser purchaserUpdate = getUpdatePurchaser(existingPurchaser.get(), purchaserRequest, tenant);
            purchaserRepository.save(purchaserUpdate);
            return RequestMapper.INSTANCE.toPurchaserDto(purchaserUpdate, timeZone);
        }
        return null;
    }

    @Override
    public PurchaserDto updatePurchaserSequence(SequenceRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Optional<Purchaser> purchaserOptional = purchaserRepository.findPurchaserByRecIdAndTenant(request.getRecId(), tenant.getRecId());
        if (purchaserOptional.isPresent()) {
           purchaserRepository.reOrderOtherPurchaserSequence(tenant.getRecId(), purchaserOptional.get().getSequence(), request.getSequence());
           Purchaser purchaserUpdateSequence = purchaserRepository.save(getUpdatePurchaserSequence(purchaserOptional.get(), request, tenant));
           return RequestMapper.INSTANCE.toPurchaserDto(purchaserUpdateSequence);
        }
        return null;
    }

    private Purchaser getUpdatePurchaserSequence(Purchaser purchaser, SequenceRequest request, Tenant tenant) {
        return Purchaser.builder()
                .recId(purchaser.getRecId())
                .tenant(tenant)
                .userId(purchaser.getUserId())
                .loginId(purchaser.getLoginId())
                .purchaserName(purchaser.getPurchaserName())
                .email(purchaser.getEmail())
                .phone(purchaser.getPhone())
                .roleId(purchaser.getRoleId())
                .sequence(request.getSequence())
                .isDefault(purchaser.isDefault())
                .active(purchaser.isActive())
                .createdBy(purchaser.getCreatedBy())
                .createdDate(purchaser.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .categoryPurchasers(purchaser.getCategoryPurchasers())
                .subCategoryPurchasers(purchaser.getSubCategoryPurchasers())
                .build();
    }

    public Purchaser getUpdatePurchaser(Purchaser purchaser, PurchaserRequest purchaserRequest, Tenant tenant) {
        return Purchaser.builder()
                .recId(purchaserRequest.getRecId())
                .tenant(tenant)
                .userId(purchaserRequest.getUserId())
                .loginId(purchaserRequest.getLoginId())
                .purchaserName(purchaserRequest.getPurchaserName())
                .email(purchaserRequest.getEmail())
                .phone(purchaserRequest.getPhone())
                .roleId(purchaserRequest.getRoleId())
                .sequence(purchaserRequest.getSequence())
                .isDefault(purchaserRequest.isDefault())
                .active(purchaserRequest.isActive())
                .createdBy(purchaser.getCreatedBy())
                .createdDate(purchaser.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .categoryPurchasers(purchaser.getCategoryPurchasers())
                .subCategoryPurchasers(purchaser.getSubCategoryPurchasers())
                .build();
    }

    @Override
    public boolean deletePurchaserByRecId(Integer purchaserId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Optional<Purchaser> purchaserDto = purchaserRepository.findPurchaserByRecIdAndTenant(purchaserId, tenant.getRecId());
        if (purchaserDto.isPresent()) {
            purchaserRepository.deletePurchaserByRecId(purchaserId);
            return true;
        }
        return false;
    }

    @Override
    public List<EPAuthUserDTO> getAllPurchaser(EPAuthUserSearchRequest epaAuthUserSearchRequest) {
        String[] privilegeCodes = new String[] {PURCHASER.privilegeCode()};
        EPAuthUserListResponse epaAuthUserListResponse = epAuthClient.getEPAuthUserList(privilegeCodes, epaAuthUserSearchRequest);
        if (epaAuthUserListResponse != null && epaAuthUserListResponse.getData() != null && !epaAuthUserListResponse.getData().isEmpty()) {
            return epaAuthUserListResponse.getData();
        }
        return null;
    }

    @Override
    public EPAuthPurchaserResponse getPurchaserListByConditions(PurchaserSearchRequest request) {
        EPAuthUserSearchRequest epAuthUserSearchRequest = EPAuthUserSearchRequest.builder()
                .conditionSearchList(request.getConditionSearchList())
                .tenantId(request.getTenantId())
                .page(request.getPage())
                .pageSize(request.getPageSize())
                .sortBy(request.getSortBy())
                .sortOrder(request.getSortOrder())
                .build();

        String[] privilegeCode = new String[] {DEPT_APPROVER.privilegeCode()};
        EPAuthUserListResponse epAuthUserListResponse = new EPAuthUserListResponse();
        try {
            epAuthUserListResponse = epAuthClient.getEPAuthUserList(privilegeCode, epAuthUserSearchRequest);
        } catch(Exception ex) {
            ex.getMessage();
        }

        List<EPAuthPurchaserDto> epAuthPurchaserDtoList = new ArrayList();

        if (epAuthUserListResponse.getData() == null) {
            return EPAuthPurchaserResponse.builder()
                    .data(null)
                    .page(epAuthUserListResponse.getPage())
                    .pageSize(epAuthUserListResponse.getPageSize())
                    .total(epAuthUserListResponse.getTotal())
                    .totalPage(epAuthUserListResponse.getTotalPage())
                    .build();
        }

        for (EPAuthUserDTO epAuthUserDTO: epAuthUserListResponse.getData()) {
            PurchaserDto savePurchaser = new PurchaserDto();
            savePurchaser.setPhone(epAuthUserDTO.getPhone());
            savePurchaser.setEmail(epAuthUserDTO.getEmail());
            savePurchaser.setUserId(epAuthUserDTO.getSysUserId());
            savePurchaser.setPurchaserName(epAuthUserDTO.getFullName());
            savePurchaser.setLoginId(epAuthUserDTO.getLoginId());
            this.savePurchaser(savePurchaser);

            epAuthPurchaserDtoList.add(
                    EPAuthPurchaserDto.builder()
                            .sysUserId(epAuthUserDTO.getSysUserId())
                            .loginId(epAuthUserDTO.getLoginId())
                            .fullName(epAuthUserDTO.getFullName())
                            .email(epAuthUserDTO.getEmail())
                            .mobilePhone(epAuthUserDTO.getMobilePhone())
                            .phone(epAuthUserDTO.getPhone())
                            .timezone(epAuthUserDTO.getTimezone())
                            .build());
        }
        return EPAuthPurchaserResponse.builder()
                .data(epAuthPurchaserDtoList)
                .page(epAuthUserListResponse.getPage())
                .pageSize(epAuthUserListResponse.getPageSize())
                .total(epAuthUserListResponse.getTotal())
                .totalPage(epAuthUserListResponse.getTotalPage())
                .build();
    }

    private Integer savePurchaser(PurchaserDto purchaserDto) {
        UserDto userDto = AppUtil.getUser();
        Tenant tenant = tenantService.findByCode(userDto.getTenantId());
        log.info("Save Report line for SysUserId : {}, username: {}", purchaserDto.getUserId(), purchaserDto.getPurchaserName());
        Purchaser purchaser = purchaserRepository.findPurchaserByTenantAndUserId(tenant, purchaserDto.getUserId())
                .orElse(new Purchaser());

        purchaser.setPhone(purchaserDto.getPhone());
        purchaser.setEmail(purchaserDto.getEmail());
        purchaser.setUserId(purchaserDto.getUserId());
        purchaser.setPurchaserName(purchaserDto.getPurchaserName());
        purchaser.setTenant(tenant);
        purchaser.setUpdatedBy(userDto.getUsername());
        purchaser.setUpdatedDate(DateTimeUtil.getTimestampUTC());
        purchaser.setLoginId(purchaserDto.getLoginId());

        if (org.apache.commons.lang3.StringUtils.isEmpty(purchaser.getCreatedBy())) {
            purchaser.setCreatedBy(userDto.getUsername());
        }
        if (purchaser.getCreatedDate() == null) {
            purchaser.setCreatedDate(DateTimeUtil.getTimestampUTC());
        }
        return purchaserRepository.save(purchaser).getRecId();
    }
}
