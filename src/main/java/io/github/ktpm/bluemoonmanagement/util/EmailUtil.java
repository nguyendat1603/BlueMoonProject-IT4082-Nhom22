package io.github.ktpm.bluemoonmanagement.util;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

public class EmailUtil {
    private EmailUtil() {}

    public static void sendEmail(JavaMailSender mailSender, String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    public static void sendEmail(JavaMailSender mailSender, String to, String subject, String content, boolean isHtml) {
        if (isHtml) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(content, true);
                mailSender.send(message);
            } catch (MessagingException e) {
                // Nếu gửi HTML thất bại, fallback sang gửi text thuần
                System.err.println("Gửi email HTML thất bại, thử gửi dạng text: " + e.getMessage());
                sendEmail(mailSender, to, subject, content);
            }
        } else {
            sendEmail(mailSender, to, subject, content);
        }
    }

    /**
     * Gửi mã OTP qua email cho tài khoản (HTML)
     * @param email Địa chỉ email nhận OTP
     * @param hoTen Họ tên người nhận
     * @param otp Mã OTP
     * @param mailSender JavaMailSender để gửi email
     */
    public static void sendEmailQuenMatKhau(String email, String hoTen, String otp, JavaMailSender mailSender) {
        String subject = "Mã xác thực OTP";
        String content = String.format(
            """
            <div style='font-family:Arial,sans-serif;'>
                <h2>Xin chào %s,</h2>
                <p>Bạn vừa yêu cầu lấy mã xác thực OTP để đặt lại mật khẩu.</p>
                <ul>
                    <li><b>Mã OTP:</b> <span style='color:black;font-size:18px;font-weight:bold;'>%s</span></li>
                    <li><b>Thời hạn hiệu lực:</b> 5 phút kể từ khi nhận email này</li>
                </ul>
                <p style='color:red;'><b>Lưu ý:</b> Không cung cấp mã OTP cho bất kỳ ai. Không phản hồi email này.</p>
                <p>Trân trọng!</p>
            </div>
            """,
            hoTen != null ? hoTen : "bạn",
            otp
        );
        sendEmail(mailSender, email, subject, content, true);
    }

    /**
     * Gửi email thông tin tài khoản đăng ký cho người dùng (HTML)
     * @param mailSender JavaMailSender để gửi email
     * @param to Địa chỉ email người nhận
     * @param hoTen Họ tên người nhận
     * @param email Tên đăng nhập (email)
     * @param matKhau Mật khẩu
     */
    public static void sendEmailDangKi(JavaMailSender mailSender, String to, String hoTen, String email, String matKhau) {
        String subject = "Thông tin tài khoản BlueMoonManagement";
        String content = String.format(
            """
            <div style='font-family:Arial,sans-serif;'>
                <h2>Chào %s,</h2>
                <p>Tài khoản của bạn đã được tạo thành công.</p>
                <ul>
                    <li><b>Họ tên:</b> %s</li>
                    <li><b>Tên đăng nhập (Email):</b> %s</li>
                    <li><b>Mật khẩu:</b> %s</li>
                </ul>
                <p style='color:red;'><b>Lưu ý:</b> Không cung cấp thông tin tài khoản cho bất kỳ ai. Không phản hồi email này.</p>
                <p>Vui lòng đổi mật khẩu sau khi đăng nhập lần đầu.</p>
                <p>Trân trọng!</p>
            </div>
            """,
            hoTen,
            hoTen,
            email,
            matKhau
        );
        sendEmail(mailSender, to, subject, content, true);
    }
}
