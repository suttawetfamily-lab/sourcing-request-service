package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Department;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.mapper.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.enums.*;
import com.pantavanij.sourcingreq.services.exception.*;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.DepartmentRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestDepartmentRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.DepartmentService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UaaService;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.*;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.*;

import static com.pantavanij.sourcingreq.services.enums.SearchDepartmentType.*;
import static com.pantavanij.sourcingreq.services.util.Constant.REC_ID;
import static com.pantavanij.sourcingreq.services.util.Constant.TENANT;

@RequiredArgsConstructor
@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final RequestDepartmentRepository requestDepartmentRepository;
    private final TenantService tenantService;
    private final UaaService uaaService;

    @Override
    public List<DepartmentDto> getDepartmentBySearchTerm(String searchTerm, Integer organizationId) { // TODO null check tenant
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);
        List<Department> departmentList =
                departmentRepository.getDepartmentByTenantIdAndSearchTerm(tenant.getRecId(), searchTerm.trim(), organizationId);
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        List<DepartmentDto> departmentDtoLst = DepartmentMapper.INSTANCE.toDepartmentDtoList(departmentList, timeZone);
        return departmentDtoLst;
    }

    @Override
    public DepartmentDto getDepartmentByDepartmentCode(String departmentCode, Integer tenantId) {
        Department department = departmentRepository.getDepartmentByDepartmentCode(departmentCode, tenantId);
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        DepartmentDto departmentDto = DepartmentMapper.INSTANCE.toDepartmentDto(department, timeZone);
        return departmentDto;
    }

    @Override
    public List<DepartmentOptionDto> getDepartmentBySearchTerm(Integer tenantId, String searchTerm, Integer organizationId) {
        List<Department> departmentList = departmentRepository.getDepartmentByTenantIdAndSearchTerm(tenantId, searchTerm.trim(), organizationId);
        return DepartmentMapper.INSTANCE.toDepartmentOptionDto(departmentList);
    }


    @Override
    public DepartmentSearchDto searchDepartmentByCondition(DepartmentSearchRequest request, Pageable pageable) {
        Page<Department> departmentPage = departmentRepository.findAll(Specification.where(getSearchDepartmentByCondition(request)), pageable);
        int totalPage = departmentPage.getTotalPages();
        long total = departmentPage.getTotalElements();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);

        List<DepartmentDto> departmentDtoList = RequestMapper.INSTANCE.toDepartmentDtoList(departmentPage.getContent(), timeZone);
        DepartmentSearchDto departmentSearchDto = new DepartmentSearchDto();
        departmentSearchDto.setDepartmentList(departmentDtoList);
        departmentSearchDto.setTotalPage(totalPage);
        departmentSearchDto.setTotal(total);
        departmentSearchDto.setPageSize(pageable.getPageSize());
        return departmentSearchDto;
    }

    private Specification<Department> getSearchDepartmentByCondition(DepartmentSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
            if (tenant != null) {
                predicates.add(criteriaBuilder.equal(root.get(TENANT).get(REC_ID), tenant.getRecId()));
            }

            List<ConditionSearchRequest> conditionSearchRequestList = request.getConditionSearchList();
            if (conditionSearchRequestList != null && !conditionSearchRequestList.isEmpty()) {
                List<Predicate> orPredicates = new ArrayList<>();

                for (ConditionSearchRequest conditionSearchRequest : conditionSearchRequestList) {
                    String searchField = conditionSearchRequest.getSearchField();
                    String searchValue = conditionSearchRequest.getSearchValue();

                    Predicate predicate = null;
                    if (StringUtils.isNotEmpty(searchField) && StringUtils.isNotEmpty(searchValue)) {
                        if (NAME.description().equalsIgnoreCase(searchField)) {
                            predicate = criteriaBuilder.like(root.get(searchField), "%"+ searchValue + "%");
                        } else if (CODE.description().equalsIgnoreCase(searchField)) {
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

    @Override
    public DepartmentDto findDepartmentByRecIdAndTenantId(Integer departmentId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        Optional<Department> departments = departmentRepository.findDepartmentByRecIdAndTenantId(departmentId, tenant.getRecId());
        return departments.map(department -> RequestMapper.INSTANCE.toDepartmentDto(department, timeZone)).orElse(null);
    }

    @Override
    public DepartmentDto createDepartment(DepartmentRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);

        try {
            Optional<Department> departmentOpt = departmentRepository.findByCodeAndTenant_RecId(request.getCode(), tenant.getRecId());
            if (departmentOpt.isPresent()) {
                throw new BusinessException(String.format(ApiMessage.E7100.description(), "This department ID: " +request.getCode()+ " is already exists"));
            }

            Optional<Department> departmentMaxSequence = departmentRepository.findFirstByTenantRecIdOrderBySequenceDesc(tenant.getRecId());
            if (departmentMaxSequence.isPresent()) {
                if(request.getSequence() > departmentMaxSequence.get().getSequence()){
                    request.setSequence(departmentMaxSequence.get().getSequence() + 1);
                } else {
                    departmentRepository.adjustOtherDepartmentSequences(tenant.getRecId(), departmentMaxSequence.get().getSequence() + 1, request.getSequence());
                }
            } else {
                request.setSequence(1);
            }
            Department department = departmentRepository.save(getSubmitDepartment(request));
            return RequestMapper.INSTANCE.toDepartmentDto(department, timeZone);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

    }

    public Department getSubmitDepartment(DepartmentRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        return Department.builder()
                .recId(request.getRecId())
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
    }

    @Override
    public DepartmentDto updateDepartment(DepartmentRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        try {
            Optional<Department> department = departmentRepository.findDepartmentByRecIdAndTenantId(request.getRecId(), tenant.getRecId());
            if (department.isPresent()) {
                departmentRepository.adjustOtherDepartmentSequences(tenant.getRecId(), department.get().getSequence(), request.getSequence());
                Department departmentUpdate = departmentRepository.save(getUpdateDepartment(department.get(), request));
                return RequestMapper.INSTANCE.toDepartmentDto(departmentUpdate, timeZone);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public Department getUpdateDepartment(Department department, DepartmentRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        return Department.builder()
                .recId(department.getRecId())
                .tenant(tenant)
                .code(request.getCode())
                .name(request.getName())
                .sequence(request.getSequence() != null ? request.getSequence() : department.getSequence())
                .isDefault(request.isDefault())
                .active(request.isActive())
                .createdBy(department.getCreatedBy())
                .createdDate(department.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }

    @Override
    public DepartmentDto updateDepartmentSequence(DepartmentSequenceRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Optional<Department> department = departmentRepository.findDepartmentByRecIdAndTenantId(request.getRecId(), tenant.getRecId());
        if (department.isPresent()) {
            departmentRepository.adjustOtherDepartmentSequences(tenant.getRecId(), department.get().getSequence(), request.getSequence());
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            Department departmentUpdate = getUpdateDepartmentSequence(department.get(), request);
            departmentRepository.save(departmentUpdate);
            return RequestMapper.INSTANCE.toDepartmentDto(departmentUpdate, timeZone);
        }
        return null;
    }

    public Department getUpdateDepartmentSequence(Department department, DepartmentSequenceRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        return Department.builder()
                .recId(department.getRecId())
                .tenant(tenant)
                .code(department.getCode())
                .name(department.getName())
                .sequence(request.getSequence() != null ? request.getSequence() : department.getSequence())
                .isDefault(department.isDefault())
                .active(department.isActive())
                .createdBy(department.getCreatedBy())
                .createdDate(department.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }

    @Override
    public int deleteDepartmentByRecId(Integer departmentId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        try {
            Optional<Department> department = departmentRepository.findDepartmentByRecIdAndTenantId(departmentId, tenant.getRecId());
            boolean notExistsRequestDepartment = requestDepartmentRepository.findByDepartment_RecIdIn(Collections.singletonList(departmentId)).isEmpty();
            if (department.isPresent() && notExistsRequestDepartment) {
                Optional<Department> departmentMaxSequence = departmentRepository.findFirstByTenantRecIdOrderBySequenceDesc(tenant.getRecId());
                departmentMaxSequence.ifPresent(value -> departmentRepository.adjustOtherDepartmentSequences(tenant.getRecId(), department.get().getSequence(), value.getSequence() + 1));
                departmentRepository.delete(department.get());
                return 1;
            } else if(department.isPresent() && !notExistsRequestDepartment) {
                return -1;
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

    }

}
