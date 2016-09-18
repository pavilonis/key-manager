package lt.pavilonis.cmmscan.client;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App extends Application {

   static final Group root = new Group();
   static Stage stage;

   public static void main(String[] args) {
      launch(args);
   }

   @Override
   public void start(Stage primaryStage) throws Exception {
      new AnnotationConfigApplicationContext(AppConfig.class);

      TabPane rootPane = new TabPane(new Tab("AAA"), new Tab("BBB"));

      primaryStage.setScene(new Scene(rootPane));
      primaryStage.setMaximized(true);
      primaryStage.show();
      App.stage = primaryStage;
      composeLayout();
   }

   private void composeLayout() {

   }
}
