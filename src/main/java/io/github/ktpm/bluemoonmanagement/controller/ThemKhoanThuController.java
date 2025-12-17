package io.github.ktpm.bluemoonmanagement.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto;
import io.github.ktpm.bluemoonmanagement.model.dto.khoanThu.KhoanThuDto;
import io.github.ktpm.bluemoonmanagement.model.dto.phiGuiXe.PhiGuiXeDto;
import io.github.ktpm.bluemoonmanagement.model.dto.taiKhoan.ThongTinTaiKhoanDto;
import io.github.ktpm.bluemoonmanagement.service.khoanThu.KhoanThuService;
import io.github.ktpm.bluemoonmanagement.session.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

@Component
public class ThemKhoanThuController {

    @FXML
    private FontAwesomeIcon buttonClose;

    @FXML
    private Button buttonThemKhoanThu;

    @FXML
    private Button buttonChinhSua;

    @FXML
    private Button buttonLuu;

    @FXML
    private Button buttonTaoHoaDon;

    @FXML
    private Button button_close_up;

    @FXML
    private Button buttonThemFile;



    @FXML
    private ComboBox<String> comboBoxDonViTinh;

    @FXML
    private ComboBox<String> comboBoxLoaiKhoanThu;

    @FXML
    private ComboBox<String> comboBoxPhamVi;

    @FXML
    private ComboBox<String> comboBoxBoPhanQuanLy;

    @FXML
    private DatePicker datePickerHanNop;

    @FXML
    private HBox text;

    @FXML
    private HBox text11;

    @FXML
    private HBox phuongTienHbox;

    @FXML
    private HBox hBoxDonGia;

    @FXML
    private HBox hBoxDonGiaPhuongTien;

    @FXML
    private VBox vBoxTenCoQuan;

    @FXML
    private VBox vBoxDonViTinhVaDonGia;

    @FXML
    private javafx.scene.control.Label labelTieuDe;

    @FXML
    private Text textError;

    @FXML
    private TextField textFieldDonGia;

    @FXML
    private TextField textFieldGiaXeDap;

    @FXML
    private TextField textFieldGiaXeMay;

    @FXML
    private TextField textFieldGiaXeOTo;

    @FXML
    private TextField textFieldTenCoQuan;

    @FXML
    private TextField textFieldTenKhoanThu;


    @Autowired
    private KhoanThuService khoanThuService;
    
    @Autowired
    private org.springframework.context.ApplicationContext applicationContext;
    
    @Autowired
    private io.github.ktpm.bluemoonmanagement.service.hoaDon.HoaDonService hoaDonService;

    // Fields for edit mode
    private boolean isEditMode = false;
    private String originalMaKhoanThu;
    private KhoanThuDto currentKhoanThu;

    public void initialize() {
        // QUAN TRỌNG: Reset trạng thái form về chế độ thêm mới
        isEditMode = false;
        originalMaKhoanThu = null;
        currentKhoanThu = null;
        
        // Khởi tạo các ComboBox với dữ liệu mẫu
        comboBoxLoaiKhoanThu.getItems().addAll("Bắt buộc", "Tự nguyện");
        comboBoxLoaiKhoanThu.setValue("Bắt buộc"); // Mặc định chọn "Bắt buộc"
        
        comboBoxDonViTinh.getItems().addAll("Diện tích", "Căn hộ", "Phương tiện");
        
        comboBoxPhamVi.getItems().addAll(
            "Tất cả", 
            "Căn hộ đang sử dụng"
        );
        comboBoxPhamVi.setValue("Căn hộ đang sử dụng"); // Mặc định chọn phổ biến nhất
        
        comboBoxBoPhanQuanLy.getItems().addAll("Ban quản lý", "Bên thứ 3");
        comboBoxBoPhanQuanLy.setValue("Ban quản lý"); // Mặc định chọn "Ban quản lý"

        // Lắng nghe sự thay đổi của comboBoxDonViTinh để kiểm tra giá trị
        comboBoxDonViTinh.setOnAction(this::onDonViTinhChanged);

        // Lắng nghe sự thay đổi của comboBoxBoPhanQuanLy để hiển thị trường tên cơ quan
        comboBoxBoPhanQuanLy.setOnAction(this::onBoPhanQuanLyChanged);

        // Enable trường đơn giá ban đầu
        if (hBoxDonGia != null) {
            hBoxDonGia.setDisable(false);
        }
        if (textFieldDonGia != null) {
            textFieldDonGia.setDisable(false);
        }

        // Thiết lập format tiền cho các text field
        setupMoneyFormatting();

        // Gọi hàm xử lý ban đầu
        onDonViTinhChanged(null); // Kiểm tra ngay khi bắt đầu
        onBoPhanQuanLyChanged(null); // Kiểm tra bộ phận quản lý
        
        // Setup close button
        if (button_close_up != null) {
            button_close_up.setOnAction(this::handleClose);
        }
        
        // Ẩn nút "Tạo hóa đơn" ban đầu (chỉ hiện trong chế độ edit)
        if (buttonTaoHoaDon != null) {
            buttonTaoHoaDon.setVisible(false);
        }
        
        // Thiết lập nút cho chế độ ADD
        setupButtonsForAddMode();
    }
    
    /**
     * Thiết lập nút cho chế độ ADD (thêm mới)
     */
    private void setupButtonsForAddMode() {
        // Hiển thị nút "Thêm khoản thu"
        if (buttonThemKhoanThu != null) {
            buttonThemKhoanThu.setVisible(true);
        }
        
        // Ẩn các nút của chế độ EDIT
        if (buttonChinhSua != null) {
            buttonChinhSua.setVisible(false);
        }
        if (buttonLuu != null) {
            buttonLuu.setVisible(false);
        }
        if (buttonTaoHoaDon != null) {
            buttonTaoHoaDon.setVisible(false);
        }
        
        // Reset text error
        if (textError != null) {
            textError.setText("");
        }
        
        // Đặt tiêu đề form
        if (labelTieuDe != null) {
            labelTieuDe.setText("Thêm khoản thu mới");
        }
        
    }
    
    @FXML
    private void onDonViTinhChanged(ActionEvent event) {
        
        // Kiểm tra nếu "Phương tiện" được chọn
        if ("Phương tiện".equals(comboBoxDonViTinh.getValue())) {
            
            // Ẩn trường đơn giá chung
            if (hBoxDonGia != null) {
                hBoxDonGia.setVisible(false);
                hBoxDonGia.setDisable(true);
            }
            if (textFieldDonGia != null) {
                textFieldDonGia.setDisable(true);
                textFieldDonGia.clear();
            }
            
            // HIỂN THỊ và enable các ô giá xe
            if (hBoxDonGiaPhuongTien != null) {
                hBoxDonGiaPhuongTien.setVisible(true);
                hBoxDonGiaPhuongTien.setDisable(false);
            }
            
            // Enable và hiển thị tất cả ô giá xe 
            if (textFieldGiaXeDap != null) {
                textFieldGiaXeDap.setDisable(false);
            textFieldGiaXeDap.setVisible(true);
            }
            if (textFieldGiaXeMay != null) {
                textFieldGiaXeMay.setDisable(false);
            textFieldGiaXeMay.setVisible(true);
            }
            if (textFieldGiaXeOTo != null) {
                textFieldGiaXeOTo.setDisable(false);
            textFieldGiaXeOTo.setVisible(true);
            }
            
        } else {
            
            // Hiển thị và enable trường đơn giá chung
            if (hBoxDonGia != null) {
                hBoxDonGia.setVisible(true);
                hBoxDonGia.setDisable(false);
            }
            if (textFieldDonGia != null) {
                textFieldDonGia.setDisable(false);
            }
            
            // Ẩn và disable container phương tiện
            if (hBoxDonGiaPhuongTien != null) {
                hBoxDonGiaPhuongTien.setVisible(false);
                hBoxDonGiaPhuongTien.setDisable(true);
            }
            
            // DISABLE tất cả các ô giá xe và clear giá trị
            if (textFieldGiaXeDap != null) {
                textFieldGiaXeDap.setDisable(true);
                textFieldGiaXeDap.clear();
            textFieldGiaXeDap.setVisible(false);
            }
            if (textFieldGiaXeMay != null) {
                textFieldGiaXeMay.setDisable(true);
                textFieldGiaXeMay.clear();
            textFieldGiaXeMay.setVisible(false);
            }
            if (textFieldGiaXeOTo != null) {
                textFieldGiaXeOTo.setDisable(true);
                textFieldGiaXeOTo.clear();
            textFieldGiaXeOTo.setVisible(false);
            }
            
        }
    }

