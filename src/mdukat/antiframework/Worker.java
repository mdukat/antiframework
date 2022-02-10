package mdukat.antiframework;

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

    private Socket clientSocket;
    private String requestString;
    private String requestPath;
    private String requestType;
    private String requestVersion;
    private final Map<String, String> requestArguments;
    private final Map<String, Function<String, String>> endpointList;
    private final Map<String, String> endpointRedirectList;
    private boolean hasCookieToSend = false;
    private boolean hasReceivedCookie = false;
    private final Map<String, String> cookiesToSend;
    private final Map<String, String> cookiesReceived;

    public Worker() {
        super();
        this.endpointList = new HashMap<>();
        this.requestArguments = new HashMap<>();
        this.endpointRedirectList = new HashMap<>();
        this.cookiesToSend = new HashMap<>();
        this.cookiesReceived = new HashMap<>();
    }

    public void setClientSocket(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    public void addEndpoint(String requestType, String requestPath, Function<String, String> endpoint){
        this.endpointList.put(requestType+requestPath, endpoint);
    }

    public void addEndpointRedirect(String requestType, String requestPath, String destination){
        this.endpointRedirectList.put(requestType+requestPath, destination);
    }

    public void addCookie(String cookieKey, String cookieValue){
        if(!this.hasCookieToSend)
            this.hasCookieToSend = true;
        this.cookiesToSend.put(cookieKey, cookieValue);
        // TODO expiration, max-age, domain, path, etc...
    }

    // Returns cookie value, or null if didn't find one
    public String getCookie(String cookieKey){
        if(!this.hasReceivedCookie)
            return null;
        // Map returns null if it can't find the cookie, so everything works
        return this.cookiesReceived.get(cookieKey);
    }

    public Map<String, String> getAllCookies(){
        if(!this.hasReceivedCookie)
            return null;
        return this.cookiesReceived;
    }

    public void run(){
        System.out.println("New worker spawned");
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.requestString = "";

            // Parse HTTP request

            // Read lines
            long inputReadyTimeoutStart = System.currentTimeMillis();
            long inputReadyTimeout = 1000; // 1000ms = 1 second

            // Wait for readiness of input; else timeout
            while(!in.ready()){
                if(System.currentTimeMillis() - inputReadyTimeoutStart > inputReadyTimeout){
                    return;
                }
            }

            String requestLine;
            while((requestLine = in.readLine()) != null && requestLine.length() != 0){
                this.requestString += requestLine + "\n";
            }

            // Find HTTP Path
            Pattern pattern = Pattern.compile("^(?<request>(GET|POST)) (?<path>.+) HTTP/(?<version>[0-9.]+)$", Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(this.requestString);

            while(matcher.find()){
                if(matcher.groupCount() == 4) {
                    this.requestPath = matcher.group("path");
                    this.requestType = matcher.group("request");
                    this.requestVersion = matcher.group("version");
                    break;
                }
            }

            // Find cookies
            String cookieHeaderReceived = getHeaderValue("Cookie");
            if(cookieHeaderReceived != null) {
                //Pattern patternCookie = Pattern.compile("[ ]*(?<cookieKey>[^;=]+)=(?<cookieValue>[^;=]+)");
                Pattern patternCookie = Pattern.compile("(?<cookieKey>[^;= ]+)=(?<cookieValue>[^;=]+)");
                Matcher matcherCookie = patternCookie.matcher(cookieHeaderReceived);

                while (matcherCookie.find()) {
                    if(!hasReceivedCookie)
                        hasReceivedCookie = true;
                    cookiesReceived.put(
                            matcherCookie.group("cookieKey"),
                            matcherCookie.group("cookieValue")
                    );
                }
            }

            // Parse GET arguments
            if(getRequestType().equals("GET") && getRequestPath().contains("?")){

                // Split path from arguments
                String[] buffer = getRequestPath().split("\\?");
                this.requestPath = buffer[0];

                // Split multiple arguments
                buffer = buffer[1].split("&");

                // Move arguments to map
                for(String arguments : buffer) {
                    String[] arglist = arguments.split("=");
                    this.requestArguments.put(arglist[0], arglist[1]);

                }
            }

            // Parse POST arguments
            if(getRequestType().equals("POST")){
                int length = Integer.parseInt(getHeaderValue("Content-Length"));

                // POST size hard limits here
                if(length < 100_000 && length > 0) {

                    // Read arguments from buffered input stream
                    char[] charBuffer = new char[length];
                    in.read(charBuffer, 0, length);

                    // Split arguments
                    String argBuffer = String.valueOf(charBuffer);
                    String[] buffer = argBuffer.split("&");

                    // Move arguments to map
                    for (String arguments : buffer) {
                        String[] arglist = arguments.split("=");
                        this.requestArguments.put(arglist[0], arglist[1]);

                    }
                }
            }

            // Handle user endpoints
            String output = "";
            String outputDocument = "";
            boolean isOutputRedirect = false;
            boolean isOutputDocument = false;
            boolean isOutputOk = false;

            // Redirect endpoint call
            {
                String endpointRedirectBuffer = endpointRedirectList.get(this.requestType + this.requestPath);
                if (endpointRedirectBuffer != null) {
                    outputDocument = endpointRedirectBuffer;
                    isOutputOk = true;
                    isOutputRedirect = true;
                }
            }

            // Document endpoint call
            {
                Function<String, String> endpointFunctionBuffer = endpointList.get(this.requestType + this.requestPath);
                if (endpointFunctionBuffer != null) {
                    outputDocument = endpointFunctionBuffer.apply(this.requestString);
                    isOutputOk = true;
                    isOutputDocument = true;
                }
            }

            // HTTP Response Status
            if(isOutputOk && isOutputDocument) {
                output = "HTTP/1.1 200 OK\n";
            }
            if(isOutputOk && isOutputRedirect) {
                output = "HTTP/1.1 301 Moved Permanently\n";
            }
            if(!isOutputOk){
                output = "HTTP/1.1 404 NOT FOUND\n";
            }

            // HTTP Response Headers
            if(isOutputOk && isOutputDocument) {
                output += "Content-Type: text/html;charset=UTF-8\n" +
                        Server.getServerHeader() + "\n";
            }
            if(isOutputOk && isOutputRedirect) {
                output += "Location: " + output + "\n" +
                        Server.getServerHeader() + "\n";
            }
            if(!isOutputOk){
                output += Server.getServerHeader() + "\n";
            }

            // Cookie?
            if(hasCookieToSend){
                for(Map.Entry<String, String> cookie : this.cookiesToSend.entrySet()){
                    output += "Set-Cookie: " + cookie.getKey() + "=" + cookie.getValue() + "\n";
                }
            }

            // HTTP Response Data
            if(isOutputOk){
                output += "\n" + outputDocument;
            }

            // Send response
            out.println(output);

            // Close connection
            clientSocket.close();

            // Close buffered input
            in.close();

        } catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }

        // Kill thread
        this.interrupt();
        return;
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
        return null;
    }

    public String getRequestVersion(){
        return this.requestVersion;
    }

    public String getRequestPath(){
        return this.requestPath;
    }

    public String getRequestType(){
        return this.requestType;
    }

    public Map<String, String> getRequestArguments(){
        return this.requestArguments;
    }

    public boolean hasArguments(){
        return !this.requestArguments.isEmpty();
    }

}
