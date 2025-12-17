package io.github.ktpm.bluemoonmanagement.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.github.ktpm.bluemoonmanagement.model.dto.cuDan.ChuHoDto;
import io.github.ktpm.bluemoonmanagement.model.dto.cuDan.CuDanTrongCanHoDto;
import io.github.ktpm.bluemoonmanagement.model.dto.cuDan.CudanDto;
import io.github.ktpm.bluemoonmanagement.model.entity.CuDan;

@Mapper(componentModel = "spring")
public interface CuDanMapper {
    CuDanTrongCanHoDto toCuDanTrongCanHoDto(CuDan cuDan);

    @Mapping(target = "maCanHo", source = "cuDan.canHo.maCanHo")
    CudanDto toCudanDto(CuDan cuDan);

    ChuHoDto toChuHoDto(CuDan cuDan);

    @Mapping(target = "canHo", ignore = true)
    CuDan fromCudanDto(CudanDto cudanDto);

    @Mapping(target = "canHo", ignore = true)
    @Mapping(target = "ngayChuyenDi", ignore = true)
    CuDan fromChuHoDto(ChuHoDto chuHoDto);
}
