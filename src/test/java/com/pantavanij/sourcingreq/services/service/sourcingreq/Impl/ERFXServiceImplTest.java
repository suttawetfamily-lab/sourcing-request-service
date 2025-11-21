package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.config.ERFXConfig;
import com.pantavanij.sourcingreq.services.domain.dto.ERFXAttachmentDto;
import com.pantavanij.sourcingreq.services.domain.dto.ShortlistDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.request.ERFXCancelRequest;
import com.pantavanij.sourcingreq.services.domain.request.ERFXReceiveRequest;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ERFXServiceImplTest {

    @InjectMocks
    private ERFXServiceImpl erfxService;
    @Mock
    private ERFXConfig erfxConfig;

    @InjectMocks
    private RequestItemServiceImpl requestItemService;
    @Mock
    private RequestItemRepository requestItemRepository;

    @InjectMocks
    private SourcingStatusServiceImpl sourcingStatusService;
    @Mock
    private SourcingStatusRepository sourcingStatusRepository;

    @InjectMocks
    private ExistingPriceItemServiceImpl existingPriceItemService;
    @Mock
    private ExistingPriceItemRepository existingPriceItemRepository;

    @InjectMocks
    private TenantUnitServiceImpl tenantUnitService;
    @Mock
    private TenantUnitRepository tenantUnitRepository;
    @InjectMocks
    private SupplierServiceImpl supplierService;
    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private UnitServiceImpl unitService;
    @Mock
    private UnitRepository unitRepository;
    @InjectMocks
    private AttachmentServiceImpl attachmentService;
    @Mock
    private AttachmentRepository attachmentRepository;
    @InjectMocks
    private RequestItemAttachmentServiceImpl requestItemAttachmentService;
    @Mock
    private RequestItemAttachmentRepository requestItemAttachmentRepository;
    @InjectMocks
    private ExistingPriceItemAttachmentServiceImpl existingPriceItemAttachmentService;
    @Mock
    private ExistingPriceItemAttachmentRepository existingPriceItemAttachmentRepository;

    private ERFXReceiveRequest receiveRequest;
    private final String sourcingDocNo = "10010";
    private final String authCode = "auth_code";
    private final String erfxNum = "11001123";
    private ShortlistDto shortlistDto;
    private  Request request;
    private RequestItem requestItem;

    @Before
    public void init() {
        ReflectionTestUtils.setField(erfxService, "sourcingStatusService", sourcingStatusService);
        ReflectionTestUtils.setField(erfxService, "requestItemService", requestItemService);
        ReflectionTestUtils.setField(erfxService, "existingPriceItemService", existingPriceItemService);
        ReflectionTestUtils.setField(existingPriceItemService, "unitService", unitService);
        ReflectionTestUtils.setField(existingPriceItemService, "tenantUnitService", tenantUnitService);
        ReflectionTestUtils.setField(existingPriceItemService, "supplierService", supplierService);
        ReflectionTestUtils.setField(erfxService, "attachmentService", attachmentService);
        ReflectionTestUtils.setField(erfxService, "requestItemAttachmentService", requestItemAttachmentService);
        ReflectionTestUtils.setField(erfxService, "existingPriceItemAttachmentService", existingPriceItemAttachmentService);

        receiveRequest = new ERFXReceiveRequest();
        shortlistDto = new ShortlistDto();
        shortlistDto.setUnit("Unit");
//        shortlistDto.setUnitPrice(BigDecimal.ZERO);
//        shortlistDto.setSupplier("Apple");
        shortlistDto.setItemName("itemName");
        shortlistDto.setItemDetail("itemDetail");
        receiveRequest.setErfxItems(Collections.singletonList(shortlistDto));

        shortlistDto.setErfxItemId(Long.valueOf(121));
        shortlistDto.setItemId(Long.valueOf(111));
//        shortlistDto.setShortName("AAP");

        request = new Request();
        request.setRecId(Long.valueOf(1122));

        requestItem = RequestItem
                .builder()
                .recId(Long.valueOf(2231))
                .tenant(new Tenant(1, "ait", "", "", "", null))
                .request(request)
                .build();

        List<ERFXAttachmentDto> attachmentDto = new ArrayList<>();
        ERFXAttachmentDto erfxAttachmentDto = new ERFXAttachmentDto();
        erfxAttachmentDto.setUrl("c://home/attachment/test.txt");
        erfxAttachmentDto.setName("test");
        attachmentDto.add(erfxAttachmentDto);
        shortlistDto.setErfxItemAttachments(attachmentDto);
        receiveRequest.setErfxAttachments(attachmentDto);

    }

    @Test
    public void testCancelERFX_sendERFXNoAndERFXCancelRequest_shouldReturnTrue() {
        // arrange
        ERFXCancelRequest request = new ERFXCancelRequest();
        request.setReason("test cancel erfx");

        RequestItem requestItem = new RequestItem();

        when(requestItemRepository.getRequestItemBySourcingDocNoAndSourcingTypeId(anyLong(), anyInt())).thenReturn(Collections.singletonList(requestItem));
        when(sourcingStatusRepository.findSourcingStatusByRecId(anyInt())).thenReturn(new SourcingStatus());

        // act
        boolean actual = erfxService.cancelERFX(Long.valueOf(erfxNum), request);

        // assert
        verify(sourcingStatusRepository, times(1)).findSourcingStatusByRecId(anyInt());
        verify(requestItemRepository, times(1)).getRequestItemBySourcingDocNoAndSourcingTypeId(anyLong(), anyInt());
        verify(requestItemRepository, times(1)).save(any(RequestItem.class));
        assertTrue(actual);
    }

    @Test
    public void testCancelERFX_sendERFXNoNotFoundDataInDB_shouldReturnFalse() {
        // arrange
        ERFXCancelRequest request = new ERFXCancelRequest();
        request.setReason("test cancel erfx");

        when(requestItemRepository.getRequestItemBySourcingDocNoAndSourcingTypeId(anyLong(), anyInt())).thenReturn(new ArrayList<>());
        when(sourcingStatusRepository.findSourcingStatusByRecId(anyInt())).thenReturn(new SourcingStatus());

        // act
        boolean actual = erfxService.cancelERFX(Long.valueOf(erfxNum), request);

        // assert
        verify(sourcingStatusRepository, times(0)).findSourcingStatusByRecId(anyInt());
        verify(requestItemRepository, times(1)).getRequestItemBySourcingDocNoAndSourcingTypeId(anyLong(), anyInt());
        verify(requestItemRepository, times(0)).save(any(RequestItem.class));
        assertFalse(actual);
    }

    @Test
    public void testGetCreateNewURL() {
        // arrange
        String expected = "ep_to_erfx_nsr";

        // act
        String actual = erfxService.getCreateNewURL(authCode);

        // assert
        assertNotNull(actual);
        assertTrue(actual.indexOf(authCode) > -1);
        assertTrue(actual.endsWith(expected));
    }

    @Test
    public void testGetCheckStatusURL() {
        // arrange
        String expected = "ep_to_erfx_csrx";

        // act
        String actual = erfxService.getCheckStatusURL(authCode);

        // assert
        assertNotNull(actual);
        assertTrue(actual.indexOf(authCode) > -1);
        assertTrue(actual.endsWith(expected));
    }

    @Test
    public void testGetApproveShortlistURL() {
        // arrange
        String expected = "ep_to_erfx_ass";

        // act
        String actual = erfxService.getApproveShortlistURL(authCode);

        // assert
        assertNotNull(actual);
        assertTrue(actual.indexOf(authCode) > -1);
        assertTrue(actual.endsWith(expected));
    }

    @Test
    public void testGetDraftURL() {
        // arrange
        String expected = "ep_proceed_to_erfx_draft";
        // act
        String actual = erfxService.getDraftURL(authCode, erfxNum);

        // assert
        assertNotNull(actual);
        assertTrue(actual.indexOf(authCode) > -1);
        assertTrue(actual.indexOf(erfxNum) > -1);
        assertTrue(actual.indexOf(expected) > -1);
        assertTrue(actual.endsWith(erfxNum));
    }

    @Test
    public void testGetMasterERFXURL() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // arrange
        Method method = ERFXServiceImpl.class.getDeclaredMethod("getMasterERFXURL", String.class);
        method.setAccessible(true);

        // act
        String actual = (String) method.invoke(erfxService, authCode);

        // assert
        assertNotNull(actual);
        assertTrue(actual.indexOf(authCode) > -1);
    }

    @Test
    public void testGetActionERFXURL_sendURLAction_shouldReturnURL() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // arrange
        Method method = ERFXServiceImpl.class.getDeclaredMethod("getActionERFXURL", String.class);
        method.setAccessible(true);
        String actionMenu = "draft";

        // act
        String actual = (String) method.invoke(erfxService, actionMenu);

        // assert
        assertNotNull(actual);
        assertTrue(actual.indexOf(actionMenu) > -1);
    }

    @Test
    public void testGetActionERFXURL_sendURLActionAndERFXNum_shouldReturnURL() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // arrange
        Method method = ERFXServiceImpl.class.getDeclaredMethod("getActionERFXURL", String.class, String.class);
        method.setAccessible(true);
        String actionMenu = "draft";

        // act
        String actual = (String) method.invoke(erfxService, actionMenu, erfxNum);

        // assert
        assertNotNull(actual);
        assertTrue(actual.indexOf(actionMenu) > -1);
        assertTrue(actual.indexOf(erfxNum) > -1);
    }

    @Test
    public void testReceiveERFX_caseNotFoundSourcingStatus_shouldThrowBusinessException() {
        // arrange
        when(sourcingStatusRepository.findSourcingStatusByRecId(anyInt())).thenReturn(null);

        // act
        Exception actual = assertThrows(BusinessException.class, () -> erfxService.receiveERFX(sourcingDocNo, receiveRequest));

        // assert
        verify(sourcingStatusRepository, times(1)).findSourcingStatusByRecId(anyInt());
        assertEquals("E7042: SourcingStatus is not found", actual.getMessage());
    }

    @Test
    public void testReceiveERFX_caseRequestItemIsNull_shouldThrowsException() {
        // arrange
        when(sourcingStatusRepository.findSourcingStatusByRecId(anyInt())).thenReturn(new SourcingStatus());
        when(requestItemRepository.getRequestItemsByCondition(anyLong(), anyString(), anyString())).thenReturn(null);

        // act
        Exception actual = assertThrows(BusinessException.class, () -> erfxService.receiveERFX(sourcingDocNo, receiveRequest));

        // assert
        verify(sourcingStatusRepository, times(1)).findSourcingStatusByRecId(anyInt());
        verify(requestItemRepository, times(1)).getRequestItemsByCondition(anyLong(), anyString(), anyString());
        assertEquals("E7041: RequestItem is not found", actual.getMessage());

    }

    @Test
    public void testSaveShortlistERFXAttachmentRequest() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // arrange
        Method method = ERFXServiceImpl.class.getDeclaredMethod("saveShortlistERFXAttachmentRequest", ERFXReceiveRequest.class, Integer.class, Long.class);
        method.setAccessible(true);

        Attachment attachment = new Attachment();
        attachment.setRecId(Long.valueOf(1));
        when(attachmentRepository.save(any(Attachment.class))).thenReturn(attachment);
        when(requestItemRepository.findRequestItemByRecId(anyLong())).thenReturn(requestItem);
        when(attachmentRepository.findAttachmentByRecId(anyLong())
                .orElseThrow(() -> new BusinessException(ApiMessage.E7078, ApiMessage.E7078.description()))).thenReturn(attachment);
        when(requestItemAttachmentRepository.findRequestItemAttachmentByRequestItemAndAttachment(any(), any())).thenReturn(new RequestItemAttachment());

        // act
        method.invoke(erfxService, receiveRequest, 1, requestItem.getRecId());

        // assert
        verify(attachmentRepository, times(1)).save(any(Attachment.class));
        verify(requestItemRepository, times(1)).findRequestItemByRecId(anyLong());
        verify(attachmentRepository, times(1)).findAttachmentByRecId(anyLong())
                .orElseThrow(() -> new BusinessException(ApiMessage.E7078, ApiMessage.E7078.description()));
        verify(requestItemAttachmentRepository, times(1)).findRequestItemAttachmentByRequestItemAndAttachment(any(), any());
    }

    @Test
    public void testSaveShortlistERFXAttachmentRequestItem_caseExistingPriceItemAttachmentIsNull() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // arrange
        Method method = ERFXServiceImpl.class.getDeclaredMethod("saveShortlistERFXAttachmentRequestItem", Integer.class, ShortlistDto.class, ExistingPriceItem.class);
        method.setAccessible(true);

        when(attachmentRepository.save(any(Attachment.class))).thenReturn(Attachment.builder().recId(1L).build());
        when(existingPriceItemRepository.findExistingPriceItemByRecId(anyLong())).thenReturn(new ExistingPriceItem());
        when(attachmentRepository.findAttachmentByRecId(anyLong())
                .orElseThrow(() -> new BusinessException(ApiMessage.E7078, ApiMessage.E7078.description()))).thenReturn(new Attachment());
        when(existingPriceItemAttachmentRepository.findExistingPriceItemAttachmentByExistingPriceItemAndAttachment(any(ExistingPriceItem.class), any(Attachment.class))).thenReturn(null);

        ExistingPriceItem existingPriceItem = new ExistingPriceItem();
        existingPriceItem.setRecId(1L);

        // act
        method.invoke(erfxService, 1, shortlistDto, existingPriceItem);

        // assert
        verify(attachmentRepository, times(1)).save(any(Attachment.class));
        verify(existingPriceItemRepository, times(1)).findExistingPriceItemByRecId(anyLong());
        verify(attachmentRepository, times(1)).findAttachmentByRecId(anyLong())
                .orElseThrow(() -> new BusinessException(ApiMessage.E7078, ApiMessage.E7078.description()));
        verify(existingPriceItemAttachmentRepository, times(1)).findExistingPriceItemAttachmentByExistingPriceItemAndAttachment(any(), any());
        verify(existingPriceItemAttachmentRepository, times(1)).saveExistingPriceItemAttachment(anyLong(), anyLong(), anyInt(), anyBoolean(), any());
        verify(existingPriceItemAttachmentRepository, times(0)).updateExistingPriceItemAttachment(anyLong(), anyLong(), anyInt(), anyBoolean(), any());

    }

    @Test
    public void testSaveShortlistERFXAttachmentRequestItem_caseExistingPriceItemAttachmentIsNotNull() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // arrange
        Method method = ERFXServiceImpl.class.getDeclaredMethod("saveShortlistERFXAttachmentRequestItem", Integer.class, ShortlistDto.class, ExistingPriceItem.class);
        method.setAccessible(true);

        when(attachmentRepository.save(any(Attachment.class))).thenReturn(Attachment.builder().recId(1L).build());
        when(existingPriceItemRepository.findExistingPriceItemByRecId(anyLong())).thenReturn(new ExistingPriceItem());
        when(attachmentRepository.findAttachmentByRecId(anyLong())
                .orElseThrow(() -> new BusinessException(ApiMessage.E7078, ApiMessage.E7078.description()))).thenReturn(new Attachment());
        when(existingPriceItemAttachmentRepository.findExistingPriceItemAttachmentByExistingPriceItemAndAttachment(any(ExistingPriceItem.class), any(Attachment.class))).thenReturn(new ExistingPriceItemAttachment());

        ExistingPriceItem existingPriceItem = new ExistingPriceItem();
        existingPriceItem.setRecId(1L);

        // act
        method.invoke(erfxService, 1, shortlistDto, existingPriceItem);

        // assert
        verify(attachmentRepository, times(1)).save(any(Attachment.class));
        verify(existingPriceItemRepository, times(1)).findExistingPriceItemByRecId(anyLong());
        verify(attachmentRepository, times(1)).findAttachmentByRecId(anyLong())
                .orElseThrow(() -> new BusinessException(ApiMessage.E7078, ApiMessage.E7078.description()));
        verify(existingPriceItemAttachmentRepository, times(1)).findExistingPriceItemAttachmentByExistingPriceItemAndAttachment(any(), any());
        verify(existingPriceItemAttachmentRepository, times(0)).saveExistingPriceItemAttachment(anyLong(), anyLong(), anyInt(), anyBoolean(), any());
        verify(existingPriceItemAttachmentRepository, times(1)).updateExistingPriceItemAttachment(anyLong(), anyLong(), anyInt(), anyBoolean(), any());

    }


}
