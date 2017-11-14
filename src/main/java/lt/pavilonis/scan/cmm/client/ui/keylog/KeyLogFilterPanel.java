package lt.pavilonis.scan.cmm.client.ui.keylog;

import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import lt.pavilonis.scan.cmm.client.MessageSourceAdapter;
import org.apache.commons.lang3.NotImplementedException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public final class KeyLogFilterPanel extends HBox {

   private final static StringConverter<LocalDate> LOCAL_DATE_CONVERTER = new StringConverter<LocalDate>() {
      @Override
      public String toString(LocalDate object) {
         return DateTimeFormatter.ISO_LOCAL_DATE.format(object);
      }

      @Override
      public LocalDate fromString(String string) {
         throw new NotImplementedException("Not needed yet");
      }
   };

   private final DatePicker periodStart = new DatePicker();
   private final DatePicker periodEnd = new DatePicker();
   private final TextField keyNumberField = new TextField();
   private final TextField nameField = new TextField();
   private final ComboBox<KeyAction> actionComboBox =
         new ComboBox<>(FXCollections.observableArrayList(KeyAction.values()));

   private final Button searchButton;

   public KeyLogFilterPanel(MessageSourceAdapter messages) {
      searchButton = new Button(
            messages.get(this, "filter"),
            new ImageView(new Image("images/flat-find-16.png"))
      );
      actionComboBox.setConverter(new StringConverter<KeyAction>() {
         @Override
         public String toString(KeyAction object) {
            return messages.get(object, object.name());
         }

         @Override
         public KeyAction fromString(String string) {
            throw new NotImplementedException("Not needed yet");
         }
      });
      periodStart.setConverter(LOCAL_DATE_CONVERTER);
      periodEnd.setConverter(LOCAL_DATE_CONVERTER);
      Stream.of(periodStart, periodEnd, actionComboBox, keyNumberField, nameField)
            .forEach(f -> f.setPrefWidth(138));

      getChildren().addAll(
            new Label(messages.get(this, "keyNumber")), keyNumberField,
            new Label(messages.get(this, "name")), nameField,
            actionComboBox,
            periodStart,
            periodEnd,
            searchButton
      );
      setAlignment(Pos.CENTER_LEFT);
      setSpacing(15);
   }

   public void reset() {
      keyNumberField.clear();
      nameField.clear();
      periodStart.setValue(LocalDate.now().minusWeeks(1));
      periodEnd.setValue(LocalDate.now());
      actionComboBox.setValue(KeyAction.ALL);
   }

   void addSearchListener(EventHandler<Event> handler) {

      EventHandler<KeyEvent> eventHandler = (KeyEvent event) -> {
         if (event.getCode() == KeyCode.ENTER) {
            handler.handle(event);
         }
      };

      Stream.of(periodStart, periodEnd, actionComboBox, keyNumberField, nameField)
            .forEach(field -> field.setOnKeyReleased(eventHandler));

      searchButton.setOnAction(handler::handle);
   }

   public KeyLogFilter getFilter() {
      return new KeyLogFilter(
            periodStart.getValue(),
            periodEnd.getValue(),
            actionComboBox.getValue(),
            keyNumberField.getText(),
            nameField.getText()
      );
   }

   public void focus() {
      keyNumberField.requestFocus();
   }
}
