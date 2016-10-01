package lt.pavilonis.scan.cmm.client.ui.scanlog;

import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScanLogTab extends Tab {

   @Autowired
   public ScanLogTab(ScanLogKeyList keyListView, ScanLogList scanLogList) {
      super("Scan Log");
      setClosable(false);

      Image image = new Image(
            "http://www.appsfuze.com/static/images/apps/7/a/c/7acd4551-e1ae-e011-a53c-78e7d1fa76f8.png",
            200, 200, true, false, true
      );
      ImageView imageView = new ImageView(image);

      VBox rightColumn = new VBox(keyListView, imageView);
      VBox.setVgrow(keyListView, Priority.ALWAYS);
      VBox.setMargin(keyListView, new Insets(0, 0, 0, 15));
      VBox.setMargin(imageView, new Insets(15, 0, 0, 15));
      BorderPane parent = new BorderPane(
            scanLogList,
            null,
            rightColumn,
            null,
            null
      );
      rightColumn.setPrefWidth(200);
      parent.setPadding(new Insets(15));
      setContent(parent);
   }
}
