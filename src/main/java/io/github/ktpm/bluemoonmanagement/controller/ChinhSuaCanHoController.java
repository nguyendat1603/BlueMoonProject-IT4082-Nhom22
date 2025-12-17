package io.github.ktpm.bluemoonmanagement.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import io.github.ktpm.bluemoonmanagement.model.dto.canHo.CanHoChiTietDto;
import io.github.ktpm.bluemoonmanagement.model.dto.canHo.CanHoDto;
import io.github.ktpm.bluemoonmanagement.model.dto.cuDan.ChuHoDto;
import io.github.ktpm.bluemoonmanagement.model.dto.cuDan.CuDanTrongCanHoDto;
import io.github.ktpm.bluemoonmanagement.model.dto.phuongTien.PhuongTienDto;
import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;
import io.github.ktpm.bluemoonmanagement.session.Session;
import io.github.ktpm.bluemoonmanagement.service.canHo.CanHoService;
import io.github.ktpm.bluemoonmanagement.service.cuDan.CuDanService;
import io.github.ktpm.bluemoonmanagement.service.phuongTien.PhuongTienService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller cho chức năng chỉnh sửa căn hộ
 */
@Component
public class ChinhSuaCanHoController implements Initializable {

    // Scroll pane chính
    @FXML private ScrollPane scrollPaneCanHo;

    // Thông tin căn hộ
    @FXML private TextField textFieldToa;
    @FXML private TextField textFieldTang;
    @FXML private TextField textFieldSoNha;
    @FXML private TextField textFieldDienTich;
    @FXML private ComboBox<String> comboBoxTrangThai;
    @FXML private ComboBox<String> comboBoxTinhTrangKiThuat;

    // Thông tin chủ sở hữu
    @FXML private VBox vBoxChuSoHuu;
    @FXML private CheckBox choiceBoxChuSoHuuMoi;
    @FXML private TextField textFieldMaDinhDanh;
    @FXML private CheckBox choiceBoxTaoCuDan;
    @FXML private TextField textFieldHoVaTen;
    @FXML private DatePicker datePickerNgaySinh;
    @FXML private ComboBox<String> comboBoxGioiTinh;
    @FXML private TextField textFieldSoDienThoai;
    @FXML private TextField textFieldEmail;
    @FXML private CheckBox choiceBoxThemChuSoHuu;
    @FXML private DatePicker datePickerNgayChuyenDen;

    // Danh sách cư dân
    @FXML private Button buttonThemCuDan;
    @FXML private TableView<CuDanTrongCanHoDto> tabelViewCuDan;
    @FXML private TableColumn<CuDanTrongCanHoDto, String> tableColumnMaDinhDanh;
    @FXML private TableColumn<CuDanTrongCanHoDto, String> tableColumnHoVaTen;
    @FXML private TableColumn<CuDanTrongCanHoDto, String> tableColumnGioiTinh;
    @FXML private TableColumn<CuDanTrongCanHoDto, String> tableColumnNgaySinh;
    @FXML private TableColumn<CuDanTrongCanHoDto, String> tableColumnNgayChuyenDen;
    @FXML private TableColumn<CuDanTrongCanHoDto, String> tableColumnTrangThaiCuDan;
    @FXML private TableColumn<CuDanTrongCanHoDto, Void> tableColumnThaoTacCuDan;

    // Danh sách phương tiện
    @FXML private Button buttonThemPhuongTien;
    @FXML private TableView<PhuongTienDto> tableViewPhuongTien;
    @FXML private TableColumn<PhuongTienDto, String> tableColumnMaPhuongTien;
    @FXML private TableColumn<PhuongTienDto, String> tableColumnBienSoXe;
    @FXML private TableColumn<PhuongTienDto, String> tableColumnLoaiPhuongTien;
    @FXML private TableColumn<PhuongTienDto, String> tableColumnNgayDangKi;
    @FXML private TableColumn<PhuongTienDto, Void> tableColumnThaoTacPhuongTien;

    // Nút lưu
    @FXML private Button buttonLuu;

