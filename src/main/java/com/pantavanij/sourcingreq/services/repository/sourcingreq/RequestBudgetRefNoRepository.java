package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestBudgetRefNo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestBudgetRefNoRepository extends JpaRepository<RequestBudgetRefNo, Integer> {

    @Query(value = "SELECT TOP 1 * FROM RequestBudgetRefNo WHERE RequestId = :requestId", nativeQuery = true)
    Optional<RequestBudgetRefNo> findTop1ByRequestId(@Param("requestId") Long requestId);

    List<RequestBudgetRefNo> findByRequest(Request request);

    List<RequestBudgetRefNo> findRequestBudgetRefNoByRequest(Request request);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    void deleteByRequest(Request request);

}
