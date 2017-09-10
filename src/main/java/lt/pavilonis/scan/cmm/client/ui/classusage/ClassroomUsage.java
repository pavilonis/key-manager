package lt.pavilonis.scan.cmm.client.ui.classusage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lt.pavilonis.scan.cmm.client.User;

import java.time.LocalDateTime;

public class ClassroomUsage {

   private final int classroomNumber;
   private final LocalDateTime dateTime;
   private final User user;
   private final boolean occupied;

   public ClassroomUsage(@JsonProperty("classroomNumber") int classroomNumber,
                         @JsonProperty("dateTime") LocalDateTime dateTime,
                         @JsonProperty("user") User user,
                         @JsonProperty("occupied") boolean occupied) {

      this.classroomNumber = classroomNumber;
      this.dateTime = dateTime;
      this.user = user;
      this.occupied = occupied;
   }

   public int getClassroomNumber() {
      return classroomNumber;
   }

   public LocalDateTime getDateTime() {
      return dateTime;
   }

   public User getUser() {
      return user;
   }

   public boolean isOccupied() {
      return occupied;
   }
}
