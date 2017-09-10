package lt.pavilonis.scan.cmm.client;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class IsoLocalDateSerializer extends LocalDateSerializer {
   @Override
   public void serialize(LocalDate date, JsonGenerator generator, SerializerProvider provider) throws IOException {
      generator.writeString(DateTimeFormatter.ISO_LOCAL_DATE.format(date));
   }
}
