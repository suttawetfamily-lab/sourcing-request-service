package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import javax.persistence.*;
import java.sql.*;
import java.util.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "ProjectChangeLogHeader")
public class ProjectChangeLogHeader {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "RecId")
    private int recId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TenantId")
    @JsonBackReference
    private Tenant tenant;

    @OneToMany(mappedBy = "projectChangeLogHeader", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectChangeLogDetail> projectChangeLogDetails;
    
    @Column(name = "FileId")
    private String fileId;
    
    @Column(name = "FileName")
    private String fileName;
    
    @Column(name = "FileSize")
    private int fileSize;
    
    @Column(name = "CreatedBy")
    private String createdBy;
    
    @Column(name = "CreatedDate")
    private Timestamp createdDate;
}
