package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import javax.persistence.*;
import java.sql.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "ProjectChangeLogDetail")
public class ProjectChangeLogDetail {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "RecId")
    private int recId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ChangeLogHeaderId")
    @JsonBackReference
    private ProjectChangeLogHeader projectChangeLogHeader;
    
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
