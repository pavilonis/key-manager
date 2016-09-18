package lt.pavilonis.cmmscan.client.representation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDate;

public class UserRepresentation {
   public final String cardCode;
   public final String firstName;
   public final String lastName;
   public final String description;
   public final boolean isStudent;
   public final LocalDate birthDate;
   public final String photoUrl;

   public UserRepresentation(@JsonProperty("cardCode") String cardCode,
                             @JsonProperty("firstName") String firstName,
                             @JsonProperty("lastName") String lastName,
                             @JsonProperty("description") String description,
                             @JsonProperty("isStudent") boolean isStudent,
                             @JsonProperty("photoUrl") String photoUrl,
                             @JsonProperty("birthDate")
                             @JsonSerialize(using = IsoLocalDateSerializer.class) LocalDate birthDate) {

      this.cardCode = cardCode;
      this.firstName = firstName;
      this.lastName = lastName;
      this.description = description;
      this.isStudent = isStudent;
      this.photoUrl = photoUrl;
      this.birthDate = birthDate;
   }
}
