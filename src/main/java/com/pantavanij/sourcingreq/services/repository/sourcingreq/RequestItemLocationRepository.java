package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItem;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItemLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestItemLocationRepository extends JpaRepository<RequestItemLocation, Long> {

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "INSERT INTO RequestItemLocation(RequestItemId, LocationId) VALUES(:requestItemId, :locationId)", nativeQuery = true)
    void saveRequestItemLocation(@Param("requestItemId") Long requestItemId, @Param("locationId") Integer locationId);

    @Query(value = "SELECT TOP 1 * FROM RequestItemLocation WHERE RequestItemId = :requestItemId", nativeQuery = true)
    Optional<RequestItemLocation> findTop1ByRequestItemId(@Param("requestItemId") Long requestItemId);

    List<RequestItemLocation> findByRequestItem(RequestItem requestItem);

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "DELETE FROM RequestItemLocation WHERE RequestItemId = :requestItemId ", nativeQuery = true)
    void deleteByRequestItem(@Param("requestItemId") Long requestItemId);

    RequestItemLocation findRequestItemLocationByRequestItemRecId(Long requestItemId);
}
