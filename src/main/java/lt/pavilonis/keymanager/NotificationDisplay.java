package lt.pavilonis.keymanager;

import javafx.application.Platform;
import javafx.scene.Node;
import lt.pavilonis.keymanager.ui.WarningBox;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

public interface NotificationDisplay {

   default void warn(String mainMessage, Exception exception) {
      String exceptionMessage = extractMessage(exception);
      var warningBox = new WarningBox(mainMessage == null ? exceptionMessage : mainMessage + "\n" + exceptionMessage);

      warningBox.setOnMouseClicked(click -> getStackPaneChildren().remove(warningBox));
      getStackPaneChildren().add(warningBox);
   }

   private String extractMessage(Exception e) {
      if (e instanceof HttpClientErrorException) {
         return "Unexpected HTTP response code: " + ((HttpClientErrorException) e).getStatusCode();

      } else if (e instanceof ResourceAccessException) {
         return "Could not access resource: " + (e.getCause() == null ? e.getMessage() : e.getCause().getMessage());

      } else {
         return "Unknown error: " + (e == null ? "" : e.getMessage());
      }
   }

   default void clearWarnings() {
      Platform.runLater(() -> getStackPaneChildren().removeIf(child -> child instanceof WarningBox));
   }

   List<Node> getStackPaneChildren();
}
