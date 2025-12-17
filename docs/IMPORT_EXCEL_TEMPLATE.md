# 📋 Hướng dẫn thêm Import Excel cho Entity mới

## 🎯 Tổng quan
Hướng dẫn này giúp bạn thêm chức năng import Excel cho bất kỳ entity nào trong hệ thống BlueMoonManagement.

## 📝 Quy trình thực hiện

### **Bước 1: Tạo file Excel mẫu**
```
📁 src/main/java/io/github/ktpm/bluemoonmanagement/service/template/
   📄 DanhSach[TenEntity].xlsx
```

**Format file Excel:**
- Dòng 1: Header (tên cột)
- Dòng 2+: Dữ liệu mẫu
- Lưu định dạng `.xlsx`

### **Bước 2: Copy file mẫu vào resources**
```powershell
Copy-Item "src\main\java\io\github\ktpm\bluemoonmanagement\service\template\DanhSach[TenEntity].xlsx" "src\main\resources\templates\" -Force
```

### **Bước 3: Thêm method importFromExcel trong Service**

#### **Template code cho Service Implementation:**

```java
@Override
public ResponseDto importFromExcel(MultipartFile file) {
    // Kiểm tra quyền (thay đổi role phù hợp)
    if (Session.getCurrentUser() == null || !"Tổ phó".equals(Session.getCurrentUser().getVaiTro())) {
        return new ResponseDto(false, "Bạn không có quyền nhập Excel [tên entity]. Chỉ [role] mới được phép.");
    }
    
    try {
        // Tạo temp file
        File tempFile = File.createTempFile("[entity]_temp", ".xlsx");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(file.getBytes());
        }
        
        // Row mapper - map mỗi dòng Excel thành DTO
        Function<Row, [EntityDto]> rowMapper = row -> {
            try {
                [EntityDto] dto = new [EntityDto]();
                
                // Map từng column (index bắt đầu từ 0)
                dto.setField1(row.getCell(0).getStringCellValue());
                dto.setField2(row.getCell(1).getStringCellValue());
                
                // Xử lý numeric field
                dto.setNumericField((int) row.getCell(2).getNumericCellValue());
                
                // Xử lý date field
                if (row.getCell(3) != null) {
                    dto.setDateField(row.getCell(3).getDateCellValue()
                        .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
                }
                
                // Xử lý boolean field
                try {
                    dto.setBooleanField(row.getCell(4).getBooleanCellValue());
                } catch (Exception e) {
                    String boolStr = row.getCell(4).getStringCellValue().toLowerCase();
                    dto.setBooleanField("true".equals(boolStr) || "có".equals(boolStr) || "1".equals(boolStr));
                }
                
                return dto;
            } catch (Exception e) {
                System.err.println("Lỗi khi đọc dòng Excel [entity]: " + e.getMessage());
                return null; // Skip invalid rows
            }
        };
        
        // Import và save
        List<[EntityDto]> dtoList = XlxsFileUtil.importFromExcel(tempFile.getAbsolutePath(), rowMapper);
        List<[Entity]> entityList = dtoList.stream()
                .filter(dto -> dto != null) // Lọc bỏ các dòng lỗi
                .map([entity]Mapper::from[EntityDto])
                .collect(Collectors.toList());
                
        [entity]Repository.saveAll(entityList);
        tempFile.delete();
        
        return new ResponseDto(true, "Thêm [entity] thành công " + entityList.size() + " bản ghi");
    } catch (Exception e) {
        return new ResponseDto(false, "Thêm [entity] thất bại: " + e.getMessage());
    }
}
```

### **Bước 4: Thêm method trong Service Interface**
```java
public interface [Entity]Service {
    // ... existing methods ...
    ResponseDto importFromExcel(MultipartFile file);
}
```

### **Bước 5: Thêm handler trong Controller**

#### **Template code cho Controller:**

```java
@FXML
private void handleNhapExcel[Entity](javafx.event.ActionEvent event) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Chọn file Excel để nhập [entity]");
    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls")
    );
    
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    File selectedFile = fileChooser.showOpenDialog(stage);
    
    if (selectedFile != null) {
        try {
            MultipartFile multipartFile = FileMultipartUtil.convertToMultipartFile(selectedFile);
            ResponseDto response = [entity]Service.importFromExcel(multipartFile);
            
            if (response.isSuccess()) {
                showAlert("Thành công", response.getMessage(), Alert.AlertType.INFORMATION);
                // Refresh data after import
                load[Entity]Data(); // Phương thức refresh dữ liệu
            } else {
                showAlert("Lỗi", response.getMessage(), Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            showAlert("Lỗi", "Lỗi khi xử lý file: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
```

### **Bước 6: Kết nối với nút trong FXML**

#### **Thêm nút trong file .fxml:**
```xml
<Button fx:id="btnNhapExcel[Entity]" onAction="#handleNhapExcel[Entity]" 
        text="Nhập Excel" styleClass="button-primary"/>
```

#### **Declare trong Controller:**
```java
@FXML
private Button btnNhapExcel[Entity];
```

### **Bước 7: Thêm quyền phù hợp**

**Quy tắc phân quyền hiện tại:**
- **Tổ phó**: Quản lý dữ liệu tòa nhà (căn hộ, cư dân, phương tiện...)
- **Kế toán**: Quản lý dữ liệu tài chính (hóa đơn, khoản thu, tài khoản...)

## 🔧 Checklist triển khai

- [ ] Tạo file Excel mẫu với đúng format
- [ ] Copy file mẫu vào resources/templates
- [ ] Thêm importFromExcel method trong Service Implementation
- [ ] Thêm method declaration trong Service Interface  
- [ ] Thêm handler method trong Controller
- [ ] Thêm nút trong FXML file
- [ ] Declare nút trong Controller
- [ ] Set đúng quyền cho role phù hợp
- [ ] Test import với dữ liệu mẫu
- [ ] Test xử lý lỗi (file sai format, quyền, etc.)

## 📋 Ví dụ cụ thể

### **Entity: PhuongTien (Phương tiện)**

**File mẫu: DanhSachPhuongTien.xlsx**
```
| Biển số | Loại xe | Màu sắc | Mã căn hộ | Ghi chú |
|---------|---------|---------|-----------|---------|
| 30A-123 | Ô tô    | Đỏ      | CH001     | Xe mới  |
```

**Service method:**
```java
Function<Row, PhuongTienDto> rowMapper = row -> {
    try {
        PhuongTienDto dto = new PhuongTienDto();
        dto.setBienSo(row.getCell(0).getStringCellValue());
        dto.setLoaiXe(row.getCell(1).getStringCellValue());
        dto.setMauSac(row.getCell(2).getStringCellValue());
        dto.setMaCanHo(row.getCell(3).getStringCellValue());
        dto.setGhiChu(row.getCell(4).getStringCellValue());
        return dto;
    } catch (Exception e) {
        return null;
    }
};
```

**Controller handler:**
```java
@FXML
private void handleNhapExcelPhuongTien(javafx.event.ActionEvent event) {
    // Same template as above
}
```

## 🚀 Kết quả
Sau khi hoàn thành các bước trên, bạn sẽ có:
- ✅ Nút "Nhập Excel" hoạt động cho entity mới
- ✅ File template để người dùng download
- ✅ Xử lý lỗi đầy đủ (null values, wrong format, permissions)
- ✅ Phân quyền theo role
- ✅ Refresh dữ liệu sau khi import thành công 