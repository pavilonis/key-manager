package lt.pavilonis.cmmscan.client.ui.scanlog;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import lt.pavilonis.cmmscan.client.representation.KeyRepresentation;
import org.slf4j.Logger;

import java.time.format.DateTimeFormatter;
import java.util.function.BiConsumer;

import static org.slf4j.LoggerFactory.getLogger;

public class KeyCell extends HBox {
   private static final Logger LOG = getLogger(KeyCell.class.getSimpleName());
   private final static DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

   public KeyCell(KeyRepresentation representation){//}, BiConsumer<String, Integer> buttonClickConsumer) {
      setSpacing(10);
      setAlignment(Pos.CENTER_LEFT);
      Button removeKeyButton = new Button(" X ");
      removeKeyButton.setPrefWidth(50);

//      removeKeyButton.setOnAction(action -> addKey(representation, buttonClickConsumer));

      this.getChildren().addAll(
            text(String.valueOf(representation.keyNumber), 80),
            removeKeyButton
      );
   }

   private Text text(String content, int width) {
      Text text = new Text(content);
      text.setFont(Font.font(null, FontWeight.SEMI_BOLD, 15));
      text.setWrappingWidth(width);
      return text;
   }
}
