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
@Table(name = "DataSource")
public class DataSource {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "RecId")
    private Integer recId;
    
    @Column(name = "Method")
    private String method;
    
    @Column(name = "Path")
    private String path;
    
    @Column(name = "QueryParameters")
    private String queryParameters;

    @Column(name = "MinimumSearch")
    private Integer minimumSearch;

    @Column(name = "ObjectKey")
    private String objectKey;
    
    @Column(name = "ValueKey")
    private String valueKey;
    
    @Column(name = "LabelKey")
    private String labelKey;
    
    @Column(name = "NameKey")
    private String nameKey;

    @Column(name = "FormName")
    private String formName;

    @Column(name = "QueryName")
    private String queryName;

    @Column(name = "FormValue")
    private String formValue;

    @Column(name = "CreatedBy")
    private String createdBy;
    
    @Column(name = "CreatedDate")
    private Timestamp createdDate;

}
