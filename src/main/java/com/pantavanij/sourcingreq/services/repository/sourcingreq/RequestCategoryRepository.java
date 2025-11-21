package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestCategoryRepository extends JpaRepository<RequestCategory, Integer> {

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "INSERT INTO RequestCategory(RequestId, CategoryId, CategoryCode, CategoryName) VALUES(:requestId, :categoryId, :categoryCode, :categoryName)", nativeQuery = true)
    void saveRequestCategory(@Param("requestId") Long requestId,
                               @Param("categoryId") Integer categoryId,
                               @Param("categoryCode") String categoryCode,
                               @Param("categoryName") String categoryName);

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE RequestCategory SET CategoryCode = :categoryCode, CategoryName = :categoryName WHERE RequestId = :requestId AND CategoryId = :categoryId ", nativeQuery = true)
    void updateRequestCategory(@Param("requestId") Long requestId,
                                 @Param("categoryId") Integer categoryId,
                                 @Param("categoryCode") String categoryCode,
                                 @Param("categoryName") String categoryName);


    List<RequestCategory> findRequestCategorysByRequest(Request request);

    List<RequestCategory> findByCategory_RecIdIn(List<Integer> categoriesId);

    RequestCategory findRequestCategoryByRequestAndCategory(Request request, Category category);

    void deleteRequestCategoryByRequest(Request request);

    void deleteRequestCategoryByRequestAndCategory(Request request, Category category);

    @Query(value = "SELECT TOP 1 * FROM RequestCategory WHERE RequestId = :requestId", nativeQuery = true)
    Optional<RequestCategory> findTop1ByRequestId(@Param("requestId") Long requestId);

    List<RequestCategory> findByRequest(Request request);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    void deleteByRequest(Request request);
}
