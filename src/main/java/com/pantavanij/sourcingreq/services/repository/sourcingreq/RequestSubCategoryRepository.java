package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestSubCategory;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestSubCategoryRepository extends JpaRepository<RequestSubCategory, Integer> {

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "INSERT INTO RequestSubCategory(RequestId, SubCategoryId, SubCategoryCode, SubCategoryName) VALUES(:requestId, :subCategoryId, :subCategoryCode, :subCategoryName)", nativeQuery = true)
    void saveRequestSubCategory(@Param("requestId") Long requestId,
                               @Param("subCategoryId") Integer subCategoryId,
                               @Param("subCategoryCode") String subCategoryCode,
                               @Param("subCategoryName") String subCategoryName);

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE RequestSubCategory SET SubCategoryCode = :subCategoryCode, SubCategoryName = :subCategoryName WHERE RequestId = :requestId AND SubCategoryId = :subCategoryId ", nativeQuery = true)
    void updateRequestSubCategory(@Param("requestId") Long requestId,
                                 @Param("subCategoryId") Integer subCategoryId,
                                 @Param("subCategoryCode") String subCategoryCode,
                                 @Param("subCategoryName") String subCategoryName);


    List<RequestSubCategory> findRequestSubCategorysByRequest(Request request);

    RequestSubCategory findRequestSubCategoryByRequestAndSubCategory(Request request, SubCategory subCategory);

    void deleteRequestSubCategoryByRequest(Request request);

    void deleteRequestSubCategoryByRequestAndSubCategory(Request request, SubCategory subCategory);

    @Query(value = "SELECT TOP 1 * FROM RequestSubCategory WHERE RequestId = :requestId", nativeQuery = true)
    Optional<RequestSubCategory> findTop1ByRequestId(@Param("requestId") Long requestId);

    List<RequestSubCategory> findByRequest(Request request);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    void deleteByRequest(Request request);

    List<RequestSubCategory> findBySubCategory_RecIdIn(List<Integer> subCategoryIds);
}
