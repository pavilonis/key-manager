package lt.pavilonis.keymanager.ui.scanlog;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import lt.pavilonis.keymanager.MessageSourceAdapter;
import lt.pavilonis.keymanager.NotificationDisplay;
import lt.pavilonis.keymanager.Spring;
import lt.pavilonis.keymanager.User;
import lt.pavilonis.keymanager.WebServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.BiConsumer;

public class ScanLogList extends ListView<ScanLogListElement> implements NotificationDisplay {

   private static final Logger LOGGER = LoggerFactory.getLogger(ScanLogList.class);
   private static final int POSITION_FIRST = 0;
   private static final int QUEUE_LENGTH = 99;
   private final WebServiceClient webServiceClient = Spring.CONTEXT.getBean(WebServiceClient.class);
   private final MessageSourceAdapter messages = Spring.CONTEXT.getBean(MessageSourceAdapter.class);
   private final ObservableList<ScanLogListElement> container = FXCollections.observableArrayList();
   private final ScanLogKeyList scanLogKeyList;
   private final PhotoView photoView;
   private ScanLogListElement lastSelection;

   public ScanLogList(ScanLogKeyList scanLogKeyList, PhotoView photoView) {
      this.scanLogKeyList = scanLogKeyList;
      this.photoView = photoView;

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
      scanLogKeyList.updateContainer(user.getCardCode());
   }

   public void addElement(ScanLog representation) {
      LOGGER.info("Adding scanLog [user={}]", representation.user.getName());
      Platform.runLater(() -> {
         if (container.size() > QUEUE_LENGTH) {
            container.remove(container.size() - 1);
         }

         var element = new ScanLogListElement(representation, elementClickConsumer());
         container.add(POSITION_FIRST, element);

         if (lastSelection == null) {
            getSelectionModel().select(element);
            scanLogKeyList.updateContainer(element.getUser().getCardCode());
            photoView.update(element.getUser().getBase64photo());
         }
      });
   }

   private BiConsumer<String, Integer> elementClickConsumer() {
      return (cardCode, keyNumber) -> webServiceClient.assignKey(
            cardCode,
            keyNumber,
            key -> {
               scanLogKeyList.append(key);
               LOGGER.info("Key {} assigned to cardCode {}", key.getKeyNumber(), key.getUser().getCardCode());
            },
            exception -> warn(messages.get(this, "canNotAssignKey"), exception)
      );
   }

   @Override
   public List<Node> getStackPaneChildren() {
      StackPane rootPane = (StackPane) getParent();
      return rootPane.getChildren();
   }
}
