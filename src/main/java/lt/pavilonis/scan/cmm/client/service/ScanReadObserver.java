package lt.pavilonis.scan.cmm.client.service;

import lt.pavilonis.scan.cmm.client.App;
import lt.pavilonis.scan.cmm.client.representation.ScanLogRepresentation;
import lt.pavilonis.scan.cmm.client.ui.scanlog.ScanLogList;
import lt.pavilonis.scan.service.ScannerReadEventObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

   @Override
   protected void consumeScannerInput(String string) {
      Consumer<Optional<ScanLogRepresentation>> consumer = response -> {
         if (response.isPresent()) {
            LOG.info("Response [user={}]", response.get().user.firstName + " " + response.get().user.lastName);
            scanLogList.addElement(response.get());
         } else {
            App.displayWarning(messages.get(this, "canNotWriteScanLog"));
         }
      };

      wsClient.writeScanLog(string + "000000", consumer);
   }
}
