package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "RequestItemAdditional")
public class RequestItemAdditional {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "RecId")
    private Long recId;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RequestItemId")
    @JsonBackReference
    private RequestItem requestItem;
    @Column(name = "BidStartDate")
    private Timestamp bidStartDate;
    @Column(name = "BidCompleteDate")
    private Timestamp bidCompleteDate;
    @Column(name = "BidCompleteMonth")
    private String bidCompleteMonth;
    @Column(name = "BidCompleteYear")
    private String bidCompleteYear;
    @Column(name = "BidNo")
    private String bidNo;
    @Column(name = "BiddingType")
    private String biddingType;
    @Column(name = "BidDescription")
    private String bidDescription;
//    @Column(name = "TaxNo")
//    private String taxNo;
    @Column(name = "VatType")
    private String vatType;

//    @Column(name = "BasePrice")
//    private BigDecimal basePrice;
//    @Column(name = "TotalProjectedPrice")
//    private BigDecimal totalProjectedPrice;
//    @Column(name = "TotalProjectedPriceVat7Percentage")
//    private BigDecimal totalProjectedPriceVat7Percentage;

    @Column(name = "CostAvoidanceVat7Percentage")
    private BigDecimal costAvoidanceVat7Percentage;
    @Column(name = "CreatedBy")
    private String createdBy;
    @Column(name = "CreatedDate")
    private Timestamp createdDate;
    @Column(name = "UpdatedBy")
    private String updatedBy;
    @Column(name = "UpdatedDate")
    private Timestamp updatedDate;
}
