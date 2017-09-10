package lt.pavilonis.scan.cmm.client.ui.classusage;

import com.google.common.collect.Lists;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import lt.pavilonis.scan.cmm.client.App;
import lt.pavilonis.scan.cmm.client.MessageSourceAdapter;
import lt.pavilonis.scan.cmm.client.WsRestClient;
import lt.pavilonis.scan.cmm.client.ui.Footer;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import static org.slf4j.LoggerFactory.getLogger;

@Controller
public class ClassroomUsageTab extends Tab {

   private static final Logger LOG = getLogger(ClassroomUsageTab.class.getSimpleName());
   private final ClassroomUsageTable classUsageTable;
   private final WsRestClient wsClient;
   private MessageSourceAdapter messages;

   @Autowired
   public ClassroomUsageTab(WsRestClient wsClient, MessageSourceAdapter messages) {
      setText(messages.get(this, "title"));

      this.messages = messages;
      this.wsClient = wsClient;
      this.classUsageTable = new ClassroomUsageTable(messages);

      setClosable(false);

      ClassroomUsageFilterPanel filterPanel = new ClassroomUsageFilterPanel(messages, wsClient);
      filterPanel.addSearchListener(event -> updateTable(filterPanel.getFilter()));

      BorderPane.setMargin(filterPanel, new Insets(0, 0, 15, 0));

      setOnSelectionChanged(event -> {
         //TODO move to abstract class?
         if (isSelected()) {
            filterPanel.reset();
            updateTable(filterPanel.getFilter());
            filterPanel.focus();
         } else {
            classUsageTable.clear();
         }
         BorderPane mainTabLayout = new BorderPane(classUsageTable, filterPanel, null, new Footer(), null);
         mainTabLayout.setPadding(new Insets(15, 15, 0, 15));
         setContent(mainTabLayout);
      });
   }

   //TODO move to abstract class?
   private void updateTable(ClassroomUsageFilter filter) {
      wsClient.classroomUsage(filter.getText(), filter.getRole(), response -> {
         if (response.isPresent()) {
            LOG.info("Loaded keyLog [entries={}]", response.get().length);
            classUsageTable.update(Lists.newArrayList(response.get()));
         } else {
            App.displayWarning(messages.get(this, "canNotLoadClassroomUsage"));
         }
      });
   }
}
