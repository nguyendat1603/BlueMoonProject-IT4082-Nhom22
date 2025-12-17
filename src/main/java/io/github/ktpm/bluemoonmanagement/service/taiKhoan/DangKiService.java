package io.github.ktpm.bluemoonmanagement.service.taiKhoan;

import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;
import io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.DangKiDto;

public interface DangKiService {

    ResponseDto dangKiTaiKhoan(DangKiDto dangKiDto);
}
