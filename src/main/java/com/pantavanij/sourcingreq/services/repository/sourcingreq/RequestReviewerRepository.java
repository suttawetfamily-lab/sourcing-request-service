package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestReportLine;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestReviewer;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Reviewer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface RequestReviewerRepository extends JpaRepository<RequestReviewer, Integer> {

    @Query(value = "SELECT TOP 1 * FROM RequestReviewer WHERE RequestId = :requestId AND ReviewerId = :reviewerId ", nativeQuery = true)
    Optional<RequestReviewer> findRequestReviewerByRequestIdAndReviewerId(@Param("requestId") Long requestId, @Param("reviewerId") Integer reviewerId);

    @Query(value = "SELECT * FROM RequestReviewer WHERE RequestId = :requestId ", nativeQuery = true)
    List<RequestReviewer> getByRequest(@Param("requestId") Long requestId);

    RequestReviewer findRequestReviewerByRecId(Long RecId);

//    @Transactional(rollbackFor = Exception.class)
//    @Modifying
//    @Query(value = "DELETE FROM RequestReviewer WHERE RequestId = :requestId AND ReviewerName NOT IN :reviewerNames ", nativeQuery = true)
//    void deleteExceptionRequestReviewer(@Param("requestId") Long requestId,
//                                        @Param("reviewerNames") List<String> reviewerNames);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "DELETE FROM RequestReviewer WHERE RequestId = :requestId ", nativeQuery = true)
    void deleteRequestReviewerByRequestId(@Param("requestId") Long requestId);

}
