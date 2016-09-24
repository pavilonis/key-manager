package lt.pavilonis.cmmscan.client.ui.scanlog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import lt.pavilonis.cmmscan.client.WsRestClient;
import lt.pavilonis.cmmscan.client.representation.KeyRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class KeyListView extends ListView<KeyCell> {

   private final ObservableList<KeyCell> container =
         FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

   private final WsRestClient wsClient;


   @Autowired
   public KeyListView(WsRestClient wsClient) {
      this.wsClient = wsClient;
      setItems(container);
   }

   public void reload(List<KeyRepresentation> keys) {
      container.clear();
      List<KeyCell> cells = keys.stream()
            .map(key -> composeCell(key.user.cardCode, key.keyNumber))
            .collect(toList());

      container.addAll(cells);
   }

   public void append(KeyRepresentation representation) {
      container.add(composeCell(representation.user.cardCode, representation.keyNumber));
   }

   private KeyCell composeCell(String cardCode, int keyNumber) {
      KeyCell cell = new KeyCell(keyNumber);
      cell.addRemoveKeyButtonListener(key -> {
         boolean success = wsClient.returnKey(cardCode, key);
         if (success) {
            container.remove(cell);
         }
      });
      return cell;
   }
}
