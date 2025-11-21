package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.mapper.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.enums.*;
import com.pantavanij.sourcingreq.services.exception.*;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.MenuPrivilegeRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestItemReportRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import io.swagger.annotations.*;
import lombok.*;
import org.apache.commons.lang3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.SearchApproverType.CREATED_BY;
import static com.pantavanij.sourcingreq.services.util.CommonUtils.distinctByKey;
import static com.pantavanij.sourcingreq.services.util.Constant.*;

@RequiredArgsConstructor
@Service
public class MenuPrivilegeServiceImpl implements MenuPrivilegeService {

    private final MenuPrivilegeRepository menuPrivilegeRepository;
    private final RequestItemReportRepository requestItemReportRepository;
    private final TenantService tenantService;
    private final UaaService uaaService;

    @Override
    public List<MenuPrivilegeDto> getMenuPrivilege() {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Integer tenantId = tenant.getRecId();

        Map<String, String> privilegeMap = AppUtil.getPrivilegeScopes();
        List<MenuPrivilege> menuPrivileges = new ArrayList<>();
        List<String> privilegeList = privilegeMap.entrySet().stream()
                .map(p -> p.getKey().toLowerCase())
                .collect(Collectors.toList());

        List<MenuPrivilege> menuPrivilegeList = menuPrivilegeRepository.getMenuPrivilege(tenantId);

        for (MenuPrivilege menuPrivilege : menuPrivilegeList) {
            String privilegeCodeStr = menuPrivilege.getPrivilegeCode();

            if (privilegeCodeStr.contains(",")) {
                // -------- OR Condition --------
                String[] privilegeCodesOr = privilegeCodeStr.split(",");
                for (String privilegeCode : privilegeCodesOr) {
                    if (privilegeList.contains(privilegeCode.trim().toLowerCase())) {
                        menuPrivileges.add(menuPrivilege);
                        break; // เจอตัวเดียวก็พอ
                    }
                }

            } else if (privilegeCodeStr.contains("&")) {
                // -------- AND Condition --------
                String[] privilegeCodesAnd = privilegeCodeStr.split("&");
                boolean allMatch = true;
                for (String privilegeCode : privilegeCodesAnd) {
                    if (!privilegeList.contains(privilegeCode.trim().toLowerCase())) {
                        allMatch = false;
                        break;
                    }
                }
                if (allMatch) {
                    menuPrivileges.add(menuPrivilege);
                }

            } else {
                // -------- Single privilegeCode --------
                if (privilegeList.contains(privilegeCodeStr.trim().toLowerCase())) {
                    menuPrivileges.add(menuPrivilege);
                }
            }
        }

        List<RequestItemReport> requestItemReportList =
                requestItemReportRepository.findRequestItemReportByTenant(tenant.getRecId());

        return MenuPrivilegeMapper.INSTANCE.toMenuPrivilegeDtoList(menuPrivileges, requestItemReportList);
    }


