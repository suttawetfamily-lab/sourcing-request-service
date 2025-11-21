package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestItemSubCategoryKey;
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
@Table(name = "RequestItemSubCategory")
public class RequestItemSubCategory {
    @EmbeddedId
    private RequestItemSubCategoryKey id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("requestItemId")
    @JoinColumn(name = "RequestItemId")
    @JsonBackReference
    private RequestItem requestItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("subCategoryId")
    @JoinColumn(name = "SubCategoryId")
    @JsonBackReference
    private TenantSubCategory subCategory;
    
    @Column(name = "SubCategoryCode")
    private String subCategoryCode;
    
    @Column(name = "SubCategoryName")
    private String subCategoryName;

}
