package lt.pavilonis.scan.cmm.client.ui.scanlog;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import lt.pavilonis.scan.cmm.client.App;
import lt.pavilonis.scan.cmm.client.service.WsRestClient;
import lt.pavilonis.scan.cmm.client.representation.KeyRepresentation;
import lt.pavilonis.scan.cmm.client.representation.ScanLogRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ScanLogList extends ListView<ScanLogListCell> {

   private static final int POSITION_FIRST = 0;
   private static final int QUEUE_LENGTH = 99;
   private final ObservableList<ScanLogListCell> container = FXCollections.observableArrayList();

   @Autowired
   private WsRestClient wsClient;

   @Autowired
   private ScanLogKeyList keyListView;

   public ScanLogList() {
      setItems(container);
      setFocusTraversable(false);

      getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
         Platform.runLater(() -> {
            if (oldValue != null) {
               oldValue.deactivate();
            }
            newValue.activate();
            Optional<List<KeyRepresentation>> keys = wsClient.userKeysAssigned(newValue.getCardCode());
            if (keys.isPresent()) {
               keyListView.reload(keys.get());
            } else {
               App.displayWarning("Can not load user assigned keys!");
            }
         });
      });
//      setStyle("-fx-font-size:15; -fx-font-weight: 600; -fx-alignment: center");
   }

   public void addElement(ScanLogRepresentation representation) {
      Platform.runLater(() -> {
         if (container.size() > QUEUE_LENGTH) {
            container.remove(container.size() - 1);
         }
         container.add(POSITION_FIRST, new ScanLogListCell(
               representation,
               (cardCode, keyNumber) -> {
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
