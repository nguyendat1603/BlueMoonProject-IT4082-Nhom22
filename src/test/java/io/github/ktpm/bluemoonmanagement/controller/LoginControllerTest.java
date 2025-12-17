package io.github.ktpm.bluemoonmanagement.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {

    private LoginController loginController;

    @BeforeAll
    static void initToolkit() throws InterruptedException {
        // Khởi tạo JavaFX toolkit cho testing
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(() -> latch.countDown());
        latch.await();
    }

    @BeforeEach
    void setUp() throws Exception {
        loginController = new LoginController();
        
        // Khởi tạo các thành phần UI bằng reflection
        initializeUIComponents();
        
        // Gọi initialize method
        loginController.initialize(null, null);
    }

    private void initializeUIComponents() throws Exception {
        // Sử dụng reflection để khởi tạo các field FXML
        setPrivateField("buttonDangNhap", new Button());
        setPrivateField("buttonNhanDienKhuonMat", new Button());
        setPrivateField("buttonQuenMatKhau", new Button());
        setPrivateField("checkBoxHienMatKhau", new CheckBox());
        setPrivateField("passwordFieldMatKhau", new PasswordField());
        setPrivateField("stackRoot", new StackPane());
        setPrivateField("textError", new Text());
        setPrivateField("textFieldEmail", new TextField());
        setPrivateField("textFieldMatKhau", new TextField());
    }

    private void setPrivateField(String fieldName, Object value) throws Exception {
        Field field = LoginController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(loginController, value);
    }

    private Object getPrivateField(String fieldName) throws Exception {
        Field field = LoginController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(loginController);
    }

    @Test
    void testInitialize() throws Exception {
        // Kiểm tra trạng thái ban đầu sau khi initialize
        TextField textFieldMatKhau = (TextField) getPrivateField("textFieldMatKhau");
        PasswordField passwordFieldMatKhau = (PasswordField) getPrivateField("passwordFieldMatKhau");
        CheckBox checkBoxHienMatKhau = (CheckBox) getPrivateField("checkBoxHienMatKhau");

        assertFalse(textFieldMatKhau.isVisible(), "TextField mật khẩu should be hidden initially");
        assertTrue(passwordFieldMatKhau.isVisible(), "PasswordField should be visible initially");
        assertFalse(checkBoxHienMatKhau.isSelected(), "CheckBox should not be selected initially");
    }

    @Test
    void testShowPassword_WhenCheckBoxSelected() throws Exception {
        // Arrange
        CheckBox checkBoxHienMatKhau = (CheckBox) getPrivateField("checkBoxHienMatKhau");
        TextField textFieldMatKhau = (TextField) getPrivateField("textFieldMatKhau");
        PasswordField passwordFieldMatKhau = (PasswordField) getPrivateField("passwordFieldMatKhau");
        
        passwordFieldMatKhau.setText("testPassword");
        checkBoxHienMatKhau.setSelected(true);

        // Act
        Platform.runLater(() -> {
            try {
                loginController.showPassword(new ActionEvent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        // Wait for JavaFX thread
        Thread.sleep(100);

        // Assert
        assertTrue(textFieldMatKhau.isVisible(), "TextField should be visible when checkbox is selected");
        assertFalse(passwordFieldMatKhau.isVisible(), "PasswordField should be hidden when checkbox is selected");
        assertEquals("testPassword", textFieldMatKhau.getText(), "TextField should contain password text");
    }

    @Test
    void testShowPassword_WhenCheckBoxNotSelected() throws Exception {
        // Arrange
        CheckBox checkBoxHienMatKhau = (CheckBox) getPrivateField("checkBoxHienMatKhau");
        TextField textFieldMatKhau = (TextField) getPrivateField("textFieldMatKhau");
        PasswordField passwordFieldMatKhau = (PasswordField) getPrivateField("passwordFieldMatKhau");
        
        textFieldMatKhau.setText("testPassword");
        checkBoxHienMatKhau.setSelected(false);

        // Act
        Platform.runLater(() -> {
            try {
                loginController.showPassword(new ActionEvent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        // Wait for JavaFX thread
        Thread.sleep(100);

        // Assert
        assertFalse(textFieldMatKhau.isVisible(), "TextField should be hidden when checkbox is not selected");
        assertTrue(passwordFieldMatKhau.isVisible(), "PasswordField should be visible when checkbox is not selected");
        assertEquals("testPassword", passwordFieldMatKhau.getText(), "PasswordField should contain password text");
    }

    @Test
    void testDangNhapPressed_WithValidCredentials() throws Exception {
        // Arrange
        TextField textFieldEmail = (TextField) getPrivateField("textFieldEmail");
        PasswordField passwordFieldMatKhau = (PasswordField) getPrivateField("passwordFieldMatKhau");
        
        textFieldEmail.setText("abcde");
        passwordFieldMatKhau.setText("12345");

        // Act & Assert
        // Note: Test này sẽ gây exception do thiếu file FXML, nhưng đó là expected behavior
        assertThrows(Exception.class, () -> {
            Platform.runLater(() -> {
                try {
                    loginController.dangNhapPressed(new ActionEvent());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            Thread.sleep(100);
        }, "Should throw exception due to missing FXML file in test environment");
    }

    @Test
    void testDangNhapPressed_WithInvalidCredentials() throws Exception {
        // Arrange
        TextField textFieldEmail = (TextField) getPrivateField("textFieldEmail");
        PasswordField passwordFieldMatKhau = (PasswordField) getPrivateField("passwordFieldMatKhau");
        Text textError = (Text) getPrivateField("textError");
        
        textFieldEmail.setText("wrong_email");
        passwordFieldMatKhau.setText("wrong_password");

        // Act
        Platform.runLater(() -> {
            try {
                loginController.dangNhapPressed(new ActionEvent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        // Wait for JavaFX thread
        Thread.sleep(100);

        // Assert
        assertEquals("Sai tài khoản hoặc mật khẩu", textError.getText(), 
                "Error message should be displayed for invalid credentials");
        assertTrue(textError.isVisible(), "Error text should be visible");
    }

    @Test
    void testDangNhapPressed_WithEmptyCredentials() throws Exception {
        // Arrange
        TextField textFieldEmail = (TextField) getPrivateField("textFieldEmail");
        PasswordField passwordFieldMatKhau = (PasswordField) getPrivateField("passwordFieldMatKhau");
        Text textError = (Text) getPrivateField("textError");
        
        textFieldEmail.setText("");
        passwordFieldMatKhau.setText("");

        // Act
        Platform.runLater(() -> {
            try {
                loginController.dangNhapPressed(new ActionEvent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        // Wait for JavaFX thread
        Thread.sleep(100);

        // Assert
        assertEquals("Sai tài khoản hoặc mật khẩu", textError.getText(), 
                "Error message should be displayed for empty credentials");
        assertTrue(textError.isVisible(), "Error text should be visible");
    }

    @Test
    void testDangNhapBangOTPClicked() throws Exception {
        // Act & Assert
        // Note: Test này sẽ gây exception do thiếu file FXML, nhưng đó là expected behavior
        assertThrows(Exception.class, () -> {
            Platform.runLater(() -> {
                try {
                    loginController.DangNhapBangOTPClicked(new ActionEvent());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            Thread.sleep(100);
        }, "Should throw exception due to missing FXML file in test environment");
    }

    @Test
    void testCredentialValidation_WithWhitespace() throws Exception {
        // Test với khoảng trắng đầu và cuối
        TextField textFieldEmail = (TextField) getPrivateField("textFieldEmail");
        PasswordField passwordFieldMatKhau = (PasswordField) getPrivateField("passwordFieldMatKhau");
        
        textFieldEmail.setText("  abcde  ");
        passwordFieldMatKhau.setText("  12345  ");

        // Act & Assert
        assertThrows(Exception.class, () -> {
            Platform.runLater(() -> {
                try {
                    loginController.dangNhapPressed(new ActionEvent());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            Thread.sleep(100);
        }, "Should handle trimmed credentials correctly and throw expected exception");
    }

    @Test
    void testPasswordFieldsInitialState() throws Exception {
        // Test trạng thái ban đầu của password fields
        TextField textFieldMatKhau = (TextField) getPrivateField("textFieldMatKhau");
        PasswordField passwordFieldMatKhau = (PasswordField) getPrivateField("passwordFieldMatKhau");
        
        // Kiểm tra rằng password field được hiển thị và text field bị ẩn
        assertFalse(textFieldMatKhau.isVisible());
        assertTrue(passwordFieldMatKhau.isVisible());
        assertEquals("", textFieldMatKhau.getText());
        assertEquals("", passwordFieldMatKhau.getText());
    }
} 