package lt.pavilonis.keymanager.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class WarningBox extends HBox {

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
