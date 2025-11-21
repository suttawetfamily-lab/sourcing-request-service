package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.mapper.ExistingPriceItemMapper;
import com.pantavanij.sourcingreq.services.domain.request.DeleteExistingPriceItemRequest;
import com.pantavanij.sourcingreq.services.domain.request.ExistingPriceItemRequest;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UaaService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

import static com.pantavanij.sourcingreq.services.enums.SourcingStatus.SOURCING_REJECTED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ExistingPriceItemServiceImplTest {

    @Mock
    private RequestItemRepository requestItemRepository;

    @Mock
    private ExistingPriceItemRepository existingPriceItemRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private SourcingStatusRepository sourcingStatusRepository;
    @Mock
    private UaaService uaaService;

    @InjectMocks
    private ExistingPriceItemServiceImpl existingPriceItemService;

    @Before
    public void setup() {

    }

//    @Test
//    public void getExistingPriceItemByRequestItem_success() {
//        Long requestItemId = 5L;
//        String authCode = "";
//        ExistingPriceItemDto existingPriceItemDto = null;
//        RequestItem requestItem = null;
//        Mockito.when(requestItemRepository.findRequestItemByRecId(requestItemId)).thenReturn(requestItem);
//        if(requestItem != null){
//            //get data existing price --> return existingPriceItemDetail(requestItem, authCode);
//        }
//        Assert.assertEquals(existingPriceItemDto, existingPriceItemService.getExistingPriceItemByRequestItem(requestItemId, authCode));
//    }

    @Test
    public void saveExistingPriceItem_success() {
        ExistingPriceItemRequest existingPriceItemRequest = new ExistingPriceItemRequest();
        existingPriceItemRequest.setRecId(0L);
        existingPriceItemRequest.setRequestId(5L);
        existingPriceItemRequest.setRequestItemId(17L);
        existingPriceItemRequest.setTenantId(1);
        existingPriceItemRequest.setMaterialCode("material code test");
        existingPriceItemRequest.setItemName("item name test");
        existingPriceItemRequest.setItemDescription("item name description test");
        existingPriceItemRequest.setBrand("brand test");
        existingPriceItemRequest.setPartNo("part no test");
        OptionDto supplierObj = new OptionDto();
        supplierObj.setValue("D10835");
        existingPriceItemRequest.setSupplierObj(supplierObj);
        OptionDto unitDto = new OptionDto();
        unitDto.setValue("1");
        existingPriceItemRequest.setUnitObj(unitDto);
        existingPriceItemRequest.setUnitPrice(BigDecimal.TEN);
        OptionDto currencyDto = new OptionDto();
        unitDto.setValue("1");
        existingPriceItemRequest.setCurrencyObj(currencyDto);
        existingPriceItemRequest.setComment("comment test");

        ExistingPriceItem existingPriceItem = new ExistingPriceItem();

        RequestItem requestItem = null;

        if(requestItem != null) {
            Request request = null;
            Tenant tenant = null;
            Currency currency = null;
//            Supplier supplier = null;
            Unit unit = null;

            //find supplier
//            int supplierId = existingPriceItemService.getSupplier(existingPriceItemRequest.getTpShortName());

            when(requestRepository.findRequestByRecId(existingPriceItemRequest.getRequestId())).thenReturn(request);
            when(tenantRepository.findTenantByRecId(existingPriceItemRequest.getTenantId())).thenReturn(tenant);
            when(currencyRepository.findCurrenciesByRecId(Integer.parseInt(existingPriceItemRequest.getCurrencyObj().getValue()))).thenReturn(currency);
//            when(supplierRepository.findSupplierByRecId(supplierId)).thenReturn(supplier);
            when(unitRepository.findByRecId(Integer.parseInt(existingPriceItemRequest.getUnitObj().getValue()))).thenReturn(unit);

            if (!existingPriceItemRequest.getRecId().equals(0L) && existingPriceItemRequest.getRecId() != null) {
                ExistingPriceItem existingPriceItemItemHeader = null;
                when(existingPriceItemRepository.findExistingPriceItemByRecId(existingPriceItemRequest.getRecId())).thenReturn(existingPriceItemItemHeader);

                if (existingPriceItemItemHeader != null) {
                    existingPriceItem = existingPriceItemItemHeader;
                } else {
                    existingPriceItem.setCreatedBy(AppUtil.getUserName());
                    existingPriceItem.setCreatedDate(DateTimeUtil.getTimestampUTC());
                }
            } else {
                existingPriceItem.setCreatedBy(AppUtil.getUserName());
                existingPriceItem.setCreatedDate(DateTimeUtil.getTimestampUTC());
            }

            existingPriceItem.setRequest(request);
            existingPriceItem.setRequestItem(requestItem);
            existingPriceItem.setTenant(tenant);
            existingPriceItem.setMaterialCode(StringUtils.isNotBlank(existingPriceItemRequest.getMaterialCode()) ? existingPriceItemRequest.getMaterialCode() : null);
            existingPriceItem.setItemName(StringUtils.isNotBlank(existingPriceItemRequest.getItemName()) ? existingPriceItemRequest.getItemName() : null);
            existingPriceItem.setItemDescription(StringUtils.isNotBlank(existingPriceItemRequest.getItemDescription()) ? existingPriceItemRequest.getItemDescription() : null);
            existingPriceItem.setBrand(StringUtils.isNotBlank(existingPriceItemRequest.getBrand()) ? existingPriceItemRequest.getBrand() : null);
            existingPriceItem.setPartNo(StringUtils.isNotBlank(existingPriceItemRequest.getPartNo()) ? existingPriceItemRequest.getPartNo() : null);
//            existingPriceItem.setSupplier(supplier);
            existingPriceItem.setUnit(unit);
            existingPriceItem.setUnitPrice(existingPriceItemRequest.getUnitPrice());
            existingPriceItem.setCurrency(currency);
            existingPriceItem.setComment(StringUtils.isNotBlank(existingPriceItemRequest.getComment()) ? existingPriceItemRequest.getComment() : null);
            existingPriceItem.setUpdatedBy(AppUtil.getUserName());
            existingPriceItem.setUpdatedDate(DateTimeUtil.getTimestampUTC());

            existingPriceItemRepository.save(existingPriceItem);
        }
    }

    @Test
    public void deleteExistingPriceItem_success() {
        boolean isDelete = false;
        ExistingPriceItem existingPriceItem = null;
        RequestItem requestItem = null;
        SourcingStatus sourcingStatus = null;

        DeleteExistingPriceItemRequest deleteExistingPriceItemRequest = new DeleteExistingPriceItemRequest();
        deleteExistingPriceItemRequest.setExistingPriceItemId(14L);
        deleteExistingPriceItemRequest.setDeletionReason("deletion reason test");

        when(existingPriceItemRepository.findExistingPriceItemByRecId(deleteExistingPriceItemRequest.getExistingPriceItemId())).thenReturn(existingPriceItem);

        if(existingPriceItem != null){
            when(requestItemRepository.findRequestItemByRecId(existingPriceItem.getRequestItem().getRecId())).thenReturn(requestItem);
            when(sourcingStatusRepository.findSourcingStatusByRecId(requestItem.getSourcingStatus().getRecId())).thenReturn(sourcingStatus);

            SourcingStatus deleteStatus = null;
//            Mockito.when(sourcingStatusRepository.findSourcingStatusByRecId(SOURCING_DELETED.id())).thenReturn(deleteStatus);
            when(sourcingStatusRepository.findSourcingStatusByRecId(SOURCING_REJECTED.id())).thenReturn(deleteStatus);

            requestItem.setDeletionReason(StringUtils.isNotBlank(deleteExistingPriceItemRequest.getDeletionReason()) ? deleteExistingPriceItemRequest.getDeletionReason() : null);
            requestItem.setSourcingStatus(deleteStatus);
            requestItem.setUpdatedBy(AppUtil.getUserName());
            requestItem.setUpdatedDate(DateTimeUtil.getTimestampUTC());
            requestItemRepository.save(requestItem);

            isDelete = true;
        }

        Assert.assertEquals(isDelete, existingPriceItemService.deleteExistingPriceItem(anyInt(), deleteExistingPriceItemRequest));
    }

    @Test
    public void getExistingPriceItemByCondition_success() {
        existingPriceItemRepository.getExistingPriceItemByRequestIdAndRequestItemIdAndNullableSourcingDocNo(anyLong(), anyLong(), anyInt(), "");
    }

    @Test
    public void updateExistingPriceItem_success() {
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

        ExistingPriceItem existingPriceItem = null;
        if(existingPriceItem != null){
//            existingPriceItem.setUnit(unit);
//            existingPriceItem.setSupplier(supplier);
            existingPriceItem.setUpdatedDate(DateTimeUtil.getTimestampUTC());
            existingPriceItem.setUpdatedBy(AppUtil.getUserName());
            existingPriceItemRepository.save(existingPriceItem);
        }
    }

    @Test
    public void getItemByRequestAndSourcingStatus_success() {
        ExistingPriceItemSearchDto existingPriceItemSearchDto = new ExistingPriceItemSearchDto();
        String authcode = "";
        Pageable pageable = null;

        SourcingStatus sourcingStatus = new SourcingStatus();
        sourcingStatus.setRecId(1);

        Request request = new Request();

        Page<RequestItem> requestItemPage = null;
        when(requestItemRepository.findByRequestAndSourcingStatus(request,sourcingStatus, pageable)).thenReturn(requestItemPage);
        if(requestItemPage!= null && !requestItemPage.isEmpty()) {

            //add existingPriceItemList.add(existingPriceItemDto);
            requestItemRepository.saveAll(requestItemPage);
        }
        Assert.assertEquals(existingPriceItemSearchDto, existingPriceItemService.getItemByRequestAndSourcingStatus(request, sourcingStatus, pageable, authcode));

    }

    @Test
    public void getItemByRequest_success() {
        ExistingPriceItemSearchDto existingPriceItemSearchDto = new ExistingPriceItemSearchDto();
        String authcode = "";
        String pathUrl = "";
        Pageable pageable = null;

        Request request = new Request();
        request.setRecId(5L);

        Page<RequestItem> requestItemPage = null;
        when(requestItemRepository.findByRequest(request, pageable)).thenReturn(requestItemPage);
        if(requestItemPage!= null && !requestItemPage.isEmpty()) {

            //add existingPriceItemList.add(existingPriceItemDto);
            requestItemRepository.saveAll(requestItemPage);
        }
        Assert.assertEquals(existingPriceItemSearchDto, existingPriceItemService.getItemByRequest(request, pageable, authcode, pathUrl));
    }

    @Test
    public void getExistingPriceItemByRequestIdAndRequestItemId_success() {
        ExistingPriceItem existingPriceItem = null;
        Long requestId = 1234L;
        Long requestItemId = 12345L;
        Integer tenantId = 1;

        when(existingPriceItemRepository.getExistingPriceItemByRequestIdAndRequestItemIdAndNullableSourcingDocNo(requestId, requestItemId, tenantId, ""))
                .thenReturn(existingPriceItem);
        when(uaaService.getUserTimeZone(any(), null)).thenReturn("Asia/Bangkok");

        ExistingPriceItemResponseDto actualResult =
                existingPriceItemService.getExistingPriceItemByRequestIdAndRequestItemId(requestId, requestItemId, tenantId,"");
        ExistingPriceItemResponseDto expectedResult = ExistingPriceItemMapper.INSTANCE.toExistingPriceItemResponseDto(
                existingPriceItem, "Asia/Bangkok");

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getExistingPriceOptionConfig_success(){
        SourcingRequestVisibleConfig sourcingRequestVisibleConfig = new SourcingRequestVisibleConfig();
        sourcingRequestVisibleConfig.setTenantId("AIT");
        sourcingRequestVisibleConfig.setVisibled(true);

        // assert
        assertEquals("AIT", sourcingRequestVisibleConfig.getTenantId());
        assertEquals(true, sourcingRequestVisibleConfig.getVisibled());
    }

    @Test
    public void rejectExistingPriceItem_success() {
        boolean isReject = false;
        ExistingPriceItem existingPriceItem = null;
        RequestItem requestItem = null;
        SourcingStatus sourcingStatus = null;

        DeleteExistingPriceItemRequest deleteExistingPriceItemRequest = new DeleteExistingPriceItemRequest();
        deleteExistingPriceItemRequest.setExistingPriceItemId(14L);
        deleteExistingPriceItemRequest.setDeletionReason("deletion reason test");

        when(existingPriceItemRepository.findExistingPriceItemByRecId(deleteExistingPriceItemRequest.getExistingPriceItemId())).thenReturn(existingPriceItem);

        if(existingPriceItem != null){
            when(requestItemRepository.findRequestItemByRecId(existingPriceItem.getRequestItem().getRecId())).thenReturn(requestItem);
            when(sourcingStatusRepository.findSourcingStatusByRecId(requestItem.getSourcingStatus().getRecId())).thenReturn(sourcingStatus);

            SourcingStatus deleteStatus = null;
//            Mockito.when(sourcingStatusRepository.findSourcingStatusByRecId(SOURCING_DELETED.id())).thenReturn(deleteStatus);
            when(sourcingStatusRepository.findSourcingStatusByRecId(SOURCING_REJECTED.id())).thenReturn(deleteStatus);

            requestItem.setDeletionReason(StringUtils.isNotBlank(deleteExistingPriceItemRequest.getDeletionReason()) ? deleteExistingPriceItemRequest.getDeletionReason() : null);
            requestItem.setSourcingStatus(deleteStatus);
            requestItem.setUpdatedBy(AppUtil.getUserName());
            requestItem.setUpdatedDate(DateTimeUtil.getTimestampUTC());
            requestItemRepository.save(requestItem);

            isReject = true;
        }

        assertEquals(isReject, existingPriceItemService.rejectExistingPriceItem(anyInt(), deleteExistingPriceItemRequest));
    }

}
