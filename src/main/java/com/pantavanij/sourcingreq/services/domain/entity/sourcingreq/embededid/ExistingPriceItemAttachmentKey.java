package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class ExistingPriceItemAttachmentKey implements Serializable {
    @Column(name = "ExistingPriceItemId")
    private Long existingPriceItemId;

    @Column(name = "AttachmentId")
    private Long attachmentId;
}
