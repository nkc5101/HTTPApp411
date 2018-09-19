/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpapp411;

// Adapted from Reese, JM (2015) Learning Network Programming with Java.

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;



public class WebServer {
    
    public WebServer() {
        System.out.println("Webserver Started");
        try (ServerSocket serverSocket = new ServerSocket(80)) {
            while (true) {
                System.out.println("Waiting for client request");
                Socket remote = serverSocket.accept();
                System.out.println("Connection made");
                new Thread(new ClientHandler(remote)).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
        public static void main(String args[]) {
            new WebServer();
        }
}
