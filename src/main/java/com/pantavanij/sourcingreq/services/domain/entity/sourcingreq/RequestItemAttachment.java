package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestItemAttachmentKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "RequestItemAttachment")
public class RequestItemAttachment {

    @EmbeddedId
    private RequestItemAttachmentKey id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("requestItemId")
    @JoinColumn(name = "RequestItemId")
    @JsonBackReference
    private RequestItem requestItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("attachmentId")
    @JoinColumn(name = "AttachmentId")
    @JsonBackReference
    private Attachment attachment;

    @Column(name = "LineNum")
    private Integer lineNum;

    @Column(name = "SendtoSupplier")
    private boolean sendtoSupplier;

    @Column(name = "Note")
    private String note;
}
