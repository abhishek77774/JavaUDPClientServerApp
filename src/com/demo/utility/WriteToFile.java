package com.demo.utility;

import com.demo.model.Customer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.TimerTask;

/**
 * This class helps to write object to binary file
 */
public class WriteToFile extends TimerTask {

    private final String binaryFileName = "Member.dat";
    private final String filePath = "src/com/demo/helper/";
    ArrayList<Customer> customerList;

    public WriteToFile(ArrayList<Customer> customerList) {
        this.customerList = customerList;
    }

    @Override
    public void run() {
        try {
            FileOutputStream fos = new FileOutputStream(filePath+binaryFileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(customerList);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