    @FXML
    private void onBoPhanQuanLyChanged(ActionEvent event) {
        
        // Kiểm tra nếu "Bên thứ 3" được chọn
        if ("Bên thứ 3".equals(comboBoxBoPhanQuanLy.getValue())) {
            
            // HIỂN THỊ vBoxTenCoQuan để có thể nhập excel hóa đơn
            if (vBoxTenCoQuan != null) {
                vBoxTenCoQuan.setVisible(true);
                vBoxTenCoQuan.setDisable(false);
            }
            
            // Ẩn và disable phần đơn vị tính và đơn giá (các nút của Ban quản lý)
            if (vBoxDonViTinhVaDonGia != null) {
                vBoxDonViTinhVaDonGia.setVisible(false);
                vBoxDonViTinhVaDonGia.setDisable(true);
            }
            
            // Clear và disable các combobox của Ban quản lý
            if (comboBoxDonViTinh != null) {
                comboBoxDonViTinh.setValue(null);
                comboBoxDonViTinh.setDisable(true);
            }
            
        } else {
            
            // ẨN vBoxTenCoQuan vì không cần nhập excel cho Ban quản lý
            if (vBoxTenCoQuan != null) {
                vBoxTenCoQuan.setVisible(false);
                vBoxTenCoQuan.setDisable(true);
            }
            
            // Hiển thị và enable lại phần đơn vị tính và đơn giá
            if (vBoxDonViTinhVaDonGia != null) {
                vBoxDonViTinhVaDonGia.setVisible(true);
                vBoxDonViTinhVaDonGia.setDisable(false);
            }
            
            // Enable lại combobox đơn vị tính
            if (comboBoxDonViTinh != null) {
                comboBoxDonViTinh.setDisable(false);
            }
            
            // Đảm bảo trường đơn giá được enable khi quay lại chế độ bình thường
            if (hBoxDonGia != null) {
                hBoxDonGia.setDisable(false);
            }
            if (textFieldDonGia != null) {
                textFieldDonGia.setDisable(false);
            }
            
            // Trigger lại logic đơn vị tính để hiển thị đúng trường đơn giá
            onDonViTinhChanged(null);
        }
        
        // Cập nhật visibility của các button invoice sau khi thay đổi bộ phận quản lý
        if (isEditMode && currentKhoanThu != null) {
            // Cập nhật thông tin ghiChu để reflect thay đổi bộ phận quản lý
            currentKhoanThu.setGhiChu(comboBoxBoPhanQuanLy.getValue().toString());
            
            // Cập nhật button visibility based on new selection
            updateInvoiceButtonsVisibility();
        }
    }
    @FXML
    void ThemKhoanThuClicked(ActionEvent event) {

        // Kiểm tra quyền trước khi thực hiện
        if (!hasPermission()) {
            String action = isEditMode ? "chỉnh sửa" : "thêm";
            textError.setText("Chỉ Kế toán mới có quyền " + action + " khoản thu.");
            textError.setStyle("-fx-fill: red;");
            return;
        }
        
        // Nếu ở chế độ edit, gọi method cập nhật
        if (isEditMode) {
            handleUpdateKhoanThu();
            return;
        }

        
        if (isAnyFieldEmpty()) {
            textError.setText("Vui lòng điền đầy đủ thông tin!");
            textError.setStyle("-fx-fill: red;");
            return; // Ngừng xử lý nếu có trường trống
        }
        // Lấy thông tin từ các trường nhập liệu
        String tenKhoanThu = textFieldTenKhoanThu.getText();  // Tên khoản thu
        String loaiKhoanThu = comboBoxLoaiKhoanThu.getValue().toString();  // Loại khoản thu
        String boPhanQuanLy = comboBoxBoPhanQuanLy.getValue().toString();  // Bộ phận quản lý
        String thoiHanNop = datePickerHanNop.getValue().toString();  // Thời hạn (ngày nộp)
        String phamVi = comboBoxPhamVi.getValue().toString();  // Phạm vi
        
        // Xử lý đơn vị tính và số tiền tùy theo bộ phận quản lý
        String donViTinh;
        int soTien;
        
        if ("Bên thứ 3".equals(boPhanQuanLy)) {
            // Nếu là bên thứ 3, có thể set giá trị mặc định hoặc để trống
            donViTinh = "Theo hóa đơn";
            soTien = 0; // hoặc giá trị mặc định khác
        } else {
            donViTinh = comboBoxDonViTinh.getValue().toString();  // Đơn vị tính
            
            // Nếu đơn vị tính là "Phương tiện", không cần đọc từ textFieldDonGia
            if ("Phương tiện".equals(donViTinh)) {
                soTien = 0; // Để 0 vì giá sẽ được lưu riêng trong bảng phi_gui_xe
            } else {
                soTien = Integer.parseInt(textFieldDonGia.getText());  // Số tiền từ ô đơn giá chung
            }
        }
        // Lấy số tiền từ các TextField
        String giaXeDap = textFieldGiaXeDap.getText();  // Giá xe đạp
        String giaXeMay = textFieldGiaXeMay.getText();  // Giá xe máy
        String giaXeOTo = textFieldGiaXeOTo.getText();  // Giá xe ô tô
        String donGia = textFieldDonGia.getText();  // Đơn giá

        // Lấy ngày tạo (hôm nay)
        String ngayTao = java.time.LocalDate.now().toString();  // Ngày tạo (hôm nay)

        KhoanThuDto khoanThuDto = new KhoanThuDto();
        khoanThuDto.setTenKhoanThu(tenKhoanThu);
        khoanThuDto.setBatBuoc("Bắt buộc".equals(loaiKhoanThu));  // Kiểm tra loại khoản thu
        khoanThuDto.setDonViTinh(donViTinh);
        khoanThuDto.setSoTien(soTien);
        khoanThuDto.setPhamVi(phamVi);
        khoanThuDto.setNgayTao(java.time.LocalDate.parse(ngayTao));
        khoanThuDto.setThoiHan(java.time.LocalDate.parse(thoiHanNop));
        // Chỉ sử dụng tên bộ phận quản lý (bỏ tên cơ quan)
        khoanThuDto.setGhiChu(boPhanQuanLy);

        // Nếu đơn vị tính là "Phương tiện", tạo PhiGuiXeDto từ các text field
        if ("Phương tiện".equals(donViTinh)) {
            ArrayList<PhiGuiXeDto> phiGuiXeList = new ArrayList<>();
            
            // Thêm xe đạp nếu có giá
            if (!giaXeDap.trim().isEmpty()) {
                int price = Integer.parseInt(giaXeDap.trim());
                phiGuiXeList.add(new PhiGuiXeDto("Xe đạp", price, null)); // maKhoanThu sẽ được set sau
            }
            
            // Thêm xe máy nếu có giá
            if (!giaXeMay.trim().isEmpty()) {
                int price = Integer.parseInt(giaXeMay.trim());
                phiGuiXeList.add(new PhiGuiXeDto("Xe máy", price, null)); // maKhoanThu sẽ được set sau
            }
            
            // Thêm xe ô tô nếu có giá
            if (!giaXeOTo.trim().isEmpty()) {
                int price = Integer.parseInt(giaXeOTo.trim());
                phiGuiXeList.add(new PhiGuiXeDto("Ô tô", price, null)); // maKhoanThu sẽ được set sau
            }
            
            khoanThuDto.setPhiGuiXeList(phiGuiXeList);
        } else {
            khoanThuDto.setPhiGuiXeList(new ArrayList<>());
        }

        // Gọi service để thêm khoản thu
        ResponseDto response = khoanThuService.addKhoanThu(khoanThuDto);
        if (response.isSuccess()) {
            textError.setText("Thêm khoản thu thành công!");
            textError.setStyle("-fx-fill: green;");

            // Refresh khoản thu table ngay lập tức
            refreshKhoanThuTable();
            
            // Close window after successful addition với delay ngắn hơn
            javafx.application.Platform.runLater(() -> {
                try {
                    Thread.sleep(1000); // Show success message for 1 second
                    handleClose(null);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    handleClose(null);
                }
            });

        } else {
            textError.setText("Lỗi: " + response.getMessage());
            textError.setStyle("-fx-fill: red;");
        }

    }
    
