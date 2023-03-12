package com.mahim.shopme.admin.category.exporter;

import com.mahim.shopme.common.entity.Category;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
public class CategoryExcelExporter extends AbstractExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    public CategoryExcelExporter() {
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        if (workbook.getSheetIndex("Categories") > -1) {
            workbook.removeSheetAt(workbook.getSheetIndex("Categories"));
        }
        sheet = workbook.createSheet(WorkbookUtil.createSafeSheetName("Categories"));
        XSSFRow row = sheet.createRow(0);

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        cellStyle.setFont(font);

        createCell(row, 0, "ID", cellStyle);
        createCell(row, 1, "Name", cellStyle);
        createCell(row, 2, "Alias", cellStyle);
        createCell(row, 3, "Enabled", cellStyle);
    }

    private void createCell(XSSFRow row, int columnIndex, Object value, CellStyle style) {
        XSSFCell cell = row.createCell(columnIndex);
        sheet.autoSizeColumn(columnIndex);

        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        }

        cell.setCellStyle(style);
    }

    public void export(HttpServletResponse response, List<Category> categories) {
        super.setResponseHeader(response, "application/octet-stream", ".xlsx");

        writeHeaderLine();
        writeDateLines(categories);

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeDateLines(List<Category> categories) {
        int rowIndex = 1;

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        cellStyle.setFont(font);

        for (Category user : categories) {
            XSSFRow row = sheet.createRow(rowIndex++);
            int columnIndex = 0;
            createCell(row, columnIndex++, user.getId(), cellStyle);
            createCell(row, columnIndex++, user.getName(), cellStyle);
            createCell(row, columnIndex++, user.getAlias(), cellStyle);
            createCell(row, columnIndex, user.isEnabled(), cellStyle);
        }

    }
}