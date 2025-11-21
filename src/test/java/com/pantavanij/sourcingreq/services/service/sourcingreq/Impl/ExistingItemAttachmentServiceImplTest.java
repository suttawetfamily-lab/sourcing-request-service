package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.request.ExistingPriceItemAttachmentRequest;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@TestPropertySource(
        locations = "classpath:application.properties")
public class ExistingItemAttachmentServiceImplTest {
    @Mock
    private ExistingPriceItemRepository existingPriceItemRepository;
    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private ExistingPriceItemAttachmentRepository existingPriceItemAttachmentRepository;

    @InjectMocks
    private ExistingPriceItemAttachmentServiceImpl existingPriceItemAttachmentService;
    @Test
    public void saveExistingPriceItemAttachment_success() {
        ExistingPriceItemAttachmentRequest existingPriceItemAttachmentRequest = new ExistingPriceItemAttachmentRequest();
        existingPriceItemAttachmentRequest.setExistingPriceItemId(207L);
        existingPriceItemAttachmentRequest.setAttachmentId(99L);
        existingPriceItemAttachmentRequest.setLineNum(1);
        existingPriceItemAttachmentRequest.setNote("test");
        existingPriceItemAttachmentRequest.setSendToSupplier("true");
        existingPriceItemAttachmentService.saveExistingPriceItemAttachment(existingPriceItemAttachmentRequest);
    }
    @Test
    public void  deleteByExistingPriceItemAndAttachment_success(){
        existingPriceItemAttachmentService.deleteByExistingPriceItemAndAttachment(207L, 99L);
    }


}
