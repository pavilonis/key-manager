package lt.pavilonis.cmmscan.client.ui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lt.pavilonis.cmmscan.client.representation.ScanLogRepresentation;
import org.springframework.stereotype.Component;

@Component
public class ScanLogTab extends Tab {

   private static final int POSITION_FIRST = 0;
   private static final int QUEUE_LENGTH = 99;
   private final ObservableList<ScanLogCell> logQueue = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

   public ScanLogTab() {
      super("Scan Log");
      setClosable(false);

      ListView<ScanLogCell> listView = new ListView<>(logQueue);
      listView.setFocusTraversable(false);

      listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
         if (oldValue != null) {
            oldValue.deactivate();
         }
         newValue.activate();
      });

      Image image = new Image("http://pensamientolateral.org/wp-content/uploads/2014/04/yo.jpg", 200, 200, true, false, true);
      ImageView imageView = new ImageView(image);

      ListView<String> stringListView = new ListView<>(FXCollections.observableArrayList("Key 1", "Key 2", "Key 3"));
      VBox rightColumn = new VBox(stringListView, imageView);
      VBox.setVgrow(stringListView, Priority.ALWAYS);
      VBox.setMargin(stringListView, new Insets(0, 0, 0, 15));
      VBox.setMargin(imageView, new Insets(15, 0, 0, 15));
      BorderPane parent = new BorderPane(
            listView,
            null,
            rightColumn,
            null,
            null
      );
      rightColumn.setPrefWidth(200);
      parent.setPadding(new Insets(15));
      setContent(parent);
   }

   public void addElement(ScanLogRepresentation representation) {
      Platform.runLater(() -> {
         if (logQueue.size() > QUEUE_LENGTH) {
            logQueue.remove(logQueue.size() - 1);
         }
         logQueue.add(
               POSITION_FIRST,
               new ScanLogCell(
                     representation,
                     (cardCode, keyNumber) -> System.out.println("TODO: assign key to user")
               )
         );
      });
   }
}
