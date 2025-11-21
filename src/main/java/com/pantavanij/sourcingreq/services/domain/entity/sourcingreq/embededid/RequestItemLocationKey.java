package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class RequestItemLocationKey implements Serializable {
    @Column(name = "RequestItemId")
    private Long requestItemId;

    @Column(name = "LocationId")
    private Integer locationId;
}
