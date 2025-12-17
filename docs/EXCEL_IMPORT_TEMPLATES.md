# 📊 Excel Import Templates - Dựa trên CSDL

## 🎯 Template cho các Entity hiện có

### **1. 🏠 CanHo (Căn hộ)**

**📄 File Excel Format: DanhSachCanHo.xlsx**
```
| ma_can_ho | toa_nha | tang | so_nha | dien_tich | da_ban_chua | trang_thai_ki_thuat | trang_thai_su_dung |
|-----------|---------|------|--------|-----------|-------------|--------------------|--------------------|
| CH001     | A       | 1    | 101    | 75.5      | true        | Tốt                 | Đang sử dụng       |
```

**Service Implementation:**
```java
@Override
public ResponseDto importFromExcel(MultipartFile file) {
    return ExcelImportHelper.importFromExcel(
        file,
        "Tổ phó", // Required role
        "căn hộ",  // Entity name
        row -> {   // Row mapper
            try {
                CanHoDto dto = new CanHoDto();
                dto.setMaCanHo(ExcelImportHelper.CellReader.getString(row, 0));
                dto.setToaNha(ExcelImportHelper.CellReader.getString(row, 1));
                dto.setTang(ExcelImportHelper.CellReader.getString(row, 2));
                dto.setSoNha(ExcelImportHelper.CellReader.getString(row, 3));
                dto.setDienTich(ExcelImportHelper.CellReader.getDouble(row, 4));
                dto.setDaBanChua(ExcelImportHelper.CellReader.getBoolean(row, 5));
                dto.setTrangThaiKiThuat(ExcelImportHelper.CellReader.getString(row, 6));
                dto.setTrangThaiSuDung(ExcelImportHelper.CellReader.getString(row, 7));
                dto.setChuHo(null); // Set null, không import chủ hộ từ Excel
                return dto;
            } catch (Exception e) {
                System.err.println("Lỗi khi đọc dòng Excel căn hộ: " + e.getMessage());
                return null;
            }
        },
        canHoMapper::fromCanHoDto, // Entity mapper
        canHoRepository           // Repository
    );
}
```

---

### **2. 👤 CuDan (Cư dân)**

**📄 File Excel Format: DanhSachCuDan.xlsx**
```
| ma_dinh_danh | ho_va_ten | gioi_tinh | ngay_sinh  | so_dien_thoai | email         | trang_thai_cu_tru | ngay_chuyen_den | ngay_chuyen_di | ma_can_ho |
|--------------|-----------|-----------|------------|---------------|---------------|--------------------|-----------------|----------------|-----------|
| 001234567890 | Nguyễn A  | Nam       | 1990-01-01 | 0123456789    | a@email.com   | Cư trú            | 2024-01-01      |                | CH001     |
```

**Service Implementation:**
```java
@Override
public ResponseDto importFromExcel(MultipartFile file) {
    return ExcelImportHelper.importFromExcel(
        file,
        "Tổ phó",
        "cư dân",
        row -> {
            try {
                CudanDto dto = new CudanDto();
                dto.setMaDinhDanh(ExcelImportHelper.CellReader.getString(row, 0));
                dto.setHoVaTen(ExcelImportHelper.CellReader.getString(row, 1));
                dto.setGioiTinh(ExcelImportHelper.CellReader.getString(row, 2));
                dto.setNgaySinh(ExcelImportHelper.CellReader.getDate(row, 3));
                dto.setSoDienThoai(ExcelImportHelper.CellReader.getString(row, 4));
                dto.setEmail(ExcelImportHelper.CellReader.getString(row, 5));
                dto.setTrangThaiCuTru(ExcelImportHelper.CellReader.getString(row, 6));
                dto.setNgayChuyenDen(ExcelImportHelper.CellReader.getDate(row, 7));
                dto.setNgayChuyenDi(ExcelImportHelper.CellReader.getDate(row, 8));
                dto.setMaCanHo(ExcelImportHelper.CellReader.getString(row, 9));
                return dto;
            } catch (Exception e) {
                System.err.println("Lỗi khi đọc dòng Excel cư dân: " + e.getMessage());
                return null;
            }
        },
        dto -> {
            CuDan cuDan = cuDanMapper.fromCudanDto(dto);
            // Set CanHo reference if maCanHo provided
            if (dto.getMaCanHo() != null && !dto.getMaCanHo().trim().isEmpty()) {
                CanHo canHo = canHoRepository.findById(dto.getMaCanHo()).orElse(null);
                cuDan.setCanHo(canHo);
            }
            return cuDan;
        },
        cuDanRepository
    );
}
```

---

### **3. 💰 KhoanThu (Khoản thu)**

**📄 File Excel Format: DanhSachKhoanThu.xlsx**
```
| ma_khoan_thu | ten_khoan_thu | bat_buoc | don_vi_tinh | so_tien | pham_vi | ngay_tao   | thoi_han   | ghi_chu     |
|--------------|---------------|----------|-------------|---------|---------|------------|------------|-------------|
| KT001        | Phí quản lý   | true     | Căn hộ      | 50000   | Tòa A   | 2024-01-01 | 2024-12-31 | Bắt buộc    |
```

