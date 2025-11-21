package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestItemLocationKey;
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
@Table(name = "RequestItemLocation")
public class RequestItemLocation  {

    @EmbeddedId
    private RequestItemLocationKey id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("requestItemId")
    @JoinColumn(name = "RequestItemId")
    @JsonBackReference
    private RequestItem requestItem;

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
