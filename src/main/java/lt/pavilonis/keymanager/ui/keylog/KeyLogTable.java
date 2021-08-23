package lt.pavilonis.keymanager.ui.keylog;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lt.pavilonis.keymanager.MessageSourceAdapter;
import lt.pavilonis.keymanager.Spring;
import lt.pavilonis.keymanager.User;
import lt.pavilonis.keymanager.ui.AbstractTable;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.util.Comparator.comparing;

public final class KeyLogTable extends AbstractTable<Key> {

   public static final String STYLE_STUDENT = "-fx-background-color: rgba(255, 164, 0, 0.15)";
   private static final String ICON_ASSINGED = "images/flat-arrow-up-24.png";
   private static final String ICON_UNASSIGNED = "images/flat-arrow-down-24.png";
   private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm");

   public KeyLogTable() {
      setItems(getContainer());

      MessageSourceAdapter messages = Spring.getBean(MessageSourceAdapter.class);
      var keyNumberColumn = new TableColumn<Key, Integer>(messages.get(this, ("keyNumber")));
      keyNumberColumn.setMinWidth(120);
      keyNumberColumn.setMaxWidth(120);
      keyNumberColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getKeyNumber()));

      var dateTimeColumn = new TableColumn<Key, Key>(messages.get(this, ("dateTime")));
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
      dateTimeColumn.setComparator(comparing(Key::getDateTime));
      dateTimeColumn.setSortType(TableColumn.SortType.DESCENDING);
      dateTimeColumn.setMinWidth(190);
      dateTimeColumn.setMaxWidth(190);

      var userColumn = new TableColumn<Key, String>(messages.get(this, "user"));
      userColumn.setCellValueFactory(param -> {
         User user = param.getValue().getUser();
         return new ReadOnlyObjectWrapper<>(user.getName());
      });

      var groupColumn = new TableColumn<Key, User>(messages.get(this, "group"));
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
               setText(item.getGroup());
               String role = item.getRole();
               if (StringUtils.hasText(role) && role.toLowerCase().contains("mokinys")) {
                  setStyle(STYLE_STUDENT);
               }
            }
         }
      });
      groupColumn.setComparator(comparing(User::getGroup));

      var actionColumn = new TableColumn<Key, KeyAction>(messages.get(this, "action"));
      actionColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getKeyAction()));
      actionColumn.setCellFactory(param -> new TableCell<Key, KeyAction>() {
         @Override
         protected void updateItem(KeyAction value, boolean empty) {
            super.updateItem(value, empty);
            if (value == null || empty) {
               setText(null);
               setGraphic(null);
               setStyle("");
               return;
            }

            switch (value) {
               case ASSIGNED:
                  this.setGraphic(new ImageView(new Image(ICON_ASSINGED)));
                  break;
               case UNASSIGNED:
                  this.setGraphic(new ImageView(new Image(ICON_UNASSIGNED)));
                  break;
               default:
                  throw new IllegalStateException("Not expected KeyAction value (should not happen)");
            }
            setAlignment(Pos.CENTER);
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
}
