package lt.pavilonis.cmmscan.client.ui;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import lt.pavilonis.cmmscan.client.ApiRestClient;
import lt.pavilonis.cmmscan.client.representation.KeyRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Arrays.asList;


@Component
public class KeyTab extends Tab {

   private final ObservableList<KeyRepresentation> tableDataSource = FXCollections.synchronizedObservableList(
         FXCollections.observableArrayList()
   );

   @Autowired
   private ApiRestClient wsClient;

   public KeyTab() {
      super("Keys");
      setClosable(false);

      TableColumn<KeyRepresentation, Integer> keyNumber = new TableColumn<>("Key Number");
      keyNumber.setMinWidth(120);
      keyNumber.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().keyNumber));

      TableColumn<KeyRepresentation, LocalDateTime> dateTime = new TableColumn<>("Time");
      dateTime.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().dateTime));
      dateTime.setMinWidth(120);

      TableColumn<KeyRepresentation, String> user = new TableColumn<>("User");
      user.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(
            param.getValue().user.firstName + " " + param.getValue().user.lastName
      ));
      user.setMinWidth(250);

      TableColumn<KeyRepresentation, String> description = new TableColumn<>("Description");
      description.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().user.description));
      description.setMinWidth(250);

      TableView<KeyRepresentation> keyTable = new TableView<>(tableDataSource);
      keyTable.getColumns().addAll(asList(keyNumber, dateTime, user, description));

      // TODO add delete button column

      HBox filterPanel = new HBox(new TextField(), new Button("Search"));
      filterPanel.setSpacing(15);
      BorderPane parent = new BorderPane(
            keyTable,
            filterPanel,
            null,
            null,
            null
      );

      BorderPane.setMargin(filterPanel, new Insets(0, 0, 15, 0));
      parent.setPadding(new Insets(15));
      setContent(parent);

      setOnSelectionChanged(event -> {
         if (isSelected()) {
            List<KeyRepresentation> keys = wsClient.keysTaken();
            tableDataSource.clear();
            tableDataSource.addAll(keys);
         }
      });
   }
}
