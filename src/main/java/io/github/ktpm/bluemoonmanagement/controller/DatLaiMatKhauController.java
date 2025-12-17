package io.github.ktpm.bluemoonmanagement.controller;
import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;
import io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.DoiMatKhauDto;
import io.github.ktpm.bluemoonmanagement.service.taiKhoan.DoiMatKhauService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatLaiMatKhauController {

    @FXML
    private Button buttonLuuMatKhau;

    @FXML
    private CheckBox checkBoxHienMatKhau;

    @FXML
    private CheckBox checkBoxHienXacNhanMatKhau;

    @FXML
    private PasswordField passwordFieldMatKhau;

    @FXML
    private PasswordField passwordFieldXacNhanMatKhau;

    @FXML
    private StackPane stackRoot;

    @FXML
    private Text textError;

    @FXML
    private TextField textFieldMatKhau;

    @FXML
    private TextField textFieldXacNhanMatKhau;

    @Autowired
    private DoiMatKhauService doiMatKhauService;

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
    void LuuMatKhauClicked(ActionEvent event) {
        // Lấy mật khẩu mới và xác nhận mật khẩu từ các trường nhập liệu
        String matKhau = passwordFieldMatKhau.getText().trim();
        String xacNhanMatKhau = passwordFieldXacNhanMatKhau.getText().trim();
        String matKhauCu = "";
        DoiMatKhauDto doiMatKhauDto = new DoiMatKhauDto(matKhauCu,matKhau, xacNhanMatKhau);
        ResponseDto response = doiMatKhauService.doiMatKhau(doiMatKhauDto);
        if(response.isSuccess()){

        }
        else if(response.getMessage().equals("Mật khẩu hiện tại không đúng")){
            textError.setText("Mật khẩu hiện tại không đúng");
            textError.setVisible(true);
        }
        else if(response.getMessage().equals("Mật khẩu mới và xác nhận không khớp")){
            textError.setText("Mật khẩu mới và xác nhận không khớp");
            textError.setVisible(true);
        }
        else{
            textError.setText("Đổi mật khẩu thất bại");
            textError.setVisible(true);
        }

    }

}

