package lt.pavilonis.scan.cmm.client.representation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;
import java.util.List;

public class ScanLogRepresentation {
   public final LocalDateTime dateTime;
   public final UserRepresentation user;
   public final List<KeyRepresentation> keys;

   public ScanLogRepresentation(@JsonSerialize(using = IsoLocalDateTimeSerializer.class)
                                @JsonProperty("dateTime") LocalDateTime dateTime,
                                @JsonProperty("user") UserRepresentation user,
                                @JsonProperty("keys") List<KeyRepresentation> keys) {

      this.dateTime = dateTime;
      this.user = user;
      this.keys = keys;
   }
}
