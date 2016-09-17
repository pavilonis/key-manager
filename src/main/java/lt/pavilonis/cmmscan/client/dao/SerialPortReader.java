package lt.pavilonis.cmmscan.client.dao;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerialPortReader implements SerialPortEventListener {

   private static final Logger LOG = LoggerFactory.getLogger(SerialPortDao.class);
   private final SerialPort serialPort;

   public SerialPortReader(SerialPort serialPort) {
      this.serialPort = serialPort;
   }

   @Override
   public void serialEvent(SerialPortEvent event) {
      int bytesNum = event.getEventValue();
      if (event.isRXCHAR()) {//If data is available

         String result = read().replace("\r", "").replace("\n", "");
         LOG.info("Read result: " + result);

      } else if (event.isCTS()) {//If CTS line has changed state
         if (bytesNum == 1) {//If line is ON
            LOG.warn("CTS - ON");
         } else {
            LOG.warn("CTS - OFF");
         }
      } else if (event.isDSR()) {///If DSR line has changed state
         if (bytesNum == 1) {//If line is ON
            LOG.warn("DSR - ON");
         } else {
            LOG.warn("DSR - OFF");
         }
      }
   }

   private String read() {
      try {
         Thread.sleep(50);
         return serialPort.readString();
      } catch (InterruptedException | SerialPortException e) {
         e.printStackTrace();
      }
      return "error";
   }
}
