package io.github.ktpm.bluemoonmanagement.service.canHo;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;
import io.github.ktpm.bluemoonmanagement.model.dto.canHo.CanHoChiTietDto;
import io.github.ktpm.bluemoonmanagement.model.dto.canHo.CanHoDto;

public interface CanHoService {
    List<CanHoDto> getAllCanHo();

    ResponseDto addCanHo(CanHoDto canHoDto);

    ResponseDto updateCanHo(CanHoDto canHoDto);

    ResponseDto deleteCanHo(CanHoDto canHoDto);

    CanHoChiTietDto getCanHoChiTiet(CanHoDto canHoDto);

    ResponseDto importFromExcel(MultipartFile file);

    ResponseDto exportToExcel(String filePath);
}
