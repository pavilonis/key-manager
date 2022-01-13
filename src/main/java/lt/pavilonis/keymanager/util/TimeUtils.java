package lt.pavilonis.keymanager.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TimeUtils {

   private TimeUtils() {/**/}

   public static String duration(LocalDateTime start) {
      LocalDateTime now = LocalDateTime.now();
      long days = start.until(now, ChronoUnit.DAYS);
      start = start.plusDays(days);

      long hours = start.until(now, ChronoUnit.HOURS);
      start = start.plusHours(hours);

      long minutes = start.until(now, ChronoUnit.MINUTES);
      start = start.plusMinutes(minutes);

      long seconds = start.until(now, ChronoUnit.SECONDS);
      start = start.plusSeconds(seconds);

      long ms = start.until(now, ChronoUnit.MILLIS);

      String result = (days > 0 ? days + "d " : "") +
            (hours > 0 ? hours + "h " : "") +
            (minutes > 0 ? minutes + "m " : "");

      return result + String.format("%d.%03ds", seconds, ms);
   }
}