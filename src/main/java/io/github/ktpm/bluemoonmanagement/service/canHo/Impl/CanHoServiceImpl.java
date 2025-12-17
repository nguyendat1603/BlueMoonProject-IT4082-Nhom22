package io.github.ktpm.bluemoonmanagement.service.canHo.Impl;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;
import io.github.ktpm.bluemoonmanagement.model.dto.canHo.CanHoChiTietDto;
import io.github.ktpm.bluemoonmanagement.model.dto.canHo.CanHoDto;
import io.github.ktpm.bluemoonmanagement.model.entity.CanHo;
import io.github.ktpm.bluemoonmanagement.model.entity.CuDan;
import io.github.ktpm.bluemoonmanagement.model.entity.HoaDon;
import io.github.ktpm.bluemoonmanagement.model.entity.PhuongTien;
import io.github.ktpm.bluemoonmanagement.model.mapper.CanHoMapper;
import io.github.ktpm.bluemoonmanagement.model.mapper.PhuongTienMapper;
import io.github.ktpm.bluemoonmanagement.repository.CanHoRepository;
import io.github.ktpm.bluemoonmanagement.repository.CuDanRepository;
import io.github.ktpm.bluemoonmanagement.repository.HoaDonRepository;
import io.github.ktpm.bluemoonmanagement.repository.PhuongTienRepository;
import io.github.ktpm.bluemoonmanagement.service.canHo.CanHoService;
import io.github.ktpm.bluemoonmanagement.session.Session;
import io.github.ktpm.bluemoonmanagement.util.XlsxExportUtil;
import io.github.ktpm.bluemoonmanagement.util.XlxsFileUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class CanHoServiceImpl implements CanHoService {

    private final CanHoRepository canHoRepository;
    private final CuDanRepository cuDanRepository;
    private final CanHoMapper canHoMapper;
    private final PhuongTienRepository phuongTienRepository;
    private final PhuongTienMapper phuongTienMapper;
    private final HoaDonRepository hoaDonRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    public CanHoServiceImpl(CanHoRepository canHoRepository, CuDanRepository cuDanRepository, CanHoMapper canHoMapper, PhuongTienRepository phuongTienRepository, PhuongTienMapper phuongTienMapper, HoaDonRepository hoaDonRepository) {
        this.canHoRepository = canHoRepository;
        this.cuDanRepository = cuDanRepository;
        this.canHoMapper = canHoMapper;
        this.phuongTienRepository = phuongTienRepository;
        this.phuongTienMapper = phuongTienMapper;
        this.hoaDonRepository = hoaDonRepository;
    }

    @Override
    public List<CanHoDto> getAllCanHo() {
        List<CanHo> canHoList = canHoRepository.findAll();
        return canHoList.stream()
                .map(canHoMapper::toCanHoDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true, propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public CanHoChiTietDto getCanHoChiTiet(CanHoDto canHoDto) {
        // Clear entity manager cache to ensure fresh data
        entityManager.clear();
        
        // Sử dụng fetch join để load căn hộ cùng với tất cả cư dân
        String jpql = "SELECT DISTINCT c FROM CanHo c LEFT JOIN FETCH c.cuDanList cd WHERE c.maCanHo = :maCanHo";
        List<CanHo> canHoResults = entityManager.createQuery(jpql, CanHo.class)
            .setParameter("maCanHo", canHoDto.getMaCanHo())
            .getResultList();
        
        
        CanHo canHo = canHoResults.isEmpty() ? null : canHoResults.get(0);
        
        if (canHo != null) {
            // Debug: Check all residents loaded by fetch join
            if (canHo.getCuDanList() != null) {
                for (CuDan cuDan : canHo.getCuDanList()) {
                }
            }
            
            // Lấy danh sách phương tiện active
            List<PhuongTien> activePhuongTiens = phuongTienRepository.findActiveByCanHo_MaCanHo(canHoDto.getMaCanHo());
            
            // Sử dụng mapper để chuyển đổi
            CanHoChiTietDto dto = canHoMapper.toCanHoChiTietDto(canHo);
            
            // Override danh sách phương tiện với active vehicles
            List<io.github.ktpm.bluemoonmanagement.model.dto.phuongTien.PhuongTienDto> activePhuongTienDtos = 
                activePhuongTiens.stream()
                    .map(phuongTienMapper::toPhuongTienDto)
                    .collect(java.util.stream.Collectors.toList());
            dto.setPhuongTienList(activePhuongTienDtos);
            
            // Debug: Check mapper results
            if (dto.getCuDanList() != null) {
                for (io.github.ktpm.bluemoonmanagement.model.dto.cuDan.CuDanTrongCanHoDto cuDan : dto.getCuDanList()) {
                }
            }
            
            return dto;
        }
        
        return null;
    }

    @Override
    @Transactional
    public ResponseDto addCanHo(CanHoDto canHoDto) {
        // Kiểm tra quyền: chỉ 'Tổ phó' mới được thêm căn hộ
        if (Session.getCurrentUser() == null || (!"Tổ phó".equals(Session.getCurrentUser().getVaiTro()))) {
            return new ResponseDto(false, "Bạn không có quyền thêm căn hộ. Chỉ Tổ trưởng và Admin mới được phép.");
        }
        
        // Check if an apartment with this code already exists
        if (canHoRepository.existsById(canHoDto.getMaCanHo())) {
            return new ResponseDto(false, "Căn hộ đã tồn tại");
        }
        
        CuDan chuHoCuDan = null;
        
        // Nếu có chủ hộ, xử lý tạo/cập nhật cư dân TRƯỚC
        if (canHoDto.getChuHo() != null) {
            String maDinhDanh = canHoDto.getChuHo().getMaDinhDanh();
            
            // Kiểm tra xem cư dân có tồn tại không
            if (!cuDanRepository.existsById(maDinhDanh)) {
                // Nếu cư dân chưa tồn tại và có thông tin đầy đủ thì tạo mới
                if (canHoDto.getChuHo().getHoVaTen() != null && 
                    !canHoDto.getChuHo().getHoVaTen().trim().isEmpty()) {
                    
                    // Tạo cư dân mới TRƯỚC (không gán căn hộ ngay)
                    CuDan cuDanMoi = new CuDan();
                    cuDanMoi.setMaDinhDanh(maDinhDanh);
                    cuDanMoi.setHoVaTen(canHoDto.getChuHo().getHoVaTen());
                    cuDanMoi.setGioiTinh(canHoDto.getChuHo().getGioiTinh());
                    cuDanMoi.setNgaySinh(canHoDto.getChuHo().getNgaySinh());
                    cuDanMoi.setSoDienThoai(canHoDto.getChuHo().getSoDienThoai());
                    cuDanMoi.setEmail(canHoDto.getChuHo().getEmail());
                    cuDanMoi.setTrangThaiCuTru(canHoDto.getChuHo().getTrangThaiCuTru());
                    
                    // Set ngày chuyển đến nếu là cư trú
                    if ("Cư trú".equals(canHoDto.getChuHo().getTrangThaiCuTru())) {
                        cuDanMoi.setNgayChuyenDen(canHoDto.getChuHo().getNgayChuyenDen() != null ? 
                            canHoDto.getChuHo().getNgayChuyenDen() : LocalDate.now());
                    }
                    
                    // Lưu cư dân KHÔNG có căn hộ trước
                    cuDanRepository.save(cuDanMoi);
                    entityManager.flush(); // Đảm bảo cư dân được lưu vào DB
                    
                    chuHoCuDan = cuDanMoi;
                } else {
                    // Chỉ có mã định danh, cư dân chưa tồn tại
                    return new ResponseDto(false, "Cư dân với mã định danh '" + maDinhDanh + "' không tồn tại trong hệ thống. Vui lòng tạo cư dân trước hoặc kiểm tra lại mã định danh.");
                }
            } else {
                chuHoCuDan = cuDanRepository.findById(maDinhDanh).orElse(null);
                
                // Nếu cư dân đang ở trạng thái "Chuyển đi", chuyển thành "Cư trú"
                if (chuHoCuDan != null && "Chuyển đi".equals(chuHoCuDan.getTrangThaiCuTru())) {
                    chuHoCuDan.setTrangThaiCuTru("Cư trú");
                    chuHoCuDan.setNgayChuyenDen(LocalDate.now()); // Set ngày chuyển đến mới
                    chuHoCuDan.setNgayChuyenDi(null); // Clear ngày chuyển đi
                    cuDanRepository.save(chuHoCuDan);
                    entityManager.flush();
                }
            }
        }
        
        // Tạo căn hộ KHÔNG có chủ hộ reference trước
        CanHo canHo = canHoMapper.fromCanHoDto(canHoDto);
        canHo.setChuHo(null); // Tạm thời không set chủ hộ
        canHoRepository.save(canHo);
        entityManager.flush(); // Đảm bảo căn hộ được lưu vào DB
        
        // Nếu có chủ hộ, cập nhật liên kết
        if (chuHoCuDan != null) {
            // Cập nhật foreign key ma_can_ho cho cư dân
            chuHoCuDan.setCanHo(canHo);
            cuDanRepository.save(chuHoCuDan);
            
            // Cập nhật chủ hộ cho căn hộ
            canHo.setChuHo(chuHoCuDan);
            canHoRepository.save(canHo);
            
        }
        
        return new ResponseDto(true, "Căn hộ đã được thêm thành công" + 
            (canHoDto.getChuHo() != null ? " với chủ hộ có mã: " + canHoDto.getChuHo().getMaDinhDanh() : ""));
    }

    @Override
    @Transactional
    public ResponseDto updateCanHo(CanHoDto canHoDto) {
        if (Session.getCurrentUser() == null || !"Tổ phó".equals(Session.getCurrentUser().getVaiTro())) {
            return new ResponseDto(false, "Bạn không có quyền cập nhật căn hộ. Chỉ Tổ phó mới được phép.");
        }
        
        // Tìm căn hộ hiện có trong database
        CanHo existingCanHo = canHoRepository.findById(canHoDto.getMaCanHo()).orElse(null);
        if (existingCanHo == null) {
            return new ResponseDto(false, "Không tìm thấy căn hộ để cập nhật");
        }
        
        // Cập nhật thông tin căn hộ hiện có (không tạo entity mới)
        existingCanHo.setToaNha(canHoDto.getToaNha());
        existingCanHo.setTang(canHoDto.getTang());
        existingCanHo.setSoNha(canHoDto.getSoNha());
        existingCanHo.setDienTich(canHoDto.getDienTich());
        existingCanHo.setDaBanChua(canHoDto.isDaBanChua());
        existingCanHo.setTrangThaiKiThuat(canHoDto.getTrangThaiKiThuat());
        existingCanHo.setTrangThaiSuDung(canHoDto.getTrangThaiSuDung());
        
        // Xử lý cập nhật chủ hộ nếu có thay đổi
        CuDan oldChuHo = existingCanHo.getChuHo();
        String oldChuHoId = oldChuHo != null ? oldChuHo.getMaDinhDanh() : "null";
        String newChuHoId = (canHoDto.getChuHo() != null && canHoDto.getChuHo().getMaDinhDanh() != null) 
            ? canHoDto.getChuHo().getMaDinhDanh() : "null";
        
        
        if (canHoDto.getChuHo() != null && canHoDto.getChuHo().getMaDinhDanh() != null) {
            CuDan chuHo = cuDanRepository.findById(canHoDto.getChuHo().getMaDinhDanh()).orElse(null);
            if (chuHo != null) {
                
                // Nếu cư dân đang ở trạng thái "Chuyển đi", chuyển thành "Cư trú"
                if ("Chuyển đi".equals(chuHo.getTrangThaiCuTru())) {
                    chuHo.setTrangThaiCuTru("Cư trú");
                    chuHo.setNgayChuyenDen(LocalDate.now()); // Set ngày chuyển đến mới
                    chuHo.setNgayChuyenDi(null); // Clear ngày chuyển đi
                    cuDanRepository.save(chuHo);
                    entityManager.flush();
                }
                
                // Set căn hộ cho cư dân
                chuHo.setCanHo(existingCanHo);
                cuDanRepository.save(chuHo);
                
                // Set chủ hộ cho căn hộ
                existingCanHo.setChuHo(chuHo);
            } else {
            }
        } else {
            existingCanHo.setChuHo(null);
        }
        
        // Lưu changes
        canHoRepository.save(existingCanHo);
        entityManager.flush(); // Đảm bảo changes được commit ngay
        
        // Clear cache để đảm bảo dữ liệu mới được load
        entityManager.clear();
        
        // Verify update thành công
        CanHo verifyCanHo = canHoRepository.findById(canHoDto.getMaCanHo()).orElse(null);

        
        
        return new ResponseDto(true, "Căn hộ đã được cập nhật thành công");
    }

    @Override
    @Transactional
    public ResponseDto deleteCanHo(CanHoDto canHoDto) {
        if (Session.getCurrentUser() == null || !"Tổ phó".equals(Session.getCurrentUser().getVaiTro())) {
            return new ResponseDto(false, "Bạn không có quyền xóa căn hộ. Chỉ Tổ phó mới được phép.");
        }
        
        try {
            String maCanHo = canHoDto.getMaCanHo();
            
            // Tìm căn hộ trước để đảm bảo nó tồn tại
            CanHo canHo = canHoRepository.findById(maCanHo).orElse(null);
            if (canHo == null) {
                return new ResponseDto(false, "Không tìm thấy căn hộ để xóa");
            }
            
            // 1. Trước tiên, xóa reference từ căn hộ đến chủ hộ để tránh constraint violation
            if (canHo.getChuHo() != null) {
                canHo.setChuHo(null);
                canHoRepository.save(canHo);
                entityManager.flush();
            }
            
            // 2. Xóa tất cả hóa đơn của căn hộ
            List<HoaDon> hoaDonList = hoaDonRepository.findAll().stream()
                .filter(hoaDon -> hoaDon.getCanHo() != null && maCanHo.equals(hoaDon.getCanHo().getMaCanHo()))
                .collect(java.util.stream.Collectors.toList());
            
            if (!hoaDonList.isEmpty()) {
                hoaDonRepository.deleteAll(hoaDonList);
                entityManager.flush();
            }

            // 3. Xóa tất cả phương tiện của căn hộ
            List<io.github.ktpm.bluemoonmanagement.model.entity.PhuongTien> allPhuongTienList = 
                phuongTienRepository.findAll().stream()
                    .filter(pt -> pt.getCanHo() != null && maCanHo.equals(pt.getCanHo().getMaCanHo()))
                    .collect(java.util.stream.Collectors.toList());
            
            if (!allPhuongTienList.isEmpty()) {
                phuongTienRepository.deleteAll(allPhuongTienList);
                entityManager.flush();
            }
            
            // 4. Cập nhật trạng thái cư dân thành "Đã chuyển đi" và xóa reference căn hộ
            List<io.github.ktpm.bluemoonmanagement.model.entity.CuDan> allCuDanList = 
                cuDanRepository.findAll().stream()
                    .filter(cuDan -> cuDan.getCanHo() != null && 
                                   maCanHo.equals(cuDan.getCanHo().getMaCanHo()))
                    .collect(java.util.stream.Collectors.toList());
            
            if (!allCuDanList.isEmpty()) {
                for (io.github.ktpm.bluemoonmanagement.model.entity.CuDan cuDan : allCuDanList) {
                    
                    // Cập nhật trạng thái thành "Đã chuyển đi"
                    cuDan.setTrangThaiCuTru("Đã chuyển đi");
                    cuDan.setNgayChuyenDi(java.time.LocalDate.now());
                    cuDan.setNgayChuyenDen(null); // Clear ngày chuyển đến
                    
                    // Xóa reference căn hộ
                    cuDan.setCanHo(null);
                    
                }
                cuDanRepository.saveAll(allCuDanList);
                entityManager.flush();
            }
            
            // 5. Cuối cùng, xóa căn hộ
            canHoRepository.deleteById(maCanHo);
            entityManager.flush();
            
            return new ResponseDto(true, "Căn hộ đã được xóa thành công. Cư dân và hóa đơn, phương tiện liên quan đã được xử lý.");
            
        } catch (Exception e) {
            System.err.println("ERROR: Lỗi khi xóa căn hộ: " + e.getMessage());
            e.printStackTrace();
            return new ResponseDto(false, "Lỗi khi xóa căn hộ: " + e.getMessage());
        }
    }

    @Override
    public ResponseDto importFromExcel(MultipartFile file) {
        if (Session.getCurrentUser() == null || !"Tổ phó".equals(Session.getCurrentUser().getVaiTro())) {
            return new ResponseDto(false, "Bạn không có quyền nhập Excel căn hộ. Chỉ Tổ phó mới được phép.");
        }
        try {
            File tempFile = File.createTempFile("canho_temp", ".xlsx");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(file.getBytes());
            }
            Function<Row, CanHoDto> rowMapper = row -> {
                try {
                    // Skip header row (row index 0)
                    if (row.getRowNum() == 0) {
                        return null;
                    }
                    
                    CanHoDto canHoDto = new CanHoDto();
                    
                    // Đọc mã căn hộ (column 0)
                    String maCanHo = "";
                    if (row.getCell(0) != null) {
                        try {
                            maCanHo = row.getCell(0).getStringCellValue();
                        } catch (Exception e) {
                            maCanHo = String.valueOf((int) row.getCell(0).getNumericCellValue());
                        }
                    }
                    canHoDto.setMaCanHo(maCanHo);
                    
                    // Đọc tòa nhà (column 1)  
                    String toaNha = "";
                    if (row.getCell(1) != null) {
                        try {
                            toaNha = row.getCell(1).getStringCellValue();
                        } catch (Exception e) {
                            toaNha = String.valueOf((int) row.getCell(1).getNumericCellValue());
                        }
                    }
                    canHoDto.setToaNha(toaNha);
                    
                    // Đọc tầng (column 2)
                    String tang = "";
                    if (row.getCell(2) != null) {
                        try {
                            tang = row.getCell(2).getStringCellValue();
                        } catch (Exception e) {
                            tang = String.valueOf((int) row.getCell(2).getNumericCellValue());
                        }
                    }
                    canHoDto.setTang(tang);
                    
                    // Đọc số nhà (column 3)
                    String soNha = "";
                    if (row.getCell(3) != null) {
                        try {
                            soNha = row.getCell(3).getStringCellValue();
                        } catch (Exception e) {
                            soNha = String.valueOf((int) row.getCell(3).getNumericCellValue());
                        }
                    }
                    canHoDto.setSoNha(soNha);
                    
                    // Đọc diện tích (column 4)
                    double dienTich = 0.0;
                    if (row.getCell(4) != null) {
                        try {
                            dienTich = row.getCell(4).getNumericCellValue();
                        } catch (Exception e) {
                            // Thử đọc như string và parse
                            String dienTichStr = row.getCell(4).getStringCellValue();
                            dienTich = Double.parseDouble(dienTichStr);
                        }
                    }
                    canHoDto.setDienTich(dienTich);
                    
                    canHoDto.setChuHo(null);
                    
                    // Xử lý đã bán chưa (column 5) 
                    boolean daBanChua = false;
                    if (row.getCell(5) != null) {
                        try {
                            daBanChua = row.getCell(5).getBooleanCellValue();
                        } catch (Exception e) {
                            // Thử đọc như string và parse
                            String boolStr = row.getCell(5).getStringCellValue().toLowerCase();
                            daBanChua = "true".equals(boolStr) || "có".equals(boolStr) || "1".equals(boolStr);
                        }
                    }
                    canHoDto.setDaBanChua(daBanChua);
                    
                    // Đọc trạng thái kỹ thuật (column 6)
                    String trangThaiKiThuat = "";
                    if (row.getCell(6) != null) {
                        try {
                            trangThaiKiThuat = row.getCell(6).getStringCellValue();
                        } catch (Exception e) {
                            trangThaiKiThuat = String.valueOf(row.getCell(6).getNumericCellValue());
                        }
                    }
                    canHoDto.setTrangThaiKiThuat(trangThaiKiThuat);
                    
                    // Đọc trạng thái sử dụng (column 7)
                    String trangThaiSuDung = "";
                    if (row.getCell(7) != null) {
                        try {
                            trangThaiSuDung = row.getCell(7).getStringCellValue();
                        } catch (Exception e) {
                            trangThaiSuDung = String.valueOf(row.getCell(7).getNumericCellValue());
                        }
                    }
                    canHoDto.setTrangThaiSuDung(trangThaiSuDung);
                    
                    return canHoDto;
                } catch (Exception e) {
                    System.err.println("ERROR: Lỗi khi đọc dòng Excel căn hộ row " + row.getRowNum() + ": " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            };
            List<CanHoDto> canHoDtoList = XlxsFileUtil.importFromExcel(tempFile.getAbsolutePath(), rowMapper);
            List<CanHo> canHoList = canHoDtoList.stream()
                    .filter(dto -> dto != null) // Lọc bỏ các dòng lỗi (null)
                    .map(canHoMapper::fromCanHoDto)
                    .collect(Collectors.toList());
            canHoRepository.saveAll(canHoList);
            tempFile.delete();
            return new ResponseDto(true, "Thêm căn hộ thành công" + canHoDtoList.size() + " căn hộ");
        } catch (Exception e) {
            return new ResponseDto(false, "Thêm căn hộ thất bại: " + e.getMessage());
        }
    }
    @Override
    public ResponseDto exportToExcel(String filePath) {
        if (Session.getCurrentUser() == null || !"Tổ phó".equals(Session.getCurrentUser().getVaiTro())) {
            return new ResponseDto(false, "Bạn không có quyền xuất căn hộ. Chỉ Tổ phó mới được phép.");
        }
        List<CanHoDto> canHoDtoList = getAllCanHo();
        String[] headers = {"Mã căn hộ", "Tòa nhà", "Tầng", "Số nhà", "Diện tích", "Đã bán/chưa", "Trạng thái kỹ thuật", "Trạng thái sử dụng"};
        try {
            XlsxExportUtil.exportToExcel(filePath, headers, canHoDtoList, (row, canHoDto) -> {
                row.createCell(0).setCellValue(canHoDto.getMaCanHo() != null ? canHoDto.getMaCanHo() : "");
                row.createCell(1).setCellValue(canHoDto.getToaNha() != null ? canHoDto.getToaNha() : "");
                row.createCell(2).setCellValue(canHoDto.getTang() != null ? canHoDto.getTang() : "");
                row.createCell(3).setCellValue(canHoDto.getSoNha() != null ? canHoDto.getSoNha() : "");
                row.createCell(4).setCellValue(canHoDto.getDienTich());
                row.createCell(5).setCellValue(canHoDto.isDaBanChua());
                row.createCell(6).setCellValue(canHoDto.getTrangThaiKiThuat() != null ? canHoDto.getTrangThaiKiThuat() : "");
                row.createCell(7).setCellValue(canHoDto.getTrangThaiSuDung() != null ? canHoDto.getTrangThaiSuDung() : "");
            });
            return new ResponseDto(true, "Xuất căn hộ thành công");
        } catch (Exception e) {
            return new ResponseDto(false, "Xuất căn hộ thất bại: " + e.getMessage());
        }
    }
}
