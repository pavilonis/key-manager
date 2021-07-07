package lt.pavilonis.scan.cmm.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

   public String cardCode;
   public String name;
   public String group;
   public String role;
   public String birthDate;
   public String base16photo;

   public User(@JsonProperty("cardCode") String cardCode,
               @JsonProperty("name") String name,
               @JsonProperty("organizationGroup") String group,
               @JsonProperty("organizationRole") String role,
               @JsonProperty("base16photo") String base16photo,
               @JsonProperty("birthDate") String birthDate) {

      this.cardCode = cardCode;
      this.name = name;
      this.group = group;
      this.role = role;
      this.base16photo = base16photo;
      this.birthDate = birthDate;
   }
}

