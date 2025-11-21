package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.TenantDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Attachment;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.AttachmentRepository;
import com.pantavanij.sourcingreq.services.util.FileUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AttachmentServiceImplTest {

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private FileUtil fileUtil;

    @InjectMocks
    private AttachmentServiceImpl attachmentService;

    @Test
    public void deleteUnusedAttachment_success() throws Exception {
        String FIRST_FIELD = "3126f323-90d1-4bb0-9e06-1402c34e0f65.png";
        String SECOND_FIELD = "2cf5a2e6-0f7a-4387-9f38-63847341101d.xlsx";
        String CODE = "ait";
        Tenant tenant = Tenant.builder().recId(1).code("ait").build();
        List<Attachment> attachment = Arrays.asList(
                Attachment.builder().fileId(FIRST_FIELD).build(),
                Attachment.builder().fileId(SECOND_FIELD).build()
        );
        when(attachmentRepository.getUnusedAttachment(1)).thenReturn(attachment);
        when(fileUtil.removeFile(any(), any())).thenReturn(true);

        attachmentService.deleteUnusedAttachment(tenant);

        verify(fileUtil, times(1)).removeFile(FIRST_FIELD, "attachments/" + CODE);
        verify(fileUtil, times(1)).removeFile(SECOND_FIELD, "attachments/" + CODE);
        verify(attachmentRepository, times(1)).delete(attachment.get(0));
        verify(attachmentRepository, times(1)).delete(attachment.get(1));
    }

}