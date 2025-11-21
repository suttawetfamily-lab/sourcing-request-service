package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.CategoryPurchaserKey;
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
@Table(name = "CategoryPurchaser")
public class CategoryPurchaser {

    @EmbeddedId
    private CategoryPurchaserKey id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("categoryId")
    @JoinColumn(name = "CategoryId")
    @JsonBackReference
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("purchaserId")
    @JoinColumn(name = "PurchaserId")
    @JsonBackReference
    private Purchaser purchaser;

}
