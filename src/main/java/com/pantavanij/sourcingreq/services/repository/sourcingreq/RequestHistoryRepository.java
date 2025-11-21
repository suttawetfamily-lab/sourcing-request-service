package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestHistoryRepository extends JpaRepository<RequestHistory, Long> {

    @Query(value = "SELECT * FROM RequestHistory h inner join Request r on r.RecId = h.requestId " +
            "WHERE h.requestId = :requestId and r.statusID != 1 ORDER BY h.createdDate DESC", nativeQuery = true)
    List<RequestHistory> getRequestHistoryListByCondition(@Param("requestId") Long requestId);

    List<RequestHistory> findByRequestRecIdOrderByCreatedDateDesc(Long requestId);

    List<RequestHistory> findRequestHistoryByRequest(Request request);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    void deleteRequestHistoriesByRequest(Request request);


    @Query(value = "SELECT TOP 1 * FROM RequestHistory re " +
            " WHERE re.RequestId = :requestId " +
            " AND re.TenantId = :tenantId " +
            " AND re.ActivityId = :activityId " +
            " ORDER BY RecId DESC", nativeQuery = true)
    Optional<RequestHistory> findTop1ByRequestAndTenantOrderByRecIdDesc(
            @Param("requestId") Long requestId,
            @Param("tenantId") Integer tenantId,
            @Param("activityId") Integer activityId
    );
}
