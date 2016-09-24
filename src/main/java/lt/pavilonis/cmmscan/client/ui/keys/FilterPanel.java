package lt.pavilonis.cmmscan.client.ui.keys;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

final class FilterPanel extends HBox {
   private final TextField textField = new TextField();
   private final Button searchButton = new Button("Search");

   public FilterPanel() {
      getChildren().addAll(textField, searchButton);
      setSpacing(15);
   }
}
