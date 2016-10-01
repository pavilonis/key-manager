package lt.pavilonis.cmmscan.client.ui.keylog;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import lt.pavilonis.cmmscan.client.App;
import lt.pavilonis.cmmscan.client.WsRestClient;
import lt.pavilonis.cmmscan.client.representation.KeyAction;
import lt.pavilonis.cmmscan.client.representation.KeyRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;


@Controller
public class KeyLogTab extends Tab {

   private final KeyLogTable keyLogTable = new KeyLogTable();
   private final WsRestClient wsClient;

   @Autowired
   public KeyLogTab(WsRestClient wsClient) {
      super("Key Log");
      this.wsClient = wsClient;

      setClosable(false);

      KeyLogFilterPanel filter = new KeyLogFilterPanel();
      filter.addSearchListener(event -> updateTable(filter));

      BorderPane.setMargin(filter, new Insets(0, 0, 15, 0));

      setOnSelectionChanged(event -> {
         if (isSelected()) {
            filter.reset();
            updateTable(filter);
         } else {
            keyLogTable.clear();
         }
         BorderPane mainTabLayout = new BorderPane(keyLogTable, filter, null, null, null);
         mainTabLayout.setPadding(new Insets(15));
         setContent(mainTabLayout);
      });
   }

   private void updateTable(KeyLogFilterPanel filter) {
      loadData(filter.getPeriodStart(), filter.getPeriodEnd(), response -> {
         if (response.isPresent()) {
            List<KeyRepresentation> keys = response.get();
            keys.removeIf(noActionMatch(filter.getAction()));
            keys.removeIf(noTextMatch(filter.getText()));
            keyLogTable.update(keys);
         } else {
            App.displayWarning("Can not load keys!");
         }
      });
   }

   private Predicate<KeyRepresentation> noActionMatch(KeyAction filter) {
      return key -> filter != KeyAction.ALL && filter != key.keyAction;
   }

   private Predicate<KeyRepresentation> noTextMatch(String filter) {
      return key -> {
         String content = key.user.firstName + key.user.lastName + key.user.description + key.keyNumber + key.dateTime;
         return !content.toLowerCase().contains(filter.toLowerCase());
      };
   }

   private void loadData(LocalDate periodStart,
                         LocalDate periodEnd,
                         Consumer<Optional<List<KeyRepresentation>>> responseConsumer) {

      new Service<Void>() {
         @Override
         protected Task<Void> createTask() {
            return new Task<Void>() {
               @Override
               protected Void call() throws Exception {
                  Optional<List<KeyRepresentation>> response = wsClient.keyLog(periodStart, periodEnd);
                  responseConsumer.accept(response);
                  return null;
               }
            };
         }
      }.start();
   }
}
