package lt.pavilonis.scan.cmm.client.ui.scanlog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import lt.pavilonis.scan.cmm.client.service.WsRestClient;
import lt.pavilonis.scan.cmm.client.representation.KeyRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class ScanLogKeyList extends ListView<ScanLogKeyListElement> {

   private final ObservableList<ScanLogKeyListElement> container =
         FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

   private final WsRestClient wsClient;

   @Autowired
   public ScanLogKeyList(WsRestClient wsClient) {
      this.wsClient = wsClient;
      setItems(container);
      setFocusTraversable(false);
   }

   void reload(List<KeyRepresentation> keys) {
      container.clear();
      List<ScanLogKeyListElement> cells = keys.stream()
            .map(key -> composeCell(key.user.cardCode, key.keyNumber))
            .collect(toList());

      container.addAll(cells);
   }

   void append(KeyRepresentation representation) {
      container.add(composeCell(representation.user.cardCode, representation.keyNumber));
   }

   private ScanLogKeyListElement composeCell(String cardCode, int keyNumber) {
      ScanLogKeyListElement cell = new ScanLogKeyListElement(keyNumber);
      cell.addRemoveKeyButtonListener(key -> {
         boolean success = wsClient.returnKey(cardCode, key);
         if (success) {
            container.remove(cell);
         }
      });
      return cell;
   }
}
