package io.github.ktpm.bluemoonmanagement.service.phuongTien;


import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;
import io.github.ktpm.bluemoonmanagement.model.dto.phuongTien.PhuongTienDto;

public interface PhuongTienService {
    ResponseDto themPhuongTien(PhuongTienDto phuongTienDto);
    ResponseDto capNhatPhuongTien(PhuongTienDto phuongTienDto);
    ResponseDto xoaPhuongTien(PhuongTienDto phuongTienDto);
}
