package lt.pavilonis.cmmscan.client.dao;

import jssc.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.function.Consumer;

import static java.util.Arrays.asList;

@Component
public class SerialPortObservableReader extends Observable implements SerialPortEventListener {

   private static final Logger LOG = LoggerFactory.getLogger(SerialPortObservableReader.class.getSimpleName());

   private SerialPort serialPort;

   @Autowired
   private Observer readObserver;

   @Value(("${scanner.port.name}"))
   private String portName;

   @PostConstruct
   public void init() {

      addObserver(readObserver);

      List<String> availablePorts = asList(SerialPortList.getPortNames());
      LOG.info("Ports found: {}", availablePorts);

      if (!availablePorts.contains(portName)) {
         LOG.warn("Port {} not found in list of available ports - may not work", portName);
      }

      LOG.info("Trying to use port: {}", portName);
      serialPort = new SerialPort(portName);

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
      process(event, string -> {
         if (StringUtils.isNotBlank(string)) {
            String result = string.replace("\r", "").replace("\n", "");
            LOG.info("Read result: {}", result);
            setChanged();
            notifyObservers(result);
         } else {
            LOG.warn("Empty read result");
         }
      });
   }

   private void process(SerialPortEvent event, Consumer<String> readStringConsumer) {
      int bytesNum = event.getEventValue();
      if (event.isRXCHAR()) {//If data is available

         readStringConsumer.accept(read());

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
