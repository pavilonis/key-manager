package lt.pavilonis.keymanager;

import javafx.application.Platform;
import javafx.concurrent.Task;
import lt.pavilonis.keymanager.ui.classusage.ScanLogBrief;
import lt.pavilonis.keymanager.ui.keylog.Key;
import lt.pavilonis.keymanager.ui.keylog.KeyAction;
import lt.pavilonis.keymanager.ui.keylog.KeyLogFilter;
import lt.pavilonis.keymanager.ui.scanlog.ScanLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.Collections.singletonList;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class WebServiceClient {

   private static final String SEGMENT_KEYS = "keys";
   private static final String SEGMENT_LOG = "log";
   private static final String SEGMENT_SCANLOG = "scanlog";
   private static final String SEGMENT_LASTSEEN = "lastseen";
   private static final Logger LOGGER = LoggerFactory.getLogger(WebServiceClient.class);

   private final String baseUri;
   private final String scannerId;
   private final RestTemplate restTemplate;

   public WebServiceClient(@Value("${api.uri.base}") String baseUri,
                           @Value("api.auth.username") String username,
                           @Value("api.auth.password") String password,
                           @Value("${scanner.id}") String scannerId) {

      Assert.hasText(baseUri, "Not expecting empty base URI");
      Assert.hasText(scannerId, "Not expecting empty scanner ID");

      this.baseUri = baseUri;
      this.scannerId = scannerId;
      this.restTemplate = createRestTemplate(username, password);
   }

   public void writeScanLog(String cardCode, Consumer<ScanLog> consumer, Consumer<Exception> exceptionConsumer) {
      URI uri = uri(SEGMENT_SCANLOG, scannerId, cardCode);
      LOGGER.info("Sending scanLog POST request [scannerId={}, cardCode={}]", scannerId, cardCode);
      request(uri, HttpMethod.POST, ScanLog.class, consumer, exceptionConsumer);
   }

   public void allActiveKeys(String keyNumber, Consumer<Key[]> consumer, Consumer<Exception> exceptionConsumer) {
      var params = new HashMap<String, String>();
      params.put("scannerId", scannerId);
      if (StringUtils.hasText(keyNumber)) {
         params.put("keyNumber", keyNumber);
      }

      request(uri(params, SEGMENT_KEYS), HttpMethod.GET, Key[].class, consumer, exceptionConsumer);
   }

   public void userKeysAssigned(String cardCode, Consumer<Key[]> consumer, Consumer<Exception> exceptionConsumer) {
      Map<String, String> params = Map.of("scannerId", scannerId, "cardCode", cardCode);
      request(uri(params, SEGMENT_KEYS), HttpMethod.GET, Key[].class, consumer, exceptionConsumer);
   }

   public void assignKey(String cardCode, int keyNumber, Consumer<Key> consumer, Consumer<Exception> exceptionConsumer) {
      URI uri = uri(SEGMENT_KEYS, scannerId, String.valueOf(keyNumber), cardCode);
      request(uri, HttpMethod.POST, Key.class, consumer, exceptionConsumer);
   }

   public void returnKey(int keyNumber, Consumer<Key> consumer, Consumer<Exception> exceptionConsumer) {
      URI uri = uri(SEGMENT_KEYS, scannerId, String.valueOf(keyNumber));
      request(uri, HttpMethod.DELETE, Key.class, consumer, exceptionConsumer);
   }

   public void keyLog(KeyLogFilter filter, Consumer<Key[]> consumer, Consumer<Exception> exceptionConsumer) {

      var params = new HashMap<String, String>();
      params.put("scannerId", scannerId);
      params.put("periodStart", DateTimeFormatter.ISO_LOCAL_DATE.format(filter.getPeriodStart()));
      params.put("periodEnd", DateTimeFormatter.ISO_LOCAL_DATE.format(filter.getPeriodEnd()));

      if (StringUtils.hasText(filter.getKeyNumber())) {
         params.put("keyNumber", filter.getKeyNumber());
      }
      if (filter.getKeyAction() != KeyAction.ALL) {
         params.put("keyAction", filter.getKeyAction().name());
      }
      if (StringUtils.hasText(filter.getName())) {
         params.put("nameLike", filter.getName());
      }

      URI uri = uri(params, SEGMENT_KEYS, SEGMENT_LOG);
      request(uri, HttpMethod.GET, Key[].class, consumer, exceptionConsumer);
   }

   public void classroomUsage(String text, Consumer<ScanLogBrief[]> consumer, Consumer<Exception> exceptionConsumer) {
      Map<String, String> params = StringUtils.hasText(text)
            ? Map.of("text", text)
            : Map.of();

      URI uri = uri(params, SEGMENT_SCANLOG, SEGMENT_LASTSEEN);
      request(uri, HttpMethod.GET, ScanLogBrief[].class, consumer, exceptionConsumer);
   }

   private URI uri(String... segments) {
      return uri(Collections.emptyMap(), segments);
   }

   private URI uri(Map<String, String> params, String... segments) {
      LinkedMultiValueMap<String, String> paramMultiMap = new LinkedMultiValueMap<>();
      params.forEach(paramMultiMap::add);

      return UriComponentsBuilder.fromUriString(baseUri)
            .pathSegment(segments)
            .queryParams(paramMultiMap)
            .build()
            .toUri();
   }

   public <T> void request(URI uri, HttpMethod requestMethod, Class<T> responseType,
                           Consumer<T> consumer, Consumer<Exception> exceptionConsumer) {
      runInBackground(() -> {
         try {
            LOGGER.info("Making request [uri={}]", uri);
            ResponseEntity<T> response = restTemplate.exchange(uri, requestMethod, null, responseType);
            if (response.getStatusCode().is2xxSuccessful()) {
               Platform.runLater(() -> consumer.accept(response.getBody()));
            }
            throw new HttpStatusCodeException(response.getStatusCode()) {
            };
         } catch (Exception e) {
            LOGGER.error("Failed to get REST service response", e);
            Platform.runLater(() -> exceptionConsumer.accept(e));
         }
      });
   }

   public RestTemplate createRestTemplate(String username, String password) {
      var rest = new RestTemplate();
      rest.setInterceptors(List.of((request, body, execution) -> {
         HttpHeaders headers = request.getHeaders();
         headers.setBasicAuth(username, password);
         headers.setAccept(singletonList(APPLICATION_JSON));
         return execution.execute(request, body);
      }));
      rest.setMessageConverters(List.of(new MappingJackson2HttpMessageConverter()));
      return rest;
   }

   private void runInBackground(Runnable action) {
      new javafx.concurrent.Service<Boolean>() {
         @Override
         protected Task<Boolean> createTask() {
            return new Task<>() {
               @Override
               protected Boolean call() {
                  action.run();
                  return true;
               }
            };
         }
      }.start();
   }
}
