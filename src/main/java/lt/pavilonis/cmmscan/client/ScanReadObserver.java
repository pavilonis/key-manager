package lt.pavilonis.cmmscan.client;

import lt.pavilonis.cmmscan.client.representation.ScanLogRepresentation;
import lt.pavilonis.cmmscan.client.ui.scanlog.ScanLogList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

@Component
public class ScanReadObserver implements Observer {
   private static final Logger LOG = LoggerFactory.getLogger(ScanReadObserver.class.getSimpleName());

   @Autowired
   private WsRestClient wsClient;

   @Autowired
   private ScanLogList scanLogList;

   @Override
   public void update(Observable o, Object arg) {
      LOG.info("Sending scan request [cardCode={}]", String.valueOf(arg));

      Optional<ScanLogRepresentation> response = wsClient.writeScanLog(arg.toString());
      if (response.isPresent()) {
         LOG.info("Response [user={}]", response.get().user.firstName + " " + response.get().user.lastName);
         scanLogList.addElement(response.get());
      } else {
         App.displayWarning("Can not write scan log");
      }
   }
}
