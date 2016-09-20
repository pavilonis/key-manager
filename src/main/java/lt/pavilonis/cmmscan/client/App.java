package lt.pavilonis.cmmscan.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lt.pavilonis.cmmscan.client.ui.ScanLogTab;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

public class App extends Application {

   public static final StackPane rootPane = new StackPane();

   public static void main(String[] args) {
      launch(args);
   }

   @Override
   public void start(Stage primaryStage) throws Exception {
      AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

      TabPane tabPane = new TabPane(context.getBean(ScanLogTab.class));
      rootPane.getChildren().add(tabPane);

      primaryStage.setScene(new Scene(rootPane));
      primaryStage.setMaximized(true);
      primaryStage.setMinHeight(700);
      primaryStage.setMinWidth(1010);
      primaryStage.show();
   }

   @Override
   public void stop() throws Exception {
      System.exit(0);
   }
}
