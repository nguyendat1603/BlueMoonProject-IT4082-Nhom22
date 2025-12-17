package io.github.ktpm.bluemoonmanagement.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;
import io.github.ktpm.bluemoonmanagement.model.dto.cuDan.CudanDto;
import io.github.ktpm.bluemoonmanagement.service.cuDan.CuDanService;
import io.github.ktpm.bluemoonmanagement.session.Session;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

@Component
public class ThemCuDanController implements Initializable {

    // FXML Controls
    @FXML private Button button_close_up;
    @FXML private Label labelTieuDe;
    @FXML private TextField textFieldHoVaTen;
    @FXML private TextField textFieldMaDinhDanh;
    @FXML private ComboBox<String> comboBoxGioiTinh;
    @FXML private DatePicker datePickerNgaySinh;
    @FXML private TextField textFieldSoDienThoai;
    @FXML private TextField textFieldEmail;
    @FXML private TextField textFieldMaCanHo;
    @FXML private ComboBox<String> comboBoxTrangThai;
    @FXML private DatePicker datePickerNgayChuyenDen;
    @FXML private DatePicker datePickerNgayChuyenDi;
    @FXML private Text textError;
    @FXML private Button buttonThemCuDan;
    @FXML private Button buttonLuu;
    @FXML private Button buttonChinhSua;
    @FXML private Button buttonXoa;
    
    // HBox containers for date fields (to hide entire rows)
    @FXML private javafx.scene.layout.HBox hBoxNgayChuyenDen;
    @FXML private javafx.scene.layout.HBox hBoxNgayChuyenDi;

    // Services
    @Autowired
    private CuDanService cuDanService;

    private ApplicationContext applicationContext;
    
    // Field để lưu mã định danh của cư dân vừa tạo thành công
    private String lastCreatedCuDanMaDinhDanh;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Controller initialization
        // Setup ComboBoxes
        setupComboBoxes();
        
        // Setup event handlers
        setupEventHandlers();
        
        // Default setup for ADD mode
        setupAddMode();
        
