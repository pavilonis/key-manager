package lt.pavilonis.keymanager.ui.classusage;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lt.pavilonis.keymanager.MessageSourceAdapter;
import lt.pavilonis.keymanager.Spring;
import lt.pavilonis.keymanager.ui.AbstractTable;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import static java.util.Comparator.naturalOrder;

final class ClassroomUsageTable extends AbstractTable<ScanLogBrief> {

   private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm");
   private final MessageSourceAdapter messages = Spring.CONTEXT.getBean(MessageSourceAdapter.class);

   public ClassroomUsageTable() {
      this.setItems(getContainer());

      TableColumn<ScanLogBrief, ScanLogBrief> dateTimeColumn = createDateTimeColumn();

      getColumns().addAll(List.of(
            createScannerColumn(), createRoomNumberColumn(), dateTimeColumn, createUserColumn(), createGroupColumn()));

      getSortOrder().add(dateTimeColumn);
      setStyle("-fx-font-size:15; -fx-font-weight: 600; -fx-alignment: center");
      setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
      setFocusTraversable(false);
   }

   private TableColumn<ScanLogBrief, String> createUserColumn() {
      var userColumn = new TableColumn<ScanLogBrief, String>(messages.get("ClassroomUsageTable.user"));
      userColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getName()));
      return userColumn;
   }

   private TableColumn<ScanLogBrief, String> createGroupColumn() {
      var groupColumn = new TableColumn<ScanLogBrief, String>(messages.get("ClassroomUsageTable.group"));
      groupColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getGroup()));
      groupColumn.setComparator(naturalOrder());
      return groupColumn;
   }

   private TableColumn<ScanLogBrief, ScanLogBrief> createDateTimeColumn() {
      var dateTimeColumn = new TableColumn<ScanLogBrief, ScanLogBrief>(messages.get("ClassroomUsageTable.dateTime"));
      dateTimeColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
      dateTimeColumn.setCellFactory(column -> new TableCell<>() {
         @Override
         protected void updateItem(ScanLogBrief item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
               setText(null);
               setGraphic(null);
               setStyle("");
            } else {
               setText(DATE_TIME_FORMAT.format(item.getDateTime()));
            }
         }
      });
      dateTimeColumn.setComparator(Comparator.comparing(ScanLogBrief::getDateTime));
      dateTimeColumn.setSortType(TableColumn.SortType.DESCENDING);
      dateTimeColumn.setMinWidth(190);
      dateTimeColumn.setMaxWidth(190);
      return dateTimeColumn;
   }

   private TableColumn<ScanLogBrief, String> createRoomNumberColumn() {
      var classroomNumber = new TableColumn<ScanLogBrief, String>(messages.get("ClassroomUsageTable.classroomNumber"));
      classroomNumber.setMinWidth(150);
      classroomNumber.setMaxWidth(150);
      classroomNumber.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getLocation()));
      classroomNumber.setComparator(naturalOrder());
      return classroomNumber;
   }

   private TableColumn<ScanLogBrief, String> createScannerColumn() {
      var scanner = new TableColumn<ScanLogBrief, String>(messages.get("ClassroomUsageTable.scanner"));
      scanner.setMinWidth(160);
      scanner.setMaxWidth(160);
      scanner.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getScanner()));
      scanner.setComparator(naturalOrder());
      return scanner;
   }
}
