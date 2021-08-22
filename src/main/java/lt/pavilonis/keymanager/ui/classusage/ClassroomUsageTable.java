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

final class ClassroomUsageTable extends AbstractTable<ScanLogBrief> {

   private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm");

   public ClassroomUsageTable() {
      this.setItems(getContainer());

      MessageSourceAdapter messages = Spring.CONTEXT.getBean(MessageSourceAdapter.class);
      var classroomNumber = new TableColumn<ScanLogBrief, String>(messages.get(this, ("classroomNumber")));
      classroomNumber.setMinWidth(150);
      classroomNumber.setMaxWidth(150);
      classroomNumber.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getLocation()));
      classroomNumber.setComparator(Comparator.naturalOrder());

      var dateTimeColumn = new TableColumn<ScanLogBrief, ScanLogBrief>(messages.get(this, ("dateTime")));
      dateTimeColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
      dateTimeColumn.setCellFactory(column -> new TableCell<ScanLogBrief, ScanLogBrief>() {
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

      var userColumn = new TableColumn<ScanLogBrief, String>(messages.get(this, "user"));
      userColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getName()));

      var groupColumn = new TableColumn<ScanLogBrief, String>(messages.get(this, "group"));
      groupColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getGroup()));
      groupColumn.setComparator(Comparator.naturalOrder());

      getColumns().addAll(List.of(classroomNumber, dateTimeColumn, userColumn, groupColumn));
      getSortOrder().add(dateTimeColumn);
      setStyle("-fx-font-size:15; -fx-font-weight: 600; -fx-alignment: center");
      setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
      setFocusTraversable(false);
   }
}
