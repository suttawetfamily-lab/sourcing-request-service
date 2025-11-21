package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.*;

import javax.validation.constraints.*;
import java.util.*;

@Repository
public interface TenantSectionDetailRepository extends JpaRepository<TenantSectionDetail, Long> {
    Optional<TenantSectionDetail> findFirstByTenantAndId(Tenant tenant, Long id);
    @Query(value = "SELECT tsd.* \n" +
            "FROM TenantSectionDetail tsd\n" +
            "JOIN TenantRequestReport trr on tsd.RecId = trr.TenantSectionDetailId AND tsd.TenantId = trr.TenantId\n" +
            "WHERE tsd.TenantId = :tenantId AND trr.RequestReportId = :requestReportId AND trr.Active = 1 ORDER BY trr.Sequence  ", nativeQuery = true)
    List<TenantSectionDetail> getTenantSectionDetailByTenantAndExportRequestRpt(@Param("tenantId") Integer tenantId, @Param("requestReportId") Integer requestReportId);

    @Query(value = "SELECT tsd.* \n" +
            "FROM TenantSectionDetail tsd\n" +
            "JOIN TenantRequestItemReport trir on tsd.RecId = trir.TenantSectionDetailId AND tsd.TenantId = trir.TenantId\n" +
            "WHERE tsd.TenantId = :tenantId AND trir.RequestItemReportId = :requestItemReportId AND trir.Active = 1 ORDER BY trir.Sequence ", nativeQuery = true)
    List<TenantSectionDetail> getTenantSectionDetailByTenantAndExportRequestItemRpt(@Param("tenantId") Integer tenantId, @Param("requestItemReportId") Integer requestItemReportId);

    @Query(value = "SELECT TOP 1 tsd.*\n" +
            "  FROM [TenantSection] ts \n" +
            "  JOIN [TenantSectionDetail] tsd on ts.RecId = tsd.tenantSectionId\n" +
            "  JOIN [TenantSectionDetailValidator] v ON tsd.RecId = v.TenantSectionDetailId\n" +
            "  WHERE tsd.TenantId = :tenantId \n" +
            "  AND tsd.FieldName = :fieldName \n" +
            "  AND v.ValidatorId = :validatorId \n" +
            "  AND ts.Type = :sectionType ", nativeQuery = true)
    TenantSectionDetail getTenantSectionDetailByTenantAndFieldName(@Param("tenantId") Integer tenantId,
                                                                         @Param("fieldName") String fieldName,
                                                                         @Param("validatorId") Integer validatorId,
                                                                         @Param("sectionType") String sectionType);

    Optional<TenantSectionDetail> findFirstByTenantRecIdAndTenantSectionIdOrderBySequenceDesc(Integer tenantId,
                                                                      Long tenantSectionId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "UPDATE TenantSectionDetail SET Sequence = Sequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId " +
            "AND Sequence != :currentSequence " +
            "AND tenantSectionId = :tenantSectionId " +
            "AND Visible = 1 " +
            "AND Sequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void reOrderOtherSequence(@Param("tenantId") Integer tenantId,
                              @Param("tenantSectionId") Integer tenantSectionId,
                              @Param("currentSequence") Integer currentSequence,
                              @Param("newSequence") Integer newSequence);


    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "UPDATE TenantSectionDetail SET ExportItemRptSequence = ExportItemRptSequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId " +
            "AND ExportItemRptSequence != :currentSequence " +
            "AND tenantSectionId = :tenantSectionId " +
            "AND ExportItemRpt = 1 " +
            "AND ExportItemRptSequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void reOrderOtherExportItemRptSequence(@Param("tenantId") Integer tenantId,
                                           @Param("tenantSectionId") Integer tenantSectionId,
                                           @Param("currentSequence") Integer currentSequence,
                                           @Param("newSequence") Integer newSequence);

