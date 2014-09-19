/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package client;

/**
 *
 * @author jim
 */
// tcpClient.java by fpont 3/2000

// usage : java tcpClient <server> <port>
// default port is 1500

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import utilities.structs.Pair;

public class Client {
        public static int MAX_PARALLEL_CLIENTS = 50;
        private static final int MAX_AVAILABLE_BASH_TASKS = 2;
        public static ArrayList<ArrayList<String>> resultArray;
        public static String[] availableTasks;
    
        public static void main(String[] args) {
                initTasks();
                ArrayList<Pair<String, String>> schedulers = loadSchedulers();
///////////        int numberOfClientThreads = Integer.parseInt(args[0]);
                int numberOfClientThreads = 2;
        
                resultArray = new ArrayList<>();
                for(int i = 0; i<numberOfClientThreads ; i++)
                        resultArray.add(new ArrayList<>());
        
                ExecutorService executor = Executors.newFixedThreadPool(MAX_PARALLEL_CLIENTS);

                for (int i = 0; i < numberOfClientThreads; i++) {
                        Runnable worker = new ClientThread(schedulers, i);
                        executor.execute(worker);
                }

                executor.shutdown();

                while (!executor.isTerminated()) {}	
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
                        int nos = Integer.parseInt(prop.getProperty("numberOfSchedulers"));
                        schedulers.add(new Pair<>( prop.getProperty("scheduler1.hostname"), 
                                                                       prop.getProperty("scheduler1.port")));
                        schedulers.add(new Pair<>( prop.getProperty("scheduler2.hostname"), 
                                                                       prop.getProperty("scheduler2.port")));
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

    private static void initTasks(){
        availableTasks = new String[MAX_AVAILABLE_BASH_TASKS];
        availableTasks[0] = "ls -al";
        availableTasks[1] = "date";
    }
}