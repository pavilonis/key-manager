package lt.pavilonis.scan.cmm.client.representation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class KeyRepresentation {
   private final int keyNumber;
   private final LocalDateTime dateTime;
   private final UserRepresentation user;
   private final KeyAction keyAction;

   public KeyRepresentation(@JsonProperty("keyNumber") int keyNumber,
//                            @JsonSerialize(using = IsoLocalDateTimeSerializer.class)
                            @JsonProperty("dateTime") LocalDateTime dateTime,
                            @JsonProperty("user") UserRepresentation user,
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

   public UserRepresentation getUser() {
      return user;
   }

   public KeyAction getKeyAction() {
      return keyAction;
   }
}
