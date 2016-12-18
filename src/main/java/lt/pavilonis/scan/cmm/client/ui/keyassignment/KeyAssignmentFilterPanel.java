package lt.pavilonis.scan.cmm.client.ui.keyassignment;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import org.springframework.context.MessageSource;

import java.util.function.Consumer;

public final class KeyAssignmentFilterPanel extends HBox {

   private static final String CLASS_NAME = KeyAssignmentFilterPanel.class.getSimpleName();
   private final Consumer<String> searchStringConsumer;
   private final TextField textField = new TextField();

   public KeyAssignmentFilterPanel(MessageSource messageSource, Consumer<String> searchStringConsumer) {
      this.searchStringConsumer = searchStringConsumer;

      Button searchButton = new Button(
            messageSource.getMessage(CLASS_NAME + ".search", null, null),
            new ImageView(new Image("images/flat-find-16.png")));
      searchButton.setOnAction(event -> action());

      textField.requestFocus();
      textField.setOnKeyReleased(event -> {
         if (event.getCode() == KeyCode.ENTER) {
            action();
         }
      });

      getChildren().addAll(textField, searchButton);
      setSpacing(15);
   }

   void reset() {
      textField.clear();
   }

   void action() {
      searchStringConsumer.accept(textField.getText());
   }
}
