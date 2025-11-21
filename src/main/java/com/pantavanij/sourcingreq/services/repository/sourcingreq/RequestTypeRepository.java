package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Type;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestTypeRepository extends JpaRepository<RequestType, Integer> {

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "INSERT INTO RequestType(RequestId, TypeId, TypeCode, TypeName) VALUES(:requestId, :typeId, :typeCode, :typeName)", nativeQuery = true)
    void saveRequestType(@Param("requestId") Long requestId,
                               @Param("typeId") Integer typeId,
                               @Param("typeCode") String typeCode,
                               @Param("typeName") String typeName);

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE RequestType SET TypeCode = :typeCode, TypeName = :typeName WHERE RequestId = :requestId AND TypeId = :typeId ", nativeQuery = true)
    void updateRequestType(@Param("requestId") Long requestId,
                                 @Param("typeId") Integer typeId,
                                 @Param("typeCode") String typeCode,
                                 @Param("typeName") String typeName);


    List<RequestType> findRequestTypesByRequest(Request request);

    RequestType findRequestTypeByRequestAndType(Request request, Type type);

    void deleteRequestTypeByRequest(Request request);

    void deleteRequestTypeByRequestAndType(Request request, Type type);

    @Query(value = "SELECT TOP 1 * FROM RequestType WHERE RequestId = :requestId", nativeQuery = true)
    Optional<RequestType> findTop1ByRequestId(@Param("requestId") Long requestId);

    List<RequestType> findByRequest(Request request);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    void deleteByRequest(Request request);

}
