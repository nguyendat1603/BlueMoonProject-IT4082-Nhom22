package io.github.ktpm.bluemoonmanagement.controller;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import io.github.ktpm.bluemoonmanagement.service.face.FaceRecognitionService;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;
import io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.DangNhapDto;
import io.github.ktpm.bluemoonmanagement.service.taiKhoan.DangNhapServive;
import io.github.ktpm.bluemoonmanagement.util.FxViewLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

@Component
public class LoginController implements Initializable {

    @FXML
    private Button buttonDangNhap;

    @FXML
    private Button buttonNhanDienKhuonMat;

    @FXML
    private Button buttonQuenMatKhau;

    @FXML
    private CheckBox checkBoxHienMatKhau;

    @FXML
    private StackPane stackRoot;

    @FXML
    private Text textError;

    @FXML
    private TextField textFieldEmail;
    @FXML
    private Label labelScreenName;
    @Autowired
    private DangNhapServive dangNhapServive;
    @Autowired
    private FxViewLoader fxViewLoader;

    @FXML private PasswordField passwordField;   // mật khẩu ẩn
    @FXML private TextField passwordText;         // mật khẩu hiện

    @FXML private Button toggleBtn;




    @Override
public void initialize(URL url, ResourceBundle resourceBundle) {
    passwordText.setVisible(false);
    passwordText.setManaged(false);
}



    @FXML
    void dangNhapPressed(ActionEvent event) {
        String email = textFieldEmail.getText().trim();
        String password = passwordField.isVisible()
        ? passwordField.getText().trim()
        : passwordText.getText().trim();


        if (email.isEmpty()) {
            textError.setText("Vui lòng nhập đầy đủ thông tin đăng nhập.");
            textError.setVisible(true);
            return;
        }



        DangNhapDto dangNhapDto = new DangNhapDto(email, password);
        ResponseDto response = dangNhapServive.dangNhap(dangNhapDto);
        if (response.isSuccess()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Đăng nhập thành công");
            alert.setHeaderText(null);
            alert.setContentText("Chào mừng " + email + "!");
            alert.showAndWait();
            try {

                Parent mainView = fxViewLoader.loadView("/view/khung.fxml");
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(mainView));
                stage.setTitle("Application");
                stage.show();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chat_view.fxml"));
                Parent chatView = loader.load();
                Stage chatStage = new Stage();
                chatStage.setTitle("ChatBot");
                chatStage.setScene(new Scene(chatView));
                chatStage.show();
            } catch (IOException e) {
                e.printStackTrace();
                // Xử lý lỗi nếu không thể tải file FXML
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Đăng nhập khuôn mặt thất bại");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng đăng nhập lại !");
            alert.showAndWait();
            textError.setText(response.getMessage());
            textError.setVisible(true);
        }
    }

 @FXML
private void togglePassword() {

    boolean showing = passwordText.isVisible();

    if (showing) {
        passwordField.setText(passwordText.getText());
        passwordText.setVisible(false);
        passwordText.setManaged(false);

        passwordField.setVisible(true);
        passwordField.setManaged(true);

        toggleBtn.getStyleClass().remove("eye-open");

    } else {
        passwordText.setText(passwordField.getText());
        passwordField.setVisible(false);
        passwordField.setManaged(false);

        passwordText.setVisible(true);
        passwordText.setManaged(true);

        if (!toggleBtn.getStyleClass().contains("eye-open")) {
            toggleBtn.getStyleClass().add("eye-open");
        }
    }
}



    @FXML
    void DangNhapBangOTPClicked(ActionEvent event) {
        try {
            // Tải file FXML mới (khung.fxml)
            Parent mainView = fxViewLoader.loadView("/view/dang_nhap_otp.fxml");
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(mainView));
            stage.setTitle("Application");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Xử lý lỗi nếu không thể tải file FXML của màn hình OTP
            textError.setText("Có lỗi xảy ra khi chuyển đến màn hình OTP.");
            textError.setVisible(true);
        }
    }
}
