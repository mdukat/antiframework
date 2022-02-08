package mdukat.antiframework;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/*

Server - listens on port and spawns worker
Called from: Main
Calls: Worker

 */

public class Server {

    private ServerSocket serverSocket;
    private int port;
    private Class worker;
    public static String version = "dev";
    private static int defaultBacklog = 50;

    // Start using default IP address
    public void start(int port, Class worker){
        try {
            serverSocket = new ServerSocket(port);
            this.port = port;
            this.worker = worker;
            serve();
        } catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }

    // Start using specified IP address
    public void start(String IP, int port, Class worker){
        try {
            InetAddress ineta = InetAddress.getByName(IP);
            serverSocket = new ServerSocket(port, defaultBacklog, ineta);
            this.port = port;
            this.worker = worker;
            serve();
        } catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }

    // Start using specified IP address and backlog value
    public void start(String IP, int port, int backlog, Class worker){
        try {
            InetAddress ineta = InetAddress.getByName(IP);
            serverSocket = new ServerSocket(port, backlog, ineta);
            this.port = port;
            this.worker = worker;
            serve();
        } catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }

    private void serve(){
        try {
            System.out.println("Started antiframe server at " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Worker newWorker = (Worker) worker.getDeclaredConstructor().newInstance();
                newWorker.setClientSocket(clientSocket);
                newWorker.start();
            }
        } catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }

    public static String getServerHeader(){
        return "Server: antiframework/" + getServerVersion();
    }

    public static String getServerVersion(){
        return version;
    }
}
