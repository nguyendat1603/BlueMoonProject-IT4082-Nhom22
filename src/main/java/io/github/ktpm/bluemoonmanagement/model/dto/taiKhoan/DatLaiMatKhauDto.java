package io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatLaiMatKhauDto {
    private String email;
    private String otp;
    private String matKhauMoi;
    private String xacNhanMatKhauMoi;
}
