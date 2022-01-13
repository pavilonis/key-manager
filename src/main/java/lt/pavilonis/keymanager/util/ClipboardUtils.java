package lt.pavilonis.keymanager.util;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClipboardUtils {

   public static void addToClipboard(String unknownCardCode) {
      var content = new ClipboardContent();
      content.putString(unknownCardCode);

      Clipboard systemClipboard = Clipboard.getSystemClipboard();
      systemClipboard.setContent(content);
   }

}
