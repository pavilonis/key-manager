package lt.pavilonis.keymanager.ui.classusage;

import javafx.event.Event;
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

final class ClassroomUsageFilterPanel extends HBox {

   private final TextField textField = new TextField();
   private final Button searchButton;

   ClassroomUsageFilterPanel(MessageSourceAdapter messages) {

      this.searchButton = new Button(
            messages.get(this, "filter"),
            new ImageView(new Image("images/flat-find-16.png"))
      );

      textField.setPrefWidth(138);
      getChildren().addAll(
            new Label(messages.get(this, "text")),
            textField,
            searchButton
      );
      setAlignment(Pos.CENTER_LEFT);
      setSpacing(15);
   }

   void reset() {
      textField.clear();
   }

   void addSearchListener(EventHandler<Event> handler) {
      EventHandler<KeyEvent> eventHandler = (KeyEvent event) -> {
         if (event.getCode() == KeyCode.ENTER) {
            handler.handle(event);
         }
      };

      textField.setOnKeyReleased(eventHandler);
      searchButton.setOnAction(handler::handle);
   }

   ClassroomUsageFilter getFilter() {
      return new ClassroomUsageFilter(textField.getText());
   }

   void focus() {
      textField.requestFocus();
   }
}
