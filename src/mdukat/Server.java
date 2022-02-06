package mdukat;

import java.net.ServerSocket;
import java.net.Socket;

/*

Server - listens on port and spawns worker
Called from: Main
Calls: Worker

 */

public class Server {

    private ServerSocket serverSocket;

    public void start(int port, Class worker){
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Started server at " + port);
            while(true){
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
}
