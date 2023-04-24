package com.mahim.shopme.admin.brand.exporter;

import com.mahim.shopme.common.entity.Brand;
import com.mahim.shopme.common.entity.Category;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrandExcelExporter extends AbstractExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    public BrandExcelExporter() {
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        if (workbook.getSheetIndex("Brands") > -1) {
            workbook.removeSheetAt(workbook.getSheetIndex("Brands"));
        }
        sheet = workbook.createSheet(WorkbookUtil.createSafeSheetName("Brands"));
        XSSFRow row = sheet.createRow(0);

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        cellStyle.setFont(font);

        createCell(row, 0, "ID", cellStyle);
        createCell(row, 1, "Name", cellStyle);
        createCell(row, 2, "Categories", cellStyle);
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

    public void export(HttpServletResponse response, List<Brand> brands) {
        super.setResponseHeader(response, "application/octet-stream", ".xlsx");

        writeHeaderLine();
        writeDateLines(brands);

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeDateLines(List<Brand> brands) {
        int rowIndex = 1;

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        cellStyle.setFont(font);

        for (Brand brand : brands) {
            XSSFRow row = sheet.createRow(rowIndex++);
            int columnIndex = 0;
            createCell(row, columnIndex++, brand.getId(), cellStyle);
            createCell(row, columnIndex++, brand.getName(), cellStyle);
            createCell(row, columnIndex++, brand.getCategories()
                    .stream().map(Category::getName)
                    .collect(Collectors.toList())
                    .toString(), cellStyle);
            createCell(row, columnIndex, brand.isEnabled(), cellStyle);
        }

    }
}