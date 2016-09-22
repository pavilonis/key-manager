package lt.pavilonis.cmmscan.client.ui.scanlog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import lt.pavilonis.cmmscan.client.representation.KeyRepresentation;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class KeyListView extends ListView<KeyCell> {

   private final ObservableList<KeyCell> container =
         FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

   public KeyListView() {
      setItems(container);
   }

   public void reload(List<KeyRepresentation> keys) {
      container.clear();
      List<KeyCell> cells = keys.stream().map(KeyCell::new).collect(toList());
      container.addAll(cells);
   }

   public void append(KeyRepresentation key) {
      container.add(new KeyCell(key));
   }
}
