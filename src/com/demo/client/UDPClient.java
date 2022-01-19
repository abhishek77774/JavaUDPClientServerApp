package com.demo.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * This class sets up and starts the UDP Client
 */
public class UDPClient {

    private DatagramSocket udpSocket;
    private InetAddress serverAddress;
    private int port;
    private Scanner scanner;

    private UDPClient(String serverAddress, int port) throws IOException {
        this.serverAddress = InetAddress.getByName(serverAddress);
        this.port = port;
        udpSocket = new DatagramSocket();
        scanner = new Scanner(System.in);
    }

    public static void main(String[] args) throws NumberFormatException, IOException {
        UDPClient sender = new UDPClient(args[0], Integer.parseInt(args[1]));
        sender.start();
    }

    /**
     * This method starts the UDP Client
     */
    private int start() throws IOException {
        int choice;
        String message = "";

        while (true) {
            System.out.println("*************Travel Kiosk*************");
            System.out.println("              1. IN                   ");
            System.out.println("              2. OUT                  ");
            System.out.println("              3. EXIT                 ");
            System.out.println("Enter:");

            choice = scanner.nextInt();

            switch (choice)
            {
                case 1:
                    System.out.println("Enter client id:");
                    message = message + scanner.next();
                    message = message + ":";
                    System.out.println("Enter client pin:");
                    message = message + scanner.nextInt();
                    message = message + ":";
                    message = message + "In";
                    sendMessage(message.trim());
                    getMessage();
                    message = "";
                    break;

                case 2:
                    System.out.println("Enter client id:");
                    message = message + scanner.next();
                    message = message + ":";
                    System.out.println("Enter client pin:");
                    message = message + scanner.nextInt();
                    message = message + ":";
                    message = message + "Out";
                    sendMessage(message.trim());
                    getMessage();
                    message = "";
                    break;

                case 3:
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid Choice");
                    break;
            }
        }
    }

    /**
     * This method sends message to the UDP Server
     * @param message Message as String
     * @throws IOException IOException
     */
    private void sendMessage(String message) throws IOException {
        DatagramPacket datagramPacket = new DatagramPacket(
                message.getBytes(), message.getBytes().length, serverAddress, port);
        this.udpSocket.send(datagramPacket);
    }

    /**
     * This method receives message from the UDP Server
     * @throws IOException IOException
     */
    private void getMessage() throws IOException {
        byte[] buffer = new byte[1024];

        DatagramPacket receivingPacket = new DatagramPacket(buffer, buffer.length);
        udpSocket.receive(receivingPacket);

        String receivedData = new String(receivingPacket.getData());
        System.out.println("Server Response: "+ receivedData.trim());
    }
}
