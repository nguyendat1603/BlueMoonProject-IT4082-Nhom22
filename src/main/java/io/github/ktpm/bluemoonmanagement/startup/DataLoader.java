package io.github.ktpm.bluemoonmanagement.startup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import io.github.ktpm.bluemoonmanagement.cache.DataCache;
import io.github.ktpm.bluemoonmanagement.service.canHo.CanHoService;
import io.github.ktpm.bluemoonmanagement.service.cuDan.CuDanService;
import io.github.ktpm.bluemoonmanagement.service.hoaDon.HoaDonService;
import io.github.ktpm.bluemoonmanagement.service.khoanThu.KhoanThuService;
import io.github.ktpm.bluemoonmanagement.service.phuongTien.PhuongTienService;

/**
 * Component để load tất cả dữ liệu từ database vào cache khi khởi động ứng dụng
 * Giúp tăng tốc độ truy cập dữ liệu trong suốt quá trình chạy ứng dụng
 */
@Component
public class DataLoader implements ApplicationRunner {

    @Autowired
    private DataCache dataCache;
    
    @Autowired
    private CanHoService canHoService;
    
    @Autowired
    private CuDanService cuDanService;
    
    @Autowired(required = false) 
    private PhuongTienService phuongTienService;
    
    @Autowired(required = false)
    private HoaDonService hoaDonService;
    
    @Autowired(required = false)
    private KhoanThuService khoanThuService;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        long startTime = System.currentTimeMillis();
        
        try {
            // Load căn hộ
            loadCanHoData();
            
            // Load cư dân
            loadCuDanData();
            
            // Load khoản thu
            loadKhoanThuData();
            
            // Mark cache as loaded
            dataCache.setLoaded(true);
            
            long endTime = System.currentTimeMillis();
            
        } catch (Exception e) {
            System.err.println("LỖI KHI LOAD DỮ LIỆU: " + e.getMessage());
            e.printStackTrace();
            // Không throw exception để ứng dụng vẫn có thể khởi động
        }
    }
    
    private void loadCanHoData() {
        try {
            if (canHoService != null) {
                var canHoList = canHoService.getAllCanHo();
                dataCache.setCanHoList(canHoList);
            }
        } catch (Exception e) {
            System.err.println("✗ Lỗi load căn hộ: " + e.getMessage());
        }
    }
    
    private void loadCuDanData() {
        try {
            if (cuDanService != null) {
                var cuDanList = cuDanService.getAllCuDan();
                dataCache.setCuDanList(cuDanList);
            }
        } catch (Exception e) {
            System.err.println("✗ Lỗi load cư dân: " + e.getMessage());
        }
    }
    
    private void loadKhoanThuData() {
        try {
            if (khoanThuService != null) {
                var khoanThuList = khoanThuService.getAllKhoanThu();
                dataCache.setKhoanThuList(khoanThuList);
            }
        } catch (Exception e) {
            System.err.println("✗ Lỗi load khoản thu: " + e.getMessage());
        }
    }
} 