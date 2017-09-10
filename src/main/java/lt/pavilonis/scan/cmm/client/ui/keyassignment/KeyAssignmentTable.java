package lt.pavilonis.scan.cmm.client.ui.keyassignment;

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
import lt.pavilonis.scan.cmm.client.ui.keylog.Key;
import lt.pavilonis.scan.cmm.client.User;
import lt.pavilonis.scan.cmm.client.MessageSourceAdapter;
import lt.pavilonis.scan.cmm.client.WsRestClient;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static org.slf4j.LoggerFactory.getLogger;

@Component
public class KeyAssignmentTable extends TableView<Key> {

   private static final Logger LOG = getLogger(KeyAssignmentTable.class.getSimpleName());
   private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm");
   private final ObservableList<Key> container = FXCollections.observableArrayList();

   @Autowired
   public KeyAssignmentTable(WsRestClient wsClient, MessageSourceAdapter messages) {
      this.setItems(container);

      TableColumn<Key, Integer> keyNumberColumn = new TableColumn<>(messages.get(this, "keyNumber"));
      keyNumberColumn.setMinWidth(120);
      keyNumberColumn.setMaxWidth(120);
      keyNumberColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getKeyNumber()));

      TableColumn<Key, LocalDateTime> dateTimeColumn =
            new TableColumn<>(messages.get(this, "assignmentTime"));
      dateTimeColumn.setMinWidth(190);
      dateTimeColumn.setMaxWidth(190);
      dateTimeColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getDateTime()));
      dateTimeColumn.setCellFactory(column -> new TableCell<Key, LocalDateTime>() {
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

      TableColumn<Key, String> userColumn = new TableColumn<>(messages.get(this, "user"));
      userColumn.setCellValueFactory(param -> {
         User user = param.getValue().getUser();
         String value = isNull(user) ? "" : user.firstName + " " + user.lastName;
         return new ReadOnlyObjectWrapper<>(value);
      });

      TableColumn<Key, Key> descriptionColumn =
            new TableColumn<>(messages.get(this, "group"));
      descriptionColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
      descriptionColumn.setCellFactory(column -> new TableCell<Key, Key>() {
         @Override
         protected void updateItem(Key item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
               setText(null);
               setGraphic(null);
               setStyle("");
            } else {
               User user = item.getUser();
               setText(user == null ? null : user.group);
               if (user != null && StringUtils.isNotBlank(user.role)
                     && StringUtils.containsIgnoreCase(user.role, "mokinys")) {
                  setStyle(AppConfig.STYLE_STUDENT);
               }
            }
         }
      });
      descriptionColumn.setComparator((key1, key2) -> ObjectUtils.compare(key1.getUser().group, key2.getUser().group));

      TableColumn<Key, Key> unassignmentColumn =
            new TableColumn<>(messages.get(this, "unassignment"));
      unassignmentColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
      unassignmentColumn.setCellFactory(param -> {
         Button returnKeyButton = new Button(null, new ImageView(new Image("images/flat-arrow-down-24.png")));
         returnKeyButton.setPrefWidth(70);
         return new TableCell<Key, Key>() {

            @Override
            protected void updateItem(Key item, boolean empty) {
               super.updateItem(item, empty);
               if (empty) {
                  setText(null);
                  setGraphic(null);
               } else {
                  setAlignment(Pos.CENTER);
                  setGraphic(returnKeyButton);
                  returnKeyButton.setOnAction(click -> wsClient.returnKey(item.getKeyNumber(), response -> {
                     if (response.isPresent()) {
                        LOG.info("Returned key [keyNumber={}]", response.get().getKeyNumber());
                        container.remove(item);
                     } else {
                        LOG.error("Erroneous state - could not return key [keyNumber={}]", item.getKeyNumber());
                     }
                  }));
               }
            }
         };
      });
      unassignmentColumn.setSortable(false);
      unassignmentColumn.setMinWidth(110);
      unassignmentColumn.setMaxWidth(110);

      getColumns().addAll(asList(keyNumberColumn, dateTimeColumn, userColumn, descriptionColumn, unassignmentColumn));
      getSortOrder().add(dateTimeColumn);
      setStyle("-fx-font-size:15; -fx-font-weight: 600; -fx-alignment: center");
      setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
      setFocusTraversable(false);
   }

   public void update(List<Key> keys) {
      container.clear();
      container.addAll(keys);
      sort();
   }

   public void clear() {
      container.clear();
   }
}
