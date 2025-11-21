package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItem;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItemSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestItemSubCategoryRepository extends JpaRepository<RequestItemSubCategory, Long> {

    @Query(value = "SELECT TOP 1 * FROM RequestItemSubCategory WHERE RequestItemId = :requestItemId", nativeQuery = true)
    Optional<RequestItemSubCategory> findTop1ByRequestItemId(@Param("requestItemId") Long requestItemId);

    List<RequestItemSubCategory> findByRequestItem(RequestItem requestItem);

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    void deleteByRequestItem(RequestItem requestItem);

}
