package lt.pavilonis.keymanager.ui.scanlog;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import lt.pavilonis.keymanager.MessageSourceAdapter;
import lt.pavilonis.keymanager.Spring;
import lt.pavilonis.keymanager.User;
import lt.pavilonis.keymanager.WebServiceClient;
import lt.pavilonis.keymanager.ui.NotificationDisplay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;

public class ScanLogList extends ListView<ScanLogListElement> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ScanLogList.class);
   private static final int POSITION_FIRST = 0;
   private static final int QUEUE_LENGTH = 99;
   private final WebServiceClient webServiceClient = Spring.getBean(WebServiceClient.class);
   private final MessageSourceAdapter messages = Spring.getBean(MessageSourceAdapter.class);
   private final ObservableList<ScanLogListElement> container = FXCollections.observableArrayList();
   private final ScanLogKeyList scanLogKeyList;
   private final PhotoView photoView;
   private final NotificationDisplay notifications;
   private ScanLogListElement lastSelection;

   public ScanLogList(ScanLogKeyList scanLogKeyList, PhotoView photoView, NotificationDisplay notifications) {
      this.scanLogKeyList = scanLogKeyList;
      this.photoView = photoView;
      this.notifications = notifications;

      setItems(container);
      setFocusTraversable(false);
      setOnMouseClicked(click -> Platform.runLater(this::processClick));
   }

   private void processClick() {
      ScanLogListElement newSelection = getSelectionModel().getSelectedItem();
      if (newSelection == null) {
         return;
      }
      if (lastSelection == newSelection) {
         lastSelection.deactivate();
         lastSelection = null;
         getSelectionModel().clearSelection();
         scanLogKeyList.clear();
         return;
      }

      if (lastSelection != null) {
         lastSelection.deactivate();
      }
      lastSelection = newSelection;

      newSelection.activate();
      User user = newSelection.getUser();

      photoView.update(user.getBase64photo());
      scanLogKeyList.updateContainerFromWebService(user.getCardCode());
   }

   public void addElement(ScanLog scan) {
      LOGGER.info("Adding scanLog [user={}]", scan.getUser().getName());
      Platform.runLater(() -> {
         if (container.size() > QUEUE_LENGTH) {
            container.remove(container.size() - 1);
         }

         var element = new ScanLogListElement(scan, elementClickConsumer());
         container.add(POSITION_FIRST, element);

         if (lastSelection == null) {
            getSelectionModel().select(element);
            scanLogKeyList.updateContainerDirectly(scan.getKeys());
            photoView.update(element.getUser().getBase64photo());
         }
      });
   }

   private BiConsumer<String, Integer> elementClickConsumer() {
      return (cardCode, keyNumber) -> {
         Platform.runLater(notifications::clear);
         webServiceClient.assignKey(
               cardCode,
               keyNumber,
               key -> {
                  scanLogKeyList.append(key);
                  LOGGER.info("Key {} assigned to cardCode {}", key.getKeyNumber(), key.getUser().getCardCode());
               },
               exception -> notifications.warn(messages.get(this, "canNotAssignKey"), exception)
         );
      };
   }
}
