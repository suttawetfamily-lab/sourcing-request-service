package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Category;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExistingPriceItem;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExistingPriceItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ExistingPriceItemCategoryRepository extends JpaRepository<ExistingPriceItemCategory, Integer> {

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "INSERT INTO ExistingPriceItemCategory(ExistingPriceItemId, CategoryId, CategoryCode, CategoryName) VALUES(:existingPriceItemId, :categoryId, :categoryCode, :categoryName)", nativeQuery = true)
    void saveExistingPriceItemCategory(@Param("existingPriceItemId") Long existingPriceItemId,
                               @Param("categoryId") Integer categoryId,
                               @Param("categoryCode") String categoryCode,
                               @Param("categoryName") String categoryName);

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE ExistingPriceItemCategory SET CategoryCode = :categoryCode, CategoryName = :categoryName WHERE ExistingPriceItemId = :existingPriceItemId AND CategoryId = :categoryId ", nativeQuery = true)
    void updateExistingPriceItemCategory(@Param("existingPriceItemId") Long existingPriceItemId,
                                 @Param("categoryId") Integer categoryId,
                                 @Param("categoryCode") String categoryCode,
                                 @Param("categoryName") String categoryName);


    List<ExistingPriceItemCategory> findExistingPriceItemCategorysByExistingPriceItem(ExistingPriceItem existingPriceItem);

    List<ExistingPriceItemCategory> findByCategory_RecIdIn(List<Integer> categoriesId);

    ExistingPriceItemCategory findExistingPriceItemCategoryByExistingPriceItemAndCategory(ExistingPriceItem existingPriceItem, Category category);

    void deleteExistingPriceItemCategoryByExistingPriceItem(ExistingPriceItem existingPriceItem);

    void deleteExistingPriceItemCategoryByExistingPriceItemAndCategory(ExistingPriceItem existingPriceItem, Category category);

    int deleteByCategory_RecIdIn(List<Integer> categoriesId);
}
