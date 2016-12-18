package lt.pavilonis.scan.cmm.client.service;

import lt.pavilonis.scan.cmm.client.App;
import lt.pavilonis.scan.cmm.client.representation.KeyRepresentation;
import lt.pavilonis.scan.cmm.client.representation.ScanLogRepresentation;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class WsRestClient {
   private static final Logger LOG = getLogger(WsRestClient.class.getSimpleName());
   private static final String SEGMENT_KEYS = "keys";
   private static final String SEGMENT_SCANLOG = "scanlog";
   private static final String SEGMENT_KEYLOG = "keylog";
   private final ExecutorService pool = Executors.newFixedThreadPool(3);

   @Value(("${api.uri.base}"))
   private String baseUri;

   @Value(("${scanner.id}"))
   private String scannerId;

   @Autowired
   private RestTemplate restTemplate;

   private String lastErrorMessage;

   public void writeScanLog(String cardCode, Consumer<Optional<ScanLogRepresentation>> consumer) {

      URI uri = uri(SEGMENT_SCANLOG, scannerId, cardCode);

      LOG.info("Sending scanLog POST request [scannerId={}, cardCode={}]", scannerId, cardCode);

      request(uri, HttpMethod.POST, ScanLogRepresentation.class, consumer);
   }

   public void allKeysAssigned(Consumer<Optional<KeyRepresentation[]>> consumer) {
      request(
            uri(SEGMENT_KEYS, scannerId),
            HttpMethod.GET,
            KeyRepresentation[].class,
            consumer
      );
   }

   public void userKeysAssigned(String cardCode, Consumer<Optional<KeyRepresentation[]>> consumer) {
      URI uri = uri(SEGMENT_KEYS, scannerId, cardCode);
      request(uri, HttpMethod.GET, KeyRepresentation[].class, consumer);
   }

   public void assignKey(String cardCode, int keyNumber, Consumer<Optional<KeyRepresentation>> consumer) {
      URI uri = uri(SEGMENT_KEYS, scannerId, cardCode, String.valueOf(keyNumber));
      request(uri, HttpMethod.POST, KeyRepresentation.class, consumer);
   }

   public void returnKey(int keyNumber, Consumer<Optional<KeyRepresentation>> consumer) {
      URI uri = uri(SEGMENT_KEYS, scannerId, String.valueOf(keyNumber));
      request(uri, HttpMethod.DELETE, KeyRepresentation.class, consumer);
   }

   public void keyLog(LocalDate periodStart, LocalDate periodEnd,
                      Consumer<Optional<KeyRepresentation[]>> consumer) {
      URI uri = uri(
            SEGMENT_KEYLOG,
            scannerId,
            DateTimeFormatter.ISO_LOCAL_DATE.format(periodStart),
            DateTimeFormatter.ISO_LOCAL_DATE.format(periodEnd)
      );
      request(uri, HttpMethod.GET, KeyRepresentation[].class, consumer);
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

   @Async
   public <T> void request(URI uri, HttpMethod requestMethod, Class<T> responseType, Consumer<Optional<T>> consumer) {

      try {
         new BackgroundTask<>(() -> {
            ResponseEntity<T> exchange = restTemplate.exchange(uri, requestMethod, null, responseType);
            lastErrorMessage = null;
            App.clearWarning();

            consumer.accept(Optional.of(exchange.getBody()));
            return null;
         });

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
   }
}
