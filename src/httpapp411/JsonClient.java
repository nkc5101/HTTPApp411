/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpapp411;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 *
 * @author nate
 */
public class JsonClient {

    public static void main(String[] args) throws Exception {

        JsonClient http = new JsonClient();

        String userInput = " ";
        Scanner in = new Scanner(System.in);

        while (!userInput.equalsIgnoreCase("quit")) {
            System.out.println("GET or POST?");
            userInput = in.nextLine();
            if (userInput.equalsIgnoreCase("GET")) {
                System.out.println("What user would you like to GET?");
                userInput = in.nextLine();
                http.sendGet(userInput);
            } else if (userInput.equalsIgnoreCase("POST")) {

                System.out.println("You are creating a new user");
                System.out.print("Username: ");
                String username = in.nextLine();
                System.out.print("Password: ");
                String password = in.nextLine();
                System.out.print("Name: ");
                String name = in.nextLine();
                System.out.print("Address: ");
                String address = in.nextLine();

                User newUser = new User(username, password, name, address);
                http.sendPost(newUser);
            } else {
                System.out.println("Request Method not recognized");
            }

        }

    }

    private void sendGet(String userInput) {
        Gson gson = new Gson();

        try {
            String getURL = "http://127.0.0.1/get";
            String charSet = "utf-8";
            URL url = new URL(getURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestProperty("user", userInput);

            int responseCode = connection.getResponseCode();

            System.out.println("Response Code: " + responseCode);
            if (responseCode == 200) {
                String response = getResponse(connection);
                System.out.println("Response: " + response);
            } else {
                System.out.println("Bad Response Code: " + responseCode);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void sendPost(User newUser) {
        Gson gson = new Gson();
        String jsonEmp = gson.toJson(newUser);
        try {
            String postURL = "http://127.0.0.1/post";
            String charSet = "utf-8";
            String query = String.format("userContent=%s", URLEncoder.encode(jsonEmp, charSet));
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
                System.out.println("response: " + response);
            } else {
                System.out.println("Bad Response Code: " + responseCode);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private String getResponse(HttpURLConnection connection) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));) {

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
}
