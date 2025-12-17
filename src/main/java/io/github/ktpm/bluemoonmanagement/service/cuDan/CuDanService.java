package io.github.ktpm.bluemoonmanagement.service.cuDan;

import java.util.List;

import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;
import io.github.ktpm.bluemoonmanagement.model.dto.cuDan.CudanDto;
import org.springframework.web.multipart.MultipartFile;

public interface CuDanService {
    List<CudanDto> getAllCuDan();
    ResponseDto addCuDan(CudanDto cudanDto);
    ResponseDto updateCuDan(CudanDto cudanDto);
    ResponseDto deleteCuDan(CudanDto cudanDto);    
    ResponseDto importFromExcel(MultipartFile file);
    ResponseDto exportToExcel(String filePath);
    
    // Soft delete method
    boolean xoaMem(String maDinhDanh);
}
