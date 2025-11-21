package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Department;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestDepartment;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestDepartmentRepository extends JpaRepository<RequestDepartment, Integer> {

    List<RequestDepartment> findByDepartment_RecIdIn(List<Integer> departmentIds);

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "INSERT INTO RequestDepartment(RequestId, DepartmentId, DepartmentCode, DepartmentName) VALUES(:requestId, :departmentId, :departmentCode, :departmentName)", nativeQuery = true)
    void saveRequestDepartment(@Param("requestId") Long requestId,
                               @Param("departmentId") Integer departmentId,
                               @Param("departmentCode") String departmentCode,
                               @Param("departmentName") String departmentName);

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE RequestDepartment SET DepartmentCode = :departmentCode, DepartmentName = :departmentName WHERE RequestId = :requestId AND DepartmentId = :departmentId ", nativeQuery = true)
    void updateRequestDepartment(@Param("requestId") Long requestId,
                                 @Param("departmentId") Integer departmentId,
                                 @Param("departmentCode") String departmentCode,
                                 @Param("departmentName") String departmentName);


    List<RequestDepartment> findRequestDepartmentsByRequest(Request request);

    RequestDepartment findRequestDepartmentByRequestAndDepartment(Request request, Department department);

    void deleteRequestDepartmentByRequest(Request request);

    void deleteRequestDepartmentByRequestAndDepartment(Request request, Department department);

    @Query(value = "SELECT TOP 1 * FROM RequestDepartment WHERE RequestId = :requestId", nativeQuery = true)
    Optional<RequestDepartment> findTop1ByRequestId(@Param("requestId") Long requestId);

    List<RequestDepartment> findByRequest(Request request);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    void deleteByRequest(Request request);
}
