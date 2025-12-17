package io.github.ktpm.bluemoonmanagement.model.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "phi_gui_xe")
@Entity
public class PhiGuiXe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "loai_xe")
    private String loaiXe;

    @Column(name = "so_tien")
    private int soTien;

    @ManyToOne
    @JoinColumn(name = "ma_khoan_thu", referencedColumnName = "ma_khoan_thu")
    private KhoanThu khoanThu;   
}
