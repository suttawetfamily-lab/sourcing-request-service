package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExcSourcing;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExcSourcingRequestItem;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.ExcSourcingRequestItemKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExcSourcingRequestItemRepository extends JpaRepository<ExcSourcingRequestItem, ExcSourcingRequestItemKey> {

    List<ExcSourcingRequestItem> findByExcSourcing(ExcSourcing excSourcing);


    @Query(value = "SELECT * FROM ExcSourcingRequestItem WHERE ExcSourcingId = :excSourcingId", nativeQuery = true)
    List<ExcSourcingRequestItem> findByExcSourcingId(@Param("excSourcingId") Long excSourcingId);
}
