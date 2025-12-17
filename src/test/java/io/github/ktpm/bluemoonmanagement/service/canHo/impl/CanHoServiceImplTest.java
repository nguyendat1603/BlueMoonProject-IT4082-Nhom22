package io.github.ktpm.bluemoonmanagement.service.canHo.impl;

import io.github.ktpm.bluemoonmanagement.model.dto.canHo.CanHoDto;
import io.github.ktpm.bluemoonmanagement.model.entity.CanHo;
import io.github.ktpm.bluemoonmanagement.model.mapper.CanHoMapper;
import io.github.ktpm.bluemoonmanagement.repository.CanHoRepository;
import io.github.ktpm.bluemoonmanagement.service.canHo.CanHoService;
import io.github.ktpm.bluemoonmanagement.service.canHo.Impl.CanHoServiceImpl;
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

class CanHoServiceImplTest {
    @Mock
    private CanHoRepository canHoRepository;
    @Mock
    private CanHoMapper canHoMapper;
    @InjectMocks
    private CanHoServiceImpl canHoServiceImpl;
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
    void testGetAllCanHo() {
        List<CanHo> list = new ArrayList<>();
        list.add(new CanHo());
        when(canHoRepository.findAll()).thenReturn(list);
        when(canHoMapper.toCanHoDto(any())).thenReturn(new CanHoDto());
        List<CanHoDto> result = canHoServiceImpl.getAllCanHo();
        assertEquals(1, result.size());
    }

    @Test
    void testAddCanHo() {
        CanHoDto dto = new CanHoDto();
        when(canHoMapper.fromCanHoDto(any())).thenReturn(new CanHo());
        when(canHoRepository.save(any())).thenReturn(new CanHo());
        assertTrue(canHoServiceImpl.addCanHo(dto).isSuccess());
    }

    @Test
    void testUpdateCanHo() {
        CanHoDto dto = new CanHoDto();
        when(canHoRepository.existsById(any())).thenReturn(true);
        when(canHoMapper.fromCanHoDto(any())).thenReturn(new CanHo());
        when(canHoRepository.save(any())).thenReturn(new CanHo());
        assertTrue(canHoServiceImpl.updateCanHo(dto).isSuccess());
    }

    @Test
    void testDeleteCanHo() {
        when(canHoRepository.existsById(any())).thenReturn(true);
        doNothing().when(canHoRepository).deleteById(any());
        CanHoDto canHoDto = new CanHoDto();
        canHoDto.setMaCanHo("1-3-7");
        assertTrue(canHoServiceImpl.deleteCanHo(canHoDto).isSuccess());
    }
}
