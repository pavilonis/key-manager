package lt.pavilonis.keymanager.ui.scanlog;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import lt.pavilonis.keymanager.MessageSourceAdapter;
import lt.pavilonis.keymanager.Spring;
import lt.pavilonis.keymanager.TimeUtils;
import lt.pavilonis.keymanager.WebServiceClient;
import lt.pavilonis.keymanager.ui.NotificationDisplay;
import lt.pavilonis.keymanager.ui.keylog.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.synchronizedObservableList;

public class ScanLogKeyList extends ListView<ScanLogKeyListElement> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ScanLogKeyList.class);
   private final ObservableList<ScanLogKeyListElement> container = synchronizedObservableList(observableArrayList());
   private final WebServiceClient webServiceClient = Spring.CONTEXT.getBean(WebServiceClient.class);
   private final MessageSourceAdapter messages = Spring.CONTEXT.getBean(MessageSourceAdapter.class);
   private final NotificationDisplay notifications;

   public ScanLogKeyList(NotificationDisplay notifications) {
      this.notifications = notifications;
      setItems(container);
      setFocusTraversable(false);
   }

   void append(Key representation) {
      container.add(composeCell(representation.getKeyNumber()));
   }

   private ScanLogKeyListElement composeCell(int keyNumber) {
      var cell = new ScanLogKeyListElement(keyNumber);
      cell.addRemoveKeyButtonListener(key -> webServiceClient.returnKey(
            key,
            responseKey -> container.remove(cell),
            exception -> LOGGER.error("Could not remove key " + key, exception)
      ));
      return cell;
   }

   void updateContainerFromWebService(String cardCode) {
      var start = now();
      Platform.runLater(notifications::clear);

      webServiceClient.userKeysAssigned(
            cardCode,
            response -> {
               addKeysToContainer(List.of(response));
               LOGGER.info("Loaded user assigned keys [cardCode={}, keysNum={}, t={}]",
                     cardCode, response.length, TimeUtils.duration(start));
            },
            exception -> notifications.warn(messages.get(this, "canNotLoadUserAssignedKeys"), exception)
      );
   }

   void updateContainerDirectly(List<Key> keys) {
      Platform.runLater(() -> addKeysToContainer(keys));
   }

   private void addKeysToContainer(List<Key> keys) {
      List<ScanLogKeyListElement> elements = keys.stream()
            .map(Key::getKeyNumber)
            .map(this::composeCell)
            .collect(toList());

      container.clear();
      container.addAll(elements);
   }

   public void clear() {
      Platform.runLater(container::clear);
   }
}
