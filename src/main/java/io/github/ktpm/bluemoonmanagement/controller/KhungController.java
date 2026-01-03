package io.github.ktpm.bluemoonmanagement.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.stage.Window;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.ktpm.bluemoonmanagement.service.canHo.CanHoService;
import io.github.ktpm.bluemoonmanagement.session.Session;
import io.github.ktpm.bluemoonmanagement.util.FxView;
import io.github.ktpm.bluemoonmanagement.util.FxViewLoader;
import io.github.ktpm.bluemoonmanagement.controller.ThongBaoController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
 
import javafx.application.Platform;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class KhungController implements Initializable{

    @FXML
    private Button buttonCanHo;

    @FXML
    private Button buttonCuDan;

    @FXML
    private Button buttonDangXuat;

    @FXML
    private Button buttonHoSo;

    @FXML
    private Button buttonKhoanThu;

    @FXML
    private Button buttonTaiKhoan;

    @FXML
    private Button buttonThuPhi;

    @FXML
    private Button buttonTrangChu;

    @FXML
    private Label labelAccountName;

    @FXML
    private Label labelScreenName;

    @FXML
    private BorderPane mainBorderPane;


    private Home_list centerController;
    private final AtomicBoolean centerLoaded = new AtomicBoolean(false);
    private final Queue<Runnable> pendingActions = new ConcurrentLinkedQueue<>();


    @Autowired
    private FxViewLoader fxViewLoader;

    @Autowired
    private CanHoService canHoService;

    @Autowired
    private org.springframework.context.ApplicationContext applicationContext;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        
        buttonTaiKhoan.setDisable(true);
        


        
        String name = Session.getCurrentUser().getHoTen();
        setAccountName(name);
        // Lazy-load center view to avoid blocking UI initialization.
        Label placeholder = new Label("Đang tải giao diện...");
        placeholder.setStyle("-fx-text-fill: white;");
        mainBorderPane.setCenter(placeholder);

       
        Thread loaderThread = new Thread(() -> {
            try {
                FxView<Home_list> fxView = fxViewLoader.loadFxView("/view/trang_chu_danh_sach.fxml");
                Platform.runLater(() -> {
                    try {
                        mainBorderPane.setCenter(fxView.getView());
                        this.centerController = fxView.getController(); // Gán đúng controller

                        // Inject services vào Home_list controller
                        if (this.centerController != null) {
                            this.centerController.injectServices(canHoService);
                            this.centerController.setParentController(this);
                            // Inject ApplicationContext nếu có
                            try {
                                this.centerController.setApplicationContext(applicationContext);
                            } catch (Exception e) {
                                System.err.println("Could not inject ApplicationContext: " + e.getMessage());
                            }
                        }

                        updateScreenLabel("Trang chủ");
                        centerLoaded.set(true);
                        // execute any queued actions
                        Runnable act;
                        while ((act = pendingActions.poll()) != null) {
                            try {
                                Platform.runLater(act);
                            } catch (Exception ex) {
                                System.err.println("Error executing queued action: " + ex.getMessage());
                                ex.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error setting center view: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                System.err.println("Không thể load trang chủ (background): " + e.getMessage());
                e.printStackTrace();
            }
        }, "KhungCenterLoader");
        loaderThread.setDaemon(true);
        loaderThread.start();

        if (Session.hasRole("Tổ trưởng")) {
            buttonTaiKhoan.setDisable(false);
        }

    }

    public void updateScreenLabel(String screenName) {
        if (labelScreenName != null) {
            labelScreenName.setText(screenName);
        }
    }

    public void setAccountName(String accountName) {
        if (labelAccountName != null) {
            labelAccountName.setText("Xin chào,"+accountName);
        }
    }

    private void runOrQueue(Runnable action) {
        if (centerLoaded.get() && centerController != null) {
            Platform.runLater(action);
        } else {
            pendingActions.add(action);
            ThongBaoController.showInfo("Đang tải", "Giao diện đang tải, vui lòng chờ...");
        }
    }

    @FXML
    void goToCanHo(ActionEvent event) {
        runOrQueue(() -> {
            try {
                centerController.goToCanHo(event);
            } catch (Exception ex) {
                System.err.println("Error running queued goToCanHo: " + ex.getMessage());
            }
        });
    }

    @FXML
    void goToCuDan(ActionEvent event) {
        runOrQueue(() -> {
            try {
                centerController.gotoCuDan(event);
            } catch (Exception ex) {
                System.err.println("Error running queued goToCuDan: " + ex.getMessage());
            }
        });
    }

    @FXML
    void goToHoSo(ActionEvent event) {
        runOrQueue(() -> {
            try {
                centerController.goToHoSo(event);
            } catch (Exception ex) {
                System.err.println("Error running queued goToHoSo: " + ex.getMessage());
            }
        });
    }

    @FXML
    void goToKhoanThu(ActionEvent event) {
        runOrQueue(() -> {
            try {
                centerController.gotoKhoanThu(event);
            } catch (Exception ex) {
                System.err.println("Error running queued goToKhoanThu: " + ex.getMessage());
            }
        });
    }

    @FXML
    void goToTaiKhoan(ActionEvent event) {
        runOrQueue(() -> {
            try {
                centerController.show("TaiKhoan");
                updateScreenLabel("Danh sách tài khoản");
            } catch (Exception ex) {
                System.err.println("Error running queued goToTaiKhoan: " + ex.getMessage());
            }
        });
    }

    @FXML
    void goToTrangChu(ActionEvent event) {
        runOrQueue(() -> {
            try {
                centerController.show("TrangChu");
                updateScreenLabel("Trang chủ");
            } catch (Exception ex) {
                System.err.println("Error running queued goToTrangChu: " + ex.getMessage());
            }
        });
    }

    @FXML
    void gotoLichSuThu(ActionEvent event) {
        runOrQueue(() -> {
            try {
                centerController.show("LichSuThu");
                updateScreenLabel("Hóa đơn");
                // Auto-refresh invoice data when switching to History tab
                centerController.refreshHoaDonData();
            } catch (Exception ex) {
                System.err.println("Error running queued gotoLichSuThu: " + ex.getMessage());
            }
        });
    }

    @FXML
    void handleDangXuat(ActionEvent event) {
        try {
            for (Window window : Window.getWindows()) {
                if (window instanceof Stage) {
                    Stage s = (Stage) window;
                    if ("ChatBot".equals(s.getTitle())) {
                        s.close();
                    }
                }
            }
            // Tải file FXML mới (khung.fxml)
            Parent mainView = fxViewLoader.loadView("/view/dang_nhap.fxml");
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(mainView));
            stage.setTitle("Trang chính");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Xử lý lỗi nếu không thể tải file FXML
        }
    }
  






}
