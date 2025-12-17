package io.github.ktpm.bluemoonmanagement.service.phuongTien.impl;

import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;
import io.github.ktpm.bluemoonmanagement.model.dto.phuongTien.PhuongTienDto;
import io.github.ktpm.bluemoonmanagement.model.entity.PhuongTien;
import io.github.ktpm.bluemoonmanagement.model.entity.CanHo;
import io.github.ktpm.bluemoonmanagement.model.mapper.PhuongTienMapper;
import io.github.ktpm.bluemoonmanagement.repository.PhuongTienRepository;
import io.github.ktpm.bluemoonmanagement.repository.CanHoRepository;
import io.github.ktpm.bluemoonmanagement.session.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PhuongTienServiceImplTest {
    @Mock
    private PhuongTienRepository phuongTienRepository;
    @Mock
    private CanHoRepository canHoRepository;
    @Mock
    private PhuongTienMapper phuongTienMapper;
    @InjectMocks
    private PhuongTienServiceImpl phuongTienServiceImpl;
    private AutoCloseable mocks;

    @BeforeEach
    void setUp() throws Exception {
        mocks = MockitoAnnotations.openMocks(this);
        Session.clear();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mocks != null) mocks.close();
    }

    @Test
    void testThemPhuongTien_Success() {
        Session.setCurrentUser(new io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.ThongTinTaiKhoanDto("a","b","Tổ phó",null,null));
        PhuongTienDto dto = new PhuongTienDto();
        dto.setBienSo("123");
        dto.setMaCanHo("CH01");
        CanHo canHo = new CanHo();
        canHo.setMaCanHo("CH01");
        when(phuongTienRepository.existsByBienSo(any())).thenReturn(false);
        when(canHoRepository.findById(any())).thenReturn(Optional.of(canHo));
        when(phuongTienMapper.fromPhuongTienDto(any())).thenReturn(new PhuongTien());
        when(phuongTienRepository.save(any())).thenReturn(new PhuongTien());
        ResponseDto response = phuongTienServiceImpl.themPhuongTien(dto);
        assertTrue(response.isSuccess());
    }

    @Test
    void testThemPhuongTien_NoPermission() {
        Session.clear();
        PhuongTienDto dto = new PhuongTienDto();
        ResponseDto response = phuongTienServiceImpl.themPhuongTien(dto);
        assertFalse(response.isSuccess());
    }

    @Test
    void testCapNhatPhuongTien_Success() {
        Session.setCurrentUser(new io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.ThongTinTaiKhoanDto("a","b","Tổ phó",null,null));
        PhuongTienDto dto = new PhuongTienDto();
        dto.setSoThuTu(1);
        dto.setBienSo("123");
        PhuongTien phuongTien = new PhuongTien();
        phuongTien.setBienSo("123");
        when(phuongTienRepository.findById(any())).thenReturn(Optional.of(phuongTien));
        when(phuongTienRepository.existsByBienSo(any())).thenReturn(false);
        when(phuongTienRepository.save(any())).thenReturn(phuongTien);
        ResponseDto response = phuongTienServiceImpl.capNhatPhuongTien(dto);
        assertTrue(response.isSuccess());
    }

    @Test
    void testCapNhatPhuongTien_NoPermission() {
        Session.clear();
        PhuongTienDto dto = new PhuongTienDto();
        ResponseDto response = phuongTienServiceImpl.capNhatPhuongTien(dto);
        assertFalse(response.isSuccess());
    }

    @Test
    void testXoaPhuongTien_Success() {
        Session.setCurrentUser(new io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.ThongTinTaiKhoanDto("a","b","Tổ phó",null,null));
        PhuongTienDto dto = new PhuongTienDto();
        dto.setSoThuTu(1);
        PhuongTien phuongTien = new PhuongTien();
        when(phuongTienRepository.findById(any())).thenReturn(Optional.of(phuongTien));
        when(phuongTienRepository.save(any())).thenReturn(phuongTien);
        ResponseDto response = phuongTienServiceImpl.xoaPhuongTien(dto);
        assertTrue(response.isSuccess());
        assertEquals(LocalDate.now(), phuongTien.getNgayHuyDangKy());
    }

    @Test
    void testXoaPhuongTien_NoPermission() {
        Session.clear();
        PhuongTienDto dto = new PhuongTienDto();
        ResponseDto response = phuongTienServiceImpl.xoaPhuongTien(dto);
        assertFalse(response.isSuccess());
    }
}
