package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExcelReportTemplateDto {

    private int rowIndex;
    private Row row;
    private Cell cell;
    private XSSFSheet sheet;

    public void nextRowIndex() {
        this.setRowIndex(++this.rowIndex);
    }

    public void setRowByCurrentIndex() {
        this.setRow(this.sheet.createRow(rowIndex));

    }

    public void nextRow() {
        this.nextRowIndex();
        this.setRowByCurrentIndex();
    }

    public Row nextAndGetRow() {
        this.nextRow();
        return this.getRow();
    }

    public Row getRowByIndex(int index) {
        return this.sheet.createRow(index);
    }
}
