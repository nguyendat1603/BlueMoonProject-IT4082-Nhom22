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
    private PasswordField passwordFieldMatKhau;

    @FXML
    private StackPane stackRoot;

    @FXML
    private Text textError;

    @FXML
    private TextField textFieldEmail;

    @FXML
    private TextField textFieldMatKhau;
    @FXML
    private Label labelScreenName;
    @Autowired
    private DangNhapServive dangNhapServive;
    @Autowired
    private FxViewLoader fxViewLoader;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        textFieldMatKhau.setVisible(false);
        passwordFieldMatKhau.setVisible(true);
        checkBoxHienMatKhau.setSelected(false);
    }

    @FXML
    void showPassword(ActionEvent event) {
        if (checkBoxHienMatKhau.isSelected()) {
            // Nếu được chọn thì hiển thị mật khẩu
            textFieldMatKhau.setText(passwordFieldMatKhau.getText());
            textFieldMatKhau.setVisible(true);
            passwordFieldMatKhau.setVisible(false);
        } else {
            // Nếu bỏ chọn thì ẩn mật khẩu
            passwordFieldMatKhau.setText(textFieldMatKhau.getText());
            passwordFieldMatKhau.setVisible(true);
            textFieldMatKhau.setVisible(false);
        }
    }

    @FXML
    void dangNhapPressed(ActionEvent event) {
        String email = textFieldEmail.getText().trim();
        String password = passwordFieldMatKhau.getText().trim();
        if (password.isEmpty()) {
            password = textFieldMatKhau.getText().trim();
        }

        if (email.isEmpty()) {
            textError.setText("Vui lòng nhập đầy đủ thông tin đăng nhập.");
            textError.setVisible(true);
            return;
        }

//        // Xác thực khuôn mặt trước khi đăng nhập
//        //FaceRecognitionService faceRecognitionService = new FaceRecognitionService();
//       //boolean faceMatched = faceRecognitionService.recognizeFace(email);
//
//        //if (!faceMatched) {
//            textError.setText("Không nhận diện được khuôn mặt hoặc khuôn mặt không khớp với tài khoản đã nhập.");
//            textError.setVisible(true);
//            return;
//        }

        // Nếu qua xác thực khuôn mặt -> tiếp tục đăng nhập
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
