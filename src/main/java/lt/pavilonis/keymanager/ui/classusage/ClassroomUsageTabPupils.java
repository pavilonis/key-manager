package lt.pavilonis.keymanager.ui.classusage;

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
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ClassroomUsageTabPupils extends Tab implements NotificationDisplay {

   private static final Logger LOGGER = LoggerFactory.getLogger(ClassroomUsageTabPupils.class);
   private final WebServiceClient webServiceClient = Spring.CONTEXT.getBean(WebServiceClient.class);
   private final MessageSourceAdapter messages = Spring.CONTEXT.getBean(MessageSourceAdapter.class);
   private final ClassroomUsageTable classUsageTable = new ClassroomUsageTable(messages);
   private final Set<String> pupilGroupExclusions =
         Set.of(Spring.getStringProperty("classroomUsage.pupils.groupExclusionList", String[].class));

   public ClassroomUsageTabPupils(Footer footer) {
      setText(messages.get(this, "title"));
      setClosable(false);

      var filterPanel = new ClassroomUsageFilterPanel(messages);
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
         var mainTabLayout = new BorderPane(classUsageTable, filterPanel, null, footer, null);
         mainTabLayout.setPadding(new Insets(15, 15, 0, 15));
         setContent(mainTabLayout);
      });
   }

   //TODO move to abstract class?
   private void updateTable(ClassroomUsageFilter filter) {
      webServiceClient.classroomUsage(filter.getText(),
            usage -> classUsageTable.update(filterEntries(usage)),
            exception -> warn(messages.get(this, "canNotLoadClassroomUsage"), exception)
      );
   }

   private List<ScanLogBrief> filterEntries(ScanLogBrief[] entries) {
      List<ScanLogBrief> filteredEntries = Stream.of(entries)
            .filter(entry -> pupilGroupExclusions.stream()
                  .noneMatch(exclusion -> entry.getGroup().toLowerCase().contains(exclusion)))
            .collect(toList());

      LOGGER.info("Brief scan logs loaded (all/role filtered) [entries={}/{}]", entries.length, filteredEntries.size());
      return filteredEntries;
   }

   @Override
   public List<Node> getStackPaneChildren() {
      StackPane stackPane = (StackPane) getTabPane().getParent();
      return stackPane.getChildren();
   }
}
