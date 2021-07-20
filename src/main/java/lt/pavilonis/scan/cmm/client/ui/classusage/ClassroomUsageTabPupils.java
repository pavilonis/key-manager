package lt.pavilonis.scan.cmm.client.ui.classusage;

import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import lt.pavilonis.scan.cmm.client.App;
import lt.pavilonis.scan.cmm.client.MessageSourceAdapter;
import lt.pavilonis.scan.cmm.client.WsRestClient;
import lt.pavilonis.scan.cmm.client.ui.Footer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Controller
public class ClassroomUsageTabPupils extends Tab {

   private static final Logger LOGGER = LoggerFactory.getLogger(ClassroomUsageTabPupils.class);
   private final String[] pupilGroupExclusions;
   private final ClassroomUsageTable classUsageTable;
   private final MessageSourceAdapter messages;

   @Autowired
   public ClassroomUsageTabPupils(WsRestClient wsClient, MessageSourceAdapter messages,
                                  @Value("${classroomUsage.pupils.groupExclusionList:moky,koncertm,budėtoja,valytoja,derintoja,pedagog,vedėja,darbinink}")
                                        String[] pupilGroupExclusions) {
      setText(messages.get(this, "title"));
      this.pupilGroupExclusions = pupilGroupExclusions;
      this.messages = messages;
      this.classUsageTable = new ClassroomUsageTable(messages);

      setClosable(false);

      var filterPanel = new ClassroomUsageFilterPanel(messages);
      filterPanel.addSearchListener(event -> updateTable(filterPanel.getFilter(), wsClient));

      BorderPane.setMargin(filterPanel, new Insets(0, 0, 15, 0));

      setOnSelectionChanged(event -> {
         //TODO move to abstract class?
         if (isSelected()) {
            filterPanel.reset();
            updateTable(filterPanel.getFilter(), wsClient);
            filterPanel.focus();
         } else {
            classUsageTable.clear();
         }
         var mainTabLayout = new BorderPane(classUsageTable, filterPanel, null, new Footer(), null);
         mainTabLayout.setPadding(new Insets(15, 15, 0, 15));
         setContent(mainTabLayout);
      });
   }

   //TODO move to abstract class?
   private void updateTable(ClassroomUsageFilter filter, WsRestClient wsClient) {
      wsClient.classroomUsage(filter.getText(), response -> response.ifPresentOrElse(
            usage -> classUsageTable.update(filterEntries(usage)),
            () -> App.displayWarning(messages.get(this, "canNotLoadClassroomUsage"))
      ));
   }

   private List<ScanLogBrief> filterEntries(ScanLogBrief[] entries) {
      List<ScanLogBrief> filteredEntries = Stream.of(entries)
            .filter(entry -> {
               String group = StringUtils.lowerCase(entry.getGroup());
               return StringUtils.indexOfAny(group, pupilGroupExclusions) < 0;
            })
            .collect(toList());

      LOGGER.info("Brief scan logs loaded (all/role filtered) [entries={}/{}]", entries.length, filteredEntries.size());
      return filteredEntries;
   }
}
