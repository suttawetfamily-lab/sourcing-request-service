package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SupplierMailingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierMailingLogRepository extends JpaRepository<SupplierMailingLog, Long>  {

    List<SupplierMailingLog> findByStatus(String status);
}
