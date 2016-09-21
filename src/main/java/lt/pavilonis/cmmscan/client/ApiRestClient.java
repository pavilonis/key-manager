package lt.pavilonis.cmmscan.client;

import lt.pavilonis.cmmscan.client.representation.KeyRepresentation;
import lt.pavilonis.cmmscan.client.representation.ScanLogRepresentation;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class ApiRestClient {

   private static final Logger LOG = getLogger(ApiRestClient.class.getSimpleName());

   @Value(("${api.uri.scan}"))
   private String scanLogUriBase;

   @Value(("${api.uri.keys}"))
   private String keyUriBase;

   @Autowired
   private RestTemplate rt;

   private String lastErrorMessage;

   public Optional<ScanLogRepresentation> scan(int scannerId, String cardCode) {

      URI scanLogFullUri = UriComponentsBuilder.fromUriString(scanLogUriBase)
            .pathSegment(String.valueOf(scannerId), cardCode)
            .build()
            .toUri();

      LOG.debug("Sending scanLog POST request [scannerId={}, cardCode={}]", scannerId, cardCode);

      try {
         ScanLogRepresentation response = rt.postForObject(scanLogFullUri, null, ScanLogRepresentation.class);
         lastErrorMessage = null;
         return Optional.of(response);
      } catch (HttpClientErrorException httpErr) {
         switch (httpErr.getStatusCode()) {
            case NOT_FOUND:
               lastErrorMessage = "User not found";
               break;
            default:
               lastErrorMessage = httpErr.getMessage();
         }
         LOG.error(lastErrorMessage);
      }
      return Optional.empty();
   }

   public Optional<String> getLastErrorMessage() {
      return Optional.ofNullable(lastErrorMessage);
   }

   public List<KeyRepresentation> keysTaken() {
      return Collections.emptyList();
   }
}
