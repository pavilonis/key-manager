package lt.pavilonis.scan.cmm.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lt.pavilonis.scan.cmm.client.service.WsRestClient;
import lt.pavilonis.scan.cmm.client.ui.WarningBox;
import lt.pavilonis.scan.cmm.client.ui.keylog.KeyLogTab;
import lt.pavilonis.scan.cmm.client.ui.keyassignment.KeyAssignmentTab;
import lt.pavilonis.scan.cmm.client.ui.scanlog.ScanLogTab;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

public class App extends Application {

   private static final StackPane ROOT_PANE = new StackPane();
   private static final WarningBox WARNING_BOX = new WarningBox(ROOT_PANE);
   private static AbstractApplicationContext context;
   private static WsRestClient wsClient;

   public static void main(String[] args) {
      launch(args);
   }

   @Override
   public void start(Stage primaryStage) throws Exception {
      context = new AnnotationConfigApplicationContext(AppConfig.class);
      wsClient = context.getBean(WsRestClient.class);

      TabPane tabPane = new TabPane(
            context.getBean(ScanLogTab.class),
            context.getBean(KeyAssignmentTab.class),
            context.getBean(KeyLogTab.class)
      );

      ROOT_PANE.getChildren().add(tabPane);

      primaryStage.setScene(new Scene(ROOT_PANE));
      primaryStage.setMaximized(true);
      primaryStage.setMinHeight(700);
      primaryStage.setMinWidth(1020);
      primaryStage.show();
   }

   @Override
   public void stop() throws Exception {
      System.exit(0);
   }

   public static void displayWarning(String text) {
      if (wsClient.getLastErrorMessage().isPresent()) {
         text += "\n" + wsClient.getLastErrorMessage().get();
      }
      WARNING_BOX.warning(text);
   }

   public static void clearWarning() {
      if (ROOT_PANE.getChildren().contains(WARNING_BOX)) {
         WARNING_BOX.hide();
      }
   }
}
