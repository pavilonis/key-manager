package lt.pavilonis.scan.cmm.client.ui.scanlog;

import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lt.pavilonis.scan.cmm.client.service.MessageSourceAdapter;
import lt.pavilonis.scan.cmm.client.ui.Footer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScanLogTab extends Tab {

   @Autowired
   public ScanLogTab(ScanLogKeyList scanLogKeyList, ScanLogList scanLogList,
                     PhotoView photoView, MessageSourceAdapter messages) {
      setText(messages.get(this, "title"));
      setClosable(false);

      VBox rightColumn = new VBox(scanLogKeyList, photoView);
      rightColumn.setPrefWidth(200);

      VBox.setVgrow(scanLogKeyList, Priority.ALWAYS);
      VBox.setMargin(scanLogKeyList, new Insets(0, 0, 0, 15));
      VBox.setMargin(photoView, new Insets(15, 0, 0, 15));

      BorderPane parent = new BorderPane(scanLogList, null, rightColumn, new Footer(), null);
      parent.setPadding(new Insets(15, 15, 0, 15));
      setContent(parent);

      setOnSelectionChanged(event -> {
         //TODO move to abstract class?
         if (isSelected()) {
            ScanLogListElement selected = scanLogList.getSelectionModel().getSelectedItem();
            if (selected != null) {
               scanLogKeyList.updateContainer(selected.getUser().cardCode);
            }
         }
      });
   }
}
