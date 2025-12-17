package io.github.ktpm.bluemoonmanagement.service.taiKhoan;

import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;
import io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.DatLaiMatKhauDto;

public interface QuenMatKhauService {

    ResponseDto guiMaOtp(DatLaiMatKhauDto datLaiMatKhauDto);

    ResponseDto xacThucOtp(DatLaiMatKhauDto datLaiMatKhauDto);

    ResponseDto datLaiMatKhau(DatLaiMatKhauDto datLaiMatKhauDto);
    
}


