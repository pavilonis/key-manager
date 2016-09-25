package lt.pavilonis.cmmscan.client.ui.keys;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;

final class FilterPanel extends HBox {

   public FilterPanel(Consumer<String> searchStringConsumer) {
      TextField textField = new TextField();
      Button searchButton = new Button("Search");

      getChildren().addAll(textField, searchButton);
      setSpacing(15);
      textField.requestFocus();

      searchButton.setOnAction(event -> searchStringConsumer.accept(textField.getText()));
      textField.setOnKeyReleased(event -> {
         if (event.getCode() == KeyCode.ENTER) {
            searchStringConsumer.accept(textField.getText());
         }
      });
   }
}
