package lt.pavilonis.cmmscan.client;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lt.pavilonis.cmmscan.client.ui.ScanLogTab;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

public class App extends Application {

   static Stage stage;

   public static void main(String[] args) {
      new AnnotationConfigApplicationContext(AppConfig.class);
      launch(args);
   }

   @Override
   public void start(Stage primaryStage) throws Exception {

      TabPane rootPane = new TabPane( new ScanLogTab());
      primaryStage.setScene(new Scene(rootPane));
      primaryStage.setMaximized(true);
      primaryStage.setMinHeight(700);
      primaryStage.setMinWidth(950);
      primaryStage.show();
      App.stage = primaryStage;
      composeLayout();
   }

   private void composeLayout() {

   }

   @Override
   public void stop() throws Exception {
      System.exit(0);
   }
}
