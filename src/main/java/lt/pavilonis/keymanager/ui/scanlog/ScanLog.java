package lt.pavilonis.keymanager.ui.scanlog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lt.pavilonis.keymanager.User;
import lt.pavilonis.keymanager.ui.keylog.Key;

import java.time.LocalDateTime;
import java.util.List;

public class ScanLog {

   private final LocalDateTime dateTime;
   private final User user;
   private final List<Key> keys;

   public ScanLog(@JsonProperty("dateTime") LocalDateTime dateTime,
                  @JsonProperty("user") User user,
                  @JsonProperty("keys") List<Key> keys) {

      this.dateTime = dateTime;
      this.user = user;
      this.keys = keys;
   }

   public LocalDateTime getDateTime() {
      return dateTime;
   }

   public User getUser() {
      return user;
   }

   public List<Key> getKeys() {
      return keys;
   }
}