**Service Implementation:**
```java
@Override
public ResponseDto importFromExcel(MultipartFile file) {
    return ExcelImportHelper.importFromExcel(
        file,
        "Kế toán",
        "khoản thu", 
        row -> {
            try {
                KhoanThuDto dto = new KhoanThuDto();
                dto.setMaKhoanThu(ExcelImportHelper.CellReader.getString(row, 0));
                dto.setTenKhoanThu(ExcelImportHelper.CellReader.getString(row, 1));
                dto.setBatBuoc(ExcelImportHelper.CellReader.getBoolean(row, 2));
                dto.setDonViTinh(ExcelImportHelper.CellReader.getString(row, 3));
                dto.setSoTien(ExcelImportHelper.CellReader.getInt(row, 4));
                dto.setPhamVi(ExcelImportHelper.CellReader.getString(row, 5));
                dto.setNgayTao(ExcelImportHelper.CellReader.getDate(row, 6));
                dto.setThoiHan(ExcelImportHelper.CellReader.getDate(row, 7));
                dto.setGhiChu(ExcelImportHelper.CellReader.getString(row, 8));
                dto.setPhiGuiXeList(new ArrayList<>()); // Set empty list
                return dto;
            } catch (Exception e) {
                System.err.println("Lỗi khi đọc dòng Excel khoản thu: " + e.getMessage());
                return null;
            }
        },
        khoanThuMapper::fromKhoanThuDto,
        khoanThuRepository
    );
}
```

---

### **4. 🚗 PhuongTien (Phương tiện)**

**📄 File Excel Format: DanhSachPhuongTien.xlsx**
```
| loai_phuong_tien | bien_so | ngay_dang_ky | ngay_huy_dang_ky | ma_can_ho |
|------------------|---------|--------------|------------------|-----------|
| Ô tô             | 30A-123 | 2024-01-01   |                  | CH001     |
| Xe máy           | 30B-456 | 2024-01-01   | 2024-06-01       | CH002     |
```

**Service Implementation:**
```java
@Override
public ResponseDto importFromExcel(MultipartFile file) {
    if (Session.getCurrentUser() == null || !"Tổ phó".equals(Session.getCurrentUser().getVaiTro())) {
        return new ResponseDto(false, "Bạn không có quyền nhập Excel phương tiện. Chỉ Tổ phó mới được phép.");
    }
    
    try {
        File tempFile = File.createTempFile("phuongtien_temp", ".xlsx");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(file.getBytes());
        }
        
        Function<Row, PhuongTienDto> rowMapper = row -> {
            try {
                PhuongTienDto dto = new PhuongTienDto();
                dto.setLoaiPhuongTien(ExcelImportHelper.CellReader.getString(row, 0));
                dto.setBienSo(ExcelImportHelper.CellReader.getString(row, 1));
                dto.setNgayDangKy(ExcelImportHelper.CellReader.getDate(row, 2));
                dto.setNgayHuyDangKy(ExcelImportHelper.CellReader.getDate(row, 3));
                dto.setMaCanHo(ExcelImportHelper.CellReader.getString(row, 4));
                return dto;
            } catch (Exception e) {
                System.err.println("Lỗi khi đọc dòng Excel phương tiện: " + e.getMessage());
                return null;
            }
        };
        
        List<PhuongTienDto> dtoList = XlxsFileUtil.importFromExcel(tempFile.getAbsolutePath(), rowMapper);
        List<PhuongTien> entityList = dtoList.stream()
                .filter(dto -> dto != null)
                .map(dto -> {
                    PhuongTien entity = phuongTienMapper.fromPhuongTienDto(dto);
                    // Set CanHo reference
                    if (dto.getMaCanHo() != null && !dto.getMaCanHo().trim().isEmpty()) {
                        CanHo canHo = canHoRepository.findById(dto.getMaCanHo()).orElse(null);
                        entity.setCanHo(canHo);
                    }
                    return entity;
                })
                .collect(Collectors.toList());
                
        phuongTienRepository.saveAll(entityList);
        tempFile.delete();
        
        return new ResponseDto(true, "Thêm phương tiện thành công " + entityList.size() + " bản ghi");
    } catch (Exception e) {
        return new ResponseDto(false, "Thêm phương tiện thất bại: " + e.getMessage());
    }
}
```

---

## 🔧 Quy trình triển khai cho Entity mới

### **Bước 1: Tạo file Excel mẫu**
1. Tạo file trong `src/main/java/io/github/ktpm/bluemoonmanagement/service/template/`
2. Đặt tên: `DanhSach[TenEntity].xlsx`
3. Header row = tên các column trong database
4. Thêm 1-2 dòng dữ liệu mẫu

### **Bước 2: Copy vào resources**
```powershell
Copy-Item "src\main\java\io\github\ktpm\bluemoonmanagement\service\template\DanhSach[Entity].xlsx" "src\main\resources\templates\" -Force
```

