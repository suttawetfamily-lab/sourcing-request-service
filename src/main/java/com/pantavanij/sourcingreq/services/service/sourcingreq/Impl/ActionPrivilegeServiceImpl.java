package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.ActionPrivilegeDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ActionPrivilege;
import com.pantavanij.sourcingreq.services.domain.mapper.ActionPrivilegeMapper;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.ActionPrivilegeRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.ActionPrivilegeService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActionPrivilegeServiceImpl implements ActionPrivilegeService {

    private final ActionPrivilegeRepository actionPrivilegeRepository;

    @Override
    public List<ActionPrivilegeDto> getActionPrivilege() {
        Map<String, String> privilegeMap = AppUtil.getPrivilegeScopes();
        List<String> privilegeList = privilegeMap.entrySet().stream().map(m -> m.getKey()).collect(Collectors.toList());
        List<ActionPrivilege> actionPrivilegeList = actionPrivilegeRepository.findByPrivilegeCodeIn(privilegeList);
        return ActionPrivilegeMapper.INSTANCE.toActionPrivilegeDto(actionPrivilegeList);
    }

}
