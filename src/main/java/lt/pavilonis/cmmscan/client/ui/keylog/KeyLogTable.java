package lt.pavilonis.cmmscan.client.ui.keylog;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lt.pavilonis.cmmscan.client.representation.KeyRepresentation;
import lt.pavilonis.cmmscan.client.representation.UserRepresentation;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.util.Arrays.asList;

final class KeyLogTable extends TableView<KeyRepresentation> {

   private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd  hh:mm:ss");
   private final ObservableList<KeyRepresentation> container = FXCollections.observableArrayList();

   public KeyLogTable() {
      this.setItems(container);

      TableColumn<KeyRepresentation, Integer> keyNumberColumn = new TableColumn<>("Key Number");
      keyNumberColumn.setMinWidth(120);
      keyNumberColumn.setMaxWidth(120);
      keyNumberColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().keyNumber));

      TableColumn<KeyRepresentation, KeyRepresentation> dateTimeColumn = new TableColumn<>("Date Time");
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
      dateTimeColumn.setSortType(TableColumn.SortType.DESCENDING);
      dateTimeColumn.setMinWidth(190);
      dateTimeColumn.setMaxWidth(190);

      TableColumn<KeyRepresentation, String> userColumn = new TableColumn<>("User");
      userColumn.setCellValueFactory(param -> {
         UserRepresentation user = param.getValue().user;
         return new ReadOnlyObjectWrapper<>(user.firstName + " " + user.lastName);
      });

      TableColumn<KeyRepresentation, UserRepresentation> descriptionColumn = new TableColumn<>("Description");
      descriptionColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().user));
      descriptionColumn.setCellFactory(column -> new TableCell<KeyRepresentation, UserRepresentation>() {
         @Override
         protected void updateItem(UserRepresentation item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
               setText(null);
               setGraphic(null);
               setStyle("");
            } else {
               setText(item.description);
               if (item.isStudent) {
                  setStyle("-fx-background-color: rgba(0, 255, 45, 0.33)");
               }
            }
         }
      });

      TableColumn<KeyRepresentation, String> actionColumn = new TableColumn<>("Action");
      actionColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().keyAction.name()));
      actionColumn.setMinWidth(100);
      actionColumn.setMaxWidth(100);

      getColumns().addAll(asList(keyNumberColumn, dateTimeColumn, userColumn, descriptionColumn, actionColumn));
      getSortOrder().add(dateTimeColumn);
      setStyle("-fx-font-size:15; -fx-font-weight: 600; -fx-alignment: center");
      setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
      setFocusTraversable(false);
   }

   public void update(List<KeyRepresentation> keys) {
      Platform.runLater(() -> {
         container.clear();
         container.addAll(keys);
      });
   }

   public void clear() {
      container.clear();
   }
}
