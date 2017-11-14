package lt.pavilonis.scan.cmm.client.ui.keylog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lt.pavilonis.scan.cmm.client.User;

import java.time.LocalDateTime;

public class Key {
   private final int keyNumber;
   private final LocalDateTime dateTime;
   private final User user;
   private final KeyAction keyAction;

   public Key(@JsonProperty("keyNumber") int keyNumber,
//                            @JsonSerialize(using = IsoLocalDateTimeSerializer.class)
              @JsonProperty("dateTime") LocalDateTime dateTime,
              @JsonProperty("user") User user,
              @JsonProperty("keyAction") KeyAction keyAction) {

      this.keyNumber = keyNumber;
      this.dateTime = dateTime;
      this.user = user;
      this.keyAction = keyAction;
   }

   public int getKeyNumber() {
      return keyNumber;
   }

   public LocalDateTime getDateTime() {
      return dateTime;
   }

   public User getUser() {
      return user;
   }

   public KeyAction getKeyAction() {
      return keyAction;
   }
}
