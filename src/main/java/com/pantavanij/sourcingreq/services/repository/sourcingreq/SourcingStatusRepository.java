package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SourcingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SourcingStatusRepository extends JpaRepository<SourcingStatus, Integer> {
    SourcingStatus findSourcingStatusByRecId(Integer recId);
    SourcingStatus findSourcingStatusByCode(String code);

    @Query(value = "SELECT * FROM SourcingStatus WHERE 1=1 " +
            "AND (LOWER(Code) LIKE LOWER(CONCAT('%',:searchTerm, '%')) " +
            "OR LOWER(Description) LIKE LOWER(CONCAT('%',:searchTerm, '%')))" +
            "ORDER BY DisplaySequence", nativeQuery = true)
    List<SourcingStatus> findBydSearchTerm(@Param("searchTerm") String searchTerm);
}
