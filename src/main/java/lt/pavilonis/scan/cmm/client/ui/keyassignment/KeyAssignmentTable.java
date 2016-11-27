package lt.pavilonis.scan.cmm.client.ui.keyassignment;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lt.pavilonis.scan.cmm.client.AppConfig;
import lt.pavilonis.scan.cmm.client.representation.KeyRepresentation;
import lt.pavilonis.scan.cmm.client.representation.UserRepresentation;
import lt.pavilonis.scan.cmm.client.service.WsRestClient;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.util.Arrays.asList;

@Component
public class KeyAssignmentTable extends TableView<KeyRepresentation> {

   private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd  hh:mm:ss");
   private final ObservableList<KeyRepresentation> container = FXCollections.observableArrayList();

   @Autowired
   private WsRestClient wsClient;

   public KeyAssignmentTable() {
      this.setItems(container);

      TableColumn<KeyRepresentation, Integer> keyNumberColumn = new TableColumn<>("Key Number");
      keyNumberColumn.setMinWidth(120);
      keyNumberColumn.setMaxWidth(120);
      keyNumberColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().keyNumber));

      TableColumn<KeyRepresentation, LocalDateTime> dateTimeColumn = new TableColumn<>("Assignment time");
      dateTimeColumn.setMinWidth(190);
      dateTimeColumn.setMaxWidth(190);
      dateTimeColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().dateTime));
      dateTimeColumn.setCellFactory(column -> new TableCell<KeyRepresentation, LocalDateTime>() {
         @Override
         protected void updateItem(LocalDateTime item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
               setText(null);
               setGraphic(null);
               setStyle("");
            } else {
               setText(DATE_TIME_FORMAT.format(item));
            }
         }
      });
      dateTimeColumn.setSortType(TableColumn.SortType.DESCENDING);

      TableColumn<KeyRepresentation, String> userColumn = new TableColumn<>("User");
      userColumn.setCellValueFactory(param -> {
         UserRepresentation user = param.getValue().user;
         return new ReadOnlyObjectWrapper<>(user.firstName + " " + user.lastName);
      });

      TableColumn<KeyRepresentation, KeyRepresentation> descriptionColumn = new TableColumn<>("Group");
      descriptionColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
      descriptionColumn.setCellFactory(column -> new TableCell<KeyRepresentation, KeyRepresentation>() {
         @Override
         protected void updateItem(KeyRepresentation item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
               setText(null);
               setGraphic(null);
               setStyle("");
            } else {
               setText(item.user.group);
               if (StringUtils.isNotBlank(item.user.role)
                     && StringUtils.containsIgnoreCase(item.user.role, "mokinys")) {
                  setStyle(AppConfig.STYLE_STUDENT);
               }
            }
         }
      });
      descriptionColumn.setComparator((key1, key2) -> ObjectUtils.compare(key1.user.group, key2.user.group));

      TableColumn<KeyRepresentation, KeyRepresentation> actionColumn = new TableColumn<>("Action");
      actionColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
      actionColumn.setCellFactory(param -> {
         Button returnKeyButton = new Button(null, new ImageView(new Image("images/flat-arrow-down-24.png")));
         returnKeyButton.setPrefWidth(50);
         return new TableCell<KeyRepresentation, KeyRepresentation>() {

            @Override
            protected void updateItem(KeyRepresentation item, boolean empty) {
               super.updateItem(item, empty);
               if (empty) {
                  setText(null);
                  setGraphic(null);
               } else {
                  setAlignment(Pos.CENTER);
                  setGraphic(returnKeyButton);
                  returnKeyButton.setOnAction(click -> {
                     wsClient.returnKey(item.keyNumber);
                     container.remove(item);
                  });
               }
            }
         };
      });
      actionColumn.setSortable(false);
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
         sort();
      });
   }

   public void clear() {
      container.clear();
   }
}
