package io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ThongTinTaiKhoanDto {
    private String email;
    private String hoTen;
    private String vaiTro;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;
}
