package lt.pavilonis.cmmscan.client.ui.scanlog;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lt.pavilonis.cmmscan.client.App;
import lt.pavilonis.cmmscan.client.WsRestClient;
import lt.pavilonis.cmmscan.client.representation.KeyRepresentation;
import lt.pavilonis.cmmscan.client.representation.ScanLogRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ScanLogTab extends Tab {

   private static final int POSITION_FIRST = 0;
   private static final int QUEUE_LENGTH = 99;
   private final ObservableList<ScanLogCell> logQueue = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

   private final WsRestClient wsClient;
   private final KeyListView keyListView;

   @Autowired
   public ScanLogTab(WsRestClient wsClient, KeyListView keyListView) {
      super("Scan Log");
      this.wsClient = wsClient;
      this.keyListView = keyListView;
      setClosable(false);

      ListView<ScanLogCell> listView = new ListView<>(logQueue);
      listView.setFocusTraversable(false);

      listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
         if (oldValue != null) {
            oldValue.deactivate();
         }
         newValue.activate();
         Platform.runLater(() -> {
            Optional<List<KeyRepresentation>> keys = this.wsClient.userKeysAssigned(newValue.getCardCode());
            if (keys.isPresent()) {
               keyListView.reload(keys.get());
            } else {
               App.displayWarning("Can not load user assigned keys!");
            }
         });
      });

      Image image = new Image("http://kaifolog.ru/uploads/posts/2013-04/1366274074_011.jpeg", 200, 200, true, false, true);
      ImageView imageView = new ImageView(image);

      VBox rightColumn = new VBox(keyListView, imageView);
      VBox.setVgrow(keyListView, Priority.ALWAYS);
      VBox.setMargin(keyListView, new Insets(0, 0, 0, 15));
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
               new ScanLogCell(representation, (cardCode, keyNumber) -> {
                  Optional<KeyRepresentation> response = wsClient.assignKey(cardCode, keyNumber);
                  if (response.isPresent()) {
                     keyListView.append(response.get());
                  } else {
                     App.displayWarning("Can not assign key!");
                  }
               })
         );
      });
   }
}
