package lt.pavilonis.cmmscan.client;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class WarningBox extends HBox {

   private final StackPane rootPaneReference;
   private final Runnable selfRemoval;

   public WarningBox(StackPane rootPaneReference) {
      this.rootPaneReference = rootPaneReference;
      this.selfRemoval = () -> this.rootPaneReference.getChildren().remove(this);
      setOnMouseClicked(click -> Platform.runLater(selfRemoval));
   }

   public void warning(String text) {
      Text textNode = new Text("Error: " + text);
      textNode.setFont(Font.font(null, FontWeight.BOLD, 50));

      Platform.runLater(() -> {
         selfRemoval.run();

         getChildren().clear();
         getChildren().add(textNode);

         rootPaneReference.getChildren().add(this);
      });

      setAlignment(Pos.CENTER);
   }

   public void hide() {
      Platform.runLater(selfRemoval);
   }
}
