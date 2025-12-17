package io.github.ktpm.bluemoonmanagement.model.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "phuong_tien")
@Entity
public class PhuongTien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int soThuTu;

    @Column(name = "loai_phuong_tien")
    private String loaiPhuongTien;

    @Column(name = "bien_so")
    private String bienSo;

    @Column(name = "ngay_dang_ky")
    private LocalDate ngayDangKy;

    @Column(name = "ngay_huy_dang_ky")
    private LocalDate ngayHuyDangKy;

    @ManyToOne
    @JoinColumn(name = "ma_can_ho", referencedColumnName = "ma_can_ho")
    private CanHo canHo;
}
