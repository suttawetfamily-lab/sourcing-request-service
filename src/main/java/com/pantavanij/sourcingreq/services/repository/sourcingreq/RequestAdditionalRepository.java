package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestAdditional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequestAdditionalRepository extends JpaRepository<RequestAdditional, Long> {

    @Query(value = "SELECT TOP 1 * FROM RequestAdditional WHERE RequestId = :requestId", nativeQuery = true)
    Optional<RequestAdditional> findTop1ByRequestId(@Param("requestId") Long requestId);

    void deleteByRequest(Request request);
}
