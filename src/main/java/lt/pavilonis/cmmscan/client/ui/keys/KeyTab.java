package lt.pavilonis.cmmscan.client.ui.keys;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import lt.pavilonis.cmmscan.client.App;
import lt.pavilonis.cmmscan.client.WsRestClient;
import lt.pavilonis.cmmscan.client.representation.KeyRepresentation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;


@Component
public class KeyTab extends Tab {

   private final KeyTable keyTable = new KeyTable();
   private final WsRestClient wsClient;

   @Autowired
   public KeyTab(WsRestClient wsClient) {
      super("Keys Assigned");
      this.wsClient = wsClient;

      setClosable(false);

      FilterPanel filterPanel = new FilterPanel(searchString ->
            loadData(response -> {
               if (response.isPresent()) {
                  List<KeyRepresentation> keys = response.get();
                  if (StringUtils.isNoneBlank(searchString)) {
                     keys.removeIf(doesNotMatch(searchString));
                  }
                  keyTable.update(keys);
               } else {
                  App.displayWarning("Can not load keys!");
               }
            }));

      BorderPane.setMargin(filterPanel, new Insets(0, 0, 15, 0));

      setOnSelectionChanged(event -> {
         if (isSelected()) {
            loadData(keys -> {
               if (keys.isPresent()) {
                  keyTable.update(keys.get());
               } else {
                  App.displayWarning("Can not load keys!");
               }
            });
         } else {
            keyTable.clear();
         }
         BorderPane mainTabLayout = new BorderPane(keyTable, filterPanel, null, null, null);
         mainTabLayout.setPadding(new Insets(15));
         setContent(mainTabLayout);
      });
   }

   private Predicate<KeyRepresentation> doesNotMatch(String searchString) {
      return key -> {
         String content = key.user.firstName + key.user.lastName + key.keyNumber + key.dateTime;
         return !content.toLowerCase().contains(searchString.toLowerCase());
      };
   }

   private void loadData(Consumer<Optional<List<KeyRepresentation>>> responseConsumer) {
      new Service<Void>() {
         @Override
         protected Task<Void> createTask() {
            return new Task<Void>() {
               @Override
               protected Void call() throws Exception {
                  Optional<List<KeyRepresentation>> response = wsClient.allKeysAssigned();
                  responseConsumer.accept(response);
                  return null;
               }
            };
         }
      }.start();
   }
}
