package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestObjectiveKey;
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
@Table(name = "RequestObjective")
public class RequestObjective {
    @EmbeddedId
    private RequestObjectiveKey id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("requestId")
    @JoinColumn(name = "RequestId")
    @JsonBackReference
    private Request request;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("objectiveId")
    @JoinColumn(name = "ObjectiveId")
    @JsonBackReference
    private Objective objective;
    
    @Column(name = "ObjectiveCode")
    private String objectiveCode;
    
    @Column(name = "ObjectiveName")
    private String objectiveName;

}
