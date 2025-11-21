package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SupplierMailingQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierMailingQueueRepository extends JpaRepository<SupplierMailingQueue, Long> {

    List<SupplierMailingQueue> findByStatus(String status);
}
