package lt.pavilonis.keymanager.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import lt.pavilonis.keymanager.MessageSourceAdapter;
import lt.pavilonis.keymanager.Spring;
import lt.pavilonis.keymanager.ui.scanlog.UserCreationForm;
import lt.pavilonis.keymanager.util.ClipboardUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

public class NotificationDisplay {

   // This constant should match same constant on server side
   public static final String UNKNOWN_USER = "Unknown user";
   private static final double POPUP_RATIO = 0.7;
   private final MessageSourceAdapter messages = Spring.getBean(MessageSourceAdapter.class);
   private final StackPane rootStackPane;

   public NotificationDisplay(StackPane rootStackPane) {
      this.rootStackPane = rootStackPane;
   }

   public void warn(String mainMessage, Exception exception) {
      String exceptionMessage = extractMessage(exception);
      String notificationMessage = mainMessage == null ? exceptionMessage : mainMessage + "\n" + exceptionMessage;
      var warningBox = new NotificationBox(notificationMessage)
            .yellow();

      warningBox.setOnMouseClicked(click -> rootStackPane.getChildren().remove(warningBox));
      rootStackPane.getChildren().add(warningBox);
   }

   public void notify(String mainMessage) {
      var infoBox = new NotificationBox(mainMessage)
            .green();

      infoBox.setOnMouseClicked(click -> rootStackPane.getChildren().remove(infoBox));
      rootStackPane.getChildren().add(infoBox);
   }

   public void show(UserCreationForm form) {
      double maxWidth = rootStackPane.getWidth() * POPUP_RATIO;
      double maxHeight = rootStackPane.getHeight() * POPUP_RATIO;
      form.setMaxSize(maxWidth, maxHeight);

      form.setCloseAction(() -> rootStackPane.getChildren().remove(form));
      rootStackPane.getChildren().add(form);
   }

   private String extractMessage(Exception e) {
      if (e instanceof HttpClientErrorException) {
         var httpException = (HttpClientErrorException) e;
         String body = httpException.getResponseBodyAsString();

         return StringUtils.hasText(body) && body.contains(UNKNOWN_USER)
               ? handleUnknownUserResponse(body)
               : "Unexpected HTTP response code: " + httpException.getStatusCode();

      } else if (e instanceof ResourceAccessException) {
         return "Could not access resource: " + (e.getCause() == null ? e.getMessage() : e.getCause().getMessage());

      } else {
         return "Unknown error: " + (e == null ? "" : e.getMessage());
      }
   }

   private String handleUnknownUserResponse(String body) {
      String unknownCardCode = body.replace(UNKNOWN_USER, "").strip();
      ClipboardUtils.addToClipboard(unknownCardCode);
      return body.replace(UNKNOWN_USER, messages.get("NotificationDisplay.unknownUser"));
   }

   public void clear() {
      Platform.runLater(() -> rootStackPane.getChildren().removeIf(child -> child instanceof NotificationBox));
   }

   private static final class NotificationBox extends HBox {

      public NotificationBox(String text) {
         setMaxSize(1, 1);
         setPadding(new Insets(10));

         var textNode = new Text(text);
         textNode.setStyle("-fx-padding: 10");
         textNode.setFont(Font.font(null, FontWeight.BOLD, 40));

         setStyle("-fx-background-radius: 10 10 10 10;");
         getChildren().add(textNode);
         setAlignment(Pos.CENTER);
      }

      NotificationBox yellow() {
         setStyle("-fx-background-color: #ffc550;");
         return this;
      }

      NotificationBox green() {
         setStyle("-fx-background-color: #77C5A2;");
         return this;
      }
   }
}
