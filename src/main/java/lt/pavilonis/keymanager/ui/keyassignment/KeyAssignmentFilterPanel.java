package lt.pavilonis.keymanager.ui.keyassignment;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lt.pavilonis.keymanager.ui.AbstractFilterPanel;

import java.util.List;
import java.util.stream.Stream;

final class KeyAssignmentFilterPanel extends AbstractFilterPanel<KeyAssignmentFilter> {

   private TextField keyNumberField;
   private TextField nameField;

   KeyAssignmentFilterPanel() {
      setSpacing(15);
      setAlignment(Pos.CENTER_LEFT);
   }

   @Override
   public void addSearchListener(EventHandler<Event> handler) {
      super.addSearchListener(handler);
      EventHandler<KeyEvent> keyPressHandler = event -> {
         if (event.getCode() == KeyCode.ENTER) {
            handler.handle(event);
         }
      };

      Stream.of(nameField, keyNumberField).forEach(field -> {
         field.setOnKeyReleased(keyPressHandler);
         field.setPrefWidth(138);
      });
   }

   @Override
   public List<Node> getPanelElements() {
      keyNumberField = new TextField();
      nameField = new TextField();
      return List.of(
            new Label(messages.get(this, "keyNumber")),
            keyNumberField,
            new Label(messages.get(this, "name")),
            nameField
      );
   }

   @Override
   public KeyAssignmentFilter getFilter() {
      return new KeyAssignmentFilter(keyNumberField.getText(), nameField.getText());
   }

   @Override
   public void reset() {
      keyNumberField.clear();
      nameField.clear();
   }

   @Override
   public void focus() {
      keyNumberField.requestFocus();
   }
}
