package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.ExistingPriceItemSupplierKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "ExistingPriceItemSupplier")
public class ExistingPriceItemSupplier {

    @EmbeddedId
    private ExistingPriceItemSupplierKey id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("existingPriceItemId")
    @JoinColumn(name = "ExistingPriceItemId")
    @JsonBackReference
    private ExistingPriceItem existingPriceItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("supplierId")
    @JoinColumn(name = "SupplierId")
    @JsonBackReference
    private Supplier supplier;
    
    @Column(name = "SupplierShortName")
    private String supplierShortName;
    
    @Column(name = "SupplierFullName")
    private String supplierFullName;

    @Column(name = "TaxId")
    private String taxId;

    @Column(name = "AwardedType")
    private String awardedType;

    @Column(name = "AwardedValue")
    private String awardedValue;

    @Column(name = "BasePrice")
    private BigDecimal basePrice;

    @Column(name = "UnitPrice")
    private BigDecimal unitPrice;

    @Column(name = "CreatedBy")
    private String createdBy;

    @Column(name = "CreatedDate")
    private Timestamp createdDate;

    @Column(name = "UpdatedBy")
    private String updatedBy;

    @Column(name = "UpdatedDate")
    private Timestamp updatedDate;

}
