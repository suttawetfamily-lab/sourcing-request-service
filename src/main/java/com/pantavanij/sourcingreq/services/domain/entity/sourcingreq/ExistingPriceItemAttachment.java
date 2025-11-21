package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.ExistingPriceItemAttachmentKey;
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
@Table(name = "ExistingPriceItemAttachment")
public class ExistingPriceItemAttachment {

    @EmbeddedId
    private ExistingPriceItemAttachmentKey id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("existingPriceItemId")
    @JoinColumn(name = "ExistingPriceItemId")
    @JsonBackReference
    private ExistingPriceItem existingPriceItem;


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
