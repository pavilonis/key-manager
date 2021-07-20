package lt.pavilonis.scan.cmm.client.ui.scanlog;

import com.google.common.io.BaseEncoding;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Component
public class PhotoView extends ImageView {

   private static final Logger LOGGER = LoggerFactory.getLogger(PhotoView.class);

   public PhotoView() {
      setFitWidth(200);
      setFitHeight(200);
   }

   void update(String base16image) {

      if (StringUtils.isBlank(base16image) || !BaseEncoding.base16().canDecode(base16image)) {
         this.setImage(new Image("images/contacts-200.png"));
         return;
      }

      this.setImage(readImage(base16image));
   }

   private Image readImage(String base16image) {

      byte[] imageBytes = BaseEncoding.base16().decode(base16image);

      try (ByteArrayInputStream input = new ByteArrayInputStream(imageBytes)) {
         BufferedImage image = ImageIO.read(input);
         return SwingFXUtils.toFXImage(image, null);

      } catch (IOException e) {
         LOGGER.error("Failed to read image from string", e);
         return null;
      }
   }
}
