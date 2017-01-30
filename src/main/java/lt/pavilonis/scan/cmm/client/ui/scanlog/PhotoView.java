package lt.pavilonis.scan.cmm.client.ui.scanlog;

import com.google.common.io.BaseEncoding;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Component
public class PhotoView extends ImageView {

   public PhotoView() {
      setFitWidth(200);
      setFitHeight(200);
   }

   void update(String base16image) {

      if (StringUtils.isNotBlank(base16image) && BaseEncoding.base16().canDecode(base16image)) {

         byte[] imageBytes = BaseEncoding.base16().decode(base16image);

         try (ByteArrayInputStream input = new ByteArrayInputStream(imageBytes)) {
            BufferedImage image = ImageIO.read(input);
            WritableImage fxImage = SwingFXUtils.toFXImage(image, null);
            this.setImage(fxImage);
         } catch (IOException e) {
            e.printStackTrace();
         }

      } else {

         this.setImage(new Image("images/contacts-200.png"));
      }
   }
}
