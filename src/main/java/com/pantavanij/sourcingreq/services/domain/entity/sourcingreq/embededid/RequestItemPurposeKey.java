package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class RequestItemPurposeKey implements Serializable {
    @Column(name = "RequestItemId")
    private Long requestItemId;

    @Column(name = "PurposeId")
    private Integer purposeId;
}
