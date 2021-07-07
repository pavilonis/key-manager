package lt.pavilonis.scan.cmm.client;

import javafx.scene.input.KeyCode;
import lt.pavilonis.scan.cmm.client.ui.scanlog.ScanLog;
import lt.pavilonis.scan.cmm.client.ui.scanlog.ScanLogList;
import lt.pavilonis.scan.service.ScannerReadEventObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.function.Consumer;

@Component
public class ScanReadObserver extends ScannerReadEventObserver {
   private static final Logger LOG = LoggerFactory.getLogger(ScanReadObserver.class.getSimpleName());

   @Autowired
   private WsRestClient wsClient;

   @Autowired
   private ScanLogList scanLogList;

   @Autowired
   private MessageSourceAdapter messages;

   @PostConstruct
   public void mockScanEvent() {
      App.ROOT_PANE.setOnKeyReleased(event -> {
         if (event.getCode() == KeyCode.F12) {
            consumeScannerInput("BE2AF66B");
         }
      });
   }

   @Override
   protected void consumeScannerInput(String string) {
      Consumer<Optional<ScanLog>> consumer = response -> {
         if (response.isPresent()) {
            LOG.info("Response [user={}]", response.get().user.name);
            scanLogList.addElement(response.get());
         } else {
            App.displayWarning(messages.get(this, "canNotWriteScanLog"));
         }
      };

      wsClient.writeScanLog(string + "000000", consumer);
   }
}
