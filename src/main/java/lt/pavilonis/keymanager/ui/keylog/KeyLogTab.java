package lt.pavilonis.keymanager.ui.keylog;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import lt.pavilonis.keymanager.MessageSourceAdapter;
import lt.pavilonis.keymanager.Spring;
import lt.pavilonis.keymanager.WebServiceClient;
import lt.pavilonis.keymanager.ui.AbstractFilterPanel;
import lt.pavilonis.keymanager.ui.AbstractTab;
import lt.pavilonis.keymanager.ui.AbstractTable;
import lt.pavilonis.keymanager.ui.Footer;
import lt.pavilonis.keymanager.ui.NotificationDisplay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class KeyLogTab extends AbstractTab<Key, KeyLogFilter> {

   private static final Logger LOGGER = LoggerFactory.getLogger(KeyLogTab.class);
   private final WebServiceClient webServiceClient = Spring.CONTEXT.getBean(WebServiceClient.class);
   private final MessageSourceAdapter messages = Spring.CONTEXT.getBean(MessageSourceAdapter.class);

   public KeyLogTab(NotificationDisplay notifications) {
      super(notifications);
      setText(messages.get(this, "title"));
      setClosable(false);
   }

   @Override
   protected AbstractTable<Key> createTable() {
      return new KeyLogTable();
   }

   @Override
   protected AbstractFilterPanel<KeyLogFilter> createFilterPanel() {
      var filterPanel = new KeyLogFilterPanel();
      BorderPane.setMargin(filterPanel, new Insets(0, 0, 15, 0));
      return filterPanel;
   }

   @Override
   public void updateTable(KeyLogFilter filter) {
      Platform.runLater(notifications::clear);
      webServiceClient.keyLog(
            filter,
            response -> {
               LOGGER.info("Loaded keyLog [entries={}]", response.length);
               getTable().update(List.of(response));
            },
            exception -> notifications.warn(messages.get(this, "canNotLoadKeys"), exception)
      );
   }
}
