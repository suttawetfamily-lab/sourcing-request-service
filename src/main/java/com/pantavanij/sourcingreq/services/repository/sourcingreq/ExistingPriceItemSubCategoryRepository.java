package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExistingPriceItem;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExistingPriceItemSubCategory;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ExistingPriceItemSubCategoryRepository extends JpaRepository<ExistingPriceItemSubCategory, Integer> {

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "INSERT INTO ExistingPriceItemSubCategory(ExistingPriceItemId, SubCategoryId, SubCategoryCode, SubCategoryName) VALUES(:existingPriceItemId, :subCategoryId, :subCategoryCode, :subCategoryName)", nativeQuery = true)
    void saveExistingPriceItemSubCategory(@Param("existingPriceItemId") Long existingPriceItemId,
                               @Param("subCategoryId") Integer subCategoryId,
                               @Param("subCategoryCode") String subCategoryCode,
                               @Param("subCategoryName") String subCategoryName);

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE ExistingPriceItemSubCategory SET SubCategoryCode = :subCategoryCode, SubCategoryName = :subCategoryName WHERE ExistingPriceItemId = :existingPriceItemId AND SubCategoryId = :subCategoryId ", nativeQuery = true)
    void updateExistingPriceItemSubCategory(@Param("existingPriceItemId") Long existingPriceItemId,
                                 @Param("subCategoryId") Integer subCategoryId,
                                 @Param("subCategoryCode") String subCategoryCode,
                                 @Param("subCategoryName") String subCategoryName);


    List<ExistingPriceItemSubCategory> findExistingPriceItemSubCategorysByExistingPriceItem(ExistingPriceItem existingPriceItem);

    ExistingPriceItemSubCategory findExistingPriceItemSubCategoryByExistingPriceItemAndSubCategory(ExistingPriceItem existingPriceItem, SubCategory subCategory);

    void deleteExistingPriceItemSubCategoryByExistingPriceItem(ExistingPriceItem existingPriceItem);

    void deleteExistingPriceItemSubCategoryByExistingPriceItemAndSubCategory(ExistingPriceItem existingPriceItem, SubCategory subCategory);

    List<ExistingPriceItemSubCategory> findBySubCategory_RecIdIn(List<Integer> subCategoryIds);
}