### **Bước 3: Implement importFromExcel**
- Sử dụng `ExcelImportHelper.importFromExcel()` cho case đơn giản
- Hoặc implement thủ công nếu cần logic đặc biệt (như set relationship)

### **Bước 4: Thêm vào Service Interface**
```java
ResponseDto importFromExcel(MultipartFile file);
```

### **Bước 5: Thêm handler trong Controller**
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
                load[Entity]Data(); // Refresh data
            } else {
                showAlert("Lỗi", response.getMessage(), Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            showAlert("Lỗi", "Lỗi khi xử lý file: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
```

### **Bước 6: Kết nối FXML**
```xml
<Button fx:id="btnNhapExcel[Entity]" onAction="#handleNhapExcel[Entity]" 
        text="Nhập Excel" styleClass="button-primary"/>
```

## ✅ Checklist hoàn thành
- [ ] File Excel mẫu với đúng tên cột database
- [ ] Copy file vào resources/templates
- [ ] Import method trong Service  
- [ ] Handler trong Controller
- [ ] Button trong FXML
- [ ] Test với dữ liệu mẫu
- [ ] Test xử lý lỗi (null, sai format, quyền)

**Quy tắc phân quyền:**
- **Tổ phó**: CanHo, CuDan, PhuongTien
- **Kế toán**: KhoanThu, HoaDon, TaiKhoan 

## Template Excel Import

Dưới đây là danh sách các template để import dữ liệu vào hệ thống:

### 1. **Căn Hộ (CanHo)** - 8 cột
```
| Mã căn hộ | Tòa nhà | Tầng | Số nhà | Diện tích | Đã bán/chưa | Trạng thái kỹ thuật | Trạng thái sử dụng |
| CH001     | A       | 1    | 101    | 75.5      | true        | Tốt                 | Đang sử dụng       |
```

### 2. **Cư Dân (CuDan)** - 10 cột  
```
| Mã định danh | Họ và tên     | Giới tính | Ngày sinh  | Số điện thoại | Email            | Trạng thái cư trú | Ngày chuyển đến | Ngày chuyển đi | Mã căn hộ |
| CD001        | Nguyễn Văn A  | Nam       | 1990-01-01 | 0987654321    | nva@email.com    | Đang cư trú       | 2023-01-01      |                | CH001     |
```

### 3. **Khoản Thu (KhoanThu)** - 9 cột
```
| Mã khoản thu | Tên khoản thu | Bắt buộc | Đơn vị tính | Số tiền | Phạm vi      | Ngày tạo   | Thời hạn   | Ghi chú      |
| KT001        | Phí quản lý   | true     | Căn hộ      | 500000  | Theo căn hộ  | 2023-01-01 | 2023-12-31 | Ban quản lý  |
```

### 4. **Phương Tiện (PhuongTien)** - 5 cột
```
| Loại phương tiện | Biển số   | Ngày đăng ký | Ngày hủy đăng ký | Mã căn hộ |
| Xe máy           | 30A-12345 | 2023-01-01   |                  | CH001     |
```

### 5. **Hóa Đơn (HoaDon)** - 3 cột
```
| Tên khoản thu | Mã căn hộ | Số tiền |
| Tiền điện     | CH001     | 150000  |
| Tiền nước     | CH001     | 80000   |
| Internet      | CH002     | 200000  |
```

⚠️ **Lưu ý**: Mã hóa đơn được tự sinh, KHÔNG cần trong file Excel

## Notes:

### **Hóa Đơn Import:**
- File phải có **3 cột chính**: Tên khoản thu, Mã căn hộ, Số tiền
- **Tự động tạo KhoanThu**: Nếu "Tên khoản thu" chưa tồn tại, hệ thống sẽ tự tạo với:
  - Loại: Tự nguyện  
  - Đơn vị tính: "Theo hóa đơn"
  - Bộ phận quản lý: "Bên thứ 3"
  - Thời hạn: 1 tháng từ ngày import
- **Validation**: Kiểm tra mã căn hộ có tồn tại, số tiền > 0
- **Trạng thái**: Hóa đơn mặc định chưa thanh toán

### **Căn Hộ Import:**
- **"Đã bán/chưa"**: Dùng `true`/`false`, `có`/`không`, hoặc `1`/`0`
- **Mã căn hộ**: Phải unique, không được trùng

### **Cư Dân Import:**  
- **Mã căn hộ**: Có thể null/trống nếu chưa có căn hộ
- **Ngày tháng**: Format `YYYY-MM-DD` (ví dụ: 2023-01-01)
- **Giới tính**: "Nam" hoặc "Nữ"

### **Khoản Thu Import:**
- **"Bắt buộc"**: Dùng `true`/`false` cho required/optional
- **"Đơn vị tính"**: "Căn hộ", "Phương tiện", "Theo hóa đơn"
- **"Phạm vi"**: "Theo căn hộ", "Toàn tòa nhà"

### **Phương Tiện Import:**
- **"Loại phương tiện"**: "Xe đạp", "Xe máy", "Ô tô"  
- **"Ngày hủy đăng ký"**: Có thể để trống nếu chưa hủy
- **"Mã căn hộ"**: Phải tồn tại trong hệ thống 