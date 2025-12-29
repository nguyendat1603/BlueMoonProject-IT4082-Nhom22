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

    /**
     * Kiểm tra xem user hiện tại có quyền admin không (có tất cả quyền của Tổ trưởng, Tổ phó, Kế toán)
     */
    public static boolean isAdmin() {
        return currentUser != null && "admin".equals(currentUser.getVaiTro());
    }

    /**
     * Kiểm tra xem user hiện tại có quyền của vai trò được chỉ định hoặc là admin không
     */
    public static boolean hasRole(String role) {
        return currentUser != null && (role.equals(currentUser.getVaiTro()) || isAdmin());
    }
}
