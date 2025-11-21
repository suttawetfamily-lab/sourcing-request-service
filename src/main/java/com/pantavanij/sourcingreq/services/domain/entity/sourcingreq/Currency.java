package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "Currency")
public class Currency {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "RecId")
    private Integer recId;
    
    @Column(name = "Code")
    private String code;
    
    @Column(name = "Name")
    private String name;

    @Column(name = "CreatedBy")
    private String createdBy;
    
    @Column(name = "CreatedDate")
    private Timestamp createdDate;
    
    @Column(name = "UpdatedBy")
    private String updatedBy;
    
    @Column(name = "UpdatedDate")
    private Timestamp updatedDate;
}
