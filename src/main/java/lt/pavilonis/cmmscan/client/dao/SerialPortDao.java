package lt.pavilonis.cmmscan.client.dao;

import jssc.SerialPort;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static java.util.Arrays.asList;

@Component
public class SerialPortDao {

   private static final Logger LOG = LoggerFactory.getLogger(SerialPortDao.class);

   public void listenPort() {

      LOG.info("Ports found: " + asList(SerialPortList.getPortNames()));

      SerialPort serialPort = new SerialPort("/dev/ttyUSB0");

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

         serialPort.addEventListener(new SerialPortReader(serialPort));
         LOG.info("Listener added");

      } catch (SerialPortException ex) {
         LOG.info(ex.toString());
      }
   }
}
