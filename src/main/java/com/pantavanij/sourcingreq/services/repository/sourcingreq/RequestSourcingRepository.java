package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestSourcing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequestSourcingRepository extends JpaRepository<RequestSourcing, Long>, JpaSpecificationExecutor<RequestSourcing> {

    @Query(value = "SELECT TOP 1 * from RequestSourcing WHERE sourcingDocNo = :sourcingDocNo ", nativeQuery = true)
    Optional<RequestSourcing> findBySourcingDocNo(@Param("sourcingDocNo") String sourcingDocNo);
}
