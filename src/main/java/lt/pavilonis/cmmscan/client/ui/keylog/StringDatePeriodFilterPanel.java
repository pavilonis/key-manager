package lt.pavilonis.cmmscan.client.ui.keylog;

import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import org.apache.commons.lang3.NotImplementedException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class StringDatePeriodFilterPanel extends HBox {

   private final static StringConverter<LocalDate> LOCALDATE_TOSTRING_CONVERTER = new StringConverter<LocalDate>() {
      @Override
      public String toString(LocalDate object) {
         return DateTimeFormatter.ISO_LOCAL_DATE.format(object);
      }

      @Override
      public LocalDate fromString(String string) {
         throw new NotImplementedException("Not needed yet");
      }
   };

   private final TriConsumer<String, LocalDate, LocalDate> triConsumer;
   private final DatePicker periodStart = new DatePicker();
   private final DatePicker periodEnd = new DatePicker();
   private final TextField textField = new TextField();

   public StringDatePeriodFilterPanel(TriConsumer<String, LocalDate, LocalDate> triConsumer) {
      this.triConsumer = triConsumer;

      Button searchButton = new Button("Search");
      searchButton.setOnAction(event -> action());

      textField.requestFocus();
      textField.setOnKeyReleased(event -> {
         if (event.getCode() == KeyCode.ENTER) {
            action();
         }
      });

      periodStart.setConverter(LOCALDATE_TOSTRING_CONVERTER);
      periodEnd.setConverter(LOCALDATE_TOSTRING_CONVERTER);

      getChildren().addAll(
            periodStart,
            periodEnd,
            textField,
            searchButton
      );
      setSpacing(15);
   }

   void action() {
      this.triConsumer.accept(textField.getText(), periodStart.getValue(), periodEnd.getValue());
   }

   public void reset() {
      textField.clear();
      periodStart.setValue(LocalDate.now().minusMonths(1));
      periodEnd.setValue(LocalDate.now());
   }

   public interface TriConsumer<A, B, C> {
      void accept(A a, B b, C c);
   }
}
