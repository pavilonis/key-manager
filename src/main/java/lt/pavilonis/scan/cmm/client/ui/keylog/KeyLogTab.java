package lt.pavilonis.scan.cmm.client.ui.keylog;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import lt.pavilonis.scan.cmm.client.App;
import lt.pavilonis.scan.cmm.client.representation.KeyAction;
import lt.pavilonis.scan.cmm.client.representation.KeyRepresentation;
import lt.pavilonis.scan.cmm.client.service.WsRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Controller
public class KeyLogTab extends Tab {

   private static final String CLASS_NAME = KeyLogTab.class.getSimpleName();
   private final KeyLogTable keyLogTable;
   private final WsRestClient wsClient;
   private MessageSource messageSource;

   @Autowired
   public KeyLogTab(WsRestClient wsClient, MessageSource messageSource) {
      setText(messageSource.getMessage(CLASS_NAME + ".title", null, null));

      this.messageSource = messageSource;
      this.wsClient = wsClient;
      this.keyLogTable = new KeyLogTable(messageSource);

      setClosable(false);

      KeyLogFilterPanel filter = new KeyLogFilterPanel(messageSource);
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
            App.displayWarning(messageSource.getMessage(CLASS_NAME + ".canNotLoadKeys", null, null));
         }
      });
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
