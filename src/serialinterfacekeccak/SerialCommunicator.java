/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package serialinterfacekeccak;

import gnu.io.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TooManyListenersException;
/**
 *
 * @author Bloodhound
 */
public class SerialCommunicator implements SerialPortEventListener
{
    //passed from main GUI
    ReceiveDataGUI window = null;

    //for containing the ports that will be found
    private Enumeration ports = null;
    //map the port names to CommPortIdentifiers
    private HashMap portMap = new HashMap();

    //this is the object that contains the opened port
    private CommPortIdentifier selectedPortIdentifier = null;
    private SerialPort serialPort = null;

    //input and output streams for sending and receiving data
    private InputStream input = null;
    private OutputStream output = null;

    //a boolean flag used for enabling and disabling buttons depending
    //on whether the program is connected to a serial port or not
    private boolean bConnected = false;

    //the timeout value for connecting with the port
    final static int TIMEOUT = 2000;

    //some ascii values for for certain things
    final static int SPACE_ASCII = 32;
    final static int DASH_ASCII = 45;
    final static int NEW_LINE_ASCII = 10;

    File file;
    private CommPortIdentifier portId;
    private FileOutputStream foutStream;
    private BufferedOutputStream bos;
    private BufferedInputStream bis;
    Thread readThread;
    final static int baudRate=9600;
    //a string for recording what goes on in the program
    //this string is written to the GUI
    String logText = "";

    public SerialCommunicator(ReceiveDataGUI window)
    {
        this.window = window;
    }

    //search for all the serial ports
    //pre: none
    //post: adds all the found ports to a combo box on the GUI
    public void searchForPorts()
    {
        int i=0;
        ports = CommPortIdentifier.getPortIdentifiers();
        String []r=new String[3];
        while (ports.hasMoreElements() && i<3)
        {
            CommPortIdentifier curPort = (CommPortIdentifier)ports.nextElement();
            //get only serial ports
            r[i++]=curPort.getName();
            portMap.put(curPort.getName(), curPort);
            /*
            if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL)
            {
                window.setJComboBoxAddItem(curPort.getName());
                portMap.put(curPort.getName(), curPort);
            }
             *
             */
        }
        window.setJComboBoxAddItem(r);
    }

    //connect to the selected port in the combo box
    //pre: ports are already found by using the searchForPorts method
    //post: the connected comm port is stored in commPort, otherwise,
    //an exception is generated
    public void connect()
    {
        String selectedPort = (window.getJComboBoxSelectedItem()).toString();
        selectedPortIdentifier = (CommPortIdentifier)portMap.get(selectedPort);

        CommPort commPort = null;

        try
        {
            //the method below returns an object of type CommPort
            commPort = selectedPortIdentifier.open("ControlPanel", TIMEOUT);
            //the CommPort object can be casted to a SerialPort object
            serialPort = (SerialPort)commPort;

            //for controlling GUI elements
            setConnected(true);
            //logging
            window.updateLabels("Connected", selectedPort, baudRate+"");
            logText = selectedPort + " opened successfully.";
            //CODE ON SETTING BAUD RATE ETC OMITTED
            //XBEE PAIR ASSUMED TO HAVE SAME SETTINGS ALREADY

            //enables the controls on the GUI if a successful connection is made
            //window.keybindingController.toggleControls();
        }
        catch (PortInUseException e)
        {
            logText = selectedPort + " is in use. (" + e.toString() + ")";
        }
        catch (Exception e)
        {
            logText = "Failed to open " + selectedPort + "(" + e.toString() + ")";
        }
        finally
        {
            System.out.println(logText);
        }
    }

