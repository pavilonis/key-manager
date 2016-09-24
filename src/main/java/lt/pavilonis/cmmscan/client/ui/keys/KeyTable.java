package lt.pavilonis.cmmscan.client.ui.keys;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import lt.pavilonis.cmmscan.client.representation.KeyRepresentation;
import lt.pavilonis.cmmscan.client.representation.UserRepresentation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

final class KeyTable extends TableView<KeyRepresentation> {

   private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
   private final ObservableList<KeyRepresentation> container = FXCollections.observableArrayList();

   public KeyTable() {
      this.setItems(container);

      TableColumn<KeyRepresentation, Integer> keyNumberColumn = new TableColumn<>("Key Number");
      keyNumberColumn.setMinWidth(120);
      keyNumberColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().keyNumber));

      TableColumn<KeyRepresentation, LocalDateTime> dateTimeColumn = new TableColumn<>("Assignment time");
      dateTimeColumn.setMinWidth(170);
      dateTimeColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().dateTime));
      dateTimeColumn.setCellFactory(new Callback<TableColumn<KeyRepresentation, LocalDateTime>, TableCell<KeyRepresentation, LocalDateTime>>() {
         @Override
         public TableCell<KeyRepresentation, LocalDateTime> call(TableColumn<KeyRepresentation, LocalDateTime> param) {
            return new TableCell<KeyRepresentation, LocalDateTime>() {
               @Override
               protected void updateItem(LocalDateTime item, boolean empty) {
                  super.updateItem(item, empty);
                  if (item == null || empty) {
                     setText(null);
                     setGraphic(null);
                     setStyle("");
                  } else {
                     setText(DATE_TIME_FORMAT.format(item));
                     setTextFill(Color.CHOCOLATE);
                     setStyle("-fx-background-color: yellow");
                  }
               }
            };
         }
      });

      TableColumn<KeyRepresentation, String> userColumn = new TableColumn<>("User");
      userColumn.setCellValueFactory(param -> {
         UserRepresentation user = param.getValue().user;
         return new ReadOnlyObjectWrapper<>(user.firstName + " " + user.lastName);
      });
      userColumn.setMinWidth(250);

      TableColumn<KeyRepresentation, String> description = new TableColumn<>("Description");
      description.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().user.description));
      description.setMinWidth(250);


      TableColumn<KeyRepresentation, String> actionColumn = new TableColumn<>("Action");
      actionColumn.setCellFactory(param -> {
         Button returnKeyButton = new Button(" X ");
         returnKeyButton.setPrefWidth(50);
         returnKeyButton.setOnAction(click -> System.out.println("hehe"));
         return new TableCell<KeyRepresentation, String>() {

            @Override
            protected void updateItem(String item, boolean empty) {
               super.updateItem(item, empty);
               if (empty) {
                  setText(null);
                  setGraphic(null);
               } else {
                  setGraphic(returnKeyButton);
               }
            }
         };
      });

//      ColumnConstraints actionConstraints = new ColumnConstraints();
//      actionConstraints.setPrefWidth(100);
//      actionConstraints.setHalignment(HPos.CENTER);
      actionColumn.setPrefWidth(100);

      getColumns().addAll(keyNumberColumn, dateTimeColumn, userColumn, description, actionColumn);
   }

   public void update(List<KeyRepresentation> keys) {
      Platform.runLater(() -> {
         container.clear();
         container.addAll(keys);
      });
   }
}
