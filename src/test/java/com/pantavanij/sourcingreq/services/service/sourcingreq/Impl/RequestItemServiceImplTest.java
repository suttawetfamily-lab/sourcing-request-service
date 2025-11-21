package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.mapper.RequestItemAttachmentMapper;
import com.pantavanij.sourcingreq.services.domain.mapper.RequestItemLocationMapper;
import com.pantavanij.sourcingreq.services.domain.mapper.RequestItemMapper;
import com.pantavanij.sourcingreq.services.domain.request.DeleteRequestItemRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequestItemRequest;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.ExcelService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.LocationService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UaaService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UnitService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import com.pantavanij.sourcingreq.services.util.FileUtil;
import com.pantavanij.sourcingreq.util.TestUtil;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RequestItemServiceImplTest {

    @Mock
    private RequestItemRepository requestItemRepository;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private SourcingTypeRepository sourcingTypeRepository;

    @Mock
    private SourcingStatusRepository sourcingStatusRepository;

    @Mock
    private RequestItemAttachmentRepository requestItemAttachmentRepository;

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private RequestItemLocationRepository requestItemLocationRepository;

    @Mock
    private TenantConfigRepository tenantConfigRepository;

    @InjectMocks
    private RequestItemServiceImpl requestItemService;

    @Mock
    private FileUtil fileUtil;

    @Mock
    private UnitService unitService;

    @Mock
    private LocationService locationService;

    @Mock
    private ExcelService excelService;

    @Mock
    private UaaService uaaService;

    @Before
    public void setup() {
    }

    @Test
    public void saveRequestItem_success() {
        RequestItemRequest requestItemRequest = new RequestItemRequest();
        requestItemRequest.setRecId(1L);
        requestItemRequest.setRequestId(5L);
        requestItemRequest.setTenantId(1);
        requestItemRequest.setPurposeDescription("purpose description test");
        requestItemRequest.setItemName("item name test");
        requestItemRequest.setItemDescription("item nam description test");
        requestItemRequest.setConditions("conditions test");
        requestItemRequest.setQuantity(BigDecimal.ZERO);
        OptionDto unitDto = new OptionDto();
        unitDto.setValue("1");
        requestItemRequest.setUnitObj(unitDto);
        requestItemRequest.setLocation("Contact Address");
        requestItemRequest.setDeliveryLocation(new LocationDto());
        requestItemRequest.setContactName("contact name test");
        requestItemRequest.setContactPhone("023456789#09");

        Request request = new Request();
        when(requestRepository.findRequestsByRecId(5L)).thenReturn(request);

        requestItemService.saveRequestItem(requestItemRequest);
    }

    @Test
    public void findRequestItemByRequestId_success() {
        List<RequestItemDto> requestItemDtoList = new ArrayList<>();
        List<RequestItem> requestItemList = new ArrayList<>();
        String mockTimeZone = TestUtil.getMockTimeZone();

        when(requestItemRepository.getRequestItemByRequestId(anyLong())).thenReturn(requestItemList);
        when(uaaService.getUserTimeZone(any(), null)).thenReturn(mockTimeZone);

        if (requestItemList != null && !requestItemList.isEmpty()) {
            requestItemDtoList = RequestItemMapper.INSTANCE.toRequestItemDtoList(requestItemList, mockTimeZone);
        }

        assertEquals(requestItemDtoList, requestItemService.getRequestItemByRequestId(Mockito.anyLong(), false));
    }

    @Test
    public void findRequestItemByRecId_success() {
        RequestItem requestItem = null;
        List<RequestItemAttachment> requestItemAttachmentList = new ArrayList<>();
        List<RequestItemLocation> requestItemLocationList = new ArrayList<>();
        String mockTimeZone = TestUtil.getMockTimeZone();

        when(requestItemRepository.findRequestItemByRecId(anyLong())).thenReturn(requestItem);

        RequestItemDto requestItemDto = null;

        if (requestItem != null) {
            requestItemDto = RequestItemMapper.INSTANCE.toRequestItemDto(requestItem, mockTimeZone);
            requestItemDto.setRequestItemAttachmentList(RequestItemAttachmentMapper.INSTANCE.toRequestItemAttachmentDtoList(requestItemAttachmentList));
            requestItemDto.setRequestItemLocationList(RequestItemLocationMapper.INSTANCE.toRequestItemLocationDtoList(requestItemLocationList));
        }
        assertEquals(requestItemDto, requestItemService.findRequestItemByRecId(anyLong()));
    }

    @Test
    public void deleteRequestItemByRecId_success() {
        requestItemService.deleteRequestItemByRecId(anyLong());
    }

    @Test
    public void deleteSourcingRequestItem_success() {
        boolean isDelete = false;

        DeleteRequestItemRequest deleteRequestItemRequest = new DeleteRequestItemRequest();
        deleteRequestItemRequest.setRequestItemId(11L);
        deleteRequestItemRequest.setDeletionReason("deletion reason test");

        RequestItem requestItem = null;
        when(requestItemRepository.findRequestItemByRecId(deleteRequestItemRequest.getRequestItemId())).thenReturn(requestItem);

        if (requestItem != null) {
            SourcingStatus sourcingStatus = null;
            when(sourcingStatusRepository.findSourcingStatusByRecId(requestItem.getSourcingStatus().getRecId())).thenReturn(sourcingStatus);
        }
        assertEquals(isDelete, requestItemService.deleteSourcingRequestItem(Mockito.anyInt(), deleteRequestItemRequest));
    }

    @Test
    public void getRequestItemsByCondition_success() {
        RequestItem requestItem = null;
        when(requestItemRepository.getRequestItemsByCondition(anyLong(), Mockito.anyString(), Mockito.anyString())).thenReturn(requestItem);
        assertEquals(requestItem, requestItemService.getRequestItemsByCondition(anyLong(), Mockito.anyString(), Mockito.anyString()));
    }

    @Test
    public void updateRequestItem_success() {
        String sourcingDocNo = "doc no - 01";
        ShortlistDto shortlist = new ShortlistDto();
        shortlist.setItemId(0L);
        shortlist.setItemName("item name");
        shortlist.setItemDetail("item detail");
        shortlist.setQuantity(BigDecimal.ONE);
        shortlist.setCondition("condition");
        shortlist.setErfxItemId(1L);

        SourcingStatus sourcingStatus = new SourcingStatus();
        sourcingStatus.setRecId(1);

        RequestItem requestItem = null;
        if (requestItem != null) {
            requestItem.setItemName(shortlist.getItemName());
            requestItem.setItemDescription(shortlist.getItemDetail());
            requestItem.setQuantity(shortlist.getQuantity());

            if (!StringUtils.isEmpty(shortlist.getCondition())) {
                requestItem.setConditions(shortlist.getCondition());
            }

            requestItem.setSourcingStatus(sourcingStatus);
            requestItem.setUpdatedDate(DateTimeUtil.getTimestampUTC());
            requestItem.setUpdatedBy(AppUtil.getUserName());
            requestItemRepository.save(requestItem);
        }

    }

    @Test
    public void updateRequestItemBySourcingStatus_success() {
        requestItemRepository.updateRequestItemBySourcingStatus(Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt());
    }

    @Test
    public void RequestItemByRequestIdAndSourcingStatusId_success() {
        Long requestId = 0L;
        Integer sourcingStatusId = 0;

        List<RequestItemDto> requestItemDtoList = null;
        Request request = null;
        String mockTimeZone = TestUtil.getMockTimeZone();
        when(requestRepository.findRequestByRecId(requestId)).thenReturn(request);

        if (request != null) {
            SourcingStatus sourcingStatus = null;
            when(sourcingStatusRepository.findSourcingStatusByRecId(sourcingStatusId)).thenReturn(sourcingStatus);
            if (sourcingStatus != null) {
                List<RequestItem> requestItemList = new ArrayList<>();
                when(requestItemRepository.getRequestItemByRequestAndSourcingStatus(request, sourcingStatus)).thenReturn(requestItemList);
                if (requestItemList != null && !requestItemList.isEmpty()) {
                    requestItemDtoList = RequestItemMapper.INSTANCE.toRequestItemDtoList(requestItemList, mockTimeZone);
                }
            }
        }

        assertEquals(requestItemDtoList, requestItemService.getRequestItemByRequestIdAndSourcingStatusId(requestId, sourcingStatusId));

    }

    @Test
    public void getRequestItemByRequestIdList_success() {
        List<RequestItemDto> requestItemDtoList = new ArrayList<>();
        Long requestId = 5L;
        List<Long> requestItemsId = new ArrayList<>();
        Integer tenantId = 1;
        String mockTimeZone = TestUtil.getMockTimeZone();

        requestItemsId.add(11L);

        List<RequestItem> requestItemList = new ArrayList<>();
        when(requestItemRepository.getRequestItemByRequestItemsId(requestId, requestItemsId, tenantId)).thenReturn(requestItemList);
        when(uaaService.getUserTimeZone(any(), null)).thenReturn(mockTimeZone);

        if (requestItemList != null && !requestItemList.isEmpty()) {
            requestItemDtoList = RequestItemMapper.INSTANCE.toRequestItemDtoList(requestItemList, mockTimeZone);
        }
        assertEquals(requestItemDtoList, requestItemService.getRequestItemByRequestIdList(requestId, requestItemsId, tenantId));
    }

    @Test
    public void getRequestItemByRequestItemId_success() {
        RequestItem requestItem = null;
        when(requestItemRepository.findRequestItemByRecId(anyLong())).thenReturn(requestItem);
        assertEquals(requestItem, requestItemService.getRequestItemByRequestItemId(anyLong()));
    }

//    @Test
//    public void downloadRequestItemTemplate_success() throws IOException {
//        String templateName = "templateName";
//        String code = "ait";
//        Integer requestTypeId = 1;
//        byte[] bytes = "MOCK_STR".getBytes(StandardCharsets.US_ASCII);
//        ByteArrayResource byteArrayResource = new ByteArrayResource(bytes);
//        Tenant tenant = Tenant.builder().recId(1).code(code).build();
//        List<String> expectedUnits = Arrays.asList("Package", "Unit");
//        List<LocationDto> locations = Collections.singletonList(LocationDto.builder().build());
//
//        when(fileUtil.downloadFile(templateName, "attachments/ait/template")).thenReturn(bytes);
//        when(unitService.getUnitByTenantIdV1(tenant.getRecId())).thenReturn(
//                Arrays.asList(
//                        UnitDto.builder().unitCode("Package").build(),
//                        UnitDto.builder().unitCode("Unit").build()));
//        when(locationService.getLocationByTenantId(tenant.getRecId())).thenReturn(locations);
//        when(excelService.generateRequestItemTemplateMasterData(
//                byteArrayResource, requestTypeId, expectedUnits, locations)).thenReturn(byteArrayResource);
//
//        ByteArrayResource actualResult = requestItemService.downloadRequestItemTemplate(tenant, templateName, requestTypeId);
//
//        assertEquals(byteArrayResource, actualResult);
//    }

    @Test
    public void testDeleteRequestItemByRecId(){
        // arrange
        Long requestItemId = Long.valueOf(112233);
        when(requestItemRepository.findRequestItemByRecId(anyLong())).thenReturn(new RequestItem());
        when(requestItemAttachmentRepository.findRequestItemAttachmentsByRequestItem(any(RequestItem.class))).thenReturn(Collections.singletonList(new RequestItemAttachment()));

        // act
        requestItemService.deleteRequestItemByRecId(requestItemId);

        // assert
        verify(requestItemRepository, times(1)).findRequestItemByRecId(anyLong());
        verify(requestItemAttachmentRepository, times(1)).findRequestItemAttachmentsByRequestItem(any(RequestItem.class));
        verify(requestItemAttachmentRepository, times(1)).deleteRequestItemAttachmentByRequestItemId(anyLong());
        verify(requestItemLocationRepository, times(1)).deleteByRequestItem(anyLong());
        verify(requestItemRepository, times(1)).deleteRequestItemByRecId(anyLong());
    }

    @Test
    public void testGetRequestItemsByCondition(){
        // arrange
        Long itemId = Long.valueOf(1122);
        String sourcingDocNo = "123123";
        String sourcingDocId = "223344";

        RequestItem requestItem = new RequestItem();
        requestItem.setRecId(itemId);
        requestItem.setSourcingDocNo(sourcingDocNo);
        requestItem.setSourcingDocId(sourcingDocId);

        when(requestItemRepository.getRequestItemsByCondition(anyLong(), anyString(), anyString())).thenReturn(requestItem);

        // act
        RequestItem actual = requestItemService.getRequestItemsByCondition(itemId, sourcingDocNo, sourcingDocId);

        // assert
        assertEquals(itemId, actual.getRecId());
        assertEquals(sourcingDocNo, actual.getSourcingDocNo());
        assertEquals(sourcingDocId, actual.getSourcingDocId());

    }

    @Test
    public void testUpdateRequestItem_caseNotFountRequestItem_shouldThrowsException() {
        // arrange
        ShortlistDto shortlist = new ShortlistDto();
        shortlist.setItemId(Long.valueOf(1));
        shortlist.setErfxItemId(Long.valueOf(11));

        SourcingStatus sourcingStatus = new SourcingStatus();

        String sourcingDocNo = "100123";

        when(requestItemRepository.getRequestItemsByCondition(anyLong(), anyString(), anyString())).thenReturn(null);

        // act
        Exception actual = assertThrows(BusinessException.class, () -> requestItemService.updateRequestItem(shortlist, sourcingStatus, sourcingDocNo));

        // assert
       assertEquals("E7041: RequestItem is not found", actual.getMessage());

    }

    @Test
    public void testUpdateRequestItem_caseSuccess_shouldReturnRequestItem() {
        // arrange
        ShortlistDto shortlist = new ShortlistDto();
        shortlist.setItemId(Long.valueOf(1));
        shortlist.setErfxItemId(Long.valueOf(11));
        shortlist.setQuantity(BigDecimal.ZERO);

        SourcingStatus sourcingStatus = new SourcingStatus();

        String sourcingDocNo = "100123";

        RequestItem requestItem = new RequestItem();
        requestItem.setQuantity(BigDecimal.ZERO);
        requestItem.setRequest(new Request());
        requestItem.setSourcingStatus(new SourcingStatus());
        requestItem.setConditions("");

        when(requestItemRepository.getRequestItemsByCondition(anyLong(), anyString(), anyString())).thenReturn(requestItem);
        when(requestItemRepository.save(any(RequestItem.class))).thenReturn(requestItem);

        // act
        RequestItem actual = requestItemService.updateRequestItem(shortlist, sourcingStatus, sourcingDocNo);

        // assert
        verify(requestItemRepository, times(1)).getRequestItemsByCondition(anyLong(), anyString(), anyString());
        verify(requestItemRepository, times(1)).save(any(RequestItem.class));

        assertNotNull(actual);
        assertNotNull(actual.getRequest());
        assertNotNull(actual.getSourcingStatus());
        assertEquals(requestItem.getConditions(), actual.getConditions());
        assertEquals(requestItem.getQuantity(), actual.getQuantity());
        assertEquals(requestItem.getQuantity(), actual.getQuantity());

    }

    @Test
    public void testUpdateRequestItemBySourcingStatus(){
        // arrange
        // act
        requestItemService.updateRequestItemBySourcingStatus(1, "112233", 1);

        // assert
        verify(requestItemRepository, times(1)).updateRequestItemBySourcingStatus(anyInt(), anyString(), anyInt());
    }

    @Test
    public void testGetRequestItemByRequestIdAndSourcingStatusId_caseNotFoundRequest_shouldReturnNull(){
        // arrange
        Long requestId = Long.valueOf(11);
        Integer sourcingStatusId = 1;

        // act
        List<RequestItemDto> actual = requestItemService.getRequestItemByRequestIdAndSourcingStatusId(requestId, sourcingStatusId);

        // assert
        verify(requestRepository, times(1)).findRequestByRecId(anyLong());
        assertNull(actual);
    }

    @Test
    public void testGetRequestItemByRequestIdAndSourcingStatusId_caseNotFoundSourcingStatus_shouldReturnNull(){
        // arrange
        Long requestId = Long.valueOf(11);
        Integer sourcingStatusId = 1;

        when(requestRepository.findRequestByRecId(anyLong())).thenReturn(new Request());
        when(sourcingStatusRepository.findSourcingStatusByRecId(anyInt())).thenReturn(null);

        // act
        List<RequestItemDto> actual = requestItemService.getRequestItemByRequestIdAndSourcingStatusId(requestId, sourcingStatusId);

        // assert
        verify(requestRepository, times(1)).findRequestByRecId(anyLong());
        verify(sourcingStatusRepository, times(1)).findSourcingStatusByRecId(anyInt());
        assertNull(actual);
    }

    @Test
    public void testGetRequestItemByRequestIdAndSourcingStatusId_caseNotFoundRequestItemList_shouldReturnNull(){
        // arrange
        Long requestId = Long.valueOf(11);
        Integer sourcingStatusId = 1;

        when(requestRepository.findRequestByRecId(anyLong())).thenReturn(new Request());
        when(sourcingStatusRepository.findSourcingStatusByRecId(anyInt())).thenReturn(new SourcingStatus());
        when(requestItemRepository.getRequestItemByRequestAndSourcingStatus(any(Request.class), any(SourcingStatus.class))).thenReturn(null);

        // act
        List<RequestItemDto> actual = requestItemService.getRequestItemByRequestIdAndSourcingStatusId(requestId, sourcingStatusId);

        // assert
        verify(requestRepository, times(1)).findRequestByRecId(anyLong());
        verify(sourcingStatusRepository, times(1)).findSourcingStatusByRecId(anyInt());
        verify(requestItemRepository, times(1)).getRequestItemByRequestAndSourcingStatus(any(Request.class), any(SourcingStatus.class));
        assertNull(actual);
    }

    @Test
    public void testGetRequestItemByRequestItemId(){
        // arrange
        Long requestItemId = Long.valueOf(123);
        RequestItem requestItem = new RequestItem();
        requestItem.setRecId(Long.valueOf(123));

        when(requestItemRepository.findRequestItemByRecId(anyLong())).thenReturn(requestItem);

        // act
        RequestItem actual = requestItemService.getRequestItemByRequestItemId(requestItemId);

        // assert
        assertNotNull(actual);
        assertEquals(requestItemId, actual.getRecId());

    }

    @Test
    public void rejectSourcingRequestItem_success() {
        boolean isReject = false;

        DeleteRequestItemRequest deleteRequestItemRequest = new DeleteRequestItemRequest();
        deleteRequestItemRequest.setRequestItemId(11L);
        deleteRequestItemRequest.setDeletionReason("deletion reason test");

        RequestItem requestItem = null;
        when(requestItemRepository.findRequestItemByRecId(deleteRequestItemRequest.getRequestItemId())).thenReturn(requestItem);

        if (requestItem != null) {
            SourcingStatus sourcingStatus = null;
            when(sourcingStatusRepository.findSourcingStatusByRecId(requestItem.getSourcingStatus().getRecId())).thenReturn(sourcingStatus);
        }
        assertEquals(isReject, requestItemService.rejectSourcingRequestItem(Mockito.anyInt(), deleteRequestItemRequest));
    }

}
