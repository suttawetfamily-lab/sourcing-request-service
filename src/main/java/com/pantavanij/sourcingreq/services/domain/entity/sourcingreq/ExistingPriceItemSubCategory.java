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
@Table(name = "ExistingPriceItemSubCategory")
@IdClass(ExistingPriceItemSubCategoryPK.class)
public class ExistingPriceItemSubCategory {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ExistingPriceItemId")
    @JsonBackReference
    private ExistingPriceItem existingPriceItem;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SubCategoryId")
    @JsonBackReference
    private SubCategory subCategory;
    
    @Column(name = "SubCategoryCode")
    private String subCategoryCode;
    
    @Column(name = "SubCategoryName")
    private String subCategoryName;

}
