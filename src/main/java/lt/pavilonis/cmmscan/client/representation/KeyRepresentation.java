package lt.pavilonis.cmmscan.client.representation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;

public class KeyRepresentation {
   public final int keyNumber;
   public final LocalDateTime dateTime;
   public final UserRepresentation user;

   public KeyRepresentation(@JsonProperty("keyNumber") int keyNumber,

                            @JsonSerialize(using = IsoLocalDateTimeSerializer.class)
                            @JsonProperty("dateTime") LocalDateTime dateTime,

                            @JsonProperty("user") UserRepresentation user) {

      this.keyNumber = keyNumber;
      this.dateTime = dateTime;
      this.user = user;
   }
}
