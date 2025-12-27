package io.github.ktpm.bluemoonmanagement.model.dto.canHo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CanHoSummaryDto {
    private String maCanHo;
    private String toaNha;
    private String tang;
    private String soNha;
    private double dienTich;
    private boolean daBanChua;
    private String trangThaiKiThuat;
    private String trangThaiSuDung;
    private String chuHoName;
}


