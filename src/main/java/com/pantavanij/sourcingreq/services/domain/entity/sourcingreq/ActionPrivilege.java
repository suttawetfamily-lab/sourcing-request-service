package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ActionPrivilege", schema = "dbo")
public class ActionPrivilege {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "RecId")
    private Integer recId;

    @Column(name = "Action")
    private String action;

    @Column(name = "TypeId")
    private int typeId;

    @Column(name = "PrivilegeCode")
    private String privilegeCode;

    @Column(name = "CreatedBy")
    private String createdBy;

    @Column(name = "UpdatedBy")
    private String updatedBy;

    @Column(name = "CreatedDate")
    private Timestamp createdDate;

    @Column(name = "UpdatedDate")
    private Timestamp updatedDate;
}
