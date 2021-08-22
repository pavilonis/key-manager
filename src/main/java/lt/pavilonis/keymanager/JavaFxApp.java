package lt.pavilonis.keymanager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lt.pavilonis.keymanager.ui.Footer;
import lt.pavilonis.keymanager.ui.NotificationDisplay;
import lt.pavilonis.keymanager.ui.classusage.ClassroomUsageTabPupils;
import lt.pavilonis.keymanager.ui.classusage.ClassroomUsageTabTeachers;
import lt.pavilonis.keymanager.ui.keyassignment.KeyAssignmentTab;
import lt.pavilonis.keymanager.ui.keylog.KeyLogTab;
import lt.pavilonis.keymanager.ui.scanlog.PhotoView;
import lt.pavilonis.keymanager.ui.scanlog.ScanLogKeyList;
import lt.pavilonis.keymanager.ui.scanlog.ScanLogList;
import lt.pavilonis.keymanager.ui.scanlog.ScanLogTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JavaFxApp extends Application {

   private static final Logger LOGGER = LoggerFactory.getLogger(Spring.class);

   @Override
   public void start(Stage stage) {
      try {
         var stackPane = new StackPane();
         var notifications = new NotificationDisplay(stackPane);

         ScanLogTab scanLogTab = createScanLogTab(notifications);
         new SerialPortListener(Spring.getStringProperty("scanner.port.name"), scanLogTab);

         var tabPane = new TabPane(
               scanLogTab,
               new KeyAssignmentTab(notifications),
               new KeyLogTab(notifications),
               new ClassroomUsageTabTeachers(notifications),
               new ClassroomUsageTabPupils(notifications)
         );

         stackPane.getChildren().add(tabPane);
         stage.setScene(createScene(stackPane));
         stage.setMaximized(true);
         stage.setMinHeight(700);
         stage.setMinWidth(1020);
         stage.show();

      } catch (Exception e) {
         LOGGER.error("Could not start JavaFx app", e);
      }
   }

   private ScanLogTab createScanLogTab(NotificationDisplay notifications) {
      var scanLogKeyList = new ScanLogKeyList(notifications);
      var photoView = new PhotoView();
      var scanLogList = new ScanLogList(scanLogKeyList, photoView, notifications);
      return new ScanLogTab(scanLogKeyList, scanLogList, photoView, notifications);
   }

   private Scene createScene(StackPane rootPane) {
      var scene = new Scene(rootPane, Color.WHITE);
      scene.setOnKeyPressed(event -> {
         if (event.getCode() == KeyCode.ESCAPE) {
            stop();
         }
      });
      return scene;
   }

   @Override
   public void stop() {
      System.exit(0);
   }
}