//    Optional<TenantSectionDetail> findFirstByTenantRecIdAndTenantSectionIdOrderByExportRequestRptSequenceDesc(Integer tenantId, Long tenantSectionId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "UPDATE TenantSectionDetail SET ExportRequestRptSequence = ExportRequestRptSequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId " +
            "AND ExportRequestRptSequence != :currentSequence " +
            "AND tenantSectionId = :tenantSectionId " +
            "AND ExportRequestRpt = 1 " +
            "AND ExportRequestRptSequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void reOrderOtherExportRequestSequence(@Param("tenantId") Integer tenantId,
                                           @Param("tenantSectionId") Integer tenantSectionId,
                                           @Param("currentSequence") Integer currentSequence,
                                           @Param("newSequence") Integer newSequence);

    Optional<TenantSectionDetail> findFirstByTenantRecIdAndTenantSectionIdOrderByHeaderSequenceDesc(Integer tenantId, Long tenantSectionId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "UPDATE TenantSectionDetail SET HeaderSequence = HeaderSequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId " +
            "AND HeaderSequence != :currentSequence " +
            "AND tenantSectionId = :tenantSectionId " +
            "AND Header = 1 " +
            "AND HeaderSequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void reOrderOtherHeaderSequence(@Param("tenantId") Integer tenantId,
                                    @Param("tenantSectionId") Integer tenantSectionId,
                                    @Param("currentSequence") Integer currentSequence,
                                    @Param("newSequence") Integer newSequence);

    Optional<TenantSectionDetail> findFirstByTenantRecIdAndTenantSectionIdOrderByModeViewSequenceDesc(Integer tenantId, Long tenantSectionId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "UPDATE TenantSectionDetail SET ModeViewSequence = ModeViewSequence + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId " +
            "AND ModeViewSequence != :currentSequence " +
            "AND tenantSectionId = :tenantSectionId " +
            "AND ModeView = 1 " +
            "AND ModeViewSequence BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void reOrderOtherModeViewSequence(@Param("tenantId") Integer tenantId,
                                      @Param("tenantSectionId") Integer tenantSectionId,
                                      @Param("currentSequence") Integer currentSequence,
                                      @Param("newSequence") Integer newSequence);

    Optional<TenantSectionDetail> findFirstByTenantRecIdAndTenantSectionIdOrderByModeViewSequenceSQNDesc(Integer tenantId, Long tenantSectionId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "UPDATE TenantSectionDetail SET ModeViewSequence_SQN = ModeViewSequence_SQN + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId " +
            "AND ModeViewSequence_SQN != :currentSequence " +
            "AND tenantSectionId = :tenantSectionId " +
            "AND ModeView_SQN = 1 " +
            "AND ModeViewSequence_SQN BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void reOrderOtherModeViewSequenceSQN(@Param("tenantId") Integer tenantId,
                                         @Param("tenantSectionId") Integer tenantSectionId,
                                         @Param("currentSequence") Integer currentSequence,
                                         @Param("newSequence") Integer newSequence);

    Optional<TenantSectionDetail> findFirstByTenantRecIdAndTenantSectionIdOrderByModeViewSequenceSQVDesc(Integer tenantId, Long tenantSectionId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "UPDATE TenantSectionDetail SET ModeViewSequence_SQV = ModeViewSequence_SQV + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId " +
            "AND ModeViewSequence_SQV != :currentSequence " +
            "AND tenantSectionId = :tenantSectionId " +
            "AND ModeView_SQV = 1 " +
            "AND ModeViewSequence_SQV BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void reOrderOtherModeViewSequenceSQV(@Param("tenantId") Integer tenantId,
                                         @Param("tenantSectionId") Integer tenantSectionId,
                                         @Param("currentSequence") Integer currentSequence,
                                         @Param("newSequence") Integer newSequence);

    Optional<TenantSectionDetail> findFirstByTenantRecIdAndTenantSectionIdOrderByModeViewSequenceSQPDesc(Integer tenantId, Long tenantSectionId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "UPDATE TenantSectionDetail SET ModeViewSequence_SQP = ModeViewSequence_SQP + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId " +
            "AND ModeViewSequence_SQP != :currentSequence " +
            "AND tenantSectionId = :tenantSectionId " +
            "AND ModeView_SQP = 1 " +
            "AND ModeViewSequence_SQP BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void reOrderOtherModeViewSequenceSQP(@Param("tenantId") Integer tenantId,
                                         @Param("tenantSectionId") Integer tenantSectionId,
                                         @Param("currentSequence") Integer currentSequence,
                                         @Param("newSequence") Integer newSequence);

    Optional<TenantSectionDetail> findFirstByTenantRecIdAndTenantSectionIdOrderByModeViewSequenceSQADesc(Integer tenantId, Long tenantSectionId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "UPDATE TenantSectionDetail SET ModeViewSequence_SQA = ModeViewSequence_SQA + CASE WHEN :currentSequence > :newSequence THEN 1 ELSE -1 END " +
            "WHERE TenantId = :tenantId " +
            "AND ModeViewSequence_SQA != :currentSequence " +
            "AND tenantSectionId = :tenantSectionId " +
            "AND ModeView_SQA = 1 " +
            "AND ModeViewSequence_SQA BETWEEN (CASE WHEN :currentSequence < :newSequence THEN :currentSequence ELSE :newSequence END) " +
            "AND (CASE WHEN :currentSequence > :newSequence THEN :currentSequence ELSE :newSequence END)", nativeQuery = true)
    void reOrderOtherModeViewSequenceSQA(@Param("tenantId") Integer tenantId,
                                         @Param("tenantSectionId") Integer tenantSectionId,
                                         @Param("currentSequence") Integer currentSequence,
                                         @Param("newSequence") Integer newSequence);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "UPDATE TenantSectionDetail " +
            "SET Visible = 0, Sequence = 0 " +
            "WHERE TenantId = :tenantId " +
            "AND tenantSectionId = :tenantSectionId " +
            "AND Sequence = :currentSequence ", nativeQuery = true)
    void updateVisibleAndSequence(@Param("tenantId") Integer tenantId,
                                  @Param("tenantSectionId") Integer tenantSectionId,
                                  @Param("currentSequence") Integer currentSequence);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "UPDATE TenantSectionDetail " +
            "SET Sequence = NewSequence " +
            "FROM (" +
            "  SELECT RecId, ROW_NUMBER() OVER (ORDER BY Sequence) AS NewSequence " +
            "  FROM TenantSectionDetail " +
            "  WHERE TenantId = :tenantId " +
            "    AND tenantSectionId = :tenantSectionId " +
            "    AND Visible = 1 " +
            ") AS OrderedRecords " +
            "WHERE TenantSectionDetail.RecId = OrderedRecords.RecId ", nativeQuery = true)
    void reOrderSequence(@Param("tenantId") Integer tenantId,
                         @Param("tenantSectionId") Integer tenantSectionId);

