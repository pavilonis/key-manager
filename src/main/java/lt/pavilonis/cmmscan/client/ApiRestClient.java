package lt.pavilonis.cmmscan.client;

import lt.pavilonis.cmmscan.client.representation.KeyRepresentation;
import lt.pavilonis.cmmscan.client.representation.ScanLogRepresentation;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class ApiRestClient {

   private static final Logger LOG = getLogger(ApiRestClient.class.getSimpleName());

   @Value(("${api.uri.base}"))
   private String baseUri;

   @Value(("${scanner.id}"))
   private int scannerId;

   @Autowired
   private RestTemplate rt;

   private String lastErrorMessage;

   public Optional<ScanLogRepresentation> scan(String cardCode) {

      URI uri = UriComponentsBuilder.fromUriString(baseUri)
            .pathSegment("logs")
            .pathSegment(String.valueOf(scannerId))
            .pathSegment(cardCode)
            .build()
            .toUri();

      LOG.info("Sending scanLog POST request [scannerId={}, cardCode={}]", scannerId, cardCode);

      try {
         ScanLogRepresentation response = rt.postForObject(uri, null, ScanLogRepresentation.class);
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
      } catch (ResourceAccessException e) {
         //TODO no connection
      }
      return Optional.empty();
   }

   public Optional<String> getLastErrorMessage() {
      return Optional.ofNullable(lastErrorMessage);
   }

   public List<KeyRepresentation> keysTaken() {
      return Collections.emptyList();
   }

   public List<KeyRepresentation> userKeys(String cardCode) {
      URI uri = UriComponentsBuilder.fromUriString(baseUri)
            .pathSegment("keys")
            .pathSegment(String.valueOf(scannerId))
            .pathSegment(cardCode)
            .build()
            .toUri();

      KeyRepresentation[] response = rt.getForObject(uri, KeyRepresentation[].class);
      LOG.info("Got keys response [cardCode={}, keysNum={}]", cardCode, response.length);
      return newArrayList(response);
   }

   public KeyRepresentation assignKey(String cardCode, int keyNumber) {
      URI uri = UriComponentsBuilder.fromUriString(baseUri)
            .pathSegment("keys")
            .pathSegment(String.valueOf(scannerId))
            .pathSegment(cardCode)
            .pathSegment(String.valueOf(keyNumber))
            .build()
            .toUri();
      KeyRepresentation response = rt.postForObject(uri, null, KeyRepresentation.class);
      LOG.info("Key {} assigned to cardCode {}", response.keyNumber, response.user.cardCode);
      return response;
   }
}
