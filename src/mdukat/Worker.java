package mdukat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*

Worker - accepts connection and handles request
Called from: Server
Calls: User-made request handler

String requestPath - Path in GET/POST request line
String requestType - GET or POST

 */

public class Worker extends Thread{

    private PrintWriter out;
    private BufferedReader in;
    private Socket clientSocket;
    private String requestString;
    private String requestPath;
    private String requestType;
    private Map<String, Function<String, String>> endpointList;

    public Worker() {
        super();
        endpointList = new HashMap<>();
    }

    public void setClientSocket(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    public void addEndpoint(String requestType, String requestPath, Function<String, String> endpoint){
        this.endpointList.put(requestType+requestPath, endpoint);
    }

    public void run(){
        System.out.println("New worker spawned");
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.requestString = new String();

            // Parse HTTP request

            // Read lines
            String requestLine = in.readLine();
            while(requestLine.length() != 0){
                this.requestString += requestLine + "\n";
                requestLine = in.readLine();
            }

            // Find HTTP Path
            // TODO get version group after HTTP
            Pattern pattern = Pattern.compile("^(?<request>(GET|POST)) (?<path>.+) HTTP/1.1$", Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(this.requestString);

            while(matcher.find()){
                if(matcher.groupCount() == 3) {
                    this.requestPath = matcher.group("path");
                    this.requestType = matcher.group("request");
                    break;
                }
            }

            // Call user-made handler
            String output = new String();
            try {
                output = endpointList.get(this.requestType + this.requestPath).apply(this.requestString);

            } catch(java.lang.NullPointerException e){
                // TODO probably do it better
                output = "404";

            } catch(Exception e){
                System.out.println(e);
                e.printStackTrace();
            }

            // TODO better response
            if(output != "404")
                output = "HTTP/1.1 200 OK\n\n" + output;
            else
                output = "HTTP/1.1 404 NOT FOUND\n\n";

            // Send response
            out.println(output);

            // Close connection
            clientSocket.close();

        } catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }

    // example: getHeaderValue("User-Agent")
    public String getHeaderValue(String headerName){
        Pattern pattern = Pattern.compile("^" + headerName + ": (?<value>.+)$", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(this.requestString);

        while(matcher.find()){
            if(matcher.groupCount() == 1) {
                return matcher.group("value");
            }
        }
        return "";
    }
}
