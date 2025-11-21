package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ByteArrayResource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

@RunWith(MockitoJUnitRunner.class)
public class ExcelServiceImplTest {

    @InjectMocks
    private ExcelServiceImpl excelService;

//    @Test
//    public void generateRequestItemTemplateMasterData_requestTypeIsQuantity_success() throws IOException {
//        // Given
//        ByteArrayResource byteArrayResource = getTemplateFile();
//        List<String> units = Arrays.asList("Package", "Unit");
//        List<LocationDto> locations = Arrays.asList(
//                LocationDto.builder().name("Stock  สำนักงานใหญ่").build(),
//                LocationDto.builder().name("Stock เมืองทอง").build(),
//                LocationDto.builder().name("Other").build()
//        );
//        List<String> expectedLocations = locations.stream().map(LocationDto::getName).collect(Collectors.toList());
//
//        // When
//        ByteArrayResource actualResult = excelService.generateRequestItemTemplateMasterData(
//                byteArrayResource, REQUEST_TYPE_QUANTITY.id(), units, locations);
//
//        // Then
//        InputStream targetStream = ByteSource.wrap(actualResult.getByteArray()).openStream();
//        XSSFWorkbook workbook = new XSSFWorkbook(targetStream);
//        XSSFSheet masterDataSheet = workbook.getSheet("MasterData");
//        List<String> actualUnits = new ArrayList();
//        List<String> actualLocations = new ArrayList();
//
//        Iterator<Row> rowIterator = masterDataSheet.iterator();
//        while(rowIterator.hasNext()) {
//            Row row = rowIterator.next();
//            if (row.getCell(0) != null) {
//                actualUnits.add(row.getCell(0).getStringCellValue());
//            }
//
//            if (row.getCell(1) != null) {
//                actualLocations.add(row.getCell(1).getStringCellValue());
//            }
//        }
//
//        assertEquals(units, actualUnits);
//        assertEquals(expectedLocations, actualLocations);
//        assertNotNull(workbook.getSheet("Quantity"));
//        assertNull(workbook.getSheet("Condition"));
//        assertNotNull(workbook.getSheet("MasterData"));
//    }

//    @Test
//    public void generateRequestItemTemplateMasterData_requestTypeIsCondition_success() throws IOException {
//        // Give
//        ByteArrayResource byteArrayResource = getTemplateFile();
//        List<String> units = Arrays.asList("Package", "Unit");
//        List<LocationDto> locations = Arrays.asList(
//                LocationDto.builder().name("Stock  สำนักงานใหญ่").build(),
//                LocationDto.builder().name("Stock เมืองทอง").build(),
//                LocationDto.builder().name("Other").build()
//        );
//        List<String> expectedLocations = locations.stream().map(LocationDto::getName).collect(Collectors.toList());
//
//        // When
//        ByteArrayResource actualResult = excelService.generateRequestItemTemplateMasterData(
//                byteArrayResource, REQUEST_TYPE_CONDITION.id(), units, locations);
//
//        // Then
//        InputStream targetStream = ByteSource.wrap(actualResult.getByteArray()).openStream();
//        XSSFWorkbook workbook = new XSSFWorkbook(targetStream);
//        XSSFSheet masterDataSheet = workbook.getSheet("MasterData");
//        List<String> actualUnits = new ArrayList();
//        List<String> actualLocations = new ArrayList();
//
//        Iterator<Row> rowIterator = masterDataSheet.iterator();
//        while(rowIterator.hasNext()) {
//            Row row = rowIterator.next();
//            if (row.getCell(0) != null) {
//                actualUnits.add(row.getCell(0).getStringCellValue());
//            }
//
//            if (row.getCell(1) != null) {
//                actualLocations.add(row.getCell(1).getStringCellValue());
//            }
//        }
//
//        assertEquals(units, actualUnits);
//        assertEquals(expectedLocations, actualLocations);
//        assertNull(workbook.getSheet("Quantity"));
//        assertNotNull(workbook.getSheet("Condition"));
//        assertNotNull(workbook.getSheet("MasterData"));
//    }

    private ByteArrayResource getTemplateFile() {
        String path = "test/Template_Upload_Item_Test.xlsx";
        URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException("file not found!");
        } else {
            File file = new File(resource.getFile());
            byte[] data;
            try {
                data = Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return new ByteArrayResource(data);
        }
    }
}