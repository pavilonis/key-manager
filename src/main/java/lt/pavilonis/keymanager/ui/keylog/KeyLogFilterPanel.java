package lt.pavilonis.keymanager.ui.keylog;

import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;
import lt.pavilonis.keymanager.MessageSourceAdapter;
import lt.pavilonis.keymanager.Spring;
import lt.pavilonis.keymanager.ui.AbstractFilterPanel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

final class KeyLogFilterPanel extends AbstractFilterPanel<KeyLogFilter> {

   private final static StringConverter<LocalDate> LOCAL_DATE_CONVERTER = new StringConverter<>() {
      @Override
      public String toString(LocalDate object) {
         return DateTimeFormatter.ISO_LOCAL_DATE.format(object);
      }

      @Override
      public LocalDate fromString(String string) {
         throw new IllegalStateException("Not implemented - was not needed yet");
      }
   };

   private DatePicker periodStart;
   private DatePicker periodEnd;
   private TextField keyNumberField;
   private TextField nameField;
   private ComboBox<KeyAction> actionComboBox;

   KeyLogFilterPanel() {
      setAlignment(Pos.CENTER_LEFT);
      setSpacing(15);
   }

   @Override
   public void reset() {
      keyNumberField.clear();
      nameField.clear();
      periodStart.setValue(LocalDate.now().minusWeeks(1));
      periodEnd.setValue(LocalDate.now());
      actionComboBox.setValue(KeyAction.ALL);
   }

   @Override
   public List<Node> getPanelElements() {
      MessageSourceAdapter messages = Spring.getBean(MessageSourceAdapter.class);
      actionComboBox = createActionCombo(messages);

      periodStart = new DatePicker();
      periodStart.setConverter(LOCAL_DATE_CONVERTER);

      periodEnd = new DatePicker();
      periodEnd.setConverter(LOCAL_DATE_CONVERTER);

      keyNumberField = new TextField();
      nameField = new TextField();

      Stream.of(periodStart, periodEnd, actionComboBox, keyNumberField, nameField)
            .forEach(f -> f.setPrefWidth(138));

      return List.of(
            new Label(messages.get(this, "keyNumber")), keyNumberField,
            new Label(messages.get(this, "name")), nameField,
            actionComboBox,
            periodStart,
            periodEnd
      );
   }

   private ComboBox<KeyAction> createActionCombo(MessageSourceAdapter messages) {
      ComboBox<KeyAction> combo = new ComboBox<>(FXCollections.observableArrayList(KeyAction.values()));
      combo.setConverter(new StringConverter<>() {
         @Override
         public String toString(KeyAction object) {
            return messages.get(object, object.name());
         }

         @Override
         public KeyAction fromString(String string) {
            throw new IllegalStateException("Not implemented - was not needed yet");
         }
      });
      return combo;
   }

   @Override
   public void addSearchListener(EventHandler<Event> handler) {
      super.addSearchListener(handler);
      EventHandler<KeyEvent> eventHandler = (KeyEvent event) -> {
         if (event.getCode() == KeyCode.ENTER) {
            handler.handle(event);
         }
      };

      Stream.of(periodStart, periodEnd, actionComboBox, keyNumberField, nameField)
            .forEach(field -> field.setOnKeyReleased(eventHandler));
   }

   @Override
   public KeyLogFilter getFilter() {
      return new KeyLogFilter(
            periodStart.getValue(),
            periodEnd.getValue(),
            actionComboBox.getValue(),
            keyNumberField.getText(),
            nameField.getText()
      );
   }

   @Override
   public void focus() {
      keyNumberField.requestFocus();
   }
}
