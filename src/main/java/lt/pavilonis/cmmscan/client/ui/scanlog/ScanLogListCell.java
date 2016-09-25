package lt.pavilonis.cmmscan.client.ui.scanlog;

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
import lt.pavilonis.cmmscan.client.representation.ScanLogRepresentation;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;

import java.time.format.DateTimeFormatter;
import java.util.function.BiConsumer;

import static org.slf4j.LoggerFactory.getLogger;

public class ScanLogListCell extends HBox {
   private static final Logger LOG = getLogger(ScanLogListCell.class.getSimpleName());
   private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

   private final TextField keyNumberField = new TextField();
   private final Button addKeyButton = new Button(null, new ImageView(new Image("images/add-icon-16.png")));
   private final HBox controls = new HBox(keyNumberField, addKeyButton);
   private final String cardCode;

   public ScanLogListCell(ScanLogRepresentation representation, BiConsumer<String, Integer> buttonClickConsumer) {
      this.cardCode = representation.user.cardCode;
      setSpacing(10);
      setAlignment(Pos.CENTER_LEFT);
      keyNumberField.setPrefWidth(70);
      addKeyButton.setPrefWidth(50);
      controls.setSpacing(5);
      controls.setPrefWidth(125);
      controls.setAlignment(Pos.CENTER_RIGHT);

      addKeyButton.setOnAction(action -> assignKey(representation, buttonClickConsumer));

      keyNumberField.setOnKeyReleased(event -> {
         if (event.getCode() == KeyCode.ENTER) {
            assignKey(representation, buttonClickConsumer);
         }
      });

      Label name = text(representation.user.firstName + " " + representation.user.lastName, 260);
      Label description = text(representation.user.description, 260);
      getChildren().addAll(
            text(TIME_FORMAT.format(representation.dateTime), 80),
            name,
            description,
            controls
      );

      setHgrow(name, Priority.ALWAYS);
      setHgrow(description, Priority.ALWAYS);
      setHgrow(controls, Priority.ALWAYS);
      controls.setVisible(false);
   }

   private void assignKey(ScanLogRepresentation representation, BiConsumer<String, Integer> buttonClickConsumer) {
      String text = keyNumberField.getCharacters().toString();
      if (NumberUtils.isDigits(text)) {
         buttonClickConsumer.accept(representation.user.cardCode, Integer.parseInt(text));
         keyNumberField.setText(null);
         keyNumberField.requestFocus();
      } else {
         LOG.error("Bad number format: {}", text);
      }
   }

   private Label text(String content, int width) {
      Label text = new Label(content);
      text.setFont(Font.font(null, FontWeight.SEMI_BOLD, 15));
      text.setMinWidth(width);
      text.setMaxWidth(width + 100);
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

   public String getCardCode() {
      return cardCode;
   }
}
