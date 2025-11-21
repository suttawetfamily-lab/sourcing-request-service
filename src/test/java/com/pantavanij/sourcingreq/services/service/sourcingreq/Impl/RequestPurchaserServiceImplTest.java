package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.SourcingRequestVisibleConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class RequestPurchaserServiceImplTest {

    @InjectMocks
    private RequestPurchaserServiceImpl requestApproverService;

    @Test
    public void getApprovalAllTapConfig_success(){
        SourcingRequestVisibleConfig sourcingRequestVisibleConfig = new SourcingRequestVisibleConfig();
        sourcingRequestVisibleConfig.setTenantId("AIT");
        sourcingRequestVisibleConfig.setVisibled(true);

        // assert
        assertEquals("AIT", sourcingRequestVisibleConfig.getTenantId());
        assertEquals(true, sourcingRequestVisibleConfig.getVisibled());
    }

}
