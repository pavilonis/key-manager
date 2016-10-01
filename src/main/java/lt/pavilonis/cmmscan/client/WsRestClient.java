package lt.pavilonis.cmmscan.client;

import lt.pavilonis.cmmscan.client.representation.KeyRepresentation;
import lt.pavilonis.cmmscan.client.representation.ScanLogRepresentation;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class WsRestClient {
   private static final Logger LOG = getLogger(WsRestClient.class.getSimpleName());
   private static final String SEGMENT_KEYS = "keys";
   private static final String SEGMENT_SCANLOG = "scanlog";
   private static final String SEGMENT_KEYLOG = "keylog";

   @Value(("${api.uri.base}"))
   private String baseUri;

   @Value(("${scanner.id}"))
   private String scannerId;

   @Autowired
   private RestTemplate restTemplate;

   private String lastErrorMessage;

   public Optional<ScanLogRepresentation> writeScanLog(String cardCode) {

      URI uri = uri(SEGMENT_SCANLOG, scannerId, cardCode);

      LOG.info("Sending scanLog POST request [scannerId={}, cardCode={}]", scannerId, cardCode);

      return request(uri, HttpMethod.POST, ScanLogRepresentation.class);
   }

   public Optional<List<KeyRepresentation>> allKeysAssigned() {
      URI uri = uri(SEGMENT_KEYS, scannerId);
      Optional<KeyRepresentation[]> response = request(uri, HttpMethod.GET, KeyRepresentation[].class);

      if (response.isPresent()) {
         LOG.info("Loaded all assigned keys [number={}]", response.get().length);
         return Optional.of(newArrayList(response.get()));
      }

      return Optional.empty();
   }

   public Optional<List<KeyRepresentation>> userKeysAssigned(String cardCode) {
      URI uri = uri(SEGMENT_KEYS, scannerId, cardCode);
      Optional<KeyRepresentation[]> response = request(uri, HttpMethod.GET, KeyRepresentation[].class);

      if (response.isPresent()) {
         LOG.info("Loaded user assigned keys [cardCode={}, keysNum={}]", cardCode, response.get().length);
         return Optional.of(newArrayList(response.get()));
      }

      return Optional.empty();
   }

   public Optional<KeyRepresentation> assignKey(String cardCode, int keyNumber) {
      URI uri = uri(SEGMENT_KEYS, scannerId, cardCode, String.valueOf(keyNumber));
      Optional<KeyRepresentation> response = request(uri, HttpMethod.POST, KeyRepresentation.class);

      if (response.isPresent()) {
         LOG.info("Key {} assigned to cardCode {}", response.get().keyNumber, response.get().user.cardCode);
      }
      return response;
   }

   public boolean returnKey(String cardCode, int keyNumber) {

      URI uri = uri(SEGMENT_KEYS, scannerId, cardCode, String.valueOf(keyNumber));
      Optional<KeyRepresentation> response = request(uri, HttpMethod.DELETE, KeyRepresentation.class);

      if (response.isPresent()) {
         LOG.info("Returned key [keyNumber={}]", response.get().keyNumber);
         return true;

      } else {
         LOG.error("Erroneous state - could not return key [keyNumber={}]", keyNumber);
         return false;
      }
   }

   public Optional<List<KeyRepresentation>> keyLog(LocalDate periodStart, LocalDate periodEnd) {
      URI uri = uri(
            SEGMENT_KEYLOG,
            scannerId,
            DateTimeFormatter.ISO_LOCAL_DATE.format(periodStart),
            DateTimeFormatter.ISO_LOCAL_DATE.format(periodEnd)
      );
      Optional<KeyRepresentation[]> response = request(uri, HttpMethod.GET, KeyRepresentation[].class);

      if (response.isPresent()) {
         LOG.info("Loaded keyLog [entries={}]", response.get().length);
         return Optional.of(newArrayList(response.get()));
      }

      return Optional.empty();
   }

   public Optional<String> getLastErrorMessage() {
      return Optional.ofNullable(lastErrorMessage);
   }

   private URI uri(String... segments) {
      return UriComponentsBuilder.fromUriString(baseUri)
            .pathSegment(segments)
            .build()
            .toUri();
   }

   private <T> Optional<T> request(URI uri, HttpMethod requestMethod, Class<T> responseType) {
      try {
         ResponseEntity<T> exchange = restTemplate.exchange(uri, requestMethod, null, responseType);
         lastErrorMessage = null;
         App.clearWarning();
         return Optional.of(exchange.getBody());

      } catch (HttpClientErrorException httpErr) {

         switch (httpErr.getStatusCode()) {
            case NOT_FOUND:
               lastErrorMessage = "Resource not found";
               break;
            case CONFLICT:
               lastErrorMessage = "Request conflict";
               break;
            default:
               lastErrorMessage = httpErr.getMessage();
         }
         LOG.error(lastErrorMessage);

      } catch (ResourceAccessException e) {
         lastErrorMessage = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
         LOG.error(lastErrorMessage);
      } catch (Exception e) {
         e.printStackTrace();
         lastErrorMessage = e.getMessage();
         LOG.error(lastErrorMessage);
      }
      return Optional.empty();
   }
}
