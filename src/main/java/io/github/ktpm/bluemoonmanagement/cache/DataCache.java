package io.github.ktpm.bluemoonmanagement.cache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import io.github.ktpm.bluemoonmanagement.model.dto.canHo.CanHoDto;
import io.github.ktpm.bluemoonmanagement.model.dto.cuDan.CudanDto;
import io.github.ktpm.bluemoonmanagement.model.dto.hoaDon.HoaDonDto;
import io.github.ktpm.bluemoonmanagement.model.dto.khoanThu.KhoanThuDto;
import io.github.ktpm.bluemoonmanagement.model.dto.phuongTien.PhuongTienDto;
import io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.ThongTinTaiKhoanDto;

/**
 * Cache để lưu trữ tất cả dữ liệu từ database khi khởi động
 * Giúp tăng tốc độ truy cập dữ liệu
 */
@Component
public class DataCache {
    
    // Cache cho từng loại dữ liệu
    private List<CanHoDto> canHoList = null;
    private List<CudanDto> cuDanList = null;
    private List<PhuongTienDto> phuongTienList = null;
    private List<HoaDonDto> hoaDonList = null;
    private List<KhoanThuDto> khoanThuList = null;
    private List<ThongTinTaiKhoanDto> taiKhoanList = null;
    
    // Map cache để tra cứu nhanh theo ID
    private Map<String, CanHoDto> canHoMap = new ConcurrentHashMap<>();
    private Map<String, CudanDto> cuDanMap = new ConcurrentHashMap<>();
    private Map<Integer, PhuongTienDto> phuongTienMap = new ConcurrentHashMap<>();
    private Map<String, HoaDonDto> hoaDonMap = new ConcurrentHashMap<>();
    private Map<String, KhoanThuDto> khoanThuMap = new ConcurrentHashMap<>();
    private Map<String, ThongTinTaiKhoanDto> taiKhoanMap = new ConcurrentHashMap<>();
    
    // Flag để biết cache đã được load chưa
    private boolean isLoaded = false;
    
    // Getters cho list data
    public List<CanHoDto> getCanHoList() {
        return canHoList;
    }
    
    public List<CudanDto> getCuDanList() {
        return cuDanList;
    }
    
    public List<PhuongTienDto> getPhuongTienList() {
        return phuongTienList;
    }
    
    public List<HoaDonDto> getHoaDonList() {
        return hoaDonList;
    }
    
    public List<KhoanThuDto> getKhoanThuList() {
        return khoanThuList;
    }
    
    public List<ThongTinTaiKhoanDto> getTaiKhoanList() {
        return taiKhoanList;
    }
    
    // Getters cho map data (tra cứu nhanh)
    public CanHoDto getCanHo(String maCanHo) {
        return canHoMap.get(maCanHo);
    }
    
    public CudanDto getCuDan(String maDinhDanh) {
        return cuDanMap.get(maDinhDanh);
    }
    
    public PhuongTienDto getPhuongTien(Integer soThuTu) {
        return phuongTienMap.get(soThuTu);
    }
    
    public HoaDonDto getHoaDon(String maHoaDon) {
        return hoaDonMap.get(maHoaDon);
    }
    
    public KhoanThuDto getKhoanThu(String maKhoanThu) {
        return khoanThuMap.get(maKhoanThu);
    }
    
    public ThongTinTaiKhoanDto getTaiKhoan(String email) {
        return taiKhoanMap.get(email);
    }
    
    // Setters để cache data
    public void setCanHoList(List<CanHoDto> canHoList) {
        this.canHoList = canHoList;
        this.canHoMap.clear();
        if (canHoList != null) {
            this.canHoMap.putAll(canHoList.stream()
                .collect(Collectors.toMap(CanHoDto::getMaCanHo, dto -> dto)));
        }
    }
    
    public void setCuDanList(List<CudanDto> cuDanList) {
        this.cuDanList = cuDanList;
        this.cuDanMap.clear();
        if (cuDanList != null) {
            this.cuDanMap.putAll(cuDanList.stream()
                .collect(Collectors.toMap(CudanDto::getMaDinhDanh, dto -> dto)));
        }
    }
    
    public void setPhuongTienList(List<PhuongTienDto> phuongTienList) {
        this.phuongTienList = phuongTienList;
        this.phuongTienMap.clear();
        if (phuongTienList != null) {
            this.phuongTienMap.putAll(phuongTienList.stream()
                .collect(Collectors.toMap(PhuongTienDto::getSoThuTu, dto -> dto)));
        }
    }
    
    public void setHoaDonList(List<HoaDonDto> hoaDonList) {
        this.hoaDonList = hoaDonList;
        this.hoaDonMap.clear();
        if (hoaDonList != null) {
            this.hoaDonMap.putAll(hoaDonList.stream()
                .collect(Collectors.toMap(dto -> String.valueOf(dto.getMaHoaDon()), dto -> dto)));
        }
    }
    
    public void setKhoanThuList(List<KhoanThuDto> khoanThuList) {
        this.khoanThuList = khoanThuList;
        this.khoanThuMap.clear();
        if (khoanThuList != null) {
            this.khoanThuMap.putAll(khoanThuList.stream()
                .collect(Collectors.toMap(KhoanThuDto::getMaKhoanThu, dto -> dto)));
        }
    }
    
    public void setTaiKhoanList(List<ThongTinTaiKhoanDto> taiKhoanList) {
        this.taiKhoanList = taiKhoanList;
        this.taiKhoanMap.clear();
        if (taiKhoanList != null) {
            this.taiKhoanMap.putAll(taiKhoanList.stream()
                .collect(Collectors.toMap(ThongTinTaiKhoanDto::getEmail, dto -> dto)));
        }
    }
    
    // Utility methods
    public boolean isLoaded() {
        return isLoaded;
    }
    
    public void setLoaded(boolean loaded) {
        this.isLoaded = loaded;
    }
    
    public void clearCache() {
        this.canHoList = null;
        this.cuDanList = null;
        this.phuongTienList = null;
        this.hoaDonList = null;
        this.khoanThuList = null;
        this.taiKhoanList = null;
        
        this.canHoMap.clear();
        this.cuDanMap.clear();
        this.phuongTienMap.clear();
        this.hoaDonMap.clear();
        this.khoanThuMap.clear();
        this.taiKhoanMap.clear();
        
        this.isLoaded = false;
    }
    
    public int getTotalCachedItems() {
        int total = 0;
        if (canHoList != null) total += canHoList.size();
        if (cuDanList != null) total += cuDanList.size();
        if (phuongTienList != null) total += phuongTienList.size();
        if (hoaDonList != null) total += hoaDonList.size();
        if (khoanThuList != null) total += khoanThuList.size();
        if (taiKhoanList != null) total += taiKhoanList.size();
        return total;
    }
} 