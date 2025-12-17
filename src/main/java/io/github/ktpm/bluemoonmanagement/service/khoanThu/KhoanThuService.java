package io.github.ktpm.bluemoonmanagement.service.khoanThu;
import java.util.List;

import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;
import io.github.ktpm.bluemoonmanagement.model.dto.khoanThu.KhoanThuDto;
import org.springframework.web.multipart.MultipartFile;

public interface KhoanThuService {
    List<KhoanThuDto> getAllKhoanThu();
    ResponseDto addKhoanThu(KhoanThuDto khoanThuDto);
    ResponseDto updateKhoanThu(KhoanThuDto khoanThuDto);
    ResponseDto deleteKhoanThu(String maKhoanThu);
    ResponseDto importFromExcel(MultipartFile file);
    ResponseDto exportToExcel(String filePath);
    
    // Methods for PieChart - query directly from database
    long countKhoanThuByBatBuoc(boolean batBuoc);
    long sumAmountByBatBuoc(boolean batBuoc);
}
