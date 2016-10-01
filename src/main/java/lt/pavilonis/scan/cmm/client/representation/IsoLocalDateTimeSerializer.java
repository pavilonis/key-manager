package lt.pavilonis.scan.cmm.client.representation;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class IsoLocalDateTimeSerializer extends LocalDateTimeSerializer {
   @Override
   public void serialize(LocalDateTime value, JsonGenerator g, SerializerProvider provider) throws IOException {
      g.writeString(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(value));
   }
}
