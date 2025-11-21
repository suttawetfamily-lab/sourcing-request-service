package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class RequestSubCategoryKey implements Serializable {
    @Column(name = "RequestId")
    private Long requestId;

    @Column(name = "SubCategoryId")
    private Integer subCategoryId;
}
