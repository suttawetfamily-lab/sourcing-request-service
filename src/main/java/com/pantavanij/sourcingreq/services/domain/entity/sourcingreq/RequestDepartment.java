package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestDepartmentKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "RequestDepartment")
public class RequestDepartment {
    @EmbeddedId
    private RequestDepartmentKey id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("requestId")
    @JoinColumn(name = "RequestId")
    @JsonBackReference
    private Request request;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("departmentId")
    @JoinColumn(name = "DepartmentId")
    @JsonBackReference
    private Department department;
    
    @Column(name = "DepartmentCode")
    private String departmentCode;
    
    @Column(name = "DepartmentName")
    private String departmentName;

}
