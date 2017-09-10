package lt.pavilonis.scan.cmm.client.ui.classusage;

public class ClassroomUsageFilter {
   private final String text;
   private final String role;

   public ClassroomUsageFilter(String text, String role) {
      this.text = text;
      this.role = role;
   }

   public String getText() {
      return text;
   }

   public String getRole() {
      return role;
   }
}
