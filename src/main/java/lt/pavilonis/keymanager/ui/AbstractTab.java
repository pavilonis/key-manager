package lt.pavilonis.keymanager.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;

public abstract class AbstractTab<T, FILTER> extends Tab {

   private final AbstractFilterPanel<FILTER> filterPanel;
   private final AbstractTable<T> table;
   protected final NotificationDisplay notifications;

   public AbstractTab(NotificationDisplay notifications) {
      this.notifications = notifications;
      table = createTable();
      filterPanel = createFilterPanel();
      filterPanel.addSearchListener(event -> updateTable(filterPanel.getFilter()));

      setOnSelectionChanged(event -> {
         if (isSelected()) {
            filterPanel.reset();
            updateTable(filterPanel.getFilter());
            filterPanel.focus();
         } else {
            table.clear();
         }
         var mainTabLayout = new BorderPane(table, filterPanel, null, new Footer(), null);
         mainTabLayout.setPadding(new Insets(15, 15, 0, 15));
         setContent(mainTabLayout);
      });
   }

   protected abstract AbstractTable<T> createTable();

   protected abstract void updateTable(FILTER filter);

   protected abstract AbstractFilterPanel<FILTER> createFilterPanel();

   public AbstractTable<T> getTable() {
      return table;
   }
}
