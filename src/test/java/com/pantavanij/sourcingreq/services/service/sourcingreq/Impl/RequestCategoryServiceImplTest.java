package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.CategoryRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestCategoryRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestCategoryService;
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
public class RequestCategoryServiceImplTest {
    private RequestCategoryRepository requestCategoryRepository = mock(RequestCategoryRepository.class);
    private CategoryRepository categoryRepository = mock(CategoryRepository.class);
    private RequestCategoryService requestCategoryService =
            new RequestCategoryServiceImpl(requestCategoryRepository, categoryRepository);

    @Captor
    private ArgumentCaptor<RequestCategory> requestCategoryCaptor;

    @Test
    public void hasRequestCategory_saveOrUpdate_deleteExistingThenSaveNew() {
        Integer newCategoryId = 2;
        Integer existingCategoryId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        Category newCategoryMock = Category.builder().recId(newCategoryId).code("CAT_CODE_2").name("CAT_NAME_2").build();
        Category existingCategoryMock =
                Category.builder().recId(existingCategoryId).code("CAT_CODE_1").name("CAT_NAME_1").build();

        RequestCategory requestCategoryMock = RequestCategory.builder()
                .request(requestMock)
                .category(existingCategoryMock)
                .build();

        when(requestCategoryRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.of(requestCategoryMock));
        when(categoryRepository.findById(newCategoryId)).thenReturn(Optional.of(newCategoryMock));

        requestCategoryService.saveOrUpdate(newCategoryId, requestMock);

        verify(requestCategoryRepository, times(1)).delete(requestCategoryMock);
        verify(requestCategoryRepository, times(1)).save(requestCategoryCaptor.capture());
        RequestCategory actualResult = requestCategoryCaptor.getValue();

        assertEquals(newCategoryMock.getCode(), actualResult.getCategoryCode());
        assertEquals(newCategoryMock.getName(), actualResult.getCategoryName());
        assertEquals(newCategoryId, actualResult.getCategory().getRecId());
        assertEquals(requestId, actualResult.getRequest().getRecId());
    }

    @Test
    public void noRequestCategory_saveOrUpdate_SaveNew() {
        Integer categoryId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        Category categoryMock = Category.builder().recId(categoryId).code("CAT_CODE").name("CAT_NAME").build();

        when(requestCategoryRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.empty());
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryMock));

        requestCategoryService.saveOrUpdate(categoryId, requestMock);

        verify(requestCategoryRepository, never()).delete(any());
        verify(requestCategoryRepository, times(1)).save(requestCategoryCaptor.capture());
        RequestCategory actualResult = requestCategoryCaptor.getValue();

        assertEquals(categoryMock.getCode(), actualResult.getCategoryCode());
        assertEquals(categoryMock.getName(), actualResult.getCategoryName());
        assertEquals(categoryId, actualResult.getCategory().getRecId());
        assertEquals(requestId, actualResult.getRequest().getRecId());
    }

    @Test
    public void sameRequestCategory_saveOrUpdate_doNothing() {
        Integer categoryId = 1;
        Integer existingCategoryId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        Category categoryMock = Category.builder().recId(existingCategoryId).code("CAT_CODE").name("CAT_NAME").build();

        RequestCategory requestCategoryMock = RequestCategory.builder()
                .request(requestMock)
                .category(categoryMock)
                .build();

        when(requestCategoryRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.of(requestCategoryMock));

        requestCategoryService.saveOrUpdate(categoryId, requestMock);

        verify(requestCategoryRepository, never()).delete(any());
        verify(requestCategoryRepository, never()).save(any());
    }

    @Test
    public void categoryNotFound_saveOrUpdate_throwBusinessException() {
        Integer categoryId = 1;
        Integer existingCategoryId = 2;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        Category categoryMock = Category.builder().recId(existingCategoryId).code("CAT_CODE").name("CAT_NAME").build();

        RequestCategory requestCategoryMock = RequestCategory.builder()
                .request(requestMock)
                .category(categoryMock)
                .build();

        when(requestCategoryRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.of(requestCategoryMock));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> requestCategoryService.saveOrUpdate(categoryId, requestMock));
    }

    @Test
    public void categoryIdIsNull_saveOrUpdate_deleteExisting() {
        Integer newCategoryId = null;
        Integer existingCategoryId = 1;
        Long requestId = 12345L;
        Request requestMock = Request.builder().recId(requestId).build();
        Category existingCategoryMock =
                Category.builder().recId(existingCategoryId).code("CAT_CODE").name("CAT_NAME").build();

        RequestCategory requestCategoryMock = RequestCategory.builder()
                .request(requestMock)
                .category(existingCategoryMock)
                .categoryCode(existingCategoryMock.getCode())
                .categoryName(existingCategoryMock.getName())
                .build();

        when(requestCategoryRepository.findTop1ByRequestId(requestId)).thenReturn(Optional.of(requestCategoryMock));

        requestCategoryService.saveOrUpdate(newCategoryId, requestMock);

        verify(requestCategoryRepository, times(1)).delete(requestCategoryCaptor.capture());
        verify(requestCategoryRepository, never()).save(any());
        RequestCategory actualResult = requestCategoryCaptor.getValue();

        assertEquals(existingCategoryMock.getCode(), actualResult.getCategoryCode());
        assertEquals(existingCategoryMock.getName(), actualResult.getCategoryName());
        assertEquals(existingCategoryId, actualResult.getCategory().getRecId());
        assertEquals(requestId, actualResult.getRequest().getRecId());
    }
}