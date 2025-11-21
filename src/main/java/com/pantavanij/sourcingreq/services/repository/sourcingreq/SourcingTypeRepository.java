package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SourcingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SourcingTypeRepository extends JpaRepository<SourcingType, Integer> {
    SourcingType findSourcingTypeByRecId(Integer recId);
}
