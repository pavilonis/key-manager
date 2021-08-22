package lt.pavilonis.keymanager.ui.keyassignment;

import org.springframework.util.StringUtils;

final class KeyAssignmentFilter {

   private final String keyNumber;
   private final String name;

   KeyAssignmentFilter(String keyNumber, String name) {
      this.keyNumber = StringUtils.hasText(keyNumber) ? keyNumber : null;
      this.name = StringUtils.hasText(name) ? name : null;
   }

   String getKeyNumber() {
      return keyNumber;
   }

   String getName() {
      return name;
   }
}
