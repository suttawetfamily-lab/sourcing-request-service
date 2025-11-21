package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.DataSourceDto;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponseStatus;
import com.pantavanij.sourcingreq.services.enums.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.DataSourceService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataSourceControllerTest {

    @Mock
    private DataSourceService dataSourceService;

    @InjectMocks
    private DataSourceController dataSourceController;

    private List<DataSourceDto> mockDataSourceList;

    @Before
    public void setup() {
        // Initialize test data
        mockDataSourceList = new ArrayList<>();

        DataSourceDto dataSource1 = new DataSourceDto();
        dataSource1.setRecId(1);
        dataSource1.setName("Test Source 1");
        mockDataSourceList.add(dataSource1);

        DataSourceDto dataSource2 = new DataSourceDto();
        dataSource2.setRecId(2);
        dataSource2.setName("Test Source 2");
        mockDataSourceList.add(dataSource2);
    }

    @Test
    public void testDatasource_ShouldReturnSuccessMessage() {
        ResponseEntity<?> response = dataSourceController.testDatasource();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Data source controller...", ((ApiResponse<?>) response.getBody()).getData());
    }

    @Test
    public void getAllDataSource_WithData_ShouldReturnDataList() {
        when(dataSourceService.getAllDataSource()).thenReturn(mockDataSourceList);

        ResponseEntity<?> response = dataSourceController.getAllDataSource();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockDataSourceList, ((ApiResponse<?>) response.getBody()).getData());
    }

    @Test
    public void getAllDataSource_WithEmptyData_ShouldReturnErrorMessage() {
        when(dataSourceService.getAllDataSource()).thenReturn(new ArrayList<>());

        ResponseEntity<?> response = dataSourceController.getAllDataSource();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Data is not found", ((ApiResponseStatus) response.getBody()).getDescription());
    }
}
