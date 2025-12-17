package io.github.ktpm.bluemoonmanagement.service.cuDan.impl;

import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;
import io.github.ktpm.bluemoonmanagement.model.dto.cuDan.CudanDto;
import io.github.ktpm.bluemoonmanagement.model.entity.CuDan;
import io.github.ktpm.bluemoonmanagement.model.mapper.CuDanMapper;
import io.github.ktpm.bluemoonmanagement.repository.CanHoRepository;
import io.github.ktpm.bluemoonmanagement.repository.CuDanRepository;
import io.github.ktpm.bluemoonmanagement.service.cuDan.CuDanService;
import io.github.ktpm.bluemoonmanagement.session.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.MockitoSession;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CuDanServiceImplTest {
    @Mock
    private CuDanRepository cuDanRepository;
    @Mock
    private CanHoRepository canHoRepository;
    @Mock
    private CuDanMapper cuDanMapper;
    @InjectMocks
    private CuDanServiceImpl cuDanServiceImpl;
    @Mock
    private EntityManager entityManager;
    private AutoCloseable mocks;

    @BeforeEach
    void setUp() throws Exception {
        mocks = MockitoAnnotations.openMocks(this);
        Session.clear();
        // Inject mock entityManager using reflection
        java.lang.reflect.Field emField = CuDanServiceImpl.class.getDeclaredField("entityManager");
        emField.setAccessible(true);
        emField.set(cuDanServiceImpl, entityManager);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mocks != null) mocks.close();
    }

    @Test
    void testGetAllCuDan() {
        List<CuDan> cuDanList = new ArrayList<>();
        CuDan cuDan = new CuDan();
        cuDanList.add(cuDan);
        when(cuDanRepository.findAllWithCanHo()).thenReturn(cuDanList);
        when(cuDanMapper.toCudanDto(any())).thenReturn(new CudanDto());
        List<CudanDto> result = cuDanServiceImpl.getAllCuDan();
        assertEquals(1, result.size());
    }

    @Test
    void testAddCuDan_NoPermission() {
        Session.clear();
        CudanDto dto = new CudanDto();
        ResponseDto response = cuDanServiceImpl.addCuDan(dto);
        assertFalse(response.isSuccess());
    }

    @Test
    void testAddCuDan_AlreadyExists() {
        Session.setCurrentUser(new io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.ThongTinTaiKhoanDto("vietanh24012005@gmail.com","Vanh ne","Tổ phó",null,null));
        CudanDto dto = new CudanDto();
        when(cuDanRepository.existsById(any())).thenReturn(true);
        ResponseDto response = cuDanServiceImpl.addCuDan(dto);
        assertFalse(response.isSuccess());
    }

    @Test
    void testAddCuDan_Success() {
        Session.setCurrentUser(new io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.ThongTinTaiKhoanDto("a","b","Tổ phó",null,null));
        CudanDto dto = new CudanDto();
        dto.setMaDinhDanh("123");
        when(cuDanRepository.existsById(any())).thenReturn(false);
        when(cuDanMapper.fromCudanDto(any())).thenReturn(new CuDan());
        when(cuDanRepository.save(any())).thenReturn(new CuDan());
        ResponseDto response = cuDanServiceImpl.addCuDan(dto);
        assertTrue(response.isSuccess());
    }

    @Test
    void testUpdateCuDan_NoPermission() {
        Session.clear();
        CudanDto dto = new CudanDto();
        ResponseDto response = cuDanServiceImpl.updateCuDan(dto);
        assertFalse(response.isSuccess());
    }

    @Test
    void testUpdateCuDan_NotFound() {
        Session.setCurrentUser(new io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.ThongTinTaiKhoanDto("a","b","Tổ phó",null,null));
        CudanDto dto = new CudanDto();
        when(cuDanRepository.existsById(any())).thenReturn(false);
        ResponseDto response = cuDanServiceImpl.updateCuDan(dto);
        assertFalse(response.isSuccess());
    }

    @Test
    void testUpdateCuDan_Success() {
        Session.setCurrentUser(new io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.ThongTinTaiKhoanDto("a","b","Tổ phó",null,null));
        CudanDto dto = new CudanDto();
        dto.setMaDinhDanh("123");
        when(cuDanRepository.existsById(any())).thenReturn(true);
        when(cuDanRepository.findById(any())).thenReturn(Optional.of(new CuDan()));
        when(cuDanMapper.fromCudanDto(any())).thenReturn(new CuDan());
        when(cuDanRepository.save(any())).thenReturn(new CuDan());
        ResponseDto response = cuDanServiceImpl.updateCuDan(dto);
        assertTrue(response.isSuccess());
    }

    @Test
    void testDeleteCuDan_NoPermission() {
        Session.clear();
        CudanDto dto = new CudanDto();
        ResponseDto response = cuDanServiceImpl.deleteCuDan(dto);
        assertFalse(response.isSuccess());
    }

    @Test
    void testDeleteCuDan_NotFound() {
        Session.setCurrentUser(new io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.ThongTinTaiKhoanDto("a","b","Tổ phó",null,null));
        CudanDto dto = new CudanDto();
        when(cuDanRepository.existsById(any())).thenReturn(false);
        ResponseDto response = cuDanServiceImpl.deleteCuDan(dto);
        assertFalse(response.isSuccess());
    }

    @Test
    void testDeleteCuDan_Success() {
        Session.setCurrentUser(new io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.ThongTinTaiKhoanDto("a","b","Tổ phó",null,null));
        CudanDto dto = new CudanDto();
        dto.setMaDinhDanh("123");
        CuDan cuDan = new CuDan();
        when(cuDanRepository.existsById(any())).thenReturn(true);
        when(cuDanRepository.findById(any())).thenReturn(Optional.of(cuDan));
        when(cuDanRepository.save(any())).thenReturn(cuDan);
        ResponseDto response = cuDanServiceImpl.deleteCuDan(dto);
        assertTrue(response.isSuccess());
    }

    @Test
    void testXoaMem_NoPermission() {
        Session.clear();
        boolean result = cuDanServiceImpl.xoaMem("123");
        assertFalse(result);
    }

    @Test
    void testXoaMem_NotFound() {
        Session.setCurrentUser(new io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.ThongTinTaiKhoanDto("a","b","Tổ phó",null,null));
        when(cuDanRepository.findById(any())).thenReturn(Optional.empty());
        boolean result = cuDanServiceImpl.xoaMem("123");
        assertFalse(result);
    }

    @Test
    void testXoaMem_Success() {
        Session.setCurrentUser(new io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.ThongTinTaiKhoanDto("a","b","Tổ phó",null,null));
        CuDan cuDan = new CuDan();
        cuDan.setMaDinhDanh("123");
        cuDan.setTrangThaiCuTru("Cư trú");
        when(cuDanRepository.findById(any())).thenReturn(Optional.of(cuDan));
        when(cuDanRepository.save(any())).thenReturn(cuDan);
        // Mock the mapper to avoid NullPointerException
        when(cuDanMapper.toCudanDto(any())).thenReturn(new CudanDto());
        boolean result = cuDanServiceImpl.xoaMem("123");
        assertTrue(result);
    }

    // You can add more tests for importFromExcel and exportToExcel if needed, using mocks for file operations.
}