    /**
     * Xử lý cập nhật khoản thu khi ở chế độ edit
     */
    private void handleUpdateKhoanThu() {
        // Hiển thị dialog xác nhận có sẵn
        if (!showConfirmationDialog()) {
            textError.setText("Đã hủy chỉnh sửa khoản thu.");
            textError.setStyle("-fx-fill: orange;");
            return;
        }
        
        if (isAnyFieldEmpty()) {
            textError.setText("Vui lòng điền đầy đủ thông tin!");
            textError.setStyle("-fx-fill: red;");
            return;
        }
        
        try {
            // Tạo DTO với thông tin mới  
            KhoanThuDto updatedDto = createKhoanThuDto();
            updatedDto.setMaKhoanThu(originalMaKhoanThu); // Giữ nguyên mã khoản thu
            
            // Gọi service để cập nhật
            ResponseDto response = khoanThuService.updateKhoanThu(updatedDto);
            
            if (response.isSuccess()) {
                
                // Tự động refresh bảng và chuyển về tab khoản thu
                refreshKhoanThuTableAndGoToTab();
                
                // Refresh cache để đảm bảo dữ liệu phí gửi xe được cập nhật
                refreshCacheAndVehicleFees();
                
                // Đóng tất cả các tab detail đang mở
                closeAllOpenDetailTabs();
                
                // Đóng form hiện tại
                handleClose(null);
                
            } else {
                textError.setText("Lỗi: " + response.getMessage());
                textError.setStyle("-fx-fill: red;");
            }
            
        } catch (Exception e) {
            textError.setText("Lỗi khi cập nhật: " + e.getMessage());
            textError.setStyle("-fx-fill: red;");
        }
    }
    
