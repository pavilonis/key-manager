package lt.pavilonis.scan.cmm.client.ui.scanlog;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import lt.pavilonis.scan.cmm.client.App;
import lt.pavilonis.scan.cmm.client.representation.KeyRepresentation;
import lt.pavilonis.scan.cmm.client.representation.ScanLogRepresentation;
import lt.pavilonis.scan.cmm.client.service.MessageSourceAdapter;
import lt.pavilonis.scan.cmm.client.service.WsRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ScanLogList extends ListView<ScanLogListElement> {

   private static final String CLASS_NAME = ScanLogList.class.getSimpleName();
   private static final int POSITION_FIRST = 0;
   private static final int QUEUE_LENGTH = 99;
   private final ObservableList<ScanLogListElement> container = FXCollections.observableArrayList();

   @Autowired
   private WsRestClient wsClient;

   @Autowired
   private ScanLogKeyList keyListView;

   @Autowired
   private PhotoView photoView;

   @Autowired
   private MessageSourceAdapter messages;

   public ScanLogList() {
      setItems(container);
      setFocusTraversable(false);

      getSelectionModel().selectedItemProperty().addListener((observable, oldElement, newElement) -> {
         Platform.runLater(() -> {
            photoView.update(newElement.getUser().base16photo);

            if (oldElement != null) {
               oldElement.deactivate();
            }
            newElement.activate();

            Optional<List<KeyRepresentation>> keys = wsClient.userKeysAssigned(newElement.getUser().cardCode);
            if (keys.isPresent()) {
               keyListView.reload(keys.get());
            } else {
               App.displayWarning(messages.get(this, "canNotLoadUserAssignedKeys"));
            }
         });
      });
   }

   public void addElement(ScanLogRepresentation representation) {
      Platform.runLater(() -> {
         if (container.size() > QUEUE_LENGTH) {
            container.remove(container.size() - 1);
         }
         container.add(POSITION_FIRST, new ScanLogListElement(representation, (cardCode, keyNumber) -> {
            Optional<KeyRepresentation> response = wsClient.assignKey(cardCode, keyNumber);
            if (response.isPresent()) {
               keyListView.append(response.get());
            } else {
               App.displayWarning(messages.get(this, "canNotAssignKey"));
            }
         }));
      });
   }
}
