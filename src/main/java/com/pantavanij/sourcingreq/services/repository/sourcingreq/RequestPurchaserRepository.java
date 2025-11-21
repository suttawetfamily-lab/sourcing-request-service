package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Purchaser;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestPurchaser;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestPurchaserRepository extends JpaRepository<RequestPurchaser, Long> {


    @Query(value = "SELECT TOP 1 * FROM RequestPurchaser WHERE RequestId = :requestId AND WorkflowPermission = 1 ORDER BY Sequence ASC", nativeQuery = true)
    Optional<RequestPurchaser> findTop1ByRequestId(@Param("requestId") Long requestId);

    @Query(value = "SELECT TOP 1 * FROM RequestPurchaser WHERE RequestId = :requestId ORDER BY Sequence DESC", nativeQuery = true)
    Optional<RequestPurchaser> findLatestPurchaserWithoutAnyPermission(@Param("requestId") Long requestId);

    @Query(value = "SELECT * FROM RequestPurchaser WHERE RequestId = :requestId AND WorkflowPermission = 1 ORDER BY Sequence ASC", nativeQuery = true)
    List<RequestPurchaser> findByRequest(@Param("requestId") Long requestId);

    List<RequestPurchaser> findByRequest(Request request);

    Optional<RequestPurchaser> findByRequestAndPurchaser(Request request, Purchaser purchaser);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    void deleteByRequest(Request request);

}
