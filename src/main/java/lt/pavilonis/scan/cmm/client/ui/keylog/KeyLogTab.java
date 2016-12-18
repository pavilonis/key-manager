package lt.pavilonis.scan.cmm.client.ui.keylog;

import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import lt.pavilonis.scan.cmm.client.App;
import lt.pavilonis.scan.cmm.client.representation.KeyAction;
import lt.pavilonis.scan.cmm.client.representation.KeyRepresentation;
import lt.pavilonis.scan.cmm.client.service.MessageSourceAdapter;
import lt.pavilonis.scan.cmm.client.service.WsRestClient;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.google.common.collect.Lists.newArrayList;
import static org.slf4j.LoggerFactory.getLogger;

@Controller
public class KeyLogTab extends Tab {

   private static final Logger LOG = getLogger(KeyLogTab.class.getSimpleName());
   private final KeyLogTable keyLogTable;
   private final WsRestClient wsClient;
   private MessageSourceAdapter messages;

   @Autowired
   public KeyLogTab(WsRestClient wsClient, MessageSourceAdapter messages) {
      setText(messages.get(this, "title"));

      this.messages = messages;
      this.wsClient = wsClient;
      this.keyLogTable = new KeyLogTable(messages);

      setClosable(false);

      KeyLogFilterPanel filter = new KeyLogFilterPanel(messages);
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
      wsClient.keyLog(
            filter.getPeriodStart(),
            filter.getPeriodEnd(),
            response -> {
               if (response.isPresent()) {
                  LOG.info("Loaded keyLog [entries={}]", response.get().length);
                  List<KeyRepresentation> keys = newArrayList(response.get());
                  keys.removeIf(noActionMatch(filter.getAction()));
                  keys.removeIf(noTextMatch(filter.getText()));
                  keyLogTable.update(keys);
               } else {
                  App.displayWarning(messages.get(this, "canNotLoadKeys"));
               }
            }
      );
   }

   private Predicate<KeyRepresentation> noActionMatch(KeyAction filter) {
      return key -> filter != KeyAction.ALL && filter != key.keyAction;
   }

   private Predicate<KeyRepresentation> noTextMatch(String filter) {
      return key -> {
         String content = key.user.firstName + key.user.lastName + key.user.group + key.user.role + key.keyNumber + key.dateTime;
         return !content.toLowerCase().contains(filter.toLowerCase());
      };
   }

//   private void loadData(LocalDate periodStart,
//                         LocalDate periodEnd,
//                         Consumer<Optional<List<KeyRepresentation>>> responseConsumer) {
//
//      new Service<Void>() {
//         @Override
//         protected Task<Void> createTask() {
//            return new Task<Void>() {
//               @Override
//               protected Void call() throws Exception {
//
//                  return null;
//               }
//            };
//         }
//      }.start();
//   }
}
