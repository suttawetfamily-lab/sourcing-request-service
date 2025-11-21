package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class RequestPurchaserKey implements Serializable {

    @Column(name = "RequestId")
    private Long requestId;

    @Column(name = "PurchaserId")
    private Integer purchaserId;

    @Column(name = "Sequence")
    private Integer sequence;

    public RequestPurchaserKey(Long requestId, Integer purchaserId, Integer sequence) {
        this.requestId = requestId;
        this.purchaserId = purchaserId;
        this.sequence = sequence;
    }

    public RequestPurchaserKey() {

    }
}
