package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "RequestItem")
public class RequestItem {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "RecId")
    private Long recId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RequestId")
    @JsonBackReference
    private Request request;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TenantId")
    @JsonBackReference
    private Tenant tenant;

    @Column(name = "SourcingDocNo")
    private String sourcingDocNo;

    @Column(name = "SourcingDocId")
    private String sourcingDocId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SourcingTypeId")
    @JsonBackReference
    private SourcingType sourcingType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SourcingStatusId")
    @JsonBackReference
    private SourcingStatus sourcingStatus;

    @Column(name = "PurposeDescription")
    private String purposeDescription;

    @Column(name = "ItemName")
    private String itemName;

    @Column(name = "ItemDescription")
    private String itemDescription;

    @Column(name = "ItemBudget")
    private BigDecimal itemBudget;

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

    @Column(name = "DeliveryLocation")
    private String deliveryLocation;

    @Column(name = "ContactName")
    private String contactName;

    @Column(name = "Phone")
    private String phone;

    @Column(name = "DeletionReason")
    private String deletionReason;

    @Column(name = "CancellationReason")
    private String cancellationReason;

    @Column(name = "CreatedBy")
    private String createdBy;

    @Column(name = "CreatedDate")
    private Timestamp createdDate;

    @Column(name = "UpdatedBy")
    private String updatedBy;

    @Column(name = "UpdatedDate")
    private Timestamp updatedDate;

    @OneToMany(mappedBy = "requestItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonBackReference
    private List<RequestItemLocation> requestItemLocationList;

    @OneToMany(mappedBy = "requestItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonBackReference
    private List<RequestItemAttachment> requestItemAttachmentList;

    @Column(name = "BidValidityStartDate")
    private Timestamp bidValidityStartDate;

    @Column(name = "BidValidityEndDate")
    private Timestamp bidValidityEndDate;

    @Column(name = "ItemSequence")
    private Integer itemSequence;

    @Column(name = "SourcingItemSequence")
    private Integer sourcingItemSequence;

}
