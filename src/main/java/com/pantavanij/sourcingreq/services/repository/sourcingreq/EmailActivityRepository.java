package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import javax.validation.constraints.*;
import java.util.*;

@Repository
public interface EmailActivityRepository extends JpaRepository<EmailActivity, Long>, JpaSpecificationExecutor<EmailActivity> {

    Optional<EmailActivity> findByRecId(Long recId);

    @Query(value = "SELECT TOP 1 * FROM EmailActivity ORDER BY Sequence DESC ", nativeQuery = true)
    Optional<EmailActivity> findLastRecord();

    @Transactional
    @Modifying
    @Query(value = "UPDATE EmailActivity SET Sequence = Sequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE Sequence != :currentSequence " +
            "AND Sequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void reOrderOtherEmailActivitySequence(@Param("currentSequence") Integer currentSequence, @Param("newSequence") Integer newSequence);
}
