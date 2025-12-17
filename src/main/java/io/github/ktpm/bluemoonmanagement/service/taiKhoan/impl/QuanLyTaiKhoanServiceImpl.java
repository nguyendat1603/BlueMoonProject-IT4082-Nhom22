package io.github.ktpm.bluemoonmanagement.service.taiKhoan.impl;

import io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.ThongTinTaiKhoanDto;
import io.github.ktpm.bluemoonmanagement.model.mapper.TaiKhoanMapper;
import io.github.ktpm.bluemoonmanagement.service.taiKhoan.QuanLyTaiKhoanService;
import io.github.ktpm.bluemoonmanagement.model.entity.TaiKhoan;
import io.github.ktpm.bluemoonmanagement.repository.TaiKhoanRepository;
import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;
import io.github.ktpm.bluemoonmanagement.session.Session;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class QuanLyTaiKhoanServiceImpl implements QuanLyTaiKhoanService {
    private final TaiKhoanRepository taiKhoanRepository;
    private final TaiKhoanMapper taiKhoanMapper;

    public QuanLyTaiKhoanServiceImpl(TaiKhoanRepository taiKhoanRepository, TaiKhoanMapper taiKhoanMapper) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.taiKhoanMapper = taiKhoanMapper;
    }

    @Override
    public List<ThongTinTaiKhoanDto> layDanhSachTaiKhoan() {
        List<ThongTinTaiKhoanDto> danhSachTaiKhoan = taiKhoanRepository.findAll()
                .stream()
                .map(taiKhoanMapper::toThongTinTaiKhoanDto)
                .collect(Collectors.toList());
        for(ThongTinTaiKhoanDto taiKhoandto : danhSachTaiKhoan) {
            if(taiKhoandto.getEmail().equals("admin")){
                danhSachTaiKhoan.remove(taiKhoandto);
                break;
            }
        }
        return danhSachTaiKhoan;
    }

    @Override
    public ResponseDto thayDoiThongTinTaiKhoan(ThongTinTaiKhoanDto dto) {
        if (Session.getCurrentUser() == null || !"Tổ trưởng".equals(Session.getCurrentUser().getVaiTro())) {
            return new ResponseDto(false, "Bạn không có quyền thay đổi thông tin tài khoản. Chỉ Tổ trưởng mới được phép.");
        }
        TaiKhoan taiKhoan = taiKhoanRepository.findById(dto.getEmail()).orElse(null);
        taiKhoan.setHoTen(dto.getHoTen());
        taiKhoan.setVaiTro(dto.getVaiTro());
        taiKhoan.setNgayCapNhat(LocalDateTime.now());
        taiKhoanRepository.save(taiKhoan);
        return new ResponseDto(true, "Thay đổi thông tin tài khoản thành công.");
    }

    @Override
    public ResponseDto xoaTaiKhoan(ThongTinTaiKhoanDto thongTinTaiKhoanDto) {
        if (Session.getCurrentUser() == null || !"Tổ trưởng".equals(Session.getCurrentUser().getVaiTro())) {
            return new ResponseDto(false, "Bạn không có quyền xóa tài khoản. Chỉ Tổ trưởng mới được phép.");
        }
        taiKhoanRepository.deleteById(thongTinTaiKhoanDto.getEmail());
        return new ResponseDto(true, "Xóa tài khoản thành công.");
    }
}

