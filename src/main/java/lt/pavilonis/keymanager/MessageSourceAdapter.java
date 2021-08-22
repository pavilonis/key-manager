package lt.pavilonis.keymanager;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class MessageSourceAdapter {

   private final MessageSource messageSource;

   public MessageSourceAdapter(MessageSource messageSource) {
      this.messageSource = messageSource;
   }

   public String get(Object classObject, String propertyName) {
      return messageSource.getMessage(classObject.getClass().getSimpleName() + "." + propertyName, null, null);
   }
}
