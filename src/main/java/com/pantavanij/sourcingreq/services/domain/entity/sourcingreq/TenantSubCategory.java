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
@Table(name = "TenantSubCategory")
public class TenantSubCategory {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "RecId")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CategoryId")
    @JsonBackReference
    private TenantCategory category;

    @Column(name = "Code")
    private String subCategoryCode;

    @Column(name = "Name")
    private String subCategoryName;

    @Column(name = "Buyer")
    private String buyer;

    @Column(name = "Email")
    private String email;

    @Column(name = "Telephone")
    private String telephone;

    @Column(name = "IsActive")
    private boolean isActive;

    @Column(name = "CreatedBy")
    private String createdBy;

    @Column(name = "CreatedDate")
    private Timestamp createdDate;

    @Column(name = "UpdatedBy")
    private String updatedBy;

    @Column(name = "UpdatedDate")
    private Timestamp updatedDate;
}
