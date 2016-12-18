package lt.pavilonis.scan.cmm.client.ui.keyassignment;

import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import lt.pavilonis.scan.cmm.client.App;
import lt.pavilonis.scan.cmm.client.representation.KeyRepresentation;
import lt.pavilonis.scan.cmm.client.service.MessageSourceAdapter;
import lt.pavilonis.scan.cmm.client.service.WsRestClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

import static com.google.common.collect.Lists.newArrayList;
import static org.slf4j.LoggerFactory.getLogger;

@Component
public class KeyAssignmentTab extends Tab {

   private static final Logger LOG = getLogger(KeyAssignmentTab.class.getSimpleName());
   private final WsRestClient wsClient;

   @Autowired
   public KeyAssignmentTab(WsRestClient wsClient,
                           KeyAssignmentTable keyAssignmentTable,
                           MessageSourceAdapter messages) {

      this.setText(messages.get(this, "title"));
      this.wsClient = wsClient;

      setClosable(false);

      KeyAssignmentFilterPanel stringFilterPanel = new KeyAssignmentFilterPanel(
            messages,
            searchString -> wsClient.allKeysAssigned(response -> {
               if (response.isPresent()) {
                  LOG.info("Loaded all assigned keys [number={}]", response.get().length);
                  List<KeyRepresentation> keys = newArrayList(response.get());
                  if (StringUtils.isNoneBlank(searchString)) {
                     keys.removeIf(doesNotMatch(searchString));
                  }
                  keyAssignmentTable.update(keys);
               } else {
                  App.displayWarning(messages.get(this, "canNotLoadKeys"));
               }
            })
      );

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

//   private void loadData(Consumer<Optional<List<KeyRepresentation>>> responseConsumer) {
//      new Service<Void>() {
//         @Override
//         protected Task<Void> createTask() {
//            return new Task<Void>() {
//               @Override
//               protected Void call() throws Exception {
//                  Optional<List<KeyRepresentation>> response = wsClient.allKeysAssigned();
//                  responseConsumer.accept(response);
//                  return null;
//               }
//            };
//         }
//      }.start();
//   }
}
