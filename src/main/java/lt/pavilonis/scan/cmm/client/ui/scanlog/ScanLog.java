package lt.pavilonis.scan.cmm.client.ui.scanlog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lt.pavilonis.scan.cmm.client.User;
import lt.pavilonis.scan.cmm.client.ui.keylog.Key;

import java.time.LocalDateTime;
import java.util.List;

public class ScanLog {

   public final LocalDateTime dateTime;
   public final User user;
   public final List<Key> keys;

   public ScanLog(@JsonProperty("dateTime") LocalDateTime dateTime,
                  @JsonProperty("user") User user,
                  @JsonProperty("keys") List<Key> keys) {

      this.dateTime = dateTime;
      this.user = user;
      this.keys = keys;
   }
}
