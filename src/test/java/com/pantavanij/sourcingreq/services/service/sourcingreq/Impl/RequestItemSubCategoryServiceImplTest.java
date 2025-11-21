package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItem;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestItemSubCategory;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SubCategory;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.TenantSubCategory;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestItemSubCategoryRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.SubCategoryRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantSubCategoryRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestItemSubCategoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RequestItemSubCategoryServiceImplTest {

    private RequestItemSubCategoryRepository requestItemSubCategoryRepository = mock(RequestItemSubCategoryRepository.class);
    private SubCategoryRepository subCategoryRepository = mock(SubCategoryRepository.class);

    private TenantSubCategoryRepository tenantSubCategoryRepository = mock(TenantSubCategoryRepository.class);
    private RequestItemSubCategoryService requestItemSubCategoryService =
            new RequestItemSubCategoryServiceImpl(requestItemSubCategoryRepository, subCategoryRepository, tenantSubCategoryRepository);

    @Captor
    private ArgumentCaptor<RequestItemSubCategory> requestItemSubCategoryCaptor;

    @Test
    public void hasRequestItemSubCategory_saveOrUpdate_deleteExistingThenSaveNew() {
        Long newSubCategoryId = 2L;
        Long existingSubCategoryId = 1L;
        Long requestItemId = 12345L;
        RequestItem requestItemMock = RequestItem.builder().recId(requestItemId).build();
        TenantSubCategory newSubCategoryMock = TenantSubCategory.builder().id(newSubCategoryId).subCategoryCode("SUB_CAT_2").subCategoryName("SUB_CAT_2").build();
        TenantSubCategory existingSubCategoryMock =
                TenantSubCategory.builder().id(existingSubCategoryId).subCategoryCode("SUB_CAT_1").subCategoryName("SUB_CAT_1").build();

        RequestItemSubCategory requestItemSubCategoryMock = RequestItemSubCategory.builder()
                .requestItem(requestItemMock)
                .subCategory(existingSubCategoryMock)
                .build();

        when(requestItemSubCategoryRepository.findTop1ByRequestItemId(requestItemId)).thenReturn(Optional.of(requestItemSubCategoryMock));
        when(tenantSubCategoryRepository.findById(newSubCategoryId)).thenReturn(Optional.of(newSubCategoryMock));

        requestItemSubCategoryService.saveOrUpdate(newSubCategoryId.intValue(), requestItemMock);

        verify(requestItemSubCategoryRepository, times(1)).delete(requestItemSubCategoryMock);
        verify(requestItemSubCategoryRepository, times(1)).save(requestItemSubCategoryCaptor.capture());
        RequestItemSubCategory actualResult = requestItemSubCategoryCaptor.getValue();

        assertEquals(newSubCategoryMock.getSubCategoryCode(), actualResult.getSubCategoryCode());
        assertEquals(newSubCategoryMock.getSubCategoryName(), actualResult.getSubCategoryName());
        assertEquals(newSubCategoryId, actualResult.getSubCategory().getId());
        assertEquals(requestItemId, actualResult.getRequestItem().getRecId());
    }

    @Test
    public void noRequestItemSubCategory_saveOrUpdate_SaveNew() {
        Integer subCategoryId = 3;
        Long requestItemId = 12345L;
        RequestItem requestItemMock = RequestItem.builder().recId(requestItemId).build();
        SubCategory subCategoryMock = SubCategory.builder().recId(subCategoryId).code("SUB_CAT").name("SUB_CAT").build();

        when(requestItemSubCategoryRepository.findTop1ByRequestItemId(requestItemId)).thenReturn(Optional.empty());
        when(subCategoryRepository.findById(subCategoryId)).thenReturn(Optional.of(subCategoryMock));

        requestItemSubCategoryService.saveOrUpdate(subCategoryId, requestItemMock);

        verify(requestItemSubCategoryRepository, never()).delete(any());
        verify(requestItemSubCategoryRepository, times(1)).save(requestItemSubCategoryCaptor.capture());
        RequestItemSubCategory actualResult = requestItemSubCategoryCaptor.getValue();

        assertEquals(subCategoryMock.getCode(), actualResult.getSubCategoryCode());
        assertEquals(subCategoryMock.getName(), actualResult.getSubCategoryName());
        assertEquals(subCategoryId, actualResult.getSubCategory().getId());
        assertEquals(requestItemId, actualResult.getRequestItem().getRecId());
    }

    @Test
    public void sameRequestItemSubCategory_saveOrUpdate_doNothing() {
        Integer subCategoryId = 1;
        Long existingSubCategoryId = 1L;
        Long requestItemId = 12345L;
        RequestItem requestItemMock = RequestItem.builder().recId(requestItemId).build();
        TenantSubCategory subCategoryMock = TenantSubCategory.builder().id(existingSubCategoryId).subCategoryCode("SUB_CAT").subCategoryName("SUB_CAT").build();

        RequestItemSubCategory requestItemSubCategoryMock = RequestItemSubCategory.builder()
                .requestItem(requestItemMock)
                .subCategory(subCategoryMock)
                .build();

        when(requestItemSubCategoryRepository.findTop1ByRequestItemId(requestItemId)).thenReturn(Optional.of(requestItemSubCategoryMock));

        requestItemSubCategoryService.saveOrUpdate(subCategoryId, requestItemMock);

        verify(requestItemSubCategoryRepository, never()).delete(any());
        verify(requestItemSubCategoryRepository, never()).save(any());
    }

    @Test
    public void subCategoryNotFound_saveOrUpdate_throwBusinessException() {
        Integer subCategoryId = 1;
        Long existingSubCategoryId = 2L;
        Long requestItemId = 12345L;
        RequestItem requestItemMock = RequestItem.builder().recId(requestItemId).build();
        TenantSubCategory subCategoryMock = TenantSubCategory.builder().id(existingSubCategoryId).subCategoryCode("SUB_CAT").subCategoryName("SUB_CAT").build();

        RequestItemSubCategory requestItemSubCategoryMock = RequestItemSubCategory.builder()
                .requestItem(requestItemMock)
                .subCategory(subCategoryMock)
                .build();

        when(requestItemSubCategoryRepository.findTop1ByRequestItemId(requestItemId)).thenReturn(Optional.of(requestItemSubCategoryMock));
        when(subCategoryRepository.findById(subCategoryId)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> requestItemSubCategoryService.saveOrUpdate(subCategoryId, requestItemMock));
    }

    @Test
    public void subCategoryIdIsNull_saveOrUpdate_deleteExisting() {
        Integer newSubCategoryId = null;
        Long existingSubCategoryId = 1L;
        Long requestItemId = 12345L;
        RequestItem requestItemMock = RequestItem.builder().recId(requestItemId).build();
        TenantSubCategory existingSubCategoryMock =
                TenantSubCategory.builder().id(existingSubCategoryId).subCategoryCode("SUB_CAT").subCategoryName("SUB_CAT").build();

        RequestItemSubCategory requestItemSubCategoryMock = RequestItemSubCategory.builder()
                .requestItem(requestItemMock)
                .subCategory(existingSubCategoryMock)
                .subCategoryCode(existingSubCategoryMock.getSubCategoryCode())
                .subCategoryName(existingSubCategoryMock.getSubCategoryName())
                .build();

        when(requestItemSubCategoryRepository.findTop1ByRequestItemId(requestItemId)).thenReturn(Optional.of(requestItemSubCategoryMock));

        requestItemSubCategoryService.saveOrUpdate(newSubCategoryId, requestItemMock);

        verify(requestItemSubCategoryRepository, times(1)).delete(requestItemSubCategoryCaptor.capture());
        verify(requestItemSubCategoryRepository, never()).save(any());
        RequestItemSubCategory actualResult = requestItemSubCategoryCaptor.getValue();

        assertEquals(existingSubCategoryMock.getSubCategoryCode(), actualResult.getSubCategoryCode());
        assertEquals(existingSubCategoryMock.getSubCategoryName(), actualResult.getSubCategoryName());
        assertEquals(existingSubCategoryId, actualResult.getSubCategory().getId());
        assertEquals(requestItemId, actualResult.getRequestItem().getRecId());
    }

}