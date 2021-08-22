package lt.pavilonis.keymanager.ui.classusage;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import lt.pavilonis.keymanager.ui.AbstractFilterPanel;

import java.util.List;

final class ClassroomUsageFilterPanel extends AbstractFilterPanel<ClassroomUsageFilter> {

   private TextField textField;

   ClassroomUsageFilterPanel() {
      setAlignment(Pos.CENTER_LEFT);
      setSpacing(15);
   }

   @Override
   public List<Node> getPanelElements() {
      textField = new TextField();
      textField.setPrefWidth(138);
      return List.of(new Label(messages.get("ClassroomUsageFilterPanel.text")), textField);
   }

   @Override
   public void reset() {
      textField.clear();
   }

   @Override
   public void addSearchListener(EventHandler<Event> handler) {
      super.addSearchListener(handler);
      textField.setOnKeyReleased(event -> {
         if (event.getCode() == KeyCode.ENTER) {
            handler.handle(event);
         }
      });
   }

   @Override
   public ClassroomUsageFilter getFilter() {
      return new ClassroomUsageFilter(textField.getText());
   }

   @Override
   public void focus() {
      textField.requestFocus();
   }
}
