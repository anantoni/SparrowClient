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
import javax.xml.ws.spi.http.HttpContext;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import static org.apache.http.HttpVersion.HTTP;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import utilities.structs.Pair;

public class Client {
    public static int MAX_PARALLEL_CLIENTS = 50;
    public static ArrayList<ArrayList<String>> resultArray;
    public static String[] availableTasks;

    public static void main(String[] args) {
        ExponentialDistribution ed = new ExponentialDistribution(100);
        ArrayList<Pair<String, String>> schedulers = loadSchedulers();
        int numberOfClientThreads = 4;         

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        //HttpClients.custom().set
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
                  
//        SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true)
//                                                                                        .setSoKeepAlive(true)
//                                                                                        .setSoReuseAddress(true)
//                                                                                        .build();
//        cm.setDefaultSocketConfig(socketConfig);
//        ConnectionKeepAliveStrategy keepAliveStrat = new DefaultConnectionKeepAliveStrategy() {
//            @Override
//            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
//                long keepAlive = super.getKeepAliveDuration(response, (org.apache.http.protocol.HttpContext) context);
//                if (keepAlive == -1) {
//                    // Keep connections alive 5 seconds if a keep-alive value
//                    // has not be explicitly set by the server
//                    keepAlive = 5000;
//                }
//            return keepAlive;
//            }
//        };

	
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