package lt.pavilonis.scan.cmm.client.ui.keyassignment;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import lt.pavilonis.scan.cmm.client.App;
import lt.pavilonis.scan.cmm.client.service.WsRestClient;
import lt.pavilonis.scan.cmm.client.representation.KeyRepresentation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;


@Component
public class KeyAssignmentTab extends Tab {

   private final WsRestClient wsClient;

   @Autowired
   public KeyAssignmentTab(WsRestClient wsClient, KeyAssignmentTable keyAssignmentTable) {
      super("Keys Assigned");
      this.wsClient = wsClient;

      setClosable(false);

      StringFilterPanel stringFilterPanel = new StringFilterPanel(searchString ->
            loadData(response -> {
               if (response.isPresent()) {
                  List<KeyRepresentation> keys = response.get();
                  if (StringUtils.isNoneBlank(searchString)) {
                     keys.removeIf(doesNotMatch(searchString));
                  }
                  keyAssignmentTable.update(keys);
               } else {
                  App.displayWarning("Can not load keys!");
               }
            }));

      BorderPane.setMargin(stringFilterPanel, new Insets(0, 0, 15, 0));

      setOnSelectionChanged(event -> {
         if (isSelected()) {
            stringFilterPanel.reset();
            stringFilterPanel.action();
         } else {
            keyAssignmentTable.clear();
         }
         BorderPane mainTabLayout = new BorderPane(keyAssignmentTable, stringFilterPanel, null, null, null);
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
