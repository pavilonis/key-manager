package lt.pavilonis.keymanager.ui.scanlog;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lt.pavilonis.keymanager.MessageSourceAdapter;
import lt.pavilonis.keymanager.NotificationDisplay;
import lt.pavilonis.keymanager.Spring;
import lt.pavilonis.keymanager.WebServiceClient;
import lt.pavilonis.keymanager.ui.Footer;

import java.util.List;
import java.util.function.Consumer;

public class ScanLogTab extends Tab implements Consumer<String>, NotificationDisplay {

   private final WebServiceClient webServiceClient = Spring.CONTEXT.getBean(WebServiceClient.class);
   private final MessageSourceAdapter messages = Spring.CONTEXT.getBean(MessageSourceAdapter.class);
   private final ScanLogList scanLogList;

   public ScanLogTab(ScanLogKeyList scanLogKeyList, ScanLogList scanLogList, PhotoView photoView, Footer footer) {
      this.scanLogList = scanLogList;

      setText(messages.get(this, "title"));
      setClosable(false);

      var rightColumn = new VBox(scanLogKeyList, photoView);
      rightColumn.setPrefWidth(200);

      VBox.setVgrow(scanLogKeyList, Priority.ALWAYS);
      VBox.setMargin(scanLogKeyList, new Insets(0, 0, 0, 15));
      VBox.setMargin(photoView, new Insets(15, 0, 0, 15));

      var parent = new BorderPane(scanLogList, null, rightColumn, footer, null);
      parent.setPadding(new Insets(15, 15, 0, 15));
      setContent(parent);

      setOnSelectionChanged(event -> {
         //TODO move to abstract class?
         if (isSelected()) {
            ScanLogListElement selected = scanLogList.getSelectionModel().getSelectedItem();
            if (selected != null) {
               scanLogKeyList.updateContainer(selected.getUser().getCardCode());
            }
         }
      });
   }

   @Override
   public void accept(String cardCode) {
      webServiceClient.writeScanLog(
            cardCode,
            scanLogList::addElement,
            exception -> warn(messages.get(this, "canNotWriteScanLog"), exception)
      );
   }

   @Override
   public List<Node> getStackPaneChildren() {
      StackPane stackPane = (StackPane) getTabPane().getParent();
      return stackPane.getChildren();
   }
}