//    @Transactional(rollbackFor = Exception.class)
//    @Modifying
//    @Query(value = "UPDATE TenantSectionDetail " +
//            "SET ExportItemRpt = 0, ExportItemRptSequence = 0 " +
//            "WHERE TenantId = :tenantId " +
//            "AND tenantSectionId = :tenantSectionId " +
//            "AND ExportItemRptSequence = :currentSequence ", nativeQuery = true)
//    void updateExportItemRptAndSequence(@Param("tenantId") Integer tenantId,
//                                        @Param("tenantSectionId") Integer tenantSectionId,
//                                        @Param("currentSequence") Integer currentSequence);

//    @Transactional(rollbackFor = Exception.class)
//    @Modifying
//    @Query(value = "UPDATE TenantSectionDetail " +
//            "SET ExportItemRptSequence = NewSequence " +
//            "FROM (" +
//            "  SELECT RecId, ROW_NUMBER() OVER (ORDER BY ExportItemRptSequence) AS NewSequence " +
//            "  FROM TenantSectionDetail " +
//            "  WHERE TenantId = :tenantId " +
//            "    AND tenantSectionId = :tenantSectionId " +
//            "    AND ExportItemRpt = 1 " +
//            ") AS OrderedRecords " +
//            "WHERE TenantSectionDetail.RecId = OrderedRecords.RecId ", nativeQuery = true)
//    void reOrderExportItemRptSequence(@Param("tenantId") Integer tenantId,
//                                      @Param("tenantSectionId") Integer tenantSectionId);

