package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "RequestQuestionnaire")
public class RequestQuestionnaire implements Cloneable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "RecId")
    private Long recId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RequestId")
    @JsonBackReference
    private Request request;

    @Column(name = "FormId")
    private Long formId;

    @Column(name = "CreatedBy")
    private String createdBy;

    @Column(name = "CreatedDate")
    private Timestamp createdDate;

    @Column(name = "UpdatedBy")
    private String updatedBy;

    @Column(name = "UpdatedDate")
    private Timestamp updatedDate;


    @OneToMany(mappedBy = "requestQuestionnaire", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonBackReference
    private List<RequestQuestionnaireFormField> requestQuestionnaireFormFields;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
