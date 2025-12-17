package io.github.ktpm.bluemoonmanagement.controller;


import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;
import io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.DangKiDto;
import io.github.ktpm.bluemoonmanagement.service.taiKhoan.DangKiService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ThemTaiKhoanController {

    @FXML
    private FontAwesomeIcon buttonClose;

    @FXML
    private Button buttonThemCuDan;

    @FXML
    private Button button_close_up;

    @FXML
    private Text textError;

    @FXML
    private TextField textFieldEmail;

    @FXML
    private TextField textFieldHoVaTen;

    @FXML
    private ComboBox<String> vaiTroComboBox;

    @Autowired
    private DangKiService dangKiService;

    public void initialize() {
        vaiTroComboBox.getItems().addAll("Tổ trưởng", "Tổ phó", "Kế toán");
    }



    @FXML
    void ThemTaiKhoanClicked(ActionEvent event) {
        buttonThemCuDan.setDisable(true);
        String hoVaTen = textFieldHoVaTen.getText().trim();
        String email = textFieldEmail.getText().trim();
        String vaiTro = vaiTroComboBox.getValue();
        if(hoVaTen.isEmpty() || email.isEmpty()) {
            textError.setText("Vui lòng nhập đầy đủ thông tin!");
            textError.setVisible(true);
            buttonThemCuDan.setDisable(false);
            return;
        }
        DangKiDto dangKiDto = new DangKiDto(email,hoVaTen, vaiTro);
        ResponseDto response = dangKiService.dangKiTaiKhoan(dangKiDto);

        if (response.isSuccess()) {
            textError.setText(response.getMessage());
            textError.setVisible(true);
            // Reset các trường nhập liệu
            textFieldHoVaTen.clear();
            textFieldEmail.clear();
        } else {
            textError.setText(response.getMessage());
            textError.setVisible(true);
        }
        buttonThemCuDan.setDisable(false);
    }

    @FXML
    void handleClose(ActionEvent event) {
        // Đóng cửa sổ hiện tại
        javafx.stage.Stage stage = (javafx.stage.Stage) button_close_up.getScene().getWindow();
        stage.close();
    }

}
