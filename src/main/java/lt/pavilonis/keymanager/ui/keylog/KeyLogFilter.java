package lt.pavilonis.keymanager.ui.keylog;

import org.springframework.util.StringUtils;

import java.time.LocalDate;

public class KeyLogFilter {

   private final LocalDate periodStart;
   private final LocalDate periodEnd;
   private final KeyAction keyAction;
   private final String keyNumber;
   private final String name;

   KeyLogFilter(LocalDate periodStart, LocalDate periodEnd,
                       KeyAction keyAction, String keyNumber, String name) {
      this.periodStart = periodStart;
      this.periodEnd = periodEnd;
      this.keyAction = keyAction;
      this.keyNumber = StringUtils.hasText(keyNumber) ? keyNumber : null;
      this.name = StringUtils.hasText(name) ? name : null;
   }

   public LocalDate getPeriodStart() {
      return periodStart;
   }

   public LocalDate getPeriodEnd() {
      return periodEnd;
   }

   public KeyAction getKeyAction() {
      return keyAction;
   }

   public String getKeyNumber() {
      return keyNumber;
   }

   public String getName() {
      return name;
   }
}
