package lt.pavilonis.cmmscan.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Base64;

import static java.util.Arrays.asList;

@Service
public class UserRequestService {

   @Value(("${api.uri.scan}"))
   private String scanLogUriBase;

   @Value(("${api.uri.keys}"))
   private String keyUriBase;

   @Value(("${api.auth.username}"))
   private String username;

   @Value(("${api.auth.password}"))
   private String password;

   @Autowired
   private RestTemplate rt;

   public ResponseEntity scan(int scannerId, String cardCode) {


      URI scanLogFullUri = UriComponentsBuilder.fromUriString(scanLogUriBase)
            .pathSegment(String.valueOf(scannerId), cardCode)
            .build()
            .toUri();

      HttpEntity<String> request = new HttpEntity<>(headers());
      rt.exchange(scanLogFullUri, HttpMethod.GET, request, /*DTO*/);
   }

   private HttpHeaders headers() {
      String creds = username + ":" + password;
      byte[] base64credsBytes = Base64.getEncoder().encode(creds.getBytes());

      HttpHeaders headers = new HttpHeaders();
      headers.add("Authorization", "Basic " + new String(base64credsBytes));
      headers.setAccept(asList(MediaType.APPLICATION_JSON));
      return headers;
   }
}
