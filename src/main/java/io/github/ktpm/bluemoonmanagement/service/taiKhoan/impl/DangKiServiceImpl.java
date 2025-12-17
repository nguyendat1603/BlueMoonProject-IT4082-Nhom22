package io.github.ktpm.bluemoonmanagement.service.taiKhoan.impl;

import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;
import io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.DangKiDto;
import io.github.ktpm.bluemoonmanagement.model.entity.TaiKhoan;
import io.github.ktpm.bluemoonmanagement.model.mapper.TaiKhoanMapper;
import io.github.ktpm.bluemoonmanagement.repository.TaiKhoanRepository;
import io.github.ktpm.bluemoonmanagement.service.taiKhoan.DangKiService;
import io.github.ktpm.bluemoonmanagement.util.PasswordUtil;
import io.github.ktpm.bluemoonmanagement.session.Session;
import io.github.ktpm.bluemoonmanagement.util.EmailUtil;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class DangKiServiceImpl implements DangKiService {
    private final TaiKhoanRepository taiKhoanRepository;
    private final TaiKhoanMapper taiKhoanMapper;
    private final JavaMailSender javaMailSender;

    public DangKiServiceImpl(TaiKhoanRepository taiKhoanRepository, TaiKhoanMapper taiKhoanMapper, JavaMailSender javaMailSender) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.taiKhoanMapper = taiKhoanMapper;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public ResponseDto dangKiTaiKhoan(DangKiDto dangKiDto) {
        // Kiểm tra quyền: chỉ 'Tổ trưởng' mới được đăng ký tài khoản
        if (Session.getCurrentUser() == null || !"Tổ trưởng".equals(Session.getCurrentUser().getVaiTro())) {
            return new ResponseDto(false, "Bạn không có quyền đăng ký tài khoản. Chỉ Tổ trưởng mới được phép.");
        }
        // Kiểm tra email đã tồn tại chưa
        if (taiKhoanRepository.existsById(dangKiDto.getEmail())) {
            return new ResponseDto(false, "Email đã tồn tại");
        }
        // Tạo mật khẩu ngẫu nhiên
        String matKhau = PasswordUtil.generatePassword();
        // Tạo tài khoản mới
        TaiKhoan taiKhoan = taiKhoanMapper.fromDangKiDto(dangKiDto);
        taiKhoan.setMatKhau(PasswordUtil.hashPassword(matKhau));

        taiKhoan.setNgayTao(LocalDateTime.now());
        taiKhoan.setNgayCapNhat(LocalDateTime.now());
        taiKhoanRepository.save(taiKhoan);
        // Gửi email thông tin tài khoản cho người dùng (HTML)
        EmailUtil.sendEmailDangKi(javaMailSender,dangKiDto.getEmail(),dangKiDto.getHoTen(),dangKiDto.getEmail(),matKhau);
        return new ResponseDto(true, "Đăng kí thành công");
    }
}
