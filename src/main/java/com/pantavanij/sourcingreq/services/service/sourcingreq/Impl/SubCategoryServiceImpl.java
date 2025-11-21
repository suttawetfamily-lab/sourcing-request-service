package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.*;
import com.pantavanij.sourcingreq.services.domain.mapper.SubCategoryMapper;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

import java.util.*;

@RequiredArgsConstructor
@Service
public class SubCategoryServiceImpl implements SubCategoryService {
    private final ExistingPriceItemSubCategoryRepository existingPriceItemSubCategoryRepository;
    private final PurchaserRepository purchaserRepository;
    private final RequestSubCategoryRepository requestSubCategoryRepository;
    private final SubCategoryPurchaserRepository subCategoryPurchaserRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final TenantService tenantService;
    private final UaaService uaaService;

    @Override
    public List<OptionDto> getSubCategoryByTenantIdAndSearchTermAndCategoryId(Integer tenantId, String searchTerm, Integer categoryId) {
        List<SubCategory> subCategories =
                subCategoryRepository.findByTenantIdAndCategoryIdAndCodeOrName(tenantId, categoryId, searchTerm.trim());
        return SubCategoryMapper.INSTANCE.toSubCategoryOptionDto(subCategories);
    }

    @Override
    public List<SubCategoryDto> getSubCategoryByTenantId(Integer tenantId) {
        List<SubCategory> subCategories =
                subCategoryRepository.findByTenantId(tenantId);
        return SubCategoryMapper.INSTANCE.toSubCategoryDtoList(subCategories);
    }

    @Override
    public List<OptionDto> getSubCategoryOptionDtoByTenantId(Integer tenantId) {
        List<SubCategory> subCategories =
                subCategoryRepository.findByTenantId(tenantId);
        return SubCategoryMapper.INSTANCE.toSubCategoryOptionDto(subCategories);
    }

