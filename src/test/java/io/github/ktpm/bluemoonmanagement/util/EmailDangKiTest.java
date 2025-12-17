package io.github.ktpm.bluemoonmanagement.util;

import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

public class EmailDangKiTest {
    @Test
    void testSendEmailDangKiSendsMail() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        // Mock MimeMessage in case the util tries to use it
        jakarta.mail.internet.MimeMessage mimeMessage = mock(jakarta.mail.internet.MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        String email = "test@example.com";
        String hoTen = "Nguyen Van B";
        String username = "user123";
        String password = "pass123";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        doNothing().when(mailSender).send(any(jakarta.mail.internet.MimeMessage.class));

        EmailUtil.sendEmailDangKi(mailSender, hoTen, username, password, email);
        verify(mailSender, atLeastOnce()).send(any(SimpleMailMessage.class));
    }
}
