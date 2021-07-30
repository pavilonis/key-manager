package lt.pavilonis.scan.cmm.client.ui.scanlog;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import lt.pavilonis.scan.cmm.client.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;

import java.time.format.DateTimeFormatter;
import java.util.function.BiConsumer;

import static org.slf4j.LoggerFactory.getLogger;

final class ScanLogListElement extends HBox {

   private static final Logger LOGGER = getLogger(ScanLogListElement.class.getSimpleName());
   private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
   private final TextField keyNumberField = new TextField();
   private final Button addKeyButton = new Button(null, new ImageView(new Image("images/flat-arrow-up-24.png")));
   private final HBox controls = new HBox(keyNumberField, addKeyButton);
   private final User user;

   ScanLogListElement(ScanLog representation, BiConsumer<String, Integer> buttonClickConsumer) {
      User user = representation.user;
      this.user = user;
      setSpacing(10);
      setAlignment(Pos.CENTER_LEFT);
      keyNumberField.setPrefWidth(70);
      addKeyButton.setPrefWidth(70);
      controls.setSpacing(5);
      controls.setPrefWidth(125);
      controls.setAlignment(Pos.CENTER_RIGHT);

      addKeyButton.setOnAction(action -> assignKey(representation, buttonClickConsumer));

      keyNumberField.setOnKeyReleased(event -> {
         if (event.getCode() == KeyCode.ENTER) {
            assignKey(representation, buttonClickConsumer);
         }
      });

      boolean pupil = StringUtils.isNotBlank(user.getRole()) && StringUtils.containsIgnoreCase(user.getRole(), "mokinys");
      Label name = text(user.getName(), (pupil ? 208 : 250));
      Label description = text(user.getGroup(), 240);

      getChildren().add(text(TIME_FORMAT.format(representation.dateTime), 80));
      if (pupil) {
         ImageView studentIcon = new ImageView(new Image("images/favorite-star-24.png"));
         studentIcon.setFitWidth(24);
         studentIcon.setFitHeight(24);
         getChildren().add(studentIcon);
      }
      getChildren().addAll(name, description, controls);

      setHgrow(name, Priority.ALWAYS);
      setHgrow(description, Priority.ALWAYS);
      setHgrow(controls, Priority.ALWAYS);
      controls.setVisible(false);
   }

   private void assignKey(ScanLog representation, BiConsumer<String, Integer> buttonClickConsumer) {
      String text = keyNumberField.getCharacters().toString();
      if (NumberUtils.isDigits(text)) {
         buttonClickConsumer.accept(representation.user.getCardCode(), Integer.parseInt(text));
         keyNumberField.setText(null);
         keyNumberField.requestFocus();
      } else {
         LOGGER.error("Bad number format: {}", text);
      }
   }

   private Label text(String content, int width) {
      var text = new Label(content);
      text.setFont(Font.font(null, FontWeight.SEMI_BOLD, 15));
      text.setMinWidth(width);
      text.setMaxWidth(width + 200);
      return text;
   }

   void activate() {
      controls.setVisible(true);
      Platform.runLater(keyNumberField::requestFocus);
   }

   void deactivate() {
      controls.setVisible(false);
      keyNumberField.clear();
   }

   User getUser() {
      return this.user;
   }
}
