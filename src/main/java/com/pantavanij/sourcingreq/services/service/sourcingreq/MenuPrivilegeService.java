package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import org.springframework.data.domain.*;

import javax.validation.*;
import java.util.List;
import java.util.Map;

public interface MenuPrivilegeService {

    List<MenuPrivilegeDto> getMenuPrivilege();

    List<MenuPrivilegeObjDto> getMenuPrivilegeByTypeIdPrivilegeCode(Integer typeId, String privilegeCode);

    MenuPrivilegeDto checkPermission(String pathUrl, Integer typeId);

    MenuPrivilegeDto checkPermission(String pathUrl, Integer typeId, Map<String, String> privilegeMap);

    MenuPrivilegeDto getMenuPrivilegeById(Integer menuPrivilegeId);

    List<OptionDto> getAllMenuPrivilegeCode();

    MenuPrivilegeSearchDto searchMenuPrivilegeByCondition(MenuPrivilegeSearchRequest request, Pageable pageable);

    Integer createMenuPrivilege(MenuPrivilegeRequest request);

    Integer updateMenuPrivilege(MenuPrivilegeRequest request);

    Integer deleteMenuPrivilegeById(Integer menuPrivilegeId);

    MenuPrivilegeDto updateMenuPrivilegeSequence(SequenceRequest request);
}
