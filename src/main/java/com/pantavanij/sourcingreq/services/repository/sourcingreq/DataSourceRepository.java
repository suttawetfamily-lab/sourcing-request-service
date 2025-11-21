package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import javax.validation.constraints.*;
import java.util.*;

@Repository
public interface DataSourceRepository extends JpaRepository<DataSource, Integer>, JpaSpecificationExecutor<DataSource> {
    Optional<DataSource> findByRecId(Integer dataSourceId);
}