        // Clear any error messages
        clearErrorMessage();
    }

    /**
     * Thiết lập các ComboBox
     */
    private void setupComboBoxes() {
        // Setup giới tính
        if (comboBoxGioiTinh != null) {
            comboBoxGioiTinh.setItems(FXCollections.observableArrayList(
                "Nam", "Nữ"
            ));
        }

        // Setup trạng thái cư trú - có thể chọn "Cư trú" hoặc "Không cư trú"
        if (comboBoxTrangThai != null) {
            comboBoxTrangThai.setItems(FXCollections.observableArrayList("Cư trú", "Không cư trú"));
            comboBoxTrangThai.setValue("Cư trú"); // Default value
            // Cho phép thay đổi trạng thái
        }
    }

    /**
     * Thiết lập event handlers
     */
    private void setupEventHandlers() {
        // Close button
        if (button_close_up != null) {
            button_close_up.setOnAction(this::handleClose);
        }

        // Add button
        if (buttonThemCuDan != null) {
            buttonThemCuDan.setOnAction(this::handleThemCuDan);
        }

        // Trạng thái change handler để enable/disable các field ngày
        if (comboBoxTrangThai != null) {
            comboBoxTrangThai.setOnAction(e -> {
                String trangThai = comboBoxTrangThai.getValue();
                handleTrangThaiChange(trangThai);
            });
        }
        
        // Ngày chuyển đi change handler để tự động thay đổi trạng thái
        // Chỉ thêm listener nếu component tồn tại trong FXML
        if (datePickerNgayChuyenDi != null) {
            datePickerNgayChuyenDi.valueProperty().addListener((observable, oldValue, newValue) -> {
                handleNgayChuyenDiChange(newValue);
            });
        }
    }

    /**
     * Xử lý thay đổi trạng thái cư trú
     */
    private void handleTrangThaiChange(String trangThai) {
        if (trangThai == null) return;
        
        boolean isCuTru = "Cư trú".equals(trangThai);
        boolean isKhongCuTru = "Không cư trú".equals(trangThai);
        boolean isChuyenDi = "Đã chuyển đi".equals(trangThai);
        
        // Hiển thị HBox ngày chuyển đến khi trạng thái là "Cư trú"
        if (hBoxNgayChuyenDen != null) {
            hBoxNgayChuyenDen.setVisible(isCuTru);
            hBoxNgayChuyenDen.setDisable(!isCuTru);
        }
        
        // Set giá trị cho datePickerNgayChuyenDen
        if (datePickerNgayChuyenDen != null) {
            if (isCuTru) {
                // Set ngày hiện tại làm mặc định khi chọn "Cư trú"
                datePickerNgayChuyenDen.setValue(LocalDate.now());
            } else {
                // Xóa ngày chuyển đến khi chọn "Không cư trú" hoặc "Đã chuyển đi"
                datePickerNgayChuyenDen.setValue(null);
            }
        }
        
        // Hiển thị HBox ngày chuyển đi khi trạng thái là "Đã chuyển đi" (chỉ khi xóa mềm)
        if (hBoxNgayChuyenDi != null) {
            hBoxNgayChuyenDi.setVisible(isChuyenDi);
            hBoxNgayChuyenDi.setDisable(!isChuyenDi);
        }
        
        // Set giá trị cho datePickerNgayChuyenDi
        if (datePickerNgayChuyenDi != null) {
            if (isChuyenDi) {
                // Set ngày hiện tại làm mặc định khi chọn "Đã chuyển đi"
                datePickerNgayChuyenDi.setValue(LocalDate.now());
            } else {
                // Xóa giá trị khi chọn trạng thái khác
                datePickerNgayChuyenDi.setValue(null);
            }
        }
    }
    
    /**
     * Xử lý thay đổi ngày chuyển đi - tự động thay đổi trạng thái
     */
    private void handleNgayChuyenDiChange(LocalDate ngayChuyenDi) {
        if (ngayChuyenDi == null) {
            // Nếu ngày chuyển đi bị xóa, tự động chuyển trạng thái sang "Cư trú"
            if (comboBoxTrangThai != null) {
                comboBoxTrangThai.setValue("Cư trú");
                // Trigger để hiện lại field ngày chuyển đến
                handleTrangThaiChange("Cư trú");
            }
        } else {
            // Nếu có ngày chuyển đi, tự động chuyển trạng thái sang "Đã chuyển đi"
            if (comboBoxTrangThai != null) {
                comboBoxTrangThai.setValue("Đã chuyển đi");
                // Trigger để hiện field ngày chuyển đi
                handleTrangThaiChange("Đã chuyển đi");
            }
        }
    }

    /**
     * Setup cho ADD mode
     */
    private void setupAddMode() {
        if (labelTieuDe != null) labelTieuDe.setText("Thêm cư dân");
        if (buttonThemCuDan != null) buttonThemCuDan.setVisible(true);
        if (buttonLuu != null) buttonLuu.setVisible(false);
        if (buttonChinhSua != null) buttonChinhSua.setVisible(false);
        

        
        // Áp dụng logic hiển thị field ngày dựa trên trạng thái mặc định
        handleTrangThaiChange("Cư trú"); // Default value
    }

    /**
     * Setup cho EDIT mode từ màn hình Chi Tiết Căn Hộ
     * @param cuDanData DTO chứa thông tin chi tiết của cư dân trong căn hộ
     * @param maCanHo Mã căn hộ hiện tại, sẽ được tự động gán
     */
    public void setupEditModeFromApartmentDetail(io.github.ktpm.bluemoonmanagement.model.dto.cuDan.CuDanTrongCanHoDto cuDanData, String maCanHo) {
        setCommonEditModeUI("Chỉnh sửa cư dân");

        // Populate form với dữ liệu
        if (textFieldMaDinhDanh != null) {
            textFieldMaDinhDanh.setText(cuDanData.getMaDinhDanh());
            textFieldMaDinhDanh.setEditable(false);
        }
        if (textFieldHoVaTen != null) {
            textFieldHoVaTen.setText(cuDanData.getHoVaTen());
        }
        if (datePickerNgaySinh != null) {
            datePickerNgaySinh.setValue(cuDanData.getNgaySinh());
        }
        if (comboBoxTrangThai != null) {
            comboBoxTrangThai.setValue(cuDanData.getTrangThaiCuTru());
            // Setup ComboBox trạng thái cho edit mode từ apartment detail
            setupEditModeComboBoxTrangThai();
        }
        // Gán và khóa mã căn hộ
        if (textFieldMaCanHo != null) {
            textFieldMaCanHo.setText(maCanHo);
            textFieldMaCanHo.setEditable(false);
        }

        // Các trường còn lại không có trong DTO này, người dùng cần tự nhập nếu muốn cập nhật
    }

    /**
     * Setup cho EDIT mode với dữ liệu cư dân từ Home_list
     */
    public void setupEditMode(io.github.ktpm.bluemoonmanagement.controller.Home_list.CuDanTableData cuDanData) {
        try {

            
            // Thay đổi UI cho edit mode
            setCommonEditModeUI("Chỉnh sửa cư dân");
            
            // Populate form với dữ liệu hiện tại
            populateFormWithData(cuDanData);
            
        } catch (Exception e) {
            System.err.println("Lỗi khi setup edit mode: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Populate form với dữ liệu cư dân
     */
    private void populateFormWithData(io.github.ktpm.bluemoonmanagement.controller.Home_list.CuDanTableData cuDanData) {
        try {
            // Fill form fields
            if (textFieldMaDinhDanh != null) {
                textFieldMaDinhDanh.setText(cuDanData.getMaDinhDanh());
                textFieldMaDinhDanh.setEditable(false); // Không cho phép chỉnh sửa mã định danh
            }
            
            if (textFieldHoVaTen != null) {
                textFieldHoVaTen.setText(cuDanData.getHoVaTen());
            }
            
            if (comboBoxGioiTinh != null) {
                comboBoxGioiTinh.setValue(cuDanData.getGioiTinh());
            }
            
            if (datePickerNgaySinh != null && cuDanData.getNgaySinh() != null && !cuDanData.getNgaySinh().isEmpty()) {
                try {
                    datePickerNgaySinh.setValue(LocalDate.parse(cuDanData.getNgaySinh()));
                } catch (Exception e) {
                    System.err.println("Lỗi parse ngày sinh: " + e.getMessage());
                }
            }
            
            if (textFieldSoDienThoai != null) {
                textFieldSoDienThoai.setText(cuDanData.getSoDienThoai());
            }
            
            if (textFieldEmail != null) {
                textFieldEmail.setText(cuDanData.getEmail());
            }
            
            if (textFieldMaCanHo != null) {
                textFieldMaCanHo.setText(cuDanData.getMaCanHo());
            }
            
            if (comboBoxTrangThai != null) {
                comboBoxTrangThai.setValue(cuDanData.getTrangThaiCuTru());
                // Setup lại ComboBox sau khi set giá trị
                setupEditModeComboBoxTrangThai();
            }
            
            if (datePickerNgayChuyenDen != null && cuDanData.getNgayChuyenDen() != null && !cuDanData.getNgayChuyenDen().isEmpty()) {
                try {
                    datePickerNgayChuyenDen.setValue(LocalDate.parse(cuDanData.getNgayChuyenDen()));
                } catch (Exception e) {
                    System.err.println("Lỗi parse ngày chuyển đến: " + e.getMessage());
                }
            }
            
            // Ngày chuyển đi không có trong CuDanTableData từ bảng chính
            // Chỉ reset về null để tránh giá trị cũ
            if (datePickerNgayChuyenDi != null) {
                datePickerNgayChuyenDi.setValue(null);
            }
            
            // Xử lý logic hiển thị/ẩn các field ngày
            handleTrangThaiChange(cuDanData.getTrangThaiCuTru());
            
        } catch (Exception e) {
            System.err.println("Lỗi khi populate form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Xử lý xóa cư dân (edit mode)
     */
    @FXML
    private void handleXoaCuDan(ActionEvent event) {
        try {
            // Check permission
            if (!hasPermission()) {
                showErrorMessage("Bạn không có quyền xóa cư dân. Chỉ Tổ phó mới được phép.");
                return;
            }

            String maDinhDanh = textFieldMaDinhDanh.getText();
            String hoVaTen = textFieldHoVaTen.getText();
            
            if (maDinhDanh == null || maDinhDanh.trim().isEmpty()) {
                showErrorMessage("Không thể xóa: Mã định danh không hợp lệ.");
                return;
            }

            // Hiển thị dialog xác nhận tùy chỉnh
            boolean confirmed = showCustomConfirmDialog(hoVaTen, maDinhDanh);
            
            if (confirmed) {
                // Xóa mềm: chuyển trạng thái thành "Chuyển đi" và set ngày chuyển đi
                try {
                    CudanDto cuDanDto = createCuDanDto();
                    cuDanDto.setTrangThaiCuTru("Đã chuyển đi");
                    cuDanDto.setNgayChuyenDi(LocalDate.now());
                    cuDanDto.setNgayChuyenDen(null); // Xóa ngày chuyển đến
                    
                    // Cập nhật cư dân thay vì xóa
                    ResponseDto response = cuDanService.updateCuDan(cuDanDto);
                    
                    if (response.isSuccess()) {

                        
                        // Refresh main residents table and switch to residents tab
                        refreshMainResidentsTable();
                        
                        showSuccessMessage("Đã xóa cư dân khỏi căn hộ.");
                        
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
                        showErrorMessage("Không thể xóa cư dân: " + response.getMessage());
                    }
                    
                } catch (Exception e) {
                    showErrorMessage("Có lỗi xảy ra khi xóa cư dân: " + e.getMessage());
                    System.err.println("Error during soft delete: " + e.getMessage());
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            showErrorMessage("Có lỗi xảy ra khi xóa cư dân: " + e.getMessage());
            System.err.println("Error in handleXoaCuDan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Xử lý cập nhật cư dân (edit mode)
     */
    @FXML
    private void handleCapNhatCuDan(ActionEvent event) {
        try {
            clearErrorMessage();
            
            // Validate input
            if (!validateInput()) {
                return;
            }

            // Check permission
            if (!hasPermission()) {
                showErrorMessage("Bạn không có quyền cập nhật cư dân. Chỉ Tổ phó mới được phép.");
                return;
            }

            // Create DTO
            CudanDto cuDanDto = createCuDanDto();

            // Call service to update
            ResponseDto response = cuDanService.updateCuDan(cuDanDto);

            if (response.isSuccess()) {

                
                // Refresh main residents table and switch to residents tab
                refreshMainResidentsTable();
                
                // Show success message and close window
                showSuccessMessage("Cập nhật cư dân thành công");
                closeWindow();
            } else {
                showErrorMessage("Lỗi: " + response.getMessage());
            }

        } catch (Exception e) {
            showErrorMessage("Có lỗi xảy ra: " + e.getMessage());
            System.err.println("Error in handleCapNhatCuDan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Xử lý sự kiện thêm cư dân
     */
    @FXML
    private void handleThemCuDan(ActionEvent event) {
        try {
            clearErrorMessage();
            
            // Validate input
            if (!validateInput()) {
                return;
            }

            // Check permission
            if (!hasPermission()) {
                showErrorMessage("Bạn không có quyền thêm cư dân. Chỉ Tổ phó mới được phép.");
                return;
            }

            // Create DTO
            CudanDto cuDanDto = createCuDanDto();

            // Call service
            ResponseDto response = cuDanService.addCuDan(cuDanDto);

            if (response.isSuccess()) {

                
                // Lưu mã định danh của cư dân vừa tạo thành công
                lastCreatedCuDanMaDinhDanh = cuDanDto.getMaDinhDanh();

                
                // First refresh main residents table and switch to residents tab
                refreshMainResidentsTable();
                
                // Refresh apartment detail windows if apartment code was provided
                if (cuDanDto.getMaCanHo() != null && !cuDanDto.getMaCanHo().trim().isEmpty()) {

                    
                    // Use Platform.runLater to refresh on JavaFX thread with slight delay
                    javafx.application.Platform.runLater(() -> {
                        try {
                            Thread.sleep(100); // Small delay to ensure main table refresh completes first

                            ChiTietCanHoController.refreshAllWindowsForApartment(cuDanDto.getMaCanHo());
                        } catch (Exception e) {
                            System.err.println("ERROR: Exception during apartment refresh: " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                }
                
                // Show success message and close window
                showSuccessMessage("Thêm chủ hộ thành công");
                clearForm();
                closeWindow();
            } else {
                showErrorMessage("Lỗi: " + response.getMessage());
            }

        } catch (Exception e) {
            showErrorMessage("Đã xảy ra lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Xử lý sự kiện đóng
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

        // Validate mã định danh
        if (textFieldMaDinhDanh == null || isBlank(textFieldMaDinhDanh.getText())) {
            showErrorMessage("Vui lòng nhập mã định danh");
            if (textFieldMaDinhDanh != null) textFieldMaDinhDanh.requestFocus();
            return false;
        }

        // Validate giới tính
        if (comboBoxGioiTinh == null || comboBoxGioiTinh.getValue() == null) {
            showErrorMessage("Vui lòng chọn giới tính");
            return false;
        }

        // Validate ngày sinh
        if (datePickerNgaySinh == null || datePickerNgaySinh.getValue() == null) {
            showErrorMessage("Vui lòng chọn ngày sinh");
            return false;
        }

        // Validate ngày sinh không được trong tương lai
        if (datePickerNgaySinh != null && datePickerNgaySinh.getValue() != null && 
            datePickerNgaySinh.getValue().isAfter(LocalDate.now())) {
            showErrorMessage("Ngày sinh không được trong tương lai");
            return false;
        }

        // Validate tuổi hợp lý (không quá 150 tuổi)
        if (datePickerNgaySinh != null && datePickerNgaySinh.getValue() != null && 
            datePickerNgaySinh.getValue().isBefore(LocalDate.now().minusYears(150))) {
            showErrorMessage("Ngày sinh không hợp lý");
            return false;
        }

        // Validate số điện thoại
        if (textFieldSoDienThoai == null || isBlank(textFieldSoDienThoai.getText())) {
            showErrorMessage("Vui lòng nhập số điện thoại");
            if (textFieldSoDienThoai != null) textFieldSoDienThoai.requestFocus();
            return false;
        }

        // Validate phone number format (Vietnam)
        if (textFieldSoDienThoai != null && !isValidPhoneNumber(textFieldSoDienThoai.getText().trim())) {
            showErrorMessage("Số điện thoại không hợp lệ");
            textFieldSoDienThoai.requestFocus();
            return false;
        }

        // Validate email
        if (textFieldEmail == null || isBlank(textFieldEmail.getText())) {
            showErrorMessage("Vui lòng nhập email");
            if (textFieldEmail != null) textFieldEmail.requestFocus();
            return false;
        }

        if (textFieldEmail != null && !isValidEmail(textFieldEmail.getText().trim())) {
            showErrorMessage("Email không hợp lệ");
            textFieldEmail.requestFocus();
            return false;
        }

        // Mã căn hộ không bắt buộc - có thể để trống
        // Trạng thái cư trú cũng không bắt buộc validation

        return true;
    }

    /**
     * Tạo CuDanDto từ form data
     */
    private CudanDto createCuDanDto() {
        CudanDto cuDanDto = new CudanDto();
        
        cuDanDto.setMaDinhDanh(textFieldMaDinhDanh.getText().trim());
        cuDanDto.setHoVaTen(textFieldHoVaTen.getText().trim());
        cuDanDto.setGioiTinh(comboBoxGioiTinh.getValue());
        cuDanDto.setNgaySinh(datePickerNgaySinh.getValue());
        cuDanDto.setSoDienThoai(textFieldSoDienThoai.getText().trim());
        cuDanDto.setEmail(textFieldEmail.getText().trim());
        
        // Mã căn hộ có thể null hoặc rỗng
        String maCanHo = textFieldMaCanHo.getText();
        cuDanDto.setMaCanHo(isBlank(maCanHo) ? null : maCanHo.trim());
        
        cuDanDto.setTrangThaiCuTru(comboBoxTrangThai.getValue());
        cuDanDto.setNgayChuyenDen(datePickerNgayChuyenDen != null ? datePickerNgayChuyenDen.getValue() : null);
        cuDanDto.setNgayChuyenDi(datePickerNgayChuyenDi != null ? datePickerNgayChuyenDi.getValue() : null);

        return cuDanDto;
    }

    /**
     * Check user permission
     */
    private boolean hasPermission() {
        return Session.getCurrentUser() != null && 
               "Tổ phó".equals(Session.getCurrentUser().getVaiTro());
    }

    /**
     * Kiểm tra string có blank không
     */
    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Validate email format
     */
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    }

    /**
     * Validate Vietnam phone number format
     */
    private boolean isValidPhoneNumber(String phone) {
        // Remove all spaces and special characters
        String cleanPhone = phone.replaceAll("[\\s\\-\\(\\)]", "");
        
        // Check Vietnam phone patterns
        return cleanPhone.matches("^(0[3|5|7|8|9])[0-9]{8}$") || // Mobile numbers
               cleanPhone.matches("^(84[3|5|7|8|9])[0-9]{8}$");   // International format
    }

    /**
     * Clear form
     */
    private void clearForm() {
        if (textFieldHoVaTen != null) textFieldHoVaTen.clear();
        if (textFieldMaDinhDanh != null) textFieldMaDinhDanh.clear();
        if (comboBoxGioiTinh != null) comboBoxGioiTinh.setValue(null);
        if (datePickerNgaySinh != null) datePickerNgaySinh.setValue(null);
        if (textFieldSoDienThoai != null) textFieldSoDienThoai.clear();
        if (textFieldEmail != null) textFieldEmail.clear();
        if (textFieldMaCanHo != null) textFieldMaCanHo.clear();
        if (comboBoxTrangThai != null) comboBoxTrangThai.setValue("Cư trú");
        if (datePickerNgayChuyenDen != null) datePickerNgayChuyenDen.setValue(null);
        if (datePickerNgayChuyenDi != null) datePickerNgayChuyenDi.setValue(null);
        
        // Áp dụng logic hiển thị field ngày sau khi reset về "Cư trú"
        handleTrangThaiChange("Cư trú");
        
        clearErrorMessage();
    }

    /**
     * Error message handling
     */
    private void showErrorMessage(String message) {
        ThongBaoController.showError("Lỗi", message);
    }

    private void clearErrorMessage() {
        if (textError != null) {
            textError.setText("");
            textError.setVisible(false);
        }
    }

    /**
     * Success message
     */
    private void showSuccessMessage(String message) {
        ThongBaoController.showSuccess("Thành công", message);
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
     * Hiển thị dialog xác nhận tùy chỉnh
     */
    private boolean showCustomConfirmDialog(String hoVaTen, String maDinhDanh) {
        try {
            // Load FXML file
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/view/xac_nhan.fxml")
            );
            javafx.scene.Parent root = loader.load();
            
            // Get controller
            XacNhanController controller = loader.getController();
            
            // Set content
            controller.setTitle("Xác nhận xóa cư dân");
            controller.setContent("Bạn có chắc chắn muốn xóa cư dân:\n" + 
                                hoVaTen + " (" + maDinhDanh + ")?");
            
            // Create and show dialog
            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle("Xác nhận");
            dialogStage.setScene(new javafx.scene.Scene(root));
            dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            
            // Set owner
            javafx.stage.Stage ownerStage = (javafx.stage.Stage) buttonXoa.getScene().getWindow();
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
            return ThongBaoController.showConfirmation("Xác nhận xóa", "Bạn có chắc chắn muốn xóa cư dân:\n" + 
                               hoVaTen + " (" + maDinhDanh + ")?");
        }
    }

    /**
     * Thiết lập UI chung cho các chế độ chỉnh sửa
     * @param title Tiêu đề của cửa sổ
     */
    private void setCommonEditModeUI(String title) {
        if (labelTieuDe != null) {
            labelTieuDe.setText(title);
        }
        if (buttonThemCuDan != null) {
            buttonThemCuDan.setVisible(false);
        }
        if (buttonLuu != null) {
            buttonLuu.setVisible(true);
            buttonLuu.setOnAction(this::handleCapNhatCuDan);
        }
        if (buttonChinhSua != null) {
            buttonChinhSua.setVisible(false);
        }
        if (buttonXoa != null) {
            buttonXoa.setVisible(false); // Ẩn nút xóa, thay bằng ComboBox trạng thái
        }
        
        // Trong edit mode, setup ComboBox trạng thái phù hợp
        setupEditModeComboBoxTrangThai();
    }
    
    /**
     * Setup ComboBox trạng thái cho edit mode
     */
    private void setupEditModeComboBoxTrangThai() {
        if (comboBoxTrangThai != null) {
            String currentStatus = comboBoxTrangThai.getValue();
            
            // Trong edit mode, luôn cho phép chọn tất cả 3 trạng thái
            comboBoxTrangThai.setItems(FXCollections.observableArrayList("Cư trú", "Không cư trú", "Đã chuyển đi"));
            comboBoxTrangThai.setDisable(false); // Cho phép thay đổi
            
            // Giữ nguyên giá trị hiện tại
            comboBoxTrangThai.setValue(currentStatus);
            

        }
    }

    // Setter for dependency injection
    public void setCuDanService(CuDanService cuDanService) {
        this.cuDanService = cuDanService;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        // Đảm bảo service được khởi tạo nếu chưa có
        if (this.cuDanService == null) {
            this.cuDanService = applicationContext.getBean(CuDanService.class);
        }
    }
    
    /**
     * Lấy mã định danh của cư dân vừa tạo thành công
     */
    public String getLastCreatedCuDanMaDinhDanh() {
        return lastCreatedCuDanMaDinhDanh;
    }
    
    /**
     * Refresh main residents table in Home_list controller and switch to residents tab with loading indicator
     */
    private void refreshMainResidentsTable() {
        try {

            
            // Use Platform.runLater to ensure this runs on JavaFX thread
            javafx.application.Platform.runLater(() -> {
                try {
                    // Show loading state first
                    showLoadingState(true);

                    
                    // Switch to residents tab and refresh data
                    new Thread(() -> {
                        try {
                            Thread.sleep(800); // Longer delay to see loading effect
                            
                            javafx.application.Platform.runLater(() -> {
                                try {
                                    // Try to find Home_list controller from scene graph and refresh
                                    refreshResidentsTableDirectly();

                                    
                                    // Wait a bit more then hide loading
                                    Thread.sleep(200);
                                    javafx.application.Platform.runLater(() -> {
                                        showLoadingState(false);

                                    });
                                    
                                } catch (Exception e) {
                                    javafx.application.Platform.runLater(() -> showLoadingState(false));
                                    System.err.println("ERROR: Failed to refresh residents data: " + e.getMessage());
                                    e.printStackTrace();
                                }
                            });
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            javafx.application.Platform.runLater(() -> showLoadingState(false));
                        }
                    }).start();
                    
                } catch (Exception e) {
                    System.err.println("ERROR: Failed to refresh main residents table: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            System.err.println("ERROR: Exception in refreshMainResidentsTable: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Directly refresh residents table by finding it from scene graph
     */
    private void refreshResidentsTableDirectly() {
        try {
            // Add delay for loading effect
            new Thread(() -> {
                try {
                    Thread.sleep(300); // 300ms delay for loading effect
                    
                    javafx.application.Platform.runLater(() -> {
                        try {
                            // Find all windows and look for Home_list controller
                            for (javafx.stage.Window window : javafx.stage.Window.getWindows()) {
                                if (window instanceof javafx.stage.Stage) {
                                    javafx.stage.Stage stage = (javafx.stage.Stage) window;
                                    javafx.scene.Scene scene = stage.getScene();
                                    if (scene != null && scene.getRoot() != null) {
                                        // Try to find the Home_list controller through scene graph
                                        findAndRefreshHomeListControllerForResidents(scene.getRoot());
                                    }
                                }
                            }

                        } catch (Exception e) {
                            System.err.println("ERROR: Failed to refresh residents data: " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        } catch (Exception e) {
            System.err.println("ERROR: Exception in refreshResidentsTableDirectly: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Find and refresh Home_list controller for residents
     */
    private void findAndRefreshHomeListControllerForResidents(javafx.scene.Node node) {
        try {
            // Check if the node has a controller property
            Object controller = node.getProperties().get("controller");
            if (controller instanceof Home_list) {
                Home_list homeListController = (Home_list) controller;
                
                // Switch to residents tab
                java.lang.reflect.Method gotoCuDanMethod = homeListController.getClass().getDeclaredMethod("gotoCuDan", javafx.event.ActionEvent.class);
                gotoCuDanMethod.setAccessible(true);
                gotoCuDanMethod.invoke(homeListController, (javafx.event.ActionEvent) null);
                
                // Refresh residents data (now public method)
                homeListController.refreshCuDanData();
                

                return;
            }
            
            // Recursively search in children if it's a Parent node
            if (node instanceof javafx.scene.Parent) {
                javafx.scene.Parent parent = (javafx.scene.Parent) node;
                for (javafx.scene.Node child : parent.getChildrenUnmodifiable()) {
                    findAndRefreshHomeListControllerForResidents(child);
                }
            }
        } catch (Exception e) {
            // Silently continue searching - this is expected for most nodes
        }
    }
    
    /**
     * Show/hide loading state on residents table
     */
    private void showLoadingState(boolean isLoading) {
        try {

            
            // Find the main stage and Home_list controller
            javafx.stage.Stage mainStage = (javafx.stage.Stage) javafx.stage.Stage.getWindows().stream()
                .filter(window -> window instanceof javafx.stage.Stage)
                .filter(stage -> "Quản Lý Chung Cư Blue Moon".equals(((javafx.stage.Stage)stage).getTitle()))
                .findFirst().orElse(null);
                
            if (mainStage != null && mainStage.getScene() != null && mainStage.getScene().getRoot() != null) {
                // Look for elements in the scene graph
                javafx.scene.control.TableView<?> residentsTable = (javafx.scene.control.TableView<?>) 
                    findNodeByFxId(mainStage.getScene().getRoot(), "tabelViewCuDan");
                javafx.scene.control.Label resultLabel = (javafx.scene.control.Label) 
                    findNodeByFxId(mainStage.getScene().getRoot(), "labelKetQuaHienThiCuDan");
                javafx.scene.control.Label displayLabel = (javafx.scene.control.Label) 
                    findNodeByFxId(mainStage.getScene().getRoot(), "labelHienThiKetQuaCuDan");
                
                if (isLoading) {

                    if (residentsTable != null) {
                        residentsTable.setDisable(true);
                        residentsTable.setStyle("-fx-opacity: 0.5; -fx-background-color: #f0f0f0;");

                    }
                    if (resultLabel != null) {
                        resultLabel.setText("🔄 Đang tải dữ liệu cư dân...");
                        resultLabel.setStyle("-fx-text-fill: #2196F3; -fx-font-weight: bold; -fx-font-size: 14px;");

                    }
                    if (displayLabel != null) {
                        displayLabel.setText("⏳ Đang xử lý...");
                        displayLabel.setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold; -fx-font-size: 14px;");

                    }
                } else {

                    if (residentsTable != null) {
                        residentsTable.setDisable(false);
                        residentsTable.setStyle("-fx-opacity: 1.0; -fx-background-color: white;");

                    }
                    if (resultLabel != null) {
                        resultLabel.setStyle("-fx-text-fill: black; -fx-font-weight: normal; -fx-font-size: 14px;");

                    }
                    if (displayLabel != null) {
                        displayLabel.setStyle("-fx-text-fill: black; -fx-font-weight: normal; -fx-font-size: 14px;");

                    }
                    
                    // Force update the result count using ApplicationContext if available
                    if (applicationContext != null) {
                        try {
                            Home_list homeListController = applicationContext.getBean(Home_list.class);
                            if (homeListController != null) {
                                java.lang.reflect.Method updateMethod = homeListController.getClass().getDeclaredMethod("updateCuDanKetQuaLabel");
                                updateMethod.setAccessible(true);
                                updateMethod.invoke(homeListController);

                            }
                        } catch (Exception e) {
                            System.err.println("ERROR: Failed to update residents result count: " + e.getMessage());
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("ERROR: Failed to show/hide residents loading state: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Helper method to find a node by fx:id
     */
    private javafx.scene.Node findNodeByFxId(javafx.scene.Node parent, String fxId) {
        if (fxId.equals(parent.getId())) {
            return parent;
        }
        if (parent instanceof javafx.scene.Parent) {
            for (javafx.scene.Node child : ((javafx.scene.Parent) parent).getChildrenUnmodifiable()) {
                javafx.scene.Node result = findNodeByFxId(child, fxId);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
} 
