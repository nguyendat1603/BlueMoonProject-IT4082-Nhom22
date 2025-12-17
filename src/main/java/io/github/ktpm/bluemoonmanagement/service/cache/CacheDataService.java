package io.github.ktpm.bluemoonmanagement.service.cache;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.ktpm.bluemoonmanagement.cache.DataCache;
import io.github.ktpm.bluemoonmanagement.model.dto.canHo.CanHoChiTietDto;
import io.github.ktpm.bluemoonmanagement.model.dto.canHo.CanHoDto;
import io.github.ktpm.bluemoonmanagement.model.dto.cuDan.CuDanTrongCanHoDto;
import io.github.ktpm.bluemoonmanagement.model.dto.cuDan.CudanDto;
import io.github.ktpm.bluemoonmanagement.model.dto.hoaDon.HoaDonDto;
import io.github.ktpm.bluemoonmanagement.model.dto.phuongTien.PhuongTienDto;

/**
 * Service để xử lý dữ liệu từ cache thay vì gọi database
 */
@Service
public class CacheDataService {
    
    @Autowired
    private DataCache dataCache;
    
    /**
     * Lấy chi tiết căn hộ từ cache
     */
    public CanHoChiTietDto getCanHoChiTietFromCache(String maCanHo) {

        // Lấy thông tin căn hộ cơ bản
        CanHoDto canHoDto = dataCache.getCanHo(maCanHo);
        if (canHoDto == null) {
            return null;
        }
        
        // Tạo CanHoChiTietDto từ CanHoDto
        CanHoChiTietDto chiTietDto = new CanHoChiTietDto();
        chiTietDto.setMaCanHo(canHoDto.getMaCanHo());
        chiTietDto.setToaNha(canHoDto.getToaNha());
        chiTietDto.setTang(canHoDto.getTang());
        chiTietDto.setSoNha(canHoDto.getSoNha());
        chiTietDto.setDienTich(canHoDto.getDienTich());
        chiTietDto.setDaBanChua(canHoDto.isDaBanChua());
        chiTietDto.setTrangThaiKiThuat(canHoDto.getTrangThaiKiThuat());
        chiTietDto.setTrangThaiSuDung(canHoDto.getTrangThaiSuDung());
        chiTietDto.setChuHo(canHoDto.getChuHo());
        
        // Lấy danh sách cư dân từ cache
        List<CuDanTrongCanHoDto> cuDanList = getCuDanListFromCache(maCanHo);
        chiTietDto.setCuDanList(cuDanList);
        
        // Lấy danh sách phương tiện từ cache
        List<PhuongTienDto> phuongTienList = getPhuongTienListFromCache(maCanHo);
        chiTietDto.setPhuongTienList(phuongTienList);
        
        // Lấy danh sách hóa đơn từ cache
        List<HoaDonDto> hoaDonList = getHoaDonListFromCache(maCanHo);
        chiTietDto.setHoaDonList(hoaDonList);
        
        return chiTietDto;
    }
    
    /**
     * Lấy danh sách cư dân của căn hộ từ cache
     */
    private List<CuDanTrongCanHoDto> getCuDanListFromCache(String maCanHo) {
        List<CudanDto> allCuDan = dataCache.getCuDanList();
        if (allCuDan == null) {
            return List.of();
        }
        
        return allCuDan.stream()
            .filter(cuDan -> maCanHo.equals(cuDan.getMaCanHo()))
            .map(this::convertToCuDanTrongCanHoDto)
            .collect(Collectors.toList());
    }
    
    /**
     * Chuyển đổi CudanDto thành CuDanTrongCanHoDto
     */
    private CuDanTrongCanHoDto convertToCuDanTrongCanHoDto(CudanDto cudanDto) {
        CuDanTrongCanHoDto dto = new CuDanTrongCanHoDto();
        dto.setMaDinhDanh(cudanDto.getMaDinhDanh());
        dto.setHoVaTen(cudanDto.getHoVaTen());
        dto.setGioiTinh(cudanDto.getGioiTinh());
        dto.setNgaySinh(cudanDto.getNgaySinh());
        dto.setSoDienThoai(cudanDto.getSoDienThoai());
        dto.setEmail(cudanDto.getEmail());
        dto.setTrangThaiCuTru(cudanDto.getTrangThaiCuTru());
        dto.setNgayChuyenDi(cudanDto.getNgayChuyenDi());
        return dto;
    }
    
    /**
     * Lấy danh sách phương tiện của căn hộ từ cache
     */
    private List<PhuongTienDto> getPhuongTienListFromCache(String maCanHo) {
        List<PhuongTienDto> allPhuongTien = dataCache.getPhuongTienList();
        if (allPhuongTien == null) {
            return List.of();
        }
        
        return allPhuongTien.stream()
            .filter(pt -> maCanHo.equals(pt.getMaCanHo()))
            .collect(Collectors.toList());
    }
    
    /**
     * Lấy danh sách hóa đơn của căn hộ từ cache  
     */
    private List<HoaDonDto> getHoaDonListFromCache(String maCanHo) {
        List<HoaDonDto> allHoaDon = dataCache.getHoaDonList();
        if (allHoaDon == null) {
            return List.of();
        }
        
        // Note: HoaDonDto không có maCanHo field, cần tìm cách khác để filter
        // Hoặc cần thêm field maCanHo vào HoaDonDto
        // Tạm thời return empty list
        return List.of();
    }
    
    /**
     * Kiểm tra cache đã được load chưa
     */
    public boolean isCacheLoaded() {
        return dataCache.isLoaded();
    }
    
    /**
     * Refresh cache data - gọi khi có thay đổi
     */
    public void refreshCacheData() {
        dataCache.clearCache();
        // DataLoader sẽ tự động load lại khi cache bị clear
    }
} 