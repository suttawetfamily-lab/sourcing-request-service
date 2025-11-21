package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItem;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItemPr;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItemPrPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RequestItemPrRepository extends JpaRepository<RequestItemPr, RequestItemPrPK>  {

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "INSERT INTO RequestItemPr(RequestItemId, PRId) VALUES(:requestItemId, :prId)", nativeQuery = true)
    void saveRequestItemPr(@Param("requestItemId") Long requestItemId, @Param("prId") Integer prId);

    List<RequestItemPr> findByRequestItem(RequestItem requestItem);

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    void deleteByRequestItem(RequestItem requestItem);
}
