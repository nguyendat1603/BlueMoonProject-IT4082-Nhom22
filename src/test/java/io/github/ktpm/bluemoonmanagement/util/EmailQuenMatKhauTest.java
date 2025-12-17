package io.github.ktpm.bluemoonmanagement.util;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import static org.mockito.Mockito.*;

class EmailQuenMatKhauTest {
    @SuppressWarnings("null")
    @Test
    void testSendEmailQuenMatKhauSendsMail() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        String email = "test@example.com";
        String hoTen = "Nguyen Van A";
        String otp = "123456";

        // Only verify that MimeMessage is sent (HTML email)
        doNothing().when(mailSender).send(any(MimeMessage.class));

        EmailUtil.sendEmailQuenMatKhau(email, hoTen, otp, mailSender);
        verify(mailSender, atLeastOnce()).send(any(MimeMessage.class));
    }
}
