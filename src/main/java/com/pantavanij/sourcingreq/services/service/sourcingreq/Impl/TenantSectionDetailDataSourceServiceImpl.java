package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.DataSource;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantSectionDetail;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantSectionDetailDataSource;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.TenantSectionDetailDataSourceKey;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.DataSourceRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantSectionDetailDataSourceRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantSectionDetailDataSourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Service
public class TenantSectionDetailDataSourceServiceImpl implements TenantSectionDetailDataSourceService {

    private final TenantSectionDetailDataSourceRepository tenantSectionDetailDataSourceRepository;

    private final DataSourceRepository dataSourceRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(Integer dataSourceId, TenantSectionDetail tenantSectionDetail) {}
}
