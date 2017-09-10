package lt.pavilonis.scan.cmm.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

   public String cardCode;
   public String firstName;
   public String lastName;
   public String group;
   public String role;
   public String birthDate;
   public String base16photo;

   public User(@JsonProperty("cardCode") String cardCode,
               @JsonProperty("firstName") String firstName,
               @JsonProperty("lastName") String lastName,
               @JsonProperty("group") String group,
               @JsonProperty("role") String role,
               @JsonProperty("base16photo") String base16photo,
               @JsonProperty("birthDate") String birthDate) {

      this.cardCode = cardCode;
      this.firstName = firstName;
      this.lastName = lastName;
      this.group = group;
      this.role = role;
      this.base16photo = base16photo;
      this.birthDate = birthDate;
   }
}

