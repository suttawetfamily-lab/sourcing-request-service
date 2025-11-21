package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class ExcSourcingRequestItemKey implements Serializable {
    @Column(name = "ExcSourcingId")
    private Long excSourcingId;

    @Column(name = "RequestItemId")
    private Long requestItemId;
}
