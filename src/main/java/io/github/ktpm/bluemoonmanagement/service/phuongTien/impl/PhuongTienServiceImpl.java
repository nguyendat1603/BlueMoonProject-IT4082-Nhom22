package io.github.ktpm.bluemoonmanagement.service.phuongTien.impl;

import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;
import io.github.ktpm.bluemoonmanagement.model.dto.phuongTien.PhuongTienDto;
import io.github.ktpm.bluemoonmanagement.model.entity.CanHo;
import io.github.ktpm.bluemoonmanagement.model.entity.PhuongTien;
import io.github.ktpm.bluemoonmanagement.model.mapper.PhuongTienMapper;
import io.github.ktpm.bluemoonmanagement.repository.CanHoRepository;
import io.github.ktpm.bluemoonmanagement.repository.PhuongTienRepository;
import io.github.ktpm.bluemoonmanagement.service.phuongTien.PhuongTienService;
import io.github.ktpm.bluemoonmanagement.session.Session;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PhuongTienServiceImpl implements PhuongTienService {
    private final PhuongTienRepository phuongTienRepository;
    private final CanHoRepository canHoRepository;
    private final PhuongTienMapper phuongTienMapper;

    public PhuongTienServiceImpl(PhuongTienRepository phuongTienRepository, 
                                CanHoRepository canHoRepository,
                                PhuongTienMapper phuongTienMapper) {
        this.phuongTienRepository = phuongTienRepository;
        this.canHoRepository = canHoRepository;
        this.phuongTienMapper = phuongTienMapper;
    }

    @Override
    public ResponseDto themPhuongTien(PhuongTienDto phuongTienDto) {
        
        if (Session.getCurrentUser() == null || !"Tổ phó".equals(Session.getCurrentUser().getVaiTro())) {
            return new ResponseDto(false, "Bạn không có quyền thêm phương tiện. Chỉ Tổ phó mới được phép.");
        }
        
        if (phuongTienDto.getBienSo() != null && phuongTienRepository.existsByBienSo(phuongTienDto.getBienSo())) {
            return new ResponseDto(false, "Biển số xe đã tồn tại");
        }
        
        // Kiểm tra căn hộ có tồn tại không
        if (phuongTienDto.getMaCanHo() == null || phuongTienDto.getMaCanHo().trim().isEmpty()) {
            return new ResponseDto(false, "Mã căn hộ không được để trống");
        }
        
        CanHo canHo = canHoRepository.findById(phuongTienDto.getMaCanHo()).orElse(null);
        if (canHo == null) {
            return new ResponseDto(false, "Không tìm thấy căn hộ với mã: " + phuongTienDto.getMaCanHo());
        }
        
        try {
            // Set ngày đăng ký là hôm nay
            phuongTienDto.setNgayDangKy(LocalDate.now());
            
            // Tạo entity từ DTO
            PhuongTien phuongTien = phuongTienMapper.fromPhuongTienDto(phuongTienDto);
            
            // Liên kết với căn hộ
            phuongTien.setCanHo(canHo);
            
            // Lưu vào database
            phuongTienRepository.save(phuongTien);
            
            
            return new ResponseDto(true, "Thêm phương tiện thành công cho căn hộ " + canHo.getMaCanHo());
        } catch (Exception e) {
            System.err.println("ERROR: Failed to add vehicle: " + e.getMessage());
            e.printStackTrace();
            return new ResponseDto(false, "Lỗi khi thêm phương tiện: " + e.getMessage());
        }
    }

    @Override
    public ResponseDto capNhatPhuongTien(PhuongTienDto phuongTienDto) {
        if (Session.getCurrentUser() == null || !"Tổ phó".equals(Session.getCurrentUser().getVaiTro())) {
            return new ResponseDto(false, "Bạn không có quyền cập nhật phương tiện. Chỉ Tổ phó mới được phép.");
        }
        
        PhuongTien phuongTien = phuongTienRepository.findById(phuongTienDto.getSoThuTu()).orElse(null);
        if (phuongTien == null) {
            return new ResponseDto(false, "Không tìm thấy phương tiện");
        }
        
        // Kiểm tra biển số đã tồn tại ở phương tiện khác
        boolean bienSoTrung = phuongTienRepository.existsByBienSo(phuongTienDto.getBienSo()) &&
            (!phuongTien.getBienSo().equals(phuongTienDto.getBienSo()));
        if (bienSoTrung) {
            return new ResponseDto(false, "Biển số xe đã tồn tại ở phương tiện khác");
        }
        
        // Cập nhật thông tin
        phuongTien.setBienSo(phuongTienDto.getBienSo());
        phuongTien.setLoaiPhuongTien(phuongTienDto.getLoaiPhuongTien());
        
        phuongTienRepository.save(phuongTien);
        return new ResponseDto(true, "Cập nhật phương tiện thành công");
    }

    @Override
    public ResponseDto xoaPhuongTien(PhuongTienDto phuongTienDto) {
        if (Session.getCurrentUser() == null || !"Tổ phó".equals(Session.getCurrentUser().getVaiTro())) {
            return new ResponseDto(false, "Bạn không có quyền xóa phương tiện. Chỉ Tổ phó mới được phép.");
        }
        
        PhuongTien phuongTien = phuongTienRepository.findById(phuongTienDto.getSoThuTu()).orElse(null);
        if (phuongTien == null) {
            return new ResponseDto(false, "Không tìm thấy phương tiện");
        }
        
        phuongTien.setNgayHuyDangKy(LocalDate.now());
        phuongTienRepository.save(phuongTien);
        return new ResponseDto(true, "Đã hủy đăng ký phương tiện thành công");
    }
}
