package io.github.ktpm.bluemoonmanagement.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FxViewLoader {

    private final ApplicationContext context;

    @Autowired
    public FxViewLoader(ApplicationContext context) {
        this.context = context;
    }

    // Hàm cũ vẫn giữ nguyên nếu bạn chỉ cần load giao diện
    public Parent loadView(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(context::getBean);
        return loader.load();
    }

    // Hàm mới: trả về cả view và controller
    public <T> FxView<T> loadFxView(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(context::getBean);
        Parent view = loader.load();
        T controller = loader.getController();
        return new FxView<>(view, controller);
    }
}


