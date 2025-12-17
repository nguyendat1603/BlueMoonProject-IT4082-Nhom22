package io.github.ktpm.bluemoonmanagement.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Controller for the notification dialog (thong_bao.fxml)
 */
public class ThongBaoController {

    @FXML
    private Button button_close_up;

    @FXML
    private Button button_close_option;

    @FXML
    private Label labelThongBao;

    @FXML
    private Text textNoiDung;

    private Stage stage;

    /**
     * Initialize the controller
     */
    @FXML
    private void initialize() {
        // Set up event handlers
        if (button_close_up != null) {
            button_close_up.setOnAction(this::handleClose);
        }
        if (button_close_option != null) {
            button_close_option.setOnAction(this::handleClose);
        }
    }

    /**
     * Set the stage for this dialog
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Set the title of the notification
     */
    public void setTitle(String title) {
        if (labelThongBao != null) {
            labelThongBao.setText(title);
        }
    }

    /**
     * Set the content/message of the notification
     */
    public void setContent(String content) {
        if (textNoiDung != null) {
            textNoiDung.setText(content);
        }
    }

    /**
     * Handle close button actions
     */
    @FXML
    private void handleClose(ActionEvent event) {
        // Lấy Stage từ nút được click
        javafx.scene.Node source = (javafx.scene.Node) event.getSource();
        javafx.stage.Stage stage = (javafx.stage.Stage) source.getScene().getWindow();
        
        if (stage != null) {
            stage.close();
        }
    }

    /**
     * Static method to show notification dialog
     */
    public static void showNotification(String title, String message) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                ThongBaoController.class.getResource("/view/thong_bao.fxml")
            );
            javafx.scene.Parent root = loader.load();
            
            ThongBaoController controller = loader.getController();
            controller.setTitle(title);
            controller.setContent(message);
            
            javafx.stage.Stage stage = new javafx.stage.Stage();
            controller.setStage(stage);
            
            stage.setTitle(title);
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            stage.setResizable(false);
            stage.centerOnScreen();
            
            stage.showAndWait();
            
        } catch (Exception e) {
            System.err.println("Error showing notification: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback to standard alert if custom dialog fails
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
    }

    /**
     * Static method to show error notification
     */
    public static void showError(String title, String message) {
        showNotification(title != null ? title : "Lỗi", message);
    }

    /**
     * Static method to show success notification
     */
    public static void showSuccess(String title, String message) {
        showNotification(title != null ? title : "Thành công", message);
    }

    /**
     * Static method to show info notification
     */
    public static void showInfo(String title, String message) {
        showNotification(title != null ? title : "Thông báo", message);
    }

    /**
     * Static method to show confirmation dialog and return user's choice
     */
    public static boolean showConfirmation(String title, String message) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                ThongBaoController.class.getResource("/view/xac_nhan.fxml")
            );
            javafx.scene.Parent root = loader.load();
            
            XacNhanController controller = loader.getController();
            controller.setTitle(title != null ? title : "Xác nhận");
            controller.setContent(message);
            
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle(title != null ? title : "Xác nhận");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            stage.setResizable(false);
            stage.centerOnScreen();
            
            stage.showAndWait();
            
            return controller.isConfirmed();
            
        } catch (Exception e) {
            System.err.println("Error showing confirmation: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback to standard alert if custom dialog fails
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
            alert.setTitle(title != null ? title : "Xác nhận");
            alert.setHeaderText(null);
            alert.setContentText(message);
            
            java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
            return result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK;
        }
    }
} 
