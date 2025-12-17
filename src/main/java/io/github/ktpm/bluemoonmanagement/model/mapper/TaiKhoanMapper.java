package io.github.ktpm.bluemoonmanagement.model.mapper;

import io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.*;
import io.github.ktpm.bluemoonmanagement.model.entity.TaiKhoan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaiKhoanMapper {
    ThongTinTaiKhoanDto toThongTinTaiKhoanDto(TaiKhoan taiKhoan);

    @Mapping(target = "matKhau", ignore = true)
    @Mapping(target = "ngayTao", ignore = true)
    @Mapping(target = "ngayCapNhat", ignore = true)
    @Mapping(target = "otp", ignore = true)
    @Mapping(target = "thoiHanOtp", ignore = true)
    TaiKhoan fromDangKiDto(DangKiDto dto);
}
