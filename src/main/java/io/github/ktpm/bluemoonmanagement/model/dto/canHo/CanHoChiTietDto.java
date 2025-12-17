package io.github.ktpm.bluemoonmanagement.model.dto.canHo;

import io.github.ktpm.bluemoonmanagement.model.dto.cuDan.ChuHoDto;
import io.github.ktpm.bluemoonmanagement.model.dto.cuDan.CuDanTrongCanHoDto;
import io.github.ktpm.bluemoonmanagement.model.dto.hoaDon.HoaDonDto;
import io.github.ktpm.bluemoonmanagement.model.dto.phuongTien.PhuongTienDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CanHoChiTietDto {
    private String maCanHo;
    private String toaNha;
    private String tang;
    private String soNha;
    private double dienTich;
    private boolean daBanChua;
    private String trangThaiKiThuat;
    private String trangThaiSuDung;
    private ChuHoDto chuHo;
    private List<PhuongTienDto> phuongTienList;
    private List<CuDanTrongCanHoDto> cuDanList;
    private List<HoaDonDto> hoaDonList;
}
