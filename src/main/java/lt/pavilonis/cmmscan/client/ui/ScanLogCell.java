package lt.pavilonis.cmmscan.client.ui;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import lt.pavilonis.cmmscan.client.representation.ScanLogRepresentation;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;

import java.time.format.DateTimeFormatter;
import java.util.function.BiConsumer;

import static org.slf4j.LoggerFactory.getLogger;

public class ScanLogCell extends HBox {
   private static final Logger LOG = getLogger(ScanLogCell.class.getSimpleName());
   private final static DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

   private final TextField keyNumberField = new TextField();
   private final Button addKeyButton = new Button(" + ");

   public ScanLogCell(ScanLogRepresentation representation, BiConsumer<String, Integer> buttonClickConsumer) {
      setSpacing(15);
      setAlignment(Pos.CENTER_LEFT);
      keyNumberField.setPrefWidth(70);
      addKeyButton.setPrefWidth(50);

      addKeyButton.setOnAction(action -> addKey(representation, buttonClickConsumer));

      keyNumberField.setOnKeyReleased(event -> {
         if (event.getCode() == KeyCode.ENTER) {
            addKey(representation, buttonClickConsumer);
         }
      });

      this.getChildren().addAll(
            text(TIME_FORMAT.format(representation.dateTime), 80),
            text(representation.user.firstName + " " + representation.user.lastName, 200),
            text(representation.user.description, 200),
            keyNumberField,
            addKeyButton
      );
      controlsVisible(false);
   }

   private void addKey(ScanLogRepresentation representation, BiConsumer<String, Integer> buttonClickConsumer) {
      String text = keyNumberField.getCharacters().toString();
      if (NumberUtils.isDigits(text)) {
         buttonClickConsumer.accept(representation.user.cardCode, Integer.parseInt(text));
      } else {
         LOG.error("Bad number format: {}", text);
      }
   }

   private Text text(String content, int width) {
      Text text = new Text(content);
      text.setFont(Font.font(null, FontWeight.SEMI_BOLD, 16));
      text.setWrappingWidth(width);
      return text;
   }

   void activate() {
      controlsVisible(true);
      Platform.runLater(keyNumberField::requestFocus);
   }

   void deactivate() {
      controlsVisible(false);
      keyNumberField.clear();
   }

   private void controlsVisible(boolean bool) {
      addKeyButton.setVisible(bool);
      keyNumberField.setVisible(bool);
   }
}
