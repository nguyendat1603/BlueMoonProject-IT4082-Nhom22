package io.github.ktpm.bluemoonmanagement.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller cho dialog xác nhận có sẵn
 */
public class XacNhanController implements Initializable {

    @FXML
    private Button button_close_up;

    @FXML
    private Button buttonCancel;

    @FXML
    private Button buttonConfirm;

    @FXML
    private Label labelXacNhan;

    @FXML
    private Text textNoiDung;

    private boolean confirmed = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        confirmed = false;
        
        // Đảm bảo các nút được enable và có thể tương tác
        if (buttonConfirm != null) {
            buttonConfirm.setDisable(false);
            buttonConfirm.setVisible(true);
            buttonConfirm.setManaged(true);
            
            // Thêm mouse event listener để test
            buttonConfirm.setOnMouseClicked(event -> {
                handleConfirm(null);
            });

            buttonConfirm.setOnMouseEntered(event -> {
            });

        }

        if (buttonCancel != null) {
            buttonCancel.setDisable(false);
            buttonCancel.setVisible(true);
            buttonCancel.setManaged(true);

            // Thêm mouse event listener để test
            buttonCancel.setOnMouseClicked(event -> {
                handleCancel(null);
            });

            buttonCancel.setOnMouseEntered(event -> {
            });

        }

        if (button_close_up != null) {
            button_close_up.setDisable(false);
            button_close_up.setVisible(true);
            button_close_up.setManaged(true);
        }
    }

    /**
     * Thiết lập nội dung thông báo
     */
    public void setContent(String content) {
        if (textNoiDung != null) {
            textNoiDung.setText(content);
        }

        // Debug kiểm tra trạng thái các nút sau khi set content
        checkButtonStates();
    }

    /**
     * Thiết lập tiêu đề
     */
    public void setTitle(String title) {
        if (labelXacNhan != null) {
            labelXacNhan.setText(title);
        }
    }

    /**
     * Xử lý khi nhấn nút đóng (X)
     */
    @FXML
    void handleCloseUp(ActionEvent event) {
        confirmed = false;
        closeDialog();
    }

    /**
     * Xử lý khi nhấn nút "Hủy"
     */
    @FXML
    void handleCancel(ActionEvent event) {
        confirmed = false;
        closeDialog();
    }

    /**
     * Xử lý khi nhấn nút "Xác nhận"
     */
    @FXML
    void handleConfirm(ActionEvent event) {
        confirmed = true;
        closeDialog();
    }

    /**
     * Đóng dialog
     */
    private void closeDialog() {
        try {
            Stage stage = (Stage) buttonCancel.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            System.err.println("ERROR: Cannot close dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Kiểm tra xem người dùng có xác nhận không
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * Kiểm tra trạng thái các nút để debug
     */
    public void checkButtonStates() {
        if (buttonConfirm != null) {

        }
        if (buttonCancel != null) {
        }

        if (button_close_up != null) {

        }
    }

    /**
     * Force enable tất cả các nút
     */
    public void forceEnableButtons() {

        if (buttonConfirm != null) {
            buttonConfirm.setDisable(false);
            buttonConfirm.setVisible(true);
            buttonConfirm.setManaged(true);
            buttonConfirm.setOpacity(1.0);
            buttonConfirm.setMouseTransparent(false);
        }

        if (buttonCancel != null) {
            buttonCancel.setDisable(false);
            buttonCancel.setVisible(true);
            buttonCancel.setManaged(true);
            buttonCancel.setOpacity(1.0);
            buttonCancel.setMouseTransparent(false);
        }

        if (button_close_up != null) {
            button_close_up.setDisable(false);
            button_close_up.setVisible(true);
            button_close_up.setManaged(true);
            button_close_up.setOpacity(1.0);
            button_close_up.setMouseTransparent(false);
        }

    }
} 
