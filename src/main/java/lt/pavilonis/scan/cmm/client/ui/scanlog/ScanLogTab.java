package lt.pavilonis.scan.cmm.client.ui.scanlog;

import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class ScanLogTab extends Tab {

   @Autowired
   public ScanLogTab(ScanLogKeyList keyListView, ScanLogList scanLogList,
                     PhotoView photoView, MessageSource messageSource) {
      String title = messageSource.getMessage(this.getClass().getSimpleName() + ".title", null, null);
      setText(title);
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
