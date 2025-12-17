package io.github.ktpm.bluemoonmanagement.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;
import io.github.ktpm.bluemoonmanagement.model.dto.canHo.CanHoChiTietDto;
import io.github.ktpm.bluemoonmanagement.model.dto.canHo.CanHoDto;
import io.github.ktpm.bluemoonmanagement.model.dto.cuDan.CuDanTrongCanHoDto;
import io.github.ktpm.bluemoonmanagement.model.dto.hoaDon.HoaDonDto;
import io.github.ktpm.bluemoonmanagement.model.dto.phuongTien.PhuongTienDto;
import io.github.ktpm.bluemoonmanagement.service.cache.CacheDataService;
import io.github.ktpm.bluemoonmanagement.service.canHo.CanHoService;
import io.github.ktpm.bluemoonmanagement.service.hoaDon.HoaDonService;
import io.github.ktpm.bluemoonmanagement.service.phuongTien.PhuongTienService;
import io.github.ktpm.bluemoonmanagement.session.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller cho trang chi tiết căn hộ
 */
@Component
public class ChiTietCanHoController implements Initializable {

    // Static list to track open windows for refresh functionality
    private static final List<ChiTietCanHoController> openWindows = new ArrayList<>();

    // Header buttons - Tab navigation
    @FXML private Button buttonThongTin;
    @FXML private Button buttonCuDan;
    @FXML private Button buttonPhuongTien;
    @FXML private Button buttonThuPhi;
    @FXML private Button button_close_up;

    // Tab content panels
    @FXML private AnchorPane anchorPaneThongTin;
    @FXML private AnchorPane anchorPaneCuDan;
    @FXML private AnchorPane anchorPanePhuongTien;
    @FXML private AnchorPane anchorPaneThuPhi;

    // Thông tin căn hộ labels
    @FXML private Label labelMaCanHo;
    @FXML private Label labelSoNha;
    @FXML private Label labelTang;
    @FXML private Label labelToa;
    @FXML private Label labelDienTich;
    @FXML private Label labelTrangThaiCanHo;
    @FXML private Label labelTinhTrangSuDung;
    @FXML private Label labelTinhTrangKiThuat;

    // Thông tin chủ sở hữu labels
    @FXML private VBox vBoxChuSoHuu;
    @FXML private Label labelMaDinhDanh;
    @FXML private Label labelHoVaTen;
    @FXML private Label labelNgaySinh;
    @FXML private Label labelGioiTinh;
    @FXML private Label labelTrangThaiChuSoHuu;
    @FXML private Label labelSoDienThoai;
    @FXML private Label labelEmail;

    // Cư dân tab
    @FXML private TableView<CuDanTrongCanHoDto> tableViewCuDan;
    @FXML private TableColumn<CuDanTrongCanHoDto, String> tableColumnMaCanHoCuDan;
    @FXML private TableColumn<CuDanTrongCanHoDto, String> tableColumnMaDinhDanhCuDan;
    @FXML private TableColumn<CuDanTrongCanHoDto, String> tableColumnHoVaTenCuDan;
    @FXML private TableColumn<CuDanTrongCanHoDto, String> tableColumnGioiTinhCuDan;
    @FXML private TableColumn<CuDanTrongCanHoDto, String> tableColumnNgaySinhCuDan;
    @FXML private TableColumn<CuDanTrongCanHoDto, String> tableColumnSoDienThoaiCuDan;
    // @FXML private TableColumn<CuDanTrongCanHoDto, String> tableColumnEmailCuDan; // Removed email column as requested
    @FXML private TableColumn<CuDanTrongCanHoDto, String> tableColumnQuanHeChuHo;
    @FXML private TableColumn<CuDanTrongCanHoDto, String> tableColumnTrangThaiCuDan;
    @FXML private TableColumn<CuDanTrongCanHoDto, Void> tableColumnThaoTacCuDan;
    @FXML private ComboBox<String> comboBoxSoKetQuaCuDan;
    @FXML private TextField textFieldTimKiemCuDan;
    @FXML private TextField textFieldMaDinhDanh;
    @FXML private ComboBox<String> comboBoxTrangThaiCuDan;
    @FXML private Button buttonTimKiemCuDan;
    @FXML private Label labelHienThiKetQuaCuDan;

    // Phương tiện tab
    @FXML private TableView<PhuongTienDto> tableViewPhuongTien;
    @FXML private TableColumn<PhuongTienDto, String> tableColumnMaPhuongTien;
    @FXML private TableColumn<PhuongTienDto, String> tableColumnBienSoXe;
    @FXML private TableColumn<PhuongTienDto, String> tableColumnLoaiPhuongTien;
    @FXML private TableColumn<PhuongTienDto, String> tableColumnNgayDangKi;
    @FXML private TableColumn<PhuongTienDto, Void> tableColumnThaoTacPhuongTien;
    @FXML private TextField textFieldMaSoXe;
    @FXML private ComboBox<String> comboBoxLoaiPhuongTien;
    @FXML private Button buttonTimKiemPhuongTien;
    @FXML private ComboBox<String> comboBoxSoKetQuaPhuongTien;
    @FXML private Label labelHienThiKetQuaPhuongTien;
    @FXML private Button buttonThemPhuongTien;

    // Thu phí tab
    @FXML private TableView<HoaDonDto> tableViewThuPhi;
    @FXML private TableColumn<HoaDonDto, String> tableColumnMaHoaDon;
    @FXML private TableColumn<HoaDonDto, String> tableColumnTenKhoanThu;
    @FXML private TableColumn<HoaDonDto, Integer> tableColumnSoTien;
    @FXML private TableColumn<HoaDonDto, String> tableColumnHanThu;
    @FXML private TableColumn<HoaDonDto, String> tableColumnThaoTacThuPhi;
    @FXML private TextField textFieldTenKhoanThu;
    @FXML private ComboBox<String> comboBoxLoaiKhoanThu;
    @FXML private ComboBox<String> comboBoxTrangThaiThanhToan; // Thêm ComboBox trạng thái thanh toán
    @FXML private Button buttonTimKiemThuPhi;
    @FXML private Label labelTongSoTien;
    @FXML private Button buttonThuToanBo;
    @FXML private Button buttonXemLichSu;
    @FXML private CheckBox checkBoxKhongTinhBatBuoc;
    
    // Edit button in thông tin tab 
    @FXML private Button buttonChinhSua;

    // Data
    private CanHoChiTietDto currentCanHo;
    private ObservableList<CuDanTrongCanHoDto> cuDanList;
    private ObservableList<PhuongTienDto> phuongTienList;
    private ObservableList<HoaDonDto> hoaDonList;

    // Service dependency injection
    @Autowired
    private CanHoService canHoService;

    @Autowired
    private PhuongTienService phuongTienService;

    @Autowired
    private CacheDataService cacheDataService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private HoaDonService hoaDonService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Controller initialized
        
        // Add this instance to tracking list
        openWindows.add(this);
        
        // Initialize lists
        cuDanList = FXCollections.observableArrayList();
        phuongTienList = FXCollections.observableArrayList();
        hoaDonList = FXCollections.observableArrayList();
        
        // Đảm bảo services được khởi tạo nếu có ApplicationContext
        if (applicationContext != null) {
            ensureServicesAvailable();
        }
        

        
        // Setup components
        setupTabNavigation();
        setupTables();
        setupComboBoxes();
        
        // Setup permissions for buttons
        setupButtonPermissions();
        
        // Setup search listeners for auto-search on input change
        setupSearchListeners();
        
