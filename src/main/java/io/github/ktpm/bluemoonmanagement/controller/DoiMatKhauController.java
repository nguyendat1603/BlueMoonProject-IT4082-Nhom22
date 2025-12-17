package io.github.ktpm.bluemoonmanagement.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;
import io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.DatLaiMatKhauDto;
import io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.DoiMatKhauDto;
import io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.ThongTinTaiKhoanDto;
import io.github.ktpm.bluemoonmanagement.service.taiKhoan.DoiMatKhauService;
import io.github.ktpm.bluemoonmanagement.service.taiKhoan.QuenMatKhauService;
import io.github.ktpm.bluemoonmanagement.session.Session;
import jakarta.servlet.ServletOutputStream;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.github.ktpm.bluemoonmanagement.session.Session.getCurrentUser;

@Component
public class DoiMatKhauController {

    @FXML
    private FontAwesomeIcon buttonClose;

    @FXML
    private Button buttonLuu;

    @FXML
    private Button button_close_up;

    @FXML
    private CheckBox checkBoxHienMatKhau;

    @FXML
    private CheckBox checkBoxHienMatKhauMoi;

    @FXML
    private CheckBox checkBoxHienMatKhauXacNhan;

    @FXML
    private Label labelTieuDe;

    @FXML
    private PasswordField passwordFieldMatKhau;

    @FXML
    private PasswordField passwordFieldMatKhauMoi;

    @FXML
    private PasswordField passwordFieldMatKhauXacNhan;

    @FXML
    private Text textError;

    @FXML
    private TextField textFieldMatKhau;

    @FXML
    private TextField textFieldMatKhauMoi;

    @FXML
    private TextField textFieldMatKhauXacNhan;

    @Autowired
    private DoiMatKhauService doiMatKhauService;

    @FXML
    void LuuClicked(ActionEvent event) {
        buttonLuu.setDisable(true);
        String matKhauHienTai = passwordFieldMatKhau.getText();
        String matKhauMoi =     passwordFieldMatKhauMoi.getText();
        String matKhauXacNhan = passwordFieldMatKhauXacNhan.getText();








        String email = Session.getCurrentUser().getEmail();

        DoiMatKhauDto doiMatKhauDto = new DoiMatKhauDto(matKhauHienTai, matKhauMoi, matKhauXacNhan);
        ResponseDto response = doiMatKhauService.doiMatKhau(doiMatKhauDto);
        if(response.isSuccess()) {


            textError.setText("Mật khẩu đã được cập nhật thành công.");
            textError.setVisible(true);
            buttonLuu.setDisable(false);


        } else {
            // Hiển thị thông báo thành công và cập nhật thông tin người dùng
            textError.setText(response.getMessage());
            textError.setVisible(true);
            buttonLuu.setDisable(false);
        }
    }

    @FXML
    void handleClose(ActionEvent event) {
        // Đóng cửa sổ hiện tại
        javafx.stage.Stage stage = (javafx.stage.Stage) button_close_up.getScene().getWindow();
        stage.close();
    }

}
