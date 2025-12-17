package io.github.ktpm.bluemoonmanagement.service.taiKhoan;

import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;
import io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.DoiMatKhauDto;

public interface DoiMatKhauService {

    ResponseDto doiMatKhau(DoiMatKhauDto doiMatKhauDto);
}
