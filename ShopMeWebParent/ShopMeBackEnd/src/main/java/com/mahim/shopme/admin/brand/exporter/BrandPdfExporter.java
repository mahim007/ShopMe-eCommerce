package com.mahim.shopme.admin.brand.exporter;

import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.mahim.shopme.common.entity.Brand;
import com.mahim.shopme.common.entity.Category;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrandPdfExporter extends AbstractExporter {
    public void export(HttpServletResponse response, List<Brand> brands) {
        super.setResponseHeader(response, "application/pdf", ".pdf");

        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document,response.getOutputStream());
            document.open();

            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            font.setSize(18);
            font.setColor(Color.BLUE);

            Paragraph paragraph = new Paragraph("List of brands", font);
            paragraph.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(paragraph);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100f);
            table.setSpacingBefore(10);
            table.setWidths(new float[] {1.2f, 3.7f, 3.0f, 1.5f});

            writeTableHeader(table);
            writeTableData(table, brands);
            document.add(table);

        } catch (IOException e) {
            System.out.println("error occurred: {}" + e.getMessage());
        }

        document.close();
    }

    private void writeTableData(PdfPTable table, List<Brand> brands) {
        for (Brand brand : brands) {
            table.addCell(String.valueOf(brand.getId()));
            table.addCell(brand.getName());
            table.addCell(brand.getCategories().stream().map(Category::getName).collect(Collectors.toList()).toString());
            table.addCell(String.valueOf(brand.isEnabled()));
        }

    }

    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.BLUE);
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("ID", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Name", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Categories", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Enabled", font));
        table.addCell(cell);
    }
}
