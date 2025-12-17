package io.github.ktpm.bluemoonmanagement.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cu_dan")
@Entity
public class CuDan {
    @Id
    @Column(name = "ma_dinh_danh")
    private String maDinhDanh;
    @Column(name = "ho_va_ten")
    private String hoVaTen;
    @Column(name = "gioi_tinh")
    private String gioiTinh;
    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;
    @Column(name = "so_dien_thoai")
    private String soDienThoai;
    @Column(name = "email")
    private String email;
    @Column(name = "trang_thai_cu_tru")
    private String trangThaiCuTru;
    @Column(name = "ngay_chuyen_den")
    private LocalDate ngayChuyenDen;
    @Column(name = "ngay_chuyen_di")
    private LocalDate ngayChuyenDi;

    @ManyToOne
    @JoinColumn(name = "ma_can_ho", referencedColumnName = "ma_can_ho")
    private CanHo canHo;

}