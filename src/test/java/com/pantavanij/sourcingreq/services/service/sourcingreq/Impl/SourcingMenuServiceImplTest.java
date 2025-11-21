package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.SourcingMenuDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SourcingMenu;
import com.pantavanij.sourcingreq.services.domain.mapper.SourcingMenuMapper;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.SourcingMenuRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class SourcingMenuServiceImplTest {

    @Mock
    private SourcingMenuRepository sourcingMenuRepository;

    @InjectMocks
    private SourcingMenuServiceImpl sourcingMenuService;

    @Before
    public void setup() {

    }

    @Test
    public void findSourcingMenuByTenantId_success() {
        List<SourcingMenu> sourcingMenuOptional = new ArrayList<>();
        List<SourcingMenuDto> sourcingMenuDtoOptional = null;

        Mockito.when(sourcingMenuRepository.getSourcingMenusByByTenantId(Mockito.anyInt())).thenReturn(sourcingMenuOptional);
        if(sourcingMenuOptional.size() > 0){
            sourcingMenuDtoOptional = SourcingMenuMapper.INSTANCE.toSourcingMenuDtoList(sourcingMenuOptional);
        }
        Assert.assertEquals(sourcingMenuDtoOptional, sourcingMenuService.getSourcingMenuByTenantId(Mockito.anyInt()));
    }
}
