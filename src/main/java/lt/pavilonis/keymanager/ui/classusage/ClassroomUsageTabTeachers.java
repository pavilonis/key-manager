package lt.pavilonis.keymanager.ui.classusage;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import lt.pavilonis.keymanager.MessageSourceAdapter;
import lt.pavilonis.keymanager.Spring;
import lt.pavilonis.keymanager.WebServiceClient;
import lt.pavilonis.keymanager.ui.AbstractTab;
import lt.pavilonis.keymanager.ui.AbstractTable;
import lt.pavilonis.keymanager.ui.AbstractFilterPanel;
import lt.pavilonis.keymanager.ui.NotificationDisplay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ClassroomUsageTabTeachers extends AbstractTab<ScanLogBrief, ClassroomUsageFilter> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ClassroomUsageTabTeachers.class);
   private final WebServiceClient webServiceClient = Spring.CONTEXT.getBean(WebServiceClient.class);
   private final MessageSourceAdapter messages = Spring.CONTEXT.getBean(MessageSourceAdapter.class);
   private final Set<String> teacherGroupInclusions =
         Set.of(Spring.getStringProperty("classroomUsage.teachers.groupInclusionList", String[].class));

   public ClassroomUsageTabTeachers(NotificationDisplay notifications) {
      super(notifications);
      setText(messages.get(this, "title"));
   }

   @Override
   protected AbstractTable<ScanLogBrief> createTable() {
      return new ClassroomUsageTable();
   }

   @Override
   protected AbstractFilterPanel<ClassroomUsageFilter> createFilterPanel() {
      var filterPanel = new ClassroomUsageFilterPanel();
      BorderPane.setMargin(filterPanel, new Insets(0, 0, 15, 0));
      return filterPanel;
   }

   @Override
   public void updateTable(ClassroomUsageFilter filter) {
      Platform.runLater(notifications::clear);
      webServiceClient.classroomUsage(
            filter.getText(),
            usage -> getTable().update(filterEntries(usage)),
            exception -> notifications.warn(messages.get(this, "canNotLoadClassroomUsage"), exception)
      );
   }

   private List<ScanLogBrief> filterEntries(ScanLogBrief[] entries) {
      List<ScanLogBrief> filteredEntries = Stream.of(entries)
            .filter(entry -> teacherGroupInclusions.stream()
                  .anyMatch(inclusion -> entry.getGroup().toLowerCase().contains(inclusion)))
            .collect(toList());

      LOGGER.info("Brief scan logs loaded (all/role filtered) [entries={}/{}]", entries.length, filteredEntries.size());
      return filteredEntries;
   }
}
