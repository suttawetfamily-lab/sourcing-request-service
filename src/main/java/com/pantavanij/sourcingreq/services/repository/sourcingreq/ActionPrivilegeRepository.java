package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ActionPrivilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionPrivilegeRepository extends JpaRepository<ActionPrivilege, Integer> {

    List<ActionPrivilege> findByPrivilegeCodeIn(List<String> privilegeCodes);

}
