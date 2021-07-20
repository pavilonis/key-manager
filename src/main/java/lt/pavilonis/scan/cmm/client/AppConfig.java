package lt.pavilonis.scan.cmm.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;

import static java.util.Collections.singletonList;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Configuration
@ComponentScan({"lt.pavilonis.scan", "resources"})
@PropertySource({"file:app.properties"})
public class AppConfig {

   public static final String STYLE_STUDENT = "-fx-background-color: rgba(255, 164, 0, 0.15)";

   @Value("${api.auth.username}")
   private String apiUsername;

   @Value("${api.auth.password}")
   private String apiPassword;

   @Bean
   public RestTemplate getRestTemplate() {
      var rest = new RestTemplate();
      rest.setInterceptors(singletonList(authenticatingInterceptor()));
      rest.setMessageConverters(singletonList(new MappingJackson2HttpMessageConverter()));
      return rest;
   }

   @Bean
   public ReloadableResourceBundleMessageSource messageSource() {
      var messageSource = new ReloadableResourceBundleMessageSource();
      messageSource.setUseCodeAsDefaultMessage(true);
      messageSource.setBasename("classpath:lang/messages");
      messageSource.setCacheSeconds(0);
      messageSource.setDefaultEncoding("UTF-8");
      Locale.setDefault(new Locale("lt", "LT"));
      return messageSource;
   }

   private ClientHttpRequestInterceptor authenticatingInterceptor() {
      return (request, body, execution) -> {
         HttpHeaders headers = request.getHeaders();
         headers.setBasicAuth(apiUsername, apiPassword);
         headers.setAccept(singletonList(APPLICATION_JSON));
         return execution.execute(request, body);
      };
   }


   /**
    * Needed for Spring @Value annotations to work
    */
   @Bean
   public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
      return new PropertySourcesPlaceholderConfigurer();
   }
}
