package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Attachment;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestAttachment;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestAttachmentKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RequestAttachmentRepository extends JpaRepository<RequestAttachment, RequestAttachmentKey> {

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "INSERT INTO RequestAttachment(RequestId, AttachmentId, LineNum, SendtoSupplier, Note) VALUES(:requestId, :attachmentId, :lineNum, :sendtoSupplier, :note)", nativeQuery = true)
    void saveRequestAttachment(@Param("requestId") Long requestId,
                               @Param("attachmentId") Long attachmentId,
                               @Param("lineNum") Integer lineNum,
                               @Param("sendtoSupplier") boolean sendtoSupplier,
                               @Param("note") String note);

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE RequestAttachment SET LineNum = :lineNum, SendtoSupplier = :sendtoSupplier, Note = :note WHERE RequestId = :requestId AND AttachmentId = :attachmentId ", nativeQuery = true)
    void updateRequestAttachment(@Param("requestId") Long requestId,
                                 @Param("attachmentId") Long attachmentId,
                                 @Param("lineNum") Integer lineNum,
                                 @Param("sendtoSupplier") boolean sendtoSupplier,
                                 @Param("note") String note);


    List<RequestAttachment> findRequestAttachmentsByRequest(Request request);

    RequestAttachment findRequestAttachmentByRequestAndAttachment(Request request, Attachment attachment);

    @Query(value = "SELECT * FROM RequestAttachment WHERE AttachmentId = :attachmentId", nativeQuery = true)
    RequestAttachment findRequestAttachmentByAttachmentId(@Param("attachmentId") Long attachmentId);

    @Query(value = "SELECT * FROM RequestAttachment WHERE RequestId = :requestId AND  AttachmentId = :attachmentId", nativeQuery = true)
    RequestAttachment findRequestAttachmentByRequestIdAndAttachmentId(@Param("requestId") Long requestId, @Param("attachmentId") Long attachmentId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "DELETE ra FROM RequestAttachment ra " +
            " JOIN Attachment a ON ra.AttachmentId = a.RecId " +
            " WHERE a.FileGroup NOT IN ('PDPA','SYSTEM') AND ra.RequestId = :requestId ", nativeQuery = true)
    void deleteRequestAttachmentByRequestId(@Param("requestId") Long requestId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "DELETE ra FROM RequestAttachment ra " +
            " JOIN Attachment a ON ra.AttachmentId = a.RecId " +
            " WHERE a.FileGroup IN ('PDPA') AND ra.RequestId = :requestId ", nativeQuery = true)
    void deletePDPARequestAttachmentByRequestId(@Param("requestId") Long requestId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "DELETE ra FROM RequestAttachment ra " +
            " JOIN Attachment a ON ra.AttachmentId = a.RecId " +
            " WHERE a.FileGroup IN ('SYSTEM') AND ra.RequestId = :requestId ", nativeQuery = true)
    void deleteSystemRequestAttachmentByRequestId(@Param("requestId") Long requestId);

    void deleteRequestAttachmentByRequestAndAttachment(Request request, Attachment attachment);
}
