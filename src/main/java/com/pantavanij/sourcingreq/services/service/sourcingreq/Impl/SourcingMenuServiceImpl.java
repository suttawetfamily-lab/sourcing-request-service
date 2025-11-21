package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.SourcingMenuDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SourcingMenu;
import com.pantavanij.sourcingreq.services.domain.mapper.SourcingMenuMapper;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.SourcingMenuRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.SourcingMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SourcingMenuServiceImpl implements SourcingMenuService {

    private final SourcingMenuRepository sourcingMenuRepository;

    @Override
    public List<SourcingMenuDto> getSourcingMenuByTenantId(Integer tenantId) {
        List<SourcingMenuDto> sourcingMenuDtoList = null;

        List<SourcingMenu> sourcingMenuList = sourcingMenuRepository.getSourcingMenusByByTenantId(tenantId);
        if(sourcingMenuList.size() > 0) {
            sourcingMenuDtoList = SourcingMenuMapper.INSTANCE.toSourcingMenuDtoList(sourcingMenuList);
        }
        return sourcingMenuDtoList;
    }

}
