package com.mahim.shopme.admin.user;

import com.mahim.shopme.common.entity.User;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class UserCsvExporter {

    public void export(List<User> users, HttpServletResponse response) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timestamp = dateFormat.format(new Date());
        String fileName = "users_" + timestamp + ".csv";

        response.setContentType("text/csv");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + fileName;
        response.setHeader(headerKey, headerValue);

        String[] header = { "User ID", "Email", "First Name", "Last Name", "Roles", "Enabled" };
        String[] fieldMapping = { "id", "email", "firstName", "lastName", "roles", "enabled" };

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        csvWriter.writeHeader(header);
        for (User user: users) {
            csvWriter.write(user, fieldMapping);
        }

        csvWriter.flush();
    }

    public void exportCommonsCsv(List<User> users, HttpServletResponse response) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timestamp = dateFormat.format(new Date());
        String fileName = "users_" + timestamp + ".csv";

        response.setContentType("text/csv");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + fileName;
        response.setHeader(headerKey, headerValue);

        String[] header = { "User ID", "Email", "First Name", "Last Name", "Roles", "Enabled" };
        CSVPrinter csvPrinter = new CSVPrinter(response.getWriter(), CSVFormat.DEFAULT.withHeader(header));

        for (User user: users) {
            csvPrinter.printRecord(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getRoles(), user.isEnabled());
        }

        csvPrinter.flush();
    }

    public void exportOpenCsv(List<User> users, HttpServletResponse response) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timestamp = dateFormat.format(new Date());
        String fileName = "users_" + timestamp + ".csv";

        response.setContentType("text/csv");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + fileName;
        response.setHeader(headerKey, headerValue);

        String[] header = { "User ID", "Email", "First Name", "Last Name", "Roles", "Enabled" };
        String[] fieldMapping = { "id", "email", "firstName", "lastName", "roles", "enabled" };

        CSVWriter headerWriter = new CSVWriter(response.getWriter(),
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);
        headerWriter.writeNext(header);

        ColumnPositionMappingStrategy strategy = new ColumnPositionMappingStrategy();
        strategy.setType(User.class);
        strategy.setColumnMapping(fieldMapping);

        StatefulBeanToCsv<User> csvWriter = new StatefulBeanToCsvBuilder(response.getWriter())
                .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                .withMappingStrategy(strategy)
                .build();
        csvWriter.write(users);
    }
}
