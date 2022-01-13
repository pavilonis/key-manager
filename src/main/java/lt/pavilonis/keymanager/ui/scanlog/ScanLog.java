package lt.pavilonis.keymanager.ui.scanlog;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lt.pavilonis.keymanager.User;
import lt.pavilonis.keymanager.ui.keylog.Key;

import java.time.LocalDateTime;
import java.util.List;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ScanLog {

   LocalDateTime dateTime;
   User user;
   List<Key> keys;

}
