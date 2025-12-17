package io.github.ktpm.bluemoonmanagement.util;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.web.multipart.MultipartFile;
import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;
import io.github.ktpm.bluemoonmanagement.session.Session;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Template utility class để giúp việc tạo import Excel cho entity mới
 * Sử dụng class này như một reference/template để copy code
 */
public class ImportExcelTemplate {

    /**
     * Template method cho import Excel - COPY CODE NÀY VÀO SERVICE IMPLEMENTATION
     * 
     * @param file File Excel được upload
     * @param requiredRole Role cần thiết để import ("Tổ phó" hoặc "Kế toán")
     * @param entityName Tên entity để hiển thị trong message
     * @param rowMapper Function để map Row thành DTO
     * @param dtoToEntityMapper Function để convert DTO thành Entity
     * @param repositorySaveMethod Function để save list entities
     * @param <D> DTO type
     * @param <E> Entity type
     * @return ResponseDto với kết quả import
     */
    public static <D, E> ResponseDto importExcelTemplate(
            MultipartFile file,
            String requiredRole,
            String entityName,
            Function<Row, D> rowMapper,
            Function<D, E> dtoToEntityMapper,
            Function<List<E>, List<E>> repositorySaveMethod) {
        
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
            List<D> dtoList = XlxsFileUtil.importFromExcel(tempFile.getAbsolutePath(), rowMapper);
            List<E> entityList = dtoList.stream()
                    .filter(dto -> dto != null) // Lọc bỏ các dòng lỗi
                    .map(dtoToEntityMapper)
                    .collect(Collectors.toList());
            
            // Save to database
            repositorySaveMethod.apply(entityList);
            tempFile.delete();
            
            return new ResponseDto(true, "Thêm " + entityName + " thành công " + entityList.size() + " bản ghi");
        } catch (Exception e) {
            return new ResponseDto(false, "Thêm " + entityName + " thất bại: " + e.getMessage());
        }
    }

    /**
     * Template cho Row Mapper - xử lý từng dòng Excel
     * COPY VÀ CUSTOMIZE CODE NÀY TRONG SERVICE
     */
    public static class RowMapperExamples {
        
        /**
         * Ví dụ row mapper cho entity có các field cơ bản
         */
        public static Function<Row, Object> basicRowMapper() {
            return row -> {
                try {
                    // CustomDto dto = new CustomDto();
                    
                    // String fields
                    // dto.setStringField(row.getCell(0).getStringCellValue());
                    
                    // Numeric fields
                    // dto.setIntField((int) row.getCell(1).getNumericCellValue());
                    // dto.setDoubleField(row.getCell(2).getNumericCellValue());
                    
                    // Date fields
                    // if (row.getCell(3) != null) {
                    //     dto.setDateField(row.getCell(3).getDateCellValue()
                    //         .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
                    // }
                    
                    // Boolean fields với xử lý linh hoạt
                    // try {
                    //     dto.setBooleanField(row.getCell(4).getBooleanCellValue());
                    // } catch (Exception e) {
                    //     String boolStr = row.getCell(4).getStringCellValue().toLowerCase();
                    //     dto.setBooleanField("true".equals(boolStr) || "có".equals(boolStr) || "1".equals(boolStr));
                    // }
                    
                    // return dto;
                    return null; // Placeholder
                } catch (Exception e) {
                    System.err.println("Lỗi khi đọc dòng Excel: " + e.getMessage());
                    return null; // Skip invalid rows
                }
            };
        }
    }

    /**
     * Template cho Controller Handler - COPY CODE NÀY VÀO CONTROLLER
     */
    public static class ControllerHandlerTemplate {
        
        public static String getHandlerTemplate(String entityName) {
            return String.format("""
                @FXML
                private void handleNhapExcel%s(javafx.event.ActionEvent event) {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Chọn file Excel để nhập %s");
                    fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls")
                    );
                    
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    File selectedFile = fileChooser.showOpenDialog(stage);
                    
                    if (selectedFile != null) {
                        try {
                            MultipartFile multipartFile = FileMultipartUtil.convertToMultipartFile(selectedFile);
                            ResponseDto response = %sService.importFromExcel(multipartFile);
                            
                            if (response.isSuccess()) {
                                showAlert("Thành công", response.getMessage(), Alert.AlertType.INFORMATION);
                                // Refresh data after import
                                load%sData(); // Thay đổi method name phù hợp
                            } else {
                                showAlert("Lỗi", response.getMessage(), Alert.AlertType.ERROR);
                            }
                        } catch (Exception e) {
                            showAlert("Lỗi", "Lỗi khi xử lý file: " + e.getMessage(), Alert.AlertType.ERROR);
                        }
                    }
                }
                """, 
                entityName, 
                entityName.toLowerCase(), 
                entityName.toLowerCase(), 
                entityName);
        }
    }

    /**
     * Helper methods cho việc xử lý Excel cells
     */
    public static class ExcelCellHelpers {
        
        /**
         * Đọc string cell an toàn
         */
        public static String getStringCellValue(Row row, int cellIndex) {
            try {
                return row.getCell(cellIndex) != null ? row.getCell(cellIndex).getStringCellValue() : "";
            } catch (Exception e) {
                return "";
            }
        }
        
        /**
         * Đọc numeric cell an toàn
         */
        public static double getNumericCellValue(Row row, int cellIndex) {
            try {
                return row.getCell(cellIndex) != null ? row.getCell(cellIndex).getNumericCellValue() : 0.0;
            } catch (Exception e) {
                return 0.0;
            }
        }
        
        /**
         * Đọc boolean cell linh hoạt
         */
        public static boolean getBooleanCellValue(Row row, int cellIndex) {
            try {
                return row.getCell(cellIndex).getBooleanCellValue();
            } catch (Exception e) {
                try {
                    String boolStr = row.getCell(cellIndex).getStringCellValue().toLowerCase();
                    return "true".equals(boolStr) || "có".equals(boolStr) || "1".equals(boolStr);
                } catch (Exception ex) {
                    return false;
                }
            }
        }
        
        /**
         * Đọc date cell an toàn
         */
        public static java.time.LocalDate getDateCellValue(Row row, int cellIndex) {
            try {
                if (row.getCell(cellIndex) != null) {
                    return row.getCell(cellIndex).getDateCellValue()
                        .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                }
                return null;
            } catch (Exception e) {
                return null;
            }
        }
    }
} 