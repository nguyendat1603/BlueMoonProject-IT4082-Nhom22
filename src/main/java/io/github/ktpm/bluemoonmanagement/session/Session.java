package io.github.ktpm.bluemoonmanagement.session;

import io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.ThongTinTaiKhoanDto;

public class Session {
    private static ThongTinTaiKhoanDto currentUser;

    public static void setCurrentUser(ThongTinTaiKhoanDto user) {
        currentUser = user;
    }

    public static ThongTinTaiKhoanDto getCurrentUser() {
        return currentUser;
    }

    public static void clear() {
        currentUser = null;
    }
}
