package io.github.ktpm.bluemoonmanagement.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.ktpm.bluemoonmanagement.model.dto.khoanThu.KhoanThuDto;
import io.github.ktpm.bluemoonmanagement.service.khoanThu.KhoanThuService;

/**
 * Utility class để xử lý dữ liệu cho PieChart
 * Tách logic khỏi controller để code sạch hơn
 */
public class PieChartDataUtil {
    
    /**
     * Lấy dữ liệu khoản thu cho PieChart - Query trực tiếp từ database
     * @param khoanThuService Service để query database
     * @return Map với key là label hiển thị, value là số lượng
     */
    public static Map<String, Integer> getKhoanThuDataFromDatabase(KhoanThuService khoanThuService) {
        try {
            if (khoanThuService == null) {
                System.err.println("⚠️ KhoanThuService is null, cannot get data");
                return null;
            }
            

            // Query trực tiếp từ database để đếm khoản thu bắt buộc
            long totalBatBuoc = khoanThuService.countKhoanThuByBatBuoc(true);
            long totalAmountBatBuoc = khoanThuService.sumAmountByBatBuoc(true);
            
            // Query trực tiếp từ database để đếm khoản thu tự nguyện  
            long totalTuNguyen = khoanThuService.countKhoanThuByBatBuoc(false);
            long totalAmountTuNguyen = khoanThuService.sumAmountByBatBuoc(false);
            
            Map<String, Integer> feeTypeCount = new HashMap<>();
            
            // Tạo label có thông tin chi tiết
            if (totalBatBuoc > 0) {
                String labelBatBuoc = String.format("Bắt buộc (%d khoản - %,d VNĐ)", 
                    totalBatBuoc, totalAmountBatBuoc);
                feeTypeCount.put(labelBatBuoc, (int)totalBatBuoc);

            }
            
            if (totalTuNguyen > 0) {
                String labelTuNguyen = String.format("Tự nguyện (%d khoản - %,d VNĐ)", 
                    totalTuNguyen, totalAmountTuNguyen);
                feeTypeCount.put(labelTuNguyen, (int)totalTuNguyen);
            }            
            return feeTypeCount;
            
        } catch (Exception e) {
            System.err.println("❌ Error getting fee data from database: " + e.getMessage());
            e.printStackTrace();
            // Fallback to list-based approach
            return getKhoanThuDataFromList(khoanThuService);
        }
    }
    
    /**
     * Fallback method: Lấy dữ liệu từ list (khi mapper trực tiếp gặp lỗi)
     * @param khoanThuService Service để lấy list dữ liệu
     * @return Map với key là label hiển thị, value là số lượng
     */
    public static Map<String, Integer> getKhoanThuDataFromList(KhoanThuService khoanThuService) {
        try {
            if (khoanThuService == null) {
                System.err.println("⚠️ KhoanThuService is null, cannot get data");
                return null;
            }
            
            // Lấy tất cả khoản thu từ database
            List<KhoanThuDto> allKhoanThu = khoanThuService.getAllKhoanThu();
            
            if (allKhoanThu == null || allKhoanThu.isEmpty()) {
                return null;
            }
            
            // Phân tích dữ liệu theo Bắt buộc vs Tự nguyện
            Map<String, Integer> feeTypeCount = new HashMap<>();
            int totalBatBuoc = 0;
            int totalTuNguyen = 0;
            long totalAmountBatBuoc = 0;
            long totalAmountTuNguyen = 0;
            
            for (KhoanThuDto dto : allKhoanThu) {
                // Sử dụng field batBuoc có sẵn trong DTO
                boolean isBatBuoc = dto.isBatBuoc();
                
                if (isBatBuoc) {
                    totalBatBuoc++;
                    totalAmountBatBuoc += dto.getSoTien();
                } else {
                    totalTuNguyen++;
                    totalAmountTuNguyen += dto.getSoTien();
                }
            }
            
            // Tạo label có thông tin chi tiết
            if (totalBatBuoc > 0) {
                String labelBatBuoc = String.format("Bắt buộc (%d khoản - %,d VNĐ)", 
                    totalBatBuoc, totalAmountBatBuoc);
                feeTypeCount.put(labelBatBuoc, totalBatBuoc);
            }
            
            if (totalTuNguyen > 0) {
                String labelTuNguyen = String.format("Tự nguyện (%d khoản - %,d VNĐ)", 
                    totalTuNguyen, totalAmountTuNguyen);
                feeTypeCount.put(labelTuNguyen, totalTuNguyen);
            }
            

            return feeTypeCount;
            
        } catch (Exception e) {
            System.err.println("❌ Error getting fee data from list: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lấy màu cho PieChart slice dựa trên tên data
     * @param dataName Tên của data slice
     * @param colorIndex Index của màu mặc định
     * @return Mã màu CSS
     */
    public static String getSliceColor(String dataName, int colorIndex) {
        if (dataName == null) {
            return getDefaultColor(colorIndex);
        }
        
        String lowerName = dataName.toLowerCase();
        
        if (lowerName.contains("bắt buộc")) {
            return "#2196F3"; // 🔵 Màu xanh cho Bắt buộc
        } else if (lowerName.contains("tự nguyện")) {
            return "#FF9800"; // 🟠 Màu cam cho Tự nguyện  
        } else {
            return getDefaultColor(colorIndex);
        }
    }
    
    /**
     * Lấy màu mặc định theo index
     * @param colorIndex Index của màu
     * @return Mã màu CSS
     */
    private static String getDefaultColor(int colorIndex) {
        // Màu mặc định: Xanh, Cam, Xanh lá, Tím
        String[] defaultColors = {
            "#2196F3", // 🔵 Xanh - cho Bắt buộc nếu không match
            "#FF9800", // 🟠 Cam - cho Tự nguyện nếu không match  

        };
        return defaultColors[colorIndex % defaultColors.length];
    }
    

} 