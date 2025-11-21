package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class SubCategoryPurchaserKey implements Serializable {

    @Column(name = "SubCategoryId")
    private Integer subCategoryId;

    @Column(name = "PurchaserId")
    private Integer purchaserId;
}
