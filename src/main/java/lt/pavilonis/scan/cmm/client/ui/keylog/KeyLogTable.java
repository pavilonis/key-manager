package lt.pavilonis.scan.cmm.client.ui.keylog;

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
import java.util.List;

final class KeyLogTable extends TableView<Key> {

   private static final String ICON_ASSINGED = "images/flat-arrow-up-24.png";
   private static final String ICON_UNASSIGNED = "images/flat-arrow-down-24.png";
   private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm");
   private final ObservableList<Key> container = FXCollections.observableArrayList();

   public KeyLogTable(MessageSourceAdapter messages) {
      this.setItems(container);

      TableColumn<Key, Integer> keyNumberColumn =
            new TableColumn<>(messages.get(this, ("keyNumber")));
      keyNumberColumn.setMinWidth(120);
      keyNumberColumn.setMaxWidth(120);
      keyNumberColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getKeyNumber()));

      TableColumn<Key, Key> dateTimeColumn =
            new TableColumn<>(messages.get(this, ("dateTime")));
      dateTimeColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
      dateTimeColumn.setCellFactory(column -> new TableCell<Key, Key>() {
         @Override
         protected void updateItem(Key item, boolean empty) {
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

      TableColumn<Key, String> userColumn =
            new TableColumn<>(messages.get(this, "user"));
      userColumn.setCellValueFactory(param -> {
         User user = param.getValue().getUser();
         return new ReadOnlyObjectWrapper<>(user.firstName + " " + user.lastName);
      });

      TableColumn<Key, User> groupColumn =
            new TableColumn<>(messages.get(this, "group"));
      groupColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getUser()));
      groupColumn.setCellFactory(column -> new TableCell<Key, User>() {
         @Override
         protected void updateItem(User item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
               setText(null);
               setGraphic(null);
               setStyle("");
            } else {
               setText(item.group);
               if (StringUtils.isNoneBlank(item.role)
                     && StringUtils.containsIgnoreCase(item.role, "mokinys")) {
                  setStyle(AppConfig.STYLE_STUDENT);
               }
            }
         }
      });
      groupColumn.setComparator((user1, user2) -> ObjectUtils.compare(user1.group, (user2.group)));

      TableColumn<Key, KeyAction> actionColumn =
            new TableColumn<>(messages.get(this, "action"));

      actionColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getKeyAction()));
      actionColumn.setCellFactory(param -> new TableCell<Key, KeyAction>() {
         @Override
         protected void updateItem(KeyAction value, boolean empty) {
            super.updateItem(value, empty);
            if (value == null || empty) {
               setText(null);
               setGraphic(null);
               setStyle("");
            } else {
               if (value == KeyAction.ASSIGNED) {
                  setIcon(ICON_ASSINGED);
               } else if (value == KeyAction.UNASSIGNED) {
                  setIcon(ICON_UNASSIGNED);
               } else {
                  throw new IllegalStateException("Not expected KeyAction value (should not happen)");
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

      getColumns().addAll(List.of(keyNumberColumn, dateTimeColumn, userColumn, groupColumn, actionColumn));
      getSortOrder().add(dateTimeColumn);
      setStyle("-fx-font-size:15; -fx-font-weight: 600; -fx-alignment: center");
      setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
      setFocusTraversable(false);
   }

   public void update(List<Key> keys) {
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
