package lt.pavilonis.cmmscan.client.ui.keylog;

import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.util.StringConverter;
import lt.pavilonis.cmmscan.client.representation.KeyAction;
import org.apache.commons.lang3.NotImplementedException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

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
   private final TextField textField = new TextField();
   private final ComboBox<KeyAction> actionComboBox = new ComboBox<>(FXCollections.observableArrayList(KeyAction.values()));
   private final Button searchButton = new Button("Search", new ImageView(new Image("images/flat-find-16.png")));

   public KeyLogFilterPanel() {
      periodStart.setConverter(LOCAL_DATE_CONVERTER);
      periodEnd.setConverter(LOCAL_DATE_CONVERTER);
      List<Region> fields = Arrays.asList(periodStart, periodEnd, actionComboBox, textField);
      fields.forEach(f -> f.setPrefWidth(190));
      getChildren().addAll(fields);
      getChildren().add(searchButton);
      setSpacing(15);
      textField.requestFocus();
   }

   public void reset() {
      textField.clear();
      periodStart.setValue(LocalDate.now().minusMonths(1));
      periodEnd.setValue(LocalDate.now());
      actionComboBox.setValue(KeyAction.ALL);
   }

   void addSearchListener(EventHandler<Event> handler) {
      textField.setOnKeyReleased((KeyEvent event) -> {
         if (event.getCode() == KeyCode.ENTER) {
            handler.handle(event);
         }
      });
      searchButton.setOnAction(handler::handle);
   }

   public LocalDate getPeriodStart() {
      return periodStart.getValue();
   }

   public LocalDate getPeriodEnd() {
      return periodEnd.getValue();
   }

   public String getText() {
      return textField.getText();
   }

   public KeyAction getAction() {
      return actionComboBox.getValue();
   }
}
