package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class RequestItemAttachmentKey implements Serializable {
    @Column(name = "RequestItemId")
    private Long requestItemId;

    @Column(name = "AttachmentId")
    private Long attachmentId;
}
