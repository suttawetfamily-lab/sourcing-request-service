package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import javax.persistence.*;
import java.sql.*;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@Entity
@Table(name = "EmailActivity")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EmailActivity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "RecId")
    private Long recId;

    @Column(name = "ActivityName")
    private String activityName;

    @Column(name = "Sequence")
    private Integer sequence;

    @Column(name = "[Default]")
    private boolean isDefault;

    @Column(name = "[Active]")
    private boolean active;

    @Column(name = "CreatedBy")
    private String createdBy;

    @Column(name = "CreatedDate")
    private Timestamp createdDate;

    @Column(name = "UpdatedBy")
    private String updatedBy;

    @Column(name = "UpdatedDate")
    private Timestamp updatedDate;
}
