package lt.pavilonis.keymanager.ui.keylog;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lt.pavilonis.keymanager.User;

import java.time.LocalDateTime;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Key {

   int keyNumber;
   LocalDateTime dateTime;
   User user;
   KeyAction keyAction;

}
