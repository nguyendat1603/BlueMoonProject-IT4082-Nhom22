package io.github.ktpm.bluemoonmanagement.controller;

import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;
import io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.DatLaiMatKhauDto;
import io.github.ktpm.bluemoonmanagement.model.entity.TaiKhoan;
import io.github.ktpm.bluemoonmanagement.repository.TaiKhoanRepository;
import io.github.ktpm.bluemoonmanagement.service.taiKhoan.QuenMatKhauService;
import io.github.ktpm.bluemoonmanagement.util.FxViewLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DangNhapOTPController {

    @FXML
    private Button buttonGuiOTP;

    @FXML
    private Button buttonQuayLaiDangNhap;

    @FXML
    private Button buttonTiepTheo;

    @FXML
    private Button buttonXacNhan;

    @FXML
    private StackPane stackRoot;

    @FXML
    private Text textError;

    @FXML
    private TextField textFieldEmail;

    @FXML
    private TextField textFieldOTP;

    @FXML
    private Label lable_OTP;

    @FXML
    private Label lable_email;


    @Autowired
    private QuenMatKhauService quenMatKhauService;
    @Autowired
    private FxViewLoader fxViewLoader;
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;


    @FXML
    void QuayLaiDangNhapClicked(ActionEvent event) {
        try {
            Parent mainView = fxViewLoader.loadView("/view/dang_nhap.fxml");
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(mainView));
            stage.setTitle("Trang chính");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            textError.setText("Có lỗi xảy ra khi quay lại màn hình đăng nhập.");
        }
    }
    String emailR = "";
    @FXML
    void GuiOTPClicked(ActionEvent event) {
        String email = textFieldEmail.getText().trim();
        String otp = textFieldOTP.getText().trim();
        if (email.isEmpty()) {
            textError.setText("Vui lòng nhập email.");
            textError.setVisible(true);
            return;
        }
        DatLaiMatKhauDto datLaiMatKhauDto = new DatLaiMatKhauDto(email,"","","");
        ResponseDto response = quenMatKhauService.guiMaOtp(datLaiMatKhauDto);
        if (response.isSuccess()) {
            textError.setText("OTP đã được gửi đến email của bạn.");
            textError.setVisible(true);
        } else {
            textError.setText(response.getMessage());
            textError.setVisible(true);
        }
    }
    @FXML
    void TiepTheoClicked(ActionEvent event) {
        String email = textFieldEmail.getText().trim();
        String otp = textFieldOTP.getText().trim();
        if (email.isEmpty() || otp.isEmpty()) {
            textError.setText("Vui lòng nhập đầy đủ thông tin.");
            textError.setVisible(true);
            return;
        }
        DatLaiMatKhauDto datLaiMatKhauDto = new DatLaiMatKhauDto(email, otp, "", "");
        ResponseDto response2 = quenMatKhauService.xacThucOtp(datLaiMatKhauDto);

        if (response2.isSuccess()) {
            textError.setText("OTP xác thực thành công. Vui lòng nhập mật khẩu mới.");
            textError.setVisible(true);

            emailR = email;

            lable_email.setText("Nhập mật khẩu mới");
            lable_OTP.setText("Xác nhận mật khẩu mới");

            textFieldEmail.clear();
            textFieldOTP.clear();

            textFieldEmail.setPromptText("Mật khẩu mới");
            textFieldOTP.setPromptText("Xác nhận mật khẩu mới");

            buttonGuiOTP.setVisible(false);

            buttonTiepTheo.setVisible(false);

            buttonXacNhan.setDisable(false);
        } else {
            textError.setText(response2.getMessage());
            textError.setVisible(true);
        }

    }
    @FXML
    void XacNhanClicked(ActionEvent event) {
        buttonXacNhan.setDisable(true);
        String matkhau = textFieldEmail.getText().trim();
        String xacnhanmatkhau = textFieldOTP.getText().trim();

        if (matkhau.isEmpty() || xacnhanmatkhau.isEmpty()) {
            textError.setText("Vui lòng nhập đầy đủ thông tin.");
            textError.setVisible(true);
            return;
        }
        DatLaiMatKhauDto datLaiMatKhauDto = new DatLaiMatKhauDto(
                emailR,
                "",
                matkhau,
                xacnhanmatkhau
        );
        ResponseDto response = quenMatKhauService.datLaiMatKhau(datLaiMatKhauDto);
        if(response.isSuccess()) {
            textError.setText("Mật khẩu đã được cập nhật thành công. Vui lòng đăng nhập lại.");
            textError.setVisible(true);
        }
        else{
            textError.setText(response.getMessage());
            textError.setVisible(true);
            buttonXacNhan.setDisable(false);
        }
    }
}

