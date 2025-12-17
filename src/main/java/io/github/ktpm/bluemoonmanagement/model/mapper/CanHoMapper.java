package io.github.ktpm.bluemoonmanagement.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.github.ktpm.bluemoonmanagement.model.dto.canHo.CanHoChiTietDto;
import io.github.ktpm.bluemoonmanagement.model.dto.canHo.CanHoDto;
import io.github.ktpm.bluemoonmanagement.model.dto.canHo.CanHoHoaDonDto;
import io.github.ktpm.bluemoonmanagement.model.entity.CanHo;

@Mapper(componentModel = "spring", uses = { CuDanMapper.class, HoaDonMapper.class, PhuongTienMapper.class }) 
public interface CanHoMapper {
    CanHoDto toCanHoDto(CanHo canHo); // Chuyển đổi từ CanHo sang CanHoDto

    CanHoChiTietDto toCanHoChiTietDto(CanHo canHo);

    CanHoHoaDonDto toCanHoHoaDonDto(CanHo canHo);

    @Mapping(target = "hoaDonList", ignore = true)
    @Mapping(target = "phuongTienList", ignore = true)
    @Mapping(target = "cuDanList", ignore = true)
    @Mapping(target = "chuHo.ngayChuyenDi", ignore = true)
    @Mapping(target = "chuHo.canHo", ignore = true)
    CanHo fromCanHoDto(CanHoDto canHoDto);
}
