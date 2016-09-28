package lt.pavilonis.cmmscan.client.ui.keylog;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import lt.pavilonis.cmmscan.client.App;
import lt.pavilonis.cmmscan.client.WsRestClient;
import lt.pavilonis.cmmscan.client.representation.KeyRepresentation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;


@Component
public class KeyLogTab extends Tab {

   private final KeyLogTable keyLogTable = new KeyLogTable();
   private final WsRestClient wsClient;

   @Autowired
   public KeyLogTab(WsRestClient wsClient) {
      super("Key Log");
      this.wsClient = wsClient;

      setClosable(false);

      StringDatePeriodFilterPanel filterPanel =
            new StringDatePeriodFilterPanel((searchString, periodStart, periodEnd) ->
                  loadData(periodStart, periodEnd, response -> {
                     if (response.isPresent()) {
                        List<KeyRepresentation> keys = response.get();
                        if (StringUtils.isNoneBlank(searchString)) {
                           keys.removeIf(doesNotMatch(searchString));
                        }
                        keyLogTable.update(keys);
                     } else {
                        App.displayWarning("Can not load keys!");
                     }
                  })
            );

      BorderPane.setMargin(filterPanel, new Insets(0, 0, 15, 0));

      setOnSelectionChanged(event -> {
         if (isSelected()) {
            filterPanel.reset();
            filterPanel.action();
         } else {
            keyLogTable.clear();
         }
         BorderPane mainTabLayout = new BorderPane(keyLogTable, filterPanel, null, null, null);
         mainTabLayout.setPadding(new Insets(15));
         setContent(mainTabLayout);
      });
   }

   private Predicate<KeyRepresentation> doesNotMatch(String searchString) {
      return key -> {
         String content = key.user.firstName + key.user.lastName + key.keyNumber + key.dateTime;
         return !content.toLowerCase().contains(searchString.toLowerCase());
      };
   }

   private void loadData(LocalDate periodStart,
                         LocalDate periodEnd,
                         Consumer<Optional<List<KeyRepresentation>>> responseConsumer) {

      new Service<Void>() {
         @Override
         protected Task<Void> createTask() {
            return new Task<Void>() {
               @Override
               protected Void call() throws Exception {
                  Optional<List<KeyRepresentation>> response = wsClient.keyLog(periodStart, periodEnd);
                  responseConsumer.accept(response);
                  return null;
               }
            };
         }
      }.start();

   }
}
