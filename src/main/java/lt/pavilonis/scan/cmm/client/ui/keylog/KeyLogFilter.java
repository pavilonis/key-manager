package lt.pavilonis.scan.cmm.client.ui.keylog;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;

public class KeyLogFilter {
   private final LocalDate periodStart;
   private final LocalDate periodEnd;
   private final KeyAction keyAction;
   private final String keyNumber;
   private final String name;

   public KeyLogFilter(LocalDate periodStart, LocalDate periodEnd,
                       KeyAction keyAction, String keyNumber, String name) {
      this.periodStart = periodStart;
      this.periodEnd = periodEnd;
      this.keyAction = keyAction;
      this.keyNumber = StringUtils.stripToNull(keyNumber);
      this.name = StringUtils.stripToNull(name);
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
