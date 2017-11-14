package lt.pavilonis.scan.cmm.client.ui.classusage;

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
import lt.pavilonis.scan.cmm.client.MessageSourceAdapter;
import lt.pavilonis.scan.cmm.client.WsRestClient;

public final class ClassroomUsageFilterPanel extends HBox {

   private final TextField textField = new TextField();
   private final Button searchButton;

   public ClassroomUsageFilterPanel(MessageSourceAdapter messages, WsRestClient rest) {

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

   public void reset() {
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

   public ClassroomUsageFilter getFilter() {
      return new ClassroomUsageFilter(textField.getText());
   }

   public void focus() {
      textField.requestFocus();
   }
}
