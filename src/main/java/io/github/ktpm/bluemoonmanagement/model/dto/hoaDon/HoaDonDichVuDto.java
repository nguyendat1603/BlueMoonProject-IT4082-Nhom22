package io.github.ktpm.bluemoonmanagement.model.dto.hoaDon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HoaDonDichVuDto {
    private String tenKhoanThu;
    private Integer soTien;
    private String maCanHo;
}
