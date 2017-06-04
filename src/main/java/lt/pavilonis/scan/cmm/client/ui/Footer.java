package lt.pavilonis.scan.cmm.client.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.ResourceBundle;

public class Footer extends HBox {

   private static final String PROPERTY_VERSION = ResourceBundle
         .getBundle("application")
         .getString("application.version");

   public Footer() {

      Label versionLabel = new Label("v " + PROPERTY_VERSION);

      getChildren().addAll(versionLabel);

      setAlignment(Pos.CENTER_LEFT);
//      setStyle("-fx-border-color: black");
   }
}
