package io.github.ktpm.bluemoonmanagement.repository;

import io.github.ktpm.bluemoonmanagement.model.entity.PhiGuiXe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PhiGuiXeRepository extends JpaRepository<PhiGuiXe, String> {
    
    @Query("SELECT p FROM PhiGuiXe p WHERE p.khoanThu.maKhoanThu = :maKhoanThu")
    List<PhiGuiXe> findByMaKhoanThu(@Param("maKhoanThu") String maKhoanThu);
    
    void deleteByKhoanThu_MaKhoanThu(String maKhoanThu);
} 