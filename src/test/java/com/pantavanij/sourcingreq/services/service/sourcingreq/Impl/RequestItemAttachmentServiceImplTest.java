package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Attachment;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItem;
import com.pantavanij.sourcingreq.services.domain.request.RequestItemAttachmentRequest;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.AttachmentRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestItemAttachmentRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestItemRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource(
        locations = "classpath:application.properties")
public class RequestItemAttachmentServiceImplTest {
    @Mock
    private RequestItemRepository requestItemRepository;
    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private RequestItemAttachmentRepository requestItemAttachmentRepository;

    @InjectMocks
    private RequestItemAttachmentServiceImpl requestItemAttachmentService;
    @Test
    public void saveRequestItemAttachment_success() {
        RequestItemAttachmentRequest requestItemAttachmentRequest = new RequestItemAttachmentRequest();
        requestItemAttachmentRequest.setAttachmentId(99L);
        requestItemAttachmentRequest.setLineNum(1);
        requestItemAttachmentRequest.setNote("test");
        requestItemAttachmentRequest.setSendToSupplier("true");
        requestItemAttachmentRequest.setRequestItemId(173L);
        RequestItem item =new RequestItem();
        Attachment attachment = new Attachment();
        when(requestItemRepository.findRequestItemByRecId(173L)).thenReturn(item);
        when(attachmentRepository.findAttachmentByRecId(99L)
                .orElseThrow(() -> new BusinessException(ApiMessage.E7078, ApiMessage.E7078.description()))).thenReturn(attachment);
        requestItemAttachmentService.saveRequestItemAttachment(requestItemAttachmentRequest);
    }
    @Test
    public void  deleteByRequestItemAndAttachment_success(){
        requestItemAttachmentService.deleteByRequestItemAndAttachment(173L,99L);
    }


}
