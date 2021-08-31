package lt.pavilonis.keymanager.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.util.List;

public abstract class AbstractTable<T> extends TableView<T> {

   private final ObservableList<T> container = FXCollections.observableArrayList();

   public void update(List<T> items) {
      Platform.runLater(() -> {
         container.clear();
         container.addAll(items);
         sort();
      });
   }

   public void clear() {
      container.clear();
   }

   public ObservableList<T> getContainer() {
      return container;
   }
}