        // Default to first tab
        showThongTinTab();
    }

    /**
     * Thiết lập navigation giữa các tab
     */
    private void setupTabNavigation() {
        buttonThongTin.setOnAction(e -> showThongTinTab());
        buttonCuDan.setOnAction(e -> showCuDanTab());
        buttonPhuongTien.setOnAction(e -> showPhuongTienTab());
        buttonThuPhi.setOnAction(e -> showThuPhiTab());
        button_close_up.setOnAction(e -> handleClose());
    }

    /**
     * Thiết lập các table
     */
    private void setupTables() {
        // Setup table cư dân với null checks và error handling
        setupCuDanTable();
        setupPhuongTienTable();
        setupThuPhiTable();
    }

    /**
     * Setup table cư dân riêng biệt để dễ debug
     */
    private void setupCuDanTable() {
        try {
            
            if (tableViewCuDan == null) {
                System.err.println("ERROR: tableViewCuDan is NULL!");
                return;
            }

            // Setup table cư dân
            if(tableColumnMaCanHoCuDan != null){
                tableColumnMaCanHoCuDan.setCellValueFactory(cellData -> 
                    new javafx.beans.property.SimpleStringProperty(
                        currentCanHo != null ? currentCanHo.getMaCanHo() : ""));
            } else {
                System.err.println("WARNING: tableColumnMaCanHoCuDan is NULL!");
            }

            if (tableColumnMaDinhDanhCuDan != null) {
                tableColumnMaDinhDanhCuDan.setCellValueFactory(cellData -> 
                    new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getMaDinhDanh() != null ? 
                        cellData.getValue().getMaDinhDanh() : ""));
            } else {
                System.err.println("WARNING: tableColumnMaDinhDanhCuDan is NULL!");
            }
            
            if (tableColumnHoVaTenCuDan != null) {
                tableColumnHoVaTenCuDan.setCellValueFactory(cellData -> 
                    new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getHoVaTen() != null ? 
                        cellData.getValue().getHoVaTen() : ""));
            } else {
                System.err.println("WARNING: tableColumnHoVaTenCuDan is NULL!");
            }
            
            if (tableColumnGioiTinhCuDan != null) {
                tableColumnGioiTinhCuDan.setCellValueFactory(cellData -> 
                    new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getGioiTinh() != null ? 
                        cellData.getValue().getGioiTinh() : ""));
            } else {
                System.err.println("WARNING: tableColumnGioiTinhCuDan is NULL!");
            }
            
            if (tableColumnSoDienThoaiCuDan != null) {
                tableColumnSoDienThoaiCuDan.setCellValueFactory(cellData -> 
                    new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getSoDienThoai() != null ? 
                        cellData.getValue().getSoDienThoai() : ""));
            } else {
                System.err.println("WARNING: tableColumnSoDienThoaiCuDan is NULL!");
            }
            
            
            
            if (tableColumnQuanHeChuHo != null) {
                tableColumnQuanHeChuHo.setCellValueFactory(cellData -> {
                    // Determine relationship based on whether this person is the apartment owner
                    String relationship = "Cư dân";
                    if (currentCanHo != null && currentCanHo.getChuHo() != null 
                        && cellData.getValue().getMaDinhDanh() != null) {
                        if (cellData.getValue().getMaDinhDanh().equals(currentCanHo.getChuHo().getMaDinhDanh())) {
                            relationship = "Chủ hộ";
                        }
                    }
                    return new javafx.beans.property.SimpleStringProperty(relationship);
                });
            } else {
                System.err.println("WARNING: tableColumnQuanHeChuHo is NULL!");
            }
            
            if (tableColumnNgaySinhCuDan != null) {
                tableColumnNgaySinhCuDan.setCellValueFactory(cellData -> 
                    new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getNgaySinh() != null ? 
                        cellData.getValue().getNgaySinh().toString() : ""));
            } else {
                System.err.println("WARNING: tableColumnNgaySinhCuDan is NULL!");
            }
            
            if (tableColumnTrangThaiCuDan != null) {
                tableColumnTrangThaiCuDan.setCellValueFactory(cellData -> 
                    new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getTrangThaiCuTru() != null ? 
                        cellData.getValue().getTrangThaiCuTru() : ""));
            } else {
                System.err.println("WARNING: tableColumnTrangThaiCuDan is NULL!");
            }

            // Thêm cột thao tác cho cư dân
            if (tableColumnThaoTacCuDan != null) {
                setupCuDanActions();
            } else {
                System.err.println("WARNING: tableColumnThaoTacCuDan is NULL!");
            }
            
 
            
            
        } catch (Exception e) {
            System.err.println("ERROR: Exception in setupCuDanTable: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Thiết lập chiều rộng cột cho bảng cư dân để phù hợp với kích thước cửa sổ 720x450
     */
   

    /**
     * Setup table phương tiện
     */
    private void setupPhuongTienTable() {
        try {
            if (tableViewPhuongTien == null) {
                System.err.println("WARNING: tableViewPhuongTien is NULL!");
                return;
            }

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

            // Thêm cột thao tác cho phương tiện
            if (tableColumnThaoTacPhuongTien != null) {
                setupPhuongTienActions();
            }
            
        } catch (Exception e) {
            System.err.println("ERROR: Exception in setupPhuongTienTable: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Setup table thu phí
     */
    private void setupThuPhiTable() {
        try {
            if (tableViewThuPhi == null) {
                System.err.println("WARNING: tableViewThuPhi is NULL!");
                return;
            }

            // Setup table thu phí/hóa đơn
            if (tableColumnMaHoaDon != null) {
                tableColumnMaHoaDon.setCellValueFactory(cellData -> 
                    new javafx.beans.property.SimpleStringProperty(String.valueOf(cellData.getValue().getMaHoaDon())));
            }
            
            if (tableColumnTenKhoanThu != null) {
                tableColumnTenKhoanThu.setCellValueFactory(cellData -> 
                    new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTenKhoanThu()));
            }
            
            if (tableColumnSoTien != null) {
                tableColumnSoTien.setCellValueFactory(cellData -> 
                    new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getSoTien()));
            }
            
            if (tableColumnHanThu != null) {
                tableColumnHanThu.setCellValueFactory(cellData -> 
                    new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getNgayNop() != null ? 
                        cellData.getValue().getNgayNop().toString() : "Chưa nộp"));
            }

            // Setup action column for thu phi
            if (tableColumnThaoTacThuPhi != null) {
                setupThuPhiActions();
            }
            
        } catch (Exception e) {
            System.err.println("ERROR: Exception in setupThuPhiTable: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Setup actions cho table phương tiện
     */
    private void setupPhuongTienActions() {
        tableColumnThaoTacPhuongTien.setCellFactory(column -> new javafx.scene.control.TableCell<PhuongTienDto, Void>() {
            private final javafx.scene.control.Button editButton = new javafx.scene.control.Button("Sửa");
            private final javafx.scene.control.Button deleteButton = new javafx.scene.control.Button("Xóa");
            private final javafx.scene.layout.HBox pane = new javafx.scene.layout.HBox(editButton, deleteButton);

            {
                pane.setSpacing(5);
                editButton.setOnAction(event -> {
                    PhuongTienDto phuongTien = getTableView().getItems().get(getIndex());
                    handleEditPhuongTien(phuongTien);
                });
                deleteButton.setOnAction(event -> {
                    PhuongTienDto phuongTien = getTableView().getItems().get(getIndex());
                    handleDeletePhuongTien(phuongTien);
                });
                
                // Style buttons
                editButton.getStyleClass().addAll("action-button", "button-sky-blue");
                deleteButton.getStyleClass().addAll("action-button", "button-red");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                    setAlignment(javafx.geometry.Pos.CENTER);
                }
            }
        });
    }

    /**
     * Setup actions cho table thu phí
     */
    private void setupThuPhiActions() {
        // Setup cột thao tác thu phí - chỉ hiển thị trạng thái
        tableColumnThaoTacThuPhi.setCellValueFactory(cellData -> {
            HoaDonDto hoaDon = cellData.getValue();
            String trangThai = hoaDon.isDaNop() ? "Đã nộp" : "Chưa nộp";
            return new javafx.beans.property.SimpleStringProperty(trangThai);
        });
        
        // Thiết lập màu sắc cho text dựa trên trạng thái
        tableColumnThaoTacThuPhi.setCellFactory(column -> new javafx.scene.control.TableCell<HoaDonDto, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setAlignment(javafx.geometry.Pos.CENTER);
                    
                    // Thiết lập màu sắc dựa trên trạng thái
                    if ("Đã nộp".equals(item)) {
                        setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;"); // Màu xanh
                    } else {
                        setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;"); // Màu đỏ
                    }
                }
            }
        });
    }

    /**
     * Thiết lập cột thao tác cho bảng cư dân (Sửa, Xóa)
     */
    private void setupCuDanActions() {
        tableColumnThaoTacCuDan.setCellFactory(param -> new javafx.scene.control.TableCell<CuDanTrongCanHoDto, Void>() {
            private final javafx.scene.control.Button editButton = new javafx.scene.control.Button("Sửa");
            private final javafx.scene.control.Button deleteButton = new javafx.scene.control.Button("Xóa");
            private final javafx.scene.layout.HBox pane = new javafx.scene.layout.HBox(editButton, deleteButton);

            {
                pane.setSpacing(10);
                editButton.getStyleClass().addAll("action-button", "button-yellow");
                deleteButton.getStyleClass().addAll("action-button", "button-red");

                editButton.setOnAction(event -> {
                    CuDanTrongCanHoDto cuDan = getTableView().getItems().get(getIndex());
                    handleEditCuDan(cuDan);
                });

                deleteButton.setOnAction(event -> {
                    CuDanTrongCanHoDto cuDan = getTableView().getItems().get(getIndex());
                    handleDeleteCuDan(cuDan);
                });

                // Disable nút cho tất cả trừ Tổ phó (chỉ Tổ phó được phép sửa/xóa cư dân)
                try {
                    String userRole = getCurrentUserRole();
                    if (!"Tổ phó".equals(userRole)) {
                        editButton.setDisable(true);
                        deleteButton.setDisable(true);
                        editButton.setOpacity(0.5);
                        deleteButton.setOpacity(0.5);
                    }
                } catch (Exception e) {
                    System.err.println("ERROR: Cannot setup cu dan action button permissions: " + e.getMessage());
                }
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                    setAlignment(javafx.geometry.Pos.CENTER);
                }
            }
        });
    }

    /**
     * Xử lý sự kiện chỉnh sửa cư dân
     */
    private void handleEditCuDan(CuDanTrongCanHoDto cuDan) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/view/them_cu_dan.fxml") // Tái sử dụng form thêm cư dân
            );
            javafx.scene.Parent root = loader.load();
            
            ThemCuDanController controller = loader.getController();
            
            // QUAN TRỌNG: Inject application context và mã căn hộ
            if (applicationContext != null) {
                controller.setApplicationContext(applicationContext);
            }
            // Tự động gán mã căn hộ hiện tại
            controller.setupEditModeFromApartmentDetail(cuDan, currentCanHo.getMaCanHo());
            
            Stage stage = new Stage();
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED); // Bỏ khung cửa sổ hệ điều hành
            stage.setTitle("Chỉnh sửa cư dân - " + cuDan.getHoVaTen());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(button_close_up.getScene().getWindow());
            
            stage.showAndWait();
            
            // Refresh lại dữ liệu sau khi đóng form
            refreshData();

        } catch (Exception e) {
            showError("Lỗi", "Không thể mở form chỉnh sửa cư dân: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Xử lý sự kiện xóa cư dân (xóa mềm)
     */
    private void handleDeleteCuDan(CuDanTrongCanHoDto cuDan) {
        boolean confirm = ThongBaoController.showConfirmation("Xác nhận xóa", "Bạn có chắc chắn muốn xóa cư dân " + cuDan.getHoVaTen() + "?\nHành động này sẽ ghi nhận ngày chuyển đi của cư dân.");

        if (confirm) {
            try {
                // Lấy service từ application context nếu chưa có
                if (applicationContext != null) {
                    io.github.ktpm.bluemoonmanagement.service.cuDan.CuDanService cuDanService = applicationContext.getBean(io.github.ktpm.bluemoonmanagement.service.cuDan.CuDanService.class);
                    
                    io.github.ktpm.bluemoonmanagement.model.dto.cuDan.CudanDto cudanDto = new io.github.ktpm.bluemoonmanagement.model.dto.cuDan.CudanDto();
                    cudanDto.setMaDinhDanh(cuDan.getMaDinhDanh());

                    ResponseDto response = cuDanService.deleteCuDan(cudanDto);

                    if (response.isSuccess()) {
                        showSuccess("Thành công", response.getMessage());
                        refreshData();
                    } else {
                        showError("Lỗi", response.getMessage());
                    }
                } else {
                    showError("Lỗi hệ thống", "ApplicationContext không khả dụng.");
                }
            } catch (Exception e) {
                showError("Lỗi", "Không thể xóa cư dân: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Thiết lập các ComboBox
     */
    private void setupComboBoxes() {
        // Setup ComboBox số kết quả cư dân
        if (comboBoxSoKetQuaCuDan != null) {
            comboBoxSoKetQuaCuDan.setItems(FXCollections.observableArrayList("10", "25", "50", "100"));
            comboBoxSoKetQuaCuDan.setValue("10");
        }

        // Setup ComboBox trạng thái cư dân
        if (comboBoxTrangThaiCuDan != null) {
            comboBoxTrangThaiCuDan.setItems(FXCollections.observableArrayList("Tất cả", "Đang cư trú", "Đã chuyển đi", "Tạm vắng"));
            comboBoxTrangThaiCuDan.setValue("Tất cả");
        }

        // Setup ComboBox loại phương tiện - will be populated from actual data after loading
        if (comboBoxLoaiPhuongTien != null) {
            comboBoxLoaiPhuongTien.setItems(FXCollections.observableArrayList("Tất cả"));
            comboBoxLoaiPhuongTien.setValue("Tất cả");
        }

        // Setup ComboBox số kết quả phương tiện
        if (comboBoxSoKetQuaPhuongTien != null) {
            comboBoxSoKetQuaPhuongTien.setItems(FXCollections.observableArrayList("10", "25", "50", "100"));
            comboBoxSoKetQuaPhuongTien.setValue("10");
        }

        // Setup ComboBox loại khoản thu - will be populated from actual data after loading
        if (comboBoxLoaiKhoanThu != null) {
            comboBoxLoaiKhoanThu.setItems(FXCollections.observableArrayList("Tất cả"));
            comboBoxLoaiKhoanThu.setValue("Tất cả");
        }
        
        // Setup ComboBox trạng thái thanh toán
        if (comboBoxTrangThaiThanhToan != null) {
            comboBoxTrangThaiThanhToan.setItems(FXCollections.observableArrayList("Tất cả", "Đã nộp", "Chưa nộp"));
            comboBoxTrangThaiThanhToan.setValue("Tất cả");
        }
    }

    /**
     * Setup search listeners for auto-search on input change
     */
    private void setupSearchListeners() {
        // Listener cho textFieldTenKhoanThu
        if (textFieldTenKhoanThu != null) {
            textFieldTenKhoanThu.textProperty().addListener((obs, oldText, newText) -> handleTimKiemThuPhi());
        }
        
        // Listener cho comboBoxLoaiKhoanThu
        if (comboBoxLoaiKhoanThu != null) {
            comboBoxLoaiKhoanThu.valueProperty().addListener((obs, oldValue, newValue) -> handleTimKiemThuPhi());
        }
        
        // Listener cho comboBoxTrangThaiThanhToan
        if (comboBoxTrangThaiThanhToan != null) {
            comboBoxTrangThaiThanhToan.valueProperty().addListener((obs, oldValue, newValue) -> handleTimKiemThuPhi());
        }
        
    }

    /**
     * Thiết lập quyền cho các nút dựa trên vai trò người dùng
     */
    private void setupButtonPermissions() {
        try {
            
            // Debug current user
            if (Session.getCurrentUser() == null) {
            } else {
            }
            
            String userRole = getCurrentUserRole();
            
            boolean isToTruong = "Tổ trưởng".equals(userRole);
            boolean isKeToan = "Kế toán".equals(userRole);
            boolean isToPho = "Tổ phó".equals(userRole);
            boolean shouldDisableButtons = isToTruong || isKeToan || isToPho ; // Tất cả vai trò đều có logic riêng
            
            
            if (shouldDisableButtons) {
                // Disable/làm mờ các nút tùy theo vai trò
                disableButtonsForRestrictedRoles(userRole);
            } else {
            }
        } catch (Exception e) {
            System.err.println("ERROR: Cannot setup button permissions: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Lấy vai trò người dùng hiện tại
     */
    private String getCurrentUserRole() {
        try {
            if (Session.getCurrentUser() == null) {
                return null;
            }
            return Session.getCurrentUser().getVaiTro();
        } catch (Exception e) {
            System.err.println("ERROR: Cannot get current user role: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Disable các nút tùy theo vai trò:
     * - Tổ trưởng: Disable tất cả (chỉ xem)
     * - Kế toán: Disable căn hộ/cư dân/phương tiện, được phép khoản thu
     * - Tổ phó: Disable khoản thu, được phép căn hộ/cư dân/phương tiện
     */
    private void disableButtonsForRestrictedRoles(String userRole) {
        
        boolean isToTruong = "Tổ trưởng".equals(userRole);
        boolean isKeToan = "Kế toán".equals(userRole);
        boolean isToPho = "Tổ phó".equals(userRole);
        
        // Phần thông tin căn hộ - chỉ Tổ phó và Kế toán bị disable chỉnh sửa
        if (buttonChinhSua != null) {
            boolean shouldDisableEdit = isToTruong || isKeToan;
            buttonChinhSua.setDisable(shouldDisableEdit);
            if (shouldDisableEdit) {
            buttonChinhSua.setOpacity(0.5);
            }
        } else {
        }
        
        // Phần cư dân - chỉ Tổ phó được phép (nút trong table sẽ được xử lý riêng)
        
        // Phần phương tiện - chỉ Tổ phó được phép
        if (buttonThemPhuongTien != null) {
            boolean shouldDisableVehicle = isToTruong || isKeToan;
            buttonThemPhuongTien.setDisable(shouldDisableVehicle);
            if (shouldDisableVehicle) {
            buttonThemPhuongTien.setOpacity(0.5);
            }
        } else {
        }
        
        // Phần khoản thu - chỉ Kế toán được phép thu phí
        
        // Nút thu toàn bộ - chỉ Kế toán được phép
        if (buttonThuToanBo != null) {
            if (!isKeToan) {
                buttonThuToanBo.setDisable(true);
                buttonThuToanBo.setOpacity(0.5);
            } else {
                buttonThuToanBo.setDisable(false);
                buttonThuToanBo.setOpacity(1.0);
            }
        } else {
        }
        
        // Nút xem lịch sử - tất cả vai trò đều được phép
        if (buttonXemLichSu != null) {
            buttonXemLichSu.setDisable(false);
            buttonXemLichSu.setOpacity(1.0);
        } else {
        }
        
        // Note: Nút tìm kiếm (buttonTimKiemCuDan, buttonTimKiemPhuongTien, buttonTimKiemThuPhi) 
        // sẽ KHÔNG bị disable theo yêu cầu
        
    }

    /**
     * Cập nhật ComboBox với dữ liệu thực từ database
     */
    private void updateComboBoxesWithRealData() {
        // Update vehicle types from loaded data
        if (comboBoxLoaiPhuongTien != null && phuongTienList != null) {
            ObservableList<String> vehicleTypes = FXCollections.observableArrayList("Tất cả");
            phuongTienList.stream()
                .map(PhuongTienDto::getLoaiPhuongTien)
                .filter(type -> type != null && !type.trim().isEmpty())
                .distinct()
                .forEach(vehicleTypes::add);
            comboBoxLoaiPhuongTien.setItems(vehicleTypes);
            // Ensure current value is safe before checking contains
            String currentVehicleValue = comboBoxLoaiPhuongTien.getValue();
            if (currentVehicleValue == null || !vehicleTypes.contains(currentVehicleValue)) {
                comboBoxLoaiPhuongTien.setValue("Tất cả");
            }
        }

        // Update fee types from loaded data  
        if (comboBoxLoaiKhoanThu != null && hoaDonList != null) {
            ObservableList<String> feeTypes = FXCollections.observableArrayList("Tất cả");
            hoaDonList.stream()
                .map(HoaDonDto::getTenKhoanThu)
                .filter(type -> type != null && !type.trim().isEmpty())
                .distinct()
                .forEach(feeTypes::add);
            comboBoxLoaiKhoanThu.setItems(feeTypes);
            // Ensure current value is safe before checking contains
            String currentValue = comboBoxLoaiKhoanThu.getValue();
            if (currentValue == null || !feeTypes.contains(currentValue)) {
                comboBoxLoaiKhoanThu.setValue("Tất cả");
            }
        }
    }

    /**
     * Load dữ liệu từ cache hoặc service tùy theo mode
     * @param maCanHo mã căn hộ cần load
     * @param forceFromService true để bắt buộc load từ service, false để ưu tiên cache
     */
    private void loadData(String maCanHo, boolean forceFromService) {
        try {
            if (maCanHo == null || maCanHo.trim().isEmpty()) {
                showError("Lỗi dữ liệu", "Mã căn hộ không hợp lệ.");
                return;
            }

            CanHoChiTietDto chiTiet = null;
            
            if (cacheDataService != null) {
            }
            
            if (!forceFromService && cacheDataService != null && cacheDataService.isCacheLoaded()) {
                chiTiet = cacheDataService.getCanHoChiTietFromCache(maCanHo);

            }

            // Fallback to service if cache fails or forced
            if (chiTiet == null) {
                chiTiet = loadDataFromService(maCanHo);
            }

            if (chiTiet != null) {
                currentCanHo = chiTiet;

                // Load danh sách từ data và lọc chỉ hiển thị cư dân chưa bị xóa (chưa có ngày chuyển đi)
                cuDanList.clear();


                if (chiTiet.getCuDanList() != null) {


                    // Hiển thị tất cả cư dân có trạng thái khác "Đã chuyển đi"
                    // Chấp nhận: "Cư trú", "Không cư trú", hoặc null
                    chiTiet.getCuDanList().stream()
                        .filter(cuDan -> {
                            String trangThai = cuDan.getTrangThaiCuTru();
                            return trangThai == null ||
                                   !"Đã chuyển đi".equals(trangThai);
                        })
                        .forEach(cuDanList::add);

                }

                phuongTienList.clear();
                if (chiTiet.getPhuongTienList() != null) {
                    phuongTienList.addAll(chiTiet.getPhuongTienList());
                }

                hoaDonList.clear();
                if (chiTiet.getHoaDonList() != null) {
                    hoaDonList.addAll(chiTiet.getHoaDonList());
                }

                // Cập nhật UI
                updateThongTinCanHo();
                setTableData();
                updateTongSoTien();

                // Cập nhật ComboBox với dữ liệu thực
                updateComboBoxesWithRealData();

            } else {
                showError("Không tìm thấy dữ liệu", "Không tìm thấy thông tin chi tiết cho căn hộ: " + maCanHo);
                clearAllData();
            }
        } catch (Exception e) {
            showError("Lỗi kết nối", "Không thể tải dữ liệu: " + e.getMessage());
            clearAllData();
        }
    }

    /**
     * Load dữ liệu từ service (chỉ khi cần thiết)
     */
    private CanHoChiTietDto loadDataFromService(String maCanHo) {
        try {
            if (canHoService == null) {
                System.err.println("CanHoService is null");
                return null;
            }

            CanHoDto canHoDto = new CanHoDto();
            canHoDto.setMaCanHo(maCanHo);

            return canHoService.getCanHoChiTiet(canHoDto);
        } catch (Exception e) {
            System.err.println("Error loading from service: " + e.getMessage());
            return null;
        }
    }

    /**
     * Xóa tất cả dữ liệu khi có lỗi
     */
    private void clearAllData() {
        currentCanHo = null;
        cuDanList.clear();
        phuongTienList.clear();
        hoaDonList.clear();

        // Clear UI
        clearThongTinCanHo();
        setTableData();
        if (labelTongSoTien != null) {
            labelTongSoTien.setText("0 VNĐ");
        }
    }

    /**
     * Xóa thông tin căn hộ trên UI
     */
    private void clearThongTinCanHo() {
        if (labelMaCanHo != null) labelMaCanHo.setText("-");
        if (labelSoNha != null) labelSoNha.setText("-");
        if (labelTang != null) labelTang.setText("-");
        if (labelToa != null) labelToa.setText("-");
        if (labelDienTich != null) labelDienTich.setText("- m²");
        if (labelTinhTrangKiThuat != null) labelTinhTrangKiThuat.setText("-");
        if (labelTinhTrangSuDung != null) labelTinhTrangSuDung.setText("-");

        // Clear thông tin chủ hộ
        if (labelMaDinhDanh != null) labelMaDinhDanh.setText("-");
        if (labelHoVaTen != null) labelHoVaTen.setText("-");
        if (labelNgaySinh != null) {
            labelNgaySinh.setText("-");
            labelNgaySinh.setVisible(false);
        }
        if (labelGioiTinh != null) {
            labelGioiTinh.setText("-");
            labelGioiTinh.setVisible(false);
        }
        if (labelTrangThaiChuSoHuu != null) labelTrangThaiChuSoHuu.setText("-");
        if (labelSoDienThoai != null) labelSoDienThoai.setText("-");
        if (labelEmail != null) labelEmail.setText("-");

        // Ẩn thông tin chủ hộ
        if (vBoxChuSoHuu != null) vBoxChuSoHuu.setVisible(false);
    }

    /**
     * Set dữ liệu cho các table
     */
    private void setTableData() {

        // Ensure this runs on JavaFX Application Thread
        javafx.application.Platform.runLater(() -> {
            try {
                // Set data for all tables
                if (tableViewCuDan != null && cuDanList != null) {

                    tableViewCuDan.setItems(cuDanList);
                    tableViewCuDan.refresh(); // Force refresh

                    // Force column refresh to ensure data is displayed
                    if (!tableViewCuDan.getColumns().isEmpty()) {
                        tableViewCuDan.getColumns().get(0).setVisible(false);
                        tableViewCuDan.getColumns().get(0).setVisible(true);
                    }



                    updateResultCount(); // Hiển thị số kết quả



                }

                if (tableViewPhuongTien != null && phuongTienList != null) {
                    tableViewPhuongTien.setItems(phuongTienList);
                    tableViewPhuongTien.refresh();
                }

                if (tableViewThuPhi != null && hoaDonList != null) {
                    tableViewThuPhi.setItems(hoaDonList);
                    tableViewThuPhi.refresh();
                }

            } catch (Exception e) {
                System.err.println("ERROR: Exception in setTableData: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Cập nhật thông tin căn hộ lên UI
     */
    private void updateThongTinCanHo() {
        if (currentCanHo == null) {
            clearThongTinCanHo();
            return;
        }

        if (labelMaCanHo != null) labelMaCanHo.setText(currentCanHo.getMaCanHo());
        if (labelSoNha != null) labelSoNha.setText(currentCanHo.getSoNha());
        if (labelTang != null) labelTang.setText(currentCanHo.getTang());
        if (labelToa != null) labelToa.setText(currentCanHo.getToaNha());
        if (labelDienTich != null) labelDienTich.setText(String.valueOf(currentCanHo.getDienTich()) + " m²");
        if (labelTinhTrangKiThuat != null) {
            labelTinhTrangKiThuat.setText(currentCanHo.getTrangThaiKiThuat());
        }
        if (labelTinhTrangSuDung != null) {
            labelTinhTrangSuDung.setText(currentCanHo.getTrangThaiSuDung());
        }

        // Cập nhật thông tin chủ sở hữu nếu có
        if (currentCanHo.getChuHo() != null) {
            if (labelMaDinhDanh != null) {
                labelMaDinhDanh.setText(currentCanHo.getChuHo().getMaDinhDanh());
            }
            if (labelHoVaTen != null) {
                labelHoVaTen.setText(currentCanHo.getChuHo().getHoVaTen());
            }

            // Hiển thị ngày sinh và giới tính nếu có
            if (labelNgaySinh != null) {
                if (currentCanHo.getChuHo().getNgaySinh() != null) {
                    labelNgaySinh.setText(currentCanHo.getChuHo().getNgaySinh().toString());
                    labelNgaySinh.setVisible(true);
                } else {
                    labelNgaySinh.setText("Chưa cập nhật");
                    labelNgaySinh.setVisible(true);
                }
            }
            if (labelGioiTinh != null) {
                if (currentCanHo.getChuHo().getGioiTinh() != null && !currentCanHo.getChuHo().getGioiTinh().trim().isEmpty()) {
                    labelGioiTinh.setText(currentCanHo.getChuHo().getGioiTinh());
                    labelGioiTinh.setVisible(true);
                } else {
                    labelGioiTinh.setText("Chưa cập nhật");
                    labelGioiTinh.setVisible(true);
                }
            }

            if (labelTrangThaiChuSoHuu != null) labelTrangThaiChuSoHuu.setText(currentCanHo.getChuHo().getTrangThaiCuTru());
            if (labelSoDienThoai != null) labelSoDienThoai.setText(currentCanHo.getChuHo().getSoDienThoai());
            if (labelEmail != null) labelEmail.setText(currentCanHo.getChuHo().getEmail());

            // Hiển thị VBox chủ sở hữu
            if (vBoxChuSoHuu != null) vBoxChuSoHuu.setVisible(true);
        } else {
            // Ẩn VBox chủ sở hữu nếu chưa có chủ
            if (vBoxChuSoHuu != null) vBoxChuSoHuu.setVisible(false);
        }
    }

    // Tab navigation methods
    @FXML
    private void showThongTinTab() {
        hideAllTabs();
        if (anchorPaneThongTin != null) {
            anchorPaneThongTin.setVisible(true);
        }
        updateTabStyles("thongtin");

        // Refresh dữ liệu thông tin căn hộ mỗi khi mở tab để đảm bảo hiển thị dữ liệu mới nhất
        if (currentCanHo != null && currentCanHo.getMaCanHo() != null) {
            loadData(currentCanHo.getMaCanHo(), true);
        }
    }

    @FXML
    private void showCuDanTab() {
        hideAllTabs();
        if (anchorPaneCuDan != null) {
            anchorPaneCuDan.setVisible(true);
        } else {
        }
        updateTabStyles("cudan");

        // Refresh dữ liệu cư dân mỗi khi mở tab
        if (currentCanHo != null && currentCanHo.getMaCanHo() != null) {

            // Load lại dữ liệu từ service để đảm bảo có dữ liệu mới nhất
            loadData(currentCanHo.getMaCanHo(), true);

            // Clear search fields để hiển thị tất cả dữ liệu
            if (textFieldTimKiemCuDan != null) {
                textFieldTimKiemCuDan.clear();
            }
            if (textFieldMaDinhDanh != null) {
                textFieldMaDinhDanh.clear();
            }

            // Update result count
            updateResultCount();
        }

        // Debug table visibility and data

    }

    @FXML
    private void showPhuongTienTab() {
        hideAllTabs();
        if (anchorPanePhuongTien != null) {
            anchorPanePhuongTien.setVisible(true);
        }
        updateTabStyles("phuongtien");
    }

    @FXML
    private void showThuPhiTab() {
        hideAllTabs();
        if (anchorPaneThuPhi != null) {
            anchorPaneThuPhi.setVisible(true);
        }
        updateTabStyles("thuphi");
    }

    private void hideAllTabs() {
        if (anchorPaneThongTin != null) anchorPaneThongTin.setVisible(false);
        if (anchorPaneCuDan != null) anchorPaneCuDan.setVisible(false);
        if (anchorPanePhuongTien != null) anchorPanePhuongTien.setVisible(false);
        if (anchorPaneThuPhi != null) anchorPaneThuPhi.setVisible(false);
    }

    private void updateTabStyles(String activeTab) {
        // Reset all tab styles
        if (buttonThongTin != null) buttonThongTin.getStyleClass().removeAll("active-tab");
        if (buttonCuDan != null) buttonCuDan.getStyleClass().removeAll("active-tab");
        if (buttonPhuongTien != null) buttonPhuongTien.getStyleClass().removeAll("active-tab");
        if (buttonThuPhi != null) buttonThuPhi.getStyleClass().removeAll("active-tab");

        // Set active tab style
        switch (activeTab) {
            case "thongtin":
                if (buttonThongTin != null) buttonThongTin.getStyleClass().add("active-tab");
                break;
            case "cudan":
                if (buttonCuDan != null) buttonCuDan.getStyleClass().add("active-tab");
                break;
            case "phuongtien":
                if (buttonPhuongTien != null) buttonPhuongTien.getStyleClass().add("active-tab");
                break;
            case "thuphi":
                if (buttonThuPhi != null) buttonThuPhi.getStyleClass().add("active-tab");
                break;
        }
    }

    // Search and filter methods
    @FXML
    private void handleTimKiemCuDan() {
        String keywordHoTen = textFieldTimKiemCuDan != null ? textFieldTimKiemCuDan.getText().trim() : "";
        String keywordMaDinhDanh = textFieldMaDinhDanh != null ? textFieldMaDinhDanh.getText().trim() : "";
        String trangThaiCuDan = comboBoxTrangThaiCuDan != null ? comboBoxTrangThaiCuDan.getValue() : "";

        // Nếu tất cả ô tìm kiếm đều trống thì hiển thị toàn bộ danh sách
        if (keywordHoTen.isEmpty() && keywordMaDinhDanh.isEmpty() &&
            ("Tất cả".equals(trangThaiCuDan) || trangThaiCuDan == null || trangThaiCuDan.isEmpty())) {
            setTableData(); // Reset to full list
            updateResultCount();
            return;
        }

        // Filter cư dân list based on search keywords
        if (cuDanList != null) {
            ObservableList<CuDanTrongCanHoDto> filteredList = cuDanList.stream()
                .filter(cuDan -> {
                    boolean matchesHoTen = keywordHoTen.isEmpty() ||
                        (cuDan.getHoVaTen() != null && cuDan.getHoVaTen().toLowerCase().contains(keywordHoTen.toLowerCase()));
                    boolean matchesMaDinhDanh = keywordMaDinhDanh.isEmpty() ||
                        (cuDan.getMaDinhDanh() != null && cuDan.getMaDinhDanh().toLowerCase().contains(keywordMaDinhDanh.toLowerCase()));
                    boolean matchesTrangThai = "Tất cả".equals(trangThaiCuDan) ||
                        trangThaiCuDan == null || trangThaiCuDan.isEmpty() ||
                        (cuDan.getTrangThaiCuTru() != null && cuDan.getTrangThaiCuTru().equals(trangThaiCuDan));
                    // Tất cả điều kiện phải thỏa mãn (AND logic)
                    return matchesHoTen && matchesMaDinhDanh && matchesTrangThai;
                })
                .collect(FXCollections::observableArrayList,
                        ObservableList::add,
                        ObservableList::addAll);

            if (tableViewCuDan != null) {
                tableViewCuDan.setItems(filteredList);
                updateResultCount(filteredList.size(), cuDanList.size());

            }
        }
    }

    @FXML
    private void handleTimKiemPhuongTien() {
        String maSoXe = textFieldMaSoXe != null ? textFieldMaSoXe.getText().trim() : "";
        String loaiPhuongTien = comboBoxLoaiPhuongTien != null ? comboBoxLoaiPhuongTien.getValue() : "";

        // Nếu tất cả điều kiện tìm kiếm đều trống thì hiển thị toàn bộ
        if (maSoXe.isEmpty() && ("Tất cả".equals(loaiPhuongTien) || loaiPhuongTien == null || loaiPhuongTien.isEmpty())) {
            setTableData(); // Reset to full list
            return;
        }

        // Filter phương tiện list
        if (phuongTienList != null) {
            ObservableList<PhuongTienDto> filteredList = phuongTienList.stream()
                .filter(pt -> {
                    boolean matchesBienSo = maSoXe.isEmpty() ||
                        (pt.getBienSo() != null && pt.getBienSo().toLowerCase().contains(maSoXe.toLowerCase()));
                    boolean matchesLoai = "Tất cả".equals(loaiPhuongTien) ||
                        loaiPhuongTien == null || loaiPhuongTien.isEmpty() ||
                        (pt.getLoaiPhuongTien() != null && pt.getLoaiPhuongTien().equals(loaiPhuongTien));
                    return matchesBienSo && matchesLoai;
                })
                .collect(FXCollections::observableArrayList,
                        ObservableList::add,
                        ObservableList::addAll);

            if (tableViewPhuongTien != null) {
                tableViewPhuongTien.setItems(filteredList);
            }

            // Update label hiển thị kết quả
            if (labelHienThiKetQuaPhuongTien != null) {
                labelHienThiKetQuaPhuongTien.setText("Hiển thị " + filteredList.size() + "/" + phuongTienList.size() + " phương tiện");
            }

        }
    }

    @FXML
    private void handleThemPhuongTien() {
        try {
            // Kiểm tra quyền
            String userRole = getCurrentUserRole();
            if ("Tổ trưởng".equals(userRole)) {
                showError("Không có quyền", "Bạn không có quyền thêm phương tiện. Chỉ có Tổ phó mới có thể thêm.");
                return;
            }

            if (currentCanHo == null || currentCanHo.getMaCanHo() == null) {
                showError("Lỗi", "Không có thông tin căn hộ để thêm phương tiện");
                return;
            }

            // Sử dụng FXMLLoader và để FXML tự tạo controller
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/view/phuong_tien.fxml")
            );

            // Load view - FXML sẽ tự tạo controller
            javafx.scene.Parent root = loader.load();

            // Get controller được tạo bởi FXML
            io.github.ktpm.bluemoonmanagement.controller.ThemPhuongTien controller = loader.getController();

            // Inject ApplicationContext trước tiên
            if (applicationContext != null) {
                controller.setApplicationContext(applicationContext);
            } else {
                System.err.println("ERROR: ApplicationContext is null in ChiTietCanHoController!");
            }

            // Inject services
            if (phuongTienService != null) {
                controller.setPhuongTienService(phuongTienService);
            } else {
            }

            // Set mã căn hộ
            controller.setMaCanHo(currentCanHo.getMaCanHo());

            // Set chế độ thêm mới
            controller.setAddMode();

            // Tạo cửa sổ mới
            Stage newStage = new Stage();
            newStage.initStyle(javafx.stage.StageStyle.UNDECORATED); // Bỏ khung cửa sổ hệ điều hành
            newStage.setScene(new Scene(root));
            newStage.setTitle("Thêm phương tiện - Căn hộ " + currentCanHo.getMaCanHo());

            // Không cho tương tác cửa sổ cha khi đang mở
            newStage.initModality(Modality.APPLICATION_MODAL);

            // Gán owner là cửa sổ hiện tại
            Stage currentStage = (Stage) button_close_up.getScene().getWindow();
            newStage.initOwner(currentStage);

            // Hiển thị cửa sổ và đợi đóng
            newStage.showAndWait();

            // Refresh data sau khi đóng form thêm phương tiện
            refreshData();

        } catch (IOException e) {
            showError("Lỗi", "Không thể mở cửa sổ thêm phương tiện: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showError("Lỗi", "Đã xảy ra lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Xử lý sự kiện chỉnh sửa phương tiện
     */
    private void handleEditPhuongTien(PhuongTienDto phuongTien) {
        try {
            // Sử dụng FXMLLoader và để FXML tự tạo controller
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/view/phuong_tien.fxml")
            );

            // Load view - FXML sẽ tự tạo controller
            javafx.scene.Parent root = loader.load();

            // Get controller được tạo bởi FXML
            io.github.ktpm.bluemoonmanagement.controller.ThemPhuongTien controller = loader.getController();

            // Inject ApplicationContext và services
            if (applicationContext != null) {
                controller.setApplicationContext(applicationContext);
            }
            if (phuongTienService != null) {
                controller.setPhuongTienService(phuongTienService);
            }

            // Set chế độ chỉnh sửa
            controller.setEditMode(phuongTien);

            // Tạo cửa sổ mới
            Stage newStage = new Stage();
            newStage.initStyle(javafx.stage.StageStyle.UNDECORATED); // Bỏ khung cửa sổ hệ điều hành
            newStage.setScene(new Scene(root));
            newStage.setTitle("Chỉnh sửa phương tiện - " + phuongTien.getBienSo());

            // Không cho tương tác cửa sổ cha khi đang mở
            newStage.initModality(Modality.APPLICATION_MODAL);

            // Gán owner là cửa sổ hiện tại
            Stage currentStage = (Stage) button_close_up.getScene().getWindow();
            newStage.initOwner(currentStage);

            // Hiển thị cửa sổ và đợi đóng
            newStage.showAndWait();

            // Refresh data sau khi đóng form chỉnh sửa phương tiện
            refreshData();

        } catch (Exception e) {
            showError("Lỗi", "Không thể mở cửa sổ chỉnh sửa phương tiện: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Xử lý sự kiện xóa phương tiện
     */
    private void handleDeletePhuongTien(PhuongTienDto phuongTien) {
        try {
            // Hiển thị dialog xác nhận
            boolean confirm = ThongBaoController.showConfirmation("Xác nhận xóa", "Bạn có chắc chắn muốn xóa phương tiện " + phuongTien.getBienSo() + "?");

            if (confirm) {
                if (phuongTienService != null) {
                    // Gọi service để xóa phương tiện
                    ResponseDto result = phuongTienService.xoaPhuongTien(phuongTien);

                    // Sử dụng reflection để lấy kết quả
                    try {
                        java.lang.reflect.Field successField = ResponseDto.class.getDeclaredField("success");
                        successField.setAccessible(true);
                        boolean success = (Boolean) successField.get(result);

                        java.lang.reflect.Field messageField = ResponseDto.class.getDeclaredField("message");
                        messageField.setAccessible(true);
                        String message = (String) messageField.get(result);

                        if (success) {
                            showSuccess("Thành công", "Đã xóa phương tiện thành công: " + message);

                            // Refresh data sau khi xóa phương tiện
                            refreshData();
                        } else {
                            showError("Lỗi", "Không thể xóa phương tiện: " + message);
                        }
                    } catch (Exception e) {
                        showError("Lỗi", "Lỗi khi xử lý phản hồi: " + e.getMessage());
                    }
                } else {
                    showError("Lỗi", "Service chưa được khởi tạo");
                }
            }

        } catch (Exception e) {
            showError("Lỗi", "Đã xảy ra lỗi khi xóa phương tiện: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTimKiemThuPhi() {
        // Safeguard: Don't run search if data is not initialized yet
        if (hoaDonList == null || currentCanHo == null) {
            return;
        }

        final String tenKhoanThu = textFieldTenKhoanThu != null ? textFieldTenKhoanThu.getText().trim() : "";
        final String loaiKhoanThu = comboBoxLoaiKhoanThu != null ?
            (comboBoxLoaiKhoanThu.getValue() != null ? comboBoxLoaiKhoanThu.getValue() : "Tất cả") : "Tất cả";
        final String trangThaiThanhToan = comboBoxTrangThaiThanhToan != null ?
            (comboBoxTrangThaiThanhToan.getValue() != null ? comboBoxTrangThaiThanhToan.getValue() : "Tất cả") : "Tất cả";

        if (tenKhoanThu.isEmpty() && "Tất cả".equals(loaiKhoanThu) && "Tất cả".equals(trangThaiThanhToan)) {
            setTableData(); // Reset to full list
            return;
        }

        // Filter hóa đơn list
        if (hoaDonList != null) {
            ObservableList<HoaDonDto> filteredList = hoaDonList.stream()
                .filter(hd -> {
                    boolean matchesTen = tenKhoanThu.isEmpty() ||
                        (hd.getTenKhoanThu() != null && hd.getTenKhoanThu().toLowerCase().contains(tenKhoanThu.toLowerCase()));
                    boolean matchesLoai = "Tất cả".equals(loaiKhoanThu) ||
                        (hd.getTenKhoanThu() != null && hd.getTenKhoanThu().contains(loaiKhoanThu));

                    // Thêm logic lọc theo trạng thái thanh toán
                    boolean matchesTrangThai = true;
                    if (!"Tất cả".equals(trangThaiThanhToan) && trangThaiThanhToan != null) {
                        if ("Đã nộp".equals(trangThaiThanhToan)) {
                            matchesTrangThai = hd.isDaNop();
                        } else if ("Chưa nộp".equals(trangThaiThanhToan)) {
                            matchesTrangThai = !hd.isDaNop();
                        }
                    }

                    return matchesTen && matchesLoai && matchesTrangThai;
                })
                .collect(FXCollections::observableArrayList,
                        ObservableList::add,
                        ObservableList::addAll);

            if (tableViewThuPhi != null) {
                tableViewThuPhi.setItems(filteredList);
            }

            // Cập nhật tổng số tiền sau khi lọc
            updateTongSoTien();
        }
    }

    // Thu phí actions
    @FXML
    private void handleThuToanBo() {
        // Kiểm tra quyền
        try {
            String userRole = getCurrentUserRole();
            if (!"Kế toán".equals(userRole)) {
                showError("Không có quyền", "Bạn không có quyền thực hiện thu toàn bộ. Chỉ có Kế toán mới có thể thực hiện.");
                return;
            }
        } catch (Exception e) {
            System.err.println("ERROR: Cannot check user permission: " + e.getMessage());
        }

        boolean baoGomBatBuoc = checkBoxKhongTinhBatBuoc != null ? !checkBoxKhongTinhBatBuoc.isSelected() : true;

        if (hoaDonList == null || hoaDonList.isEmpty()) {
            showError("Không có dữ liệu", "Không có hóa đơn nào để thu");
            return;
        }

        // Tính tổng tiền cần thu
        int tongTien = 0;
        for (HoaDonDto hoaDon : hoaDonList) {
            if (hoaDon.getSoTien() != null) {
                tongTien += hoaDon.getSoTien();
            }
        }

        if (tongTien <= 0) {
            showInfo("Thông báo", "Không có khoản phí nào cần thu");
            return;
        }

        // Thu toàn bộ phí trực tiếp mà không cần xác nhận
        try {

            // Kiểm tra hoaDonService có sẵn sàng không
            if (hoaDonService == null) {
                showError("Lỗi hệ thống", "Service hóa đơn không khả dụng");
                return;
            }

            // Gọi service thực hiện thu phí
            io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto response =
                hoaDonService.thuToanBoPhiCanHo(currentCanHo.getMaCanHo(),
                    new java.util.ArrayList<>(hoaDonList));

            if (response.isSuccess()) {
                // Hiển thị thông báo thành công bằng ThongBaoController
                ThongBaoController.showSuccess("Thu phí thành công! 🎉", response.getMessage());


                // Reload data after payment để cập nhật trạng thái
                if (currentCanHo != null) {
                    loadData(currentCanHo.getMaCanHo(), true); // Force reload from service

                    // Refresh Home_list's invoice table
                    if (applicationContext != null) {
                        Home_list homeList = applicationContext.getBean(Home_list.class);
                        if (homeList != null) {
                            homeList.refreshHoaDonData();
                        }
                    }
                }
            } else {
                showError("Lỗi thu phí", response.getMessage());
            }

        } catch (Exception e) {
            System.err.println("❌ Error during payment: " + e.getMessage());
            e.printStackTrace();
            showError("Lỗi thu phí", "Có lỗi xảy ra khi thu phí: " + e.getMessage());
        }
    }


    @FXML
    private void handleXemLichSu() {
        // Kiểm tra quyền - Tổ trưởng, Tổ phó đều xem được
        try {
            String userRole = getCurrentUserRole();
            if (userRole == null || userRole.trim().isEmpty()) {
                showError("Không có quyền", "Không thể xác định vai trò người dùng.");
                return;
            }
            // Tất cả các vai trò đều có thể xem lịch sử (bỏ giới hạn quyền)
        } catch (Exception e) {
            System.err.println("ERROR: Cannot check user permission: " + e.getMessage());
        }

        if (currentCanHo == null) {
            showError("Lỗi", "Không có thông tin căn hộ");
            return;
        }

        showInfo("Lịch sử thu phí", "Chức năng xem lịch sử thu phí cho căn hộ " + currentCanHo.getMaCanHo());
    }

    /**
     * Xử lý xem lịch sử cho một hóa đơn cụ thể
     */
    private void handleXemLichSuHoaDon(HoaDonDto hoaDon) {
        try {
            // Kiểm tra quyền - Tổ trưởng, Tổ phó,  đều xem được
            String userRole = getCurrentUserRole();
            if (userRole == null || userRole.trim().isEmpty()) {
                showError("Không có quyền", "Không thể xác định vai trò người dùng.");
                return;
            }

            // Tất cả các vai trò đều có thể xem lịch sử (bỏ giới hạn quyền)

            if (hoaDon == null) {
                showError("Lỗi", "Không có thông tin hóa đơn");
                return;
            }

            // Tạo nội dung chi tiết hóa đơn
            StringBuilder lichSu = new StringBuilder();
            lichSu.append(" CHI TIẾT HÓA ĐƠN\n");
            lichSu.append("=".repeat(30)).append("\n");
            lichSu.append(" Mã hóa đơn: ").append(hoaDon.getMaHoaDon()).append("\n");
            lichSu.append(" Mã căn hộ: ").append(currentCanHo != null ? currentCanHo.getMaCanHo() : "N/A").append("\n");
            lichSu.append(" Tên khoản thu: ").append(hoaDon.getTenKhoanThu()).append("\n");
            lichSu.append(" Số tiền: ").append(String.format("%,d VNĐ", hoaDon.getSoTien())).append("\n");
            lichSu.append(" Ngày nộp: ").append(
                hoaDon.getNgayNop() != null ? hoaDon.getNgayNop().toString() : "Chưa nộp"
            ).append("\n");
            lichSu.append("✅ Trạng thái: ").append(
                hoaDon.isDaNop() ? "Đã thanh toán" : "Chưa thanh toán"
            ).append("\n\n");

            if (hoaDon.isDaNop()) {
                lichSu.append("🎉 Hóa đơn này đã được thanh toán thành công!");
            } else {
                lichSu.append("⏳ Hóa đơn này chưa được thanh toán.");
            }

            showInfo("Lịch sử hóa đơn", lichSu.toString());

        } catch (Exception e) {
            System.err.println("ERROR: Cannot show invoice history: " + e.getMessage());
            showError("Lỗi", "Không thể xem lịch sử hóa đơn: " + e.getMessage());
        }
    }

    @FXML
    private void handleClose() {
        try {
            // Remove this instance from tracking list
            openWindows.remove(this);

            javafx.stage.Stage stage = (javafx.stage.Stage) button_close_up.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            System.err.println("Lỗi khi đóng cửa sổ chi tiết căn hộ: " + e.getMessage());
        }
    }

    @FXML
    private void handleChinhSuaCanHo() {
        try {

            // Kiểm tra quyền
            String userRole = getCurrentUserRole();
            if ("Tổ trưởng".equals(userRole) || "Kế toán".equals(userRole)) {
                showError("Không có quyền", "Bạn không có quyền chỉnh sửa căn hộ. Chỉ được xem thông tin.");
                return;
            }

            if (currentCanHo == null) {
                showError("Lỗi", "Không có thông tin căn hộ để chỉnh sửa");
                return;
            }



            // Sử dụng form thêm căn hộ cho chỉnh sửa
            openEditFormUsingAddForm();


        } catch (Exception e) {
            System.err.println("ERROR: Exception in handleChinhSuaCanHo: " + e.getMessage());
            e.printStackTrace();
            showError("Lỗi mở chỉnh sửa", "Không thể mở form chỉnh sửa căn hộ: " + e.getMessage());
        }
    }

    /**
     * Mở form chỉnh sửa bằng cách sử dụng form thêm căn hộ
     */
    private void openEditFormUsingAddForm() {
        try {

            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/view/them_can_ho.fxml")
            );
            javafx.scene.Parent root = loader.load();

            // Get controller
            io.github.ktpm.bluemoonmanagement.controller.ThemCanHoButton controller = loader.getController();
            if (controller != null) {
                // Inject services
                if (canHoService != null) {
                    controller.setCanHoService(canHoService);
                }

                // Inject ApplicationContext
                if (applicationContext != null) {
                    controller.setApplicationContext(applicationContext);
                }

                // Convert current data to CanHoDto for edit mode
                CanHoDto canHoDto = convertToCanHoDto();

                // Setup edit mode
                controller.setupEditMode(canHoDto);

                // Setup form untuk chỉnh sửa
                setupEditForm(controller);
            }

            // Create and show stage
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED); // Bỏ khung cửa sổ hệ điều hành
            stage.setTitle("Chỉnh sửa căn hộ - " + currentCanHo.getMaCanHo());
            stage.setScene(new javafx.scene.Scene(root, 900, 650));
            stage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            stage.initOwner(button_close_up.getScene().getWindow());

            // Add event handler để refresh data khi form đóng
            stage.setOnHiding(event -> {
                // Refresh lại dữ liệu sau khi form đóng
                javafx.application.Platform.runLater(() -> {
                    try {
                        Thread.sleep(500); // Delay nhỏ để đảm bảo database được cập nhật
                        refreshData();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            });

            stage.show();


        } catch (Exception e) {
            System.err.println("ERROR: Cannot open edit form: " + e.getMessage());
            e.printStackTrace();
            showError("Lỗi", "Không thể mở form chỉnh sửa: " + e.getMessage());
        }
    }

    /**
     * Convert CanHoChiTietDto to CanHoDto
     */
    private CanHoDto convertToCanHoDto() {
        CanHoDto dto = new CanHoDto();
        dto.setMaCanHo(currentCanHo.getMaCanHo());
        dto.setToaNha(currentCanHo.getToaNha());
        dto.setTang(currentCanHo.getTang());
        dto.setSoNha(currentCanHo.getSoNha());
        dto.setDienTich(currentCanHo.getDienTich());
        dto.setDaBanChua(currentCanHo.isDaBanChua());
        dto.setTrangThaiKiThuat(currentCanHo.getTrangThaiKiThuat());
        dto.setTrangThaiSuDung(currentCanHo.getTrangThaiSuDung());
        dto.setChuHo(currentCanHo.getChuHo());
        return dto;
    }

    /**
     * Setup form để phù hợp với chỉnh sửa
     */
    private void setupEditForm(io.github.ktpm.bluemoonmanagement.controller.ThemCanHoButton controller) {
        try {
            // Access và disable các field không được chỉnh sửa bằng reflection
            disableReadOnlyFields(controller);

            // Thay đổi title và button text
            updateFormLabels(controller);


        } catch (Exception e) {
            System.err.println("ERROR: Cannot setup edit form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Disable các field không được chỉnh sửa
     */
    private void disableReadOnlyFields(io.github.ktpm.bluemoonmanagement.controller.ThemCanHoButton controller) {
        try {
            // Disable tòa nhà
            setFieldDisabled(controller, "textFieldToa", true);

            // Disable tầng
            setFieldDisabled(controller, "textFieldTang", true);

            // Disable số nhà
            setFieldDisabled(controller, "textFieldSoNha", true);


        } catch (Exception e) {
            System.err.println("ERROR: Cannot disable read-only fields: " + e.getMessage());
        }
    }

    /**
     * Set field disabled bằng reflection
     */
    private void setFieldDisabled(io.github.ktpm.bluemoonmanagement.controller.ThemCanHoButton controller,
                                  String fieldName, boolean disabled) {
        try {
            java.lang.reflect.Field field = controller.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object fieldObject = field.get(controller);

            if (fieldObject instanceof javafx.scene.control.TextField) {
                ((javafx.scene.control.TextField) fieldObject).setDisable(disabled);
            } else if (fieldObject instanceof javafx.scene.control.ComboBox) {
                ((javafx.scene.control.ComboBox<?>) fieldObject).setDisable(disabled);
            }

        } catch (Exception e) {
            System.err.println("WARNING: Cannot disable field " + fieldName + ": " + e.getMessage());
        }
    }

    /**
     * Update form labels cho chỉnh sửa
     */
    private void updateFormLabels(io.github.ktpm.bluemoonmanagement.controller.ThemCanHoButton controller) {
        try {
            // Change title
            setLabelText(controller, "labelTitle", "Chỉnh sửa căn hộ");

            // Change button text
            setButtonText(controller, "buttonTaoCanHo", "Cập nhật căn hộ");


        } catch (Exception e) {
            System.err.println("ERROR: Cannot update form labels: " + e.getMessage());
        }
    }

    /**
     * Set label text bằng reflection
     */
    private void setLabelText(io.github.ktpm.bluemoonmanagement.controller.ThemCanHoButton controller,
                              String fieldName, String text) {
        try {
            java.lang.reflect.Field field = controller.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object fieldObject = field.get(controller);

            if (fieldObject instanceof javafx.scene.control.Label) {
                ((javafx.scene.control.Label) fieldObject).setText(text);
            }

        } catch (Exception e) {
            System.err.println("WARNING: Cannot set label text for " + fieldName + ": " + e.getMessage());
        }
    }

    /**
     * Set button text bằng reflection
     */
    private void setButtonText(io.github.ktpm.bluemoonmanagement.controller.ThemCanHoButton controller,
                               String fieldName, String text) {
        try {
            java.lang.reflect.Field field = controller.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object fieldObject = field.get(controller);

            if (fieldObject instanceof javafx.scene.control.Button) {
                ((javafx.scene.control.Button) fieldObject).setText(text);
            }

        } catch (Exception e) {
            System.err.println("WARNING: Cannot set button text for " + fieldName + ": " + e.getMessage());
        }
    }

    /**
     * Thiết lập dữ liệu căn hộ từ bên ngoài
     */
    public void setCanHoData(CanHoChiTietDto canHoData) {
        try {

            if (canHoData != null) {
                // Load từ cache khi xem, chỉ từ service khi cần refresh
                loadData(canHoData.getMaCanHo(), false);
            } else {
                System.err.println("ERROR: CanHoData is null");
                showError("Lỗi dữ liệu", "Không có thông tin căn hộ để hiển thị");
                clearAllData();
            }
        } catch (Exception e) {
            System.err.println("ERROR: Exception in setCanHoData: " + e.getMessage());
            e.printStackTrace();
            showError("Lỗi thiết lập dữ liệu", "Không thể thiết lập dữ liệu căn hộ: " + e.getMessage());
        }
    }

    /**
     * Cập nhật tổng số tiền cần thu
     */
    private void updateTongSoTien() {
        if (hoaDonList != null && labelTongSoTien != null) {
            int tongTien = 0;
            for (HoaDonDto hoaDon : hoaDonList) {
                if (hoaDon.getSoTien() != null) {
                    tongTien += hoaDon.getSoTien();
                }
            }
            labelTongSoTien.setText(String.format("%,d VNĐ", tongTien));
        }
    }

    /**
     * Cập nhật số kết quả hiển thị
     */
    private void updateResultCount() {
        if (cuDanList != null && labelHienThiKetQuaCuDan != null) {
            labelHienThiKetQuaCuDan.setText("Hiển thị " + cuDanList.size() + "/" + cuDanList.size() + " kết quả");
        }
    }

    /**
     * Cập nhật số kết quả hiển thị với số đã lọc
     */
    private void updateResultCount(int filtered, int total) {
        if (labelHienThiKetQuaCuDan != null) {
            labelHienThiKetQuaCuDan.setText("Hiển thị " + filtered + "/" + total + " kết quả");
        }
    }

    // Utility methods for showing alerts
    private void showError(String title, String message) {
        ThongBaoController.showError(title, message);
    }

    private void showSuccess(String title, String message) {
        ThongBaoController.showSuccess(title, message);
    }

    private void showInfo(String title, String message) {
        ThongBaoController.showInfo(title, message);
    }

    // Setter for dependency injection
    public void setCanHoService(CanHoService canHoService) {
        this.canHoService = canHoService;
    }

    /**
     * Setter để inject ApplicationContext từ bên ngoài
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;

        // Sau khi có ApplicationContext, thử lấy các service nếu chưa có
        ensureServicesAvailable();
    }

    /**
     * Setter để inject PhuongTienService từ bên ngoài
     */
    public void setPhuongTienService(io.github.ktpm.bluemoonmanagement.service.phuongTien.PhuongTienService phuongTienService) {
        this.phuongTienService = phuongTienService;
    }

    /**
     * Đảm bảo các service luôn có sẵn
     */
    private void ensureServicesAvailable() {
        if (applicationContext == null) {
            System.err.println("ERROR: ApplicationContext is null, cannot get services");
            return;
        }

        try {
            if (canHoService == null) {
                canHoService = applicationContext.getBean(CanHoService.class);
            }

            if (phuongTienService == null) {
                phuongTienService = applicationContext.getBean(io.github.ktpm.bluemoonmanagement.service.phuongTien.PhuongTienService.class);
            }

            if (cacheDataService == null) {
                cacheDataService = applicationContext.getBean(CacheDataService.class);
            }

            // Thêm HoaDonService injection
            if (hoaDonService == null) {
                hoaDonService = applicationContext.getBean(io.github.ktpm.bluemoonmanagement.service.hoaDon.HoaDonService.class);
            }

        } catch (Exception e) {
            System.err.println("ERROR: Cannot get services from ApplicationContext: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Refresh dữ liệu từ database - gọi khi cần cập nhật sau khi thêm/sửa/xóa
     * QUAN TRỌNG: Không refresh nếu căn hộ đã bị xóa (currentCanHo = null)
     */
    public void refreshData() {
        if (currentCanHo == null) {
            return;
        }

        if (currentCanHo.getMaCanHo() != null) {
            String maCanHo = currentCanHo.getMaCanHo();
            // Clear cache for this apartment to ensure fresh data
            if (cacheDataService != null) {
                cacheDataService.refreshCacheData();
            }

            // Load fresh data from service (force refresh)
            loadData(maCanHo, true);



            // Force table refresh on JavaFX Application Thread
            javafx.application.Platform.runLater(() -> {
                try {

                    // Force refresh table views
                    if (tableViewCuDan != null && cuDanList != null) {
                        tableViewCuDan.refresh();
                        tableViewCuDan.getColumns().get(0).setVisible(false);
                        tableViewCuDan.getColumns().get(0).setVisible(true);
                    }

                    if (tableViewPhuongTien != null && phuongTienList != null) {
                        tableViewPhuongTien.refresh();
                    }

                    if (tableViewThuPhi != null && hoaDonList != null) {
                        tableViewThuPhi.refresh();
                    }

                } catch (Exception e) {
                    System.err.println("ERROR: Exception during table refresh: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Static method để refresh tất cả cửa sổ chi tiết đang mở cho một căn hộ
     * CẢNH BÁO: Không gọi method này cho căn hộ đã bị xóa!
     */
    public static void refreshAllWindowsForApartment(String maCanHo) {

        int refreshedCount = 0;
        for (ChiTietCanHoController controller : new ArrayList<>(openWindows)) {
            if (controller.currentCanHo != null &&
                maCanHo.equals(controller.currentCanHo.getMaCanHo())) {
                try {
                    controller.refreshData();
                    refreshedCount++;
                } catch (Exception e) {
                    System.err.println("ERROR: Failed to refresh window for apartment " + maCanHo + ": " + e.getMessage());
                    // Nếu refresh thất bại, có thể căn hộ đã bị xóa, đóng cửa sổ
                    controller.handleClose();
                }
            }
        }

    }

    /**
     * Static method để đóng tất cả cửa sổ chi tiết đang mở cho một căn hộ (khi căn hộ bị xóa)
     */
    public static void closeAllWindowsForApartment(String maCanHo) {


        // Tạo copy của list để tránh ConcurrentModificationException
        List<ChiTietCanHoController> controllersToClose = new ArrayList<>();

        for (ChiTietCanHoController controller : openWindows) {
            if (controller.currentCanHo != null &&
                maCanHo.equals(controller.currentCanHo.getMaCanHo())) {
                // QUAN TRỌNG: Vô hiệu hóa controller trước khi đóng để tránh refresh
                controller.currentCanHo = null; // Xóa reference để tránh refresh lỗi
                controllersToClose.add(controller);
            }
        }

        // Đóng các cửa sổ NGAY LẬP TỨC trên UI thread
        javafx.application.Platform.runLater(() -> {
            try {
                for (ChiTietCanHoController controller : controllersToClose) {
                    // Remove khỏi list trước để tránh reference
                    openWindows.remove(controller);

                    if (controller.button_close_up != null) {
                        javafx.stage.Stage stage = (javafx.stage.Stage) controller.button_close_up.getScene().getWindow();
                        if (stage != null) {
                            stage.close();
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("ERROR: Failed to close windows for apartment: " + maCanHo + " - " + e.getMessage());
            }
        });
  }
} 