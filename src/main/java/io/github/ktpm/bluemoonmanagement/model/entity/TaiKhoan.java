package io.github.ktpm.bluemoonmanagement.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tai_khoan")
@Entity
public class TaiKhoan {
    @Id
    private String email;
    @Column(name = "mat_khau")
    private String matKhau;
    @Column(name = "ho_ten")
    private String hoTen;
    @Column(name = "vai_tro")
    private String vaiTro;
    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;
    @Column(name = "otp")
    private String otp;
    @Column(name = "thoi_han_otp")
    private LocalDateTime thoiHanOtp;
}
