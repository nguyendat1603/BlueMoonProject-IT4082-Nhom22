package io.github.ktpm.bluemoonmanagement.repository;

import io.github.ktpm.bluemoonmanagement.model.entity.HoaDon;
import io.github.ktpm.bluemoonmanagement.model.entity.KhoanThu;
import io.github.ktpm.bluemoonmanagement.model.entity.CanHo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {
    /**
     * Find all invoices for a specific fee
     */
    List<HoaDon> findByKhoanThu(KhoanThu khoanThu);
    
    /**
     * Find invoices for a specific fee and apartment combination
     */
    List<HoaDon> findByKhoanThuAndCanHo(KhoanThu khoanThu, CanHo canHo);
}
