package io.github.ktpm.bluemoonmanagement.service.taiKhoan;

import io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.DangNhapDto;
import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;

public interface DangNhapServive {

    ResponseDto dangNhap(DangNhapDto dangNhapDto);
}
