package lt.pavilonis.keymanager.ui.scanlog;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import lt.pavilonis.keymanager.MessageSourceAdapter;
import lt.pavilonis.keymanager.NotificationDisplay;
import lt.pavilonis.keymanager.Spring;
import lt.pavilonis.keymanager.WebServiceClient;
import lt.pavilonis.keymanager.ui.keylog.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.synchronizedObservableList;

public class ScanLogKeyList extends ListView<ScanLogKeyListElement> implements NotificationDisplay {

   private static final Logger LOGGER = LoggerFactory.getLogger(ScanLogKeyList.class);
   private final ObservableList<ScanLogKeyListElement> container = synchronizedObservableList(observableArrayList());
   private final WebServiceClient wsClient = Spring.CONTEXT.getBean(WebServiceClient.class);
   private final MessageSourceAdapter messages = Spring.CONTEXT.getBean(MessageSourceAdapter.class);

   public ScanLogKeyList() {
      setItems(container);
      setFocusTraversable(false);
   }

   void append(Key representation) {
      container.add(composeCell(representation.getKeyNumber()));
   }

   private ScanLogKeyListElement composeCell(int keyNumber) {
      var cell = new ScanLogKeyListElement(keyNumber);
      cell.addRemoveKeyButtonListener(key -> wsClient.returnKey(
            key,
            responseKey -> container.remove(cell),
            e -> LOGGER.error("Could not remove key " + key, e)
      ));
      return cell;
   }

   void updateContainer(String cardCode) {
      Platform.runLater(container::clear);

      wsClient.userKeysAssigned(cardCode, response -> {
               LOGGER.info("Loaded user assigned keys [cardCode={}, keysNum={}]", cardCode, response.length);

               List<ScanLogKeyListElement> elements = Stream.of(response)
                     .map(Key::getKeyNumber)
                     .map(this::composeCell)
                     .collect(toList());

               container.addAll(elements);
            },
            exception -> warn(messages.get(this, "canNotLoadUserAssignedKeys"), exception)
      );
   }

   public void clear() {
      Platform.runLater(container::clear);
   }

   @Override
   public List<Node> getStackPaneChildren() {
      StackPane rootPane = (StackPane) getParent();
      return rootPane.getChildren();
   }
}
