package io.github.ktpm.bluemoonmanagement.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;
import io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.ThongTinTaiKhoanDto;
import io.github.ktpm.bluemoonmanagement.service.taiKhoan.QuanLyTaiKhoanService;
import io.github.ktpm.bluemoonmanagement.session.Session;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class ChiTietTaiKhoanController implements Initializable {

    @FXML
    private Button buttonChinhSua;

    @FXML
    private FontAwesomeIcon buttonClose;

    @FXML
    private Button buttonLuu;

    @FXML
    private Button buttonXoaTaiKhoan;

    @FXML
    private Button button_close_up;

    @FXML
    private ComboBox<String> comboBoxVaiTro;

    @FXML
    private HBox hBoxChinhSuaXoaTaiKhoan;

    @FXML
    private Label labelTieuDe;

    @FXML
    private Text textError;

    @FXML
    private TextField textFieldEmail;

    @FXML
    private TextField textFieldHoVaTen;

    // Services
    @Autowired
    private QuanLyTaiKhoanService taiKhoanService;
    
    private ApplicationContext applicationContext;
    
    // Current account data
    private ThongTinTaiKhoanDto currentTaiKhoanDto;
    
    // Flag to track if form is in edit mode
    private boolean isEditMode = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupComboBoxes();
        setupEventHandlers();
        setupViewMode();
        clearErrorMessage();
    }

    /**
     * Setup combo boxes
     */
    private void setupComboBoxes() {
        if (comboBoxVaiTro != null) {
            comboBoxVaiTro.setItems(FXCollections.observableArrayList(
                "Tổ trưởng", "Tổ phó", "Kế toán"
            ));
        }
    }

    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        if (button_close_up != null) {
            button_close_up.setOnAction(this::handleClose);
        }
    }

    /**
     * Setup view mode (default)
     */
    private void setupViewMode() {
        setFormEditable(false);
        if (buttonChinhSua != null) buttonChinhSua.setVisible(true);
        if (buttonLuu != null) buttonLuu.setVisible(false);
        if (buttonXoaTaiKhoan != null) buttonXoaTaiKhoan.setVisible(false);
        isEditMode = false;
    }

    /**
     * Setup edit mode
     */
    private void setupEditMode() {
        setFormEditable(true);
        if (buttonChinhSua != null) buttonChinhSua.setVisible(false);
        if (buttonLuu != null) buttonLuu.setVisible(true);
        if (buttonXoaTaiKhoan != null) buttonXoaTaiKhoan.setVisible(true);
        isEditMode = true;
    }

    /**
     * Set form fields editable/readonly
     */
    private void setFormEditable(boolean editable) {
        if (textFieldEmail != null) textFieldEmail.setEditable(false); // Email never editable
        if (textFieldHoVaTen != null) textFieldHoVaTen.setEditable(editable);
        if (comboBoxVaiTro != null) comboBoxVaiTro.setDisable(!editable);
    }

    /**
     * Handle edit button click
     */
    @FXML
    void ChinhXuaTaiKhoanClicked(ActionEvent event) {
        try {
            // Check permission
            if (!hasPermission()) {
                showErrorMessage("Bạn không có quyền chỉnh sửa tài khoản. Chỉ Tổ trưởng mới được phép.");
                return;
            }
            
            setupEditMode();
            clearErrorMessage();
        } catch (Exception e) {
            showErrorMessage("Có lỗi xảy ra khi chuyển sang chế độ chỉnh sửa: " + e.getMessage());
        }
    }

    /**
     * Handle save button click
     */
    @FXML
    void LuuClicked(ActionEvent event) {
        try {
            clearErrorMessage();
            
            // Validate input
            if (!validateInput()) {
                return;
            }

            // Check permission
            if (!hasPermission()) {
                showErrorMessage("Bạn không có quyền cập nhật tài khoản. Chỉ Tổ trưởng mới được phép.");
                return;
            }

            // Create updated DTO
            ThongTinTaiKhoanDto updatedDto = createUpdatedDto();

            // Call service to update
            ResponseDto response = taiKhoanService.thayDoiThongTinTaiKhoan(updatedDto);

            if (response.isSuccess()) {
                showSuccessMessage("Cập nhật tài khoản thành công");
                
                // Update current data
                currentTaiKhoanDto = updatedDto;
                
                // Refresh main accounts table
                refreshMainAccountsTable();
                
                // Switch back to view mode
                setupViewMode();
            } else {
                showErrorMessage("Lỗi: " + response.getMessage());
            }

        } catch (Exception e) {
            showErrorMessage("Có lỗi xảy ra: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle delete button click
     */
    @FXML
    void XoaTaiKhoanCicked(ActionEvent event) {
        try {
            // Check permission
            if (!hasPermission()) {
                showErrorMessage("Bạn không có quyền xóa tài khoản. Chỉ Tổ trưởng mới được phép.");
                return;
            }

            if (currentTaiKhoanDto == null) {
                showErrorMessage("Không thể xóa: Dữ liệu tài khoản không hợp lệ.");
                return;
            }

            String email = currentTaiKhoanDto.getEmail();
            String hoTen = currentTaiKhoanDto.getHoTen();
            
            // Prevent deleting current user's own account
            if (Session.getCurrentUser() != null && email.equals(Session.getCurrentUser().getEmail())) {
                showErrorMessage("Bạn không thể xóa tài khoản của chính mình.");
                return;
            }
            
            // Prevent deleting admin account
            if ("admin".equals(email)) {
                showErrorMessage("Không thể xóa tài khoản admin.");
                return;
            }

            // Show confirmation dialog
            boolean confirmed = showCustomConfirmDialog(hoTen, email);
            
            if (confirmed) {
                // Call service to delete
                ResponseDto response = taiKhoanService.xoaTaiKhoan(currentTaiKhoanDto);
                
                if (response.isSuccess()) {
                    showSuccessMessage("Đã xóa tài khoản thành công.");
                    
                    // Refresh main accounts table
                    refreshMainAccountsTable();
                    
                    // Close window after successful deletion
                    javafx.application.Platform.runLater(() -> {
                        try {
                            Thread.sleep(1000); // Show success message for 1 second
                            closeWindow();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            closeWindow();
                        }
                    });
                } else {
                    showErrorMessage("Không thể xóa tài khoản: " + response.getMessage());
                }
            }

        } catch (Exception e) {
            showErrorMessage("Có lỗi xảy ra khi xóa tài khoản: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle close button click
     */
    @FXML
    private void handleClose(ActionEvent event) {
        closeWindow();
    }

    /**
     * Validate input data
     */
    private boolean validateInput() {
        // Validate họ và tên
        if (textFieldHoVaTen == null || isBlank(textFieldHoVaTen.getText())) {
            showErrorMessage("Vui lòng nhập họ và tên");
            if (textFieldHoVaTen != null) textFieldHoVaTen.requestFocus();
            return false;
        }

        // Validate email (readonly but check)
        if (textFieldEmail == null || isBlank(textFieldEmail.getText())) {
            showErrorMessage("Email không được để trống");
            return false;
        }

        if (textFieldEmail != null && !isValidEmail(textFieldEmail.getText().trim())) {
            showErrorMessage("Email không hợp lệ");
            return false;
        }

        // Validate vai trò
        if (comboBoxVaiTro == null || comboBoxVaiTro.getValue() == null) {
            showErrorMessage("Vui lòng chọn vai trò");
            return false;
        }

        return true;
    }

    /**
     * Create updated DTO from form data
     */
    private ThongTinTaiKhoanDto createUpdatedDto() {
        return new ThongTinTaiKhoanDto(
            textFieldEmail.getText().trim(),
            textFieldHoVaTen.getText().trim(),
            comboBoxVaiTro.getValue(),
            currentTaiKhoanDto.getNgayTao(),
            currentTaiKhoanDto.getNgayCapNhat()
        );
    }

    /**
     * Check user permission
     */
    private boolean hasPermission() {
        return Session.getCurrentUser() != null && 
               "Tổ trưởng".equals(Session.getCurrentUser().getVaiTro());
    }

    /**
     * Validate email format
     */
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    }

    /**
     * Check if string is blank
     */
    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Show error message
     */
    private void showErrorMessage(String message) {
        ThongBaoController.showError("Lỗi", message);
    }

    /**
     * Show success message
     */
    private void showSuccessMessage(String message) {
        ThongBaoController.showSuccess("Thành công", message);
    }

    /**
     * Clear error message
     */
    private void clearErrorMessage() {
        if (textError != null) {
            textError.setText("");
            textError.setVisible(false);
        }
    }

    /**
     * Close window
     */
    private void closeWindow() {
        try {
            Stage stage = (Stage) button_close_up.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            System.err.println("Lỗi khi đóng cửa sổ: " + e.getMessage());
        }
    }

    /**
     * Show custom confirmation dialog
     */
    private boolean showCustomConfirmDialog(String hoTen, String email) {
        try {
            // Load FXML file
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/view/xac_nhan.fxml")
            );
            javafx.scene.Parent root = loader.load();
            
            // Get controller
            XacNhanController controller = loader.getController();
            
            // Set content
            controller.setTitle("Xác nhận xóa tài khoản");
            controller.setContent("Bạn có chắc chắn muốn xóa tài khoản:\n" + 
                                hoTen + " (" + email + ")?");
            
            // Create and show dialog
            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle("Xác nhận");
            dialogStage.setScene(new javafx.scene.Scene(root));
            dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            
            // Set owner
            javafx.stage.Stage ownerStage = (javafx.stage.Stage) buttonXoaTaiKhoan.getScene().getWindow();
            dialogStage.initOwner(ownerStage);
            
            // Set position to center of parent
            dialogStage.setX(ownerStage.getX() + (ownerStage.getWidth() - 450) / 2);
            dialogStage.setY(ownerStage.getY() + (ownerStage.getHeight() - 220) / 2);
            
            // Show and wait
            dialogStage.showAndWait();
            
            // Return result
            return controller.isConfirmed();
            
        } catch (Exception e) {
            System.err.println("Lỗi khi hiển thị dialog xác nhận: " + e.getMessage());
            e.printStackTrace();

            // Fallback to standard alert if custom dialog fails
            return ThongBaoController.showConfirmation("Xác nhận xóa", 
                "Bạn có chắc chắn muốn xóa tài khoản:\n" + hoTen + " (" + email + ")?");
        }
    }

    /**
     * Refresh main accounts table
     */
    private void refreshMainAccountsTable() {
        try {
            javafx.application.Platform.runLater(() -> {
                try {
                    // Find all windows and look for Home_list controller
                    for (javafx.stage.Window window : javafx.stage.Window.getWindows()) {
                        if (window instanceof javafx.stage.Stage) {
                            javafx.stage.Stage stage = (javafx.stage.Stage) window;
                            javafx.scene.Scene scene = stage.getScene();
                            if (scene != null && scene.getRoot() != null) {
                                // Try to find the Home_list controller through scene graph
                                findAndRefreshHomeListControllerForAccounts(scene.getRoot());
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("ERROR: Failed to refresh accounts data: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            System.err.println("ERROR: Exception in refreshMainAccountsTable: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Find and refresh Home_list controller for accounts
     */
    private void findAndRefreshHomeListControllerForAccounts(javafx.scene.Node node) {
        try {
            // Check if the node has a controller property
            Object controller = node.getProperties().get("controller");
            if (controller instanceof Home_list) {
                Home_list homeListController = (Home_list) controller;
                
                // Switch to accounts tab and refresh data
                java.lang.reflect.Method gotoTaiKhoanMethod = homeListController.getClass().getDeclaredMethod("gotoTaiKhoan", javafx.event.ActionEvent.class);
                gotoTaiKhoanMethod.setAccessible(true);
                gotoTaiKhoanMethod.invoke(homeListController, (javafx.event.ActionEvent) null);
                
                // Refresh accounts data (now public method)
                homeListController.refreshTaiKhoanData();
                
                return;
            }
            
            // Recursively search in children if it's a Parent node
            if (node instanceof javafx.scene.Parent) {
                javafx.scene.Parent parent = (javafx.scene.Parent) node;
                for (javafx.scene.Node child : parent.getChildrenUnmodifiable()) {
                    findAndRefreshHomeListControllerForAccounts(child);
                }
            }
        } catch (Exception e) {
            // Silently continue searching - this is expected for most nodes
        }
    }

    // Setters for dependency injection
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        // Ensure service is initialized if not already
        if (this.taiKhoanService == null) {
            this.taiKhoanService = applicationContext.getBean(QuanLyTaiKhoanService.class);
        }
    }

    public void setTaiKhoanService(QuanLyTaiKhoanService taiKhoanService) {
        this.taiKhoanService = taiKhoanService;
    }

    /**
     * Set account data and populate form
     */
    public void setTaiKhoanData(ThongTinTaiKhoanDto taiKhoanDto) {
        this.currentTaiKhoanDto = taiKhoanDto;
        populateForm();
    }

    /**
     * Populate form with account data
     */
    private void populateForm() {
        if (currentTaiKhoanDto != null) {
            if (textFieldEmail != null) {
                textFieldEmail.setText(currentTaiKhoanDto.getEmail());
            }
            if (textFieldHoVaTen != null) {
                textFieldHoVaTen.setText(currentTaiKhoanDto.getHoTen());
            }
            if (comboBoxVaiTro != null) {
                comboBoxVaiTro.setValue(currentTaiKhoanDto.getVaiTro());
            }
        }
    }
}
