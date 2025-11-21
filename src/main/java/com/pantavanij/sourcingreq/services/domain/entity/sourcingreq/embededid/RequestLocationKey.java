package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
@Data
@Embeddable
public class RequestLocationKey implements Serializable {
    @Column(name = "RequestId")
    private long requestId;

    @Column(name = "LocationId")
    private int locationId;

}