    //open the input and output streams
    //pre: an open port
    //post: initialized input stream to receive serial data and output stream to
    //write data to file
    public boolean initIOStream()
    {
        //return value for whather opening the streams is successful or not
        boolean successful = false;
        try
        {
            //setting up input streams for receiving data and output stream for writing data to file
            input=serialPort.getInputStream();
            output=serialPort.getOutputStream();
            file=new File("d:/RawOut.txt");
            if(!file.exists())
                file.createNewFile();
            try
            {
                foutStream=new FileOutputStream(file);
                //bos = new BufferedOutputStream(foutStream);
                successful=true;
            }
            catch (IOException e)
            {
                System.out.println(e);
            }
            //outputStream=serialPort.getOutputStream();
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
        finally
        {
            return successful;
        }
    }

    //starts the event listener that knows whenever data is available to be read
    //pre: an open serial port
    //post: an event listener for the serial port that knows when data is recieved
    public void initListener()
    {
        try
        {
            //adding event listener to check receiving data
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        }
        catch(TooManyListenersException e)
        {
            System.out.println(e);
        }
        try
        {
            serialPort.setSerialPortParams(baudRate,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
        }
        catch(UnsupportedCommOperationException e)
        {
            System.out.println(e);
        }
    }

    //disconnect the serial port
    //pre: an open serial port
    //post: clsoed serial port
    public void disconnect()
    {
        //close the serial port
        try
        {
            serialPort.removeEventListener();
            serialPort.close();
            input.close();
            output.close();
            foutStream.close();
            setConnected(false);
            //window.keybindingController.toggleControls();
            window.updateLabels("Disconnected", "0", "0");
            logText = "Disconnected.";
            //window.txtLog.setForeground(Color.red);
            //window.txtLog.append(logText + "\n");

            //analyse the text generated and obtain the correct hash value.
        }
        catch (Exception e)
        {
            logText = "Failed to close " + serialPort.getName() + "(" + e.toString() + ")";
            //window.txtLog.setForeground(Color.red);
            //window.txtLog.append(logText + "\n");
        }
        finally
        {
            System.out.println(logText);
        }
    }

    final public boolean getConnected()
    {
        return bConnected;
    }

    public void setConnected(boolean bConnected)
    {
        this.bConnected = bConnected;
    }

    //what happens when data is received
    //pre: serial event is triggered
    //post: processing on the data it reads
    public void serialEvent(SerialPortEvent event)
    {

        switch (event.getEventType())
        {
            case SerialPortEvent.BI:
            case SerialPortEvent.OE:
            case SerialPortEvent.FE:
            case SerialPortEvent.PE:
            case SerialPortEvent.CD:
            case SerialPortEvent.CTS:
            case SerialPortEvent.DSR:
            case SerialPortEvent.RI:
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                break;
            case SerialPortEvent.DATA_AVAILABLE:
                byte readBuffer[]=new byte[150];
                byte buff;
                try
                {
                    int av;
                    while ((av=input.available())>0)
                    {
                        int numBytes = input.read(readBuffer);
                        //System.out.print(new String(readBuffer));
                        //read=input.read(readBuffer);
                        //read=input.read();
                        String y=new String(readBuffer, 0, numBytes);
                        foutStream.write(readBuffer, 0, numBytes);
                        //foutStream.flush();
                        //foutStream.write(readBuffer);
                        //foutStream.flush();
                        //System.out.print(new String(readBuffer));
                        window.setJTextAreaText(y);
                        System.out.println("av="+av+"\tread="+numBytes);
                        waiting(1);    //waiting time in millisec
                    }
                    //bos.flush();
                }
                catch (IOException e)
                {
                    System.out.println(e);
                }
                break;
        }
    }

    void sendMessage(String msg)
    {
        byte writeBuff[]=msg.getBytes();
        int i=0;
        try
        {
            while(i<writeBuff.length)
            {
                output.write(writeBuff[i++]);
                waiting(1);
            }

        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
        }
    }


    public static void waiting(int n)
    {
        long t0,t1;
        t0=System.currentTimeMillis();
        do
        {
            t1=System.currentTimeMillis();
        }
        while((t1-t0)<n);
        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
