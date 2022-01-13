package lt.pavilonis.keymanager.ui.scanlog;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

public class PhotoView extends ImageView {

   private static final Logger LOGGER = LoggerFactory.getLogger(PhotoView.class);

   public PhotoView() {
      setFitWidth(200);
      setFitHeight(200);
   }

   void update(String base64image) {
      if (StringUtils.hasText(base64image)) {
         this.setImage(readImage(base64image));

      } else {
         this.setImage(new Image("images/contacts-200.png"));
      }
   }

   private Image readImage(String base64image) {

      byte[] imageBytes = Base64.getDecoder().decode(base64image);
      try (ByteArrayInputStream input = new ByteArrayInputStream(imageBytes)) {
         BufferedImage image = ImageIO.read(input);
         if (image == null) {
            return null;
         }
         return SwingFXUtils.toFXImage(image, null);

      } catch (IOException e) {
         LOGGER.error("Failed to read image from string", e);
         return null;
      }
   }
}