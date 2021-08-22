package lt.pavilonis.keymanager.ui.keyassignment;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import lt.pavilonis.keymanager.MessageSourceAdapter;
import lt.pavilonis.keymanager.Spring;
import lt.pavilonis.keymanager.WebServiceClient;
import lt.pavilonis.keymanager.ui.AbstractFilterPanel;
import lt.pavilonis.keymanager.ui.AbstractTab;
import lt.pavilonis.keymanager.ui.AbstractTable;
import lt.pavilonis.keymanager.ui.NotificationDisplay;
import lt.pavilonis.keymanager.ui.keylog.Key;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;
import static lt.pavilonis.keymanager.TimeUtils.duration;
import static org.slf4j.LoggerFactory.getLogger;

public class KeyAssignmentTab extends AbstractTab<Key, KeyAssignmentFilter> {

   private static final Logger LOGGER = getLogger(KeyAssignmentTab.class.getSimpleName());
   private final WebServiceClient webServiceClient = Spring.CONTEXT.getBean(WebServiceClient.class);
   private final MessageSourceAdapter messages = Spring.CONTEXT.getBean(MessageSourceAdapter.class);

   public KeyAssignmentTab(NotificationDisplay notifications) {
      super(notifications);
      setText(messages.get(this, "title"));
      setClosable(false);
   }

   @Override
   protected AbstractTable<Key> createTable() {
      return new KeyAssignmentTable();
   }

   @Override
   protected AbstractFilterPanel<KeyAssignmentFilter> createFilterPanel() {
      var filterPanel = new KeyAssignmentFilterPanel();
      BorderPane.setMargin(filterPanel, new Insets(0, 0, 15, 0));
      return filterPanel;
   }

   @Override
   protected void updateTable(KeyAssignmentFilter filter) {
      var start = now();
      Consumer<Key[]> consumer = response -> {
         List<Key> keys = filter(filter, response);
         LOGGER.info("Loaded active keys [number={}/{}, duration={}]", keys.size(), response.length, duration(start));
         getTable().update(keys);
      };

      Platform.runLater(notifications::clear);
      webServiceClient.allActiveKeys(filter.getKeyNumber(), consumer,
            exception -> notifications.warn(messages.get(this, "canNotLoadKeys"), exception));
   }

   private List<Key> filter(KeyAssignmentFilter filter, Key[] keys) {
      if (!StringUtils.hasText(filter.getName())) {
         return List.of(keys);
      }

      String filterString = filter.getName().toLowerCase();
      return Stream.of(keys)
            .filter(key -> {
               String content = key.getUser().getName();
               return !content.toLowerCase().contains(filterString);
            })
            .collect(toList());
   }
}
