package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.ExcSourcingRequestItemKey;
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
@Table(name = "ExcSourcingRequestItem")
public class ExcSourcingRequestItem {

    @EmbeddedId
    private ExcSourcingRequestItemKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("excSourcingId")
    @JoinColumn(name = "ExcSourcingId")
    private ExcSourcing excSourcing;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("requestItemId")
    @JoinColumn(name = "RequestItemId")
    private RequestItem requestItem;

}
