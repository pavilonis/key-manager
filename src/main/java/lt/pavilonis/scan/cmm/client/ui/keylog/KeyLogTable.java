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
import lt.pavilonis.scan.cmm.client.representation.KeyAction;
import lt.pavilonis.scan.cmm.client.representation.KeyRepresentation;
import lt.pavilonis.scan.cmm.client.representation.UserRepresentation;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.util.Arrays.asList;

final class KeyLogTable extends TableView<KeyRepresentation> {

   private static final String CLASS_NAME = KeyLogTable.class.getSimpleName();
   private static final String ICON_ASSINGED = "images/flat-arrow-up-24.png";
   private static final String ICON_UNASSIGNED = "images/flat-arrow-down-24.png";
   private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd  hh:mm:ss");
   private final ObservableList<KeyRepresentation> container = FXCollections.observableArrayList();

   public KeyLogTable(MessageSource messageSource) {
      this.setItems(container);

      TableColumn<KeyRepresentation, Integer> keyNumberColumn =
            new TableColumn<>(messageSource.getMessage(CLASS_NAME + ".keyNumber", null, null));
      keyNumberColumn.setMinWidth(120);
      keyNumberColumn.setMaxWidth(120);
      keyNumberColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().keyNumber));

      TableColumn<KeyRepresentation, KeyRepresentation> dateTimeColumn =
            new TableColumn<>(messageSource.getMessage(CLASS_NAME + ".dateTime", null, null));
      dateTimeColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
      dateTimeColumn.setCellFactory(column -> new TableCell<KeyRepresentation, KeyRepresentation>() {
         @Override
         protected void updateItem(KeyRepresentation item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
               setText(null);
               setGraphic(null);
               setStyle("");
            } else {
               setText(DATE_TIME_FORMAT.format(item.dateTime));
            }
         }
      });
      dateTimeColumn.setComparator((key1, key2) -> key1.dateTime.compareTo(key2.dateTime));
      dateTimeColumn.setSortType(TableColumn.SortType.DESCENDING);
      dateTimeColumn.setMinWidth(190);
      dateTimeColumn.setMaxWidth(190);

      TableColumn<KeyRepresentation, String> userColumn =
            new TableColumn<>(messageSource.getMessage(CLASS_NAME+".user", null, null));
      userColumn.setCellValueFactory(param -> {
         UserRepresentation user = param.getValue().user;
         return new ReadOnlyObjectWrapper<>(user.firstName + " " + user.lastName);
      });

      TableColumn<KeyRepresentation, UserRepresentation> groupColumn =
            new TableColumn<>(messageSource.getMessage(CLASS_NAME+".group", null, null));
      groupColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().user));
      groupColumn.setCellFactory(column -> new TableCell<KeyRepresentation, UserRepresentation>() {
         @Override
         protected void updateItem(UserRepresentation item, boolean empty) {
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

      TableColumn<KeyRepresentation, KeyAction> actionColumn =
            new TableColumn<>(messageSource.getMessage(CLASS_NAME+".action", null, null));

      actionColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().keyAction));
      actionColumn.setCellFactory(param -> new TableCell<KeyRepresentation, KeyAction>() {
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

      getColumns().addAll(asList(keyNumberColumn, dateTimeColumn, userColumn, groupColumn, actionColumn));
      getSortOrder().add(dateTimeColumn);
      setStyle("-fx-font-size:15; -fx-font-weight: 600; -fx-alignment: center");
      setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
      setFocusTraversable(false);
   }

   public void update(List<KeyRepresentation> keys) {
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