    @Override
    public List<MenuPrivilegeObjDto> getMenuPrivilegeByTypeIdPrivilegeCode(Integer typeId, String privilegeCode) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Integer tenantId = tenant.getRecId();
        List<MenuPrivilege> menuPrivilegeList = menuPrivilegeRepository.findByTenantIdAndTypeIdAndPrivilegeCode(tenantId, typeId, privilegeCode);
        return MenuPrivilegeMapper.INSTANCE.toMenuPrivilegeDynamicDtoList(menuPrivilegeList);
    }

    @Override
    public MenuPrivilegeDto checkPermission(String pathUrl, Integer typeId) {
        Map<String, String> privilegeMap = AppUtil.getPrivilegeScopes();
        return getMenuPrivilegeByPathUrl(pathUrl, typeId, privilegeMap);
    }

    @Override
    public MenuPrivilegeDto checkPermission(String pathUrl, Integer typeId, Map<String, String> privilegeMap) {
        return getMenuPrivilegeByPathUrl(pathUrl, typeId, privilegeMap);
    }

    @Override
    public MenuPrivilegeDto getMenuPrivilegeById(Integer menuPrivilegeId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        try {
            Optional<MenuPrivilege> menuPrivilegeOpt = menuPrivilegeRepository.findByTenantRecIdAndRecId(tenant.getRecId(), menuPrivilegeId);
            if (menuPrivilegeOpt.isPresent()) {
                MenuPrivilege menuPrivilege = menuPrivilegeOpt.get();
                return MenuPrivilegeMapper.INSTANCE.toMenuPrivilegeDto(menuPrivilege);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ApiMessage.E7100, String.format(ApiMessage.E7100.description(), e.getMessage()));
        }
    }

    @Override
    public List<OptionDto> getAllMenuPrivilegeCode() {
        try {
            List<MenuPrivilege> menuPrivilegeList = menuPrivilegeRepository.findAll();
            if (!menuPrivilegeList.isEmpty()) {
                List<OptionDto> optionDtoList = MenuPrivilegeMapper.INSTANCE.toOptionDtoList(menuPrivilegeList);
                return optionDtoList.stream().distinct().collect(Collectors.toList());
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ApiMessage.E7100, String.format(ApiMessage.E7100.description(), e.getMessage()));
        }

    }

    @Override
    public MenuPrivilegeSearchDto searchMenuPrivilegeByCondition(MenuPrivilegeSearchRequest request, Pageable pageable) {
        Page<MenuPrivilege> menuPrivilegePage = menuPrivilegeRepository.findAll(Specification.where(searchMenuPrivilegeListByCondition(request)), pageable);
        int totalPage = menuPrivilegePage.getTotalPages();
        long total = menuPrivilegePage.getTotalElements();

        List<MenuPrivilegeDto> menuPrivilegeSearchDtoList = MenuPrivilegeMapper.INSTANCE.toMenuPrivilegeDtoList(menuPrivilegePage.getContent());
        MenuPrivilegeSearchDto menuPrivilegeSearchDto = new MenuPrivilegeSearchDto();
        menuPrivilegeSearchDto.setMenuPrivilegeList(menuPrivilegeSearchDtoList);
        menuPrivilegeSearchDto.setTotal(total);
        menuPrivilegeSearchDto.setTotalPage(totalPage);
        menuPrivilegeSearchDto.setPage(pageable.getPageNumber());
        return menuPrivilegeSearchDto;
    }

    @Override
    public Integer createMenuPrivilege(MenuPrivilegeRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        try {
            Optional<MenuPrivilege> menuPrivilegeOpt = menuPrivilegeRepository.findByTenantRecIdAndRecId(tenant.getRecId(), request.getRecId());

            Optional<MenuPrivilege> menuPrivilegeMaxSequence = menuPrivilegeRepository.findFirstByTenantRecIdAndMenuNameOrderBySequenceDesc(tenant.getRecId(), request.getMenuName());
            if (menuPrivilegeMaxSequence.isPresent()) {
                if(request.getSequence() > menuPrivilegeMaxSequence.get().getSequence()){
                    request.setSequence(menuPrivilegeMaxSequence.get().getSequence() + 1);
                } else {
                    menuPrivilegeRepository.reOrderOtherMenuPrivilegeSequence(tenant.getRecId(), menuPrivilegeMaxSequence.get().getMenuName(),menuPrivilegeMaxSequence.get().getSequence() + 1, request.getSequence());
                }
            } else {
                request.setSequence(1);
            }

            if (menuPrivilegeOpt.isEmpty()) {
                MenuPrivilege menuPrivilege = MenuPrivilege.builder()
                        .tenant(tenant)
                        .pathUrl(request.getPathUrl())
                        .typeId(request.getTypeId())
                        .privilegeCode(request.getPrivilegeCode())
                        .label(request.getLabel())
                        .menuName(request.getMenuName())
                        .sequence(request.getSequence())
                        .active(request.isActive())
                        .isDefault(request.isDefault())
                        .createdBy(AppUtil.getUserName())
                        .createdDate(DateTimeUtil.getTimestampUTC())
                        .updatedBy(AppUtil.getUserName())
                        .updatedDate(DateTimeUtil.getTimestampUTC())
                        .build();
                return menuPrivilegeRepository.save(menuPrivilege).getRecId();
            }
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ApiMessage.E7100, String.format(ApiMessage.E7100.description(), e.getMessage()));
        }
    }

    @Override
    public Integer updateMenuPrivilege(MenuPrivilegeRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        try {
            Optional<MenuPrivilege> menuPrivilegeOpt = menuPrivilegeRepository.findByTenantRecIdAndRecId(tenant.getRecId(), request.getRecId());
            if (menuPrivilegeOpt.isPresent()) {
                menuPrivilegeRepository.reOrderOtherMenuPrivilegeSequence(tenant.getRecId(), menuPrivilegeOpt.get().getMenuName() ,menuPrivilegeOpt.get().getSequence(), request.getSequence());
                MenuPrivilege menuPrivilege = menuPrivilegeOpt.get();
                menuPrivilege.setPathUrl(request.getPathUrl());
                menuPrivilege.setTypeId(request.getTypeId());
                menuPrivilege.setPrivilegeCode(request.getPrivilegeCode());
                menuPrivilege.setLabel(request.getLabel());
                menuPrivilege.setMenuName(request.getMenuName());
                menuPrivilege.setSequence(request.getSequence() != null ? request.getSequence() : menuPrivilegeOpt.get().getSequence());
                menuPrivilege.setActive(request.isActive());
                menuPrivilege.setDefault(request.isDefault());
                menuPrivilege.setCreatedBy(menuPrivilege.getCreatedBy());
                menuPrivilege.setCreatedDate(menuPrivilege.getCreatedDate());
                menuPrivilege.setUpdatedBy(AppUtil.getUserName());
                menuPrivilege.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                return menuPrivilegeRepository.save(menuPrivilege).getRecId();
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ApiMessage.E7100, String.format(ApiMessage.E7100.description(), e.getMessage()));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Override
    public Integer deleteMenuPrivilegeById(Integer menuPrivilegeId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        try {
            Optional<MenuPrivilege> menuPrivilegeOpt = menuPrivilegeRepository.findByTenantRecIdAndRecId(tenant.getRecId(), menuPrivilegeId);
            if (menuPrivilegeOpt.isPresent()) {
                MenuPrivilege menuPrivilege = menuPrivilegeOpt.get();
                boolean notExistsInRequestItemReport = requestItemReportRepository.findByMenuPrivilege_RecIdIn(Collections.singletonList(menuPrivilege.getRecId())).isEmpty();
                if (notExistsInRequestItemReport) {
                    Optional<MenuPrivilege> menuPrivilegeMaxSequence = menuPrivilegeRepository.findFirstByTenantRecIdAndMenuNameOrderBySequenceDesc(tenant.getRecId(), menuPrivilegeOpt.get().getMenuName());
                    menuPrivilegeMaxSequence.ifPresent(value -> menuPrivilegeRepository.reOrderOtherMenuPrivilegeSequence(tenant.getRecId(), menuPrivilegeOpt.get().getMenuName() ,menuPrivilegeOpt.get().getSequence(), value.getSequence() + 1));
                    menuPrivilegeRepository.delete(menuPrivilege);
                    return 1;
                }
                return -1;
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ApiMessage.E7100, String.format(ApiMessage.E7100.description(), e.getMessage()));
        }
    }

    private Specification<MenuPrivilege> searchMenuPrivilegeListByCondition(MenuPrivilegeSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
            if (tenant != null) {
                predicates.add(criteriaBuilder.equal(root.get(TENANT).get(REC_ID), tenant.getRecId()));
            }

            List<ConditionSearchRequest> conditionSearchRequestList = request.getConditionSearchList();
            if (conditionSearchRequestList != null && !conditionSearchRequestList.isEmpty()) {
                List<Predicate> orPredicates = new ArrayList<>();
                List<Predicate> andPredicates = new ArrayList<>();
                for (ConditionSearchRequest condition : conditionSearchRequestList) {
                    String searchField = condition.getSearchField();
                    String searchValue = condition.getSearchValue();

                    Predicate predicate = null;
                    if (StringUtils.isNotEmpty(searchField) && StringUtils.isNotEmpty(searchValue)) {
                        if(MENU_NAME.equalsIgnoreCase(searchField)) {
                            andPredicates.add(criteriaBuilder.like(root.get(searchField), "%" + searchValue.toLowerCase() + "%"));
                        } else if (PATH_URL.equalsIgnoreCase(searchField)) {
                            predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%");
                        } else if (Constant.PRIVILEGE_CODE.equalsIgnoreCase(searchField)) {
                            predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%");
                        }  else {
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
                if (!andPredicates.isEmpty()) {
                    predicates.add(criteriaBuilder.and(andPredicates.toArray(new Predicate[0])));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private MenuPrivilegeDto getMenuPrivilegeByPathUrl(String pathUrl, Integer typeId, Map<String, String> privilegeMap) {
        try {
            List<MenuPrivilege> menuPrivilegeList = menuPrivilegeRepository.findAll(Specification.where(getSpecificationByCondition(pathUrl, typeId)));
            List<MenuPrivilege> menuPrivileges = new ArrayList<>();
            List<String> privilegeList = privilegeMap.entrySet().stream().map(p -> p.getKey().toLowerCase()).collect(Collectors.toList());

            for (MenuPrivilege menuPrivilege : menuPrivilegeList) {
                String[] privilegeCodes = menuPrivilege.getPrivilegeCode().split(",");
                for (String privilegeCode: privilegeCodes) {
                    if (privilegeList.contains(privilegeCode)) {
                        menuPrivileges.add(menuPrivilege);
                        break;
                    }
                }
            }

            List<MenuPrivilegeDto> menuPrivilegeDtoList = MenuPrivilegeMapper.INSTANCE.toMenuPrivilegeDtoList(menuPrivileges);
            return menuPrivilegeDtoList.stream()
                    .findFirst()
                    .orElse(new MenuPrivilegeDto());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ApiMessage.E7100, String.format(ApiMessage.E7100.description(), e.getMessage()));
        }

    }

    private Specification<MenuPrivilege> getSpecificationByCondition(String pathUrl, Integer typeId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        return (root, query, criteriaBuilder) -> {
            Predicate pathPredicate = criteriaBuilder.equal(root.get(PATH_URL), pathUrl);
            Predicate tenantPredicate = criteriaBuilder.equal(root.get("tenant"), tenant);
            Predicate typePredicate = criteriaBuilder.equal(root.get("typeId"), typeId);
            return criteriaBuilder.and(pathPredicate, tenantPredicate, typePredicate);
        };
    }

    @Override
    public MenuPrivilegeDto updateMenuPrivilegeSequence(SequenceRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Optional<MenuPrivilege> menuPrivilegeCurrencyOpt = menuPrivilegeRepository.findByTenantRecIdAndRecId(tenant.getRecId(), request.getRecId());
        if (menuPrivilegeCurrencyOpt.isPresent()) {
            MenuPrivilege menuPrivilegeUnitExist = menuPrivilegeCurrencyOpt.get();
            menuPrivilegeRepository.reOrderOtherMenuPrivilegeSequence(tenant.getRecId(), menuPrivilegeUnitExist.getMenuName(), menuPrivilegeUnitExist.getSequence(), request.getSequence());

            menuPrivilegeUnitExist.setSequence(request.getSequence());

            MenuPrivilege updateSequence = menuPrivilegeRepository.save(menuPrivilegeUnitExist);
            return MenuPrivilegeMapper.INSTANCE.toMenuPrivilegeDto(updateSequence);
        }
        return null;
    }

}
