package lt.pavilonis.keymanager.ui.keyassignment;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import lt.pavilonis.keymanager.MessageSourceAdapter;

import java.util.function.Consumer;
import java.util.stream.Stream;

final class KeyAssignmentFilterPanel extends HBox {

   private final Consumer<KeyAssignmentFilter> filterConsumer;
   private final TextField keyNumberField = new TextField();
   private final TextField nameField = new TextField();

   KeyAssignmentFilterPanel(MessageSourceAdapter messages, Consumer<KeyAssignmentFilter> filterConsumer) {
      this.filterConsumer = filterConsumer;
      addKeyListeners();

      getChildren().addAll(
            new Label(messages.get(this, "keyNumber")), keyNumberField,
            new Label(messages.get(this, "name")), nameField,
            createSearchButton(messages)
      );
      setSpacing(15);
      setAlignment(Pos.CENTER_LEFT);
   }

   private void addKeyListeners() {
      EventHandler<KeyEvent> handler = event -> {
         if (event.getCode() == KeyCode.ENTER) {
            action();
         }
      };

      Stream.of(nameField, keyNumberField).forEach(field -> {
         field.setOnKeyReleased(handler);
         field.setPrefWidth(138);
      });
   }

   private Button createSearchButton(MessageSourceAdapter messages) {
      var searchButton = new Button(
            messages.get(this, "filter"),
            new ImageView(new Image("images/flat-find-16.png"))
      );
      searchButton.setOnAction(event -> action());
      return searchButton;
   }

   KeyAssignmentFilter getFilter() {
      return new KeyAssignmentFilter(keyNumberField.getText(), nameField.getText());
   }

   void reset() {
      keyNumberField.clear();
      nameField.clear();
   }

   void action() {
      filterConsumer.accept(getFilter());
   }

   public void focus() {
      keyNumberField.requestFocus();
   }
}
