package lt.pavilonis.cmmscan.client.ui.keys;

import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import lt.pavilonis.cmmscan.client.App;
import lt.pavilonis.cmmscan.client.WsRestClient;
import lt.pavilonis.cmmscan.client.representation.KeyRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
public class KeyTab extends Tab {

   @Autowired
   private WsRestClient wsClient;

   public KeyTab() {
      super("Keys");
      setClosable(false);

      KeyTable table = new KeyTable();
      FilterPanel filterPanel = new FilterPanel();

      BorderPane.setMargin(filterPanel, new Insets(0, 0, 15, 0));

      setOnSelectionChanged(event -> {
         if (isSelected()) {
            Optional<List<KeyRepresentation>> keys = wsClient.allKeysAssigned();
            if (keys.isPresent()) {
               table.update(keys.get());
            } else {
               App.displayWarning("Can not load keys!");
            }
         }
      });

      BorderPane mainTabLayout = new BorderPane(table, filterPanel, null, null, null);
      mainTabLayout.setPadding(new Insets(15));
      setContent(mainTabLayout);
   }
}
