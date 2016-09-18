package lt.pavilonis.cmmscan.client.ui;

import com.google.common.collect.EvictingQueue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lt.pavilonis.cmmscan.client.representation.KeyRepresentation;
import lt.pavilonis.cmmscan.client.representation.ScanLogRepresentation;
import lt.pavilonis.cmmscan.client.representation.UserRepresentation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class ScanLogTab extends Tab {
   public ScanLogTab() {
      super("Scan Log");
      setClosable(false);

      UserRepresentation vasia = new UserRepresentation("777",
            "Vasia",
            "Pupking",
            "Desc1",
            false,
            "http://pensamientolateral.org/wp-content/uploads/2014/04/yo.jpg",
            LocalDate.of(1986, 7, 31)
      );
      UserRepresentation petia = new UserRepresentation("777",
            "Piotr",
            "Bobrov",
            "Desc2",
            false,
            "http://pensamientolateral.org/wp-content/uploads/2014/04/yo.jpg",
            LocalDate.of(1984, 2, 15)
      );

      ObservableList<ScanLogCell> observableList = FXCollections.observableArrayList(EvictingQueue.create(20));

      List<ScanLogRepresentation> list = newArrayList(
            new ScanLogRepresentation(
                  LocalDateTime.now(),
                  vasia,
                  newArrayList(new KeyRepresentation(206, LocalDateTime.now().minusHours(2), vasia))
            ),

            new ScanLogRepresentation(
                  LocalDateTime.now().minusDays(2),
                  petia,
                  newArrayList(new KeyRepresentation(206, LocalDateTime.now().minusHours(2), petia))
            )
      );

      list.stream()
            .map(scanLog -> new ScanLogCell(scanLog, (cardCode, keyNumber) -> System.out.println(cardCode + " " + keyNumber)))
            .forEach(observableList::add);

      ListView<ScanLogCell> listView = new ListView<>(observableList);

      listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
         if (oldValue != null) {
            oldValue.deactivate();
         }
         newValue.activate();
      });

      Image image = new Image("http://pensamientolateral.org/wp-content/uploads/2014/04/yo.jpg", 250, 250, true, false, true);
      ImageView imageView = new ImageView(image);

      ListView<String> stringListView = new ListView<>(FXCollections.observableArrayList("Key 1", "Key 2", "Key 3"));
      VBox rightColumn = new VBox(stringListView, imageView);
      VBox.setVgrow(stringListView, Priority.ALWAYS);
      VBox.setMargin(stringListView, new Insets(0, 0, 0, 15));
      VBox.setMargin(imageView, new Insets(15, 0, 0, 15));
      BorderPane parent = new BorderPane(
            listView,
            null,
            rightColumn,
            null,
            null
      );
      parent.setPadding(new Insets(15));
      setContent(parent);
   }
}
