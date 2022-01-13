package lt.pavilonis.keymanager.ui.scanlog;

import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lt.pavilonis.keymanager.MessageSourceAdapter;
import lt.pavilonis.keymanager.Spring;
import lt.pavilonis.keymanager.WebServiceClient;
import lt.pavilonis.keymanager.ui.Footer;
import lt.pavilonis.keymanager.ui.NotificationDisplay;
import lt.pavilonis.keymanager.util.ClipboardUtils;

import java.util.function.Consumer;

import static lt.pavilonis.keymanager.ui.NotificationDisplay.UNKNOWN_USER;

public class ScanLogTab extends Tab implements Consumer<String> {

   private final WebServiceClient webServiceClient = Spring.getBean(WebServiceClient.class);
   private final MessageSourceAdapter messages = Spring.getBean(MessageSourceAdapter.class);
   private final ScanLogList scanLogList;
   private final NotificationDisplay notifications;

   public ScanLogTab(ScanLogKeyList scanLogKeyList, ScanLogList scanLogList,
                     PhotoView photoView, NotificationDisplay notifications) {

      this.scanLogList = scanLogList;
      this.notifications = notifications;

      setText(messages.get(this, "title"));
      setClosable(false);

      var rightColumn = new VBox(scanLogKeyList, photoView);
      rightColumn.setPrefWidth(200);

      VBox.setVgrow(scanLogKeyList, Priority.ALWAYS);
      VBox.setMargin(scanLogKeyList, new Insets(0, 0, 0, 15));
      VBox.setMargin(photoView, new Insets(15, 0, 0, 15));

      var parent = new BorderPane(scanLogList, null, rightColumn, new Footer(), null);
      parent.setPadding(new Insets(15, 15, 0, 15));
      setContent(parent);

      setOnSelectionChanged(event -> {
         if (isSelected()) {
            ScanLogListElement selected = scanLogList.getSelectionModel().getSelectedItem();
            if (selected != null) {
               scanLogKeyList.updateContainerFromWebService(selected.getUser().getCardCode());
            }
         }
      });
   }

   @Override
   public void accept(String cardCode) {
      notifications.clear();
      writeScanLog(cardCode);
   }

   private void writeScanLog(String cardCode) {
      webServiceClient.writeScanLog(
            cardCode,
            scanLogList::addElement,
            exception -> handleException(exception, cardCode)
      );
   }

   private void handleException(Exception exception, String cardCode) {
      String message = exception.getMessage();

      if (message != null && message.contains(UNKNOWN_USER)) {
         UserCreationForm form = new UserCreationForm(cardCode);
         form.setConfirmAction(() -> createUser(form, cardCode));
         ClipboardUtils.addToClipboard(cardCode);
         notifications.show(form);

      } else {
         notifications.warn(messages.get("ScanLogTab.canNotWriteScanLog"), exception);
      }
   }

   private void createUser(UserCreationForm form, String cardCode) {
      webServiceClient.createUser(
            form.getValue(),
            () -> {
               form.close();
               writeScanLog(cardCode);
               notifications.notify("User created");
            },
            exception -> notifications.warn("User not created", exception)
      );
   }
}
