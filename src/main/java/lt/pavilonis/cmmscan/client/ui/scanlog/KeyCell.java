package lt.pavilonis.cmmscan.client.ui.scanlog;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.function.Consumer;

public class KeyCell extends HBox {

   private final Button removeKeyButton = new Button(" X ");
   private final int keyNumber;

   public KeyCell(int keyNumber) {
      this.setSpacing(10);
      this.removeKeyButton.setPrefWidth(50);
      this.keyNumber = keyNumber;
      this.getChildren().addAll(
            text(String.valueOf(keyNumber), 80),
            removeKeyButton
      );
   }

   private Text text(String content, int width) {
      Text text = new Text(content);
      text.setFont(Font.font(null, FontWeight.SEMI_BOLD, 15));
      text.setWrappingWidth(width);
      return text;
   }

   void addRemoveKeyButtonListener(Consumer<Integer> keyConsumer) {
      removeKeyButton.setOnAction(event -> keyConsumer.accept(keyNumber));
   }
}
