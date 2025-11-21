package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import javax.validation.constraints.*;
import java.util.*;

@Repository
public interface ValidatorRepository extends JpaRepository<Validator, Long>, JpaSpecificationExecutor<Validator> {
    Optional<Validator> findByRecId(Integer recId);
}
