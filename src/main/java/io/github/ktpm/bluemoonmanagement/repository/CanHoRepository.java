package io.github.ktpm.bluemoonmanagement.repository;

import io.github.ktpm.bluemoonmanagement.model.entity.CanHo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CanHoRepository extends JpaRepository<CanHo, String>, io.github.ktpm.bluemoonmanagement.repository.custom.CanHoRepositoryCustom{
    
    @Query("SELECT DISTINCT c FROM CanHo c LEFT JOIN FETCH c.phuongTienList")
    List<CanHo> findAllWithPhuongTien();
    
}
