package io.github.ktpm.bluemoonmanagement.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "khoan_thu")
@Entity
public class KhoanThu {
    @Id
    @Column(name = "ma_khoan_thu")
    private String maKhoanThu;

    @Column(name = "ten_khoan_thu")
    private String tenKhoanThu;
    
    @Column(name = "bat_buoc")
    private boolean batBuoc;
    
    @Column(name = "don_vi_tinh")
    private String donViTinh;
    
    @Column(name = "so_tien")
    private int soTien;

    @Column(name = "pham_vi")
    private String phamVi;
    
    @Column(name = "ngay_tao")
    private LocalDate ngayTao;
    
    @Column(name = "thoi_han")
    private LocalDate thoiHan;
    
    @Column(name = "ghi_chu")
    private String ghiChu;

    @Column(name = "tao_hoa_don")
    private boolean taoHoaDon;

    @OneToMany(mappedBy = "khoanThu")
    private List<HoaDon> hoaDonList;

    @OneToMany(mappedBy = "khoanThu")
    private List<PhiGuiXe> phiGuiXeList;
}