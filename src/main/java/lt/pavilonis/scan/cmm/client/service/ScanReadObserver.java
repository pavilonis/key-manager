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

@Component
public class ScanReadObserver extends ScannerReadEventObserver {
   private static final Logger LOG = LoggerFactory.getLogger(ScanReadObserver.class.getSimpleName());

   @Autowired
   private WsRestClient wsClient;

   @Autowired
   private ScanLogList scanLogList;

   @Override
   protected void consumeScannerInput(String string) {
      LOG.info("Sending scan request [cardCode={}]", string);

      Optional<ScanLogRepresentation> response = wsClient.writeScanLog(string);
      if (response.isPresent()) {
         LOG.info("Response [user={}]", response.get().user.firstName + " " + response.get().user.lastName);
         scanLogList.addElement(response.get());
      } else {
         App.displayWarning("Can not write scan log");
      }
   }
}
