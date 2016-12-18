package lt.pavilonis.scan.cmm.client.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.concurrent.Callable;

public class BackgroundTask<T> extends Service<T> {

   private final Callable<T> action;

   public BackgroundTask(Callable<T> action) {
      this.action = action;
      this.start();
   }

   @Override
   protected Task<T> createTask() {
      return new Task<T>() {
         @Override
         protected T call() throws Exception {
            return action.call();
         }
      };
   }
}
