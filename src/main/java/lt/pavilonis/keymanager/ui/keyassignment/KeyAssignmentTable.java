package lt.pavilonis.keymanager.ui.keyassignment;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lt.pavilonis.keymanager.MessageSourceAdapter;
import lt.pavilonis.keymanager.Spring;
import lt.pavilonis.keymanager.User;
import lt.pavilonis.keymanager.WebServiceClient;
import lt.pavilonis.keymanager.ui.AbstractTable;
import lt.pavilonis.keymanager.ui.keylog.Key;
import lt.pavilonis.keymanager.ui.keylog.KeyLogTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.util.Comparator.comparing;

public class KeyAssignmentTable extends AbstractTable<Key> {

   private static final Logger LOGGER = LoggerFactory.getLogger(KeyAssignmentTable.class);
   private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm");
   private final WebServiceClient webServiceClient = Spring.CONTEXT.getBean(WebServiceClient.class);

   public KeyAssignmentTable() {
      this.setItems(getContainer());

      MessageSourceAdapter messages = Spring.CONTEXT.getBean(MessageSourceAdapter.class);
      var keyNumberColumn = new TableColumn<Key, Integer>(messages.get(this, "keyNumber"));
      keyNumberColumn.setMinWidth(120);
      keyNumberColumn.setMaxWidth(120);
      keyNumberColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getKeyNumber()));

      var dateTimeColumn = new TableColumn<Key, LocalDateTime>(messages.get(this, "assignmentTime"));
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

      var userColumn = new TableColumn<Key, String>(messages.get(this, "user"));
      userColumn.setCellValueFactory(param -> {
         User user = param.getValue().getUser();
         return new ReadOnlyObjectWrapper<>(user == null ? "" : user.getName());
      });

      var descriptionColumn = new TableColumn<Key, Key>(messages.get(this, "group"));
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
               setText(user == null ? null : user.getName());
               if (user != null && StringUtils.hasText(user.getName())
                     //TODO
                     && user.getRole().toLowerCase().contains("mokinys")) {
                  setStyle(KeyLogTable.STYLE_STUDENT);
               }
            }
         }
      });
      descriptionColumn.setComparator(comparing(key -> key.getUser().getGroup()));

      var unassignmentColumn = new TableColumn<Key, Key>(messages.get(this, "unassignment"));
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
                  return;
               }

               setAlignment(Pos.CENTER);
               setGraphic(returnKeyButton);
               returnKeyButton.setOnAction(click -> webServiceClient.returnKey(
                     item.getKeyNumber(),
                     response -> {
                        LOGGER.info("Returned key [keyNumber={}]", response.getKeyNumber());
                        getContainer().remove(item);
                     },
                     e -> LOGGER.error("Erroneous state - could not return key " + item.getKeyNumber(), e)
               ));
            }
         };
      });
      unassignmentColumn.setSortable(false);
      unassignmentColumn.setMinWidth(110);
      unassignmentColumn.setMaxWidth(110);

      getColumns().addAll(List.of(keyNumberColumn, dateTimeColumn, userColumn, descriptionColumn, unassignmentColumn));
      getSortOrder().add(dateTimeColumn);
      setStyle("-fx-font-size:15; -fx-font-weight: 600; -fx-alignment: center");
      setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
      setFocusTraversable(false);
   }
}
