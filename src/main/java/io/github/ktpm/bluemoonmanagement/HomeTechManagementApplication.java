package io.github.ktpm.bluemoonmanagement;

import io.github.ktpm.bluemoonmanagement.util.FxViewLoader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class HomeTechManagementApplication extends Application {

    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        long start = System.currentTimeMillis();
        try {
            context = new SpringApplicationBuilder(SpringBootApp.class) //
                    .web(WebApplicationType.NONE)
                    .properties("spring.main.lazy-initialization=true")
                    .run(getParameters().getRaw().toArray(new String[0]));
            long took = System.currentTimeMillis() - start;
            System.out.println("Spring context initialized in " + took + " ms");
        } catch (Exception e) {
            long took = System.currentTimeMillis() - start;
            System.err.println("Failed to initialize Spring context after " + took + " ms: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FxViewLoader viewLoader = context.getBean(FxViewLoader.class); // 👈 Lấy bean từ Spring
        Parent root = viewLoader.loadView("/view/dang_nhap.fxml"); // 👈 Load FXML qua FxViewLoader

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Home Tech Management");
        primaryStage.show();
    }

    @Override
    public void stop() {
        context.close();
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
