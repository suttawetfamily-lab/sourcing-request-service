package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class ExistingPriceItemSupplierKey implements Serializable {
    @Column(name = "ExistingPriceItemId")
    private Long existingPriceItemId;

    @Column(name = "SupplierId")
    private Integer supplierId;

    public ExistingPriceItemSupplierKey(Long existingPriceItemId, Integer supplierId) {
        this.existingPriceItemId = existingPriceItemId;
        this.supplierId = supplierId;
    }

    public ExistingPriceItemSupplierKey() {

    }
}
