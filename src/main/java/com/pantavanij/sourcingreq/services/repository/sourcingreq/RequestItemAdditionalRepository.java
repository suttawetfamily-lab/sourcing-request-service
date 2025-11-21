package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItemAdditional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequestItemAdditionalRepository extends JpaRepository<RequestItemAdditional, Long> {

    @Query(value = "SELECT * FROM RequestItemAdditional WHERE RequestItemId = :requestItemId", nativeQuery = true)
    Optional<RequestItemAdditional> findByRequestItemId(@Param("requestItemId") Long requestItemId);

}
