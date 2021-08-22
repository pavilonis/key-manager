package lt.pavilonis.keymanager.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

public class NotificationDisplay {

   private final StackPane rootStackPane;

   public NotificationDisplay(StackPane rootStackPane) {
      this.rootStackPane = rootStackPane;
   }

   public void warn(String mainMessage, Exception exception) {
      String exceptionMessage = extractMessage(exception);
      var warningBox = new WarningBox(mainMessage == null ? exceptionMessage : mainMessage + "\n" + exceptionMessage);

      warningBox.setOnMouseClicked(click -> rootStackPane.getChildren().remove(warningBox));
      rootStackPane.getChildren().add(warningBox);
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

   public void clear() {
      Platform.runLater(() -> rootStackPane.getChildren().removeIf(child -> child instanceof WarningBox));
   }

   private static final class WarningBox extends HBox {

      public WarningBox(String text) {
         setMaxSize(1, 1);
         setPadding(new Insets(10));

         var textNode = new Text(text);
         textNode.setStyle("-fx-padding: 10");
         textNode.setFont(Font.font(null, FontWeight.BOLD, 40));

         setStyle("-fx-background-color: #ffc550; -fx-background-radius: 10 10 10 10;");
         getChildren().add(textNode);
         setAlignment(Pos.CENTER);
      }
   }
}
