package lt.pavilonis.scan.cmm.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class MessageSourceAdapter {

   @Autowired
   private MessageSource messageSource;

   public String get(Object classObject, String propertyName) {
      return messageSource.getMessage(classObject.getClass().getSimpleName() + "." + propertyName, null, null);
   }
}
