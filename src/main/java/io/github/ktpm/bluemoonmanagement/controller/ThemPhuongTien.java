package io.github.ktpm.bluemoonmanagement.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;
import io.github.ktpm.bluemoonmanagement.model.dto.phuongTien.PhuongTienDto;
import io.github.ktpm.bluemoonmanagement.service.phuongTien.PhuongTienService;
import io.github.ktpm.bluemoonmanagement.session.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

@Component
public class ThemPhuongTien implements Initializable {

    @FXML
    private FontAwesomeIcon buttonClose;

    @FXML
    private Button button_close_up;

    @FXML
    private Button buttonThemPhuongTien;

    @FXML
    private Button buttonLuu;

    @FXML
    private Button buttonChinhSua;

    @FXML
    private Button buttonXoaPhuongTien;

    @FXML
    private HBox hBoxChinhSuaXoaTaiKhoan;

    @FXML
    private TextField textFieldBienSoXe;

    @FXML
    private ComboBox<String> comboBoxLoaiPhuongTien;

    @FXML
    private DatePicker datePickerNgayDangKi;

    @FXML
    private Label labelTieuDe;

    @FXML
    private Text textError;

    // Service instance - sẽ được inject tự động hoặc thủ công
    @Autowired(required = false)
    private PhuongTienService phuongTienService;

    @Autowired
    private ApplicationContext applicationContext;

    // Dữ liệu phương tiện hiện tại (cho chế độ chỉnh sửa)
    private PhuongTienDto currentPhuongTien;

    // Mã căn hộ để liên kết phương tiện
    private String maCanHo;

    // Mode: "ADD" hoặc "EDIT"
    private String mode = "ADD";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        
        // Đảm bảo service được khởi tạo
        ensureServiceAvailable();
        
