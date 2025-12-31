package io.github.ktpm.bluemoonmanagement.repository;

import io.github.ktpm.bluemoonmanagement.model.entity.HoaDon;
import io.github.ktpm.bluemoonmanagement.model.entity.KhoanThu;
import io.github.ktpm.bluemoonmanagement.model.entity.CanHo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

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

    /**
     * Find all invoices with eager loading of relationships to prevent N+1 queries
     */
    @EntityGraph(attributePaths = {"khoanThu", "canHo"})
    @Query("SELECT h FROM HoaDon h")
    List<HoaDon> findAllWithRelationships();
}
