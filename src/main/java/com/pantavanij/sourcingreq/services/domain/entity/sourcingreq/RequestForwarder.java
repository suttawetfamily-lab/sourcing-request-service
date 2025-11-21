package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "RequestForwarder")
public class RequestForwarder {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name="RecId")
    private Long recId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "RequestId")
    @JsonBackReference
    private Request request;

    @Column(name = "FromApprover")
    private String fromApprover;

    @Column(name = "ToApprover")
    private String toApprover;

    @Column(name = "CreatedBy")
    private String createdBy;

    @Column(name = "CreatedDate")
    private Timestamp createdDate;

}
