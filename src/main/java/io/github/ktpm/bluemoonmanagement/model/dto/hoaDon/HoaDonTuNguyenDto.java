package io.github.ktpm.bluemoonmanagement.model.dto.hoaDon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HoaDonTuNguyenDto {
    private Integer maHoaDon;
    private String tenKhoanThu;
    private String maCanHo;
    private Integer soTien;    
}
