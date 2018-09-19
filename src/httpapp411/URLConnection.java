/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpapp411;

// Adapted from Reese, JM (2015) Learning Network Programming with Java.

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author SHaynes
 */
public class URLConnection {
    
    public URLConnection(){
        
        try{
            String urlStr = "https://www.oracle.com/technetwork/java/javase/overview/faqs-jsp-136696.html";
            //String urlStr = "www.packtpub.com/books/info/packt/faq/index.html";
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine = "";
            while((nextLine = in.readLine()) != null){
                System.out.println(nextLine);
            }
            in.close();
        }catch(IOException ioe){
            
        }
        
    }
    
}
