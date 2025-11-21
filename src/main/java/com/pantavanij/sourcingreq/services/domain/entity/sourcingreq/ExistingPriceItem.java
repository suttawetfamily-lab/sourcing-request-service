package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "ExistingPriceItem")
public class ExistingPriceItem {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "RecId")
    private Long recId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RequestId")
    @JsonBackReference
    private Request request;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RequestItemId")
    @JsonBackReference
    private RequestItem requestItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TenantId")
    @JsonBackReference
    private Tenant tenant;
    
    @Column(name = "MaterialCode")
    private String materialCode;
    
    @Column(name = "ItemName")
    private String itemName;
    
    @Column(name = "ItemDescription")
    private String itemDescription;

    @Column(name = "Brand")
    private String brand;

    @Column(name = "PartNo")
    private String partNo;

    @Column(name = "Conditions")
    private String conditions;

    @Column(name = "Quantity")
    private BigDecimal quantity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UnitId")
    @JsonBackReference
    private Unit unit;

    @Column(name = "UnitPrice")
    private BigDecimal unitPrice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CurrencyId")
    @JsonBackReference
    private Currency currency;

    @Column(name = "Comment")
    private String comment;

    @Column(name = "VatTypeId")
    private Integer vatTypeId;
    
    @Column(name = "CreatedBy")
    private String createdBy;
    
    @Column(name = "CreatedDate")
    private Timestamp createdDate;
    
    @Column(name = "UpdatedBy")
    private String updatedBy;
    
    @Column(name = "UpdatedDate")
    private Timestamp updatedDate;

    @OneToMany(mappedBy = "existingPriceItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonBackReference
    private List<ExistingPriceItemAttachment> existingPriceItemAttachmentList;

    @OneToMany(mappedBy = "existingPriceItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnoreProperties
    private List<ExistingPriceItemSupplier> existingPriceItemSupplierList;

    @Column(name = "SourcingTypeId")
    private Integer sourcingTypeId;

    @Column(name = "SourcingDocNo")
    private String sourcingDocNo;


}
