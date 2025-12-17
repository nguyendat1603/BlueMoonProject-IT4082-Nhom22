package io.github.ktpm.bluemoonmanagement.model.dto.phuongTien;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhuongTienDto {
    private int soThuTu;
    private String loaiPhuongTien;
    private String bienSo;
    private LocalDate ngayDangKy;
    private String maCanHo;  // Mã căn hộ để liên kết phương tiện với căn hộ
}