    public SubCategory getSubCategorySubmit(SubCategoryRequest request, Tenant tenant) {
        return SubCategory.builder()
                .recId(request.getRecId())
                .tenant(tenant)
                .category(Category.builder()
                        .recId(request.getCategoryId())
                        .build())
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
    @Synchronized
    public SubCategoryDto createSubCategory(SubCategoryRequest request) {
        try {
            Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
            if(request.isReOrderSequence()){
                Optional<SubCategory> subCategoryLastSequence = subCategoryRepository.findFirstByTenantRecIdAndCategoryRecIdOrderBySequenceDesc(tenant.getRecId(), request.getCategoryId());
                if (subCategoryLastSequence.isPresent()) {
                    if(request.getSequence() > subCategoryLastSequence.get().getSequence()){
                        request.setSequence(subCategoryLastSequence.get().getSequence() + 1);
                    } else {
                        subCategoryRepository.reOrderOtherSubcategorySequence(tenant.getRecId(), request.getCategoryId(), subCategoryLastSequence.get().getSequence() + 1, request.getSequence());
                    }
                } else {
                    request.setSequence(1);
                }
            }


            SubCategory subCategorySubmit = subCategoryRepository.save(getSubCategorySubmit(request, tenant));

            List<SubCategoryPurchaser> subCategoryPurchaserList = new ArrayList<>();
            if (request.getPurchasersId() != null && !request.getPurchasersId().isEmpty()) {
                for (Integer purchaserId : request.getPurchasersId()) {
                    Optional<Purchaser> purchaserOptional = purchaserRepository.findFirstByRecId(purchaserId);
                    if (purchaserOptional.isPresent()) {
                        SubCategoryPurchaser subCategoryPurchaser = getSubCatePurchaser(subCategorySubmit, purchaserOptional.get());
                        subCategoryPurchaserList.add(subCategoryPurchaser);
                    }
                }
            }
            if (!subCategoryPurchaserList.isEmpty()) {
                subCategoryPurchaserRepository.saveAll(subCategoryPurchaserList);
            }

            return SubCategoryMapper.INSTANCE.toSubCategoryDto(subCategorySubmit);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    public int deleteSubCategoryByRecId(Integer id, boolean isReOrderSequence) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        try {
            SubCategory subCategory = subCategoryRepository.findSubCategoryByRecId(id);
            if (subCategory != null && subCategory.getRecId() != null) {

                boolean notExistsExistingPriceItemSubCategory = existingPriceItemSubCategoryRepository.findBySubCategory_RecIdIn(Collections.singletonList(id)).isEmpty();
                boolean notExistsRequestSubCategory = requestSubCategoryRepository.findBySubCategory_RecIdIn(Collections.singletonList(id)).isEmpty();
                //boolean notExistsSubCategoryPurchaser = subCategoryPurchaserRepository.findBySubCategory_RecIdIn(Collections.singletonList(id)).isEmpty();

                if (notExistsExistingPriceItemSubCategory && notExistsRequestSubCategory) {
                    subCategoryPurchaserRepository.deleteAllBySubCategoryRecId(subCategory.getRecId());
                    subCategoryRepository.delete(subCategory);
                    if(isReOrderSequence) {
                        subCategoryRepository.reOrderSequenceByTenantRecIdAndCategoryRecId(tenant.getRecId(), subCategory.getCategory().getRecId());
                    }
                    return 1;
                }
                return -1;
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting sub category by ID: " + id);
        }
    }

    @Override
    public SubCategoryDto updateSubCategorySequence(SequenceRequest request) {
//        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
//        Optional<SubCategory> subCategoryOptional = subCategoryRepository.findByRecIdAndTenant(request.getRecId(), tenant);
//        if (subCategoryOptional.isPresent()) {
//            subCategoryRepository.reOrderOtherSubcategorySequence(tenant.getRecId(), subCategoryOptional.get().getSequence(), request.getSequence());
//            SubCategory subCategoryUpdateSequence = getSubCategoryUpdateSequence(subCategoryOptional.get(), request, tenant);
//            subCategoryRepository.save(subCategoryUpdateSequence);
//            return SubCategoryMapper.INSTANCE.toSubCategoryDto(subCategoryUpdateSequence);
//        }
        return null;
    }

    @Override
    public SubCategoryDto getSubCategoryById(Integer subCategoryId) {
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Optional<SubCategory> subCategoryOptional = subCategoryRepository.findByRecIdAndTenant(subCategoryId, tenant);
        return subCategoryOptional.map(subCategory -> SubCategoryMapper.INSTANCE.toSubCategoryDto(subCategory, timeZone)).orElse(null);
    }

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Override
    public SubCategoryDto updateSubCategory(SubCategoryRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        List<SubCategoryPurchaser> subCategoryPurchaserList = new ArrayList<>();

        try {
            Optional<SubCategory> subCategoryOptional = subCategoryRepository.findByRecId(request.getRecId());
            if (subCategoryOptional.isPresent()) {
                subCategoryPurchaserRepository.deleteAllBySubCategoryRecId(subCategoryOptional.get().getRecId());
                if(request.isReOrderSequence()){
                    subCategoryRepository.reOrderOtherSubcategorySequence(tenant.getRecId(), request.getCategoryId(), subCategoryOptional.get().getSequence(), request.getSequence());
                }
                SubCategory subCategoryUpdate = subCategoryRepository.save(getSubCategoryUpdate(subCategoryOptional.get(), request, tenant));

                if (request.getPurchasersId() != null && !request.getPurchasersId().isEmpty()) {
                    for (Integer purchaserId : request.getPurchasersId()) {
                        Optional<Purchaser> purchaserOptional = purchaserRepository.findFirstByRecId(purchaserId);
                        if (purchaserOptional.isPresent()) {
                            SubCategoryPurchaser subCategoryPurchaser = getSubCatePurchaser(subCategoryUpdate, purchaserOptional.get());
                            subCategoryPurchaserList.add(subCategoryPurchaser);
                        }
                    }
                }

                if (!subCategoryPurchaserList.isEmpty()) {
                    subCategoryPurchaserRepository.saveAll(subCategoryPurchaserList);
                }
                return SubCategoryMapper.INSTANCE.toSubCategoryDto(subCategoryUpdate);
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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

    public SubCategory getSubCategoryUpdateSequence(SubCategory subCategory, SequenceRequest request, Tenant tenant) {
        return SubCategory.builder()
                .recId(subCategory.getRecId())
                .tenant(tenant)
                .category(subCategory.getCategory())
                .code(subCategory.getCode())
                .name(subCategory.getName())
                .sequence(request.getSequence())
                .isDefault(subCategory.isDefault())
                .active(subCategory.isActive())
                .createdBy(subCategory.getCreatedBy())
                .createdDate(subCategory.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .purchaserList(new ArrayList<>())
                .build();
    }

    public SubCategoryPurchaser getSubCatePurchaser(SubCategory subCategory, Purchaser purchaser) {
        return SubCategoryPurchaser.builder()
                .id(new SubCategoryPurchaserKey())
                .subCategory(subCategory)
                .purchaser(purchaser)
                .build();
    }

}
