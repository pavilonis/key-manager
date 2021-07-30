package lt.pavilonis.scan.cmm.client.ui.keyassignment;

import org.apache.commons.lang3.StringUtils;

public final class KeyAssignmentFilter {

   private final String keyNumber;
   private final String name;

   public KeyAssignmentFilter(String keyNumber, String name) {
      this.keyNumber = StringUtils.stripToNull(keyNumber);
      this.name = StringUtils.stripToNull(name);
   }

   public String getKeyNumber() {
      return keyNumber;
   }

   public String getName() {
      return name;
   }
}
