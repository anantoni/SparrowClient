/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package client;

/**
 *
 * @author jim
 * @refactoring anantoni
 */
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import utilities.structs.Pair;

public class Client {
    public static int MAX_PARALLEL_CLIENTS = 50;
    public static ArrayList<ArrayList<String>> resultArray;
    public static String[] availableTasks;

    public static void main(String[] args) {
        ExponentialDistribution ed = new ExponentialDistribution(100);
        ArrayList<Pair<String, String>> schedulers = loadSchedulers();
        int numberOfClientThreads = 2;         

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
                  
        ExecutorService executor = Executors.newFixedThreadPool(4);
        
        for (int i = 0; i < numberOfClientThreads; i++) {
            Runnable worker = new ClientThread(httpClient, schedulers, i, i, ed);
            executor.execute(worker);
        }
        
        executor.shutdown();
        while (!executor.isTerminated()) {}
        try {
            httpClient.close();
        } 
        catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    

    private static ArrayList<Pair<String, String>> loadSchedulers(){        
        ArrayList<Pair<String, String>> schedulers = new ArrayList<>();

        Properties prop = new Properties();
        InputStream input = null;

        try {
            // open available schedulers property file
            input = new FileInputStream("./config/available_schedulers.properties");

            // load the properties file properties file
            prop.load(input);

            // get the property value and print it out
            schedulers.add(new Pair<>( prop.getProperty("scheduler1.hostname"), 
                                                           prop.getProperty("scheduler1.port")));
            //schedulers.add(new Pair<>( prop.getProperty("scheduler2.hostname"), 
            //prop.getProperty("scheduler2.port")));
        } catch (IOException ex) {
                        ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                        input.close();
                } catch (IOException e) {
                        e.printStackTrace();
                }   
            }
        }

        return schedulers;
    }
}