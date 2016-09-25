package lt.pavilonis.cmmscan.client.ui.scanlog;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.function.Consumer;

public class ScanLogKeyListCell extends HBox {

   private final Button removeKeyButton = new Button(null, new ImageView(new Image("images/delete-icon-16.png")));
   private final int keyNumber;

   public ScanLogKeyListCell(int keyNumber) {
      this.setSpacing(10);
      this.removeKeyButton.setPrefWidth(50);
      this.keyNumber = keyNumber;

      Label keyLabel = new Label(String.valueOf(keyNumber));
      keyLabel.setFont(Font.font(null, FontWeight.SEMI_BOLD, 15));
      keyLabel.setPrefWidth(80);

      this.getChildren().addAll(keyLabel, removeKeyButton);
      HBox.setHgrow(keyLabel, Priority.ALWAYS);
      this.setAlignment(Pos.CENTER_RIGHT);

   }

   void addRemoveKeyButtonListener(Consumer<Integer> keyConsumer) {
      removeKeyButton.setOnAction(event -> keyConsumer.accept(keyNumber));
   }
}
