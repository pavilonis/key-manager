package lt.pavilonis.scan.cmm.client.ui.keyassignment;

import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import lt.pavilonis.TimeUtils;
import lt.pavilonis.scan.cmm.client.App;
import lt.pavilonis.scan.cmm.client.representation.KeyRepresentation;
import lt.pavilonis.scan.cmm.client.service.MessageSourceAdapter;
import lt.pavilonis.scan.cmm.client.service.WsRestClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

import static com.google.common.collect.Lists.newArrayList;
import static org.slf4j.LoggerFactory.getLogger;

@Component
public class KeyAssignmentTab extends Tab {

   private static final Logger LOG = getLogger(KeyAssignmentTab.class.getSimpleName());
   private final MessageSourceAdapter messages;
   private final WsRestClient wsClient;
   private final KeyAssignmentTable keyAssignmentTable;

   @Autowired
   public KeyAssignmentTab(MessageSourceAdapter messages,
                           WsRestClient wsClient, KeyAssignmentTable keyAssignmentTable) {

      this.messages = messages;
      this.wsClient = wsClient;
      this.keyAssignmentTable = keyAssignmentTable;

      setText(messages.get(this, "title"));
      setClosable(false);

      KeyAssignmentFilterPanel stringFilterPanel =
            new KeyAssignmentFilterPanel(messages, this::searchAction);

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

   private void searchAction(String searchString) {
      LocalDateTime opStart = LocalDateTime.now();
      wsClient.allActiveKeys(response -> {
         if (response.isPresent()) {
            List<KeyRepresentation> keys = newArrayList(response.get());
            LOG.info("Loaded active keys [number={}, duration={}]", keys.size(), TimeUtils.duration(opStart));

            updateTable(searchString, keys);
         } else {
            App.displayWarning(messages.get(this, "canNotLoadKeys"));
         }
      });
   }

   private void updateTable(String searchString, List<KeyRepresentation> keys) {
      LocalDateTime opStart = LocalDateTime.now();
      if (StringUtils.isNoneBlank(searchString)) {
         keys.removeIf(doesNotMatch(searchString));
      }
      keyAssignmentTable.update(keys);
      LOG.info("Filtered and updated table data [number={}, duration={}]",
            keys.size(), TimeUtils.duration(opStart));
   }

   private Predicate<KeyRepresentation> doesNotMatch(String searchString) {
      return key -> {
         String content = key.getUser().firstName + key.getUser().lastName + key.getKeyNumber() + key.getDateTime();
         return !content.toLowerCase().contains(searchString.toLowerCase());
      };
   }
}
