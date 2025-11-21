package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestAttachmentKey;
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
@Table(name = "RequestAttachment")
public class RequestAttachment {

    @EmbeddedId
    private RequestAttachmentKey id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @MapsId("requestId")
    @JoinColumn(name = "RequestId")
    @JsonBackReference
    private Request request;

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
