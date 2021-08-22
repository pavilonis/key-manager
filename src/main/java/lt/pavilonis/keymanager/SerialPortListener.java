package lt.pavilonis.keymanager;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class SerialPortListener implements SerialPortEventListener {

   private static final Logger LOGGER = LoggerFactory.getLogger(SerialPortListener.class);
   private final SerialPort serialPort;
   private final Consumer<String> inputConsumer;

   public SerialPortListener(String portName, Consumer<String> inputConsumer) {
      this.serialPort = createSerialPort(portName);
      this.inputConsumer = inputConsumer;
   }

   @Override
   public void serialEvent(SerialPortEvent event) {
      extractData(event).ifPresent(inputConsumer);
   }

   private Optional<String> extractData(SerialPortEvent event) {
      if (event.isRXCHAR()) {//If data is available
         return process(read());
      }

      if (event.isCTS()) {//If CTS line has changed state
         if (event.getEventValue() == 1) {//If line is ON
            LOGGER.warn("CTS - ON");
         } else {
            LOGGER.warn("CTS - OFF");
         }
      } else if (event.isDSR()) {///If DSR line has changed state
         if (event.getEventValue() == 1) {//If line is ON
            LOGGER.warn("DSR - ON");
         } else {
            LOGGER.warn("DSR - OFF");
         }
      }
      return Optional.empty();
   }

   private Optional<String> process(String string) {
      if (string != null && string.trim().length() > 0) {
         String result = string
               .replace("\r", "")
               .replace("\n", "");

         LOGGER.info("Read result: {}", result);
         return Optional.of(result);
      } else {
         LOGGER.warn("Empty read result");
         return Optional.empty();
      }
   }

   private String read() {
      try {
         Thread.sleep(50);
         return serialPort.readString();
      } catch (InterruptedException | SerialPortException e) {
         LOGGER.error("Failed to read data from serial port", e);
      }
      return "error";
   }

   private SerialPort createSerialPort(String portName) {

      List<String> availablePorts = List.of(SerialPortList.getPortNames());
      LOGGER.info("Ports found: {}", availablePorts);
      if (!availablePorts.contains(portName)) {
         LOGGER.warn("Port {} not found in list of available ports - should not work", portName);
      }
      LOGGER.info("Trying to use port: {}", portName);

      var port = new SerialPort(portName);
      try {
         port.openPort();
         port.setEventsMask(SerialPort.MASK_RXCHAR);
         port.setRTS(false);
         port.setDTR(false);
         port.setParams(
               SerialPort.BAUDRATE_9600,
               SerialPort.DATABITS_8,
               SerialPort.STOPBITS_1,
               SerialPort.PARITY_NONE
         );

         port.addEventListener(this);
         LOGGER.info("Listener added");

      } catch (SerialPortException e) {
         LOGGER.error("Failed to configure serial port", e);
      }
      return port;
   }
}
