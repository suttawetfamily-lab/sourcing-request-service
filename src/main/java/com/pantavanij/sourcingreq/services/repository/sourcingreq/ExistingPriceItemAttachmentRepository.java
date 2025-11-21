package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ExistingPriceItemAttachmentRepository extends JpaRepository<ExistingPriceItemAttachment, Integer> {

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "INSERT INTO ExistingPriceItemAttachment(ExistingPriceItemId, AttachmentId, LineNum, SendtoSupplier, Note) VALUES(:existingPriceItemId, :attachmentId, :lineNum, :sendtoSupplier, :note)", nativeQuery = true)
    void saveExistingPriceItemAttachment(@Param("existingPriceItemId") Long existingPriceItemId,
                                         @Param("attachmentId") Long attachmentId,
                                         @Param("lineNum") Integer lineNum,
                                         @Param("sendtoSupplier") boolean sendtoSupplier,
                                         @Param("note") String note);

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE ExistingPriceItemAttachment SET LineNum = :lineNum, SendtoSupplier = :sendtoSupplier, Note = :note WHERE ExistingPriceItemId = :existingPriceItemId AND AttachmentId = :attachmentId ", nativeQuery = true)
    void updateExistingPriceItemAttachment(@Param("existingPriceItemId") Long existingPriceItemId,
                                           @Param("attachmentId") Long attachmentId,
                                           @Param("lineNum") Integer lineNum,
                                           @Param("sendtoSupplier") boolean sendtoSupplier,
                                           @Param("note") String note);

    List<ExistingPriceItemAttachment> findExistingPriceItemAttachmentsByExistingPriceItem(ExistingPriceItem existingPriceItem);

    ExistingPriceItemAttachment findExistingPriceItemAttachmentByExistingPriceItemAndAttachment(ExistingPriceItem existingPriceItem, Attachment attachment);

    void deleteExistingPriceItemAttachmentByExistingPriceItemAndAttachment(ExistingPriceItem existingPriceItem, Attachment attachment);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "DELETE FROM ExistingPriceItemAttachment WHERE ExistingPriceItemId = :existingPriceItemId ", nativeQuery = true)
    void deleteExistingPriceItemAttachmentByExistingPriceItemId(@Param("existingPriceItemId") Long existingPriceItemId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "DELETE FROM ExistingPriceItemAttachment WHERE ExistingPriceItemId IN (:existingPriceItemRecIds)", nativeQuery = true)
    void deleteByExistingPriceItemRecIdIn(@Param("existingPriceItemRecIds") List<Long> existingPriceItemRecIds);
}
