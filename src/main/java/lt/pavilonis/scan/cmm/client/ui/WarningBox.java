package lt.pavilonis.scan.cmm.client.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.List;

public class WarningBox extends HBox {

   private final List<Node> rootElements;
   private final Runnable selfRemoval;

   public WarningBox(List<Node> rootElements) {
      this.rootElements = rootElements;
      this.selfRemoval = () -> this.rootElements.remove(this);
      setOnMouseClicked(click -> Platform.runLater(selfRemoval));
      setMaxSize(1, 1);
      setPadding(new Insets(10));
   }

   public void warning(String text) {
      Text textNode = new Text(text);
      textNode.setStyle("-fx-padding: 10");
      textNode.setFont(Font.font(null, FontWeight.BOLD, 40));
      setStyle("-fx-background-color: #ffc550; -fx-background-radius: 10 10 10 10;");
      Platform.runLater(() -> {
         selfRemoval.run();

         getChildren().clear();
         getChildren().add(textNode);

         rootElements.add(this);
      });

      setAlignment(Pos.CENTER);
   }

   public void hide() {
      Platform.runLater(selfRemoval);
   }
}
