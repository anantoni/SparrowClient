/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import utilities.structs.Pair;

/**
 *
 * @author jim
 */
public class ClientThread implements Runnable{
        private static final int ERROR = 1;
        private final String schedulerHostname;
        private final int schedulerPort;
        private Socket socket;

        /*constructor*/
        public ClientThread(ArrayList<Pair<String, String>> schedulers, int resPos, int threadCounter) {
                Pair<String, Integer> chosenScheduler = chooseScheduler(schedulers);
                this.schedulerHostname = chosenScheduler.getVar1();
                this.schedulerPort = chosenScheduler.getVar2();
        }
    
        /*interface methods*/
        @Override
        public void run() {        
                //set number of jobs
                int numOfJobs = 5000;
                ExponentialDistribution ed = new ExponentialDistribution(100);
                
                
                for(int j=0; j< numOfJobs; j++){
                    CloseableHttpClient httpclient = HttpClients.createDefault();
                    //http request     
                    try {
                        
                        HttpPost httpPost = new HttpPost(schedulerUrl());
                        httpPost.setProtocolVersion(HttpVersion.HTTP_1_1);
                        List <NameValuePair> nvps = new ArrayList <>();
                        nvps.add(new BasicNameValuePair("task-duration", String.valueOf(exponentialProduceTaskDuration(ed))));
                        nvps.add(new BasicNameValuePair("task-quantity", String.valueOf(10)));
                        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
                        System.out.println("sending task request");
                        CloseableHttpResponse response = httpclient.execute(httpPost);
                        try {
                            System.out.println(response.getStatusLine());
                            HttpEntity entity2 = response.getEntity();
                            EntityUtils.consume(entity2);
                        }   
                        catch (IOException ex) {
                            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
                        } 
                        finally {
                            try {
                                response.close();
                            } 
                            catch (IOException ex) {
                                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }   
                    catch (UnsupportedEncodingException ex) {
                            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
                    } 
                    catch (IOException ex) {
                            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
                    } 
                    finally {
                        try {
                            httpclient.close();
                        } 
                        catch (IOException ex) {
                            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }       
            }
    }    
    
    private int exponentialProduceTaskDuration(ExponentialDistribution ed) {
        return (int)ed.sample();
    }
        
    /*private-helper methods*/
    private Pair<String, Integer> chooseScheduler(ArrayList<Pair<String, String>> schedulers){       
        Collections.shuffle(schedulers);
       
         return new Pair<>(schedulers.get(0).getVar1(), Integer.parseInt(schedulers.get(0).getVar2()));
    }
    
    private String schedulerUrl(){
        StringBuilder surl = new StringBuilder("http://");
        return surl.append(this.schedulerHostname).append(":").append(this.schedulerPort).toString();
    }
   

   private int randInt(int min, int max) {
        Random rand = new Random();
        return  rand.nextInt((max - min) + 1) + min;
    }
   
   //    private ArrayList<String> produceJob(int j){
//        ArrayList<String> job = new ArrayList<>();
//        int jobSelection = randInt(0,1);
//        int jobSelection = j%2;
//        if (jobSelection == 0)
//           for (int i = 0; i < 10; i++) 
//                job.add("task3.sh");
//        else {
//            if (threadCounter % 3 == 1)
//                for (int i = 0; i < 100; i++) 
//                    job.add("task4.sh");
//            else 
//                for (int i = 0; i < 10; i++)
//                    job.add("task1.sh");
//        }
//        return job;
//    }
}