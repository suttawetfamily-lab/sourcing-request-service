package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class CategoryPurchaserKey implements Serializable {

    @Column(name = "CategoryId")
    private Integer categoryId;

    @Column(name = "PurchaserId")
    private Integer purchaserId;
}
