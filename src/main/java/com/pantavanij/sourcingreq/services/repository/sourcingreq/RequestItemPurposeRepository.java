package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItem;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItemPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestItemPurposeRepository extends JpaRepository<RequestItemPurpose, Long> {

    @Query(value = "SELECT TOP 1 * FROM RequestItemPurpose WHERE RequestItemId = :requestItemId", nativeQuery = true)
    Optional<RequestItemPurpose> findTop1ByRequestItemId(@Param("requestItemId") Long requestItemId);

    List<RequestItemPurpose> findByRequestItem(RequestItem requestItem);

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    void deleteByRequestItem(RequestItem requestItem);
}
