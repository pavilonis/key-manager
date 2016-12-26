package lt.pavilonis.scan.cmm.client.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class BackgroundTask<T> extends Service<T> {

   private final Runnable action;

   public BackgroundTask(Runnable action) {
      this.action = action;
      this.start();
   }

   @Override
   protected Task<T> createTask() {
      return new Task<T>() {
         @Override
         protected T call() throws Exception {
            action.run();
            return null;
         }
      };
   }
}
