package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Attachment;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItem;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItemAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RequestItemAttachmentRepository extends JpaRepository<RequestItemAttachment, Integer> {
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "INSERT INTO RequestItemAttachment(RequestItemId, AttachmentId, LineNum, SendtoSupplier, Note) VALUES(:requestItemId, :attachmentId, :lineNum, :sendtoSupplier, :note)", nativeQuery = true)
    void saveRequestItemAttachment(@Param("requestItemId") Long requestItemId,
                                   @Param("attachmentId") Long attachmentId,
                                   @Param("lineNum") Integer lineNum,
                                   @Param("sendtoSupplier") boolean sendtoSupplier,
                                   @Param("note") String note);

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE RequestItemAttachment SET LineNum = :lineNum, SendtoSupplier = :sendtoSupplier, Note = :note WHERE RequestItemId = :requestItemId AND AttachmentId = :attachmentId ", nativeQuery = true)
    void updateRequestItemAttachment(@Param("requestItemId") Long requestItemId,
                                     @Param("attachmentId") Long attachmentId,
                                     @Param("lineNum") Integer lineNum,
                                     @Param("sendtoSupplier") boolean sendtoSupplier,
                                     @Param("note") String note);

    List<RequestItemAttachment> findRequestItemAttachmentsByRequestItem(RequestItem requestItem);

    RequestItemAttachment findRequestItemAttachmentByRequestItemAndAttachment(RequestItem requestItem, Attachment attachment);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "DELETE FROM RequestItemAttachment WHERE RequestItemId = :requestItemId ", nativeQuery = true)
    void deleteRequestItemAttachmentByRequestItemId(@Param("requestItemId") Long requestItemId);

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "DELETE FROM RequestItemAttachment WHERE RequestItemId = :requestItemId AND AttachmentId = :attachmentId", nativeQuery = true)
    void deleteRequestItemAttachmentByRequestItemIdAndAttachmentId(@Param("requestItemId") Long requestItemId,
                                                                   @Param("attachmentId") Long attachmentId);
}

