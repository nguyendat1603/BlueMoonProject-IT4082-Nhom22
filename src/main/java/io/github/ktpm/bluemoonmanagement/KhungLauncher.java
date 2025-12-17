package io.github.ktpm.bluemoonmanagement;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Launcher để mở khung chính của ứng dụng
 * Khung này sẽ load trang_chu_danh_sach vào center area
 */
public class KhungLauncher extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load file khung.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dang_nhap.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 1300, 757); // Set kích thước cố định 1280x720
            primaryStage.setScene(scene);
            primaryStage.setTitle("Blue Moon- Hệ Thống Quản Lý Chung Cư");
            
            // Lock kích thước cửa sổ
            primaryStage.setResizable(false); // Không cho phép resize
            primaryStage.setWidth(1300);
            primaryStage.setHeight(757);
            primaryStage.centerOnScreen(); // Căn giữa màn hình
            
            primaryStage.show();
            
            System.out.println("Khung chính đã được khởi động thành công!");
            System.out.println("Kích thước cửa sổ: 1280x720 (đã lock)");
            System.out.println("Trang chủ danh sách sẽ được load tự động vào center area.");
            
        } catch (Exception e) {
            System.err.println("Lỗi khi khởi động khung chính: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Phương thức chính để chạy ứng dụng
     */
    public static void main(String[] args) {
        System.out.println("Đang khởi động khung chính HomeTech...");
        launch(args);
    }
} 