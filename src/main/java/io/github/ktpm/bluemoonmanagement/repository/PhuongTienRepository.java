package io.github.ktpm.bluemoonmanagement.repository;

import io.github.ktpm.bluemoonmanagement.model.entity.PhuongTien;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PhuongTienRepository extends JpaRepository<PhuongTien, Integer> {
    boolean existsByBienSo(String bienSo);
    List<PhuongTien> findByCanHo_MaCanHo(String maCanHo);
    
    // Tìm phương tiện chưa bị hủy đăng ký (ngayHuyDangKy = null)
    @Query("SELECT p FROM PhuongTien p WHERE p.canHo.maCanHo = :maCanHo AND p.ngayHuyDangKy IS NULL")
    List<PhuongTien> findActiveByCanHo_MaCanHo(@Param("maCanHo") String maCanHo);
}
