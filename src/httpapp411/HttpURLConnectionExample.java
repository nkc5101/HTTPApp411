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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class HttpURLConnectionExample {

    private final String USER_AGENT = "Mozilla/5.0";

    public static void main(String[] args) throws Exception {
        HttpURLConnectionExample http = new HttpURLConnectionExample();
        
        String userInput =" ";
        Scanner in = new Scanner(System.in);
        
        System.out.println("Sent Http GET request");
        
        
        http.sendGet();
        
        while(!userInput.equals("quit")){
            System.out.print("Diary entry: ");
            userInput = in.nextLine();
            http.sendPost(userInput);
        }
        

    }

    private void sendGet() {
        try {
            String urlQuery = "http://127.0.0.1/";
            String userQuery = "diary";
            String urlEncoded = urlQuery + URLEncoder.encode(userQuery, "UTF-8");
            System.out.println(urlEncoded);
            
            String query = "http://127.0.0.1/diary";
            URL url = new URL(query);
          url = new URL(urlEncoded);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = connection.getResponseCode();

            System.out.println("Response Code: " + responseCode);
            if (responseCode == 200) {
                String response = getResponse(connection);
                System.out.println("response: " + response.toString());
            } else {
                System.out.println("Bad Response Code: " + responseCode);
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String getResponse(HttpURLConnection connection) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));) {

            // Process headers
            System.out.println("Request Headers");
            Map<String, List<String>> requestHeaders = connection.getHeaderFields();
            Set<String> keySet = requestHeaders.keySet();
            for (String key : keySet) {
                if ("Set-cookie".equals(key)) {
                    List values = requestHeaders.get(key);
                    String cookie = key + " = " + values.toString() + "\n";
                    String cookieName = cookie.substring(0, cookie.indexOf("="));
                    String cookieValue = cookie.substring(cookie.indexOf("=") + 1, cookie.length());
                    System.out.println(cookieName + ":" + cookieValue);
                }
            }
            System.out.println();

            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            return response.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    private void sendPost(String userInput) {
        try{
            String postURL = "http://127.0.0.1/diary";
            String charSet = "utf-8";
            String query = String.format("userContent=%s", URLEncoder.encode(userInput, charSet));
            URL url = new URL(postURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            
            connection.setDoOutput(true);
            OutputStream out = connection.getOutputStream();
            out.write(query.getBytes());
            
            int responseCode = connection.getResponseCode();

            System.out.println("Response Code: " + responseCode);
            if (responseCode == 200) {
                String response = getResponse(connection);
                System.out.println("response: " + response.toString());
            } else {
                System.out.println("Bad Response Code: " + responseCode);
            }
        } catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }

}