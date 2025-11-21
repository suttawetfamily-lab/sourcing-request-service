package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestCategoryKey;
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
@Table(name = "RequestCategory")
public class RequestCategory {

    @EmbeddedId
    private RequestCategoryKey id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("requestId")
    @JoinColumn(name = "RequestId")
    @JsonBackReference
    private Request request;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("categoryId")
    @JoinColumn(name = "CategoryId")
    @JsonBackReference
    private Category category;
    
    @Column(name = "CategoryCode")
    private String categoryCode;
    
    @Column(name = "CategoryName")
    private String categoryName;
    
}
