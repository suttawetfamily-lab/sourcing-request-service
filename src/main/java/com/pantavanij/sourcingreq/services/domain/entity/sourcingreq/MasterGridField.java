package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "MasterGridField")
public class MasterGridField {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @javax.persistence.Column(name = "RecId")
    private Integer recId;

    @Column(name = "Code")
    private String code;
    
    @Column(name = "DisplayName")
    private String displayName;
    
    @Column(name = "GroupName")
    private String groupName;
    
    @Column(name = "Sequence")
    private int sequence;
    
    @Column(name = "Searchable")
    private boolean searchable;
    
    @Column(name = "Sorting")
    private String sorting;
    
    @Column(name = "Width")
    private Integer width;
    
    @Column(name = "Type")
    private String type;
    
    @Column(name = "visible")
    private boolean visible;
    
    @Column(name = "CreatedBy")
    private String createdBy;

    @Column(name = "CreatedDate")
    private Timestamp createdDate;
    
    @Column(name = "UpdatedBy")
    private String updatedBy;
    
    @Column(name = "UpdatedDate")
    private Timestamp updatedDate;

}
