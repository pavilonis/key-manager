package lt.pavilonis.cmmscan.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Observable;
import java.util.Observer;

@Component
public class ScanReadObserver implements Observer {
   private static final Logger LOG = LoggerFactory.getLogger(ScanReadObserver.class);

   @Override
   public void update(Observable o, Object arg) {
      LOG.debug(String.valueOf(arg));
   }
}