    /**
     * Hiển thị dialog xác nhận có sẵn
     */
    private boolean showConfirmationDialog() {
        try {
            String content = "Bạn có chắc chắn muốn chỉnh sửa khoản thu này?\n" +
                           "Hành động này không thể hoàn tác.";
            
            // Sử dụng ThongBaoController với giao diện xac_nhan.fxml
            return ThongBaoController.showConfirmation("Xác nhận chỉnh sửa", content);
            
        } catch (Exception e) {
            System.err.println("Error showing confirmation dialog: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean isAnyFieldEmpty() {
        
        // Kiểm tra từng trường một cách chi tiết

        // Kiểm tra các trường bắt buộc cơ bản
        if (comboBoxLoaiKhoanThu.getValue() == null) {
            return true;
        }
        if (comboBoxPhamVi.getValue() == null) {
            return true;
        }
        if (comboBoxBoPhanQuanLy.getValue() == null) {
            return true;
        }

        // Kiểm tra tên khoản thu
        String tenKhoanThu = textFieldTenKhoanThu.getText();
        if (tenKhoanThu.isEmpty()) {
            return true;
        }

        // Kiểm tra DatePicker có giá trị không
        if (datePickerHanNop.getValue() == null) {
            return true;
        }

        // Nếu chọn "Bên thứ 3" thì không cần kiểm tra gì thêm (bỏ tên cơ quan)
        if ("Bên thứ 3".equals(comboBoxBoPhanQuanLy.getValue())) {
            return false;
        }

        // Nếu không phải "Bên thứ 3" thì kiểm tra đơn vị tính và đơn giá
        if (comboBoxDonViTinh.getValue() == null) {
            return true;
        }

        // Kiểm tra đơn giá tùy theo đơn vị tính
        if ("Phương tiện".equals(comboBoxDonViTinh.getValue())) {
            
            // Kiểm tra ít nhất 1 loại xe phải có giá
            String giaXeDap = textFieldGiaXeDap.getText().trim();
            String giaXeMay = textFieldGiaXeMay.getText().trim();
            String giaXeOTo = textFieldGiaXeOTo.getText().trim();
            
            
            if (giaXeDap.isEmpty() && giaXeMay.isEmpty() && giaXeOTo.isEmpty()) {
                return true;
            }
            
            // Kiểm tra format số cho các giá xe được nhập (sử dụng method format tiền)
            if (!giaXeDap.isEmpty()) {
                int value = getNumberFromFormattedMoney(giaXeDap);
                if (value <= 0) {
                    return true;
                }
            }
            if (!giaXeMay.isEmpty()) {
                int value = getNumberFromFormattedMoney(giaXeMay);
                if (value <= 0) {
                    return true;
                }
            }
            if (!giaXeOTo.isEmpty()) {
                int value = getNumberFromFormattedMoney(giaXeOTo);
                if (value <= 0) {
                    return true;
                }
            }
            
        } else {
            // Kiểm tra đơn giá chung
            if (textFieldDonGia.getText().isEmpty()) {
                return true;
            }
            
            // Kiểm tra format số cho đơn giá chung (sử dụng method format tiền)
            int value = getNumberFromFormattedMoney(textFieldDonGia.getText());
            if (value <= 0) {
                return true;
            }
        }

        return false; // Tất cả các trường đều có giá trị
    }
    
    @FXML
    private void handleClose(ActionEvent event) {
        try {
            javafx.stage.Stage stage = (javafx.stage.Stage) button_close_up.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            System.err.println("Lỗi khi đóng cửa sổ: " + e.getMessage());
        }
    }
    
    /**
     * Mở popup để nhập chi tiết giá từng loại xe
     */
    private void openVehicleDetailsPopup(String maKhoanThu, String tenKhoanThu) {
        try {
            
            // Tạo dialog popup
            javafx.scene.control.Dialog<java.util.List<PhiGuiXeDto>> dialog = new javafx.scene.control.Dialog<>();
            dialog.setTitle("Chi tiết phí gửi xe");
            dialog.setHeaderText("Nhập giá từng loại xe cho khoản thu: " + tenKhoanThu);
            
            // Tạo layout cho popup
            javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(10);
            content.setPadding(new javafx.geometry.Insets(20));
            
            // Label hướng dẫn
            javafx.scene.control.Label label = new javafx.scene.control.Label(
                "Vui lòng nhập giá cho các loại xe (có thể bỏ trống nếu không có):");
            label.setStyle("-fx-font-weight: bold;");
            
            // Tạo các TextField cho từng loại xe
            javafx.scene.layout.HBox xeDapBox = new javafx.scene.layout.HBox(10);
            javafx.scene.control.Label xeDapLabel = new javafx.scene.control.Label("Xe đạp:");
            xeDapLabel.setPrefWidth(80);
            javafx.scene.control.TextField xeDapField = new javafx.scene.control.TextField();
            xeDapField.setPromptText("Nhập giá xe đạp (VND)");
            xeDapBox.getChildren().addAll(xeDapLabel, xeDapField);
            
            javafx.scene.layout.HBox xeMayBox = new javafx.scene.layout.HBox(10);
            javafx.scene.control.Label xeMayLabel = new javafx.scene.control.Label("Xe máy:");
            xeMayLabel.setPrefWidth(80);
            javafx.scene.control.TextField xeMayField = new javafx.scene.control.TextField();
            xeMayField.setPromptText("Nhập giá xe máy (VND)");
            xeMayBox.getChildren().addAll(xeMayLabel, xeMayField);
            
            javafx.scene.layout.HBox xeOToBox = new javafx.scene.layout.HBox(10);
            javafx.scene.control.Label xeOToLabel = new javafx.scene.control.Label("Xe ô tô:");
            xeOToLabel.setPrefWidth(80);
            javafx.scene.control.TextField xeOToField = new javafx.scene.control.TextField();
            xeOToField.setPromptText("Nhập giá xe ô tô (VND)");
            xeOToBox.getChildren().addAll(xeOToLabel, xeOToField);
            
            content.getChildren().addAll(label, xeDapBox, xeMayBox, xeOToBox);
            dialog.getDialogPane().setContent(content);
            
            // Thêm buttons
            javafx.scene.control.ButtonType saveButtonType = new javafx.scene.control.ButtonType(
                "Lưu", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            javafx.scene.control.ButtonType cancelButtonType = javafx.scene.control.ButtonType.CANCEL;
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);
            
            // Xử lý kết quả
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    java.util.List<PhiGuiXeDto> phiGuiXeList = new java.util.ArrayList<>();
                    
                    // Thêm xe đạp nếu có giá
                    if (!xeDapField.getText().isEmpty()) {
                        try {
                            int price = Integer.parseInt(xeDapField.getText());
                            phiGuiXeList.add(new PhiGuiXeDto("Xe đạp", price, maKhoanThu));
                        } catch (NumberFormatException e) {
                            System.err.println("Giá xe đạp không hợp lệ: " + xeDapField.getText());
                        }
                    }
                    
                    // Thêm xe máy nếu có giá
                    if (!xeMayField.getText().isEmpty()) {
                        try {
                            int price = Integer.parseInt(xeMayField.getText());
                            phiGuiXeList.add(new PhiGuiXeDto("Xe máy", price, maKhoanThu));
                        } catch (NumberFormatException e) {
                            System.err.println("Giá xe máy không hợp lệ: " + xeMayField.getText());
                        }
                    }
                    
                    // Thêm xe ô tô nếu có giá
                    if (!xeOToField.getText().isEmpty()) {
                        try {
                            int price = Integer.parseInt(xeOToField.getText());
                            phiGuiXeList.add(new PhiGuiXeDto("Ô tô", price, maKhoanThu));
                        } catch (NumberFormatException e) {
                            System.err.println("Giá xe ô tô không hợp lệ: " + xeOToField.getText());
                        }
                    }
                    
                    return phiGuiXeList;
                }
                return null;
            });
            
            // Hiển thị dialog và xử lý kết quả
            java.util.Optional<java.util.List<PhiGuiXeDto>> result = dialog.showAndWait();
            result.ifPresent(phiGuiXeList -> {
                if (!phiGuiXeList.isEmpty()) {
                    // Ở đây bạn có thể gọi service để lưu danh sách phí xe
                    // Ví dụ: phiGuiXeService.savePhiGuiXeList(phiGuiXeList);
                    
                    for (PhiGuiXeDto dto : phiGuiXeList) {
                    }
                } else {
                }
                
                // Đóng form chính sau khi xử lý xong
                javafx.application.Platform.runLater(() -> {
                    try {
                        Thread.sleep(500);
                        handleClose(null);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        handleClose(null);
                    }
                });
            });
            
        } catch (Exception e) {
            System.err.println("Lỗi khi mở popup chi tiết xe: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Refresh khoản thu table in Home_list controller
     */
    private void refreshKhoanThuTable() {
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
                                findAndRefreshKhoanThuController(scene.getRoot());
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("ERROR: Failed to refresh fee data: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            System.err.println("ERROR: Exception in refreshKhoanThuTable: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Find and refresh Home_list controller for fees
     */
    private void findAndRefreshKhoanThuController(javafx.scene.Node node) {
        try {
            // Check if the node has a controller property
            Object controller = node.getProperties().get("controller");
            if (controller instanceof io.github.ktpm.bluemoonmanagement.controller.Home_list) {
                io.github.ktpm.bluemoonmanagement.controller.Home_list homeListController = (io.github.ktpm.bluemoonmanagement.controller.Home_list) controller;
                
                // Switch to fees tab
                java.lang.reflect.Method gotoKhoanThuMethod = homeListController.getClass().getDeclaredMethod("gotoKhoanThu", javafx.event.ActionEvent.class);
                gotoKhoanThuMethod.setAccessible(true);
                gotoKhoanThuMethod.invoke(homeListController, (javafx.event.ActionEvent) null);
                
                // Refresh all data including charts
                homeListController.refreshAllDataIncludingCharts();
                
                return;
            }
            
            // Recursively search in children if it's a Parent node
            if (node instanceof javafx.scene.Parent) {
                javafx.scene.Parent parent = (javafx.scene.Parent) node;
                for (javafx.scene.Node child : parent.getChildrenUnmodifiable()) {
                    findAndRefreshKhoanThuController(child);
                }
            }
        } catch (Exception e) {
            // Silently continue searching - this is expected for most nodes
        }
    }

    private boolean hasPermission() {
        try {
            ThongTinTaiKhoanDto currentUser = Session.getCurrentUser();
            if (currentUser == null) {
                return false;
            }
            String vaiTro = currentUser.getVaiTro();
            // Chỉ Kế toán mới được phép thêm/sửa khoản thu
            return "Kế toán".equals(vaiTro);
        } catch (Exception e) {
            System.err.println("Lỗi khi kiểm tra quyền: " + e.getMessage());
            return false;
        }
    }

    // Method to setup edit mode
    public void setupEditMode(io.github.ktpm.bluemoonmanagement.controller.Home_list.KhoanThuTableData khoanThuData) {
        isEditMode = true;
        originalMaKhoanThu = khoanThuData.getMaKhoanThu();
        
        // Load currentKhoanThu từ service để có đầy đủ thông tin
        if (khoanThuService != null) {
            try {
                List<KhoanThuDto> khoanThuList = khoanThuService.getAllKhoanThu();
                currentKhoanThu = khoanThuList.stream()
                    .filter(kt -> kt.getMaKhoanThu().equals(khoanThuData.getMaKhoanThu()))
                    .findFirst()
                    .orElse(null);
            } catch (Exception e) {
                System.err.println("Error loading KhoanThu data for edit mode: " + e.getMessage());
            }
        }
        
        // Chuyển đổi UI để hiển thị chế độ chỉnh sửa
        if (labelTieuDe != null) {
            labelTieuDe.setText("Chi tiết khoản thu");
        }
        
        // Thay đổi text button từ "Thêm khoản thu" thành "Chỉnh sửa"
        if (buttonThemKhoanThu != null) {
            buttonThemKhoanThu.setText("Chỉnh sửa");
        }
        
        // Ẩn các button khác 
        if (buttonChinhSua != null) {
            buttonChinhSua.setVisible(false);
        }
        if (buttonLuu != null) {
            buttonLuu.setVisible(false);
        }
        
        // Hiển thị/ẩn các button dựa trên trạng thái tạo hóa đơn và quyền người dùng
        updateInvoiceButtonsVisibility();
        
        // Điền dữ liệu vào form
        fillFormWithData(khoanThuData);
        
        // Enable tất cả fields để chỉnh sửa, NGOẠI TRỪ đơn vị tính
        setFieldsEditable(true);
        
        // LUÔN LUÔN disable đơn vị tính trong chế độ edit
        if (comboBoxDonViTinh != null) {
            comboBoxDonViTinh.setDisable(true);
        }
    }
    
    private void fillFormWithData(io.github.ktpm.bluemoonmanagement.controller.Home_list.KhoanThuTableData data) {
        if (textFieldTenKhoanThu != null) {
            textFieldTenKhoanThu.setText(data.getTenKhoanThu());
        }
        
        if (comboBoxLoaiKhoanThu != null) {
            comboBoxLoaiKhoanThu.setValue(data.getLoaiKhoanThu());
        }
        
        if (textFieldDonGia != null) {
            // Parse số tiền từ chuỗi có format và format lại
            String soTienText = data.getSoTien();
            if (soTienText != null && !soTienText.trim().isEmpty()) {
                // Loại bỏ định dạng cũ để lấy số
                String numbersOnly = soTienText.replaceAll("[^0-9]", "");
                if (!numbersOnly.isEmpty()) {
                    try {
                        int amount = Integer.parseInt(numbersOnly);
                                    textFieldDonGia.setText(String.valueOf(amount));
                    } catch (NumberFormatException e) {
                        textFieldDonGia.setText(soTienText); // Fallback to original
                    }
                } else {
                    textFieldDonGia.setText(soTienText);
                }
            }
        }
        
        if (comboBoxDonViTinh != null) {
            comboBoxDonViTinh.setValue(data.getDonViTinh());
        }
        
        if (comboBoxPhamVi != null) {
            comboBoxPhamVi.setValue(data.getPhamVi());
        }
        
        // Parse date if available
        if (datePickerHanNop != null && data.getThoiHan() != null && !data.getThoiHan().isEmpty()) {
            try {
                datePickerHanNop.setValue(java.time.LocalDate.parse(data.getThoiHan()));
            } catch (Exception e) {
                System.err.println("Lỗi parse ngày: " + e.getMessage());
            }
        }
        
        if (comboBoxBoPhanQuanLy != null) {
            // Nếu ghiChu không phải là "Ban quản lý" hoặc "Bên thứ 3", 
            // nghĩa là đây có thể là tên cơ quan cụ thể
            String ghiChu = data.getGhiChu();
            if ("Ban quản lý".equals(ghiChu) || "Bên thứ 3".equals(ghiChu)) {
                comboBoxBoPhanQuanLy.setValue(ghiChu);
            } else {
                // Đây là tên cơ quan, set bộ phận là "Bên thứ 3" và điền tên cơ quan
                comboBoxBoPhanQuanLy.setValue("Bên thứ 3");
                if (textFieldTenCoQuan != null) {
                    textFieldTenCoQuan.setText(ghiChu);
                }
            }
        }
        
        // Trigger sự kiện để hiển thị trường tên cơ quan nếu cần
        onBoPhanQuanLyChanged(null);
        
        // Nếu là loại phương tiện, load và hiển thị giá các loại xe
        if ("Phương tiện".equals(data.getDonViTinh()) && khoanThuService != null) {
            try {
                // Lấy chi tiết khoản thu từ service để có thông tin phí xe
                List<io.github.ktpm.bluemoonmanagement.model.dto.khoanThu.KhoanThuDto> khoanThuList = khoanThuService.getAllKhoanThu();
                io.github.ktpm.bluemoonmanagement.model.dto.khoanThu.KhoanThuDto khoanThuDto = khoanThuList.stream()
                    .filter(kt -> kt.getMaKhoanThu().equals(data.getMaKhoanThu()))
                    .findFirst()
                    .orElse(null);
                    
                if (khoanThuDto != null && khoanThuDto.getPhiGuiXeList() != null && !khoanThuDto.getPhiGuiXeList().isEmpty()) {
                    
                    // Clear existing values
                    if (textFieldGiaXeDap != null) textFieldGiaXeDap.clear();
                    if (textFieldGiaXeMay != null) textFieldGiaXeMay.clear();
                    if (textFieldGiaXeOTo != null) textFieldGiaXeOTo.clear();
                    
                    // Điền giá cho từng loại xe
                    for (io.github.ktpm.bluemoonmanagement.model.dto.phiGuiXe.PhiGuiXeDto phiXe : khoanThuDto.getPhiGuiXeList()) {
                        String loaiXe = phiXe.getLoaiXe();
                        String gia = String.valueOf(phiXe.getSoTien());
                        
                        
                        if ("Xe đạp".equals(loaiXe) && textFieldGiaXeDap != null) {
                            textFieldGiaXeDap.setText(String.valueOf(phiXe.getSoTien()));
                        } else if ("Xe máy".equals(loaiXe) && textFieldGiaXeMay != null) {
                            textFieldGiaXeMay.setText(String.valueOf(phiXe.getSoTien()));
                        } else if ("Ô tô".equals(loaiXe) && textFieldGiaXeOTo != null) {
                            textFieldGiaXeOTo.setText(String.valueOf(phiXe.getSoTien()));
                        }
                    }
                    
                    // Trigger sự kiện để hiển thị các field xe
                    onDonViTinhChanged(null);
                } else {
                }
            } catch (Exception e) {
                System.err.println("Error loading vehicle prices: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private void setFieldsEditable(boolean editable) {
        if (textFieldTenKhoanThu != null) {
            textFieldTenKhoanThu.setDisable(!editable);
        }
        if (comboBoxLoaiKhoanThu != null) {
            comboBoxLoaiKhoanThu.setDisable(!editable);
        }
        if (textFieldDonGia != null) {
            textFieldDonGia.setDisable(!editable);
        }
        if (hBoxDonGia != null) {
            hBoxDonGia.setDisable(!editable);
        }
        if (comboBoxDonViTinh != null) {
            comboBoxDonViTinh.setDisable(!editable);
        }
        if (comboBoxPhamVi != null) {
            comboBoxPhamVi.setDisable(!editable);
        }
        if (comboBoxBoPhanQuanLy != null) {
            comboBoxBoPhanQuanLy.setDisable(!editable);
        }
        if (datePickerHanNop != null) {
            datePickerHanNop.setDisable(!editable);
        }
        if (textFieldGiaXeDap != null) {
            textFieldGiaXeDap.setDisable(!editable);
        }
        if (textFieldGiaXeMay != null) {
            textFieldGiaXeMay.setDisable(!editable);
        }
        if (textFieldGiaXeOTo != null) {
            textFieldGiaXeOTo.setDisable(!editable);
        }
        if (textFieldTenCoQuan != null) {
            textFieldTenCoQuan.setDisable(!editable);
        }
    }
    
    @FXML
    private void handleChinhSua(ActionEvent event) {
        // Kiểm tra quyền
        if (!hasPermission()) {
            textError.setText("Bạn không có quyền chỉnh sửa khoản thu.");
            textError.setStyle("-fx-fill: red;");
            return;
        }
        
        // Enable tất cả fields để chỉnh sửa
        setFieldsEditable(true);
        
        // Ẩn button chỉnh sửa, hiện button lưu
        if (buttonChinhSua != null) {
            buttonChinhSua.setVisible(false);
        }
        if (buttonLuu != null) {
            buttonLuu.setVisible(true);
        }
        
        textError.setText("Đang trong chế độ chỉnh sửa. Vui lòng thay đổi thông tin và nhấn Lưu.");
        textError.setStyle("-fx-fill: blue;");
    }
    
    @FXML
    private void handleLuu(ActionEvent event) {
        // Kiểm tra quyền
        if (!hasPermission()) {
            textError.setText("Bạn không có quyền lưu thay đổi.");
            textError.setStyle("-fx-fill: red;");
            return;
        }
        
        if (isAnyFieldEmpty()) {
            textError.setText("Vui lòng điền đầy đủ thông tin!");
            textError.setStyle("-fx-fill: red;");
            return;
        }
        
        try {
            // Tạo DTO với thông tin mới
            KhoanThuDto updatedDto = createKhoanThuDto();
            updatedDto.setMaKhoanThu(originalMaKhoanThu); // Giữ nguyên mã khoản thu
            
            // Gọi service để cập nhật
            ResponseDto response = khoanThuService.updateKhoanThu(updatedDto);
            
            if (response.isSuccess()) {
                textError.setText("Cập nhật khoản thu thành công!");
                textError.setStyle("-fx-fill: green;");
                
                // Refresh table
                refreshKhoanThuTable();
                
                // Disable fields và chuyển về chế độ xem
                setFieldsEditable(false);
                if (buttonLuu != null) {
                    buttonLuu.setVisible(false);
                }
                if (buttonChinhSua != null) {
                    buttonChinhSua.setVisible(true);
                }
                
                // Close window after successful update với delay ngắn hơn
                javafx.application.Platform.runLater(() -> {
                    try {
                        Thread.sleep(1000); // Show success message for 1 second
                        handleClose(null);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        handleClose(null);
                    }
                });
                
            } else {
                textError.setText("Lỗi: " + response.getMessage());
                textError.setStyle("-fx-fill: red;");
            }
            
        } catch (Exception e) {
            textError.setText("Lỗi khi cập nhật: " + e.getMessage());
            textError.setStyle("-fx-fill: red;");
        }
    }
    
    private KhoanThuDto createKhoanThuDto() {
        String tenKhoanThu = textFieldTenKhoanThu.getText();
        String loaiKhoanThu = comboBoxLoaiKhoanThu.getValue().toString();
        String boPhanQuanLy = comboBoxBoPhanQuanLy.getValue().toString();
        String thoiHanNop = datePickerHanNop.getValue().toString();
        String phamVi = comboBoxPhamVi.getValue().toString();
        
        // Xử lý đơn vị tính và số tiền tùy theo bộ phận quản lý
        String donViTinh;
        int soTien;
        
        if ("Bên thứ 3".equals(boPhanQuanLy)) {
            // Nếu là bên thứ 3, có thể set giá trị mặc định hoặc để trống
            donViTinh = "Theo hóa đơn";
            soTien = 0; // hoặc giá trị mặc định khác
        } else {
            donViTinh = comboBoxDonViTinh.getValue().toString();
            if ("Phương tiện".equals(donViTinh)) {
                soTien = 0; // For vehicle fees, individual prices are in PhiGuiXeList
            } else {
                soTien = getNumberFromFormattedMoney(textFieldDonGia.getText());
            }
        }
        
        KhoanThuDto khoanThuDto = new KhoanThuDto();
        khoanThuDto.setTenKhoanThu(tenKhoanThu);
        khoanThuDto.setBatBuoc("Bắt buộc".equals(loaiKhoanThu));
        khoanThuDto.setDonViTinh(donViTinh);
        khoanThuDto.setSoTien(soTien);
        khoanThuDto.setPhamVi(phamVi);
        // Giữ nguyên ngày tạo gốc nếu đang ở chế độ edit
        if (isEditMode && currentKhoanThu != null && currentKhoanThu.getNgayTao() != null) {
            khoanThuDto.setNgayTao(currentKhoanThu.getNgayTao());
        } else {
            khoanThuDto.setNgayTao(java.time.LocalDate.now());
        }
        khoanThuDto.setThoiHan(java.time.LocalDate.parse(thoiHanNop));
        // Sử dụng tên cơ quan nếu có, nếu không thì dùng bộ phận quản lý
        String ghiChu = boPhanQuanLy;
        if ("Bên thứ 3".equals(boPhanQuanLy) && textFieldTenCoQuan != null && !textFieldTenCoQuan.getText().isEmpty()) {
            ghiChu = textFieldTenCoQuan.getText();
        }
        khoanThuDto.setGhiChu(ghiChu);
        
        // *** XỬ LÝ GIÁ XE CHO PHƯƠNG TIỆN ***
        if ("Phương tiện".equals(donViTinh)) {
            List<PhiGuiXeDto> phiGuiXeList = new ArrayList<>();
            
            // Thêm giá xe đạp nếu có
            if (textFieldGiaXeDap != null && !textFieldGiaXeDap.getText().trim().isEmpty()) {
                int giaXeDap = getNumberFromFormattedMoney(textFieldGiaXeDap.getText());
                if (giaXeDap > 0) {
                    PhiGuiXeDto phiXeDap = new PhiGuiXeDto();
                    phiXeDap.setLoaiXe("Xe đạp");
                    phiXeDap.setSoTien(giaXeDap);
                    phiGuiXeList.add(phiXeDap);
                }
            }
            
            // Thêm giá xe máy nếu có
            if (textFieldGiaXeMay != null && !textFieldGiaXeMay.getText().trim().isEmpty()) {
                int giaXeMay = getNumberFromFormattedMoney(textFieldGiaXeMay.getText());
                if (giaXeMay > 0) {
                    PhiGuiXeDto phiXeMay = new PhiGuiXeDto();
                    phiXeMay.setLoaiXe("Xe máy");
                    phiXeMay.setSoTien(giaXeMay);
                    phiGuiXeList.add(phiXeMay);
                }
            }
            
            // Thêm giá xe ô tô nếu có
            if (textFieldGiaXeOTo != null && !textFieldGiaXeOTo.getText().trim().isEmpty()) {
                int giaXeOTo = getNumberFromFormattedMoney(textFieldGiaXeOTo.getText());
                if (giaXeOTo > 0) {
                    PhiGuiXeDto phiXeOTo = new PhiGuiXeDto();
                    phiXeOTo.setLoaiXe("Ô tô");
                    phiXeOTo.setSoTien(giaXeOTo);
                    phiGuiXeList.add(phiXeOTo);
                }
            }
            
            khoanThuDto.setPhiGuiXeList(phiGuiXeList);
        } else {
            khoanThuDto.setPhiGuiXeList(new ArrayList<>());
        }
        
        return khoanThuDto;
    }
    

    /**
     * Refresh bảng khoản thu và chuyển về tab khoản thu
     */
    private void refreshKhoanThuTableAndGoToTab() {
        try {
            
            // Find all windows and look for Home_list controller
            for (javafx.stage.Window window : javafx.stage.Window.getWindows()) {
                if (window instanceof javafx.stage.Stage) {
                    javafx.stage.Stage stage = (javafx.stage.Stage) window;
                    javafx.scene.Scene scene = stage.getScene();
                    if (scene != null && scene.getRoot() != null) {
                        // Try to find the Home_list controller through scene graph
                        findAndRefreshKhoanThuControllerForceTab(scene.getRoot());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR: Failed to refresh fee data and switch tab: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Find and refresh Home_list controller for fees with forced tab switch
     */
    private void findAndRefreshKhoanThuControllerForceTab(javafx.scene.Node node) {
        try {
            // Check if the node has a controller property
            Object controller = node.getProperties().get("controller");
            if (controller instanceof io.github.ktpm.bluemoonmanagement.controller.Home_list) {
                io.github.ktpm.bluemoonmanagement.controller.Home_list homeListController = (io.github.ktpm.bluemoonmanagement.controller.Home_list) controller;
                
                // Force switch to fees tab first
                java.lang.reflect.Method gotoKhoanThuMethod = homeListController.getClass().getDeclaredMethod("gotoKhoanThu", javafx.event.ActionEvent.class);
                gotoKhoanThuMethod.setAccessible(true);
                gotoKhoanThuMethod.invoke(homeListController, (javafx.event.ActionEvent) null);
                
                // Then refresh all data including charts
                homeListController.refreshAllDataIncludingCharts();
                
                return;
            }
            
            // Recursively search in children if it's a Parent node
            if (node instanceof javafx.scene.Parent) {
                javafx.scene.Parent parent = (javafx.scene.Parent) node;
                for (javafx.scene.Node child : parent.getChildrenUnmodifiable()) {
                    findAndRefreshKhoanThuControllerForceTab(child);
                }
            }
        } catch (Exception e) {
            // Silently continue searching - this is expected for most nodes
        }
    }
    
    /**
     * Refresh cache và cập nhật dữ liệu phí gửi xe
     */
    private void refreshCacheAndVehicleFees() {
        try {
            
            javafx.application.Platform.runLater(() -> {
                try {
                    // Find all windows and refresh relevant components
                    for (javafx.stage.Window window : javafx.stage.Window.getWindows()) {
                        if (window instanceof javafx.stage.Stage) {
                            javafx.stage.Stage stage = (javafx.stage.Stage) window;
                            javafx.scene.Scene scene = stage.getScene();
                            if (scene != null && scene.getRoot() != null) {
                                // Try to find controllers and refresh cache
                                refreshCacheInScene(scene.getRoot());
                            }
                        }
                    }
                    
                } catch (Exception e) {
                    System.err.println("ERROR: Failed to refresh cache and vehicle fees: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            System.err.println("ERROR: Exception in refreshCacheAndVehicleFees: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Tìm và refresh cache trong scene graph
     */
    private void refreshCacheInScene(javafx.scene.Node node) {
        try {
            // Check if the node has a controller property
            Object controller = node.getProperties().get("controller");
            
            // Refresh cache for different controller types
            if (controller instanceof io.github.ktpm.bluemoonmanagement.controller.Home_list) {
                io.github.ktpm.bluemoonmanagement.controller.Home_list homeListController = (io.github.ktpm.bluemoonmanagement.controller.Home_list) controller;
                // Home_list controller có thể có cache service
                try {
                    java.lang.reflect.Field cacheField = homeListController.getClass().getDeclaredField("cacheDataService");
                    cacheField.setAccessible(true);
                    Object cacheService = cacheField.get(homeListController);
                    if (cacheService != null) {
                        java.lang.reflect.Method refreshMethod = cacheService.getClass().getMethod("refreshCacheData");
                        refreshMethod.invoke(cacheService);
                    }
                } catch (Exception e) {
                    // Silently continue - cache refresh is optional
                }
            }
            
            else if (controller instanceof io.github.ktpm.bluemoonmanagement.controller.ChiTietCanHoController) {
                io.github.ktpm.bluemoonmanagement.controller.ChiTietCanHoController detailController = (io.github.ktpm.bluemoonmanagement.controller.ChiTietCanHoController) controller;
                // Refresh data in apartment detail windows
                try {
                    java.lang.reflect.Method refreshMethod = detailController.getClass().getDeclaredMethod("refreshData");
                    refreshMethod.setAccessible(true);
                    refreshMethod.invoke(detailController);
                } catch (Exception e) {
                    // Silently continue - refresh is optional
                }
            }
            
            // Recursively search in children if it's a Parent node
            if (node instanceof javafx.scene.Parent) {
                javafx.scene.Parent parent = (javafx.scene.Parent) node;
                for (javafx.scene.Node child : parent.getChildrenUnmodifiable()) {
                    refreshCacheInScene(child);
                }
            }
        } catch (Exception e) {
            // Silently continue searching - this is expected for most nodes
        }
    }
    
    /**
     * Đóng tất cả các tab detail đang mở (form chỉnh sửa, chi tiết căn hộ, etc.)
     */
    private void closeAllOpenDetailTabs() {
        try {
            
            // Get all open windows
            java.util.List<javafx.stage.Window> openWindows = new java.util.ArrayList<>(javafx.stage.Window.getWindows());
            
            for (javafx.stage.Window window : openWindows) {
                if (window instanceof javafx.stage.Stage) {
                    javafx.stage.Stage stage = (javafx.stage.Stage) window;
                    
                    // Skip the main window - only close detail/form windows
                    if (isDetailOrFormWindow(stage)) {
                        javafx.application.Platform.runLater(() -> {
                            try {
                                stage.close();
                            } catch (Exception e) {
                                System.err.println("Error closing window: " + e.getMessage());
                            }
                        });
                    }
                }
            }
            
            
        } catch (Exception e) {
            System.err.println("ERROR: Failed to close detail tabs: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Kiểm tra xem window có phải là detail/form window không
     */
    private boolean isDetailOrFormWindow(javafx.stage.Stage stage) {
        try {
            String title = stage.getTitle();
            if (title == null) return false;
            
            // Đóng các window có title chứa các từ khóa này
            return title.contains("Chi tiết") || 
                   title.contains("Chỉnh sửa") || 
                   title.contains("Thêm") ||
                   title.contains("Xác nhận") ||
                   title.contains("Detail") ||
                   title.contains("Edit") ||
                   title.contains("Add") ||
                   title.toLowerCase().contains("form");
                   
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Thiết lập format tiền cho các text field
     */
    private void setupMoneyFormatting() {
        // Format cho đơn giá chung
        if (textFieldDonGia != null) {
            setupMoneyFormattingForField(textFieldDonGia);
        }
        
        // Format cho giá xe đạp
        if (textFieldGiaXeDap != null) {
            setupMoneyFormattingForField(textFieldGiaXeDap);
        }
        
        // Format cho giá xe máy
        if (textFieldGiaXeMay != null) {
            setupMoneyFormattingForField(textFieldGiaXeMay);
        }
        
        // Format cho giá xe ô tô
        if (textFieldGiaXeOTo != null) {
            setupMoneyFormattingForField(textFieldGiaXeOTo);
        }
        
    }
    
    /**
     * Thiết lập format tiền cho một text field cụ thể - chỉ cho phép số
     */
    private void setupMoneyFormattingForField(javafx.scene.control.TextField textField) {
        // Chỉ cho phép nhập số
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Chỉ giữ lại các ký tự số
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }
    
    /**
     * Format input khi đang nhập (real-time)
     */
    private String formatMoneyInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        
        // Loại bỏ tất cả ký tự không phải số
        String numbersOnly = input.replaceAll("[^0-9]", "");
        
        if (numbersOnly.isEmpty()) {
            return "";
        }
        
        try {
            long number = Long.parseLong(numbersOnly);
            if (number == 0) {
                return "";
            }
            
            // Format với dấu phẩy
            String formatted = String.format("%,d", number);
            return formatted + " VNĐ";
            
        } catch (NumberFormatException e) {
            return input; // Trả về giá trị gốc nếu không format được
        }
    }
    
    /**
     * Format hoàn chỉnh khi hoàn thành nhập
     */
    private String formatMoneyComplete(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        
        // Loại bỏ tất cả ký tự không phải số
        String numbersOnly = input.replaceAll("[^0-9]", "");
        
        if (numbersOnly.isEmpty()) {
            return "";
        }
        
        try {
            long number = Long.parseLong(numbersOnly);
            if (number == 0) {
                return "";
            }
            
            // Format với dấu phẩy và VNĐ
            return String.format("%,d VNĐ", number);
            
        } catch (NumberFormatException e) {
            return input; // Trả về giá trị gốc nếu không format được
        }
    }
    
    /**
     * Kiểm tra xem text đã được format chưa
     */
    private boolean isFormattedMoney(String text) {
        if (text == null) return false;
        
        // Kiểm tra có kết thúc bằng " VNĐ" và có dấu phẩy
        return text.endsWith(" VNĐ") && text.contains(",");
    }
    
    /**
     * Lấy giá trị số từ text (chỉ là số thuần)
     */
    private int getNumberFromFormattedMoney(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Xử lý sự kiện click nút "Tạo hóa đơn"
     */
    @FXML
    private void handleTaoHoaDon(ActionEvent event) {
        try {
            // Kiểm tra quyền (chỉ Kế toán mới được tạo hóa đơn)
            if (!hasHoaDonPermission()) {
                showErrorDialog("Lỗi quyền truy cập", 
                    "Bạn không có quyền tạo hóa đơn.\n" +
                    "Chỉ người dùng có vai trò 'Kế toán' mới được phép thực hiện thao tác này.");
                return;
            }

            // Kiểm tra xem có phải chế độ edit không
            if (!isEditMode || currentKhoanThu == null) {
                showErrorDialog("Lỗi", "Chỉ có thể tạo hóa đơn từ khoản thu đã tồn tại.");
                return;
            }

            // Tạo hóa đơn trực tiếp mà không cần xác nhận
            try {
                if (hoaDonService != null) {
                    
                    // Kiểm tra trạng thái tạo hóa đơn trước khi gọi service
                    if (currentKhoanThu.isTaoHoaDon()) {
                        showErrorDialog("❌ Không thể tạo hóa đơn", 
                            "Hóa đơn cho khoản thu '" + currentKhoanThu.getTenKhoanThu() + "' đã được tạo trước đó.\n\n" +
                            "💡 Lưu ý: Mỗi khoản thu chỉ có thể tạo hóa đơn một lần để tránh trùng lặp.\n\n" +
                            "🔍 Bạn có thể kiểm tra trạng thái 'Đã tạo' trong cột 'Trạng thái hóa đơn' \n" +
                            "của bảng khoản thu hoặc xem danh sách hóa đơn trong trang 'Lịch sử thu'.");
                        return;
                    }
                    
                    // Gọi service để tạo hóa đơn (chỉ cho khoản thu chưa tạo)
                    io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto response = 
                        hoaDonService.generateHoaDon(currentKhoanThu);
                    
                    if (response.isSuccess()) {
                        
                        // Cập nhật trạng thái local của khoản thu hiện tại
                        if (currentKhoanThu != null) {
                            currentKhoanThu.setTaoHoaDon(true);
                        }
                        
                        // Refresh dữ liệu trước khi hiển thị thông báo để đảm bảo cập nhật ngay lập tức
                        
                        // 1. Refresh invoice data in Home_list để cập nhật danh sách hóa đơn
                        refreshInvoiceDataInHomeList();
                        
                        // 2. Refresh fee data để cập nhật trạng thái "Đã tạo hóa đơn" trong bảng khoản thu
                        refreshKhoanThuTable();
                        
                        // 3. Refresh cache để đảm bảo dữ liệu được cập nhật toàn bộ hệ thống
                        refreshCacheAndVehicleFees();
                        
                        // 4. Cập nhật visibility của các button trong form hiện tại
                        updateButtonVisibilityAfterInvoiceCreation();
                        
                        // 5. Hiển thị thông báo thành công sau khi đã refresh
                        ThongBaoController.showSuccess("🎉 Tạo hóa đơn thành công!", 
                            " Đã tạo hóa đơn thành công cho khoản thu: " + currentKhoanThu.getTenKhoanThu() + "\n\n" +
                            " Trạng thái khoản thu đã được cập nhật thành 'Đã tạo'\n" +
                            " Kiểm tra tab 'Lịch sử thu' để xem hóa đơn mới được tạo");
                        
                        // 6. Đóng form hiện tại
                        javafx.stage.Stage currentStage = (javafx.stage.Stage) buttonTaoHoaDon.getScene().getWindow();
                        currentStage.close();
                        
                        // 6. Chuyển sang tab Lịch sử thu để hiển thị hóa đơn vừa tạo
                        javafx.application.Platform.runLater(() -> {
                            try {
                                refreshInvoiceDataAndGoToHistoryTab();
                            } catch (Exception e) {
                                System.err.println("Could not switch to History tab: " + e.getMessage());
                            }
                        });
                    } else {
                        // Tạo hóa đơn thất bại
                        String errorMessage = response.getMessage();
                        System.err.println("❌ Tạo hóa đơn thất bại: " + errorMessage);
                        
                        showErrorDialog("Không thể tạo hóa đơn", 
                            "Tạo hóa đơn thất bại:\n\n" + errorMessage + "\n\n" +
                            "Các lý do có thể:\n" +
                            "• Hóa đơn đã được tạo trước đó\n" +
                            "• Đây là khoản thu tự nguyện\n" +
                            "• Không đủ quyền hạn");
                    }
                } else {
                    showErrorDialog("Lỗi hệ thống", 
                        "Dịch vụ tạo hóa đơn không khả dụng.\nVui lòng liên hệ quản trị viên.");
                }
            } catch (Exception e) {
                System.err.println("❌ Exception khi tạo hóa đơn: " + e.getMessage());
                e.printStackTrace();
                showErrorDialog("Lỗi tạo hóa đơn", 
                    "Có lỗi xảy ra khi tạo hóa đơn:\n\n" + e.getMessage() + "\n\n" +
                    "Vui lòng kiểm tra console để biết chi tiết.");
            }

        } catch (Exception e) {
            System.err.println("Error in handleTaoHoaDon: " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("Lỗi", "Có lỗi xảy ra: " + e.getMessage());
        }
    }

    /**
     * Kiểm tra quyền tạo hóa đơn (chỉ Kế toán)
     */
    private boolean hasHoaDonPermission() {
        try {
            ThongTinTaiKhoanDto currentUser = Session.getCurrentUser();
            if (currentUser != null && currentUser.getVaiTro() != null) {
                return "Kế toán".equals(currentUser.getVaiTro());
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error checking invoice permission: " + e.getMessage());
            return false;
        }
    }



    /**
     * Hiển thị dialog thành công
     */
    private void showSuccessDialog(String title, String message) {
        ThongBaoController.showSuccess(title, message);
    }

    /**
     * Hiển thị dialog lỗi
     */
    private void showErrorDialog(String title, String message) {
        ThongBaoController.showError(title, message);
    }
    
    /**
     * Refresh invoice data in Home_list and go to History tab after creating invoices
     */
    private void refreshInvoiceDataAndGoToHistoryTab() {
        try {
            // Find the Home_list controller through the scene graph
            javafx.scene.Node currentNode = buttonTaoHoaDon; // Use any FXML component as starting point
            while (currentNode != null) {
                javafx.scene.Scene scene = currentNode.getScene();
                if (scene != null) {
                    javafx.scene.Node rootNode = scene.getRoot();
                    findAndRefreshHomeListInvoiceDataAndGoToHistoryTab(rootNode);
                    break;
                }
                if (currentNode.getParent() != null) {
                    currentNode = currentNode.getParent();
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Could not refresh invoice data and switch tab in Home_list: " + e.getMessage());
            // Not critical error, just log it
        }
    }
    
    /**
     * Refresh invoice data in Home_list after creating invoices
     */
    private void refreshInvoiceDataInHomeList() {
        try {
            // Find the Home_list controller through the scene graph
            javafx.scene.Node currentNode = buttonTaoHoaDon; // Use any FXML component as starting point
            while (currentNode != null) {
                javafx.scene.Scene scene = currentNode.getScene();
                if (scene != null) {
                    javafx.scene.Node rootNode = scene.getRoot();
                    findAndRefreshHomeListInvoiceData(rootNode);
                    break;
                }
                if (currentNode.getParent() != null) {
                    currentNode = currentNode.getParent();
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Could not refresh invoice data in Home_list: " + e.getMessage());
            // Not critical error, just log it
        }
    }
    
    /**
     * Recursively find Home_list controller and refresh invoice data then go to History tab
     */
    private void findAndRefreshHomeListInvoiceDataAndGoToHistoryTab(javafx.scene.Node node) {
        if (node == null) return;
        
        try {
            // Check if this node has a Home_list controller
            Object controller = null;
            if (node instanceof javafx.scene.Parent) {
                javafx.scene.Parent parent = (javafx.scene.Parent) node;
                // Look for the controller in userData or properties
                controller = parent.getProperties().get("controller");
                if (controller == null) {
                    controller = parent.getUserData();
                }
            }
            
            // If found Home_list controller, call refresh method and switch to History tab
            if (controller instanceof io.github.ktpm.bluemoonmanagement.controller.Home_list) {
                io.github.ktpm.bluemoonmanagement.controller.Home_list homeList = 
                    (io.github.ktpm.bluemoonmanagement.controller.Home_list) controller;
                    
                // Refresh invoice data
                homeList.refreshHoaDonData();
                
                // Switch to LichSuThu tab to show new invoices
                homeList.show("LichSuThu");
                return;
            }
            
            // Recursively search children
            if (node instanceof javafx.scene.Parent) {
                javafx.scene.Parent parent = (javafx.scene.Parent) node;
                for (javafx.scene.Node child : parent.getChildrenUnmodifiable()) {
                    findAndRefreshHomeListInvoiceDataAndGoToHistoryTab(child);
                }
            }
            
        } catch (Exception e) {
            // Silently continue searching if error occurs
        }
    }
    
    /**
     * Recursively find Home_list controller and refresh invoice data
     */
    private void findAndRefreshHomeListInvoiceData(javafx.scene.Node node) {
        if (node == null) return;
        
        try {
            // Check if this node has a Home_list controller
            Object controller = null;
            if (node instanceof javafx.scene.Parent) {
                javafx.scene.Parent parent = (javafx.scene.Parent) node;
                // Look for the controller in userData or properties
                controller = parent.getProperties().get("controller");
                if (controller == null) {
                    controller = parent.getUserData();
                }
            }
            
            // If found Home_list controller, call refresh method
            if (controller instanceof io.github.ktpm.bluemoonmanagement.controller.Home_list) {
                io.github.ktpm.bluemoonmanagement.controller.Home_list homeList = 
                    (io.github.ktpm.bluemoonmanagement.controller.Home_list) controller;
                homeList.refreshHoaDonData();
                return;
            }
            
            // Recursively search children
            if (node instanceof javafx.scene.Parent) {
                javafx.scene.Parent parent = (javafx.scene.Parent) node;
                for (javafx.scene.Node child : parent.getChildrenUnmodifiable()) {
                    findAndRefreshHomeListInvoiceData(child);
                }
            }
            
        } catch (Exception e) {
            // Silently continue searching if error occurs
        }
    }
    
    /**
     * Nhập Excel hóa đơn cho bên thứ 3
     */
    @FXML
    private void handleNhapExcelHoaDon(javafx.event.ActionEvent event) {
        try {
            if (hoaDonService != null) {
                // Chọn file Excel để import
                javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
                fileChooser.setTitle("Chọn file Excel hóa đơn");
                fileChooser.getExtensionFilters().add(
                    new javafx.stage.FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls")
                );
                
                javafx.stage.Stage stage = (javafx.stage.Stage) buttonThemFile.getScene().getWindow();
                java.io.File selectedFile = fileChooser.showOpenDialog(stage);
                
                if (selectedFile != null) {
                    try {
                        // Convert File thành MultipartFile
                        io.github.ktpm.bluemoonmanagement.util.FileMultipartUtil.FileMultipartFile multipartFile = 
                            new io.github.ktpm.bluemoonmanagement.util.FileMultipartUtil.FileMultipartFile(selectedFile);
                        
                        // Gọi service để import hóa đơn
                        io.github.ktpm.bluemoonmanagement.model.dto.ResponseDto response = 
                            hoaDonService.importFromExcel(multipartFile);
                        
                        if (response.isSuccess()) {
                            
                            // 1. Refresh dữ liệu hóa đơn trong Home_list để cập nhật danh sách hóa đơn mới
                            refreshInvoiceDataInHomeList();
                            
                            // 2. Refresh bảng khoản thu để cập nhật trạng thái "Đã tạo hóa đơn"
                            refreshKhoanThuTable();
                            
                            // 3. Refresh cache để đảm bảo dữ liệu được cập nhật toàn bộ hệ thống
                            refreshCacheAndVehicleFees();
                            
                            // 4. Cập nhật UI form hiện tại nếu đang ở chế độ edit và khoản thu hiện tại được cập nhật
                            if (isEditMode && currentKhoanThu != null) {
                                // Reload current KhoanThu để lấy trạng thái mới nhất
                                try {
                                    currentKhoanThu.setTaoHoaDon(true); // Cập nhật local state
                                    
                                    // Ẩn nút "Nhập excel hóa đơn" vì đã tạo hóa đơn
                                    if (buttonThemFile != null) {
                                        buttonThemFile.setVisible(false);
                                    }
                                    
                                    // Hiển thị nút "Tạo hóa đơn" nếu user có quyền (cho trường hợp ban quản lý)
                                    updateButtonVisibilityAfterInvoiceCreation();
                                    
                                } catch (Exception refreshEx) {
                                    System.err.println("Warning: Could not refresh current form state: " + refreshEx.getMessage());
                                }
                            }
                            
                            // 5. Hiển thị thông báo thành công
                            showSuccessDialog("🎉 Nhập Excel thành công!", 
                                "✅ Đã nhập hóa đơn từ " + selectedFile.getName() + " thành công!\n\n" + 
                                response.getMessage() + "\n\n" +
                                "🔄 Trạng thái khoản thu đã được cập nhật thành 'Đã tạo'\n" +
                                "💡 Kiểm tra tab 'Lịch sử thu' để xem hóa đơn mới được tạo");
                            
                        } else {
                            showErrorDialog("❌ Lỗi nhập Excel", "Lỗi: " + response.getMessage());
                        }
                    } catch (Exception ex) {
                        showErrorDialog("❌ Lỗi nhập Excel", "Chi tiết: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            } else {
                showErrorDialog("❌ Lỗi", "Service hóa đơn không khả dụng");
            }
        } catch (Exception e) {
            showErrorDialog("❌ Lỗi nhập Excel", "Chi tiết: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Cập nhật visibility của các button dựa trên trạng thái và quyền người dùng
     */
    private void updateInvoiceButtonsVisibility() {
        try {
            if (currentKhoanThu == null) {
                // Nếu không có khoản thu hiện tại, ẩn tất cả
                if (buttonTaoHoaDon != null) buttonTaoHoaDon.setVisible(false);
                if (buttonThemFile != null) buttonThemFile.setVisible(false);
                return;
            }
            
            boolean hasInvoiceCreated = currentKhoanThu.isTaoHoaDon();
            boolean hasAccountantPermission = hasHoaDonPermission(); // Chỉ Kế toán
            boolean isBenThuBa = "Bên thứ 3".equals(currentKhoanThu.getGhiChu());
            
            // Nút "Tạo hóa đơn" (cho Ban quản lý)
            if (buttonTaoHoaDon != null) {
                boolean shouldShowCreateInvoice = hasAccountantPermission && !hasInvoiceCreated && !isBenThuBa;
                buttonTaoHoaDon.setVisible(shouldShowCreateInvoice);
            }
            
            // Nút "Nhập excel hóa đơn" (cho Bên thứ 3)
            if (buttonThemFile != null) {
                boolean shouldShowImportExcel = hasAccountantPermission && !hasInvoiceCreated && isBenThuBa;
                buttonThemFile.setVisible(shouldShowImportExcel);
            }
            
        } catch (Exception e) {
            System.err.println("Warning: Could not update invoice buttons visibility: " + e.getMessage());
        }
    }
    
    /**
     * Cập nhật visibility của các button sau khi tạo hóa đơn thành công
     */
    private void updateButtonVisibilityAfterInvoiceCreation() {
        try {
            if (currentKhoanThu != null && currentKhoanThu.isTaoHoaDon()) {
                // Ẩn nút "Nhập excel hóa đơn" vì đã tạo
                if (buttonThemFile != null) {
                    buttonThemFile.setVisible(false);
                }
                
                // Ẩn nút "Tạo hóa đơn" vì đã tạo
                if (buttonTaoHoaDon != null) {
                    buttonTaoHoaDon.setVisible(false);
                }
                
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not update button visibility: " + e.getMessage());
        }
    }
}
