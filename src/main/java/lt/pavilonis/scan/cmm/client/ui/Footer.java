package lt.pavilonis.scan.cmm.client.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import lt.pavilonis.scan.cmm.client.service.MessageSourceAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

@Scope("prototype")
@Component
public class Footer extends HBox {
   private static final String PROPERTY_VERSION = ResourceBundle
         .getBundle("application")
         .getString("application.version");

   @Autowired
   public Footer(MessageSourceAdapter messageSource) {

      Label versionLabel = new Label(messageSource.get(this, "version") + " " + PROPERTY_VERSION);

      getChildren().addAll(versionLabel);

      setAlignment(Pos.CENTER_LEFT);
//      setStyle("-fx-border-color: black");
   }
}
