package io.github.ktpm.bluemoonmanagement.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "hoa_don")
@Entity
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maHoaDon;

    @ManyToOne
    @JoinColumn(name = "ma_khoan_thu", referencedColumnName = "ma_khoan_thu")
    private KhoanThu khoanThu;

    @ManyToOne
    @JoinColumn(name = "ma_can_ho", referencedColumnName = "ma_can_ho")
    private CanHo canHo;

    @Column(name = "so_tien")
    private Integer soTien;

    @Column(name = "ngay_nop")
    private LocalDateTime ngayNop;
    
    @Column(name = "da_nop")
    private boolean daNop;
}
