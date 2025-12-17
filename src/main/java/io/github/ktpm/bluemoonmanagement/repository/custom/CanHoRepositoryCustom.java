package io.github.ktpm.bluemoonmanagement.repository.custom;

import io.github.ktpm.bluemoonmanagement.model.entity.CanHo;

public interface CanHoRepositoryCustom {
    
    /**
     * Lấy thông tin căn hộ cùng với danh sách cư dân có trạng thái cư trú là "Cư trú" trong căn hộ đó.
     *
     * @param maCanHo Mã căn hộ cần truy vấn
     * @return Đối tượng CanHo kèm danh sách cư dân đang cư trú, hoặc null nếu không tìm thấy
     */
    CanHo findCanHoWithCuDanCuTru(String maCanHo);
}