    // Data
    private CanHoChiTietDto currentCanHo;
    private ObservableList<CuDanTrongCanHoDto> cuDanList;
    private ObservableList<PhuongTienDto> phuongTienList;
    private String originalOwnerMaDinhDanh; // Lưu mã định danh chủ sở hữu ban đầu

    // Service dependency injection
    @Autowired
    private CanHoService canHoService;

    @Autowired
    private CuDanService cuDanService;

    @Autowired
    private PhuongTienService phuongTienService;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        
        // Initialize lists
        cuDanList = FXCollections.observableArrayList();
        phuongTienList = FXCollections.observableArrayList();
        
        // Setup components
        setupComboBoxes();
        setupTables();
        setupEventHandlers();
        
        // Set table height for scrolling (10 rows approximately)
        if (tabelViewCuDan != null) {
            tabelViewCuDan.setPrefHeight(300.0);
        }
        
        if (tableViewPhuongTien != null) {
            tableViewPhuongTien.setPrefHeight(250.0);
        }
    }

    /**
     * Thiết lập các ComboBox
     */
    private void setupComboBoxes() {
        // Trạng thái căn hộ
        comboBoxTrangThai.setItems(FXCollections.observableArrayList(
            "Chưa bán", "Đã bán", "Đang bán"
        ));
        
        // Tình trạng kỹ thuật
        comboBoxTinhTrangKiThuat.setItems(FXCollections.observableArrayList(
            "Tốt", "Khá", "Trung bình", "Yếu", "Kém"
        ));
        
        // Giới tính
        comboBoxGioiTinh.setItems(FXCollections.observableArrayList(
            "Nam", "Nữ", "Khác"
        ));
    }

    /**
     * Thiết lập các TableView
     */
    private void setupTables() {
        // Setup table cư dân
        if (tableColumnMaDinhDanh != null) {
            tableColumnMaDinhDanh.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMaDinhDanh()));
        }
        
        if (tableColumnHoVaTen != null) {
            tableColumnHoVaTen.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getHoVaTen()));
        }
        
        if (tableColumnGioiTinh != null) {
            tableColumnGioiTinh.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getGioiTinh()));
        }
        
        if (tableColumnNgaySinh != null) {
            tableColumnNgaySinh.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getNgaySinh() != null ? 
                    cellData.getValue().getNgaySinh().toString() : ""));
        }
        
        if (tableColumnNgayChuyenDen != null) {
            tableColumnNgayChuyenDen.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getNgayChuyenDi() != null ? 
                    cellData.getValue().getNgayChuyenDi().toString() : ""));
        }
        
        if (tableColumnTrangThaiCuDan != null) {
            tableColumnTrangThaiCuDan.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTrangThaiCuTru()));
        }

        // Setup action column for cư dân
        setupCuDanActionColumn();

        // Setup table phương tiện
        if (tableColumnMaPhuongTien != null) {
            tableColumnMaPhuongTien.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(String.valueOf(cellData.getValue().getSoThuTu())));
        }
        
        if (tableColumnBienSoXe != null) {
            tableColumnBienSoXe.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBienSo()));
        }
        
        if (tableColumnLoaiPhuongTien != null) {
            tableColumnLoaiPhuongTien.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLoaiPhuongTien()));
        }
        
        if (tableColumnNgayDangKi != null) {
            tableColumnNgayDangKi.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getNgayDangKy() != null ? 
                    cellData.getValue().getNgayDangKy().toString() : ""));
        }

        // Setup action column for phương tiện
        setupPhuongTienActionColumn();

        // Set data to tables
        tabelViewCuDan.setItems(cuDanList);
        tableViewPhuongTien.setItems(phuongTienList);
    }

    /**
     * Thiết lập cột thao tác cho bảng cư dân
     */
    private void setupCuDanActionColumn() {
        if (tableColumnThaoTacCuDan != null) {
            tableColumnThaoTacCuDan.setCellFactory(param -> new javafx.scene.control.TableCell<CuDanTrongCanHoDto, Void>() {
                private final Button editButton = new Button("Sửa");
                private final Button deleteButton = new Button("Xóa");
                
                {
                    editButton.getStyleClass().addAll("action-button", "button-blue");
                    deleteButton.getStyleClass().addAll("action-button", "button-red");
                    
                    editButton.setOnAction(e -> {
                        CuDanTrongCanHoDto cuDan = getTableView().getItems().get(getIndex());
                        handleEditCuDan(cuDan);
                    });
                    
                    deleteButton.setOnAction(e -> {
                        CuDanTrongCanHoDto cuDan = getTableView().getItems().get(getIndex());
                        handleDeleteCuDan(cuDan);
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        javafx.scene.layout.HBox hbox = new javafx.scene.layout.HBox(5);
                        hbox.getChildren().addAll(editButton, deleteButton);
                        setGraphic(hbox);
                    }
                }
            });
        }
    }

    /**
     * Thiết lập cột thao tác cho bảng phương tiện
     */
    private void setupPhuongTienActionColumn() {
        if (tableColumnThaoTacPhuongTien != null) {
            tableColumnThaoTacPhuongTien.setCellFactory(param -> new javafx.scene.control.TableCell<PhuongTienDto, Void>() {
                private final Button editButton = new Button("Sửa");
                private final Button deleteButton = new Button("Xóa");
                
                {
                    editButton.getStyleClass().addAll("action-button", "button-blue");
                    deleteButton.getStyleClass().addAll("action-button", "button-red");
                    
                    editButton.setOnAction(e -> {
                        PhuongTienDto phuongTien = getTableView().getItems().get(getIndex());
                        handleEditPhuongTien(phuongTien);
                    });
                    
                    deleteButton.setOnAction(e -> {
                        PhuongTienDto phuongTien = getTableView().getItems().get(getIndex());
                        handleDeletePhuongTien(phuongTien);
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        javafx.scene.layout.HBox hbox = new javafx.scene.layout.HBox(5);
                        hbox.getChildren().addAll(editButton, deleteButton);
                        setGraphic(hbox);
                    }
                }
            });
        }
    }

    /**
     * Thiết lập event handlers
     */
    private void setupEventHandlers() {
        // Checkbox chủ sở hữu mới
        if (choiceBoxChuSoHuuMoi != null) {
            choiceBoxChuSoHuuMoi.setOnAction(e -> handleChuSoHuuMoiChange());
        }
        
        // Button thêm cư dân
        if (buttonThemCuDan != null) {
            buttonThemCuDan.setOnAction(e -> handleThemCuDan());
        }
        
        // Button thêm phương tiện
        if (buttonThemPhuongTien != null) {
            buttonThemPhuongTien.setOnAction(e -> handleThemPhuongTien());
        }
        
        // Button lưu
        if (buttonLuu != null) {
            buttonLuu.setOnAction(e -> handleLuuThongTin());
        }
        

    }
    


    /**
     * Xử lý khi thay đổi checkbox "Chủ sở hữu mới"
     */
    @FXML
    private void handleChuSoHuuMoiChange() {
        boolean isNewOwner = choiceBoxChuSoHuuMoi.isSelected();
        
        if (isNewOwner) {
            // Hiện các thành phần liên quan và xóa nội dung hiện tại
            choiceBoxTaoCuDan.setVisible(true);
            choiceBoxThemChuSoHuu.setVisible(true);
            datePickerNgayChuyenDen.setVisible(true);
            
            // Xóa thông tin chủ sở hữu hiện tại
            clearOwnerInfo();
        } else {
            // Ẩn các thành phần liên quan và khôi phục thông tin ban đầu
            choiceBoxTaoCuDan.setVisible(false);
            choiceBoxThemChuSoHuu.setVisible(false);
            datePickerNgayChuyenDen.setVisible(false);
            
            // Khôi phục thông tin chủ sở hữu ban đầu
            restoreOriginalOwnerInfo();
        }
    }

    /**
     * Xóa thông tin chủ sở hữu
     */
    private void clearOwnerInfo() {
        textFieldMaDinhDanh.clear();
        textFieldHoVaTen.clear();
        datePickerNgaySinh.setValue(null);
        comboBoxGioiTinh.setValue(null);
        textFieldSoDienThoai.clear();
        textFieldEmail.clear();
    }

    /**
     * Khôi phục thông tin chủ sở hữu ban đầu
     */
    private void restoreOriginalOwnerInfo() {
        if (currentCanHo != null && currentCanHo.getChuHo() != null) {
            var chuHo = currentCanHo.getChuHo();
            textFieldMaDinhDanh.setText(chuHo.getMaDinhDanh());
            textFieldHoVaTen.setText(chuHo.getHoVaTen());
            datePickerNgaySinh.setValue(null); // ChuHoDto không có ngày sinh
            comboBoxGioiTinh.setValue(null); // ChuHoDto không có giới tính
            textFieldSoDienThoai.setText(chuHo.getSoDienThoai());
            textFieldEmail.setText(chuHo.getEmail());
        }
    }

    /**
     * Xử lý thêm cư dân
     */
    @FXML
    private void handleThemCuDan() {

        // TODO: Implement thêm cư dân functionality
        showInfo("Thông báo", "Chức năng thêm cư dân đang được phát triển");
    }

    /**
     * Xử lý sửa cư dân
     */
    private void handleEditCuDan(CuDanTrongCanHoDto cuDan) {

        // TODO: Implement sửa cư dân functionality
        showInfo("Thông báo", "Chức năng sửa cư dân đang được phát triển");
    }

    /**
     * Xử lý xóa cư dân
     */
    private void handleDeleteCuDan(CuDanTrongCanHoDto cuDan) {
        boolean confirm = ThongBaoController.showConfirmation("Xác nhận xóa", "Bạn có chắc chắn muốn xóa cư dân này?\nCư dân: " + cuDan.getHoVaTen());

        if (confirm) {
            try {
                // TODO: Call service to delete cư dân
                cuDanList.remove(cuDan);
                showSuccess("Thành công", "Đã xóa cư dân thành công");
            } catch (Exception e) {
                showError("Lỗi", "Không thể xóa cư dân: " + e.getMessage());
            }
        }
    }

    /**
     * Xử lý thêm phương tiện
     */
    @FXML
    private void handleThemPhuongTien() {

        // TODO: Implement thêm phương tiện functionality
        showInfo("Thông báo", "Chức năng thêm phương tiện đang được phát triển");
    }

    /**
     * Xử lý sửa phương tiện
     */
    private void handleEditPhuongTien(PhuongTienDto phuongTien) {

        // TODO: Implement sửa phương tiện functionality
        showInfo("Thông báo", "Chức năng sửa phương tiện đang được phát triển");
    }

    /**
     * Xử lý xóa phương tiện
     */
    private void handleDeletePhuongTien(PhuongTienDto phuongTien) {
        boolean confirm = ThongBaoController.showConfirmation("Xác nhận xóa", "Bạn có chắc chắn muốn xóa phương tiện này?\nBiển số: " + phuongTien.getBienSo());

        if (confirm) {
            try {
                // TODO: Call service to delete phương tiện
                phuongTienList.remove(phuongTien);
                showSuccess("Thành công", "Đã xóa phương tiện thành công");
            } catch (Exception e) {
                showError("Lỗi", "Không thể xóa phương tiện: " + e.getMessage());
            }
        }
    }

    /**
     * Xử lý lưu thông tin căn hộ
     */
    @FXML
    private void handleLuuThongTin() {
        try {

            
            // Validate input
            if (!validateInput()) {

                return;
            }
            
            // Check permission
            if (Session.getCurrentUser() == null) {

                showError("Lỗi quyền", "Vui lòng đăng nhập để thực hiện thao tác này.");
                return;
            }
            
            String userRole = Session.getCurrentUser().getVaiTro();

            
            if ("Tổ trưởng".equals(userRole) || "Kế toán".equals(userRole)) {

                showError("Lỗi quyền", "Bạn không có quyền chỉnh sửa căn hộ. Chỉ được xem thông tin.");
                return;
            }
            
            // Build DTO from form data
            CanHoDto canHoDto = buildCanHoDto();

            
            // Check if service is available
            if (canHoService == null) {
                showError("Lỗi", "Service chưa sẵn sàng. Vui lòng thử lại.");
                return;
            }
            
            // Call service to update

            ResponseDto response = canHoService.updateCanHo(canHoDto);
            
            if (response != null) {
                if (response.isSuccess()) {

                    showSuccess("Thành công", response.getMessage());
                    
                    // Refresh apartment detail windows if they are open
                    ChiTietCanHoController.refreshAllWindowsForApartment(canHoDto.getMaCanHo());
                    
                    // Close window
                    Stage stage = (Stage) buttonLuu.getScene().getWindow();
                    stage.close();
                } else {

                    showError("Lỗi", response.getMessage());
                }
            } else {
                showError("Lỗi", "Không nhận được phản hồi từ hệ thống");
            }
            
        } catch (Exception e) {
            showError("Lỗi", "Không thể lưu thông tin: " + e.getMessage());
        }
    }
    
    /**
     * Build CanHoDto from form data
     */
    private CanHoDto buildCanHoDto() {
        CanHoDto dto = new CanHoDto();
        
        // Giữ nguyên mã căn hộ và thông tin không thay đổi
        dto.setMaCanHo(currentCanHo.getMaCanHo());
        dto.setToaNha(currentCanHo.getToaNha());
        dto.setTang(currentCanHo.getTang());
        dto.setSoNha(currentCanHo.getSoNha());
        
        // Chỉ cập nhật các field được phép chỉnh sửa
        dto.setDienTich(Double.parseDouble(textFieldDienTich.getText().trim()));
        
        // Parse trạng thái
        String trangThai = comboBoxTrangThai.getValue();
        dto.setDaBanChua("Đã bán".equals(trangThai));
        dto.setTrangThaiKiThuat(comboBoxTinhTrangKiThuat.getValue());
        
        // Determine trangThaiSuDung based on whether apartment has active residents
        boolean hasActiveResidents = cuDanList.stream()
            .anyMatch(cuDan -> "Cư trú".equals(cuDan.getTrangThaiCuTru()));
        dto.setTrangThaiSuDung(hasActiveResidents ? "Đang ở" : "Trống");
        
        // Xử lý thông tin chủ sở hữu
        dto.setChuHo(buildChuHoDto());
        

        return dto;
    }
    
    /**
     * Build ChuHoDto from owner form data
     */
    private ChuHoDto buildChuHoDto() {
        // Nếu không có thông tin chủ sở hữu trong form thì trả về null
        if (isBlank(textFieldMaDinhDanh.getText()) || isBlank(textFieldHoVaTen.getText())) {
            return null;
        }
        
        ChuHoDto chuHoDto = new ChuHoDto();
        chuHoDto.setMaDinhDanh(textFieldMaDinhDanh.getText().trim());
        chuHoDto.setHoVaTen(textFieldHoVaTen.getText().trim());
        chuHoDto.setSoDienThoai(textFieldSoDienThoai.getText().trim());
        chuHoDto.setEmail(textFieldEmail.getText().trim());
        

        return chuHoDto;
    }
    
    /**
     * Check if string is blank or null
     */
    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
    


    /**
     * Validate input data
     */
    private boolean validateInput() {
        // Chỉ validate các field được phép chỉnh sửa
        
        if (textFieldDienTich.getText().trim().isEmpty()) {
            showError("Lỗi", "Vui lòng nhập diện tích");
            return false;
        }
        
        try {
            double dienTich = Double.parseDouble(textFieldDienTich.getText().trim());
            if (dienTich <= 0) {
                showError("Lỗi", "Diện tích phải lớn hơn 0");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Lỗi", "Diện tích phải là số hợp lệ");
            return false;
        }
        
        if (comboBoxTrangThai.getValue() == null) {
            showError("Lỗi", "Vui lòng chọn trạng thái căn hộ");
            return false;
        }
        
        if (comboBoxTinhTrangKiThuat.getValue() == null) {
            showError("Lỗi", "Vui lòng chọn tình trạng kỹ thuật");
            return false;
        }
        
        // Validate thông tin chủ sở hữu nếu có nhập
        if (!validateOwnerInfo()) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Validate owner information
     */
    private boolean validateOwnerInfo() {
        String maDinhDanh = textFieldMaDinhDanh.getText().trim();
        String hoVaTen = textFieldHoVaTen.getText().trim();
        String soDienThoai = textFieldSoDienThoai.getText().trim();
        String email = textFieldEmail.getText().trim();
        
        // Nếu có một trong các field chủ sở hữu được nhập thì phải nhập đầy đủ
        boolean hasAnyOwnerInfo = !isBlank(maDinhDanh) || !isBlank(hoVaTen) || 
                                 !isBlank(soDienThoai) || !isBlank(email);
        
        if (hasAnyOwnerInfo) {
            if (isBlank(maDinhDanh)) {
                showError("Lỗi", "Vui lòng nhập mã định danh chủ sở hữu");
                return false;
            }
            
            if (isBlank(hoVaTen)) {
                showError("Lỗi", "Vui lòng nhập họ và tên chủ sở hữu");
                return false;
            }
            
            if (!isBlank(soDienThoai) && !isValidPhoneNumber(soDienThoai)) {
                showError("Lỗi", "Số điện thoại không hợp lệ (10-11 chữ số)");
                return false;
            }
            
            if (!isBlank(email) && !isValidEmail(email)) {
                showError("Lỗi", "Email không hợp lệ");
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Validate email format
     */
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    
    /**
     * Validate phone number format
     */
    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^[0-9]{10,11}$");
    }
    


    /**
     * Set data cho form
     */
    public void setCanHoData(CanHoChiTietDto canHoData) {
        this.currentCanHo = canHoData;
        
        if (canHoData != null) {
            // Load thông tin căn hộ
            loadCanHoInfo(canHoData);
            
            // Load thông tin chủ sở hữu
            loadOwnerInfo(canHoData);
            
            // Load danh sách cư dân
            loadCuDanList(canHoData);
            
            // Load danh sách phương tiện
            loadPhuongTienList(canHoData);
        }
    }

    /**
     * Load thông tin căn hộ
     */
    private void loadCanHoInfo(CanHoChiTietDto canHoData) {
        // Load data nhưng disable các field không được chỉnh sửa
        textFieldToa.setText(canHoData.getToaNha());
        textFieldToa.setDisable(true); // Không cho chỉnh sửa tòa nhà
        
        textFieldTang.setText(canHoData.getTang());
        textFieldTang.setDisable(true); // Không cho chỉnh sửa tầng
        
        textFieldSoNha.setText(canHoData.getSoNha());
        textFieldSoNha.setDisable(true); // Không cho chỉnh sửa số phòng
        
        // Chỉ cho phép chỉnh sửa diện tích và trạng thái
        textFieldDienTich.setText(String.valueOf(canHoData.getDienTich()));
        comboBoxTrangThai.setValue(canHoData.isDaBanChua() ? "Đã bán" : "Chưa bán");
        comboBoxTinhTrangKiThuat.setValue(canHoData.getTrangThaiKiThuat());
        

    }

    /**
     * Load thông tin chủ sở hữu
     */
    private void loadOwnerInfo(CanHoChiTietDto canHoData) {
        if (canHoData.getChuHo() != null) {
            var chuHo = canHoData.getChuHo();
            textFieldMaDinhDanh.setText(chuHo.getMaDinhDanh());
            textFieldHoVaTen.setText(chuHo.getHoVaTen());
            datePickerNgaySinh.setValue(null); // ChuHoDto không có ngày sinh
            comboBoxGioiTinh.setValue(null); // ChuHoDto không có giới tính
            textFieldSoDienThoai.setText(chuHo.getSoDienThoai());
            textFieldEmail.setText(chuHo.getEmail());
            
            // Lưu mã định danh ban đầu
            originalOwnerMaDinhDanh = chuHo.getMaDinhDanh();
            

        } else {
            // Nếu chưa có chủ sở hữu, để trống form để có thể tạo mới
            textFieldMaDinhDanh.clear();
            textFieldHoVaTen.clear();
            datePickerNgaySinh.setValue(null);
            comboBoxGioiTinh.setValue(null);
            textFieldSoDienThoai.clear();
            textFieldEmail.clear();
            
            originalOwnerMaDinhDanh = null;
            

        }
        
        // Enable all owner fields for editing/creating
        textFieldMaDinhDanh.setDisable(false);
        textFieldHoVaTen.setDisable(false);
        textFieldSoDienThoai.setDisable(false);
        textFieldEmail.setDisable(false);
    }

    /**
     * Load danh sách cư dân
     */
    private void loadCuDanList(CanHoChiTietDto canHoData) {
        cuDanList.clear();
        if (canHoData.getCuDanList() != null) {
            cuDanList.addAll(canHoData.getCuDanList());
        }
    }

    /**
     * Load danh sách phương tiện
     */
    private void loadPhuongTienList(CanHoChiTietDto canHoData) {
        phuongTienList.clear();
        if (canHoData.getPhuongTienList() != null) {
            phuongTienList.addAll(canHoData.getPhuongTienList());
        }
    }

    /**
     * Show error message
     */
    private void showError(String title, String message) {
        ThongBaoController.showError(title, message);
    }

    /**
     * Show success message
     */
    private void showSuccess(String title, String message) {
        ThongBaoController.showSuccess(title, message);
    }

    /**
     * Show info message
     */
    private void showInfo(String title, String message) {
        ThongBaoController.showInfo(title, message);
    }

    // Setters for dependency injection
    public void setCanHoService(CanHoService canHoService) {
        this.canHoService = canHoService;
    }

    public void setCuDanService(CuDanService cuDanService) {
        this.cuDanService = cuDanService;
    }

    public void setPhuongTienService(PhuongTienService phuongTienService) {
        this.phuongTienService = phuongTienService;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    /**
     * Static method để mở form chỉnh sửa căn hộ từ controller khác
     */
    public static void openEditForm(CanHoChiTietDto canHoData, CanHoService canHoService, 
                                   CuDanService cuDanService, PhuongTienService phuongTienService,
                                   ApplicationContext applicationContext, javafx.stage.Window parent) {
        try {

            
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                ChinhSuaCanHoController.class.getResource("/view/chinh_sua_can_ho.fxml")
            );
            javafx.scene.Parent root = loader.load();
            
            // Get controller và inject dependencies
            ChinhSuaCanHoController controller = loader.getController();
            if (controller != null) {
                controller.setCanHoService(canHoService);
                controller.setCuDanService(cuDanService);
                controller.setPhuongTienService(phuongTienService);
                controller.setApplicationContext(applicationContext);
                
                // Load data
                controller.setCanHoData(canHoData);
            }
            
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED); // Bỏ khung cửa sổ hệ điều hành
            stage.setTitle("Chỉnh sửa căn hộ - " + canHoData.getMaCanHo());
            stage.setScene(new javafx.scene.Scene(root, 1000, 700));
            stage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            if (parent != null) {
                stage.initOwner(parent);
            }
            stage.show();
            

            
        } catch (Exception e) {
            ThongBaoController.showError("Không thể mở form chỉnh sửa", "Chi tiết: " + e.getMessage());
        }
    }
    
    /**
     * Refresh data từ database - gọi khi cần reload sau khi thay đổi
     */
    public void refreshData() {
        if (currentCanHo != null && canHoService != null) {
            try {

                
                CanHoDto canHoDto = new CanHoDto();
                canHoDto.setMaCanHo(currentCanHo.getMaCanHo());
                
                CanHoChiTietDto refreshedData = canHoService.getCanHoChiTiet(canHoDto);
                if (refreshedData != null) {
                    setCanHoData(refreshedData);

                } else {
                    showError("Lỗi", "Không thể tải lại dữ liệu căn hộ");
                }
            } catch (Exception e) {
                showError("Lỗi", "Không thể tải lại dữ liệu: " + e.getMessage());
            }
        }
    }
}
