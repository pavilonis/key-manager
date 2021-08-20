package lt.pavilonis.scan.cmm.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

   private final String cardCode;
   private final String name;
   private final String group;
   private final String role;
   private final String birthDate;
   private final String base64photo;
   private final User supervisor;

   public User(@JsonProperty("cardCode") String cardCode,
               @JsonProperty("name") String name,
               @JsonProperty("organizationGroup") String group,
               @JsonProperty("organizationRole") String role,
               @JsonProperty("base64photo") String base64photo,
               @JsonProperty("birthDate") String birthDate,
               @JsonProperty("supervisor") User supervisor) {

      this.cardCode = cardCode;
      this.name = name;
      this.group = group;
      this.role = role;
      this.base64photo = base64photo;
      this.birthDate = birthDate;
      this.supervisor = supervisor;
   }

   public String getCardCode() {
      return cardCode;
   }

   public String getName() {
      return name;
   }

   public String getGroup() {
      return group;
   }

   public String getRole() {
      return role;
   }

   public String getBirthDate() {
      return birthDate;
   }

   public String getBase64photo() {
      return base64photo;
   }

   public User getSupervisor() {
      return supervisor;
   }
}

