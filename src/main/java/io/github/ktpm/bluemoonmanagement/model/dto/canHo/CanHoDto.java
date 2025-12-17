package io.github.ktpm.bluemoonmanagement.model.dto.canHo;

import io.github.ktpm.bluemoonmanagement.model.dto.cuDan.ChuHoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CanHoDto {
    private String maCanHo;
    private String toaNha;
    private String tang;
    private String soNha;
    private double dienTich;
    private ChuHoDto chuHo;
    private boolean daBanChua;
    private String trangThaiKiThuat;
    private String trangThaiSuDung; 
}
