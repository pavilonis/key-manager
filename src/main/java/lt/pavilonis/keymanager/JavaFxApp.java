package lt.pavilonis.keymanager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import lt.pavilonis.keymanager.ui.NotificationDisplay;
import lt.pavilonis.keymanager.ui.classusage.ClassroomUsageTabPupils;
import lt.pavilonis.keymanager.ui.classusage.ClassroomUsageTabTeachers;
import lt.pavilonis.keymanager.ui.keyassignment.KeyAssignmentTab;
import lt.pavilonis.keymanager.ui.keylog.KeyLogTab;
import lt.pavilonis.keymanager.ui.scanlog.PhotoView;
import lt.pavilonis.keymanager.ui.scanlog.ScanLogKeyList;
import lt.pavilonis.keymanager.ui.scanlog.ScanLogList;
import lt.pavilonis.keymanager.ui.scanlog.ScanLogTab;

@Slf4j
public final class JavaFxApp extends Application {

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
         stage.setScene(new Scene(stackPane, Color.WHITE));
         stage.setMaximized(true);
         stage.setMinHeight(700);
         stage.setMinWidth(1020);
         stage.show();

      } catch (Exception e) {
         log.error("Could not start JavaFx app", e);
      }
   }

   private ScanLogTab createScanLogTab(NotificationDisplay notifications) {
      var scanLogKeyList = new ScanLogKeyList(notifications);
      var photoView = new PhotoView();
      var scanLogList = new ScanLogList(scanLogKeyList, photoView, notifications);
      return new ScanLogTab(scanLogKeyList, scanLogList, photoView, notifications);
   }

   @Override
   public void stop() {
      System.exit(0);
   }
}
