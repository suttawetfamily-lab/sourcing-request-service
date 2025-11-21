package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestItemCurrencyKey;
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
@Table(name = "RequestItemCurrency")
public class RequestItemCurrency {

    @EmbeddedId
    private RequestItemCurrencyKey id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("requestItemId")
    @JoinColumn(name = "RequestItemId")
    @JsonBackReference
    private RequestItem requestItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("currencyId")
    @JoinColumn(name = "CurrencyId")
    @JsonBackReference
    private Currency currency;

    @Column(name = "CurrencyCode")
    private String currencyCode;
    
    @Column(name = "CurrencyName")
    private String currencyName;

}
