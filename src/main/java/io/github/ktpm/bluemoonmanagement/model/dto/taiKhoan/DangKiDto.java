package io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DangKiDto {
    private String email;
    private String hoTen;
    private String vaiTro;
}
