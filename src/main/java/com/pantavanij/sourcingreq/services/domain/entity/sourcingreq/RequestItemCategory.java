package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestItemCategoryKey;
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
@Table(name = "RequestItemCategory")
public class RequestItemCategory {
    @EmbeddedId
    private RequestItemCategoryKey id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("requestItemId")
    @JoinColumn(name = "RequestItemId")
    @JsonBackReference
    private RequestItem requestItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("categoryId")
    @JoinColumn(name = "CategoryId")
    @JsonBackReference
    private TenantCategory category;
    
    @Column(name = "CategoryCode")
    private String categoryCode;
    
    @Column(name = "CategoryName")
    private String categoryName;

}
