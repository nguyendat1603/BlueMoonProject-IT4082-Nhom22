package io.github.ktpm.bluemoonmanagement.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.github.ktpm.bluemoonmanagement.model.dto.hoaDon.HoaDonDto;
import io.github.ktpm.bluemoonmanagement.model.dto.hoaDon.HoaDonTuNguyenDto;
import io.github.ktpm.bluemoonmanagement.model.dto.hoaDon.HoaDonDichVuDto;
import io.github.ktpm.bluemoonmanagement.model.entity.HoaDon;

@Mapper(componentModel = "spring")
public interface HoaDonMapper {
    @Mapping(target = "tenKhoanThu", source = "hoaDon.khoanThu.tenKhoanThu")
    @Mapping(target = "maCanHo", source = "hoaDon.canHo.maCanHo")
    @Mapping(target = "loaiKhoanThu", expression = "java(hoaDon.getKhoanThu().isBatBuoc() ? \"Bắt buộc\" : \"Tự nguyện\")")
    HoaDonDto toHoaDonDto(HoaDon hoaDon);

    @Mapping(target = "maHoaDon", ignore = true)
    @Mapping(target = "khoanThu.tenKhoanThu", source = "hoaDonDichVuDto.tenKhoanThu")
    @Mapping(target = "canHo.maCanHo", source = "hoaDonDichVuDto.maCanHo")
    @Mapping(target = "ngayNop", ignore = true)
    @Mapping(target = "daNop", ignore = true)
    HoaDon fromHoaDonDichVuDto(HoaDonDichVuDto hoaDonDichVuDto);

    @Mapping(target = "ngayNop", ignore = true)
    @Mapping(target = "daNop", ignore = true)
    @Mapping(target = "khoanThu.tenKhoanThu", source = "hoaDonTuNguyenDto.tenKhoanThu")
    @Mapping(target = "canHo.maCanHo", source = "hoaDonTuNguyenDto.maCanHo")
    HoaDon fromHoaDonTuNguyenDto(HoaDonTuNguyenDto hoaDonTuNguyenDto);

    // MapStruct mapping from HoaDonDto to HoaDon (for import)
    @Mapping(target = "khoanThu", ignore = true) // Set manually if needed
    @Mapping(target = "canHo", ignore = true)   // Set manually if needed
    HoaDon fromHoaDonDto(HoaDonDto hoaDonDto);
}
