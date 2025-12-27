package io.github.ktpm.bluemoonmanagement;

import org.springframework.context.ApplicationContext;
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
            // load SpringBootApp class by name to avoid compile-time resolution issues in some environments
            Class<?> appClass = Class.forName("io.github.ktpm.bluemoonmanagement.SpringBootApp");
            context = new SpringApplicationBuilder(appClass) //
                    .web(WebApplicationType.NONE)
                    .properties("spring.main.lazy-initialization=true")
                    .run(getParameters().getRaw().toArray(new String[0]));
            long took = System.currentTimeMillis() - start;
            System.out.println("Spring context initialized in " + took + " ms");
        } catch (Exception e) {
            long took = System.currentTimeMillis() - start;
            System.err.println("Failed to initialize Spring context after " + took + " ms: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Use runtime reflective access to avoid compile-time type resolution problems
        Object viewLoaderBean = null;
        try {
            // try by bean name first (Spring registers component as 'fxViewLoader' by default)
            viewLoaderBean = context.getBean("fxViewLoader");
        } catch (Exception ignored) {
            // fallback to searching any bean of matching class name
            for (String name : context.getBeanDefinitionNames()) {
                Object b = context.getBean(name);
                if (b != null && b.getClass().getName().equals("io.github.ktpm.bluemoonmanagement.util.FxViewLoader")) {
                    viewLoaderBean = b;
                    break;
                }
            }
        }

        if (viewLoaderBean == null) {
            throw new IllegalStateException("FxViewLoader bean not found in Spring context");
        }

        // invoke loadView(String) reflectively
        Parent root;
        try {
            java.lang.reflect.Method m = viewLoaderBean.getClass().getMethod("loadView", String.class);
            root = (Parent) m.invoke(viewLoaderBean, "/view/dang_nhap.fxml");
        } catch (ClassCastException | NoSuchMethodException ex) {
            throw new IllegalStateException("Failed to invoke loadView on FxViewLoader bean", ex);
        }

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Home Tech Management");
        primaryStage.show();
    }

    @Override
    public void stop() {
        try {
            if (context != null) {
                context.close();
            }
        } catch (Exception e) {
            System.err.println("Error closing Spring context: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                Platform.exit();
            } catch (Exception e) {
                System.err.println("Error exiting JavaFX Platform: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
