package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.SubCategoryPurchaserKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "SubCategoryPurchaser")
public class SubCategoryPurchaser {

    @EmbeddedId
    private SubCategoryPurchaserKey id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("subCategoryId")
    @JoinColumn(name = "SubCategoryId")
    @JsonBackReference
    private SubCategory subCategory;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("purchaserId")
    @JoinColumn(name = "PurchaserId")
    @JsonBackReference
    private Purchaser purchaser;

}
