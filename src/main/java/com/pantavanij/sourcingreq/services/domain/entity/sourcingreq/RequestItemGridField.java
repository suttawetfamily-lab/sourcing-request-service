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
@Table(name = "RequestItemGridField")
public class RequestItemGridField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer recId;

    private String code;

    private String displayName;

    private Integer sequence;

    private String sorting;

    private Integer width;

    private String type;

    private boolean visible;

    private String tooltip;

    private String align;

    private String createdBy;

    private Timestamp createdDate;

    private String updatedBy;

    private Timestamp updatedDate;

}
