package lt.pavilonis.scan.cmm.client.ui.scanlog;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import lt.pavilonis.scan.cmm.client.App;
import lt.pavilonis.scan.cmm.client.representation.KeyRepresentation;
import lt.pavilonis.scan.cmm.client.representation.ScanLogRepresentation;
import lt.pavilonis.scan.cmm.client.representation.UserRepresentation;
import lt.pavilonis.scan.cmm.client.service.MessageSourceAdapter;
import lt.pavilonis.scan.cmm.client.service.WsRestClient;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.google.common.collect.Lists.newArrayList;
import static org.slf4j.LoggerFactory.getLogger;

@Component
public class ScanLogList extends ListView<ScanLogListElement> {
   private static final Logger LOG = getLogger(ScanLogList.class.getSimpleName());
   private static final int POSITION_FIRST = 0;
   private static final int QUEUE_LENGTH = 99;
   private final ObservableList<ScanLogListElement> container = FXCollections.observableArrayList();

   @Autowired
   private WsRestClient wsClient;

   @Autowired
   private ScanLogKeyList scanLogKeyList;

   @Autowired
   private PhotoView photoView;

   @Autowired
   private MessageSourceAdapter messages;

   public ScanLogList() {
      setItems(container);
      setFocusTraversable(false);

      getSelectionModel().selectedItemProperty().addListener((observable, oldElement, newElement) -> {
         Platform.runLater(() -> {
            UserRepresentation user = newElement.getUser();
            photoView.update(user.base16photo);

            if (oldElement != null) {
               oldElement.deactivate();
            }
            newElement.activate();
            scanLogKeyList.updateContainer(user.cardCode);
         });
      });
   }

   public void addElement(ScanLogRepresentation representation) {
      Platform.runLater(() -> {
         if (container.size() > QUEUE_LENGTH) {
            container.remove(container.size() - 1);
         }
         container.add(POSITION_FIRST, new ScanLogListElement(representation, (cardCode, keyNumber) ->
               wsClient.assignKey(cardCode, keyNumber, response -> {
                  if (response.isPresent()) {
                     KeyRepresentation key = response.get();
                     scanLogKeyList.append(key);
                     LOG.info("Key {} assigned to cardCode {}", key.getKeyNumber(), key.getUser().cardCode);
                  } else {
                     App.displayWarning(messages.get(this, "canNotAssignKey"));
                  }
               })));
      });
   }
}
