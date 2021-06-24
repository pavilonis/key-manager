package lt.pavilonis.scan.cmm.client.ui.scanlog;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import lt.pavilonis.scan.cmm.client.App;
import lt.pavilonis.scan.cmm.client.MessageSourceAdapter;
import lt.pavilonis.scan.cmm.client.WsRestClient;
import lt.pavilonis.scan.cmm.client.ui.keylog.Key;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

@Component
public class ScanLogKeyList extends ListView<ScanLogKeyListElement> {

   private static final Logger LOG = getLogger(ScanLogList.class.getSimpleName());

   private final ObservableList<ScanLogKeyListElement> container =
         FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

   @Autowired
   private WsRestClient wsClient;

   @Autowired
   private MessageSourceAdapter messages;

   public ScanLogKeyList() {
      setItems(container);
      setFocusTraversable(false);
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
