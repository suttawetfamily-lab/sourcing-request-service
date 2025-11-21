package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.microsoft.schemas.office.visio.x2012.main.PageType;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.dto.category.CategoryDto;
import com.pantavanij.sourcingreq.services.domain.dto.requester.RequesterRequestDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.response.UserDetailResponse;
import com.pantavanij.sourcingreq.services.enums.SourcingStatus;
import com.pantavanij.sourcingreq.services.enums.SourcingType;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.PurposeRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormatSymbols;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.RequestType.REQUEST_TYPE_CONDITION;
import static com.pantavanij.sourcingreq.services.enums.RequestType.REQUEST_TYPE_QUANTITY;
import static com.pantavanij.sourcingreq.services.enums.SourcingStatus.SOURCING_NO_SUPPLIER_SELECTED;
import static com.pantavanij.sourcingreq.services.enums.SourcingStatus.SOURCING_QUALIFIED_SUPPLIER;
import static com.pantavanij.sourcingreq.services.enums.SourcingType.SOURCING_TYPE_ERFX;
import static com.pantavanij.sourcingreq.services.enums.SourcingType.SOURCING_TYPE_EXISTING_PRICE;

@Service
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {

    private static final String PATH_REPORT_EXCEL_SOURCINGREQ_TEMPLATE = "sourcing-request";
//    private static final String PATH_REPORT_EXCEL_SOURCINGREQ_TEMPLATE2 = "sourcing-request item";

    private static final String FILE_EXTENSION = ".xlsx";
    private static final String FONT_FREESIAL_UPC = "FreesiaUPC";
    private static final String FONT_CALIBRI_BODY = "Calibri (Body)";
    private static final String DECIMAL_2PLACES_FORMAT = "###,###,###,##0.00";
    private static final String DECIMAL_4PLACES_FORMAT = "###,###,###,##0.0000";
    private static final String NUMERIC_FORMAT = "###,###,##0";
    private final int ROW_DATA_LAST_AT_INDEX = 200;
    private final int ROW_DATA_HEADED_AT_INDEX = 1;
    private final int ROW_DATA_FIRST_AT_INDEX = 2;

    private static final String TEXT_FORMAT = "@";
    // Report Sheet
    private static final String REQUEST_REPORT_SHEET_SOURCINGREQ = "SOURCINGREQ";
    private static final String REQUEST_ITEM_SHEET2_TEMPLATE = "MasterData";
    private static final String REQUEST_ITEM_SHEET3_TEMPLATE = "Example";

    private final UaaService uaaService;
    private final TenantService tenantService;
    private final TenantSectionService tenantSectionService;
    private final UnitService unitService;
    private final LocationService locationService;
    private final TenantSectionDetailService tenantSectionDetailService;
    private final PurposeService purposeService;
    private final CategoryService categoryService;
    private final CurrencyService currencyService;
    private final TenantSubCategoryService tenantSubCategoryService;
    private final TenantConfigService tenantConfigService;


    @Override
    public String generateRequestReport(List<RequesterRequestDto> requestList, Integer requestReportId, Integer organizationId) throws IOException {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        Font font = workbook.createFont();
        font.setFontName(FONT_CALIBRI_BODY);
        font.setFontHeightInPoints((short) 12);

        List<CellStyle> headerCellStyles = generateTableHeaderCellStyles(workbook, font);
        List<CellStyle> bodyCellStyles = generateTableBodyCellStyles(workbook, font);

        SXSSFSheet sheet = workbook.createSheet(REQUEST_REPORT_SHEET_SOURCINGREQ);

        ExcelReportDto excelReport = ExcelReportDto.builder()
                .rowIndex(0)
                .sheet(sheet)
                .build();
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());

        List<TenantSectionDetailDto> headerList = tenantSectionDetailService.getExportRequestRpt(
                tenant.getRecId(), requestReportId, organizationId
        );

        generateHeaderRequestReport(excelReport, headerCellStyles, headerList);
        generateBodyRequestReport(excelReport, bodyCellStyles, requestList, headerList);

        int newColumnWidth = 25;
        for (EXCEL_HEADER_MAPPING_INDEX index : EXCEL_HEADER_MAPPING_INDEX.values()) {
            int columnIndex = index.getValue();
            setColumnWidth(sheet, columnIndex, newColumnWidth);
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmm");
        String dateString = format.format(DateTimeUtil.getTimestampByTimeZone(DateTimeUtil.getTimestamp(), getTimeZoneUser()));
        String outputFileName = String.format("%s-%s%s", PATH_REPORT_EXCEL_SOURCINGREQ_TEMPLATE, dateString, FILE_EXTENSION);

        FileOutputStream fileOut = new FileOutputStream(outputFileName);
        workbook.write(fileOut);
        fileOut.flush();
        fileOut.close();

        return outputFileName;
    }


    @Override
    public String generateRequestItemReport(List<ExcelRequestItemDto> excelDataList, String pathURL, Integer requestItemReportId, Integer organizationId) throws IOException, ParseException {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        Font font = workbook.createFont();
        font.setFontName(FONT_CALIBRI_BODY);
        font.setFontHeightInPoints((short) 12);

        List<CellStyle> headerCellStyles = generateTableHeaderCellStyles(workbook, font);
        List<CellStyle> bodyCellStyles = generateTableBodyCellStyles(workbook, font);

        SXSSFSheet sheet = workbook.createSheet(REQUEST_REPORT_SHEET_SOURCINGREQ);

        ExcelReportDto excelReport = ExcelReportDto.builder()
                .rowIndex(0)
                .sheet(sheet)
                .build();
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());

        List<TenantSectionDetailDto> headerList = tenantSectionDetailService.getExportRequestItemRpt(
                tenant.getRecId(), requestItemReportId, organizationId
        );

        generateHeaderRequestItemReport(excelReport, headerCellStyles, headerList);
        generateBodyRequestItemReport(excelReport, bodyCellStyles, excelDataList, headerList, pathURL);

        int newColumnWidth = 35;
        for (EXCEL_HEADER_MAPPING_INDEX index : EXCEL_HEADER_MAPPING_INDEX.values()) {
            int columnIndex = index.getValue();
            setColumnWidth(sheet, columnIndex, newColumnWidth);
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmm");
        String dateString = format.format(DateTimeUtil.getTimestampByTimeZone(DateTimeUtil.getTimestamp(), getTimeZoneUser()));
        String sourcingRequestItemReportName = tenantConfigService.getRequestItemReportName(tenant.getRecId());
        String outputFileName = String.format("%s-%s%s", sourcingRequestItemReportName, dateString, FILE_EXTENSION);

        FileOutputStream fileOut = new FileOutputStream(outputFileName);
        workbook.write(fileOut);
        fileOut.flush();
        fileOut.close();

        return outputFileName;
    }

    public String generateRequestItemTemplate(Integer requestTypeId, Integer type, Integer organizationId) throws IOException, ParseException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Font font = workbook.createFont();
        font.setFontName(FONT_CALIBRI_BODY);
        font.setFontHeightInPoints((short) 12);

        List<CellStyle> headerCellStyles = generateTableHeaderCellStyles(workbook, font);
        String sheet1 = requestTypeId == REQUEST_TYPE_QUANTITY.id() ? REQUEST_TYPE_QUANTITY.code() : REQUEST_TYPE_CONDITION.code();
        XSSFSheet sheet = workbook.createSheet(sheet1);
        ExcelReportTemplateDto excelReport = ExcelReportTemplateDto.builder()
                .rowIndex(0)
                .sheet(sheet)
                .build();

        List<TenantSectionDto> tenantSectionDtoList = tenantSectionService.getRequestItemFieldsForTemplateExcel(
                requestTypeId, organizationId
        );

        generateHeaderRequestItemTemplate(excelReport, headerCellStyles, sheet1, tenantSectionDtoList, type, organizationId);

        String outputFileName = "Template_Upload_Item.xlsx";
        try (FileOutputStream fileOut = new FileOutputStream(outputFileName)) {
            workbook.write(fileOut);
        }
        return outputFileName;
    }


    public void populateColumnData(Sheet sheet, int columnIndex, String[] data) {
        for (int i = 0; i < data.length; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                row = sheet.createRow(i);
            }
            Cell cell = row.createCell(columnIndex);
            cell.setCellValue(data[i]);
        }
    }

    private void populateDateFormatAndValidateCellStyle(Sheet sheet, List<Integer> columnIndex, int maximumRecord, CellStyle cellStyle) throws ParseException {
        for (int i = 1; i <= maximumRecord; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                row = sheet.createRow(i);
            }
            for (int idx : columnIndex) {
                Cell cell = row.createCell(idx);
                cell.setCellStyle(cellStyle);
//                SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
//                DataValidationHelper dvHelper = sheet.getDataValidationHelper();
//                DataValidationConstraint dvConstraint = dvHelper.createDateConstraint(DataValidationConstraint.OperatorType.BETWEEN,
//                        "" + DateUtil.getExcelDate(sdf.parse("01/01/2000")), "" + DateUtil.getExcelDate(sdf.parse("31/12/2100")), "dd/mm/yyyy");
//                CellRangeAddressList addressList = new CellRangeAddressList(ROW_DATA_HEADED_AT_INDEX, ROW_DATA_LAST_AT_INDEX, idx, idx);
//                DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
//                validation.createErrorBox("Invalid date format", "Please input date as \"DD/MM/YYYY/\" ex. 21/02/2024");
//                validation.setShowErrorBox(true);
//                sheet.addValidationData(validation);
            }
        }

    }

    public void addDropdownValidationToColumn(Sheet sheet, Sheet sheet2, int columnIndex, int i, String[] values) {
        populateColumnData(sheet2, columnIndex, values);
        String formula = sheet2.getSheetName() + "!$" + CellReference.convertNumToColString(columnIndex) + "$1:$" + CellReference.convertNumToColString(columnIndex) + "$" + values.length;
        DataValidationHelper dvHelper = sheet.getDataValidationHelper();
        DataValidationConstraint dvConstraint = dvHelper.createFormulaListConstraint(formula);
        CellRangeAddressList addressList = new CellRangeAddressList(ROW_DATA_HEADED_AT_INDEX, ROW_DATA_LAST_AT_INDEX, i, i);
        DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
        sheet.addValidationData(validation);
    }

    public void setFormulaCellValue(Sheet sheet, int columnLocation, int column, int field, int length, int index) {
        String formula = REQUEST_ITEM_SHEET2_TEMPLATE + "!$" + CellReference.convertNumToColString(column) +
                "$" + ROW_DATA_HEADED_AT_INDEX + ":$" + CellReference.convertNumToColString(column) + "$" + length;
        String formula2 = REQUEST_ITEM_SHEET2_TEMPLATE + "!$" + CellReference.convertNumToColString(columnLocation) +
                "$" + ROW_DATA_HEADED_AT_INDEX + ":$" + CellReference.convertNumToColString(columnLocation) + "$" + length;

        for (int rowIndex = ROW_DATA_HEADED_AT_INDEX; rowIndex <= ROW_DATA_LAST_AT_INDEX; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                row = sheet.createRow(rowIndex);
            }
            Cell cell = row.createCell(index);
            String excelFormula = "IFERROR(INDEX(" + formula + ", MATCH($" + CellReference.convertNumToColString(field) + "$" + (rowIndex + 1) + ", " + formula2 + ", 0)), \"\")";
            cell.setCellFormula(excelFormula);
        }
    }

    public void generateHeaderRequestItemTemplate(ExcelReportTemplateDto excelReport, List<CellStyle> headerCellStyles, String sheet1, List<TenantSectionDto> tenantSectionList, Integer type, Integer organizationId) throws ParseException {
        if (tenantSectionList != null && !tenantSectionList.isEmpty() && tenantSectionList.get(0).getFields() != null) {
            int columnIndex = 0;
            Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
            Sheet inputDataSheet = excelReport.getSheet().getWorkbook().getSheet(sheet1);
            XSSFSheet exampleSheet = excelReport.getSheet().getWorkbook().createSheet(REQUEST_ITEM_SHEET3_TEMPLATE);
            Sheet masterDataSheet = excelReport.getSheet().getWorkbook().createSheet(REQUEST_ITEM_SHEET2_TEMPLATE);
            Row r = excelReport.getRowByIndex(0);
            Row exampleHeaderRow = exampleSheet.createRow(0);
            Row exampleDataRow = exampleSheet.createRow(1);
            List<TenantSectionDetailDto> fields = tenantSectionList.get(0).getFields();
            List<CategoryDto> categoryDtoList = null;
            List<Integer> dateColumnIndex = new ArrayList<>();
            int maximumRecord = tenantConfigService.getMaximumUploadItem(tenant.getRecId());

//            exampleSheet.trackAllColumnsForAutoSizing();
//            excelReport.getSheet().trackAllColumnsForAutoSizing();

            for (int i = 0; i < fields.size(); i++) {
                String headerName =  fields.get(i).getLabel();
                String headerDisplayName = fields.get(i).getTenantSectionDetailValidatorList().size() == 0 ? fields.get(i).getLabel() + " (Optional)" : fields.get(i).getLabel();
                Cell c = r.createCell(i, CellType.STRING);
                c.setCellValue(headerDisplayName);
                c.setCellStyle(headerCellStyles.get(TABLE_HEADER_CELL_STYLES_INDEX.HEADER_LOCKED.getValue()));

                Cell exCellHeader = exampleHeaderRow.createCell(i, CellType.STRING);
                exCellHeader.setCellValue(headerDisplayName);
                exCellHeader.setCellStyle(headerCellStyles.get(TABLE_HEADER_CELL_STYLES_INDEX.HEADER_LOCKED.getValue()));
                exampleSheet.autoSizeColumn(i);

                String fieldName = fields.get(i).getFieldName();
                Cell exCellData;
                if (fieldName.equals("quantity")) {
                    exCellData = exampleDataRow.createCell(i, CellType.NUMERIC);
                    if (fields.get(i).getReportExampleData() != null) {
                        exCellData.setCellValue(Double.parseDouble(fields.get(i).getReportExampleData()));
                    }
                    excelReport.getSheet().autoSizeColumn(i);
                } else {
                    exCellData = exampleDataRow.createCell(i, CellType.STRING);
                    exCellData.setCellValue(fields.get(i).getReportExampleData());
                    excelReport.getSheet().autoSizeColumn(i);
                }

                // add drop-down list to "Unit" column header cell
                if ("Unit".equals(headerName)) {
                    List<UnitDto> unitDtoList = unitService.getUnitByTenantIdV1(tenant.getRecId(), organizationId);
                    String[] units = unitDtoList.stream().map(UnitDto::getUnitCode).toArray(String[]::new);
                    addDropdownValidationToColumn(inputDataSheet, masterDataSheet, columnIndex++, i, units);
                } else if ("Bid Validity Start Date".equalsIgnoreCase(headerName) || "Bid Validity End Date".equalsIgnoreCase(headerName)) {
                    dateColumnIndex.add(i);
                } else if ("Delivery Location".equals(headerName)) {
                    final int columnLocation = columnIndex;
                    final int field = i;
                    List<LocationDto> locationDtoList = locationService.getLocationByTenantId(tenant.getRecId(), organizationId);
                    String[] locations = locationDtoList.stream().map(LocationDto::getName).toArray(String[]::new);
                    String[] contactNames = locationDtoList.stream()
                            .map(LocationDto::getContactName)
                            .toArray(String[]::new);
                    String[] contactPhones = locationDtoList.stream()
                            .map(LocationDto::getPhone)
                            .toArray(String[]::new);
                    String[] addresses = locationDtoList.stream()
                            .map(LocationDto::getAddress)
                            .toArray(String[]::new);
                    addDropdownValidationToColumn(inputDataSheet, masterDataSheet, columnIndex++, i++, locations);
                    c = r.createCell(i, CellType.STRING);
//                    String locationHeader = fields.get(i).getTenantSectionDetailValidatorList().size() == 0 ? fields.get(i).getLabel() + " (Optional)" : fields.get(i).getLabel();
                    c.setCellValue(fields.get(i).getLabel() + " (Optional)");
                    c.setCellStyle(headerCellStyles.get(TABLE_HEADER_CELL_STYLES_INDEX.HEADER_LOCKED.getValue()));
                    excelReport.getSheet().autoSizeColumn(i);

                    addDropdownValidationToColumn(inputDataSheet, masterDataSheet, columnIndex, i, addresses);
                    setFormulaCellValue(inputDataSheet, columnLocation, columnIndex++, field, addresses.length, i++);

                    c = r.createCell(i, CellType.STRING);
//                    String contactNameHeader = fields.get(i).getTenantSectionDetailValidatorList().size() == 0 ? fields.get(i).getLabel() + " (Optional)" : fields.get(i).getLabel();
                    c.setCellValue(fields.get(i).getLabel() + " (Optional)");
                    c.setCellStyle(headerCellStyles.get(TABLE_HEADER_CELL_STYLES_INDEX.HEADER_LOCKED.getValue()));
                    excelReport.getSheet().autoSizeColumn(i);

                    addDropdownValidationToColumn(inputDataSheet, masterDataSheet, columnIndex, i, contactNames);
                    setFormulaCellValue(inputDataSheet, columnLocation, columnIndex++, field, contactNames.length, i++);

                    c = r.createCell(i, CellType.STRING);
//                    String contactPhoneHeader = fields.get(i).getTenantSectionDetailValidatorList().size() == 0 ? fields.get(i).getLabel() + " (Optional)" : fields.get(i).getLabel();
                    c.setCellValue(fields.get(i).getLabel() + " (Optional)");
                    c.setCellStyle(headerCellStyles.get(TABLE_HEADER_CELL_STYLES_INDEX.HEADER_LOCKED.getValue()));
                    excelReport.getSheet().autoSizeColumn(i);

                    addDropdownValidationToColumn(inputDataSheet, masterDataSheet, columnIndex, i, contactPhones);
                    setFormulaCellValue(inputDataSheet, columnLocation, columnIndex++, field, contactPhones.length, i++);

                } else if ("Purpose of Request".equals(headerName)) {
                    if (fields.get(i).getFieldName().equals("purposeObj")) {
                        List<PurposeDto> purposeDtoList = purposeService.getPurposeByTenantId(tenant.getRecId());
                        String[] purposrs = purposeDtoList.stream().map(PurposeDto::getName).toArray(String[]::new);
                        addDropdownValidationToColumn(inputDataSheet, masterDataSheet, columnIndex++, i, purposrs);
                    }
                } else if ("Category".equals(headerName)) {
                    categoryDtoList = categoryService.getCategoryByTenant(tenant.getRecId());
                    String[] category = categoryDtoList.stream().map(CategoryDto::getName).toArray(String[]::new);
                    addDropdownValidationToColumn(inputDataSheet, masterDataSheet, columnIndex++, i, category);
                } else if ("Subcategory".equals(headerName)) {
                    List<TenantSubCategoryDto> tenantSubCategoryList = tenantSubCategoryService.getSubCategoryByTenantIdAndType(tenant.getRecId(), type);
                    String[] subCategories = tenantSubCategoryList.stream()
                            .sorted(Comparator.comparing(TenantSubCategoryDto::getName,
                                    Comparator.nullsFirst(Comparator.naturalOrder())))
                            .map(TenantSubCategoryDto::getName)
                            .toArray(String[]::new);
                    addDropdownValidationToColumn(inputDataSheet, masterDataSheet, columnIndex++, i, subCategories);

                    /*
                    List<SubCategoryDto> subCategoryDtoList = subCategoryService.getSubCategoryByTenantId(tenant.getRecId());
                    String formula = "";
                    String lastFormula = "F2:F201";
                    for (CategoryDto categoryDto : categoryList) {
                        String[] subCategorys = subCategoryDtoList.stream().filter(subCategoryDto -> categoryDto.getRecId().equals(subCategoryDto.getCategoryId())).map(SubCategoryDto::getName).toArray(String[]::new);
                        formula += "IF( $" + CellReference.convertNumToColString(i - 1) + "2 = \"" + categoryDto.getName() + "\" , " + REQUEST_ITEM_SHEET2_TEMPLATE + "!$" + CellReference.convertNumToColString(columnIndex) +
                                "$" + ROW_DATA_HEADED_AT_INDEX + ":$" + CellReference.convertNumToColString(columnIndex) + "$" + subCategorys.length + " , ";
                        populateColumnData(sheet2, columnIndex++, subCategorys);
                        lastFormula += ")";
                    }
                    DataValidationHelper dvHelper = sheet.getDataValidationHelper();
                    DataValidationConstraint dvConstraint = dvHelper.createFormulaListConstraint(formula + lastFormula);
                    CellRangeAddressList addressList = new CellRangeAddressList(ROW_DATA_HEADED_AT_INDEX, ROW_DATA_LAST_AT_INDEX, i, i);
                    DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
                    sheet.addValidationData(validation);
                     */
                } else if ("Quantity".equals(headerName)) {
                    DataValidation validation = getNumberDataValidation(inputDataSheet, i);
                    inputDataSheet.addValidationData(validation);
                }else if("Item Budget".equals(headerName)){
                    DataValidation validation = getNumberDataValidation(inputDataSheet, i);
                    inputDataSheet.addValidationData(validation);

                    List<OptionDto> currencyDtoList = currencyService.getCurrencyByTenantId(tenant.getRecId());
                    String[] currency = currencyDtoList.stream()
                            .map(OptionDto::getLabel)
                            .toArray(String[]::new);
                    c = r.createCell(++i, CellType.STRING);
                    c.setCellValue("Currency (Optional)");
                    c.setCellStyle(headerCellStyles.get(TABLE_HEADER_CELL_STYLES_INDEX.HEADER_LOCKED.getValue()));
                    excelReport.getSheet().autoSizeColumn(i);

                    exCellHeader = exampleHeaderRow.createCell(i, CellType.STRING);
                    exCellHeader.setCellValue("Currency (Optional)");
                    exCellHeader.setCellStyle(headerCellStyles.get(TABLE_HEADER_CELL_STYLES_INDEX.HEADER_LOCKED.getValue()));
                    exCellData = exampleDataRow.createCell(i, CellType.STRING);
                    exCellData.setCellValue("THB");
                    exampleSheet.autoSizeColumn(i);

                    addDropdownValidationToColumn(inputDataSheet, masterDataSheet, columnIndex, i, currency);
                }
            }
            if (dateColumnIndex.size() > 0) {
                populateDateFormatAndValidateCellStyle(inputDataSheet, dateColumnIndex, maximumRecord, headerCellStyles.get(3));
            }
        }
    }

    private DataValidation getNumberDataValidation(Sheet inputDataSheet, int i) {
        DataValidationHelper dvHelper = inputDataSheet.getDataValidationHelper();
        // Define the decimal format validation
        DataValidationConstraint dvConstraint = dvHelper.createDecimalConstraint(
                DataValidationConstraint.OperatorType.BETWEEN,
                "0.0001", // Minimum value
                "999999999999999"   // Maximum value
        );
        CellRangeAddressList addressList = new CellRangeAddressList(ROW_DATA_HEADED_AT_INDEX, ROW_DATA_LAST_AT_INDEX, i, i);
        DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
        validation.setShowErrorBox(true);
        return validation;
    }

    private void generateHeaderRequestReport(ExcelReportDto excelReport, List<CellStyle> headerCellStyles, List<TenantSectionDetailDto> headerList) {
        Row r = excelReport.getRowByIndex(0);
        int i = 0;
        for (TenantSectionDetailDto header : headerList) {
            String headerName = header.getReportLabel();
            Cell c = r.createCell(i++, CellType.STRING);
            c.setCellValue(headerName);
            c.setCellStyle(headerCellStyles.get(TABLE_HEADER_CELL_STYLES_INDEX.HEADER_LOCKED.getValue()));
        }
    }

    private void generateHeaderRequestItemReport(ExcelReportDto excelReport, List<CellStyle> headerCellStyles, List<TenantSectionDetailDto> headerList) {
        Row r = excelReport.getRowByIndex(0);
        int i = 0;
        for (TenantSectionDetailDto header : headerList) {
            String headerName = header.getReportLabel();
            Cell c = r.createCell(i++, CellType.STRING);
            c.setCellValue(headerName);
            c.setCellStyle(headerCellStyles.get(TABLE_HEADER_CELL_STYLES_INDEX.HEADER_LOCKED.getValue()));
        }
    }


    private OrganizationClientDto organizationClientDto = new OrganizationClientDto();

    private void generateBodyRequestReport(ExcelReportDto excelReport, List<CellStyle> bodyCellStyles, List<RequesterRequestDto> retuestList, List<TenantSectionDetailDto> headedList) {
        if (!CollectionUtils.isEmpty(retuestList)) {
            String tenantId = AppUtil.getTenantId();
            String idp = AppUtil.getIdp();
            String username = AppUtil.getUserName();
            organizationClientDto =
                    uaaService.getOrganizationByTenantIdAndUserName(tenantId, idp, username);
            for (RequesterRequestDto requestDto : retuestList) {
                int index = 0;
                Row row = excelReport.nextAndGetRow();
                for (TenantSectionDetailDto headed : headedList) {
                    setRequesterRequestDtoCellValues(requestDto, index++, bodyCellStyles, row, headed.getName());
                }

            }
        }
    }

    private static final String DEFAULT_NULL_VALUE = "-";
    private final PurposeRepository purposeRepository;

    private void setRequesterRequestDtoCellValues(RequesterRequestDto requestDto, int index, List<CellStyle> bodyCellStyles, Row row, String header) {
        Cell cell = row.createCell(index, CellType.STRING);
        switch (header) {
            case "requestNo":
                String requestNo = requestDto.getRequestNo() != null ? requestDto.getRequestNo() : DEFAULT_NULL_VALUE;
                cell.setCellValue(requestNo);
                break;
            case "requestName":
                String requestName = requestDto.getRequestName() != null ? requestDto.getRequestName() : DEFAULT_NULL_VALUE;
                cell.setCellValue(requestName);
                break;
            case "requestTypeId":
                String requestTypeName = "-";
                if (requestDto.getRequestTypeId() != null) {
                    if (requestDto.getRequestTypeId() == REQUEST_TYPE_QUANTITY.id()) {
                        requestTypeName = REQUEST_TYPE_QUANTITY.code();
                    } else if (requestDto.getRequestTypeId() == REQUEST_TYPE_CONDITION.id()) {
                        requestTypeName = REQUEST_TYPE_CONDITION.code();
                    }
                }
                cell.setCellValue(requestTypeName);
                break;
            case "project":
            case "projectCode":
                String projectNameOrDepartmentName = "-";
                if (requestDto.getProjectObj() != null) {
                    projectNameOrDepartmentName = requestDto.getProjectObj().getLabel() != null
                            ? requestDto.getProjectObj().getLabel()
                            : DEFAULT_NULL_VALUE;
                } else if (requestDto.getDepartmentObj() != null) {
                    projectNameOrDepartmentName = requestDto.getDepartmentObj().getLabel() != null
                            ? requestDto.getDepartmentObj().getLabel()
                            : DEFAULT_NULL_VALUE;
                } else {
                    projectNameOrDepartmentName = requestDto.getProjectName() == null ? "-" : requestDto.getProjectName();
                }
                cell.setCellValue(projectNameOrDepartmentName);
                break;
            case "organizationId":
                String organizationName = organizationClientDto.getBorgUserList().stream()
                        .filter(borgUser -> String.valueOf(borgUser.getBorgID()).equals(requestDto.getOrganizationId()))
                        .findFirst()
                        .map(BorgUserAdditionalDto::getBorgName)
                        .orElse(DEFAULT_NULL_VALUE);
                cell.setCellValue(organizationName);
                break;
            case "createdBy":
                String requesterName = requestDto.getCreatedByName() != null ? requestDto.getCreatedByName() : DEFAULT_NULL_VALUE;
                cell.setCellValue(requesterName);
                break;
            case "purchaser":
            case "assignedBy":
                String purchaserName = requestDto.getAssignedByName() != null ? requestDto.getAssignedByName() : DEFAULT_NULL_VALUE;
                cell.setCellValue(purchaserName);
                break;
            case "createdDate":
                String createdDate = requestDto.getCreatedDate() != null ? new SimpleDateFormat("dd/MM/yyyy").format(requestDto.getCreatedDate()) : DEFAULT_NULL_VALUE;
                cell.setCellValue(createdDate);
                break;
            case "expectedDate":
                String expectedDate = requestDto.getExpectedDate() != null ? new SimpleDateFormat("dd/MM/yyyy").format(requestDto.getExpectedDate()) : DEFAULT_NULL_VALUE;
                cell.setCellValue(expectedDate);
                break;
            case "requestDate":
                String requestedDate = requestDto.getRequestDate() != null ? new SimpleDateFormat("dd/MM/yyyy").format(requestDto.getRequestDate()) : DEFAULT_NULL_VALUE;
                cell.setCellValue(requestedDate);
                break;
            case "requestStatus":
                String requestStatus = requestDto.getRequestStatus() == null ? DEFAULT_NULL_VALUE : requestDto.getRequestStatus().getName();
                cell.setCellValue(requestStatus);
                break;
            case "updatedDate":
                String updatedDate = requestDto.getUpdatedDate() != null ? new SimpleDateFormat("dd/MM/yyyy").format(requestDto.getUpdatedDate()) : DEFAULT_NULL_VALUE;
                cell.setCellValue(updatedDate);
                break;
            case "type":
                String type = requestDto.getTypeObj() != null ? requestDto.getTypeObj().getLabel() : DEFAULT_NULL_VALUE;
                cell.setCellValue(type);
                break;
            default:
                cell.setCellValue(DEFAULT_NULL_VALUE);
                break;
        }
        cell.setCellStyle(bodyCellStyles.get(TABLE_BODY_CELL_STYLES_INDEX.CENTER_WARP_TEXE_CELL.getValue()));
    }

    private void generateBodyRequestItemReport(
            ExcelReportDto excelReport,
            List<CellStyle> bodyCellStyles,
            List<ExcelRequestItemDto> excelDataList,
            List<TenantSectionDetailDto> headerList,
            String pathURL
    ) throws ParseException {
        if (!CollectionUtils.isEmpty(excelDataList)) {

            String tenantId = AppUtil.getTenantId();
            String idp = AppUtil.getIdp();
            String username = AppUtil.getUserName();
            organizationClientDto = uaaService.getOrganizationByTenantIdAndUserName(tenantId, idp, username);
            Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
            SourcingRequestDisplayDto sourcingRequestDisplayDto = tenantConfigService.getDisplayConfiguration(tenant);

            Map<String, String> createdUser = new HashMap<>();
            List<String> createdByList = excelDataList.stream().map(ExcelRequestItemDto::getCreatedBy).distinct().collect(Collectors.toList());
            for (String createdBy : createdByList) {
                if (createdBy != null && !createdBy.trim().equals("")) {
                    createdUser.put(createdBy, UserDetailServiceUtil.getFullName(createdBy));
                }
            }

            Map<String, String> assignedUser = new HashMap<>();
            List<String> assignedByList = excelDataList.stream().map(ExcelRequestItemDto::getAssignedBy).distinct().collect(Collectors.toList());
            for (String assignedBy : assignedByList) {
                if (assignedBy != null && !assignedBy.trim().equals("")) {
                    assignedUser.put(assignedBy, UserDetailServiceUtil.getFullName(assignedBy));
                }
            }

            Map<String, String> delegatedUser = new HashMap<>();
            List<String> delegateActionByList = excelDataList.stream().map(ExcelRequestItemDto::getDelegateActionBy).distinct().collect(Collectors.toList());
            for (String delegateActionBy : delegateActionByList) {
                if (delegateActionBy != null && !delegateActionBy.trim().equals("")) {
                    delegatedUser.put(delegateActionBy, UserDetailServiceUtil.getFullName(delegateActionBy));
                }
            }

            //Sort by RequestNo, RequestItemId
            excelDataList.sort(Comparator.comparing(ExcelRequestItemDto::getRequestNo)
                    //TODO: Add new Sorting => SourcingType sequence
                    .thenComparing(ExcelRequestItemDto::getSourcingDocNo, Comparator.nullsFirst(Comparator.naturalOrder()))
                    .thenComparing(ExcelRequestItemDto::getSourcingItemSequence, Comparator.nullsFirst(Comparator.naturalOrder()))
                    .thenComparing(ExcelRequestItemDto::getItemSequence, Comparator.nullsFirst(Comparator.naturalOrder())));

            if (StringUtils.isNotBlank(pathURL)){
                if (pathURL.equalsIgnoreCase("request-item-report")) {
                    // Get request item report column.
                } else if (pathURL.equalsIgnoreCase("bid-log-report")) {
                    // Get bid log report column.
                }
            }

            for (ExcelRequestItemDto excelRequestItem : excelDataList) {
                int index = 0;
                Row row = excelReport.nextAndGetRow();
                for (TenantSectionDetailDto header : headerList) {
                    setRequestItemCellValues(excelRequestItem, index++, bodyCellStyles, row, header.getName(), assignedUser, createdUser, delegatedUser, sourcingRequestDisplayDto);
                }
            }
        }
    }

    private static String unitPrice = "-";

    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        try {
            str = str.replaceAll(",", "");
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String formatProjectString(String format, String projectCode, String projectName) {
        if (format == null || format.isEmpty()) {
            throw new IllegalArgumentException("Format string cannot be null or empty");
        }

        if(projectName != null && projectCode != null && !projectCode.isEmpty() && !projectName.isEmpty()) {
            return format.replace("projectCode", projectCode)
                    .replace("projectName", projectName);
        } else {
            String safeProjectCode = (projectCode == null || projectCode.isEmpty()) ? "" : projectCode;
            String safeProjectName = (projectName == null || projectName.isEmpty()) ? "" : projectName;
            return !safeProjectCode.isEmpty() ? safeProjectCode : safeProjectName;
        }

    }

    private void setRequestItemCellValues(ExcelRequestItemDto excelRequestItem, int index, List<CellStyle> bodyCellStyles, Row row, String header, Map<String, String> assignedUser, Map<String, String> createdUser, Map<String, String> delegatedUser, SourcingRequestDisplayDto sourcingRequestDisplayDto) throws ParseException {
        Cell cell = row.createCell(index, CellType.STRING);
        String[] shortMonths = new DateFormatSymbols().getShortMonths();
        switch(header) {
            case "requestNo" :
                String requestNo = excelRequestItem.getRequestNo();
                cell.setCellValue(requestNo);
                break;

            case "requestTypeId" :
                String requestType = excelRequestItem.getRequestTypeId() == 1 ? "Quantity" : "Condition";
                cell.setCellValue(requestType);
                break;

            case "requestName" :
                String requestName = excelRequestItem.getRequestName() != null ? excelRequestItem.getRequestName() : "-";
                cell.setCellValue(requestName);
                break;

            case "project" :
                String project = "-";

                String projectFormat = sourcingRequestDisplayDto.getReportProjectFormat();
                if(!excelRequestItem.getTypeCode().equals("DEPARTMENT") && projectFormat != null) {
                    project = formatProjectString(projectFormat,excelRequestItem.getProjectCode(),excelRequestItem.getProjectName());
                }
                cell.setCellValue(project);
                break;

            case "projectCode" :
                String projectCode = "-";
                if (excelRequestItem.getProjectCode() != null) {
                    projectCode = String.format("%s", excelRequestItem.getProjectCode());
                }
                cell.setCellValue(projectCode);
                break;
            case "projectName" :
                String projectName = "-";
                if (excelRequestItem.getProjectName() != null) {
                    projectName = String.format("%s", excelRequestItem.getProjectName());
                }
                cell.setCellValue(projectName);
                break;

            case "departmentName" :
                String departmentName = "-";
                if (excelRequestItem.getTypeCode() != null && !excelRequestItem.getTypeCode().isEmpty()) {
                    if (excelRequestItem.getTypeCode().equals("PROJECT") || excelRequestItem.getTypeCode().equals("DEPARTMENT")) {
                        if (excelRequestItem.getTypeCode().equals("DEPARTMENT") && excelRequestItem.getDepartmentName() != null) {
                            departmentName = String.format("%s", excelRequestItem.getDepartmentName());
                        } else if (!excelRequestItem.getTypeCode().equals("PROJECT") && excelRequestItem.getDepartmentName() != null) {
                            departmentName = String.format("%s", excelRequestItem.getDepartmentName());
                        }
                    } else {
                        if(excelRequestItem.getDepartmentName() != null){
                            departmentName = String.format("%s", excelRequestItem.getDepartmentName());
                        } else {
                            String tenantId = AppUtil.getTenantId();
                            String idp = AppUtil.getIdp();
                            ContractDetailClientDto contractDetailClientDto = uaaService.getContractDetail(tenantId, idp, excelRequestItem.getCreatedBy(), null);
                            if(contractDetailClientDto != null) {
                                departmentName = contractDetailClientDto.getDepartment();
                            }
                        }

                    }
                }
                cell.setCellValue(departmentName);
                break;

            case "requestAdditionalDepartment":
                String requestAdditionalDepartment = "-";
                if (excelRequestItem.getRequestAdditionalDepartment() != null) {
                    requestAdditionalDepartment = String.format("%s", excelRequestItem.getRequestAdditionalDepartment());
                }
                cell.setCellValue(requestAdditionalDepartment);
                break;

            case "projectOrDepartment":
                String projectNameOrDepartmentName = "-";
                if (excelRequestItem.getTypeCode() != null && !excelRequestItem.getTypeCode().isEmpty()) {
                    if (excelRequestItem.getTypeCode().equals("PROJECT") && excelRequestItem.getProjectCode() != null && excelRequestItem.getProjectName() != null) {
                        projectNameOrDepartmentName = String.format("%s-%s", excelRequestItem.getProjectCode(), excelRequestItem.getProjectName());

                    } else if (!excelRequestItem.getTypeCode().equals("DEPARTMENT") && excelRequestItem.getProjectCode() != null && excelRequestItem.getProjectName() != null) {
                        projectNameOrDepartmentName = String.format("%s-%s", excelRequestItem.getProjectCode(), excelRequestItem.getProjectName());

                    } else if (excelRequestItem.getTypeCode().equals("DEPARTMENT") && excelRequestItem.getDepartmentName() != null) {
                        projectNameOrDepartmentName = String.format("%s", excelRequestItem.getDepartmentName());
                    }
                }
                cell.setCellValue(projectNameOrDepartmentName);
                break;

            case "budgetRefNo" :
                String budgetRefNo = excelRequestItem.getBudgetRefNo() != null ? excelRequestItem.getBudgetRefNo() : "-";
                cell.setCellValue(budgetRefNo);
                break;

            case "budget" :
                String budget = "-";
                if (excelRequestItem.getBudget() != null && BigDecimal.ZERO.compareTo(excelRequestItem.getBudget()) != 0)
                    budget = String.format("%,.2f", excelRequestItem.getBudget());
                cell.setCellValue(budget);
                break;

            case "requestDate" :
            case "createdDate" :
                String requestedDate = excelRequestItem.getRequestDate() != null ? new SimpleDateFormat("dd/MM/yyyy").format(excelRequestItem.getRequestDate()) : "";
                cell.setCellValue(requestedDate);
                break;

            case "expectedDate" :
                String expectedDate = excelRequestItem.getExpectedDate() != null ? new SimpleDateFormat("dd/MM/yyyy").format(excelRequestItem.getExpectedDate()) : "";
                cell.setCellValue(expectedDate);
                break;

            case "ownerName" :
            case "createdBy" :
                String requesterName = !createdUser.get(excelRequestItem.getCreatedBy()).equals("") ? createdUser.get(excelRequestItem.getCreatedBy()) : excelRequestItem.getCreatedBy();
                cell.setCellValue(requesterName);
                break;

            case "StatusId" :
            case "requestRadio" :
                String requesterStatus = excelRequestItem.getRequestStatus() != null ? excelRequestItem.getRequestStatus() : "-";
                cell.setCellValue(requesterStatus.toUpperCase());
                break;

            case "sourcingTypeId" :
                String sourcingType = "-";
                if (!SourcingType.SOURCING_TYPE_DRAFT.id().equals(excelRequestItem.getSourcingTypeId()) && excelRequestItem.getSourcingTypeName() != null) {
                    sourcingType = excelRequestItem.getSourcingTypeName();
                }
                cell.setCellValue(sourcingType);
                break;

            case "eRFXNo" :
                String eRFXNo = excelRequestItem.getSourcingDocNo() != null ? excelRequestItem.getSourcingDocNo() : "-";
                cell.setCellValue(eRFXNo);
                break;

            case "purposeDescription" :
                String purpose = excelRequestItem.getPurposeDescription() != null ? excelRequestItem.getPurposeDescription() : "-";
                cell.setCellValue(purpose);
                break;

            case "itemName" :
            case "existingPriceItemName" :
                String ItemName = "-";
                if (excelRequestItem.getExistingPriceItemName() != null) {
                    ItemName = excelRequestItem.getExistingPriceItemName();
                } else if (excelRequestItem.getItemName() != null) {
                    ItemName = excelRequestItem.getItemName();
                }
                cell.setCellValue(ItemName);
                break;

            case "itemDescription" :
            case "existingPriceItemDescription" :
                String ItemDescription = "-";
                if (excelRequestItem.getExistingPriceItemDescription() != null) {
                    ItemDescription = excelRequestItem.getExistingPriceItemDescription();
                } else if (excelRequestItem.getItemDescription() != null) {
                    ItemDescription = excelRequestItem.getItemDescription();
                }
                cell.setCellValue(ItemDescription);
                break;

            case "brand" :
                String brand = excelRequestItem.getBrand() != null ? excelRequestItem.getBrand() : "-";
                cell.setCellStyle(bodyCellStyles.get(TABLE_BODY_CELL_STYLES_INDEX.CENTER_WARP_TEXE_CELL.getValue()));
                cell.setCellValue(brand);
                cell.setCellType(CellType.STRING);
                break;

            case "partNo" :
                String partNo = excelRequestItem.getPartNo() != null ? excelRequestItem.getPartNo() : "-";
                cell.setCellStyle(bodyCellStyles.get(TABLE_BODY_CELL_STYLES_INDEX.CENTER_WARP_TEXE_CELL.getValue()));
                cell.setCellValue(partNo);
                cell.setCellType(CellType.STRING);
                break;

            case "quantity" :
                if (excelRequestItem.getQuantity() != null) {
                    cell.setCellStyle(bodyCellStyles.get(TABLE_BODY_CELL_STYLES_INDEX.NUMERIC_2DP_CELL.getValue()));
                    if(excelRequestItem.getRequestTypeId() == REQUEST_TYPE_CONDITION.id()){
                        cell.setCellValue(1.0);
                    } else {
                        cell.setCellValue(excelRequestItem.getQuantity().doubleValue());
                    }
                    cell.setCellType(CellType.NUMERIC);
                }
                break;

            case "conditions" :
                String condition = "-";
                if (excelRequestItem.getConditions() != null && !excelRequestItem.getConditions().isEmpty()){
                    condition = excelRequestItem.getConditions();
                }
                cell.setCellValue(condition);
                break;

            case "quantityOrCondition" :
                if (excelRequestItem.getRequestTypeId() == 1) {
                    if (excelRequestItem.getQuantity() != null) {
                        cell.setCellStyle(bodyCellStyles.get(TABLE_BODY_CELL_STYLES_INDEX.NUMERIC_2DP_CELL.getValue()));
                        cell.setCellValue(excelRequestItem.getConditions() != null ? excelRequestItem.getConditions(): "-");
                        cell.setCellValue(excelRequestItem.getQuantity().doubleValue());
                        cell.setCellType(CellType.NUMERIC);

                    } else {
                        cell.setCellStyle(bodyCellStyles.get(TABLE_BODY_CELL_STYLES_INDEX.CENTER_WARP_TEXE_CELL.getValue()));
                        cell.setCellValue("-");
                    }

                } else {
                    cell.setCellValue(excelRequestItem.getConditions() != null ? excelRequestItem.getConditions(): "-");
                }
                break;

            case "unitCode" :
                String unit = "-";
                if (excelRequestItem.getEXUnitCode() != null) {
                    unit = excelRequestItem.getEXUnitCode();
                } else if (excelRequestItem.getUnitCode() != null) {
                    unit = excelRequestItem.getUnitCode();
                }
                cell.setCellValue(unit);
                break;

            case "itemBudget" :
                String itemBudget = "-";
                if (excelRequestItem.getItemBudget() != null) {
                    cell.setCellStyle(bodyCellStyles.get(TABLE_BODY_CELL_STYLES_INDEX.NUMERIC_2DP_CELL.getValue()));
                    cell.setCellValue(excelRequestItem.getItemBudget().doubleValue());
                    cell.setCellType(CellType.NUMERIC);
                } else {
                    cell.setCellValue(itemBudget);
                }
                break;

            case "estimatedUnitPrice":
            case "unitPrice" :
                boolean freeItem = (excelRequestItem.getSourcingStatusId() == SOURCING_QUALIFIED_SUPPLIER.id() ||
                        excelRequestItem.getSourcingStatusId() == SourcingStatus.SOURCING_AWAITING_APPROVE_SHORTLIST.id() ||
                        excelRequestItem.getSourcingStatusId() == SourcingStatus.SOURCING_NO_QUALIFIED_SUPPLIER.id());
                freeItem &= (excelRequestItem.getUnitPrice() != null && (excelRequestItem.getEXSupplierName() != null && (BigDecimal.ZERO.compareTo(excelRequestItem.getUnitPrice()) == 0)));

                if (excelRequestItem.getUnitPrice() != null && (excelRequestItem.getSourcingStatusId().equals(SOURCING_QUALIFIED_SUPPLIER.id()) ||
                        freeItem)) {
                    if (freeItem) {
                        unitPrice = "Free item";
                        cell.setCellValue(unitPrice);
                    } else if (BigDecimal.ZERO.compareTo(excelRequestItem.getUnitPrice()) != 0) {
                        unitPrice = String.format("%,.4f", excelRequestItem.getUnitPrice());
                        if (excelRequestItem.getUnitPrice() != null) {
                            cell.setCellStyle(bodyCellStyles.get(TABLE_BODY_CELL_STYLES_INDEX.NUMERIC_4DP_CELL.getValue()));
                            cell.setCellValue(excelRequestItem.getUnitPrice().doubleValue());
                            cell.setCellType(CellType.NUMERIC);
                        } else {
                            cell.setCellValue("-");
                        }
                    }
                } else {
                    cell.setCellValue("-");
                }
                break;

            case "currencyCode" :
                if(excelRequestItem.getSourcingStatusId().equals(SOURCING_QUALIFIED_SUPPLIER.id()) || excelRequestItem.getSourcingStatusId().equals(SOURCING_NO_SUPPLIER_SELECTED.id())){
                    String currencyName = "-";
                    if (excelRequestItem.getCurrencyCode() != null) {
                        currencyName = excelRequestItem.getCurrencyCode();
                    }
                    cell.setCellValue(currencyName);
                } else {
                    cell.setCellValue("-");
                }
                break;

            case "supplier" :
            case "supplierName" :
            case "eXSupplierName" :
                String supplierName = "-";
                if (excelRequestItem.getEXSupplierName() != null &&
                        (!unitPrice.equals("-"))) {
                    supplierName = excelRequestItem.getEXSupplierName();
                } else if (excelRequestItem.getSupplierName() != null &&
                        (!unitPrice.equals("-"))) {
                    supplierName = excelRequestItem.getSupplierName();
                }
                cell.setCellValue(supplierName);
                break;

            case "deliveryLocation" :
            case "locationName" :
            case "contactName" :
            case "phone" :
                String deliveryLocation = "-";
                if (excelRequestItem.getLocationName() != null && excelRequestItem.getLocationName().equalsIgnoreCase("other")) {
                    List<String> locationArr = new ArrayList<>();
                    if (StringUtils.isNotBlank(excelRequestItem.getDeliveryLocation())) {
                        locationArr.add(excelRequestItem.getDeliveryLocation());
                    }
                    if (StringUtils.isNotBlank(excelRequestItem.getContactName())) {
                        locationArr.add(excelRequestItem.getContactName());
                    }

                    if (StringUtils.isNotBlank(excelRequestItem.getPhone())) {
                        locationArr.add(excelRequestItem.getPhone());
                    }
                    deliveryLocation = String.join(",", locationArr);

                } else {
                    deliveryLocation = (excelRequestItem.getLocationName() != null &&
                            !excelRequestItem.getLocationName().isEmpty()) ? excelRequestItem.getLocationName(): "-";
                }
                cell.setCellValue(deliveryLocation);
                break;

            case "assignedBy" :
            case "buyerName" : // Recheck on buyer
                String purchaserName = "-";
                if(StringUtils.isNotEmpty(excelRequestItem.getDelegateActionBy())) {
                    purchaserName = !delegatedUser.get(excelRequestItem.getDelegateActionBy()).equals("") ? delegatedUser.get(excelRequestItem.getDelegateActionBy()) : excelRequestItem.getDelegateActionBy();
                } else if(StringUtils.isNotEmpty(excelRequestItem.getAssignedBy())) {
                    purchaserName = !assignedUser.get(excelRequestItem.getAssignedBy()).equals("") ? assignedUser.get(excelRequestItem.getAssignedBy()) : excelRequestItem.getAssignedBy();
                }
                cell.setCellValue(purchaserName);
                break;

            case "sourcingStatus" :
            case "sourcingStatusName" :
                String SouringStatus = "-";
                if (!SourcingStatus.SOURCING_NONE.id().equals(excelRequestItem.getSourcingStatusId()))
                    SouringStatus = excelRequestItem.getSourcingStatusName() != null ? excelRequestItem.getSourcingStatusName() : "-";
                cell.setCellValue(SouringStatus);
                break;

            case "sourcingDocNo" :
                String sourcingDocNo = excelRequestItem.getSourcingDocNo() != null ? excelRequestItem.getSourcingDocNo() : DEFAULT_NULL_VALUE;
                cell.setCellValue(sourcingDocNo);
                break;

            case "awardedValue" :
                if (excelRequestItem.getAwardedAmount() != null) {
                    String awardedValue = excelRequestItem.getAwardedAmount();
                    if(isNumeric(awardedValue)){
                        BigDecimal formattedValue = new BigDecimal(awardedValue);
                        cell.setCellStyle(bodyCellStyles.get(TABLE_BODY_CELL_STYLES_INDEX.NUMERIC_2DP_CELL.getValue()));
                        cell.setCellValue(formattedValue.doubleValue());
                        cell.setCellType(CellType.NUMERIC);
                    } else {
                        cell.setCellValue(excelRequestItem.getAwardedAmount());
                    }
                } else if (excelRequestItem.getAwardedQuantity() != null) {
                    cell.setCellStyle(bodyCellStyles.get(TABLE_BODY_CELL_STYLES_INDEX.NUMERIC_2DP_CELL.getValue()));
                    cell.setCellValue(excelRequestItem.getAwardedQuantity().doubleValue());
                    cell.setCellType(CellType.NUMERIC);
                } else {
                    cell.setCellValue("-");
                }
                break;

            case "awardedNetAmount" :
                if (excelRequestItem.getAwardedNetAmount() != null) {
                    cell.setCellStyle(bodyCellStyles.get(TABLE_BODY_CELL_STYLES_INDEX.NUMERIC_2DP_CELL.getValue()));
                    cell.setCellValue(excelRequestItem.getAwardedNetAmount().doubleValue());
                    cell.setCellType(CellType.NUMERIC);
                } else {
                    cell.setCellValue("-");
                }
                break;

            case "locationId" :
                break;

            case "eXUnitCode" :
                break;

            case "objective" :
            case "objectiveCode" :
            case "objectiveName" :
                String objectiveName = "-";
                if (excelRequestItem.getObjective() != null) {
                    objectiveName = String.format("%s", excelRequestItem.getObjective());
                } else if (excelRequestItem.getObjectiveCode() != null && excelRequestItem.getObjectiveName() != null) {
                    objectiveName = String.format("%s-%s", excelRequestItem.getObjectiveCode(), excelRequestItem.getObjectiveName());
                }
                cell.setCellValue(objectiveName);
                break;

            case "types" :
                String typeName = "-";
                if (excelRequestItem.getTypeCode() != null && excelRequestItem.getTypeName() != null) {
                    // typeName = String.format("%s-%s", excelRequestItem.getTypeCode(), excelRequestItem.getTypeName());
                    typeName = excelRequestItem.getTypeName();
                }
                cell.setCellValue(typeName);
                break;

            case "organizationId":
            case "organizationCode":
                String organizationCode = "-";
                if (excelRequestItem.getOrganizationId() != null) {
                    organizationCode = organizationClientDto.getBorgUserList().stream()
                            .filter(borgUser -> borgUser.getBorgID() == excelRequestItem.getOrganizationId())
                            .findFirst()
                            .map(BorgUserAdditionalDto::getBorgCode)
                            .orElse(DEFAULT_NULL_VALUE);
                }
                cell.setCellValue(excelRequestItem.getCompanyCode() != null ? excelRequestItem.getCompanyCode() : organizationCode);
                break;

            case "organizationName":
                String organizationName = "-";
                if (excelRequestItem.getOrganizationId() != null) {
                    organizationName = organizationClientDto.getBorgUserList().stream()
                            .filter(borgUser -> borgUser.getBorgID() == excelRequestItem.getOrganizationId())
                            .findFirst()
                            .map(BorgUserAdditionalDto::getBorgName)
                            .orElse(DEFAULT_NULL_VALUE);
                }
                cell.setCellValue(excelRequestItem.getCompanyName() != null ? excelRequestItem.getCompanyName() : organizationName);
                break;

            case "type":
                String type = excelRequestItem.getTypeName() != null ? excelRequestItem.getTypeName() : DEFAULT_NULL_VALUE;
                cell.setCellValue(type);
                break;

//            case "buyerName" :
//                String buyerName = excelRequestItem.getBuyerName() != null ? excelRequestItem.getBuyerName() : DEFAULT_NULL_VALUE;
//                cell.setCellValue(buyerName);
//                break;

            case "bidStartDate":
                if(excelRequestItem.getSourcingStatusId().equals(SOURCING_QUALIFIED_SUPPLIER.id()) || excelRequestItem.getSourcingStatusId().equals(SOURCING_NO_SUPPLIER_SELECTED.id())){
                    String bidStartDate = excelRequestItem.getBidStartDate() != null ? new SimpleDateFormat("dd/MM/yyyy").format(excelRequestItem.getBidStartDate()) : "-";
                    cell.setCellValue(bidStartDate);
                } else {
                    cell.setCellValue("-");
                }

                break;

            case "bidCompleteDate":
                if(excelRequestItem.getSourcingStatusId().equals(SOURCING_QUALIFIED_SUPPLIER.id()) || excelRequestItem.getSourcingStatusId().equals(SOURCING_NO_SUPPLIER_SELECTED.id())){
                    String bidCompleteDate = excelRequestItem.getBidCompleteDate() != null ? new SimpleDateFormat("dd/MM/yyyy").format(excelRequestItem.getBidCompleteDate()) : "-";
                    cell.setCellValue(bidCompleteDate);
                } else {
                    cell.setCellValue("-");
                }

                break;

            case "bidCompleteMonth":
                try {
                    if(excelRequestItem.getSourcingStatusId().equals(SOURCING_QUALIFIED_SUPPLIER.id()) || excelRequestItem.getSourcingStatusId().equals(SOURCING_NO_SUPPLIER_SELECTED.id())){
                        String bidCompleteMonth = excelRequestItem.getBidCompleteMonth() != null
                                ? shortMonths[Month.of(Integer.valueOf(excelRequestItem.getBidCompleteMonth())).getValue() - 1]
                                : "-";
                        cell.setCellValue(bidCompleteMonth);
                    } else {
                        cell.setCellValue("-");
                    }

                } catch (Exception ex) {
                    cell.setCellValue("-");
                }
                break;

            case "bidCompleteYear":
                if (excelRequestItem.getBidCompleteYear() != null) {
                   try {
                       if(excelRequestItem.getSourcingStatusId().equals(SOURCING_QUALIFIED_SUPPLIER.id()) || excelRequestItem.getSourcingStatusId().equals(SOURCING_NO_SUPPLIER_SELECTED.id())){
                           cell.setCellStyle(bodyCellStyles.get(TABLE_BODY_CELL_STYLES_INDEX.CENTER_WARP_TEXE_CELL.getValue()));
                           cell.setCellValue(Double.parseDouble(excelRequestItem.getBidCompleteYear()));
                           cell.setCellType(CellType.NUMERIC);
                       } else {
                           cell.setCellValue("-");
                       }

                   }  catch (Exception ex) {
                       cell.setCellValue("-");
                   }
                } else {
                    cell.setCellValue("-");
                }

                break;

            case "bidNo" :
                String bidNo = excelRequestItem.getBidNo() != null ? excelRequestItem.getBidNo() : "-";
                cell.setCellValue(bidNo);
                break;

            case "biddingType":
                String biddingType = excelRequestItem.getBiddingType() != null ? excelRequestItem.getBiddingType() : "-";
                cell.setCellValue(biddingType);
                break;

            case "bidDescription":
                String bidDescription = excelRequestItem.getBidDescription() != null ? excelRequestItem.getBidDescription() : "-";
                cell.setCellValue(bidDescription);
                break;

            case "awardedVendorName":
                if(excelRequestItem.getSourcingStatusId().equals(SOURCING_QUALIFIED_SUPPLIER.id()) || excelRequestItem.getSourcingStatusId().equals(SOURCING_NO_SUPPLIER_SELECTED.id())){
                    String awardedVendorName = excelRequestItem.getAwardedVendorName() != null ? excelRequestItem.getAwardedVendorName() : "-";
                    cell.setCellValue(awardedVendorName);
                } else {
                    cell.setCellValue("-");
                }

                break;

            case "taxNo":
                String taxNo = excelRequestItem.getTaxNo() != null ? excelRequestItem.getTaxNo() : "-";
                cell.setCellValue(taxNo);
                break;

            case "categoryName" :
                String categoryName = excelRequestItem.getCategoryName() != null ? excelRequestItem.getCategoryName() : "-";
                cell.setCellValue(categoryName);
                break;

            case "subCategoryName" :
                String subCategoryName = excelRequestItem.getSubCategoryName() != null ? excelRequestItem.getSubCategoryName() : "-";
                cell.setCellValue(subCategoryName);
                break;

            case "bidValidityStartDate" :
                String bidValidityStartDate = excelRequestItem.getBidValidityStartDate() != null ? new SimpleDateFormat("dd/MM/yyyy").format(excelRequestItem.getBidValidityStartDate()) : "-";
                cell.setCellValue(bidValidityStartDate);
                break;

            case "bidValidityEndDate" :
                String bidValidityEndDate = excelRequestItem.getBidValidityEndDate() != null ? new SimpleDateFormat("dd/MM/yyyy").format(excelRequestItem.getBidValidityEndDate()) : "-";
                cell.setCellValue(bidValidityEndDate);
                break;

            case "vatType" :
                if(excelRequestItem.getSourcingStatusId().equals(SOURCING_QUALIFIED_SUPPLIER.id()) || excelRequestItem.getSourcingStatusId().equals(SOURCING_NO_SUPPLIER_SELECTED.id())){
                    String vatType = excelRequestItem.getVatType() != null ? excelRequestItem.getVatType() : excelRequestItem.getVatTypeId() != null ? excelRequestItem.getVatTypeId().toString() : "-";
                    switch (vatType) {
                        case "Y" :
                        case "1" :
                            vatType = "Included vat";
                            break;

                        case "N" :
                        case "2" :
                            vatType = "Excluded vat";
                            break;

                        case "O" :
                            vatType = "Non vat";
                            break;

                        case "0" :
                            vatType = "-";
                            break;

                    }
                    cell.setCellValue(vatType);
                } else {
                    cell.setCellValue("-");
                }

                break;

            case "orderedQty" :

                if (excelRequestItem.getRequestTypeId() != null) {
                    if (excelRequestItem.getRequestTypeId() == REQUEST_TYPE_QUANTITY.id() && excelRequestItem.getQuantity() != null) {
                        Double orderedQty = null;
                        if(excelRequestItem.getSourcingTypeId() == SOURCING_TYPE_ERFX.id()) {
                            if(Set.of(SOURCING_QUALIFIED_SUPPLIER.id()).contains(excelRequestItem.getSourcingStatusId())){
                                orderedQty = excelRequestItem.getAwardedValue() != null ? Double.parseDouble(excelRequestItem.getAwardedValue()) : null;
                            } else {
                                orderedQty = excelRequestItem.getQuantity().doubleValue();
                            }
                        } else {
                            orderedQty =  excelRequestItem.getQuantity().doubleValue();
                        }
                        if(orderedQty != null) {
                            cell.setCellStyle(bodyCellStyles.get(TABLE_BODY_CELL_STYLES_INDEX.NUMERIC_2DP_CELL.getValue()));
                            cell.setCellValue(orderedQty);
                            cell.setCellType(CellType.NUMERIC);
                        } else {
                            cell.setCellValue("1.00");
                        }

                    } else if (excelRequestItem.getRequestTypeId() == REQUEST_TYPE_CONDITION.id()) {
                        String orderedQty = "-";
                        if(excelRequestItem.getSourcingTypeId() == SOURCING_TYPE_ERFX.id()) {
                            if(Set.of(SOURCING_QUALIFIED_SUPPLIER.id()).contains(excelRequestItem.getSourcingStatusId())){
                                orderedQty = excelRequestItem.getAwardedValue() != null ? excelRequestItem.getAwardedValue() : "1.00";
                            } else {
                                orderedQty =  "1.00";
                            }
                        } else {
                            orderedQty =  "1.00";
                        }
                        cell.setCellStyle(bodyCellStyles.get(TABLE_BODY_CELL_STYLES_INDEX.NUMERIC_2DP_CELL.getValue()));
                        cell.setCellValue(Double.parseDouble(orderedQty));
                        cell.setCellType(CellType.NUMERIC);
                    }
                } else if (excelRequestItem.getQuantity() != null) {
                    cell.setCellStyle(bodyCellStyles.get(TABLE_BODY_CELL_STYLES_INDEX.NUMERIC_2DP_CELL.getValue()));
                    cell.setCellValue(excelRequestItem.getQuantity().doubleValue());
                    cell.setCellType(CellType.NUMERIC);
                } else {
                    cell.setCellValue("1.00");
                }
                break;

            case "totalProjectedPrice" :
                if(excelRequestItem.getSourcingStatusId().equals(SOURCING_QUALIFIED_SUPPLIER.id()) || excelRequestItem.getSourcingStatusId().equals(SOURCING_NO_SUPPLIER_SELECTED.id())){
                    String totalProjectedPrice = "-";
                    if (excelRequestItem.getTotalProjectedPrice() != null) {
                        cell.setCellStyle(bodyCellStyles.get(TABLE_BODY_CELL_STYLES_INDEX.NUMERIC_2DP_CELL.getValue()));
                        cell.setCellValue(excelRequestItem.getTotalProjectedPrice().doubleValue());
                        cell.setCellType(CellType.NUMERIC);
                    } else {
                        cell.setCellValue(totalProjectedPrice);
                    }
                } else {
                    cell.setCellValue("-");
                }
                break;

            case "totalProjectedPriceVat7Percentage" :
                if(excelRequestItem.getSourcingStatusId().equals(SOURCING_QUALIFIED_SUPPLIER.id()) || excelRequestItem.getSourcingStatusId().equals(SOURCING_NO_SUPPLIER_SELECTED.id())){
                    String totalProjectedPriceVat7Percentage = "-";
                    if (excelRequestItem.getTotalProjectedPriceVat7Percentage() != null) {
                        cell.setCellStyle(bodyCellStyles.get(TABLE_BODY_CELL_STYLES_INDEX.NUMERIC_2DP_CELL.getValue()));
                        cell.setCellValue(excelRequestItem.getTotalProjectedPriceVat7Percentage().doubleValue());
                        cell.setCellType(CellType.NUMERIC);
                    } else {
                        cell.setCellValue(totalProjectedPriceVat7Percentage);
                    }
                } else {
                    cell.setCellValue("-");
                }
                break;

            case "totalFinalPrice" :
                if(excelRequestItem.getSourcingStatusId().equals(SOURCING_QUALIFIED_SUPPLIER.id()) || excelRequestItem.getSourcingStatusId().equals(SOURCING_NO_SUPPLIER_SELECTED.id())){
                    String totalFinalPrice = "-";
                    if (excelRequestItem.getTotalFinalPrice() != null) {
                        cell.setCellStyle(bodyCellStyles.get(TABLE_BODY_CELL_STYLES_INDEX.NUMERIC_2DP_CELL.getValue()));
                        cell.setCellValue(excelRequestItem.getTotalFinalPrice().doubleValue());
                        cell.setCellType(CellType.NUMERIC);
                    } else {
                        cell.setCellValue(totalFinalPrice);
                    }
                } else {
                    cell.setCellValue("-");
                }
                break;

            case "totalFinalPriceVat7Percentage" :
                if(excelRequestItem.getSourcingStatusId().equals(SOURCING_QUALIFIED_SUPPLIER.id()) || excelRequestItem.getSourcingStatusId().equals(SOURCING_NO_SUPPLIER_SELECTED.id())){
                    String totalFinalPriceVat7Percentage = "-";
                    if (excelRequestItem.getTotalFinalPriceVat7Percentage() != null) {
                        cell.setCellStyle(bodyCellStyles.get(TABLE_BODY_CELL_STYLES_INDEX.NUMERIC_2DP_CELL.getValue()));
                        cell.setCellValue(excelRequestItem.getTotalFinalPriceVat7Percentage().doubleValue());
                        cell.setCellType(CellType.NUMERIC);
                    } else {
                        cell.setCellValue(totalFinalPriceVat7Percentage);
                    }
                } else {
                    cell.setCellValue("-");
                }
                break;

            case "totalSavingAmount" :
                if(excelRequestItem.getSourcingStatusId().equals(SOURCING_QUALIFIED_SUPPLIER.id()) || excelRequestItem.getSourcingStatusId().equals(SOURCING_NO_SUPPLIER_SELECTED.id())){
                    String totalSavingAmount = "-";
                    if (excelRequestItem.getTotalSavingAmount() != null) {
                        cell.setCellStyle(bodyCellStyles.get(TABLE_BODY_CELL_STYLES_INDEX.NUMERIC_2DP_CELL.getValue()));
                        cell.setCellValue(excelRequestItem.getTotalSavingAmount().doubleValue());
                        cell.setCellType(CellType.NUMERIC);
                    } else {
                        cell.setCellValue(totalSavingAmount);
                    }
                } else {
                    cell.setCellValue("-");
                }
                break;

            case "totalSavingAmountVat7Percentage" :
                if(excelRequestItem.getSourcingStatusId().equals(SOURCING_QUALIFIED_SUPPLIER.id()) || excelRequestItem.getSourcingStatusId().equals(SOURCING_NO_SUPPLIER_SELECTED.id())){
                    String totalSavingAmountVat7Percentage = "-";
                    if (excelRequestItem.getTotalSavingAmountVat7Percentage() != null) {
                        cell.setCellStyle(bodyCellStyles.get(TABLE_BODY_CELL_STYLES_INDEX.NUMERIC_2DP_CELL.getValue()));
                        cell.setCellValue(excelRequestItem.getTotalSavingAmountVat7Percentage().doubleValue());
                        cell.setCellType(CellType.NUMERIC);
                    } else {
                        cell.setCellValue(totalSavingAmountVat7Percentage);
                    }
                } else {
                    cell.setCellValue("-");
                }
                break;

            case "costAvoidanceVat7Percentage" :
                if(excelRequestItem.getSourcingStatusId().equals(SOURCING_QUALIFIED_SUPPLIER.id()) || excelRequestItem.getSourcingStatusId().equals(SOURCING_NO_SUPPLIER_SELECTED.id())){
                    String costAvoidanceVat7Percentage = "-";
                    if (excelRequestItem.getCostAvoidanceVat7Percentage() != null) {
                        cell.setCellStyle(bodyCellStyles.get(TABLE_BODY_CELL_STYLES_INDEX.NUMERIC_4DP_CELL.getValue()));
                        cell.setCellValue(excelRequestItem.getCostAvoidanceVat7Percentage().doubleValue());
                        cell.setCellType(CellType.NUMERIC);
                    } else {
                        cell.setCellValue(costAvoidanceVat7Percentage);
                    }
                } else {
                    cell.setCellValue("-");
                }
                break;

            case "savingPercentage" :
                if(excelRequestItem.getSourcingStatusId().equals(SOURCING_QUALIFIED_SUPPLIER.id()) || excelRequestItem.getSourcingStatusId().equals(SOURCING_NO_SUPPLIER_SELECTED.id())){
                    String savingPercentage = "-";
                    if (excelRequestItem.getSavingPercentage() != null) {
                        cell.setCellStyle(bodyCellStyles.get(TABLE_BODY_CELL_STYLES_INDEX.NUMERIC_2DP_CELL.getValue()));
                        cell.setCellValue(excelRequestItem.getSavingPercentage().doubleValue());
                        cell.setCellType(CellType.NUMERIC);
                    } else {
                        cell.setCellValue(savingPercentage);
                    }
                } else {
                    cell.setCellValue("-");
                }
                break;

            case "comment" :
                String comment = excelRequestItem.getExistingPriceComment() != null ? excelRequestItem.getExistingPriceComment() : "-";
                cell.setCellValue(comment);
                break;

            case "withdraw" :
                String withdraw = "No";
                if (excelRequestItem.getWithdraw() != null) {
                    withdraw = excelRequestItem.getWithdraw() ? "Yes" : "No";
                }
                cell.setCellValue(withdraw);
                break;

            case "withdrawReason" :
                String withdrawReason = "-";
                if (excelRequestItem.getWithdrawReason() != null) {
                    withdrawReason = excelRequestItem.getWithdrawReason();
                }
                cell.setCellValue(withdrawReason);
                break;

        }

        Object cellValue = null; //cell.getStringCellValue();

        if (cell.getCellTypeEnum().equals(CellType.NUMERIC)) {
            cellValue = cell.getNumericCellValue();
        } else {
            cellValue = cell.getStringCellValue();
        }

        if (cellValue instanceof String) {
            String value = (String) cellValue;
            cell.setCellValue(StringUtils.isNotBlank(value) ? value : "");
            cell.setCellStyle(bodyCellStyles.get(TABLE_BODY_CELL_STYLES_INDEX.CENTER_WARP_TEXE_CELL.getValue()));
        } /*else if (cellValue instanceof Double) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((Double) cellValue);
            cell.setCellStyle(bodyCellStyles.get(TABLE_BODY_CELL_STYLES_INDEX.NUMERIC_CELL.getValue()));
        }*/ else if (cellValue instanceof Date) {
            cell.setCellValue((Date) cellValue);
            cell.setCellStyle(bodyCellStyles.get(TABLE_BODY_CELL_STYLES_INDEX.DATE_CELL.getValue()));
        }

    }

    private void setColumnWidth(SXSSFSheet sheet, int columnIndex, int width) {
        sheet.setColumnWidth(columnIndex, width * 256);
    }

    private List<CellStyle> generateTableHeaderCellStyles(XSSFWorkbook workbook, Font font) {
        List<CellStyle> cellStyles = new ArrayList<>();
        CellStyle tableHeaderCellStyles = workbook.createCellStyle();
        tableHeaderCellStyles.setLocked(false);
        tableHeaderCellStyles.setFont(font);
        tableHeaderCellStyles.setAlignment(HorizontalAlignment.CENTER);
        tableHeaderCellStyles.setVerticalAlignment(VerticalAlignment.TOP);
        tableHeaderCellStyles.setDataFormat(workbook.createDataFormat().getFormat(TEXT_FORMAT));
        tableHeaderCellStyles.setBorderTop(BorderStyle.THIN);
        tableHeaderCellStyles.setBorderBottom(BorderStyle.THIN);
        tableHeaderCellStyles.setBorderLeft(BorderStyle.THIN);
        tableHeaderCellStyles.setBorderRight(BorderStyle.THIN);
        tableHeaderCellStyles.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        tableHeaderCellStyles.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyles.add(tableHeaderCellStyles);

        CellStyle tableHeaderEditCellStyles = workbook.createCellStyle();
        tableHeaderEditCellStyles.setLocked(false);
        tableHeaderEditCellStyles.setFont(font);
        tableHeaderEditCellStyles.setAlignment(HorizontalAlignment.CENTER);
        tableHeaderEditCellStyles.setVerticalAlignment(VerticalAlignment.TOP);
        tableHeaderEditCellStyles.setDataFormat(workbook.createDataFormat().getFormat(TEXT_FORMAT));
        tableHeaderEditCellStyles.setBorderTop(BorderStyle.THIN);
        tableHeaderEditCellStyles.setBorderBottom(BorderStyle.THIN);
        tableHeaderEditCellStyles.setBorderLeft(BorderStyle.THIN);
        tableHeaderEditCellStyles.setBorderRight(BorderStyle.THIN);
        tableHeaderEditCellStyles.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyles.add(tableHeaderEditCellStyles);

        CellStyle dateCellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat( "dd/MM/yyyy"));
        dateCellStyle.setAlignment(HorizontalAlignment.CENTER);
        dateCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        dateCellStyle.setWrapText(true);
        cellStyles.add(dateCellStyle);

        CellStyle textCellStyle = workbook.createCellStyle();
        textCellStyle.setDataFormat(workbook.createDataFormat().getFormat("@"));
        textCellStyle.setAlignment(HorizontalAlignment.CENTER);
        textCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        textCellStyle.setWrapText(true);
        cellStyles.add(textCellStyle);

        return cellStyles;
    }

    private List<CellStyle> generateTableHeaderCellStyles(SXSSFWorkbook workbook, Font font) {
        List<CellStyle> cellStyles = new ArrayList<>();
        CellStyle tableHeaderCellStyles = workbook.createCellStyle();
        tableHeaderCellStyles.setLocked(false);
        tableHeaderCellStyles.setFont(font);
        tableHeaderCellStyles.setAlignment(HorizontalAlignment.CENTER);
        tableHeaderCellStyles.setVerticalAlignment(VerticalAlignment.TOP);
        tableHeaderCellStyles.setDataFormat(workbook.createDataFormat().getFormat(TEXT_FORMAT));
        tableHeaderCellStyles.setBorderTop(BorderStyle.THIN);
        tableHeaderCellStyles.setBorderBottom(BorderStyle.THIN);
        tableHeaderCellStyles.setBorderLeft(BorderStyle.THIN);
        tableHeaderCellStyles.setBorderRight(BorderStyle.THIN);
        tableHeaderCellStyles.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        tableHeaderCellStyles.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyles.add(tableHeaderCellStyles);

        CellStyle tableHeaderEditCellStyles = workbook.createCellStyle();
        tableHeaderEditCellStyles.setLocked(false);
        tableHeaderEditCellStyles.setFont(font);
        tableHeaderEditCellStyles.setAlignment(HorizontalAlignment.CENTER);
        tableHeaderEditCellStyles.setVerticalAlignment(VerticalAlignment.TOP);
        tableHeaderEditCellStyles.setDataFormat(workbook.createDataFormat().getFormat(TEXT_FORMAT));
        tableHeaderEditCellStyles.setBorderTop(BorderStyle.THIN);
        tableHeaderEditCellStyles.setBorderBottom(BorderStyle.THIN);
        tableHeaderEditCellStyles.setBorderLeft(BorderStyle.THIN);
        tableHeaderEditCellStyles.setBorderRight(BorderStyle.THIN);
        tableHeaderEditCellStyles.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyles.add(tableHeaderEditCellStyles);

        CellStyle dateCellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat( "dd/MM/yyyy"));
        dateCellStyle.setAlignment(HorizontalAlignment.CENTER);
        dateCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        dateCellStyle.setWrapText(true);
        cellStyles.add(dateCellStyle);

        CellStyle textCellStyle = workbook.createCellStyle();
        textCellStyle.setDataFormat(workbook.createDataFormat().getFormat("@"));
        textCellStyle.setAlignment(HorizontalAlignment.CENTER);
        textCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        textCellStyle.setWrapText(true);
        cellStyles.add(textCellStyle);

        return cellStyles;
    }

    private List<CellStyle> generateTableFooterCellStyles(SXSSFWorkbook workbook, Font font) {
        List<CellStyle> cellStyles = new ArrayList<>();

        CellStyle rightNumericLockedCellStyle = workbook.createCellStyle();
        rightNumericLockedCellStyle.setLocked(true);
        rightNumericLockedCellStyle.setFont(font);
        rightNumericLockedCellStyle.setAlignment(HorizontalAlignment.RIGHT);
        rightNumericLockedCellStyle.setDataFormat(workbook.createDataFormat().getFormat(DECIMAL_2PLACES_FORMAT));
        rightNumericLockedCellStyle.setBorderLeft(BorderStyle.THIN);
        rightNumericLockedCellStyle.setBorderRight(BorderStyle.THIN);
        rightNumericLockedCellStyle.setBorderBottom(BorderStyle.THIN);
        rightNumericLockedCellStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        rightNumericLockedCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyles.add(rightNumericLockedCellStyle);

        CellStyle alignCenterCellStyle = workbook.createCellStyle();
        alignCenterCellStyle.setLocked(true);
        alignCenterCellStyle.setFont(font);
        alignCenterCellStyle.setAlignment(HorizontalAlignment.CENTER);
        alignCenterCellStyle.setDataFormat(workbook.createDataFormat().getFormat(TEXT_FORMAT));
        alignCenterCellStyle.setBorderLeft(BorderStyle.THIN);
        alignCenterCellStyle.setBorderRight(BorderStyle.THIN);
        alignCenterCellStyle.setBorderBottom(BorderStyle.THIN);
        alignCenterCellStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        alignCenterCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyles.add(alignCenterCellStyle);

        return cellStyles;
    }

    private List<CellStyle> generateTableBodyCellStyles(SXSSFWorkbook workbook, Font font) {
        List<CellStyle> cellStyles = new ArrayList<>();

        CellStyle tableCellStyle = workbook.createCellStyle();
        tableCellStyle.setLocked(false);
        tableCellStyle.setFont(font);
        tableCellStyle.setAlignment(HorizontalAlignment.LEFT);
        tableCellStyle.setDataFormat(workbook.createDataFormat().getFormat(TEXT_FORMAT));
        tableCellStyle.setBorderTop(BorderStyle.THIN);
        tableCellStyle.setBorderBottom(BorderStyle.THIN);
        tableCellStyle.setBorderLeft(BorderStyle.THIN);
        tableCellStyle.setBorderRight(BorderStyle.THIN);
        cellStyles.add(tableCellStyle);

        CellStyle tableBottomCellStyle = workbook.createCellStyle();
        tableBottomCellStyle.setLocked(false);
        tableBottomCellStyle.setFont(font);
        tableBottomCellStyle.setAlignment(HorizontalAlignment.LEFT);
        tableBottomCellStyle.setDataFormat(workbook.createDataFormat().getFormat(TEXT_FORMAT));
        tableBottomCellStyle.setBorderLeft(BorderStyle.THIN);
        tableBottomCellStyle.setBorderRight(BorderStyle.THIN);
        tableBottomCellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyles.add(tableBottomCellStyle);

        CellStyle lockedTableCellStyle = workbook.createCellStyle();
        lockedTableCellStyle.setLocked(true);
        lockedTableCellStyle.setFont(font);
        lockedTableCellStyle.setAlignment(HorizontalAlignment.LEFT);
        lockedTableCellStyle.setDataFormat(workbook.createDataFormat().getFormat(TEXT_FORMAT));
        lockedTableCellStyle.setBorderTop(BorderStyle.THIN);
        lockedTableCellStyle.setBorderBottom(BorderStyle.THIN);
        lockedTableCellStyle.setBorderLeft(BorderStyle.THIN);
        lockedTableCellStyle.setBorderRight(BorderStyle.THIN);
        cellStyles.add(lockedTableCellStyle);

        CellStyle lockedBottomCellStyle = workbook.createCellStyle();
        lockedBottomCellStyle.setLocked(true);
        lockedBottomCellStyle.setFont(font);
        lockedBottomCellStyle.setAlignment(HorizontalAlignment.CENTER);
        lockedBottomCellStyle.setDataFormat(workbook.createDataFormat().getFormat(TEXT_FORMAT));
        lockedBottomCellStyle.setBorderLeft(BorderStyle.THIN);
        lockedBottomCellStyle.setBorderRight(BorderStyle.THIN);
        lockedBottomCellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyles.add(lockedBottomCellStyle);

        CellStyle rightNumeric2DPCellStyle = workbook.createCellStyle();
        rightNumeric2DPCellStyle.setLocked(false);
        rightNumeric2DPCellStyle.setFont(font);
        rightNumeric2DPCellStyle.setAlignment(HorizontalAlignment.RIGHT);
        rightNumeric2DPCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        rightNumeric2DPCellStyle.setDataFormat(workbook.createDataFormat().getFormat(DECIMAL_2PLACES_FORMAT));
        rightNumeric2DPCellStyle.setBorderLeft(BorderStyle.THIN);
        rightNumeric2DPCellStyle.setBorderRight(BorderStyle.THIN);
        rightNumeric2DPCellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyles.add(rightNumeric2DPCellStyle);

        CellStyle rightNumeric2DPLockedCellStyle = workbook.createCellStyle();
        rightNumeric2DPLockedCellStyle.setLocked(true);
        rightNumeric2DPLockedCellStyle.setFont(font);
        rightNumeric2DPLockedCellStyle.setAlignment(HorizontalAlignment.RIGHT);
        rightNumeric2DPLockedCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        rightNumeric2DPLockedCellStyle.setDataFormat(workbook.createDataFormat().getFormat(DECIMAL_2PLACES_FORMAT));
        rightNumeric2DPLockedCellStyle.setBorderLeft(BorderStyle.THIN);
        rightNumeric2DPLockedCellStyle.setBorderRight(BorderStyle.THIN);
        rightNumeric2DPLockedCellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyles.add(rightNumeric2DPLockedCellStyle);

        CellStyle rightNumeric4DPCellStyle = workbook.createCellStyle();
        rightNumeric4DPCellStyle.setLocked(false);
        rightNumeric4DPCellStyle.setFont(font);
        rightNumeric4DPCellStyle.setAlignment(HorizontalAlignment.RIGHT);
        rightNumeric4DPCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        rightNumeric4DPCellStyle.setDataFormat(workbook.createDataFormat().getFormat(DECIMAL_4PLACES_FORMAT));
        rightNumeric4DPCellStyle.setBorderLeft(BorderStyle.THIN);
        rightNumeric4DPCellStyle.setBorderRight(BorderStyle.THIN);
        rightNumeric4DPCellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyles.add(rightNumeric4DPCellStyle);

        CellStyle rightNumeric4DPLockedCellStyle = workbook.createCellStyle();
        rightNumeric4DPLockedCellStyle.setLocked(true);
        rightNumeric4DPLockedCellStyle.setFont(font);
        rightNumeric4DPLockedCellStyle.setAlignment(HorizontalAlignment.RIGHT);
        rightNumeric4DPLockedCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        rightNumeric4DPLockedCellStyle.setDataFormat(workbook.createDataFormat().getFormat(DECIMAL_4PLACES_FORMAT));
        rightNumeric4DPLockedCellStyle.setBorderLeft(BorderStyle.THIN);
        rightNumeric4DPLockedCellStyle.setBorderRight(BorderStyle.THIN);
        rightNumeric4DPLockedCellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyles.add(rightNumeric4DPLockedCellStyle);

        CellStyle centerWarpTextCellStyle = workbook.createCellStyle();
        centerWarpTextCellStyle.setLocked(false);
        centerWarpTextCellStyle.setFont(font);
        centerWarpTextCellStyle.setAlignment(HorizontalAlignment.CENTER);
        centerWarpTextCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        centerWarpTextCellStyle.setDataFormat(workbook.createDataFormat().getFormat(TEXT_FORMAT));
        centerWarpTextCellStyle.setBorderTop(BorderStyle.THIN);
        centerWarpTextCellStyle.setBorderLeft(BorderStyle.THIN);
        centerWarpTextCellStyle.setBorderRight(BorderStyle.THIN);
        centerWarpTextCellStyle.setBorderBottom(BorderStyle.THIN);
        centerWarpTextCellStyle.setWrapText(true);
        cellStyles.add(centerWarpTextCellStyle);

        CellStyle dateCellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat( "dd/MM/yyyy"));
        dateCellStyle.setAlignment(HorizontalAlignment.CENTER);
        dateCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        dateCellStyle.setBorderTop(BorderStyle.THIN);
        dateCellStyle.setBorderLeft(BorderStyle.THIN);
        dateCellStyle.setBorderRight(BorderStyle.THIN);
        dateCellStyle.setBorderBottom(BorderStyle.THIN);
        dateCellStyle.setWrapText(true);
        cellStyles.add(dateCellStyle);

        CellStyle textCellStyle = workbook.createCellStyle();
        textCellStyle.setDataFormat(workbook.createDataFormat().getFormat("@"));
        textCellStyle.setAlignment(HorizontalAlignment.CENTER);
        textCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        textCellStyle.setBorderTop(BorderStyle.THIN);
        textCellStyle.setBorderLeft(BorderStyle.THIN);
        textCellStyle.setBorderRight(BorderStyle.THIN);
        textCellStyle.setBorderBottom(BorderStyle.THIN);
        textCellStyle.setWrapText(true);
        cellStyles.add(textCellStyle);

        return cellStyles;
    }

    private List<CellStyle> generateGeneralCellStyles(SXSSFWorkbook workbook, Font font) {
        List<CellStyle> cellStyles = new ArrayList<>();

        CellStyle generalCellStyle = workbook.createCellStyle();
        generalCellStyle.setLocked(false);
        generalCellStyle.setFont(font);
        generalCellStyle.setAlignment(HorizontalAlignment.LEFT);
        generalCellStyle.setDataFormat(workbook.createDataFormat().getFormat(TEXT_FORMAT));
        cellStyles.add(generalCellStyle);

        Font boldFont = workbook.createFont();
        boldFont.setFontName(FONT_FREESIAL_UPC);
        boldFont.setFontHeightInPoints((short) 18);

        CellStyle boldCellStyle = workbook.createCellStyle();
        boldCellStyle.setLocked(false);
        boldCellStyle.setFont(boldFont);
        boldCellStyle.setAlignment(HorizontalAlignment.LEFT);
        boldCellStyle.setDataFormat(workbook.createDataFormat().getFormat(TEXT_FORMAT));
        cellStyles.add(boldCellStyle);

        Font blueFont = workbook.createFont();
        blueFont.setFontName(FONT_FREESIAL_UPC);
        blueFont.setFontHeightInPoints((short) 16);
        blueFont.setColor(IndexedColors.BLUE.getIndex());

        CellStyle blueFontCellStyle = workbook.createCellStyle();
        blueFontCellStyle.setLocked(false);
        blueFontCellStyle.setFont(blueFont);
        blueFontCellStyle.setAlignment(HorizontalAlignment.LEFT);
        blueFontCellStyle.setDataFormat(workbook.createDataFormat().getFormat(TEXT_FORMAT));
        cellStyles.add(blueFontCellStyle);

        CellStyle lockedCellStyle = workbook.createCellStyle();
        lockedCellStyle.setLocked(true);
        lockedCellStyle.setFont(font);
        lockedCellStyle.setAlignment(HorizontalAlignment.LEFT);
        lockedCellStyle.setDataFormat(workbook.createDataFormat().getFormat(TEXT_FORMAT));
        cellStyles.add(lockedCellStyle);

        CellStyle alignRightCellStyle = workbook.createCellStyle();
        alignRightCellStyle.setLocked(false);
        alignRightCellStyle.setFont(blueFont);
        alignRightCellStyle.setAlignment(HorizontalAlignment.RIGHT);
        alignRightCellStyle.setDataFormat(workbook.createDataFormat().getFormat(TEXT_FORMAT));
        cellStyles.add(alignRightCellStyle);

        CellStyle alignCenterCellStyle = workbook.createCellStyle();
        alignCenterCellStyle.setLocked(true);
        alignCenterCellStyle.setFont(boldFont);
        alignCenterCellStyle.setAlignment(HorizontalAlignment.CENTER);
        alignCenterCellStyle.setDataFormat(workbook.createDataFormat().getFormat(TEXT_FORMAT));
        cellStyles.add(alignCenterCellStyle);

        CellStyle wrapText = workbook.createCellStyle();
        wrapText.setWrapText(true);
        cellStyles.add(wrapText);

        CellStyle borderThin = workbook.createCellStyle();
        borderThin.setBorderTop(BorderStyle.THIN);
        borderThin.setBorderBottom(BorderStyle.THIN);
        borderThin.setBorderLeft(BorderStyle.THIN);
        borderThin.setBorderRight(BorderStyle.THIN);
        cellStyles.add(borderThin);

        CellStyle greenCell = workbook.createCellStyle();
        greenCell.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        greenCell.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyles.add(greenCell);

        CellStyle alignRightBlackFontCellStyle = workbook.createCellStyle();
        alignRightBlackFontCellStyle.setLocked(false);
        alignRightBlackFontCellStyle.setFont(font);
        alignRightBlackFontCellStyle.setAlignment(HorizontalAlignment.RIGHT);
        alignRightBlackFontCellStyle.setDataFormat(workbook.createDataFormat().getFormat(TEXT_FORMAT));
        cellStyles.add(alignRightBlackFontCellStyle);

        return cellStyles;
    }

    public enum HEADER_CELL_STYLES_INDEX {
        HEADER_LOCKED(0),
        HEADER_EDITABLE(1);

        private static Map map = new HashMap<>();

        static {
            for (HEADER_CELL_STYLES_INDEX pageType : HEADER_CELL_STYLES_INDEX.values()) {
                map.put(pageType.value, pageType);
            }
        }

        private int value;

        HEADER_CELL_STYLES_INDEX(int value) {
            this.value = value;
        }

        public static PageType valueOf(int pageType) {
            return (PageType) map.get(pageType);
        }

        public int getValue() {
            return value;
        }
    }

    public enum CONTENT_CELL_STYLES_INDEX {
        TEXT(0),
        NUMERIC(1),
        NUMBER(2),
        DECIMAL(3),
        DATE(4),
        PERCENTAGE(5);

        private static Map map = new HashMap<>();

        static {
            for (CONTENT_CELL_STYLES_INDEX pageType : CONTENT_CELL_STYLES_INDEX.values()) {
                map.put(pageType.value, pageType);
            }
        }

        private int value;

        CONTENT_CELL_STYLES_INDEX(int value) {
            this.value = value;
        }

        public static PageType valueOf(int pageType) {
            return (PageType) map.get(pageType);
        }

        public int getValue() {
            return value;
        }
    }

    public enum TABLE_HEADER_CELL_STYLES_INDEX {
        HEADER_LOCKED(0),
        HEADER_EDITABLE(1);

        private static Map map = new HashMap<>();

        static {
            for (HEADER_CELL_STYLES_INDEX pageType : HEADER_CELL_STYLES_INDEX.values()) {
                map.put(pageType.value, pageType);
            }
        }

        private int value;

        TABLE_HEADER_CELL_STYLES_INDEX(int value) {
            this.value = value;
        }

        public static PageType valueOf(int pageType) {
            return (PageType) map.get(pageType);
        }

        public int getValue() {
            return value;
        }
    }

    public enum TABLE_BODY_CELL_STYLES_INDEX {
        BODY_CELL(0),
        BODY_BOTTOM_CELL(1),
        LOCKED_CELL(2),
        LOCKED_BOTTOM_CELL(3),
        NUMERIC_2DP_CELL(4),
        LOCKED_NUMERIC_2DP_CELL(5),
        NUMERIC_4DP_CELL(6),
        LOCKED_NUMERIC_4DP_CELL(7),
        CENTER_WARP_TEXE_CELL(8),
        DATE_CELL(9);

        private static Map map = new HashMap<>();

        static {
            for (HEADER_CELL_STYLES_INDEX pageType : HEADER_CELL_STYLES_INDEX.values()) {
                map.put(pageType.value, pageType);
            }
        }

        private int value;

        TABLE_BODY_CELL_STYLES_INDEX(int value) {
            this.value = value;
        }

        public static PageType valueOf(int pageType) {
            return (PageType) map.get(pageType);
        }

        public int getValue() {
            return value;
        }
    }

    public enum TABLE_FOOTER_CELL_STYLES_INDEX {
        LOCKED_NUMERIC_CELL(0),
        CENTER_TEXT_CELL(1);

        private static Map map = new HashMap<>();

        static {
            for (TABLE_FOOTER_CELL_STYLES_INDEX pageType : TABLE_FOOTER_CELL_STYLES_INDEX.values()) {
                map.put(pageType.value, pageType);
            }
        }

        private int value;

        TABLE_FOOTER_CELL_STYLES_INDEX(int value) {
            this.value = value;
        }

        public static PageType valueOf(int pageType) {
            return (PageType) map.get(pageType);
        }

        public int getValue() {
            return value;
        }
    }

    public enum GENERAL_CELL_STYLES_INDEX {
        GENERAL_CELL(0),
        BOLD_CELL(1),
        BLUE_CELL(2),
        LOCKED_CELL(3),
        ALIGN_RIGHT(4),
        ALIGN_CENTER(5),
        WRAP_TEXT(6),
        BORDER_CELL(7),
        GREEN_CELL(8),
        ALIGN_RIGHT_BLACK_FONT(9);

        private static Map map = new HashMap<>();

        static {
            for (HEADER_CELL_STYLES_INDEX pageType : HEADER_CELL_STYLES_INDEX.values()) {
                map.put(pageType.value, pageType);
            }
        }

        private int value;

        GENERAL_CELL_STYLES_INDEX(int value) {
            this.value = value;
        }

        public static PageType valueOf(int pageType) {
            return (PageType) map.get(pageType);
        }

        public int getValue() {
            return value;
        }
    }

    public enum EXCEL_HEADER_MAPPING_INDEX {
        A(0),
        B(1),
        C(2),
        D(3),
        E(4),
        F(5),
        G(6),
        H(7),
        I(8),
        J(9),
        K(10),
        L(11),
        M(12),
        N(13),
        O(14),
        P(15),
        Q(16),
        R(17),
        S(18),
        T(19),
        U(20),
        V(21),
        W(22),
        X(23),
        Y(24),
        Z(25),
        AA(26),
        AB(27),
        AC(28),
        AD(29),
        AE(30),
        AF(31),
        AG(32),
        AH(33),
        AI(34),
        AJ(35),
        AK(36),
        AL(37),
        AM(38),
        AN(39),
        AO(40),
        AP(41),
        AQ(42),
        AR(43),
        AS(44),
        AT(45),
        AU(46),
        AV(47),
        AW(48),
        AX(49),
        AY(50),
        AZ(51);

        private static Map map = new HashMap<>();

        static {
            for (EXCEL_HEADER_MAPPING_INDEX pageType : EXCEL_HEADER_MAPPING_INDEX.values()) {
                map.put(pageType.value, pageType);
            }
        }

        private int value;

        EXCEL_HEADER_MAPPING_INDEX(int value) {
            this.value = value;
        }

        public static PageType valueOf(int pageType) {
            return (PageType) map.get(pageType);
        }

        public int getValue() {
            return value;
        }

    }

    private String getTimeZoneUser() {
        UserDto userDto = AppUtil.getUser();
        String userName = userDto.getUsername();
        String tenantId = userDto.getTenantId();
        String idp = userDto.getIdp();
        UserDetailResponse userDetail = uaaService.getUserDetailByTenantIdAndIdpAndUserName(tenantId, idp, null, userName, null);
        return userDetail != null ? userDetail.getTimeZone() : null;
    }
}
