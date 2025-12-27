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
        // Start data loading on a background thread so Spring context initialization and JavaFX UI are not blocked.
        Thread bg = new Thread(() -> {
            long totalStart = System.currentTimeMillis();
            try {
                long s = System.currentTimeMillis();
                // Load căn hộ
                loadCanHoData();
                System.out.println("DataLoader(background): loadCanHoData took " + (System.currentTimeMillis() - s) + " ms");

                s = System.currentTimeMillis();
                // Load cư dân
                loadCuDanData();
                System.out.println("DataLoader(background): loadCuDanData took " + (System.currentTimeMillis() - s) + " ms");

                s = System.currentTimeMillis();
                // Load khoản thu
                loadKhoanThuData();
                System.out.println("DataLoader(background): loadKhoanThuData took " + (System.currentTimeMillis() - s) + " ms");

                // Mark cache as loaded
                dataCache.setLoaded(true);

                long total = System.currentTimeMillis() - totalStart;
                System.out.println("DataLoader(background): total data load time = " + total + " ms");

            } catch (Exception e) {
                System.err.println("LỖI KHI LOAD DỮ LIỆU (background): " + e.getMessage());
                e.printStackTrace();
            }
        }, "DataLoader-Background");
        bg.setDaemon(true);
        bg.start();
        System.out.println("DataLoader: started background data loading thread");
    }
    
    private void loadCanHoData() {
        try {
            if (canHoService != null) {
                // Phase 1: fast initial load (limited) to populate cache quickly
                var initialList = canHoService.getCanHoPage(200);
                dataCache.setCanHoList(initialList);
                System.out.println("DataLoader: initial canHo cache size = " + (initialList == null ? 0 : initialList.size()));

                // Phase 2: continue loading full dataset in background (non-blocking)
                Thread fullLoader = new Thread(() -> {
                    try {
                        var fullList = canHoService.getAllCanHo();
                        dataCache.setCanHoList(fullList);
                        System.out.println("DataLoader: full canHo cache size = " + (fullList == null ? 0 : fullList.size()));
                    } catch (Exception ex) {
                        System.err.println("✗ Lỗi load full danh sách căn hộ: " + ex.getMessage());
                    }
                }, "CanHo-FullLoader");
                fullLoader.setDaemon(true);
                fullLoader.start();
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