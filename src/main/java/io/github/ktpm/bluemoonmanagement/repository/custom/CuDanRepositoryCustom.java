package io.github.ktpm.bluemoonmanagement.repository.custom;

import java.util.List;
import io.github.ktpm.bluemoonmanagement.model.entity.CuDan;

public interface CuDanRepositoryCustom {
    
    /**
     * Lấy tất cả cư dân với fetch join căn hộ
     * @return Danh sách cư dân đã được fetch join với căn hộ
     */
    List<CuDan> findAllWithCanHo();
    
} 