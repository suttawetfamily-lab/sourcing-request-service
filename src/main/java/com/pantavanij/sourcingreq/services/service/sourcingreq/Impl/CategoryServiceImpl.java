package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.dto.category.CategoryDto;
import com.pantavanij.sourcingreq.services.domain.dto.category.CategorySearchDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.*;
import com.pantavanij.sourcingreq.services.domain.mapper.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.util.Constant.*;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryPurchaserRepository categoryPurchaserRepository;
    private final ExistingPriceItemCategoryRepository existingPriceItemCategoryRepository;
    private final PurchaserRepository purchaserRepository;
    private final RequestCategoryRepository requestCategoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final SubCategoryPurchaserRepository subCategoryPurchaserRepository;
    private final TenantService tenantService;
    private final UaaService uaaService;

    @Override
    public List<OptionDto> getCategoryByTenantIdAndSearchTerm(Integer tenantId, String searchTerm, Integer organizationId) {
        List<Category> categories = categoryRepository.findByTenantIdAndCodeOrName(tenantId, searchTerm.trim(), organizationId);
        return CategoryMapper.INSTANCE.toCategoryOptionDto(categories);
    }

    @Override
    public List<CategoryDto> getCategoryByTenant(Integer tenantId) {
        List<Category> categories = categoryRepository.findByTenantId(tenantId);
        return CategoryMapper.INSTANCE.toCategoryDtoList(categories);
    }

    @Override
    public CategorySearchDto searchCategoryByCondition(CategorySearchRequest searchRequest, Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(Specification.where(getSpecificationByCondition(searchRequest)), pageable);
        int totalPage = categories.getTotalPages();
        long total = categories.getTotalElements();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        List<CategoryDto> resultList = CategoryMapper.INSTANCE.toCategoryDtoList(categories.getContent(), timeZone);

        resultList = resultList.stream()
                .peek(categoryDto -> {
                    categoryDto.setCreatedByName(UserDetailServiceUtil.getFullName(categoryDto.getCreatedBy()));
                    categoryDto.setUpdatedByName(categoryDto.getUpdatedBy() != null ? UserDetailServiceUtil.getFullName(categoryDto.getUpdatedBy()) : null);
                }).collect(Collectors.toList());

        CategorySearchDto categorySearchDto = new CategorySearchDto();
        categorySearchDto.setCategoryDtoList(resultList);
        categorySearchDto.setTotal(total);
        categorySearchDto.setTotalPage(totalPage);
        categorySearchDto.setPageSize(resultList.size());
        return categorySearchDto;
    }

    @Override
    public CategoryDto findCategoryByRecId(Integer categoryRecId) {
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        Category category = categoryRepository.findCategoryByRecId(categoryRecId);
        if (category != null && category.getTenant() != null) {
            CategoryDto categoryDto = CategoryMapper.INSTANCE.toCategoryDto(category, timeZone);

            List<SubCategoryDto> subCategoryDtoList = new ArrayList<>();
            for (SubCategory subCategory : category.getSubCategoryList()) {
                SubCategoryDto subCategoryDto = SubCategoryMapper.INSTANCE.toSubCategoryDto(subCategory, timeZone);
                subCategoryDtoList.add(subCategoryDto);
            }

            subCategoryDtoList = subCategoryDtoList.stream()
                    .sorted(Comparator.comparing(SubCategoryDto::getSequence))
                    .collect(Collectors.toList());

            categoryDto.setSubCategoryList(subCategoryDtoList);
            return categoryDto;
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Synchronized
    public CategoryDto saveCategory(CategoryRequest request) {
        List<CategoryPurchaser> categoryPurchaserList = new ArrayList<>();
        List<SubCategoryPurchaser> subCategoryPurchaserList = new ArrayList<>();
        try {
            Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
            Optional<Category> categoryOpt = categoryRepository.findByTenantAndCode(tenant,request.getCode());
            if (categoryOpt.isPresent()) {
                throw new BusinessException(String.format(ApiMessage.E7100.description(), "This category ID: " +request.getCode()+ " is already exists"));
            }

            Optional<Category> categoryLastSequence = categoryRepository.findFirstByTenantRecIdOrderBySequenceDesc(tenant.getRecId());
            if (categoryLastSequence.isPresent()) {
                if(request.getSequence() > categoryLastSequence.get().getSequence()){
                    request.setSequence(categoryLastSequence.get().getSequence() + 1);
                } else {
                    categoryRepository.reOrderOtherCategorySequence(tenant.getRecId(), categoryLastSequence.get().getSequence() + 1, request.getSequence());
                }
            } else {
                request.setSequence(1);
            }

            Category category = categoryRepository.save(getCategorySubmit(request, tenant));
            if (request.getPurchasersId() != null && !request.getPurchasersId().isEmpty()) {
                for (Integer purchaserId: request.getPurchasersId()) {
                    Optional<Purchaser> purchaserOptional = purchaserRepository.findFirstByRecId(purchaserId);
                    if (purchaserOptional.isPresent()) {
                        CategoryPurchaser categoryPurchaser = getCategoryPurchaser(category, purchaserOptional.get());
                        categoryPurchaserList.add(categoryPurchaser);
                    }
                }
            }
            if (!categoryPurchaserList.isEmpty()) {
                categoryPurchaserRepository.saveAll(categoryPurchaserList);
            }

            // Sub-category
            List<SubCategory> subCategoryList = new ArrayList<>();
            if (request.getSubCategoryRequestList() != null && !request.getSubCategoryRequestList().isEmpty()) {
                int count = 0;
                for (SubCategoryRequest subCategoryRequest : request.getSubCategoryRequestList()) {
                    if (subCategoryRequest.getSequence() == 0) {
                        subCategoryRequest.setSequence(count += 1);
                    }
                    subCategoryList.add(getSubCategorySubmit(subCategoryRequest, category, tenant));
                }
            }

            if (!subCategoryList.isEmpty()) {
                List<SubCategory> subCategorySaveList = subCategoryRepository.saveAll(subCategoryList);

                for (int i = 0; i < subCategorySaveList.size(); i++) {
                    SubCategory saveSubCategory = subCategorySaveList.get(i);
                    List<Integer> purchaserIdList = request.getSubCategoryRequestList().get(i).getPurchasersId();

                    for (Integer purchaserId : purchaserIdList) {
                        Optional<Purchaser> purchaserOptional = purchaserRepository.findFirstByRecId(purchaserId);
                        if (purchaserOptional.isPresent()) {
                            SubCategoryPurchaser subCategoryPurchaser = getSubCatePurchaser(saveSubCategory, purchaserOptional.get());
                            subCategoryPurchaserList.add(subCategoryPurchaser);
                        }
                    }
                }
                if (!subCategoryPurchaserList.isEmpty()) {
                    subCategoryPurchaserRepository.saveAll(subCategoryPurchaserList);
                }
            }
            return CategoryMapper.INSTANCE.toCategoryDto(category);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public SubCategoryPurchaser getSubCatePurchaser(SubCategory subCategory, Purchaser purchaser) {
        return SubCategoryPurchaser.builder()
                .id(new SubCategoryPurchaserKey())
                .subCategory(subCategory)
                .purchaser(purchaser)
                .build();
    }

    public SubCategory getSubCategorySubmit(SubCategoryRequest request, Category category, Tenant tenant) {
        return SubCategory.builder()
                .recId(request.getRecId())
                .tenant(tenant)
                .category(category)
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
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    public CategoryDto updateCategory(CategoryRequest request, List<Purchaser> purchaserList) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        List<CategoryPurchaser> categoryPurchaserList = new ArrayList<>();
        List<SubCategoryPurchaser> subCategoryPurchaserList = new ArrayList<>();

        try {
            Optional<Category> categoryOptional = categoryRepository.findFirstByRecId(request.getRecId());
            if (categoryOptional.isPresent()) {
                categoryPurchaserRepository.deleteByCategory_RecIdIn(Collections.singletonList(categoryOptional.get().getRecId()));
                categoryRepository.reOrderOtherCategorySequence(tenant.getRecId(), categoryOptional.get().getSequence(), request.getSequence());
                Category category = categoryRepository.save(getCategoryUpdate(categoryOptional.get(), request));

                for (Integer purchaserId: request.getPurchasersId()) {
                    if (purchaserId != null && !purchaserList.isEmpty()) {
                        Purchaser purchaserResult = purchaserList.stream()
                                .filter(purchaser -> purchaser.getRecId().equals(purchaserId))
                                .findFirst()
                                .orElse(null);

                        if (purchaserResult != null) {
                            CategoryPurchaser categoryPurchaser = getCategoryPurchaser(category, purchaserResult);
                            categoryPurchaserList.add(categoryPurchaser);
                        }
                    }
                }

                if (!categoryPurchaserList.isEmpty()) {
                    categoryPurchaserRepository.saveAll(categoryPurchaserList);
                }

                // Sub-category
                List<SubCategory> subCategoryList = new ArrayList<>();
                List<Integer> subCategoryPurchaserDeleteList = new ArrayList<>();
                Map<Integer, List<Integer>> sequenceRequestMap = new HashMap<>();
                if (!request.getSubCategoryRequestList().isEmpty()) {
                    for (SubCategoryRequest subCategoryRequest : request.getSubCategoryRequestList()) {
                        Optional<SubCategory> subCategoryOptional = subCategoryRepository.findByRecId(subCategoryRequest.getRecId());
                        if (subCategoryOptional.isPresent()) {
                            subCategoryPurchaserDeleteList.add(subCategoryOptional.get().getRecId());
                            subCategoryList.add(getSubCategoryUpdate(subCategoryOptional.get(), subCategoryRequest, tenant));
                            sequenceRequestMap.put(subCategoryRequest.getRecId(), new ArrayList<>(
                                    Arrays.asList(tenant.getRecId(), subCategoryOptional.get().getSequence() + 1, subCategoryRequest.getSequence(), category.getRecId())));
                        }
                    }
                    if (!subCategoryPurchaserDeleteList.isEmpty()) {
                        subCategoryPurchaserRepository.deleteAllBySubCategoryRecIdIn(subCategoryPurchaserDeleteList);
                    }
                    if (!subCategoryList.isEmpty()){
                        if (!sequenceRequestMap.isEmpty()) {
                            sequenceRequestMap.forEach((key, value) -> {
                                Integer tenantId = value.get(0);
                                Integer currentSequence = value.get(1);
                                Integer newSequence = value.get(2);
                                Integer categoryId = value.get(3);
                                subCategoryRepository.reOrderOtherSubcategorySequence(tenantId, currentSequence, newSequence, categoryId);
                            });
                        }
                        List<SubCategory> subCategorySaveList = subCategoryRepository.saveAll(subCategoryList);

                        for (int i = 0; i < subCategorySaveList.size(); i++) {
                            SubCategory saveSubCategory = subCategorySaveList.get(i);
                            List<Integer> purchaserIdList = request.getSubCategoryRequestList().get(i).getPurchasersId();

                            for (Integer purchaserId : purchaserIdList) {
                                Optional<Purchaser> purchaserOptional = purchaserRepository.findFirstByRecId(purchaserId);
                                if (purchaserOptional.isPresent()) {
                                    SubCategoryPurchaser subCategoryPurchaser = getSubCatePurchaser(saveSubCategory, purchaserOptional.get());
                                    subCategoryPurchaserList.add(subCategoryPurchaser);
                                }
                            }
                        }
                        if (!subCategoryPurchaserList.isEmpty()) {
                            subCategoryPurchaserRepository.saveAll(subCategoryPurchaserList);
                        }
                    }
                }
                return CategoryMapper.INSTANCE.toCategoryDto(category);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    public int deleteCategory(Integer categoryId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        try {
            Optional<Category> categoryOpt = categoryRepository.findFirstByRecId(categoryId);
            if (categoryOpt.isPresent()) {
                Category category = categoryOpt.get();
                //boolean notExistsCategoryPurchaser = categoryPurchaserRepository.findByCategory_RecIdIn(Collections.singletonList(category.getRecId())).isEmpty();
                boolean notExistsRequestCategory = requestCategoryRepository.findByCategory_RecIdIn(Collections.singletonList(category.getRecId())).isEmpty();
                boolean notExistsExistingPriceItemCategory = existingPriceItemCategoryRepository.findByCategory_RecIdIn(Collections.singletonList(category.getRecId())).isEmpty();

                if (notExistsRequestCategory && notExistsExistingPriceItemCategory) {
                    List<SubCategory> subCategoryList = subCategoryRepository.findByTenantRecIdAndCategory_RecId(tenant.getRecId(), category.getRecId());
                    for (SubCategory subCategory : subCategoryList) {
                        subCategoryPurchaserRepository.deleteAllBySubCategoryRecId(subCategory.getRecId());
                    }
                    categoryPurchaserRepository.deleteAllByCategoryRecId(category.getRecId());
                    subCategoryRepository.deleteAllByTenantRecIdAndCategoryRecId(tenant.getRecId(), category.getRecId());
                    boolean isDeleted = categoryRepository.deleteByRecId(category.getRecId()) >= 1;
                    if (isDeleted) {
                        List<Category> categoryList = categoryRepository.findByTenantId(tenant.getRecId());
                        if (!categoryList.isEmpty()) {
                            categoryRepository.reOrderSequenceByTenantRecId(tenant.getRecId());
                        }
                    }
                    return 1;
                }
                return -1;
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while deleting Category by ID: " + categoryId);
        }
    }

    @Override
    public CategoryDto updateCategorySequence(SequenceRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Optional<Category> categoryOptional = categoryRepository.findByRecIdAndTenant(request.getRecId(), tenant);
        if (categoryOptional.isPresent()) {
            categoryRepository.reOrderOtherCategorySequence(tenant.getRecId(), categoryOptional.get().getSequence(), request.getSequence());
            Category categoryUpdateSequence = getCategoryUpdateSequence(categoryOptional.get(), request, tenant);
            categoryRepository.save(categoryUpdateSequence);
            return CategoryMapper.INSTANCE.toCategoryDto(categoryUpdateSequence);
        }
        return null;
    }

    private Category getCategoryUpdateSequence(Category category, SequenceRequest request, Tenant tenant) {
        return Category.builder()
                .recId(category.getRecId())
                .tenant(tenant)
                .code(category.getCode())
                .name(category.getName())
                .sequence(request.getSequence())
                .isDefault(category.isDefault())
                .active(category.isActive())
                .createdBy(category.getCreatedBy())
                .createdDate(category.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .subCategoryList(category.getSubCategoryList())
                .build();
    }

    private Category getCategoryUpdate(Category category, CategoryRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        return Category.builder()
                .recId(request.getRecId())
                .tenant(tenant)
                .code(request.getCode())
                .name(request.getName())
                .sequence(request.getSequence())
                .isDefault(request.isDefault())
                .active(request.isActive())
                .createdBy(category.getCreatedBy())
                .createdDate(category.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .subCategoryList(new ArrayList<>())
                .build();
    }

    private CategoryPurchaser getCategoryPurchaser(Category category, Purchaser purchaser) {
        return CategoryPurchaser.builder()
                .id(new CategoryPurchaserKey())
                .category(category)
                .purchaser(purchaser)
                .build();
    }

    private Category getCategorySubmit(CategoryRequest request, Tenant tenant) {
        return Category.builder()
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

    public SubCategory getSubCategoryUpdate(SubCategory subCategory, SubCategoryRequest request, Tenant tenant) {
        return SubCategory.builder()
                .recId(subCategory.getRecId())
                .tenant(tenant)
                .category(Category.builder()
                        .recId(request.getCategoryId())
                        .build())
                .code(request.getCode())
                .name(request.getName())
                .sequence(request.getSequence())
                .isDefault(request.isDefault())
                .active(request.isActive())
                .createdBy(subCategory.getCreatedBy())
                .createdDate(subCategory.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .purchaserList(new ArrayList<>())
                .build();
    }


    private Specification<Category> getSpecificationByCondition(CategorySearchRequest searchRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
            if (tenant != null && tenant.getRecId() != null) {
                predicates.add(criteriaBuilder.equal(root.get(TENANT).get(REC_ID), tenant.getRecId()));
            }

            List<ConditionSearchRequest> conditionSearchRequestList = searchRequest.getConditionSearchList();
            if (conditionSearchRequestList != null && !conditionSearchRequestList.isEmpty()) {
                List<Predicate> orPredicates = new ArrayList<>();
                for (ConditionSearchRequest condition : conditionSearchRequestList) {
                    String searchField = condition.getSearchField();
                    String searchValue = condition.getSearchValue();

                    Predicate predicate = null;
                    if (!StringUtils.isEmpty(searchField) && !StringUtils.isEmpty(searchValue)) {
                        predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue.toLowerCase() + "%");
                    }
                    if (predicate != null) {
                        orPredicates.add(predicate);
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
