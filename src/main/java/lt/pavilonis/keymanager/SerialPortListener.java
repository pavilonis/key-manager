package lt.pavilonis.keymanager;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
public class SerialPortListener implements SerialPortEventListener {

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
            log.warn("CTS - ON");
         } else {
            log.warn("CTS - OFF");
         }
      } else if (event.isDSR()) {///If DSR line has changed state
         if (event.getEventValue() == 1) {//If line is ON
            log.warn("DSR - ON");
         } else {
            log.warn("DSR - OFF");
         }
      }
      return Optional.empty();
   }

   private Optional<String> process(String string) {
      if (string != null && string.trim().length() > 0) {
         String result = string
               .replace("\r", "")
               .replace("\n", "");

         log.info("Read result: {}", result);
         return Optional.of(result);
      } else {
         log.debug("Empty read result");
         return Optional.empty();
      }
   }

   private String read() {
      try {
         Thread.sleep(50);
         return serialPort.readString();
      } catch (InterruptedException | SerialPortException e) {
         log.error("Failed to read data from serial port", e);
      }
      return "error";
   }

   private SerialPort createSerialPort(String portName) {

      List<String> availablePorts = List.of(SerialPortList.getPortNames());
      log.info("Ports found: {}", availablePorts);
      if (!availablePorts.contains(portName)) {
         log.warn("Port {} not found in list of available ports - should not work", portName);
      }
      log.info("Trying to use port: {}", portName);

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
         log.info("Listener added");

      } catch (SerialPortException e) {
         log.error("Failed to configure serial port", e);
      }
      return port;
   }
}
