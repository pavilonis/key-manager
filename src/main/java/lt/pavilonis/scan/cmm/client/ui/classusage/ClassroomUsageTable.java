package lt.pavilonis.scan.cmm.client.ui.classusage;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lt.pavilonis.scan.cmm.client.AppConfig;
import lt.pavilonis.scan.cmm.client.MessageSourceAdapter;
import lt.pavilonis.scan.cmm.client.User;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

final class ClassroomUsageTable extends TableView<ClassroomUsage> {

   private static final String ICON_CLASSROOM_OCCUPIED = "images/lock_24_red.png";
   private static final String ICON_CLASSROOM_FREE = "images/lock_24_green.png";
   private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm");
   private final ObservableList<ClassroomUsage> container = FXCollections.observableArrayList();

   public ClassroomUsageTable(MessageSourceAdapter messages) {
      this.setItems(container);

      TableColumn<ClassroomUsage, Integer> classroomNumber = new TableColumn<>(messages.get(this, ("classroomNumber")));
      classroomNumber.setMinWidth(120);
      classroomNumber.setMaxWidth(120);
      classroomNumber.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getClassroomNumber()));

      TableColumn<ClassroomUsage, ClassroomUsage> dateTimeColumn = new TableColumn<>(messages.get(this, ("dateTime")));
      dateTimeColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
      dateTimeColumn.setCellFactory(column -> new TableCell<ClassroomUsage, ClassroomUsage>() {
         @Override
         protected void updateItem(ClassroomUsage item, boolean empty) {
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
      dateTimeColumn.setComparator((key1, key2) -> key1.getDateTime().compareTo(key2.getDateTime()));
      dateTimeColumn.setSortType(TableColumn.SortType.DESCENDING);
      dateTimeColumn.setMinWidth(190);
      dateTimeColumn.setMaxWidth(190);

      TableColumn<ClassroomUsage, String> userColumn = new TableColumn<>(messages.get(this, "user"));
      userColumn.setCellValueFactory(param -> {
         User user = param.getValue().getUser();
         return new ReadOnlyObjectWrapper<>(user.firstName + " " + user.lastName);
      });

      TableColumn<ClassroomUsage, User> roleColumn =
            new TableColumn<>(messages.get(this, "role"));
      roleColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getUser()));
      roleColumn.setCellFactory(column -> new TableCell<ClassroomUsage, User>() {
         @Override
         protected void updateItem(User item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
               setText(null);
               setGraphic(null);
               setStyle("");
            } else {
               setText(item.role);
               if (StringUtils.containsIgnoreCase(item.role, "mokinys")) {
                  setStyle(AppConfig.STYLE_STUDENT);
               }
            }
         }
      });
      roleColumn.setComparator((user1, user2) -> ObjectUtils.compare(user1.role, user2.role));

      TableColumn<ClassroomUsage, Boolean> actionColumn = new TableColumn<>(messages.get(this, "action"));

      actionColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().isOccupied()));
      actionColumn.setCellFactory(param -> new TableCell<ClassroomUsage, Boolean>() {
         @Override
         protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
               setText(null);
               setGraphic(null);
               setStyle("");
            } else {
               if (Boolean.TRUE.equals(item)) {
                  setIcon(ICON_CLASSROOM_OCCUPIED);
               } else {
                  setIcon(ICON_CLASSROOM_FREE);
               }
               setAlignment(Pos.CENTER);
            }
         }

         private void setIcon(String path) {
            this.setGraphic(new ImageView(new Image(path)));
         }
      });
      actionColumn.setMinWidth(100);
      actionColumn.setMaxWidth(100);

      getColumns().addAll(Arrays.asList(classroomNumber, dateTimeColumn, userColumn, roleColumn, actionColumn));
      getSortOrder().add(dateTimeColumn);
      setStyle("-fx-font-size:15; -fx-font-weight: 600; -fx-alignment: center");
      setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
      setFocusTraversable(false);
   }

   public void update(List<ClassroomUsage> keys) {
      Platform.runLater(() -> {
         container.clear();
         container.addAll(keys);
         sort();
      });
   }

   public void clear() {
      container.clear();
   }
}
