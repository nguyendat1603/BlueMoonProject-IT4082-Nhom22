package io.github.ktpm.bluemoonmanagement.service.khoanThu.impl;

import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;
import io.github.ktpm.bluemoonmanagement.model.dto.khoanThu.KhoanThuDto;
import io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.ThongTinTaiKhoanDto;
import io.github.ktpm.bluemoonmanagement.model.entity.KhoanThu;
import io.github.ktpm.bluemoonmanagement.model.mapper.KhoanThuMapper;
import io.github.ktpm.bluemoonmanagement.repository.KhoanThuRepository;
import io.github.ktpm.bluemoonmanagement.service.khoanThu.KhoanThuService;
import io.github.ktpm.bluemoonmanagement.service.khoanThu.Impl.KhoanThuServiceImpl;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class KhoanThuServiceImplTest {
    @Mock
    private KhoanThuRepository khoanThuRepository;
    @Mock
    private KhoanThuMapper khoanThuMapper;
    @InjectMocks
    private KhoanThuServiceImpl khoanThuServiceImpl;
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
    void testGetAllKhoanThu() {
        List<KhoanThu> khoanThuList = new ArrayList<>();
        KhoanThu khoanThu = new KhoanThu();
        khoanThuList.add(khoanThu);
        when(khoanThuRepository.findAllWithPhiGuiXe()).thenReturn(khoanThuList);
        when(khoanThuMapper.toKhoanThuDto(any())).thenReturn(new KhoanThuDto());
        List<KhoanThuDto> result = khoanThuServiceImpl.getAllKhoanThu();
        assertEquals(1, result.size());
    }

    @Test
    void testAddKhoanThu() {
        Session.setCurrentUser(new ThongTinTaiKhoanDto("a","b","Kế toán",null,null));
        KhoanThuDto dto = new KhoanThuDto();
        when(khoanThuRepository.existsById(any())).thenReturn(false);
        when(khoanThuMapper.fromKhoanThuDto(any())).thenReturn(new KhoanThu());
        when(khoanThuRepository.save(any())).thenReturn(new KhoanThu());
        ResponseDto response = khoanThuServiceImpl.addKhoanThu(dto);
        assertTrue(response.isSuccess());
    }

    @Test
    void testUpdateKhoanThu() {
        Session.setCurrentUser(new ThongTinTaiKhoanDto("a","b","Kế toán",null,null));
        KhoanThuDto dto = new KhoanThuDto();
        dto.setMaKhoanThu("KT01");
        KhoanThu existing = new KhoanThu();
        existing.setTaoHoaDon(false);
        when(khoanThuRepository.findById(any())).thenReturn(Optional.of(existing));
        when(khoanThuMapper.fromKhoanThuDto(any())).thenReturn(new KhoanThu());
        when(khoanThuRepository.save(any())).thenReturn(new KhoanThu());
        ResponseDto response = khoanThuServiceImpl.updateKhoanThu(dto);
        assertTrue(response.isSuccess());
    }

    @Test
    void testDeleteKhoanThu() {
        Session.setCurrentUser(new ThongTinTaiKhoanDto("a","b","Kế toán",null,null));
        KhoanThu khoanThu = new KhoanThu();
        khoanThu.setTaoHoaDon(false);
        when(khoanThuRepository.findById(any())).thenReturn(Optional.of(khoanThu));
        doNothing().when(khoanThuRepository).deleteById(any());
        ResponseDto response = khoanThuServiceImpl.deleteKhoanThu("KT01");
        assertTrue(response.isSuccess());
    }

    @Test
    void testAddKhoanThu_NoPermission() {
        Session.clear();
        KhoanThuDto dto = new KhoanThuDto();
        ResponseDto response = khoanThuServiceImpl.addKhoanThu(dto);
        assertFalse(response.isSuccess());
    }

    @Test
    void testUpdateKhoanThu_NoPermission() {
        Session.clear();
        KhoanThuDto dto = new KhoanThuDto();
        ResponseDto response = khoanThuServiceImpl.updateKhoanThu(dto);
        assertFalse(response.isSuccess());
    }

    @Test
    void testDeleteKhoanThu_NoPermission() {
        Session.clear();
        ResponseDto response = khoanThuServiceImpl.deleteKhoanThu("KT01");
        assertFalse(response.isSuccess());
    }

    @Test
    void testUpdateKhoanThu_NotFound() {
        Session.setCurrentUser(new io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.ThongTinTaiKhoanDto("a","b","Kế toán",null,null));
        KhoanThuDto dto = new KhoanThuDto();
        dto.setMaKhoanThu("KT01");
        when(khoanThuRepository.findById(any())).thenReturn(Optional.empty());
        ResponseDto response = khoanThuServiceImpl.updateKhoanThu(dto);
        assertFalse(response.isSuccess());
    }

    @Test
    void testDeleteKhoanThu_AlreadyHasInvoice() {
        Session.setCurrentUser(new io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.ThongTinTaiKhoanDto("a","b","Kế toán",null,null));
        KhoanThu khoanThu = new KhoanThu();
        khoanThu.setTaoHoaDon(true);
        when(khoanThuRepository.findById(any())).thenReturn(Optional.of(khoanThu));
        ResponseDto response = khoanThuServiceImpl.deleteKhoanThu("KT01");
        assertFalse(response.isSuccess());
    }
}
