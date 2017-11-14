package lt.pavilonis.scan.cmm.client.ui.keyassignment;

import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import lt.pavilonis.scan.cmm.client.App;
import lt.pavilonis.scan.cmm.client.ui.keylog.Key;
import lt.pavilonis.scan.cmm.client.MessageSourceAdapter;
import lt.pavilonis.scan.cmm.client.WsRestClient;
import lt.pavilonis.scan.cmm.client.ui.Footer;
import lt.pavilonis.util.TimeUtils;
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
   public KeyAssignmentTab(MessageSourceAdapter messages, WsRestClient wsClient,
                           KeyAssignmentTable keyAssignmentTable) {

      this.messages = messages;
      this.wsClient = wsClient;
      this.keyAssignmentTable = keyAssignmentTable;

      setText(messages.get(this, "title"));
      setClosable(false);

      KeyAssignmentFilterPanel filterPanel =
            new KeyAssignmentFilterPanel(messages, this::searchAction);

      BorderPane.setMargin(filterPanel, new Insets(0, 0, 15, 0));

      setOnSelectionChanged(event -> {
         //TODO move to abstract class?
         if (isSelected()) {
            filterPanel.reset();
            filterPanel.action();
            filterPanel.focus();
         } else {
            keyAssignmentTable.clear();
         }
         BorderPane mainTabLayout = new BorderPane(keyAssignmentTable, filterPanel, null, new Footer(), null);
         mainTabLayout.setPadding(new Insets(15, 15, 0, 15));
         setContent(mainTabLayout);
      });
   }

   //TODO move to abstract class?
   private void searchAction(KeyAssignmentFilter filter) {
      LocalDateTime opStart = LocalDateTime.now();
      wsClient.allActiveKeys(filter.getKeyNumber(), response -> {
         if (response.isPresent()) {
            List<Key> keys = newArrayList(response.get());
            LOG.info("Loaded active keys [number={}, duration={}]", keys.size(), TimeUtils.duration(opStart));

            keys.removeIf(doesNotMatch(filter.getName()));
            keyAssignmentTable.update(keys);
         } else {
            App.displayWarning(messages.get(this, "canNotLoadKeys"));
         }
      });
   }

   private Predicate<Key> doesNotMatch(String searchString) {
      return key -> {
         if (StringUtils.isBlank(searchString)) {
            return false;
         }
         String content = key.getUser().firstName + key.getUser().lastName;
         return !content.toLowerCase().contains(searchString.toLowerCase());
      };
   }
}
