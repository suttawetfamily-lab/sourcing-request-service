package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestSubCategoryRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.SubCategoryRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestSubCategoryService;
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
public class RequestSubCategoryServiceImplTest {
    private RequestSubCategoryRepository requestSubCategoryRepository = mock(RequestSubCategoryRepository.class);
    private SubCategoryRepository subCategoryRepository = mock(SubCategoryRepository.class);
    private RequestSubCategoryService requestSubCategoryService =
            new RequestSubCategoryServiceImpl(requestSubCategoryRepository, subCategoryRepository);

    @Captor
    private ArgumentCaptor<RequestSubCategory> requestSubCategoryCaptor;

    @Test
    public void hasRequestSubCategory_saveOrUpdate_deleteExistingThenSaveNew() {
        Integer newCategoryId = 2;
        Integer existingCategoryId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        SubCategory newSubCategoryMock =
                SubCategory.builder().recId(newCategoryId).code("SUB_CAT_CODE_2").name("SUB_CAT_NAME_2").build();
        SubCategory existingSubCategoryMock =
                SubCategory.builder().recId(existingCategoryId).code("SUB_CAT_CODE_1").name("SUB_CAT_NAME_1").build();

        RequestSubCategory requestCategoryMock = RequestSubCategory.builder()
                .request(requestMock)
                .subCategory(existingSubCategoryMock)
                .build();

        when(requestSubCategoryRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.of(requestCategoryMock));
        when(subCategoryRepository.findById(newCategoryId)).thenReturn(Optional.of(newSubCategoryMock));

        requestSubCategoryService.saveOrUpdate(newCategoryId, requestMock);

        verify(requestSubCategoryRepository, times(1)).delete(requestCategoryMock);
        verify(requestSubCategoryRepository, times(1)).save(requestSubCategoryCaptor.capture());
        RequestSubCategory actualResult = requestSubCategoryCaptor.getValue();

        assertEquals(newSubCategoryMock.getCode(), actualResult.getSubCategoryCode());
        assertEquals(newSubCategoryMock.getName(), actualResult.getSubCategoryName());
        assertEquals(newCategoryId, actualResult.getSubCategory().getRecId());
        assertEquals(requestId, actualResult.getRequest().getRecId());
    }

    @Test
    public void noRequestSubCategory_saveOrUpdate_SaveNew() {
        Integer categoryId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        SubCategory subCategoryMock =
                SubCategory.builder().recId(categoryId).code("SUB_CAT_CODE").name("SUB_CAT_NAME").build();

        when(requestSubCategoryRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.empty());
        when(subCategoryRepository.findById(categoryId)).thenReturn(Optional.of(subCategoryMock));

        requestSubCategoryService.saveOrUpdate(categoryId, requestMock);

        verify(requestSubCategoryRepository, never()).delete(any());
        verify(requestSubCategoryRepository, times(1)).save(requestSubCategoryCaptor.capture());
        RequestSubCategory actualResult = requestSubCategoryCaptor.getValue();

        assertEquals(subCategoryMock.getCode(), actualResult.getSubCategoryCode());
        assertEquals(subCategoryMock.getName(), actualResult.getSubCategoryName());
        assertEquals(categoryId, actualResult.getSubCategory().getRecId());
        assertEquals(requestId, actualResult.getRequest().getRecId());
    }

    @Test
    public void sameRequestSubCategory_saveOrUpdate_doNothing() {
        Integer newSubCategoryId = 1;
        Integer existingSubCategoryId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        SubCategory existingSubCategoryMock =
                SubCategory.builder().recId(existingSubCategoryId).code("SUB_CAT_CODE").name("SUB_CAT_NAME").build();

        RequestSubCategory requestSubCategoryMock = RequestSubCategory.builder()
                .request(requestMock)
                .subCategory(existingSubCategoryMock)
                .build();

        when(requestSubCategoryRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.of(requestSubCategoryMock));

        requestSubCategoryService.saveOrUpdate(newSubCategoryId, requestMock);

        verify(requestSubCategoryRepository, never()).delete(any());
        verify(requestSubCategoryRepository, never()).save(any());
    }

    @Test
    public void subCategoryNotFound_saveOrUpdate_throwBusinessException() {
        Integer categoryId = 2;
        Integer existingCategoryId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        SubCategory subCategoryMock =
                SubCategory.builder().recId(existingCategoryId).code("SUB_CAT_CODE").name("SUB_CAT_NAME").build();

        RequestSubCategory requestCategoryMock = RequestSubCategory.builder()
                .request(requestMock)
                .subCategory(subCategoryMock)
                .build();

        when(requestSubCategoryRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.of(requestCategoryMock));
        when(subCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> requestSubCategoryService.saveOrUpdate(categoryId, requestMock));
    }

    @Test
    public void subCategoryIdIsNull_saveOrUpdate_deleteExisting() {
        Integer newSubCategoryId = null;
        Integer existingSubCategoryId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        SubCategory existingSubCategoryMock =
                SubCategory.builder().recId(existingSubCategoryId).code("SUB_CODE").name("SUB_NAME").build();

        RequestSubCategory requestSubCategoryMock = RequestSubCategory.builder()
                .request(requestMock)
                .subCategory(existingSubCategoryMock)
                .subCategoryCode(existingSubCategoryMock.getCode())
                .subCategoryName(existingSubCategoryMock.getName())
                .build();

        when(requestSubCategoryRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.of(requestSubCategoryMock));

        requestSubCategoryService.saveOrUpdate(newSubCategoryId, requestMock);

        verify(requestSubCategoryRepository, times(1)).delete(requestSubCategoryCaptor.capture());
        verify(requestSubCategoryRepository, never()).save(any());
        RequestSubCategory actualResult = requestSubCategoryCaptor.getValue();

        assertEquals(existingSubCategoryMock.getCode(), actualResult.getSubCategoryCode());
        assertEquals(existingSubCategoryMock.getName(), actualResult.getSubCategoryName());
        assertEquals(existingSubCategoryId, actualResult.getSubCategory().getRecId());
        assertEquals(requestId, actualResult.getRequest().getRecId());
    }
}