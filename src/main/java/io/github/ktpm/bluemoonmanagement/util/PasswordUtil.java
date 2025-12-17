package io.github.ktpm.bluemoonmanagement.util;

import com.password4j.Password;

import java.security.SecureRandom;

import com.password4j.Hash;

public class PasswordUtil {
    private static final int PASSWORD_LENGTH = 10;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";

    // Sinh mật khẩu ngẫu nhiên
    public static String generatePassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

    // Băm mật khẩu bằng Argon2id
    public static String hashPassword(String password) {
        Hash hash = Password.hash(password).addRandomSalt().withArgon2();
        return hash.getResult();
    }

    // Xác thực mật khẩu với hash đã lưu
    public static boolean verifyPassword(String password, String hashedPassword) {
        return Password.check(password, hashedPassword).withArgon2();
    }

    // Kiểm tra mật khẩu có đúng định dạng: chứa chữ hoa, số và ký tự đặc biệt !@#$%^&*
    public static boolean isValidPasswordFormat(String password) {
        if (password == null) return false;
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(c -> ("!@#$%^&*".indexOf(c)) >= 0);
        return hasUpper && hasDigit && hasSpecial;
    }
}
