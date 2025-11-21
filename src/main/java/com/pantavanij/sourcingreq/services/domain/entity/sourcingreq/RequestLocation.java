package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestLocationKey;
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
@Table(name = "RequestLocation")
public class RequestLocation {

    @EmbeddedId
    private RequestLocationKey id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("requestId")
    @JoinColumn(name = "RequestId")
    @JsonBackReference
    private Request request;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("locationId")
    @JoinColumn(name = "LocationId")
    @JsonBackReference
    private Location location;
    
    @Column(name = "DeliveryLocation")
    private String deliveryLocation;
    
    @Column(name = "ContactName")
    private String contactName;
    
    @Column(name = "Phone")
    private String phone;

}
