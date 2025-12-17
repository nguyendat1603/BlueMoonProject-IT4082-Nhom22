package io.github.ktpm.bluemoonmanagement.model.dto.phiGuiXe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhiGuiXeDto {
    private Integer id;
    private String loaiXe;
    private int soTien;
    private String maKhoanThu;
    
    // Constructor without id for creating new records
    public PhiGuiXeDto(String loaiXe, int soTien, String maKhoanThu) {
        this.loaiXe = loaiXe;
        this.soTien = soTien;
        this.maKhoanThu = maKhoanThu;
    }
}
