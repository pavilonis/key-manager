package lt.pavilonis.scan.cmm.client.ui.classusage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScanLogBrief {

   private final LocalDateTime dateTime;
   private final String scanner;
   private final String cardCode;
   private final String name;
   private final String group;
   private final String role;
   private final String location;

   public ScanLogBrief(@JsonProperty("dateTime") LocalDateTime dateTime,
                       @JsonProperty("scannerName") String scannerName,
                       @JsonProperty("cardCode") String cardCode,
                       @JsonProperty("name") String name,
                       @JsonProperty("group") String group,
                       @JsonProperty("role") String role,
                       @JsonProperty("location") String location) {

      this.dateTime = dateTime;
      this.scanner = scannerName;
      this.cardCode = cardCode;
      this.name = name;
      this.group = group;
      this.role = role;
      this.location = location;
   }

   public LocalDateTime getDateTime() {
      return dateTime;
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

   public String getScanner() {
      return scanner;
   }

   public String getLocation() {
      return location;
   }
}
