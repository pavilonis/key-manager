package lt.pavilonis.scan.cmm.client.ui.classusage;

import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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

   private final ComboBox<String> rolesCombo = new ComboBox<>();
   private final TextField textField = new TextField();
   private final Button searchButton;

   public ClassroomUsageFilterPanel(MessageSourceAdapter messages, WsRestClient rest) {
      rest.loadRoles(roles ->
            roles.ifPresent(r ->
                  this.rolesCombo.setItems(FXCollections.observableArrayList(r))));


      this.searchButton = new Button(
            messages.get(this, "filter"),
            new ImageView(new Image("images/flat-find-16.png"))
      );

      textField.setPrefWidth(138);
      getChildren().addAll(
            new Label(messages.get(this, "text")),
            textField,
            new Label(messages.get(this, "role")),
            rolesCombo,
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
      return new ClassroomUsageFilter(textField.getText(), rolesCombo.getValue());
   }

   public void focus() {
      textField.requestFocus();
   }
}
