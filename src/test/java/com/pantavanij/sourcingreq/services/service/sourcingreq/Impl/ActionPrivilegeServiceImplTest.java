package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.ActionPrivilegeDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ActionPrivilege;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.ActionPrivilegeRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.ActionPrivilegeService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ActionPrivilegeServiceImplTest {

    private ActionPrivilegeRepository actionPrivilegeRepository = mock(ActionPrivilegeRepository.class);
    private ActionPrivilegeService actionPrivilegeService = new ActionPrivilegeServiceImpl(actionPrivilegeRepository);

    @Test
    public void getActionPrivilege_success() {
        Map<String, String> mockPrivileges = new HashMap<String, String>() {{
            put("SQN", "1");
            put("SQW", "2");
        }};

        List<ActionPrivilege> mockActionPrivileges = Arrays.asList(
                ActionPrivilege.builder().recId(1).action("Create Sourcing").typeId(1).privilegeCode("SQN").build(),
                ActionPrivilege.builder().recId(2).action("Copy to PR").typeId(1).privilegeCode("SQR").build()
        );

        List<ActionPrivilegeDto> expectedResult = Arrays.asList(
                ActionPrivilegeDto.builder().recId(1).action("Create Sourcing").typeId(1).privilegeCode("SQN").build(),
                ActionPrivilegeDto.builder().recId(2).action("Copy to PR").typeId(1).privilegeCode("SQR").build()
        );

        when(actionPrivilegeRepository.findByPrivilegeCodeIn(any())).thenReturn(mockActionPrivileges);

        List<ActionPrivilegeDto> actualResult;

        try (MockedStatic mockAppUtil = mockStatic(AppUtil.class)) {
            mockAppUtil.when(AppUtil::getPrivilegeScopes).thenReturn(mockPrivileges);
            actualResult = actionPrivilegeService.getActionPrivilege();
        }

        assertEquals(expectedResult, actualResult);
    }

}