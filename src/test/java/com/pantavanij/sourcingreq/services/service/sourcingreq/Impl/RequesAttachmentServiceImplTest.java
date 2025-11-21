package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.request.RequestAttachmentRequest;
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
public class RequesAttachmentServiceImplTest {
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private RequestItemRepository requestItemRepository;
    @Mock
    private AttachmentRepository attachmentRepository;
    @Mock
    private RequestAttachmentRepository requestAttachmentRepository;
    @InjectMocks
    private RequestAttachmentServiceImpl requestAttachmentService;
    @Test
    public void saveRequestAttachment_success() {
        RequestAttachmentRequest requestAttachmentRequest = new RequestAttachmentRequest();
        requestAttachmentRequest.setRequestId(5L);
        requestAttachmentRequest.setAttachmentId(99L);
        requestAttachmentRequest.setLineNum(1);
        requestAttachmentRequest.setNote("test");
        requestAttachmentRequest.setSendToSupplier("true");
        requestAttachmentService.saveRequestAttachment(requestAttachmentRequest);
    }
    @Test
    public void  deleteByRequestAndAttachment(){
        requestAttachmentService.deleteByRequestAndAttachment(5L,99L);
    }


}
