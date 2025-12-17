package io.github.ktpm.bluemoonmanagement.model.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "can_ho")
@Entity
public class CanHo {
    @Id
    @Column(name = "ma_can_ho")
    private String maCanHo;
    @Column(name = "toa_nha")
    private String toaNha;
    @Column(name = "tang")
    private String tang;
    @Column(name = "so_nha")
    private String soNha;
    @Column(name = "dien_tich")
    private double dienTich;
    @Column(name = "da_ban_chua")
    private boolean daBanChua;
    @Column(name = "trang_thai_ki_thuat")
    private String trangThaiKiThuat;
    @Column(name = "trang_thai_su_dung")
    private String trangThaiSuDung;

    @OneToMany(mappedBy = "canHo")
    private List<PhuongTien> phuongTienList;

    @OneToMany(mappedBy = "canHo")
    private List<CuDan> cuDanList;

    @ManyToOne
    @JoinColumn(name = "ma_chu_ho", referencedColumnName = "ma_dinh_danh")
    private CuDan chuHo;

    @OneToMany(mappedBy = "canHo")
    private List<HoaDon> hoaDonList;
}
