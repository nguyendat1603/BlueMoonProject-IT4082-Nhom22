package io.github.ktpm.bluemoonmanagement.service.taiKhoan.impl;

import org.springframework.stereotype.Service;

import io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.DangNhapDto;
import io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.ThongTinTaiKhoanDto;
import io.github.ktpm.bluemoonmanagement.model.entity.TaiKhoan;
import io.github.ktpm.bluemoonmanagement.model.mapper.TaiKhoanMapper;
import io.github.ktpm.bluemoonmanagement.repository.TaiKhoanRepository;
import io.github.ktpm.bluemoonmanagement.service.taiKhoan.DangNhapServive;
import io.github.ktpm.bluemoonmanagement.session.Session;
import io.github.ktpm.bluemoonmanagement.util.PasswordUtil;
import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;

@Service
public class DangNhapServiceImpl implements DangNhapServive {

    private final TaiKhoanRepository taiKhoanRepository;
    private final TaiKhoanMapper taiKhoanMapper;

    public DangNhapServiceImpl(TaiKhoanRepository taiKhoanRepository, TaiKhoanMapper taiKhoanMapper) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.taiKhoanMapper = taiKhoanMapper;
    }

    @Override
    public ResponseDto dangNhap(DangNhapDto dangNhapDto) {
    TaiKhoan taiKhoan = (TaiKhoan)this.taiKhoanRepository.findById(dangNhapDto.getEmail()).orElse(null);
    
    if (taiKhoan == null) {
        return new ResponseDto(false, "Tài khoản không tồn tại");
    } else {
        // SỬA TẠI ĐÂY: So sánh trực tiếp chuỗi vì DB đang lưu plain text
        // boolean isMatch = PasswordUtil.verifyPassword(dangNhapDto.getMatKhau(), taiKhoan.getMatKhau());
        boolean isMatch = dangNhapDto.getMatKhau().equals(taiKhoan.getMatKhau());

        if (!isMatch) {
            return new ResponseDto(false, "Mật khẩu không đúng");
        } else {
            ThongTinTaiKhoanDto thongTinTaiKhoanDto = this.taiKhoanMapper.toThongTinTaiKhoanDto(taiKhoan);
            Session.setCurrentUser(thongTinTaiKhoanDto);
            return new ResponseDto(true, "Đăng nhập thành công");
        }
    }
}

}


