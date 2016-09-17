package lt.pavilonis.cmmscan.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScan({"lt.pavilonis.cmmscan.client", "resources"})
@PropertySource({"file:#{properties}"})
public class AppConfig {

   @Bean
   public UserRequestService firstService() {
      return new UserRequestService();
   }

   @Bean
   public RestTemplate getRestTemplate() {
      return new RestTemplate();
   }

   @Bean
   public static PropertySourcesPlaceholderConfigurer getPropertySourcesPlaceholderConfigurer() {
      PropertySourcesPlaceholderConfigurer conf = new PropertySourcesPlaceholderConfigurer();
      conf.setFileEncoding("UTF-8");
      return conf;
   }
}
