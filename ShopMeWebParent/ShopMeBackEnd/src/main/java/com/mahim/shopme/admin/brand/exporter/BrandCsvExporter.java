package com.mahim.shopme.admin.brand.exporter;

import com.mahim.shopme.common.entity.Brand;
import com.mahim.shopme.common.entity.Category;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
public class BrandCsvExporter extends AbstractExporter {

    public void export(HttpServletResponse response, List<Brand> brands) throws IOException {
        super.setResponseHeader(response, "text/csv", ".csv");

        String[] header = { "ID", "Name", "Categories" , "Enabled" };
        String[] fieldMapping = { "id", "name", "categories", "enabled" };

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        csvWriter.writeHeader(header);
        for (Brand brand: brands) {
            csvWriter.write(brand, fieldMapping);
        }

        csvWriter.flush();
    }

    public void exportCommonsCsv(List<Brand> brands, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "text/csv", ".csv");

        String[] header = { "ID", "Name", "Alias", "Enabled" };
        CSVPrinter csvPrinter = new CSVPrinter(response.getWriter(), CSVFormat.DEFAULT.withHeader(header));

        for (Brand brand: brands) {
            csvPrinter.printRecord(brand.getId(),  brand.getName(), brand.isEnabled());
        }

        csvPrinter.flush();
    }

    public void exportOpenCsv(List<Category> categories, HttpServletResponse response) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        super.setResponseHeader(response, "text/csv", ".csv");

        String[] header = { "ID", "Name", "Alias", "Enabled" };
        String[] fieldMapping = { "id", "name", "alias", "enabled" };

        CSVWriter headerWriter = new CSVWriter(response.getWriter(),
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);
        headerWriter.writeNext(header);

        ColumnPositionMappingStrategy strategy = new ColumnPositionMappingStrategy();
        strategy.setType(Category.class);
        strategy.setColumnMapping(fieldMapping);

        StatefulBeanToCsv<Category> csvWriter = new StatefulBeanToCsvBuilder(response.getWriter())
                .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                .withMappingStrategy(strategy)
                .build();
        csvWriter.write(categories);
    }
}
