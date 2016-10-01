package lt.pavilonis.cmmscan.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

import static java.util.Collections.singletonList;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Configuration
@ComponentScan({"lt.pavilonis.cmmscan.client", "resources"})
@PropertySource({"file:${properties}"})
public class AppConfig {

   public final static String STYLE_STUDENT = "-fx-background-color: rgba(255, 164, 0, 0.15)";

   @Value(("${api.auth.username}"))
   private String apiUsername;

   @Value(("${api.auth.password}"))
   private String apiPassword;

   @Bean
   public RestTemplate getRestTemplate() {
      RestTemplate rest = new RestTemplate();
      rest.setInterceptors(singletonList(authenticatingInterceptor()));
      rest.setMessageConverters(singletonList(new MappingJackson2HttpMessageConverter()));
      return rest;
   }

   @Bean
   public static PropertySourcesPlaceholderConfigurer getPropertySourcesPlaceholderConfigurer() {
      PropertySourcesPlaceholderConfigurer conf = new PropertySourcesPlaceholderConfigurer();
      conf.setFileEncoding("UTF-8");
      return conf;
   }

   private ClientHttpRequestInterceptor authenticatingInterceptor() {
      return (request, body, execution) -> {
         String creds = apiUsername + ":" + apiPassword;
         byte[] base64credsBytes = Base64.getEncoder().encode(creds.getBytes());

         HttpHeaders headers = request.getHeaders();
         headers.add("Authorization", "Basic " + new String(base64credsBytes));
         headers.setAccept(singletonList(APPLICATION_JSON));
         return execution.execute(request, body);
      };
   }
}
