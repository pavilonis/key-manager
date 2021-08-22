package lt.pavilonis.keymanager.ui.keylog;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class KeyLogTab extends Tab implements NotificationDisplay {

   private static final Logger LOGGER = LoggerFactory.getLogger(KeyLogTab.class);
   private final WebServiceClient webServiceClient = Spring.CONTEXT.getBean(WebServiceClient.class);
   private final MessageSourceAdapter messages = Spring.CONTEXT.getBean(MessageSourceAdapter.class);
   private final KeyLogTable keyLogTable;

   public KeyLogTab(Footer footer) {
      this.keyLogTable = new KeyLogTable(messages);

      setText(messages.get(this, "title"));
      setClosable(false);

      var filterPanel = new KeyLogFilterPanel(messages);
      filterPanel.addSearchListener(event -> updateTable(filterPanel.getFilter()));

      BorderPane.setMargin(filterPanel, new Insets(0, 0, 15, 0));

      setOnSelectionChanged(event -> {
         //TODO move to abstract class?
         if (isSelected()) {
            filterPanel.reset();
            updateTable(filterPanel.getFilter());
            filterPanel.focus();
         } else {
            keyLogTable.clear();
         }
         var mainTabLayout = new BorderPane(keyLogTable, filterPanel, null, footer, null);
         mainTabLayout.setPadding(new Insets(15, 15, 0, 15));
         setContent(mainTabLayout);
      });
   }

   //TODO move to abstract class?
   private void updateTable(KeyLogFilter filter) {
      webServiceClient.keyLog(
            filter,
            response -> {
               LOGGER.info("Loaded keyLog [entries={}]", response.length);
               keyLogTable.update(List.of(response));
            },
            exception -> warn(messages.get(this, "canNotLoadKeys"), exception)
      );
   }

   @Override
   public List<Node> getStackPaneChildren() {
      StackPane stackPane = (StackPane) getTabPane().getParent();
      return stackPane.getChildren();
   }
}
