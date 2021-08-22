package lt.pavilonis.keymanager.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import lt.pavilonis.keymanager.Spring;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
import java.time.ZoneId;

public class Footer extends HBox {

   public Footer() {
      getChildren().addAll(createVersionLabel());

      setAlignment(Pos.CENTER_LEFT);
//      setStyle("-fx-border-color: black");
   }

   private Label createVersionLabel() {
      BuildProperties buildProperties = Spring.getBean(BuildProperties.class);
      MessageSource messages = Spring.getBean(MessageSource.class);

      Object[] messageParams =
            {buildProperties.getVersion(), LocalDate.ofInstant(buildProperties.getTime(), ZoneId.systemDefault())};

      String version = messages.getMessage("Footer.version", messageParams, null);
      return new Label(version);
   }
}
