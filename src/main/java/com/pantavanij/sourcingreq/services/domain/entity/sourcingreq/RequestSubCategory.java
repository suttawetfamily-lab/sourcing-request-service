package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestSubCategoryKey;
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
@Table(name = "RequestSubCategory")
public class RequestSubCategory {
    @EmbeddedId
    private RequestSubCategoryKey id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("requestId")
    @JoinColumn(name = "RequestId")
    @JsonBackReference
    private Request request;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("subCategoryId")
    @JoinColumn(name = "SubCategoryId")
    @JsonBackReference
    private SubCategory subCategory;
    
    @Column(name = "SubCategoryCode")
    private String subCategoryCode;
    
    @Column(name = "SubCategoryName")
    private String subCategoryName;

}
