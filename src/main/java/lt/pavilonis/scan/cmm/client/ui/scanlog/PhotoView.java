package lt.pavilonis.scan.cmm.client.ui.scanlog;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class PhotoView extends ImageView {

   private static final Logger LOG = getLogger(PhotoView.class.getSimpleName());

   public PhotoView() {
      setFitWidth(200);
      setFitHeight(200);
   }

   void update(String imageUrl) {
      Image image = imageAvailable(imageUrl)
            ? new Image(imageUrl, 200, 200, true, true, true)
            : new Image("images/contacts-200.png");
      this.setImage(image);
   }

   private boolean imageAvailable(String url) {
      try {
         URL u = new URL(url);
         HttpURLConnection http = (HttpURLConnection) u.openConnection();
         http.setInstanceFollowRedirects(false);
         http.setRequestMethod("HEAD");
         http.connect();
         return http.getResponseCode() == HttpURLConnection.HTTP_OK;
      } catch (Exception e) {
         LOG.error("Photo loading error: {}", e.getMessage());
         e.printStackTrace();
         return false;
      }
   }
}
