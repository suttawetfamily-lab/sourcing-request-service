package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "TenantConfig")
public class TenantConfig {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "RecId")
    private Integer recId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TenantId")
    @JsonBackReference
    private Tenant tenant;
    
    @Column(name = "Topic")
    private String topic;
    
    @Column(name = "Section")
    private String section;
    
    @Column(name = "Name")
    private String name;
    
    @Column(name = "Value")
    private String value;
    
    @Column(name = "Sequence")
    private Integer sequence;

    @Column(name = "Description")
    private String description;

    @Column(name = "CreatedBy")
    private String createdBy;
    
    @Column(name = "CreatedDate")
    private Timestamp createdDate;
    
    @Column(name = "UpdatedBy")
    private String updatedBy;
    
    @Column(name = "UpdatedDate")
    private Timestamp updatedDate;

}
