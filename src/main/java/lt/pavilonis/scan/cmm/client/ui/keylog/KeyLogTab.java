package lt.pavilonis.scan.cmm.client.ui.keylog;

import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import lt.pavilonis.scan.cmm.client.App;
import lt.pavilonis.scan.cmm.client.MessageSourceAdapter;
import lt.pavilonis.scan.cmm.client.WsRestClient;
import lt.pavilonis.scan.cmm.client.ui.Footer;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Controller
public class KeyLogTab extends Tab {

   private static final Logger LOG = getLogger(KeyLogTab.class.getSimpleName());
   private final KeyLogTable keyLogTable;
   private final WsRestClient wsClient;
   private final MessageSourceAdapter messages;

   public KeyLogTab(WsRestClient wsClient, MessageSourceAdapter messages) {
      setText(messages.get(this, "title"));

      this.messages = messages;
      this.wsClient = wsClient;
      this.keyLogTable = new KeyLogTable(messages);

      setClosable(false);

      KeyLogFilterPanel filterPanel = new KeyLogFilterPanel(messages);
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
         var mainTabLayout = new BorderPane(keyLogTable, filterPanel, null, new Footer(), null);
         mainTabLayout.setPadding(new Insets(15, 15, 0, 15));
         setContent(mainTabLayout);
      });
   }

   //TODO move to abstract class?
   private void updateTable(KeyLogFilter filter) {
      wsClient.keyLog(filter, optionalResponse -> optionalResponse.ifPresentOrElse(
            response -> {
               LOG.info("Loaded keyLog [entries={}]", response.length);
               keyLogTable.update(List.of(response));
            },
            () -> App.displayWarning(messages.get(this, "canNotLoadKeys"))
      ));
   }
}
