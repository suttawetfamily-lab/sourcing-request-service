package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    Optional<Attachment> findAttachmentByRecId(Long recId);

    Attachment findAttachmentByFileId(String fileId);

    @Query(value = "select * from Attachment a where a.CreatedDate < DATEADD(day, -1, GETDATE()) and a.TenantId = :tenantId and a.RecId not in " +
            "(select ra.AttachmentId from RequestAttachment ra where ra.AttachmentId in (select RecId from Attachment) union " +
            "select ria.AttachmentId from RequestItemAttachment ria where ria.AttachmentId in (select RecId from Attachment) union " +
            "select eia.AttachmentId from ExistingPriceItemAttachment eia where eia.AttachmentId in (select RecId from Attachment))" +
            " AND a.FileGroup NOT IN ('PDPA','SYSTEM') ",
            nativeQuery = true)
    List<Attachment> getUnusedAttachment(@Param("tenantId") Integer tenantId);

    void deleteAttachmentByRecId(Long recId);

}
