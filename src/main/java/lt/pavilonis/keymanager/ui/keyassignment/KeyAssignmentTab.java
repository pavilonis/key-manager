package lt.pavilonis.keymanager.ui.keyassignment;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import lt.pavilonis.keymanager.MessageSourceAdapter;
import lt.pavilonis.keymanager.NotificationDisplay;
import lt.pavilonis.keymanager.Spring;
import lt.pavilonis.keymanager.WebServiceClient;
import lt.pavilonis.keymanager.ui.Footer;
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

public class KeyAssignmentTab extends Tab implements NotificationDisplay {

   private static final Logger LOGGER = getLogger(KeyAssignmentTab.class.getSimpleName());
   private final WebServiceClient webServiceClient = Spring.CONTEXT.getBean(WebServiceClient.class);
   private final MessageSourceAdapter messages = Spring.CONTEXT.getBean(MessageSourceAdapter.class);
   private final KeyAssignmentTable keyAssignmentTable = new KeyAssignmentTable(webServiceClient, messages);

   public KeyAssignmentTab(Footer footer) {
      setText(messages.get(this, "title"));
      setClosable(false);

      var filterPanel = new KeyAssignmentFilterPanel(messages, this::searchAction);
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
         var mainTabLayout = new BorderPane(keyAssignmentTable, filterPanel, null, footer, null);
         mainTabLayout.setPadding(new Insets(15, 15, 0, 15));
         setContent(mainTabLayout);
      });
   }

   //TODO move to abstract class?
   private void searchAction(KeyAssignmentFilter filter) {
      var start = now();
      Consumer<Key[]> consumer = response -> {
         List<Key> keys = filter(filter, response);
         LOGGER.info("Loaded active keys [number={}/{}, duration={}]", keys.size(), response.length, duration(start));
         keyAssignmentTable.update(keys);
      };

      clearWarnings();
      webServiceClient.allActiveKeys(filter.getKeyNumber(), consumer,
            exception -> warn(messages.get(this, "canNotLoadKeys"), exception));
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

   @Override
   public List<Node> getStackPaneChildren() {
      StackPane stackPane = (StackPane) getTabPane().getParent();
      return stackPane.getChildren();
   }
}