        setupUI();
    }

    /**
     * Setter để inject ApplicationContext từ bên ngoài
     */
    public void setApplicationContext(ApplicationContext applicationContext) {

        this.applicationContext = applicationContext;
        
        // Sau khi có ApplicationContext, thử lấy service
        ensureServiceAvailable();
    }

    /**
     * Đảm bảo service luôn có sẵn
     */
    private void ensureServiceAvailable() {
        if (phuongTienService == null) {

            try {
                if (applicationContext != null) {
                    phuongTienService = applicationContext.getBean(PhuongTienService.class);

                } else {
                    System.err.println("ERROR: ApplicationContext is null! Service injection will fail.");
                }
            } catch (Exception e) {
                System.err.println("ERROR: Cannot get PhuongTienService from context: " + e.getMessage());
            }
        } else {

        }
    }

    /**
     * Setter để inject service từ bên ngoài (với fallback)
     */
    public void setPhuongTienService(PhuongTienService phuongTienService) {

        this.phuongTienService = phuongTienService;
        
        // Nếu service vẫn null, thử lấy từ ApplicationContext
        if (this.phuongTienService == null) {
            ensureServiceAvailable();
        }
    }

    /**
     * Setter để set mã căn hộ
     */
    public void setMaCanHo(String maCanHo) {
        this.maCanHo = maCanHo;
    }

    /**
     * Thiết lập chế độ thêm mới
     */
    public void setAddMode() {
        this.mode = "ADD";
        labelTieuDe.setText("Thêm phương tiện");
        buttonThemPhuongTien.setVisible(true);
        buttonLuu.setVisible(false);
        hBoxChinhSuaXoaTaiKhoan.setVisible(false);
        clearForm();
    }

    /**
     * Thiết lập chế độ chỉnh sửa
     */
    public void setEditMode(PhuongTienDto phuongTien) {
        this.mode = "EDIT";
        this.currentPhuongTien = phuongTien;
        labelTieuDe.setText("Chi tiết phương tiện");
        buttonThemPhuongTien.setVisible(false);
        buttonLuu.setVisible(false);
        hBoxChinhSuaXoaTaiKhoan.setVisible(true);
        
        // Disable form cho đến khi nhấn "Chỉnh sửa"
        setFormEditable(false);
        
        // Load dữ liệu
        loadPhuongTienData();
    }

    private void setupUI() {
        // Khởi tạo dữ liệu ComboBox
        initializeComboBoxes();
        
        // Thiết lập event handlers
        setupEventHandlers();
        
        // Ẩn error text ban đầu
        textError.setVisible(false);
        
        // Thiết lập ngày đăng ký mặc định là hôm nay
        datePickerNgayDangKi.setValue(LocalDate.now());
    }

    private void initializeComboBoxes() {
        // ComboBox Loại phương tiện
        comboBoxLoaiPhuongTien.getItems().addAll(
            "Ô tô", 
            "Xe máy", 
            "Xe đạp", 
            "Xe đạp điện"
        );
        
        // Thiết lập giá trị mặc định
        comboBoxLoaiPhuongTien.setValue("Ô tô");
    }

    private void setupEventHandlers() {
        // Xử lý nút thêm phương tiện
        buttonThemPhuongTien.setOnAction(this::handleThemPhuongTien);
        
        // Xử lý nút lưu
        buttonLuu.setOnAction(this::handleLuu);
        
        // Xử lý nút chỉnh sửa
        buttonChinhSua.setOnAction(this::handleChinhSua);
        
        // Xử lý nút xóa
        buttonXoaPhuongTien.setOnAction(this::handleXoaPhuongTien);
        
        // Xử lý nút đóng
        button_close_up.setOnAction(this::handleClose);
    }

    @FXML
    private void handleThemPhuongTien(ActionEvent event) {
        try {
            // Xóa thông báo lỗi trước đó
            clearErrorMessage();
            
            // Kiểm tra service đã được inject chưa
            if (phuongTienService == null) {
                showErrorMessage("Lỗi hệ thống: Service chưa được khởi tạo");
                return;
            }
            
            // Kiểm tra quyền truy cập
            if (!hasPermission()) {
                showErrorMessage("Bạn không có quyền thêm phương tiện. Chỉ Tổ phó mới được phép.");
                return;
            }
            
            // Validate dữ liệu đầu vào
            if (!validateInput()) {
                return;
            }
            
            // Tạo DTO phương tiện
            PhuongTienDto phuongTienDto = createPhuongTienDto();
            
            // Gọi service để thêm phương tiện
            ResponseDto response = phuongTienService.themPhuongTien(phuongTienDto);
            
            // Sử dụng reflection để truy cập các field do vấn đề Lombok
            boolean isSuccess = getResponseSuccess(response);
            String message = getResponseMessage(response);
            
            if (isSuccess) {
                showSuccessMessage("Thêm phương tiện thành công: " + message);
                clearForm();
                closeWindow();
            } else {
                showErrorMessage("Lỗi: " + message);
            }
            
        } catch (Exception e) {
            showErrorMessage("Đã xảy ra lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLuu(ActionEvent event) {
        try {
            // Xóa thông báo lỗi trước đó
            clearErrorMessage();
            
            // Kiểm tra service đã được inject chưa
            if (phuongTienService == null) {
                showErrorMessage("Lỗi hệ thống: Service chưa được khởi tạo");
                return;
            }
            
            // Kiểm tra quyền truy cập
            if (!hasPermission()) {
                showErrorMessage("Bạn không có quyền cập nhật phương tiện. Chỉ Tổ phó mới được phép.");
                return;
            }
            
            // Validate dữ liệu đầu vào
            if (!validateInput()) {
                return;
            }
            
            // Tạo DTO phương tiện với ID từ dữ liệu hiện tại
            PhuongTienDto phuongTienDto = createPhuongTienDto();
            if (currentPhuongTien != null) {
                phuongTienDto.setSoThuTu(currentPhuongTien.getSoThuTu());
            }
            
            // Gọi service để cập nhật phương tiện
            ResponseDto response = phuongTienService.capNhatPhuongTien(phuongTienDto);
            
            // Sử dụng reflection để truy cập các field do vấn đề Lombok
            boolean isSuccess = getResponseSuccess(response);
            String message = getResponseMessage(response);
            
            if (isSuccess) {
                showSuccessMessage("Cập nhật phương tiện thành công: " + message);
                
                // Cập nhật lại currentPhuongTien và chuyển về chế độ xem
                currentPhuongTien = phuongTienDto;
                setFormEditable(false);
                buttonLuu.setVisible(false);
                hBoxChinhSuaXoaTaiKhoan.setVisible(true);
            } else {
                showErrorMessage("Lỗi: " + message);
            }
            
        } catch (Exception e) {
            showErrorMessage("Đã xảy ra lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChinhSua(ActionEvent event) {
        // Chuyển sang chế độ chỉnh sửa
        setFormEditable(true);
        hBoxChinhSuaXoaTaiKhoan.setVisible(false);
        buttonLuu.setVisible(true);
        
        // Focus vào trường đầu tiên
        textFieldBienSoXe.requestFocus();
    }

    @FXML
    private void handleXoaPhuongTien(ActionEvent event) {
        try {
            // Xóa thông báo lỗi trước đó
            clearErrorMessage();
            
            // Kiểm tra service đã được inject chưa
            if (phuongTienService == null) {
                showErrorMessage("Lỗi hệ thống: Service chưa được khởi tạo");
                return;
            }
            
            // Kiểm tra quyền truy cập
            if (!hasPermission()) {
                showErrorMessage("Bạn không có quyền xóa phương tiện. Chỉ Tổ phó mới được phép.");
                return;
            }
            
            if (currentPhuongTien == null) {
                showErrorMessage("Không có dữ liệu phương tiện để xóa");
                return;
            }
            
            // Gọi service để xóa phương tiện
            ResponseDto response = phuongTienService.xoaPhuongTien(currentPhuongTien);
            
            // Sử dụng reflection để truy cập các field do vấn đề Lombok
            boolean isSuccess = getResponseSuccess(response);
            String message = getResponseMessage(response);
            
            if (isSuccess) {
                showSuccessMessage("Xóa phương tiện thành công: " + message);
                closeWindow();
            } else {
                showErrorMessage("Lỗi: " + message);
            }
            
        } catch (Exception e) {
            showErrorMessage("Đã xảy ra lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClose(ActionEvent event) {
        closeWindow();
    }

    private boolean hasPermission() {
        try {
            if (Session.getCurrentUser() == null) return false;
            
            // Sử dụng reflection để lấy vaiTro do vấn đề Lombok
            java.lang.reflect.Field vaiTroField = Session.getCurrentUser().getClass().getDeclaredField("vaiTro");
            vaiTroField.setAccessible(true);
            String vaiTro = (String) vaiTroField.get(Session.getCurrentUser());
            
            return "Tổ phó".equals(vaiTro);
        } catch (Exception e) {
            System.err.println("Lỗi khi kiểm tra quyền: " + e.getMessage());
            return false;
        }
    }

    private boolean validateInput() {
        // Validate biển số xe
        if (isBlank(textFieldBienSoXe.getText())) {
            showErrorMessage("Vui lòng nhập biển số xe");
            textFieldBienSoXe.requestFocus();
            return false;
        }
        
        // Validate biển số xe format (có thể tùy chỉnh theo format Việt Nam)
        if (!isValidBienSoXe(textFieldBienSoXe.getText().trim())) {
            showErrorMessage("Biển số xe không hợp lệ");
            textFieldBienSoXe.requestFocus();
            return false;
        }
        
        // Validate loại phương tiện
        if (comboBoxLoaiPhuongTien.getValue() == null) {
            showErrorMessage("Vui lòng chọn loại phương tiện");
            return false;
        }
        
        // Validate ngày đăng ký
        if (datePickerNgayDangKi.getValue() == null) {
            showErrorMessage("Vui lòng chọn ngày đăng ký");
            return false;
        }
        
        // Validate ngày đăng ký không được trong tương lai
        if (datePickerNgayDangKi.getValue().isAfter(LocalDate.now())) {
            showErrorMessage("Ngày đăng ký không được trong tương lai");
            datePickerNgayDangKi.requestFocus();
            return false;
        }
        
        return true;
    }

    private PhuongTienDto createPhuongTienDto() {
        PhuongTienDto phuongTienDto = new PhuongTienDto();
        
        try {
            // Sử dụng reflection để set các field nếu Lombok setter không hoạt động
            java.lang.reflect.Field[] fields = PhuongTienDto.class.getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                field.setAccessible(true);
                switch (field.getName()) {
                    case "bienSo":
                        field.set(phuongTienDto, textFieldBienSoXe.getText().trim().toUpperCase());
                        break;
                    case "loaiPhuongTien":
                        field.set(phuongTienDto, comboBoxLoaiPhuongTien.getValue());
                        break;
                    case "ngayDangKy":
                        field.set(phuongTienDto, datePickerNgayDangKi.getValue());
                        break;
                    case "maCanHo":
                        field.set(phuongTienDto, maCanHo);  // Liên kết với căn hộ
                        break;
                }
            }
            

        } catch (Exception e) {
            System.err.println("Lỗi khi set field cho PhuongTienDto: " + e.getMessage());
        }
        
        return phuongTienDto;
    }

    private void loadPhuongTienData() {
        if (currentPhuongTien == null) return;
        
        try {
            // Load dữ liệu vào form
            textFieldBienSoXe.setText(currentPhuongTien.getBienSo());
            comboBoxLoaiPhuongTien.setValue(currentPhuongTien.getLoaiPhuongTien());
            datePickerNgayDangKi.setValue(currentPhuongTien.getNgayDangKy());
        } catch (Exception e) {
            System.err.println("Lỗi khi load dữ liệu phương tiện: " + e.getMessage());
        }
    }

    private void setFormEditable(boolean editable) {
        textFieldBienSoXe.setEditable(editable);
        comboBoxLoaiPhuongTien.setDisable(!editable);
        datePickerNgayDangKi.setDisable(!editable);
    }

    private void clearForm() {
        // Clear all fields
        textFieldBienSoXe.clear();
        comboBoxLoaiPhuongTien.setValue("Ô tô");
        datePickerNgayDangKi.setValue(LocalDate.now());
        
        // Clear error message
        clearErrorMessage();
        
        // Enable form for editing
        setFormEditable(true);
    }

    private void clearErrorMessage() {
        textError.setVisible(false);
        textError.setText("");
    }

    private void showErrorMessage(String message) {
        ThongBaoController.showError("Lỗi", message);
    }

    private void showSuccessMessage(String message) {
        ThongBaoController.showSuccess("Thành công", message);
    }

    private void closeWindow() {
        Stage stage = (Stage) button_close_up.getScene().getWindow();
        stage.close();
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    private boolean isValidBienSoXe(String bienSo) {
        // Validate biển số xe theo format Việt Nam
        // Format cơ bản: XX-XXXX (ít nhất 6 ký tự, có thể có dấu gạch ngang)
        return bienSo.matches("^[0-9A-Z\\-\\.]{6,12}$");
    }

    /**
     * Helper method để lấy success field từ ResponseDto bằng reflection
     */
    private boolean getResponseSuccess(ResponseDto response) {
        try {
            java.lang.reflect.Field field = ResponseDto.class.getDeclaredField("success");
            field.setAccessible(true);
            return (Boolean) field.get(response);
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy success từ ResponseDto: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper method để lấy message field từ ResponseDto bằng reflection
     */
    private String getResponseMessage(ResponseDto response) {
        try {
            java.lang.reflect.Field field = ResponseDto.class.getDeclaredField("message");
            field.setAccessible(true);
            return (String) field.get(response);
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy message từ ResponseDto: " + e.getMessage());
            return "Lỗi không xác định";
        }
    }
}
