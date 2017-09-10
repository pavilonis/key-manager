package lt.pavilonis.scan.cmm.client.ui.scanlog;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lt.pavilonis.scan.cmm.client.IsoLocalDateTimeSerializer;
import lt.pavilonis.scan.cmm.client.ui.keylog.Key;
import lt.pavilonis.scan.cmm.client.User;

import java.time.LocalDateTime;
import java.util.List;

public class ScanLog {
   public final LocalDateTime dateTime;
   public final User user;
   public final List<Key> keys;

   public ScanLog(@JsonSerialize(using = IsoLocalDateTimeSerializer.class)
                  @JsonProperty("dateTime") LocalDateTime dateTime,
                  @JsonProperty("user") User user,
                  @JsonProperty("keys") List<Key> keys) {

      this.dateTime = dateTime;
      this.user = user;
      this.keys = keys;
   }
}
