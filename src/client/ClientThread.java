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
import org.apache.http.HttpEntity;
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
        private static final String DEFAULT_NAME = "CLIENT:";
        private static final int ERROR = 1;
        private static final int MAX_NUM_OF_TASKS = 20;
        private static final int MIN_NUM_OF_TASKS = 10;
        private static final int MAX_NUM_OF_JOBS = 100;
        private static final int MIN_NUM_OF_JOBS = 1;


        private final String clientName; 
        private final String schedulerHostname;
        private final int schedulerPort;
        private final int resultPos;
        private Socket socket;

        /*constructor*/
        public ClientThread(ArrayList<Pair<String, String>> schedulers, int resPos) {
                this.clientName = DEFAULT_NAME + Thread.currentThread().getId();
                Pair<String, Integer> chosenScheduler = chooseScheduler(schedulers);
                this.schedulerHostname = chosenScheduler.getVar1();
                this.schedulerPort = chosenScheduler.getVar2();
                this.resultPos = resPos;
        }
    
        /*interface methods*/
        @Override
        public void run() {
        // connect to server
	try {
	    this.socket = new Socket(this.schedulerHostname, this.schedulerPort);
	    System.out.println("Connected with scheduler " +
				   this.socket.getInetAddress() +
				   ":" + this.socket.getPort());
	}
	catch (UnknownHostException e) {
	    System.out.println(e);
	    System.exit(ERROR);
	}
	catch (IOException e) {
	    System.out.println(e);
	    System.exit(ERROR);
	}
        
                //creating jobs for http requests to scheduler
                
                //set number of jobs
                int numOfJobs = 5;
                for(int i = 0; i<numOfJobs; i++)
                    Client.resultArray.get(this.resultPos).add("RES_INIT");           

                for(int j=0; j< numOfJobs; j++){
                    ArrayList<String> job = produceJob(j);
                    String jobId = Integer.toString(j);
                    StringBuilder tasks = new StringBuilder();
                    StringBuilder taskIds = new StringBuilder();

                    for(int i =  0; i < job.size(); i++){
                        if(i  < job.size() - 1){
                                tasks.append(job.get(i)).append(",");
                                taskIds.append(i).append(",");
                        }
                        else{
                                tasks.append(job.get(i));
                                taskIds.append(i);
                        }
                    }

                    //http request     
                    CloseableHttpClient httpclient = HttpClients.createDefault();
                    try {
                        HttpPost httpPost = new HttpPost(schedulerUrl());
                        List <NameValuePair> nvps = new ArrayList <>();
                        nvps.add(new BasicNameValuePair("job-id", jobId));
                        nvps.add(new BasicNameValuePair("task-commands", tasks.toString()));
                        nvps.add(new BasicNameValuePair("task-ids", taskIds.toString()));

                        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
                        System.out.println("sending task request");
                        CloseableHttpResponse response = httpclient.execute(httpPost);
                        try {
                            System.out.println(response.getStatusLine());
                            HttpEntity entity2 = response.getEntity();
                            // do something useful with the response body
                            // and ensure it is fully consumed

                            // TODO: add result to Client.resultArray.get(this.resultPos).get(j).set(`result from response message :)) `)//
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

            //closing socket...
            try {
                socket.close();
            }
            catch (IOException e) {
                System.out.println(e);
            }
    }    
    
    /*private-helper methods*/
    private Pair<String, Integer> chooseScheduler(ArrayList<Pair<String, String>> schedulers){
//         int randomScheduler = randInt(0, schedulers.size());
//         String hostname = schedulers.get(randomScheduler).getVar1();
//         int port = Integer.parseInt(schedulers.get(randomScheduler).getVar2());         
        Collections.shuffle(schedulers);
       
         return new Pair<>(schedulers.get(0).getVar1(), Integer.parseInt(schedulers.get(0).getVar2()));
    }
    
    private String schedulerUrl(){
        StringBuilder surl = new StringBuilder("http://");
        return surl.append(this.schedulerHostname).append(":").append(this.schedulerPort).toString();
    }
      
    private ArrayList<String> produceJob(int j){
        ArrayList<String> job = new ArrayList<>();
        //int jobSelection = randInt(0,1);
        int jobSelection = j%2;
        if (jobSelection == 0)
           for (int i = 0; i < 10; i++) 
                job.add("task1.sh");
        else
            for (int i = 0; i < 10; i++) 
                job.add("task2.sh");
        
        return job;
    }

   private int randInt(int min, int max) {
        Random rand = new Random();
        return  rand.nextInt((max - min) + 1) + min;
    }
}