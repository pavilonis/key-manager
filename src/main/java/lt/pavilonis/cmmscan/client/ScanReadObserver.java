package lt.pavilonis.cmmscan.client;

import javafx.application.Platform;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import lt.pavilonis.cmmscan.client.representation.ScanLogRepresentation;
import lt.pavilonis.cmmscan.client.ui.ScanLogTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

@Component
public class ScanReadObserver implements Observer {
   private static final Logger LOG = LoggerFactory.getLogger(ScanReadObserver.class);

   @Value(("${scanner.id}"))
   private int scannerId;

   @Autowired
   private ApiRestClient wsClient;

   @Autowired
   private ScanLogTab scanLogTab;

   @Override
   public void update(Observable o, Object arg) {
      LOG.debug("Sending scan request [scannerId={}, cardCode={}]", scannerId, String.valueOf(arg));

      Optional<ScanLogRepresentation> response = wsClient.scan(scannerId, arg.toString());
      if (response.isPresent()) {
         LOG.debug("Response [user={}]", response.get().user.firstName + " " + response.get().user.lastName);
         scanLogTab.addElement(response.get());

      } else {
         displayWarning();
      }
   }

   private void displayWarning() {
      Text textNode = new Text("Error: " + wsClient.getLastErrorMessage().orElse("Unknown"));
      textNode.setFont(Font.font(null, FontWeight.BOLD, 50));
      Platform.runLater(() -> App.rootPane.getChildren().add(textNode));
   }
}
