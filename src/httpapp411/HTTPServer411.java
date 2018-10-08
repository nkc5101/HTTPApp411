/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpapp411;

// Adapted from Reese, JM (2015) Learning Network Programming with Java.
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Executors;

public class HTTPServer411 {

    static File file = new File("diary.txt");

    public static void main(String[] args) throws Exception {
        System.out.println("MyHTTPServer Started");
        HttpServer server = HttpServer.create(new InetSocketAddress(80), 0);
        server.createContext("/diary", new DetailHandler());
        server.setExecutor(Executors.newCachedThreadPool());
        server.setExecutor(Executors.newFixedThreadPool(5));
        server.start();
    }

    public static String getResponse() throws FileNotFoundException {
        StringBuilder responseBuffer = new StringBuilder();
        Scanner in = new Scanner(file);
        String data = " ";

        while (in.hasNextLine()) {
            data = data + " " + in.nextLine();
        }
        responseBuffer
                .append("<html><h1>My Diary</h1><br>")
                .append("<b>Welcome to the Diary!</b><br><br>")
                .append(data.toString() + "<br>")
                .append("</html>");
        return responseBuffer.toString();
    }

    static class IndexHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println(exchange.getRemoteAddress());

            String response = getResponse();
            exchange.sendResponseHeaders(200, response.length());

            OutputStream out = exchange.getResponseBody();
            out.write(response.getBytes());
            out.close();
        }
    }

    static class DetailHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Get request headers
            System.out.println("\nRequest Headers");
            Headers requestHeaders = exchange.getRequestHeaders();
            Set<String> keySet = requestHeaders.keySet();
            for (String key : keySet) {
                List values = requestHeaders.get(key);
                String header = key + " = " + values.toString() + "\n";
                System.out.print(header);
            }

            // Process GET request
            String requestMethod = exchange.getRequestMethod();
            if (requestMethod.equalsIgnoreCase("GET")) {
                // Process request body
                System.out.println("Request Body");
                InputStream in = exchange.getRequestBody();
                if (in != null) {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(in));) {
                        String inputLine;
                        StringBuilder response = new StringBuilder();
                        while ((inputLine = br.readLine()) != null) {
                            response.append(inputLine);
                        }
                        br.close();
                        System.out.println(inputLine);
                    } catch (IOException ex) {
                        ex.getMessage();
                    }
                }
                // Manage response headers
                Headers responseHeaders = exchange.getResponseHeaders();

                // Send response headers
                String responseMessage = getResponse();
                responseHeaders.set("Content-Type", "text/html");
                responseHeaders.set("Server", "MyHTTPServer/1.0");
                responseHeaders.set("Set-cookie", "userID=Cookie Monster");
                exchange.sendResponseHeaders(200, responseMessage.getBytes().length);

                System.out.println("Response Headers");
                Set<String> responseHeadersKeySet = responseHeaders.keySet();
                responseHeadersKeySet
                        .stream()
                        .map((key) -> {
                            List values = responseHeaders.get(key);
                            String header = key + " = " + values.toString() + "\n";
                            return header;
                        })
                        .forEach((header) -> {
                            System.out.print(header);
                        });

                // Send message body
                try (OutputStream responseBody = exchange.getResponseBody()) {
                    responseBody.write(responseMessage.getBytes());
                }
            } else if (requestMethod.equalsIgnoreCase("POST")) {

                System.out.println("POST received");
                Map<String, Object> parameters = (Map<String, Object>) exchange.getAttribute("parameters");
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String query = br.readLine();
                query = URLDecoder.decode(query, "utf-8");
                String[] postData = query.split("=");
                BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
                writer.append("\r\n");
                writer.append(postData[1]);
                writer.close();
                
                // Manage response headers
                Headers responseHeaders = exchange.getResponseHeaders();

                // Send response headers
                String responseMessage = getResponse();
                responseHeaders.set("Content-Type", "text/html");
                responseHeaders.set("Server", "MyHTTPServer/1.0");
                responseHeaders.set("Set-cookie", "userID=Cookie Monster");
                exchange.sendResponseHeaders(200, responseMessage.getBytes().length);

                System.out.println("Response Headers");
                Set<String> responseHeadersKeySet = responseHeaders.keySet();
                responseHeadersKeySet
                        .stream()
                        .map((key) -> {
                            List values = responseHeaders.get(key);
                            String header = key + " = " + values.toString() + "\n";
                            return header;
                        })
                        .forEach((header) -> {
                            System.out.print(header);
                        });

                // Send message body
                try (OutputStream responseBody = exchange.getResponseBody()) {
                    responseBody.write(responseMessage.getBytes());
                }
            } else {

                System.out.println("Request body is empty");
            }
        }

        }

    
} 