package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestBudgetRefNoKey;
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
@Table(name = "RequestBudgetRefNo")
public class RequestBudgetRefNo {

    @EmbeddedId
    private RequestBudgetRefNoKey id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("requestId")
    @JoinColumn(name = "RequestId")
    @JsonBackReference
    private Request request;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("budgetRefNoId")
    @JoinColumn(name = "BudgetRefNoId")
    @JsonBackReference
    private BudgetRefNo budgetRefNo;
    
    @Column(name = "BudgetRefNoCode")
    private String budgetRefNoCode;
    
    @Column(name = "BudgetRefNoName")
    private String budgetRefNoName;
    
}
