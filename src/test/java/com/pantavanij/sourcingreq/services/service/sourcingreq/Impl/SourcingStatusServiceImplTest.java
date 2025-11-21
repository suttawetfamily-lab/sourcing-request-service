package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SourcingStatus;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.SourcingStatusRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class SourcingStatusServiceImplTest {

    @Mock
    private SourcingStatusRepository sourcingStatusRepository;

    @InjectMocks
    private SourcingStatusServiceImpl sourcingStatusService;

    @Before
    public void setup() {

    }

    @Test
    public void findSourcingStatusById_success() {
        SourcingStatus sourcingStatus = new SourcingStatus();

        Mockito.when(sourcingStatusRepository.findSourcingStatusByRecId(Mockito.anyInt())).thenReturn(sourcingStatus);
        Assert.assertEquals(sourcingStatus, sourcingStatusService.getSourcingStatusById(Mockito.anyInt()));
    }

}
