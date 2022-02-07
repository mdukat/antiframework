package mdukat.antiframework;

/*

Example Handler

 */

public class ExampleHandler extends Worker {
    public ExampleHandler(){
        super();
        addEndpoint("GET", "/test", this::testEndpoint);
        addEndpoint("GET", "/useragent", this::getUserAgentEndpoint);
        addEndpoint("GET", "/debug", this::debugEndpoint);
        addEndpoint("POST", "/debug", this::debugEndpoint);
    }

    // Returns static "Test123"
    String testEndpoint(String input){
        System.out.println("Hello from test endpoint");
        return "Test123";
    }

    // Returns value from User-Agent HTTP header
    String getUserAgentEndpoint(String input){
        return getHeaderValue("User-Agent");
    }

    String debugEndpoint(String input){
        System.out.println("Called debug endpoint");
        String output = "Debug endpoint\n";
        output += "User-Agent: " + getHeaderValue("User-Agent") + "\n"
            + "Request path: " + getRequestPath() + "\n"
            + "Request type: " + getRequestType() + "\n"
            + "HTTP Version: " + getRequestVersion() + "\n";

        if(hasArguments())
            output += "Has arguments: " + getRequestArguments().toString() + "\n";

        return output;
    }
}
