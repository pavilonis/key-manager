package lt.pavilonis.scan.cmm.client.ui.scanlog;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import lt.pavilonis.scan.cmm.client.App;
import lt.pavilonis.scan.cmm.client.MessageSourceAdapter;
import lt.pavilonis.scan.cmm.client.WsRestClient;
import lt.pavilonis.scan.cmm.client.ui.keylog.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.synchronizedObservableList;

@Component
public class ScanLogKeyList extends ListView<ScanLogKeyListElement> {

   private static final Logger LOG = LoggerFactory.getLogger(ScanLogKeyList.class);
   private final ObservableList<ScanLogKeyListElement> container = synchronizedObservableList(observableArrayList());
   private final WsRestClient wsClient;
   private final MessageSourceAdapter messages;

   public ScanLogKeyList(WsRestClient wsClient, MessageSourceAdapter messages) {
      setItems(container);
      setFocusTraversable(false);
      this.wsClient = wsClient;
      this.messages = messages;
   }

   void append(Key representation) {
      container.add(composeCell(representation.getKeyNumber()));
   }

   private ScanLogKeyListElement composeCell(int keyNumber) {
      ScanLogKeyListElement cell = new ScanLogKeyListElement(keyNumber);
      cell.addRemoveKeyButtonListener(key ->
            wsClient.returnKey(key, response -> {
               if (response.isPresent()) {
                  container.remove(cell);
               }
            }));
      return cell;
   }

   void updateContainer(String cardCode) {
      Platform.runLater(container::clear);

      wsClient.userKeysAssigned(cardCode, optionalResponse -> optionalResponse.ifPresentOrElse(
            response -> {
               List<Key> keys = List.of(response);
               LOG.info("Loaded user assigned keys [cardCode={}, keysNum={}]", cardCode, keys.size());

               List<ScanLogKeyListElement> elements = keys.stream()
                     .map(Key::getKeyNumber)
                     .map(this::composeCell)
                     .collect(toList());

               container.addAll(elements);
            },
            () -> App.displayWarning(messages.get(this, "canNotLoadUserAssignedKeys"))
      ));
   }

   public void clear() {
      Platform.runLater(container::clear);
   }
}
