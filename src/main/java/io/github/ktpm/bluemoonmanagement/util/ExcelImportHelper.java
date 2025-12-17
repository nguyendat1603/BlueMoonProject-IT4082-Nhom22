package io.github.ktpm.bluemoonmanagement.util;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.web.multipart.MultipartFile;
import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;
import io.github.ktpm.bluemoonmanagement.session.Session;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 🔧 Excel Import Helper
 * 
 * Utility class để hỗ trợ tạo import Excel cho các entity mới
 * Dựa trên pattern chuẩn từ CSDL hiện có
 */
public class ExcelImportHelper {

    /**
     * Template method cho import Excel
     * 
     * @param file MultipartFile từ FileChooser
     * @param requiredRole Role yêu cầu ("Tổ phó" hoặc "Kế toán")
     * @param entityName Tên entity để hiển thị trong thông báo
     * @param rowMapper Function để map từ Row Excel sang DTO
     * @param entityMapper Function để map từ DTO sang Entity
     * @param repository Repository để save entity
     * @return ResponseDto kết quả
     */
    public static <T, E> ResponseDto importFromExcel(
            MultipartFile file,
            String requiredRole,
            String entityName,
            Function<Row, T> rowMapper,
            Function<T, E> entityMapper,
            org.springframework.data.jpa.repository.JpaRepository<E, ?> repository) {
        
        // Kiểm tra quyền
        if (Session.getCurrentUser() == null || !requiredRole.equals(Session.getCurrentUser().getVaiTro())) {
            return new ResponseDto(false, "Bạn không có quyền nhập Excel " + entityName + ". Chỉ " + requiredRole + " mới được phép.");
        }
        
        try {
            // Tạo temp file
            File tempFile = File.createTempFile(entityName.toLowerCase() + "_temp", ".xlsx");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(file.getBytes());
            }
            
            // Import và convert
            List<T> dtoList = XlxsFileUtil.importFromExcel(tempFile.getAbsolutePath(), rowMapper);
            List<E> entityList = dtoList.stream()
                    .filter(dto -> dto != null) // Lọc bỏ các dòng lỗi
                    .map(entityMapper)
                    .collect(Collectors.toList());
            
            // Save vào database
            repository.saveAll(entityList);
            tempFile.delete();
            
            return new ResponseDto(true, "Thêm " + entityName + " thành công " + entityList.size() + " bản ghi");
        } catch (Exception e) {
            return new ResponseDto(false, "Thêm " + entityName + " thất bại: " + e.getMessage());
        }
    }

    /**
     * 🔧 Helper methods để đọc dữ liệu từ Excel cell an toàn
     */
    public static class CellReader {
        
        public static String getString(Row row, int cellIndex) {
            try {
                return row.getCell(cellIndex) != null ? row.getCell(cellIndex).getStringCellValue() : "";
            } catch (Exception e) {
                return "";
            }
        }
        
        public static int getInt(Row row, int cellIndex) {
            try {
                return (int) row.getCell(cellIndex).getNumericCellValue();
            } catch (Exception e) {
                return 0;
            }
        }
        
        public static double getDouble(Row row, int cellIndex) {
            try {
                return row.getCell(cellIndex).getNumericCellValue();
            } catch (Exception e) {
                return 0.0;
            }
        }
        
        public static boolean getBoolean(Row row, int cellIndex) {
            try {
                return row.getCell(cellIndex).getBooleanCellValue();
            } catch (Exception e) {
                // Thử đọc như string và parse
                try {
                    String boolStr = row.getCell(cellIndex).getStringCellValue().toLowerCase();
                    return "true".equals(boolStr) || "có".equals(boolStr) || "1".equals(boolStr);
                } catch (Exception ex) {
                    return false;
                }
            }
        }
        
        public static LocalDate getDate(Row row, int cellIndex) {
            try {
                return row.getCell(cellIndex).getDateCellValue()
                    .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            } catch (Exception e) {
                return null;
            }
        }
    }
} 