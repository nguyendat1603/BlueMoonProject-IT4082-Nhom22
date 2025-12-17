package io.github.ktpm.bluemoonmanagement.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;

public class XlsxExportUtil {
    /**
     * Exports a list of objects to an Excel file (.xlsx).
     * @param filePath Path to save the Excel file
     * @param headers Array of column headers
     * @param data List of objects to export
     * @param rowWriter Function to write object fields to a Row (row, object)
     * @throws IOException if file cannot be written
     */
    public static <T> void exportToExcel(String filePath, String[] headers, List<T> data, BiConsumer<Row, T> rowWriter) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet 1");
            int rowIdx = 0;

            // Write header
            Row headerRow = sheet.createRow(rowIdx++);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Write data rows
            for (T item : data) {
                Row row = sheet.createRow(rowIdx++);
                rowWriter.accept(row, item);
            }

            // Autosize columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
        }
    }
}
