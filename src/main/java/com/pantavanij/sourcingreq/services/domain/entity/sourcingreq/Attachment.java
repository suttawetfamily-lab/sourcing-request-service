package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

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
@Table(name = "Attachment")
public class Attachment {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "RecId")
    private Long recId;
    
    @Column(name = "TenantId")
    private Integer tenantId;
    
    @Column(name = "FileId")
    private String fileId;
    
    @Column(name = "FileName")
    private String fileName;
    
    @Column(name = "FileSize")
    private Integer fileSize;

    @Column(name = "FileGroup")
    private String fileGroup;

    @Column(name = "StatusId")
    private Integer statusId;

    @Column(name = "FileURL")
    private String fileURL;
    
    @Column(name = "CreatedBy")
    private String createdBy;
    
    @Column(name = "CreatedDate")
    private Timestamp createdDate;

}