//    @Transactional(rollbackFor = Exception.class)
//    @Modifying
//    @Query(value = "UPDATE TenantSectionDetail " +
//            "SET ExportRequestRpt = 0, ExportRequestRptSequence = 0 " +
//            "WHERE TenantId = :tenantId " +
//            "AND tenantSectionId = :tenantSectionId " +
//            "AND ExportRequestRptSequence = :currentSequence ", nativeQuery = true)
//    void updateExportRequestRptAndSequence(@Param("tenantId") Integer tenantId,
//                                           @Param("tenantSectionId") Integer tenantSectionId,
//                                           @Param("currentSequence") Integer currentSequence);

//    @Transactional(rollbackFor = Exception.class)
//    @Modifying
//    @Query(value = "UPDATE TenantSectionDetail " +
//            "SET ExportRequestRptSequence = NewSequence " +
//            "FROM (" +
//            "  SELECT RecId, ROW_NUMBER() OVER (ORDER BY ExportRequestRptSequence) AS NewSequence " +
//            "  FROM TenantSectionDetail " +
//            "  WHERE TenantId = :tenantId " +
//            "    AND tenantSectionId = :tenantSectionId " +
//            "    AND ExportRequestRpt = 1 " +
//            ") AS OrderedRecords " +
//            "WHERE TenantSectionDetail.RecId = OrderedRecords.RecId ", nativeQuery = true)
//    void reOrderExportRequestRptSequence(@Param("tenantId") Integer tenantId,
//                                         @Param("tenantSectionId") Integer tenantSectionId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "UPDATE TenantSectionDetail " +
            "SET Header = 0, HeaderSequence = 0 " +
            "WHERE TenantId = :tenantId " +
            "AND tenantSectionId = :tenantSectionId " +
            "AND HeaderSequence = :currentSequence ", nativeQuery = true)
    void updateHeaderAndSequence(@Param("tenantId") Integer tenantId,
                                 @Param("tenantSectionId") Integer tenantSectionId,
                                 @Param("currentSequence") Integer currentSequence);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "UPDATE TenantSectionDetail " +
            "SET HeaderSequence = NewSequence " +
            "FROM (" +
            "  SELECT RecId, ROW_NUMBER() OVER (ORDER BY HeaderSequence) AS NewSequence " +
            "  FROM TenantSectionDetail " +
            "  WHERE TenantId = :tenantId " +
            "    AND tenantSectionId = :tenantSectionId " +
            "    AND Header = 1 " +
            ") AS OrderedRecords " +
            "WHERE TenantSectionDetail.RecId = OrderedRecords.RecId ", nativeQuery = true)
    void reOrderHeaderSequence(@Param("tenantId") Integer tenantId,
                               @Param("tenantSectionId") Integer tenantSectionId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "UPDATE TenantSectionDetail " +
            "SET ModeView = 0, ModeViewSequence = 0 " +
            "WHERE TenantId = :tenantId " +
            "AND tenantSectionId = :tenantSectionId " +
            "AND ModeViewSequence = :currentSequence ", nativeQuery = true)
    void updateModeViewAndSequence(@Param("tenantId") Integer tenantId,
                                   @Param("tenantSectionId") Integer tenantSectionId,
                                   @Param("currentSequence") Integer currentSequence);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "UPDATE TenantSectionDetail " +
            "SET ModeViewSequence = NewSequence " +
            "FROM (" +
            "  SELECT RecId, ROW_NUMBER() OVER (ORDER BY ModeViewSequence) AS NewSequence " +
            "  FROM TenantSectionDetail " +
            "  WHERE TenantId = :tenantId " +
            "    AND tenantSectionId = :tenantSectionId " +
            "    AND ModeView = 1 " +
            ") AS OrderedRecords " +
            "WHERE TenantSectionDetail.RecId = OrderedRecords.RecId ", nativeQuery = true)
    void reOrderModeViewSequence(@Param("tenantId") Integer tenantId,
                                 @Param("tenantSectionId") Integer tenantSectionId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "UPDATE TenantSectionDetail " +
            "SET ModeView_SQN = 0, ModeViewSequence_SQN = 0 " +
            "WHERE TenantId = :tenantId " +
            "AND tenantSectionId = :tenantSectionId " +
            "AND ModeViewSequence_SQN = :currentSequence ", nativeQuery = true)
    void updateModeViewSQNAndSequence(@Param("tenantId") Integer tenantId,
                                   @Param("tenantSectionId") Integer tenantSectionId,
                                   @Param("currentSequence") Integer currentSequence);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "UPDATE TenantSectionDetail " +
            "SET ModeViewSequence_SQN = NewSequence " +
            "FROM (" +
            "  SELECT RecId, ROW_NUMBER() OVER (ORDER BY ModeViewSequence_SQN) AS NewSequence " +
            "  FROM TenantSectionDetail " +
            "  WHERE TenantId = :tenantId " +
            "    AND tenantSectionId = :tenantSectionId " +
            "    AND ModeView_SQN = 1 " +
            ") AS OrderedRecords " +
            "WHERE TenantSectionDetail.RecId = OrderedRecords.RecId ", nativeQuery = true)
    void reOrderModeViewSequenceSQN(@Param("tenantId") Integer tenantId,
                                 @Param("tenantSectionId") Integer tenantSectionId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "UPDATE TenantSectionDetail " +
            "SET ModeView_SQV = 0, ModeViewSequence_SQV = 0 " +
            "WHERE TenantId = :tenantId " +
            "AND tenantSectionId = :tenantSectionId " +
            "AND ModeViewSequence_SQV = :currentSequence ", nativeQuery = true)
    void updateModeViewSQVAndSequence(@Param("tenantId") Integer tenantId,
                                      @Param("tenantSectionId") Integer tenantSectionId,
                                      @Param("currentSequence") Integer currentSequence);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "UPDATE TenantSectionDetail " +
            "SET ModeViewSequence_SQV = NewSequence " +
            "FROM (" +
            "  SELECT RecId, ROW_NUMBER() OVER (ORDER BY ModeViewSequence_SQV) AS NewSequence " +
            "  FROM TenantSectionDetail " +
            "  WHERE TenantId = :tenantId " +
            "    AND tenantSectionId = :tenantSectionId " +
            "    AND ModeView_SQV = 1 " +
            ") AS OrderedRecords " +
            "WHERE TenantSectionDetail.RecId = OrderedRecords.RecId ", nativeQuery = true)
    void reOrderModeViewSequenceSQV(@Param("tenantId") Integer tenantId,
                                    @Param("tenantSectionId") Integer tenantSectionId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "UPDATE TenantSectionDetail " +
            "SET ModeView_SQP = 0, ModeViewSequence_SQP = 0 " +
            "WHERE TenantId = :tenantId " +
            "AND tenantSectionId = :tenantSectionId " +
            "AND ModeViewSequence_SQP = :currentSequence ", nativeQuery = true)
    void updateModeViewSQPAndSequence(@Param("tenantId") Integer tenantId,
                                      @Param("tenantSectionId") Integer tenantSectionId,
                                      @Param("currentSequence") Integer currentSequence);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "UPDATE TenantSectionDetail " +
            "SET ModeViewSequence_SQP = NewSequence " +
            "FROM (" +
            "  SELECT RecId, ROW_NUMBER() OVER (ORDER BY ModeViewSequence_SQP) AS NewSequence " +
            "  FROM TenantSectionDetail " +
            "  WHERE TenantId = :tenantId " +
            "    AND tenantSectionId = :tenantSectionId " +
            "    AND ModeView_SQP = 1 " +
            ") AS OrderedRecords " +
            "WHERE TenantSectionDetail.RecId = OrderedRecords.RecId ", nativeQuery = true)
    void reOrderModeViewSequenceSQP(@Param("tenantId") Integer tenantId,
                                    @Param("tenantSectionId") Integer tenantSectionId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "UPDATE TenantSectionDetail " +
            "SET ModeView_SQA = 0, ModeViewSequence_SQA = 0 " +
            "WHERE TenantId = :tenantId " +
            "AND tenantSectionId = :tenantSectionId " +
            "AND ModeViewSequence_SQA = :currentSequence ", nativeQuery = true)
    void updateModeViewSQAAndSequence(@Param("tenantId") Integer tenantId,
                                      @Param("tenantSectionId") Integer tenantSectionId,
                                      @Param("currentSequence") Integer currentSequence);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "UPDATE TenantSectionDetail " +
            "SET ModeViewSequence_SQA = NewSequence " +
            "FROM (" +
            "  SELECT RecId, ROW_NUMBER() OVER (ORDER BY ModeViewSequence_SQA) AS NewSequence " +
            "  FROM TenantSectionDetail " +
            "  WHERE TenantId = :tenantId " +
            "    AND tenantSectionId = :tenantSectionId " +
            "    AND ModeView_SQA = 1 " +
            ") AS OrderedRecords " +
            "WHERE TenantSectionDetail.RecId = OrderedRecords.RecId ", nativeQuery = true)
    void reOrderModeViewSequenceSQA(@Param("tenantId") Integer tenantId,
                                    @Param("tenantSectionId") Integer tenantSectionId);

    List<TenantSectionDetail> findByTenant(Tenant tenant);

    List<TenantSectionDetail> findByTenantAndTenantSectionIdIn(Tenant tenant, List<Long> tenantSectionIds);

    @Query(value = "SELECT tsd.* " +
            "FROM TenantSectionDetail tsd " +
            "JOIN TenantRequestItemReport trir " +
            "  ON tsd.RecId = trir.TenantSectionDetailId " +
            " AND tsd.TenantId = trir.TenantId " +
            "JOIN TenantSection ts " +
            "  ON ts.RecId = tsd.TenantSectionId " +
            "WHERE tsd.TenantId = :tenantId " +
            "  AND ts.TemplateId = :templateId " +
            "  AND trir.RequestItemReportId = :requestItemReportId " +
            "  AND trir.Active = 1 " +
            "ORDER BY trir.Sequence", nativeQuery = true)
    List<TenantSectionDetail> getTenantSectionDetailByTenantAndTemplateAndExportRequestItemRpt(
            @Param("tenantId") Integer tenantId,
            @Param("templateId") Integer templateId,
            @Param("requestItemReportId") Integer requestItemReportId
    );

    @Query(value = "SELECT tsd.* " +
            "FROM TenantSectionDetail tsd " +
            "JOIN TenantRequestReport trr " +
            "  ON tsd.RecId = trr.TenantSectionDetailId " +
            " AND tsd.TenantId = trr.TenantId " +
            "JOIN TenantSection ts " +
            "  ON ts.RecId = tsd.TenantSectionId " +
            "WHERE tsd.TenantId = :tenantId " +
            "  AND ts.TemplateId = :templateId " +
            "  AND trr.RequestReportId = :requestReportId " +
            "  AND trr.Active = 1 " +
            "ORDER BY trr.Sequence", nativeQuery = true)
    List<TenantSectionDetail> getTenantSectionDetailByTenantAndTemplateAndExportRequestRpt(
            @Param("tenantId") Integer tenantId,
            @Param("templateId") Integer templateId,
            @Param("requestReportId") Integer requestReportId
    );

}