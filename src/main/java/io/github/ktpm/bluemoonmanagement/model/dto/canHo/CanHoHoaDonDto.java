package io.github.ktpm.bluemoonmanagement.model.dto.canHo;

import java.util.List;

import io.github.ktpm.bluemoonmanagement.model.dto.phuongTien.PhuongTienDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CanHoHoaDonDto {
    private String maCanHo;
    private double dienTich;
    private boolean daBanChua;
    private String trangThaiSuDung;
    private List<PhuongTienDto> phuongTienList;
}
