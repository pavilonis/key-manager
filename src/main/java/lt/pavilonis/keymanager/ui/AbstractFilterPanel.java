package lt.pavilonis.keymanager.ui;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import lt.pavilonis.keymanager.MessageSourceAdapter;
import lt.pavilonis.keymanager.Spring;

import java.util.List;

public abstract class AbstractFilterPanel<T> extends HBox {

   protected final MessageSourceAdapter messages = Spring.getBean(MessageSourceAdapter.class);
   protected final Button searchButton;

   public AbstractFilterPanel() {
      this.searchButton = new Button(
            messages.get(this, "filter"),
            new ImageView(new Image("images/flat-find-16.png"))
      );
      getChildren().addAll(getPanelElements());
      getChildren().add(searchButton);
   }

   public void addSearchListener(EventHandler<Event> handler) {
      searchButton.setOnAction(handler::handle);
   }

   public abstract List<Node> getPanelElements();

   public abstract T getFilter();

   public abstract void reset();

   public abstract void focus();
}
