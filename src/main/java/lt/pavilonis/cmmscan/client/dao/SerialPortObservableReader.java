package lt.pavilonis.cmmscan.client.dao;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Observable;
import java.util.Observer;

import static java.util.Arrays.asList;

@Component
public class SerialPortObservableReader extends Observable implements SerialPortEventListener {

   private static final Logger LOG = LoggerFactory.getLogger(SerialPortObservableReader.class);

   private SerialPort serialPort;

   @Autowired
   private Observer readObserver;

   @PostConstruct
   public void init() {

      addObserver(readObserver);

      LOG.info("Ports found: " + asList(SerialPortList.getPortNames()));
      serialPort = new SerialPort("/dev/ttyUSB0");

      try {
         serialPort.openPort();
         serialPort.setEventsMask(SerialPort.MASK_RXCHAR);
         serialPort.setRTS(false);
         serialPort.setDTR(false);
         serialPort.setParams(
               SerialPort.BAUDRATE_9600,
               SerialPort.DATABITS_8,
               SerialPort.STOPBITS_1,
               SerialPort.PARITY_NONE
         );

         serialPort.addEventListener(this);
         LOG.info("Listener added");

      } catch (SerialPortException ex) {
         LOG.info(ex.toString());
      }
   }

   @Override
   public void serialEvent(SerialPortEvent event) {
      process(event, () -> {
         String result = read().replace("\r", "").replace("\n", "");
         LOG.info("Read result: " + result);
         setChanged();
         notifyObservers(result);
      });
   }

   private void process(SerialPortEvent event, Runnable dataReadEvent) {
      int bytesNum = event.getEventValue();
      if (event.isRXCHAR()) {//If data is available

         dataReadEvent.run();

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
