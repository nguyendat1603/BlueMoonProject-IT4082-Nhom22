package io.github.ktpm.bluemoonmanagement.service.taiKhoan.impl;

import io.github.ktpm.bluemoonmanagement.model.entity.TaiKhoan;
import io.github.ktpm.bluemoonmanagement.repository.TaiKhoanRepository;
import io.github.ktpm.bluemoonmanagement.util.OtpUtil;
import io.github.ktpm.bluemoonmanagement.util.PasswordUtil;
import io.github.ktpm.bluemoonmanagement.util.EmailUtil;
import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;
import io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.DatLaiMatKhauDto;
import io.github.ktpm.bluemoonmanagement.service.taiKhoan.QuenMatKhauService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class QuenMatKhauServiceImpl implements QuenMatKhauService {
    private final TaiKhoanRepository taiKhoanRepository;
    private final JavaMailSender javaMailSender;

    public QuenMatKhauServiceImpl(TaiKhoanRepository taiKhoanRepository, JavaMailSender javaMailSender) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public ResponseDto guiMaOtp(DatLaiMatKhauDto dto) {
        TaiKhoan taiKhoan = taiKhoanRepository.findById(dto.getEmail()).orElse(null);
        if (taiKhoan == null) {
            return new ResponseDto(false, "Tài khoản không tồn tại");
        }
        String otp = OtpUtil.generateOtp();
        taiKhoan.setOtp(otp);
        taiKhoan.setThoiHanOtp(LocalDateTime.now().plusMinutes(5));
        taiKhoanRepository.save(taiKhoan);
        // Gửi email OTP sử dụng EmailUtil
        EmailUtil.sendEmailQuenMatKhau(dto.getEmail(), taiKhoan.getHoTen(), otp, javaMailSender);
        return new ResponseDto(true, "OTP đã được gửi về email");
    }

    @Override
    public ResponseDto xacThucOtp(DatLaiMatKhauDto datLaiMatKhauDto) {
        TaiKhoan taiKhoan = taiKhoanRepository.findById(datLaiMatKhauDto.getEmail()).orElse(null);
        if (taiKhoan == null) {
            return new ResponseDto(false, "Tài khoản không tồn tại");
        }
        if (taiKhoan.getOtp() == null || !taiKhoan.getOtp().equals(datLaiMatKhauDto.getOtp())) {
            return new ResponseDto(false, "OTP không hợp lệ");
        }
        if (taiKhoan.getThoiHanOtp() == null || LocalDateTime.now().isAfter(taiKhoan.getThoiHanOtp())) {
            return new ResponseDto(false, "OTP đã hết hạn");
        }
        return new ResponseDto(true, "OTP hợp lệ");
    }

    @Override
    public ResponseDto datLaiMatKhau(DatLaiMatKhauDto dto) {
        TaiKhoan taiKhoan = taiKhoanRepository.findById(dto.getEmail()).orElse(null);
        if(!PasswordUtil.isValidPasswordFormat(dto.getMatKhauMoi())){
            return new ResponseDto(false, "Mật khẩu mới không đúng định dạng. Mật khẩu phải có ít nhất một chữ hoa, một số và một ký tự đặc biệt.");
        };
        if (!dto.getMatKhauMoi().equals(dto.getXacNhanMatKhauMoi())) {
            return new ResponseDto(false, "Mật khẩu mới và xác nhận mật khẩu không khớp");
        }
        taiKhoan.setMatKhau(PasswordUtil.hashPassword(dto.getMatKhauMoi()));
        taiKhoan.setOtp(null);
        taiKhoan.setThoiHanOtp(null);
        taiKhoan.setNgayCapNhat(LocalDateTime.now());
        taiKhoanRepository.save(taiKhoan);
        return new ResponseDto(true, "Đặt lại mật khẩu thành công");
    }
}
