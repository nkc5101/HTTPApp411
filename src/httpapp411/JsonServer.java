/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpapp411;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.Map;
import java.util.concurrent.Executors;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nate
 */
public class JsonServer {

    static ArrayList<User> userList = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        System.out.println("MyHTTPServer Started");
        HttpServer server = HttpServer.create(new InetSocketAddress(80), 0);
        server.createContext("/users", new DetailHandler());
        server.setExecutor(Executors.newCachedThreadPool());
        server.setExecutor(Executors.newFixedThreadPool(5));
        server.start();
    }

    private static class DetailHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestMethod = exchange.getRequestMethod();
            if (requestMethod.equalsIgnoreCase("GET")) {
                getHandler(exchange);
            } else if (requestMethod.equalsIgnoreCase("POST")) {
                postHandler(exchange);
            }
        }

        private void getHandler(HttpExchange exchange) throws IOException {
            System.out.println("GET Request Received");

            Headers requestHeaders = exchange.getRequestHeaders();
            String query;
            List<String> values = requestHeaders.get("user");
            query = values.get(0);

            User requestedUser = null;
            String responseMessage;
            for (int i = 0; i < userList.size(); i++) {
                if (userList.get(i).getUsername().equalsIgnoreCase(query)) {
                    requestedUser = userList.get(i);
                }
            }

            if (requestedUser == null) {
                responseMessage = "User does not exist.";
            } else {
                Gson gson = new Gson();
                responseMessage = gson.toJson(requestedUser);
            }

            // Manage response headers
            Headers responseHeaders = exchange.getResponseHeaders();

            // Send response headers
            responseHeaders.set("Content-Type", "application/json");
            responseHeaders.set("Server", "MyHTTPServer/1.0");
            exchange.sendResponseHeaders(200, responseMessage.getBytes().length);

            // Send message body
            try (OutputStream responseBody = exchange.getResponseBody()) {
                responseBody.write(responseMessage.getBytes());
            }
        }

        private void postHandler(HttpExchange exchange) throws UnsupportedEncodingException, IOException {
            System.out.println("POST Received");
            Map<String, Object> parameters = (Map<String, Object>) exchange.getAttribute("parameters");
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            query = URLDecoder.decode(query, "utf-8");
            String[] postData = query.split("=");
            Gson gson = new Gson();
            User newUser = gson.fromJson(postData[1], User.class);
            userList.add(newUser);

            Headers responseHeaders = exchange.getResponseHeaders();

            // Send response headers
            String responseMessage = "Upload received";
            responseHeaders.set("Content-Type", "application/json");
            responseHeaders.set("Server", "MyHTTPServer/1.0");
            exchange.sendResponseHeaders(200, responseMessage.getBytes().length);

            // Send message body
            try (OutputStream responseBody = exchange.getResponseBody()) {
                responseBody.write(responseMessage.getBytes());
            }
        }

    }

}

