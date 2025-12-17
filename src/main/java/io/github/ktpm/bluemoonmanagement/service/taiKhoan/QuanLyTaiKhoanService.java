package io.github.ktpm.bluemoonmanagement.service.taiKhoan;

import io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.ThongTinTaiKhoanDto;
import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;

import java.util.List;

public interface QuanLyTaiKhoanService {
    List<ThongTinTaiKhoanDto> layDanhSachTaiKhoan();
    ResponseDto thayDoiThongTinTaiKhoan(ThongTinTaiKhoanDto thongTinTaiKhoanDto);
    ResponseDto xoaTaiKhoan(ThongTinTaiKhoanDto thongTinTaiKhoanDto);
}
