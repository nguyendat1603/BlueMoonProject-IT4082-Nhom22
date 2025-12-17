package io.github.ktpm.bluemoonmanagement.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class XlxsFileUtil<T> {
    /**
     * Reads an Excel file and maps each row to an object of type T using the provided rowMapper.
     * @param filePath Path to the Excel file (.xlsx)
     * @param rowMapper Function to map a Row to an object of type T
     * @return List of objects of type T
     * @throws IOException if file cannot be read
     */
    public static <T> List<T> importFromExcel(String filePath, Function<Row, T> rowMapper) throws IOException {
        List<T> resultList = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean isFirstRow = true;
            for (Row row : sheet) {
                // Skip header row if needed
                if (isFirstRow) {
                    isFirstRow = false;
                    continue;
                }
                T obj = rowMapper.apply(row);
                if (obj != null) {
                    resultList.add(obj);
                }
            }
        }
        return resultList;
    }
}
