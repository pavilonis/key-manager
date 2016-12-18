package lt.pavilonis.scan.cmm.client.ui.scanlog;

import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lt.pavilonis.scan.cmm.client.service.MessageSourceAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScanLogTab extends Tab {

   @Autowired
   public ScanLogTab(ScanLogKeyList keyListView, ScanLogList scanLogList,
                     PhotoView photoView, MessageSourceAdapter messages) {
      setText(messages.get(this, "title"));
      setClosable(false);

      VBox rightColumn = new VBox(keyListView, photoView);
      VBox.setVgrow(keyListView, Priority.ALWAYS);
      VBox.setMargin(keyListView, new Insets(0, 0, 0, 15));
      VBox.setMargin(photoView, new Insets(15, 0, 0, 15));

      BorderPane parent = new BorderPane(scanLogList, null, rightColumn, null, null);

      rightColumn.setPrefWidth(200);
      parent.setPadding(new Insets(15));
      setContent(parent);
   }
}
