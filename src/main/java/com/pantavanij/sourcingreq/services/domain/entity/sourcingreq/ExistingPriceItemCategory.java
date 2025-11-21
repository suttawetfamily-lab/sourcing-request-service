package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "ExistingPriceItemCategory")
@IdClass(ExistingPriceItemCategoryPK.class)
public class ExistingPriceItemCategory {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ExistingPriceItemId")
    @JsonBackReference
    private ExistingPriceItem existingPriceItem;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CategoryId")
    @JsonBackReference
    private Category category;
    
    @Column(name = "CategoryCode")
    private String categoryCode;
    
    @Column(name = "CategoryName")
    private String categoryName;
    
}
