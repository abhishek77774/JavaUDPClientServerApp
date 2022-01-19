package com.demo.server;

import com.demo.model.Customer;
import com.demo.utility.WriteToFile;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;

/**
 * This class sets up and starts the UDP Server
 */
public class UDPServer {

    private final DatagramSocket udpSocket;
    private final int port;
    private InetAddress ipAddress;
    private int senderPort;
    private final int writeToFileTimerInSeconds = 120;
    private final double initialCost = 0.0;
    private final double costAfterFreeTravel = 3.0;
    private final Map<String,String> fileData;
    private List<Customer> customerList = new ArrayList<>();
    private static final String filePath = "src/com/demo/helper/Member.txt";

    public static void main(String[] args) throws Exception {
        UDPServer client = new UDPServer(Integer.parseInt(args[0]));
        client.listen();
    }

    public UDPServer(int port) throws IOException {
        this.port = port;
        this.udpSocket = new DatagramSocket(this.port);
        fileData = getHashMapFromTextFile();
        callWriteToFileTimer();
    }

    /**
     * This method starts the UDP Server to listen client requests
     */
    private void listen() throws Exception {
        System.out.println("UDP Server running...");
        String message;

        while (true) {
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            udpSocket.receive(packet);

            ipAddress = packet.getAddress();
            senderPort = packet.getPort();

            message = new String(packet.getData()).trim();

            processCustomerRequest(message);
        }
    }

    /**
     * This method processes the customer request and returns appropriate response
     * @param message Message from the client
     * @throws IOException IOException
     */
    private void processCustomerRequest(String message) throws IOException {
        String[] enteredDetails = message.split(":");
        String clientId = enteredDetails[0];
        String clientPin = enteredDetails[1];
        String operation = enteredDetails[2];

        if(fileData.containsKey(clientId))
        {
            if(fileData.get(clientId).equals(clientPin)) {

                Customer customer;
                int index = getLastIndexOfCustomer(clientId);

                if (operation.equals("Out") && customerList.isEmpty()) {
                    sendMessageToClient("Error-Already signed-out");
                    return;
                } else if (operation.equals("Out") && !customerList.get(index).isStatus()) {
                    sendMessageToClient("Error-Already signed-out");
                    return;
                } else if (operation.equals("Out") && customerList.get(index).isStatus()) {
                    customer = new Customer();
                    customer.setClientId(clientId);
                    customer.setPinNumber(Integer.parseInt(clientPin));
                    customer.setStatus(false);
                    customer.setNumberOfTravels(customerList.get(index).getNumberOfTravels());
                    customer.setTotalCost(customerList.get(index).getTotalCost());
                    customerList.set(index, customer);
                    sendMessageToClient("Success-Good bye");
                    return;
                }

                if (operation.equals("In")) {
                    if (customerList.isEmpty()) {
                        customer = new Customer();
                        customer.setClientId(clientId);
                        customer.setPinNumber(Integer.parseInt(clientPin));
                        customer.setStatus(true);
                        customer.setNumberOfTravels(1);
                        customer.setTotalCost(initialCost);
                        sendMessageToClient("Success-Welcome");
                        customerList.add(customer);
                        printData();
                        return;
                    }

                    else if(customerList.get(index).isStatus())
                    {
                        sendMessageToClient("Error-Already signed-in");
                        return;
                    }

                    else {
                        customer = new Customer();
                        customer.setClientId(clientId);
                        customer.setPinNumber(Integer.parseInt(clientPin));
                        customer.setStatus(true);
                        customer.setNumberOfTravels(customerList.get(index).getNumberOfTravels() + 1);
                        customer.setTotalCost(initialCost);
                        if (customerList.get(index).getNumberOfTravels() >= 5) {
                            customer.setTotalCost(customerList.get(index).getTotalCost() + costAfterFreeTravel);
                        }
                        customerList.add(customer);
                        printData();
                        sendMessageToClient("Success-Welcome");
                        return;
                    }
                }
            }
            sendMessageToClient("Error-Invalid Pin Number");
        }
        else
        {
            sendMessageToClient("Invalid customer");
        }
    }

    /**
     * This method sends response to the client
     * @param message Message as String
     * @throws IOException IOException
     */
    public void sendMessageToClient(String message) throws IOException {
        DatagramPacket datagramPacket = new DatagramPacket(
                message.getBytes(), message.getBytes().length, ipAddress, senderPort);
        udpSocket.send(datagramPacket);
    }

    /**
     * This method loads the text file data into a HashMap
     * @return a HashMap
     */
    public static Map<String, String> getHashMapFromTextFile()
    {
        Map<String, String> map = new HashMap<>();
        BufferedReader br = null;

        try {
            File file = new File(filePath);
            br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {

                String[] parts = line.split(" ");

                String clientId = parts[0].trim();
                String clientPin = parts[1].trim();

                if (!clientId.equals("") && !clientPin.equals(""))
                    map.put(clientId, clientPin);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

    /**
     * This method gets the last index of a specific customer
     * @param clientId clientId as String
     * @return last index in the record (ArrayList)
     */
    public int getLastIndexOfCustomer(String clientId) {
        if (clientId == null) {
            for (int i = customerList.size()-1; i >= 0; i--)
                if (customerList.get(i) == null)
                    return i;
        } else {
            for (int i = customerList.size()-1; i >= 0; i--)
                if (clientId.equals(customerList.get(i).getClientId()))
                    return i;
        }
        return -1;
    }

    /**
     * Prints the data on console
     */
    private void printData() {
        for (Customer c : customerList) {
            System.out.print(c.getClientId());
            System.out.print(" " + c.getNumberOfTravels());
            System.out.print(" " + "$"+c.getTotalCost());
            System.out.println();
        }
    }

    /**
     * This method calls the WriteToFile class thread to write
     * customerList object to a binary file at given interval
     */
    public void callWriteToFileTimer()
    {
        WriteToFile writeToFile = new WriteToFile((ArrayList<Customer>) customerList);
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(writeToFile, 0, writeToFileTimerInSeconds*1000);
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        timer.cancel();
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
