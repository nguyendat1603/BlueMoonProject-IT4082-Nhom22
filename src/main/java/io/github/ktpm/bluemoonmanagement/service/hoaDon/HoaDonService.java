package io.github.ktpm.bluemoonmanagement.service.hoaDon;

import java.util.List;

import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;
import io.github.ktpm.bluemoonmanagement.model.dto.hoaDon.HoaDonDto;
import io.github.ktpm.bluemoonmanagement.model.dto.hoaDon.HoaDonTuNguyenDto;
import io.github.ktpm.bluemoonmanagement.model.dto.khoanThu.KhoanThuDto;
import io.github.ktpm.bluemoonmanagement.model.dto.khoanThu.KhoanThuTuNguyenDto;
import org.springframework.web.multipart.MultipartFile;

public interface HoaDonService {
    ResponseDto generateHoaDon(KhoanThuDto khoanThuDto);
    List<HoaDonDto> getAllHoaDon();
    ResponseDto addHoaDonTuNguyen(HoaDonTuNguyenDto hoaDonTuNguyenDto, KhoanThuTuNguyenDto khoanThuTuNguyenDto);
    ResponseDto importFromExcel(MultipartFile file);
    
    /**
     * Cập nhật trạng thái thanh toán cho một hóa đơn cụ thể
     */
    ResponseDto updateTrangThaiThanhToan(Integer maHoaDon, boolean daNop);
    
    /**
     * Thu toàn bộ khoản phí bắt buộc cho một căn hộ
     */
    ResponseDto thuToanBoPhiCanHo(String maCanHo, List<HoaDonDto> hoaDonList);
}
